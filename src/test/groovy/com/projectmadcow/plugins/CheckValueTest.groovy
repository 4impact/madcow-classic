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
 * Test class for the CheckValueTest plugin.
 */
class CheckValueTest extends AbstractPluginTestCase {

    CheckValue checkValuePlugin = new CheckValue()

    void setUp() {
        super.setUp()

        final String html = """<html><body>
                                    <form>
	                                    <input id="addressLine1" name="addressLine1Name" label="addressLine1Label" value="Adelaide St"/>
                                    </form>

                                    <form>
	                                    <input type="text" name="addressLine2" label="addressLine2Label" value="Queen St"/>
                                    </form>

                                    <form>
	                                    <span id="addressLine3">Apartment 7A</span>
                                    </form>

                                    <form>
	                                    <input type="button" name="button1" value="value1" />
                                    </form>

                                    <form>
	                                    <input type="text" name="firstname" value="value2" />
                                    </form>

                                    <form>
	                                    <input type="radio" name="sex" value="male" /> Male<br />
										<input type="radio" name="sex" value="female" /> Female
									</form>

                                    <form>
	                                    <input type="radio" name="sex_checked" value="man" />Man<br />
										<input type="radio" name="sex_checked" value="woman" checked/>Woman
									</form>

									<form>
									    <span id='spanWithValueAndText' value='GOATS'>goats</span>
									</form>

                                    <form>
                                        <select name="mydropdown">
                                            <option value="Milk">Fresh Milk</option>
                                            <option value="Cheese" selected="yes">Old Cheese</option>
                                            <option value="Bread">Hot Bread</option>
                                        </select>
									</form>
                               </body></html>"""
        // TODO: TC: check html validity before testing on it
        contextStub.setDefaultResponse(html)
    }

    // TODO: TC: also need to do negative tests
    // TODO: TC: also need to test for UNKNOWN pluginParameter and UNIMPLEMENTED type pluginParameter

    void testCheckValueByHtmlId() {
        checkValuePlugin.invoke(antBuilder, [htmlId: 'addressLine1', value: 'Adelaide St'])
        Task pluginTask = findTask('verifyElementText')
        assert findAttribute(pluginTask, 'htmlId') == 'addressLine1'
        assert findAttribute(pluginTask, 'text') == 'Adelaide St'
    }

    void testCheckValueByHtmlIdInputType() {
        checkValuePlugin.invoke(antBuilder, [type: 'input', htmlId: 'addressLine1', value: 'Adelaide St'])
        Task pluginTask = findTask('verifyElementText')
        assert findAttribute(pluginTask, 'htmlId') == 'addressLine1'
        assert findAttribute(pluginTask, 'text') == 'Adelaide St'
    }

    void testCheckValueByXPathWithValue() {
        def attributes = [xpath: '//input[@label=\'addressLine1Label\']/@value', value: 'Adelaide St']
        checkValuePlugin.invoke(antBuilder, attributes)
    }

    void testCheckValueByXPathWithoutValue() {
        def attributes = [xpath: '//input[@label=\'addressLine1Label\']', value: 'Adelaide St']
        checkValuePlugin.invoke(antBuilder, attributes)
    }

    void testCheckValueByXPathWithText() {
        def attributes = [xpath: '//*[@id=\'addressLine3\']/text()', value: 'Apartment 7A']
        checkValuePlugin.invoke(antBuilder, attributes)
    }

    void testCheckValueByXPathWithoutText() {
        def attributes = [xpath: '//*[@id=\'addressLine3\']', value: 'Apartment 7A']
        checkValuePlugin.invoke(antBuilder, attributes)
    }

    void testCheckValueByForLabel() {
        checkValuePlugin.invoke(antBuilder, [forLabel: 'addressLine1Label', value: 'Adelaide St'])
        Task pluginTask = findTask('verifyXPath')
        assert findAttribute(pluginTask, 'text') == 'Adelaide St'
    }

    void testCheckInputValueByNameInputTypeExplicit() {
        checkValuePlugin.invoke(antBuilder, [type: 'input', name: 'addressLine1Name', value: 'Adelaide St'])
        Task pluginTask = findTask('verifyElementText')
        assert findAttribute(pluginTask, 'name') == 'addressLine1Name'
        assert findAttribute(pluginTask, 'text') == 'Adelaide St'
    }

    void testCheckInputValueByNameInputTypeImplicit() {
        checkValuePlugin.invoke(antBuilder, [name: 'addressLine1Name', value: 'Adelaide St'])
    }

    void testCheckButtonByName() {

        checkValuePlugin.invoke(antBuilder, [type: 'input', name: 'button1', value: 'value1'])
        Task pluginTask = findTask('verifyElementText')
        assert findAttribute(pluginTask, 'name') == 'button1'
        assert findAttribute(pluginTask, 'text') == 'value1'
    }

    void testCheckRadioByNameUnchecked() {

        checkValuePlugin.invoke(antBuilder, [type: 'radio', name: 'sex', value: 'male'])
        Task pluginTask = findTask('verifyRadioButton')
        assert findAttribute(pluginTask, 'name') == 'sex'
        assert findAttribute(pluginTask, 'value') == 'male'
    }

    void testCheckRadioByNameUnchecked2() {

        checkValuePlugin.invoke(antBuilder, [type: 'radio', name: 'sex', value: 'female'])
        Task pluginTask = findTask('verifyRadioButton')
        assert findAttribute(pluginTask, 'name') == 'sex'
        assert findAttribute(pluginTask, 'value') == 'female'
    }

    void testCheckRadioByNameCheckedNo() {

        checkValuePlugin.invoke(antBuilder, [type: 'radio', name: 'sex_checked', value: 'man'])
        Task pluginTask = findTask('verifyRadioButton')
        assert findAttribute(pluginTask, 'name') == 'sex_checked'
        assert findAttribute(pluginTask, 'value') == 'man'
    }

    void testCheckRadioByNameCheckedYes() {

        assertStepFailedException({
            checkValuePlugin.invoke(antBuilder, [type: 'radio', name: 'sex_checked', value: 'woman'])
        }, "RadioButton is checked!")
        Task pluginTask = findTask('verifyRadioButton')
        assert findAttribute(pluginTask, 'name') == 'sex_checked'
        assert findAttribute(pluginTask, 'value') == 'woman'
    }

    void testCheckMenuOptionValueByName() {
        checkValuePlugin.invoke(antBuilder, [type: 'select', name: 'mydropdown', value: 'Cheese'])
        Task pluginTask = findTask('verifySelectField')
        assert findAttribute(pluginTask, 'name') == 'mydropdown'
        assert findAttribute(pluginTask, 'value') == 'Cheese'
    }

    void testCheckValueUsesTextInPreferenceToValue() {
        checkValuePlugin.invoke(antBuilder, [htmlId: 'spanWithValueAndText', value: 'goats'])
    }

}
