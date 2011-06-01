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

    private static String IS_EMPTY_FAILURE_STRING = 'Expected value "(.+)" but got ""';

    void setUp() {
        super.setUp()

        final String html = """<html><body>
                                    <form><input id="addressLine1" name="addressLine1Name" label="addressLine1Label" value="Adelaide St"/></form>
                                    <form><input id="addressLine2" name="addressLine2Name" label="addressLine2Label" value=""/></form>
                                    <form><input id="addressLine3" name="addressLine3Name" label="addressLine3Label"/></form>
                                    <form><input id="addressLine4" name="addressLine4Name" label="addressLine4Label" value="      "/></form>
                                    <form><input id="addressLine5" name="addressLine5Name" label="addressLine5Label" value="    leading/trailing  "/></form>
                               </body></html>"""
        contextStub.setDefaultResponse(html)
    }

    void testCheckValueNotEmptyByHtmlId() {
        checkValueNotEmptyPlugin.invoke(antBuilder, [htmlId: 'addressLine1'])
        Task pluginTask = findTask('verifyElementText')
        assert findAttribute(pluginTask, 'htmlId') == 'addressLine1'
        assert findAttribute(pluginTask, 'regex') == 'true'
    }

    void testCheckValueNotEmptyByHtmlIdEmptyString() {
        assertStepFailedException({
            checkValueNotEmptyPlugin.invoke(antBuilder, [htmlId: 'addressLine2'])
        }, IS_EMPTY_FAILURE_STRING)
        Task pluginTask = findTask('verifyElementText')
        assert findAttribute(pluginTask, 'htmlId') == 'addressLine2'
        assert findAttribute(pluginTask, 'regex') == 'true'
    }

    void testCheckValueNotEmptyByHtmlIdNoValue() {
        assertStepFailedException({
            checkValueNotEmptyPlugin.invoke(antBuilder, [htmlId: 'addressLine3'])
        }, IS_EMPTY_FAILURE_STRING)
        Task pluginTask = findTask('verifyElementText')
        assert findAttribute(pluginTask, 'htmlId') == 'addressLine3'
        assert findAttribute(pluginTask, 'regex') == 'true'
    }

    void testCheckValueNotEmptyByHtmlIdBlankString() {
        assertStepFailedException({
            checkValueNotEmptyPlugin.invoke(antBuilder, [htmlId: 'addressLine4'])
        }, IS_EMPTY_FAILURE_STRING)
        Task pluginTask = findTask('verifyElementText')
        assert findAttribute(pluginTask, 'htmlId') == 'addressLine4'
        assert findAttribute(pluginTask, 'regex') == 'true'
    }

    void testCheckValueNotEmptyByHtmlIdLeadingBlankNonEmptyString() {
        checkValueNotEmptyPlugin.invoke(antBuilder, [htmlId: 'addressLine5'])
        Task pluginTask = findTask('verifyElementText')
        assert findAttribute(pluginTask, 'htmlId') == 'addressLine5'
        assert findAttribute(pluginTask, 'regex') == 'true'
    }

    void testCheckValueNotEmptyByXPath() {
        def attributes = [xpath: '//input[@label=\'addressLine1Label\']/@value']

        checkValueNotEmptyPlugin.invoke(antBuilder, attributes)
        Task pluginTask = findTask('verifyXPath')
        assert findAttribute(pluginTask, 'xpath') == attributes.xpath
        assert findAttribute(pluginTask, 'regex') == 'true'
    }

    void testCheckValueNotEmptyByXPathIdEmptyString() {
        def attributes = [xpath: '//input[@label=\'addressLine2Label\']/@value']

        assertStepFailedException({
            checkValueNotEmptyPlugin.invoke(antBuilder, attributes)
        }, IS_EMPTY_FAILURE_STRING)
        Task pluginTask = findTask('verifyXPath')
        assert findAttribute(pluginTask, 'xpath') == attributes.xpath
        assert findAttribute(pluginTask, 'regex') == 'true'
    }

    void testCheckValueNotEmptyByXPathNoValue() {
        def attributes = [xpath: '//input[@label=\'addressLine3Label\']/@value']

        assertStepFailedException({
            checkValueNotEmptyPlugin.invoke(antBuilder, attributes)
        }, IS_EMPTY_FAILURE_STRING)
        Task pluginTask = findTask('verifyXPath')
        assert findAttribute(pluginTask, 'xpath') == attributes.xpath
        assert findAttribute(pluginTask, 'regex') == 'true'
    }


    void testCheckValueNotEmptyByForLabel() {

        checkValueNotEmptyPlugin.invoke(antBuilder, [forLabel: 'addressLine1Label'])
        Task pluginTask = findTask('verifyXPath')
        assert findAttribute(pluginTask, 'regex') == 'true'
    }

    void testCheckValueNotEmptyByForLabelEmptyString() {
        assertStepFailedException({
            checkValueNotEmptyPlugin.invoke(antBuilder, [forLabel: 'addressLine2Label'])
        }, IS_EMPTY_FAILURE_STRING)
        Task pluginTask = findTask('verifyXPath')
        assert findAttribute(pluginTask, 'regex') == 'true'
    }

    void testCheckValueNotEmptyByForLabelNoValue() {
        assertStepFailedException({
            checkValueNotEmptyPlugin.invoke(antBuilder, [forLabel: 'addressLine3Label'])
        }, IS_EMPTY_FAILURE_STRING)
        Task pluginTask = findTask('verifyXPath')
        assert findAttribute(pluginTask, 'regex') == 'true'
    }

    // Raised as JIRA: MADCOW-170 - fixed 12/5/11
    void testCheckInputValueByNameInputTypeImplicit() {

        checkValueNotEmptyPlugin.invoke(antBuilder, [name: 'addressLine1Name'])
        Task pluginTask = findTask('verifyElementText')
        assert findAttribute(pluginTask, 'name') == 'addressLine1Name'
        assert findAttribute(pluginTask, 'regex') == 'true'
    }

    void testCheckValueNotEmptyByNameEmptyString() {
        assertStepFailedException({
            checkValueNotEmptyPlugin.invoke(antBuilder, [name: 'addressLine2Name'])
        }, IS_EMPTY_FAILURE_STRING)
        Task pluginTask = findTask('verifyElementText')
        assert findAttribute(pluginTask, 'regex') == 'true'
    }

    void testCheckValueNotEmptyByNameNoValue() {
        assertStepFailedException({
            checkValueNotEmptyPlugin.invoke(antBuilder, [name: 'addressLine3Name'])
        }, IS_EMPTY_FAILURE_STRING)
        Task pluginTask = findTask('verifyElementText')
        assert findAttribute(pluginTask, 'regex') == 'true'
    }

    void testCheckInputValueByNameInputTypeExplicit() {

        checkValueNotEmptyPlugin.invoke(antBuilder, [type: 'input', name: 'addressLine1Name'])
        Task pluginTask = findTask('verifyElementText')
        assert findAttribute(pluginTask, 'name') == 'addressLine1Name'
        assert findAttribute(pluginTask, 'regex') == 'true'
    }

}
