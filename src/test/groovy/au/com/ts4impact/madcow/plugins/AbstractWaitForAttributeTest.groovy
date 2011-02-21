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

package au.com.ts4impact.madcow.plugins

import au.com.ts4impact.madcow.engine.plugin.Plugin
import org.apache.tools.ant.Task
import org.junit.Ignore

/**
 * Abstract base test class for the WaitForAttribute plugin tests.
 */
@Ignore
abstract class AbstractWaitForAttributeTest extends AbstractPluginTestCase {

    Plugin plugin
    final String antTaskName = 'waitForAttribute'
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
            plugin.invoke(antBuilder, [htmlId : 'female', milliseconds : '1'])
        }, 'did not appear within timeout for element')

        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'htmlId') == 'female'
        assert findAttribute(pluginTask, 'attribute') == 'checked'
    }

//    TODO - Raised Jira MADCOW-172
//    void testWaitByName() {
//        assertStepFailedException({
//            plugin.invoke(antBuilder, [name : 'female', milliseconds : '1'])
//        }, 'did not appear within timeout for element')
//
//        Task pluginTask = findTask(antTaskName)
//        assert findAttribute(pluginTask, 'htmlId') == 'female'
//        assert findAttribute(pluginTask, 'attribute') == 'checked'
//    }

    void testWaitForByXPath() {
        assertStepFailedException({
            plugin.invoke(antBuilder, [xpath : '//input[2]', milliseconds : '1'])
        }, 'did not appear within timeout for element')

        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'xpath') == '//input[2]'
        assert findAttribute(pluginTask, 'attribute') == 'checked'
    }

    void testAttributesMandatoryMissing() {
        assertStepExecutionException({
            plugin.invoke(antBuilder, [:])
        }, '"htmlId" or "xPath" must be set!')
    }

    void testAttributeUnsupported() {
        assertUnsupportedAttribute({
            plugin.invoke(antBuilder, [unsupported : 'willfail'])
        }, antTaskName, 'unsupported')
    }
}
