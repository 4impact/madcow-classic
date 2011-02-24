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
 * Test class for the ClickLink plugin.
 */
public class TestInfoTest extends AbstractPluginTestCase {

    TestInfo testInfoPlugin = new TestInfo()

    void setUp() {
        super.setUp()

        final String html = """<html><body>
                               </body></html>"""
        contextStub.setDefaultResponse(html)
    }

    void testPluginBasicCall() {

        def attributes = [value: 'this test does various things', description: 'set the test info']

        testInfoPlugin.invoke(antBuilder, attributes)

        Task pluginTask = findTask('testInfo')
        assert findAttribute(pluginTask, 'type') == 'MadcowTestInfo'
        assert findAttribute(pluginTask, 'info') == attributes.value
        assert findAttribute(pluginTask, 'description') == attributes.description
    }

    void testPluginMissingAttributes() {

        def attributes = [value : 'this test does various things']
        testInfoPlugin.invoke(antBuilder, attributes)
        Task pluginTask = findTask('testInfo')
        assert findAttribute(pluginTask, 'type') == 'MadcowTestInfo'
        assert findAttribute(pluginTask, 'info') == attributes.value
        assert findAttribute(pluginTask, 'description') == 'null'

        testInfoPlugin.invoke(antBuilder, [:])
        pluginTask = findTask('testInfo')
        assert findAttribute(pluginTask, 'type') == 'MadcowTestInfo'
        assert findAttribute(pluginTask, 'info') == 'null'
        assert findAttribute(pluginTask, 'description') == 'null'
    }

}
