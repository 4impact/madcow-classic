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

package com.projectmadcow.plugins

import org.apache.tools.ant.Task

/**
 * Test class for the ExecuteJavascript plugin.
 */
class ExecuteJavascriptTest extends AbstractPluginTestCase {

    ExecuteJavascript executeJavascriptPlugin = new ExecuteJavascript()
    String antTaskName = 'executeJavascript'

    void setUp() {
        super.setUp()

        final String html = """<html><body><form name="testForm">
                                    <label for="addressLine1">Address</label>: <input name="addressLine1" id="addressLine1" type="text"/>
                               </form></body></html>"""
        contextStub.setDefaultResponse(html)
    }

    void testExecuteJavascriptValid() {
        executeJavascriptPlugin.invoke(antBuilder, [value : 'alert("test")'])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'javascript') == 'alert("test")'
    }

    void testExecuteJavascriptInvalid() {
        assertStepFailedException({
            executeJavascriptPlugin.invoke(antBuilder, [value : 'some invalid javascript'])
        }, 'JavaScript error loading page')
    }
}
