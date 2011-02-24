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
import org.junit.Ignore

/**
 * Base test class for radio button plugins.
 */
@Ignore
abstract class AbstractRadioButtonTest extends AbstractPluginTestCase {

    AbstractRadioButton radioButtonPlugin
    String htmlId = 'male'
    boolean shouldBeChecked
    final String antTaskName = 'verifyRadioButton'


    void setUp() {
        super.setUp()

        final String html = """<html><body><form>
                                    <input type="radio" value="male" name="male" id="male"/>
                                    <input type="radio" value="female" name="female" id="female" checked/>
                               </form></body></html>"""
        contextStub.setDefaultResponse(html)
    }

    void testRadioButtonByHtmlId() {
        radioButtonPlugin.invoke(antBuilder, [htmlId : htmlId])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'checked') == "${shouldBeChecked}"
    }

    void testRadioButtonByName() {
        radioButtonPlugin.invoke(antBuilder, [name : htmlId])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'checked') == "${shouldBeChecked}"
    }

    void testRadioButtonByXPath() {
        radioButtonPlugin.invoke(antBuilder, [xpath : "//input[@value='${htmlId}']"])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'checked') == "${shouldBeChecked}"
    }

    void testAttributesMandatoryMissing() {
        assertStepExecutionException({
            radioButtonPlugin.invoke(antBuilder, [:])
        }, 'One of \'forLabel\', \'htmlId\', \'name\', or \'xpath\' must be set!')
    }

    void testAttributesExclusiveCombinations() {

        final String errorMessage = 'Only one of \'forLabel\', \'htmlId\', \'name\', and \'xpath\' should be set!'

        assertStepExecutionException({
            radioButtonPlugin.invoke(antBuilder, [htmlId: htmlId, xpath: "//input[@value='${htmlId}']", name: htmlId])
        }, errorMessage)

        assertStepExecutionException({
            radioButtonPlugin.invoke(antBuilder, [htmlId: htmlId, xpath: "//input[@value='${htmlId}']"])
        }, errorMessage)

        assertStepExecutionException({
            radioButtonPlugin.invoke(antBuilder, [htmlId: htmlId, name: htmlId])
        }, errorMessage)

        assertStepExecutionException({
            radioButtonPlugin.invoke(antBuilder, [xpath: "//input[@value='${htmlId}']", name: htmlId])
        }, errorMessage)
    }
}
