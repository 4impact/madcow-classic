/*
 * Copyright 2008-2011 4impact Technology Services, Brisbane, Australia
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.projectmadcow.engine.grass

import java.util.regex.Matcher
import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import com.projectmadcow.engine.RuntimeContext
import com.projectmadcow.engine.FileFinder
import com.projectmadcow.plugins.Store

/**
 * Grass Parser used to parse Properties, Spreadsheets and CSV file code.
 *
 * @author gbunney
 */
class GrassParser {

    protected static final Logger LOG = Logger.getLogger(GrassParser.class);

    protected static final String DATA_PARAMETER_KEY = '@'

    protected static final String DATA_PARAMETER_INLINE_REGEX = '@\\{([^}]+)\\}'

    protected static final String STORE_PLUGIN_NAME = StringUtils.uncapitalise(StringUtils.substringAfterLast(Store.class.toString(), '.'))

    protected static final Map IMPORT_COMMAND_DIRECTORIES = [ 'import' : 'templates',
                                                              'importTemplate' : 'templates',
                                                              'importTest' : 'test']

    protected RuntimeContext runtimeContext
    protected List<String> parsedCode

    GrassParser(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext
        this.parsedCode = []
    }

    /**
     * Parse the list of code.
     */
    public List<String> parseCode(List unparsedCode, boolean clearParsedCode = true) {

        if (clearParsedCode)
            this.parsedCode = []

        unparsedCode.each { String lineWithWhiteSpace ->
            String line = StringUtils.strip(lineWithWhiteSpace)

            // ignore comment and empty lines
            if (line.startsWith('#') || StringUtils.isEmpty(line))
                return

            // if it is just an operation, add the line
            if (!line.contains('=')) {
                parsedCode.add(line)
                return
            }

            def splitLine = line.split('=', 2)
            splitLine.eachWithIndex { ln, idx -> splitLine[idx] = StringUtils.strip(ln) }
            line = splitLine.join('=')
            String operation = splitLine[0]
            String expression = splitLine[1]

            String operationCommand = null
            if (operation.contains('.'))
                operationCommand = StringUtils.substringAfterLast(operation, '.')

            this.parseLine(line, operation, expression, operationCommand ?: operation)
        }

        LOG.debug "Parsed code: $parsedCode"
        return parsedCode
    }

    /**
     * Parse an individual line of code.
     */
    protected void parseLine(String line, String operation, String expression, String operationCommand = operation) {

        // recursive callback for imported files
        if (operation.startsWith('import')) {

            // import short hand for importTemplate
            if (operation == 'import')
                operation = 'importTemplate'

            String filename = ParseUtil.unquote(expression)

            IMPORT_COMMAND_DIRECTORIES.each { String key, String value ->
                if (key == operation) {
                    parsedCode.add("${operation} = [value: '$filename', startOfImport : 'true']")
                    parsedCode.addAll(parseCode(loadImport(filename, value), false))
                    parsedCode.add("${operation} = [value: '$filename', endOfImport : 'true']")
                }
            }

            return
        }

        // evaluate the expression to expand any embedded closures
        def expressionValue = ParseUtil.evalMe(expression)

        LOG.trace "Evaluated expression : $expressionValue, type of ${expressionValue.class}"
        
        boolean isParserInstruction = false
        boolean isSettingDataParameter = operation.startsWith(DATA_PARAMETER_KEY)

        if (isSettingDataParameter) {
            setDataParameterValue(operation, expression)
            isParserInstruction = true
        }

        switch (expressionValue) {
            case String:
            case GString:
                expression = this.parseStringExpression(expressionValue as String, line, operation, operationCommand, isSettingDataParameter)
                break
            case Map:
                expression = this.parseMapExpression(expressionValue as Map, line, operation, operationCommand, isSettingDataParameter)
                break
            case List:
                expression = this.parseListExpression(expressionValue as List, line, operation, operationCommand, isSettingDataParameter)
                break
        }

        LOG.trace "Expression post processing : $expression"

        if ((!isParserInstruction) && (expression)) {
            line = "$operation=$expression"
            parsedCode.add(line)
        }
    }

