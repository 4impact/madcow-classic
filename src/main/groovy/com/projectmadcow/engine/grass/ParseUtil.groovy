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

import org.apache.commons.lang.StringUtils

/**
 * Various utilities to handle converting string representations of Groovy code to
 * Groovy objects and back to Strings again.
 *
 * @author gbunney
 */
class ParseUtil {

    /**
     * Converts a Map to a String which can be passed to the Eval and be returned as a Map.
     */
    static String convertMapToString(Map mapToConvert) {
        String line = '';
        mapToConvert.each { key, value ->

            String valueString
            if ((value instanceof String) || (value instanceof GString)) {
                valueString = value
            } else if (value instanceof List) {
                valueString = convertListToString(value as List)
            } else {
                valueString = ParseUtil.convertMapToString(value as Map)
            }

            line += "'$key' : ${valueString}, "
        }
        return "[$line]"
    }

    /**
     * Converts a List to a String which can be passed to the Eval and be returned as a List.
     */
    static String convertListToString(List listToConvert) {
        String quotedList = '';
        listToConvert.each { String val -> quotedList += "${val}, " }
        return "[$quotedList]"
    }

    /**
     * Unquote the string, by removing the first and last three characters.
     */
    static String unquote(String stringToUnquote) {
        if ((stringToUnquote.startsWith('"""')) && (stringToUnquote.endsWith('"""')))
            return StringUtils.substring(stringToUnquote, '"""'.length(), stringToUnquote.length() - '"""'.length())
        else if ((stringToUnquote.startsWith("'")) && (stringToUnquote.endsWith("'")))
            return StringUtils.substring(stringToUnquote, 1, stringToUnquote.length() - 1)
        else if ((stringToUnquote.startsWith('"')) && (stringToUnquote.endsWith('"')))
            return StringUtils.substring(stringToUnquote, 1, stringToUnquote.length() - 1)
        else
            return stringToUnquote
    }

    /**
     * Calls the groovy Eval.me for the string.
     * Returns an object of the results; if it is a Map/List, then it is just returned.
     * If it is a String, then it is returned as a String
     */
    static def evalMe(String stringToEval) {
        try {
            def evaledValue = Eval.me(stringToEval)
            switch (evaledValue) {
                case Map:
                    return evaledValue as Map
                case List:
                    return evaledValue as List
                default:
                    break
            }
        } catch (e) {
            // ignored
        }
        return stringToEval
    }

    /**
     * Quotes a String and escapes as necessary
     */
    static String quoteString(String str) {
        str = str.replace("\\","\\\\")
        return "'${str.replace('\'', '\\\'')}'"
    }

    /**
     * Converts the specified object to a Eval friendly string version
     */
    static String convertToString(def objToConvert) {
        switch (objToConvert) {
            case Map:
                return convertMapToString(objToConvert)
            case List:
                return convertListToString(objToConvert)
            default:
                return objToConvert.toString()
        }
    }

    static String escapeSingleQuotes(String str) {
        return (str.contains("'") ? '"' + str + '"' : "'" + str + "'")
    }
}
