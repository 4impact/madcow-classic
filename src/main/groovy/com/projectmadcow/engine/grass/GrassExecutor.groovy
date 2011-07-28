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

import com.canoo.webtest.engine.WebTestException
import org.apache.log4j.Logger

import com.projectmadcow.engine.RuntimeContext

/**
 * Executor for Grass Code. Called by the Properties, CSV and Spreadsheet runners,
 * to parse and then execute the Grass Code.
 */
public class GrassExecutor {

    protected static final Logger LOG = Logger.getLogger(GrassExecutor.class);

    static def executeCode(RuntimeContext runtimeContext, List unparsedCode, def currentTestName) {
        try {
            if (isTestIgnored(unparsedCode)) {
                ignoreTest(runtimeContext, currentTestName)
            } else {
                parseAndExecuteGrassCode(runtimeContext, unparsedCode, currentTestName)
            }
        } catch (WebTestException wte) {
            throw wte
        } catch (e) {
            e.printStackTrace()
            runtimeContext.antBuilder.executionError(message: e.message)
        }
    }

    protected static boolean isTestIgnored(List unparsedCode) {
        return unparsedCode.any { String lineOfCode ->
            lineOfCode.trim().equalsIgnoreCase('madcow.ignore')
        }
    }

    protected static def ignoreTest(RuntimeContext runtimeContext, currentTestName) {
        LOG.debug "Test $currentTestName has been ignored"
        runtimeContext.antBuilder.ignoreTest()
        
    }

    protected static def parseAndExecuteGrassCode(RuntimeContext runtimeContext, List unparsedCode, currentTestName) {
        GrassParser grassParser = new GrassParser(runtimeContext);
        grassParser.parseCode(unparsedCode).each {String line ->
            try {
                Eval.x runtimeContext, "x." + line
                LOG.debug line
            } catch (Throwable t) {
                LOG.error "unable to parse test : ${currentTestName} line : $line"
                throw new Exception("Unable to parse line: $line", t)
            }
        }
    }


}