    List loadImport(String filename, String basedir) {
        File file = FileFinder.findPropertyFile(filename, basedir)
        return FileUtils.readLines(file)
    }

    String getDataParameterValue(String line, String parameterName) {
        if (!runtimeContext.dataParameters.containsKey(parameterName))
            throw new Exception("Unable to parse line: $line, data parameter not set ${parameterName}")

        LOG.debug "Replacing data parameter $parameterName with ${runtimeContext.dataParameters.get(parameterName)}"
        return runtimeContext.dataParameters.get(parameterName)
    }

    void setDataParameterValue(String key, String value) {
        LOG.debug "Setting data parameter ${key} : ${value}"
        runtimeContext.dataParameters.put key, ParseUtil.unquote(value)
    }

    /**
     * Parse an expression that is a String.
     */
    protected String parseStringExpression(String expression, String line, String operation, String operationCommand, boolean isSettingDataParameter) {

        Matcher inlineParameters = expression =~ DATA_PARAMETER_INLINE_REGEX
        if (inlineParameters.size() > 0) {
            inlineParameters.each { String paramMatch, String paramName ->
                expression = StringUtils.replace(expression as String, paramMatch, getDataParameterValue(line, "@${paramName}"))
            }
        } else {
            if (expression.startsWith("'$DATA_PARAMETER_KEY")) {
                expression = getDataParameterValue(line, ParseUtil.unquote(expression))
                if (!expression.startsWith('['))
                    expression = "'$expression'"
            }
        }

        if (expression.startsWith("'madcow.eval")) {
            Macro macro = new Macro()
            expression = "'${Eval.x(macro, 'x.' + ParseUtil.unquote(expression))}'"
        }

        if (isSettingDataParameter) {
            setDataParameterValue(operation, expression)
        }

        if (operationCommand == STORE_PLUGIN_NAME) {
            setDataParameterValue("@${ParseUtil.unquote(expression)}", "#{${ParseUtil.unquote(expression)}}")
        }

        return expression
    }

    /**
     * Parse an expression that is a Map.
     */
    protected String parseMapExpression(Map expression, String line, String operation, String operationCommand, boolean isSettingDataParameter) {

        boolean parsedMap = false
        expression.each { String key, def value ->

            if ((value instanceof String) || (value instanceof GString)) {
                String parsedValue = ParseUtil.unquote(parseStringExpression("'$value'", line, null, null, false))
                if (parsedValue != value) {
                    expression[key] = parsedValue
                    parsedMap = true
                }

            } else if (value instanceof List) {
                List parsedValue = Eval.me(parseListExpression(value, line, null, null, false)) as List
                if (parsedValue != value) {
                    expression[key] = parsedValue
                    parsedMap = true
                }
            }
        }

        String evalableMapString = ParseUtil.convertMapToString(expression)
        if ((parsedMap) && (isSettingDataParameter)) {
            setDataParameterValue(operation, evalableMapString)
            return null
        } else {
            return evalableMapString
        }
    }

    /**
     * Parse an expression that is a List.
     */
    protected String parseListExpression(List expression, String line, String operation, String operationCommand, boolean isSettingDataParameter) {

        boolean parsedList = false
        expression.eachWithIndex { String value, int idx ->
            String parsedValue = ParseUtil.unquote(parseStringExpression("'$value'", line, null, null, false))
            if (parsedValue != value) {
                expression[idx] = parsedValue
                parsedList = true
            }
        }

        String evalableListString = ParseUtil.convertListToString(expression)
        if ((parsedList) && (isSettingDataParameter)) {
            setDataParameterValue(operation, evalableListString)
            return null
        } else {
            return evalableListString
        }
    }
}
