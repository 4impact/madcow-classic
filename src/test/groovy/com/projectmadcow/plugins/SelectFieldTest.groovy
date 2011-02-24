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
 * Test class for the SelectField plugin.
 */
class SelectFieldTest extends AbstractPluginTestCase {

    SelectField selectFieldPlugin = new SelectField()
    final String antTaskName = 'setSelectField'

    void setUp() {
        super.setUp()

        final String html = """<html><body>
                                    <form><select id="state" name="state" label="State">
                                        <option>Australan Captial Territory</option>
                                        <option>New South Wales</option>
                                        <option>Northern Territory</option>
                                        <option selected>Queensland</option>
                                        <option>South Australia</option>
                                        <option>Victoria</option>
                                        <option>Western Australia</option>
                                    </select></form>
                               </body></html>"""
        contextStub.setDefaultResponse(html)
    }

    void testSelectByHtmlId() {
        selectFieldPlugin.invoke(antBuilder, [htmlId : 'state', value : 'Queensland'])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'text') == 'Queensland'
    }

    void testSelectByName() {
        selectFieldPlugin.invoke(antBuilder, [name : 'state', value : 'Queensland'])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'text') == 'Queensland'
    }

    void testSelectByXPath() {
        selectFieldPlugin.invoke(antBuilder, [xpath : '//select[@label = \'State\']', value : 'Queensland'])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'text') == 'Queensland'
    }

    void testAttributesMandatoryMissing() {
        assertStepExecutionException({
            selectFieldPlugin.invoke(antBuilder, [value : 'Queensland'])
        }, 'One of \'forLabel\', \'htmlId\', \'name\', or \'xpath\' must be set!')
    }

    void testAttributeUnsupported() {
        assertUnsupportedAttribute({
            selectFieldPlugin.invoke(antBuilder, [unsupported : 'willfail', value : 'Queensland'])
        }, antTaskName, 'unsupported')
    }

    void testMissingIdentifier() {
        assertStepFailedException({
            selectFieldPlugin.invoke(antBuilder, [htmlId: 'state', value : ''])
        }, 'No option element found with text ""')
    }

    void testAttributesExclusiveCombinations() {

        final String errorMessage = 'Only one of \'forLabel\', \'htmlId\', \'name\', and \'xpath\' should be set!'

        assertStepExecutionException({
            selectFieldPlugin.invoke(antBuilder, [htmlId: 'state', xpath: '//select[@id=\'state\']', name: 'state', value : 'Queensland'])
        }, errorMessage)

        assertStepExecutionException({
            selectFieldPlugin.invoke(antBuilder, [htmlId: 'state', xpath: '//select[@id=\'state\']', value : 'Queensland'])
        }, errorMessage)

        assertStepExecutionException({
            selectFieldPlugin.invoke(antBuilder, [htmlId: 'state', name: 'state', value : 'Queensland'])
        }, errorMessage)

        assertStepExecutionException({
            selectFieldPlugin.invoke(antBuilder, [xpath: '//select[@id=\'state\']', name: 'state', value : 'Queensland'])
        }, errorMessage)
    }
}
