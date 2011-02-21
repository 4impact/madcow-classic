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

import com.gargoylesoftware.htmlunit.javascript.host.Event
import org.apache.tools.ant.Task

/**
 * Test class for the ExecuteEvent plugin.
 */
class ExecuteEventTest extends AbstractPluginTestCase {

    ExecuteEvent executeEventPlugin = new ExecuteEvent()
    String antTaskName = 'executeEvent'

    void setUp() {
        super.setUp()

        final String html = """<html><body><form name="testForm">
                                    <label for="addressLine1">Address</label>: <input name="addressLine1" id="addressLine1" type="text"/>
                               </form></body></html>"""
        contextStub.setDefaultResponse(html)
    }

    void testExecuteEventWithoutData() {
        executeEventPlugin.invoke(antBuilder, [htmlId : 'addressLine1', value : Event.TYPE_RESET])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'htmlId') == 'addressLine1'
        assert findAttribute(pluginTask, 'eventType') == 'reset'
    }

    void testExecuteEventWithData() {
        executeEventPlugin.invoke(antBuilder, [htmlId : 'addressLine1', eventData : '15', value : Event.TYPE_BLUR])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'htmlId') == 'addressLine1'
        assert findAttribute(pluginTask, 'eventType') == 'blur'
        assert findAttribute(pluginTask, 'eventData') == '15'
    }

    void testAttributesMandatoryMissing() {
        assertStepExecutionException({
            executeEventPlugin.invoke(antBuilder, [value : 'reset'])
        }, '"htmlId" or "xPath" must be set!')
    }

    void testAttributesExclusiveCombinations() {
        assertStepExecutionException({
            executeEventPlugin.invoke(antBuilder, [htmlId: 'addressLine1', xpath: '//input[1]', value : Event.TYPE_RESET])
        }, 'Only one from "htmlId" and "xPath" can be set!')
    }
}
