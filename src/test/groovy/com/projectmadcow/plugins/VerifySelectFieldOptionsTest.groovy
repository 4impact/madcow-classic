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

import com.projectmadcow.engine.grass.ParseUtil
import org.apache.tools.ant.Task

/**
 * Test class for the VerifySelectFieldOptions plugin.
 */
class VerifySelectFieldOptionsTest extends AbstractPluginTestCase {

    VerifySelectFieldOptions verifySelectFieldOptionsPlugin = new VerifySelectFieldOptions()
    final String antTaskName = 'verifySelectFieldOptions'
    final List options = ['Australan Captial Territory',
                          'New South Wales',
                          'Northern Territory',
                          'Queensland',
                          'South Australia',
                          'Victoria',
                          'Western Australia']

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

    void testSelectedOptionByHtmlId() {
        verifySelectFieldOptionsPlugin.invoke(antBuilder, [htmlId : 'state', value : options])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'htmlId') == 'state'
        assert findAttribute(pluginTask, 'options') == ParseUtil.convertListToString(options).replaceAll(', ', ',')
    }

    void testSelectedOptionByName() {
        verifySelectFieldOptionsPlugin.invoke(antBuilder, [name : 'state', value : options])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'name') == 'state'
        assert findAttribute(pluginTask, 'options') == ParseUtil.convertListToString(options).replaceAll(', ', ',')
    }

    void testSelectedOptionByXPath() {
        verifySelectFieldOptionsPlugin.invoke(antBuilder, [xpath : '//select[@label = \'State\']', value : options])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'xpath') == '//select[@label = \'State\']'
        assert findAttribute(pluginTask, 'options') == ParseUtil.convertListToString(options).replaceAll(', ', ',')
    }

    void testAttributeUnsupported() {
        assertUnsupportedAttribute({
            verifySelectFieldOptionsPlugin.invoke(antBuilder, [unsupported : 'willfail', value : options])
        }, antTaskName, 'unsupported')
    }

    void testAttributesMandatoryMissing() {
        assertStepExecutionException({
            verifySelectFieldOptionsPlugin.invoke(antBuilder, [value : options])
        }, '"htmlId" or "name" or "xpath" must be set!')
    }

    void testAttributesExclusiveCombinations() {

        final String errorMessage = 'Only one from "htmlId", "name" or "xpath" can be set!'

        assertStepExecutionException({
            verifySelectFieldOptionsPlugin.invoke(antBuilder, [htmlId: 'state', xpath: '//select[@id=\'state\']', name: 'state', value : options])
        }, errorMessage)

        assertStepExecutionException({
            verifySelectFieldOptionsPlugin.invoke(antBuilder, [htmlId: 'state', xpath: '//select[@id=\'state\']', value : options])
        }, errorMessage)

        assertStepExecutionException({
            verifySelectFieldOptionsPlugin.invoke(antBuilder, [htmlId: 'state', name: 'state', value : options])
        }, errorMessage)

        assertStepExecutionException({
            verifySelectFieldOptionsPlugin.invoke(antBuilder, [xpath: '//select[@id=\'state\']', name: 'state', value : options])
        }, errorMessage)
    }
}
