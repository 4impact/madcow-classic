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
 * Test class for the Value plugin.
 */
class ValueTest extends AbstractPluginTestCase {

    Value valuePlugin = new Value()
    final String antTaskName = 'setInputField'

    void setUp() {
        super.setUp()

        final String html = """<html><body><form>
                                     <input id="state" name="state" label="State" value="Victoria" />
                               </form></body></html>"""
        contextStub.setDefaultResponse(html)
    }

    void testValueByHtmlId() {
        valuePlugin.invoke(antBuilder, [htmlId : 'state', value : 'Queensland'])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'value') == 'Queensland'
    }

    void testValueByName() {
        valuePlugin.invoke(antBuilder, [name : 'state', value : 'Queensland'])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'value') == 'Queensland'
    }

    void testValueByXPath() {
        valuePlugin.invoke(antBuilder, [xpath : '//input[@label = \'State\']', value : 'Queensland'])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'value') == 'Queensland'
    }

    void testAttributesMandatoryMissing() {
        assertStepExecutionException({
            valuePlugin.invoke(antBuilder, [value : 'Queensland'])
        }, 'One of \'forLabel\', \'htmlId\', \'name\', or \'xpath\' must be set!')
    }

    void testAttributeUnsupported() {
        assertUnsupportedAttribute({
            valuePlugin.invoke(antBuilder, [unsupported : 'willfail', value : 'Queensland'])
        }, antTaskName, 'unsupported')
    }

    void testAttributesExclusiveCombinations() {

        final String errorMessage = 'Only one of \'forLabel\', \'htmlId\', \'name\', and \'xpath\' should be set!'

        assertStepExecutionException({
            valuePlugin.invoke(antBuilder, [htmlId: 'state', xpath: '//input[@id=\'state\']', name: 'state', value : 'Queensland'])
        }, errorMessage)

        assertStepExecutionException({
            valuePlugin.invoke(antBuilder, [htmlId: 'state', xpath: '//input[@id=\'state\']', value : 'Queensland'])
        }, errorMessage)

        assertStepExecutionException({
            valuePlugin.invoke(antBuilder, [htmlId: 'state', name: 'state', value : 'Queensland'])
        }, errorMessage)

        assertStepExecutionException({
            valuePlugin.invoke(antBuilder, [xpath: '//input[@id=\'state\']', name: 'state', value : 'Queensland'])
        }, errorMessage)
    }
}
