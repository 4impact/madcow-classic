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
 * Test case class for WaitUntilExists plugin.
 */
class WaitUntilExistsTest extends AbstractPluginTestCase {

    WaitUntilExists waitUntilExistsPlugin = new WaitUntilExists()
    final String antTaskName = 'waitForElement'
    String attributeValue

    void setUp() {
        super.setUp()

        final String html = """<html><body><form>
                                    <input type="radio" value="male" name="male" id="male"/>
                                    <input type="radio" value="female" name="female" id="female" checked="false" />
                               </form></body></html>"""
        contextStub.setDefaultResponse(html)
    }

    void testWaitForByHtmlId() {
        assertStepFailedException({
            waitUntilExistsPlugin.invoke(antBuilder, [htmlId : 'unknown', milliseconds : '1'])
        }, 'did not appear within timeout for element')

        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'htmlId') == 'unknown'
    }

//    TODO - Raised Jira MADCOW-172
//    void testWaitByName() {
//        assertStepFailedException({
//            waitUntilExistsPlugin.invoke(antBuilder, [name : 'unknown', milliseconds : '1'])
//        }, 'did not appear within timeout for element')
//
//        Task pluginTask = findTask(antTaskName)
//        assert findAttribute(pluginTask, 'name') == 'unknown'
//    }

    void testWaitForByXPath() {
        assertStepFailedException({
            waitUntilExistsPlugin.invoke(antBuilder, [xpath : '//input[3]', milliseconds : '1'])
        }, 'did not appear within timeout for element')

        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'xpath') == '//input[3]'
    }

    void testAttributesMandatoryMissing() {
        assertStepExecutionException({
            waitUntilExistsPlugin.invoke(antBuilder, [:])
        }, '"htmlId" or "xPath" must be set!')
    }

    void testAttributeUnsupported() {
        assertUnsupportedAttribute({
            waitUntilExistsPlugin.invoke(antBuilder, [unsupported : 'willfail'])
        }, antTaskName, 'unsupported')
    }
}
