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
 * Test class for the CheckValueNotEmptyTest plugin.
 */
class CheckValueNotEmptyTest extends AbstractPluginTestCase {

    CheckValueNotEmpty checkValueNotEmptyPlugin = new CheckValueNotEmpty()

    void setUp() {
        super.setUp()

        final String html = """<html><body>
                                    <form><input id="addressLine1" name="addressLine1" label="addressLine1" value="Adelaide St"/></form>
                               </body></html>"""
        contextStub.setDefaultResponse(html)
    }

    void testCheckValueByHtmlId() {
        checkValueNotEmptyPlugin.invoke(antBuilder, [htmlId : 'addressLine1'])
        Task pluginTask = findTask('verifyElementText')
        assert findAttribute(pluginTask, 'htmlId') == 'addressLine1'
        assert findAttribute(pluginTask, 'regex') == 'true'
    }

    void testCheckValueByXPath() {
        def attributes = [xpath : '//input[@label=\'addressLine1\']/@value']

        checkValueNotEmptyPlugin.invoke(antBuilder, attributes)
        Task pluginTask = findTask('verifyXPath')
        assert findAttribute(pluginTask, 'xpath') == attributes.xpath
        assert findAttribute(pluginTask, 'regex') == 'true'
    }

//    TODO - Raised JIRA: MADCOW-170
//    void testCheckValueByName() {
//
//        checkValueNotEmptyPlugin.invoke(antBuilder, [name: 'addressLine1'])
//        Task pluginTask = findTask('verifyXPath')
//        assert findAttribute(pluginTask, 'regex') == 'true'
//    }
}
