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
 * Test class for the SelectFieldByIndex plugin.
 */
class SelectFieldByIndexTest extends AbstractPluginTestCase {

    SelectFieldByIndex selectFieldByIndexPlugin = new SelectFieldByIndex()
    final String antTaskName = 'setSelectField'

    void setUp() {
        super.setUp()

        final String html = """<html><body>
                                    <form><select id="state" name="state" label="State">
                                        <option>Australan Captial Territory</option>
                                        <option>New South Wales</option>
                                        <option>Northern Territory</option>
                                        <option>Queensland</option>
                                        <option>South Australia</option>
                                        <option>Victoria</option>
                                        <option>Western Australia</option>
                                    </select></form>
                               </body></html>"""
        contextStub.setDefaultResponse(html)
    }

    void testSelectByHtmlId() {
        selectFieldByIndexPlugin.invoke(antBuilder, [htmlId : 'state', value : '3'])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'optionIndex') == '3'
    }

    void testSelectByName() {
        selectFieldByIndexPlugin.invoke(antBuilder, [name : 'state', value : '3'])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'optionIndex') == '3'
    }

    void testSelectByXPath() {
        selectFieldByIndexPlugin.invoke(antBuilder, [xpath : '//select[@label = \'State\']', value : '3'])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'optionIndex') == '3'
    }
}
