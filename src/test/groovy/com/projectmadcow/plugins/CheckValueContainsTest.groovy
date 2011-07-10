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

class CheckValueContainsTest extends AbstractPluginTestCase {

    CheckValueContains plugin = new CheckValueContains()

    XPathEvaluator evaluator 

    void setUp() {
        super.setUp()

        final String html = """<html><body>
                                    <form>
	                                    <input id="addressLine1" type="text" name="addressLine1Name" label="addressLine1Label" value="Adelaide St"/>
                                    </form>

                                    <form>
	                                    <input type="text" id="addressLine2" name="addressLine2" label="addressLine2" value="Queen St"/>
                                    </form>

                                    <form>
	                                    <input type="button" label="button1Label" id="button1Id" name="button1" value="value1" />
                                    </form>

                                    <form>
	                                    <input type="radio" label="sexMale" id="sexMale" name="sexMale" value="male" /> Male<br />
									</form>

                                    <form>
										<input type="radio" label="sexCheckedFemale" id="sexCheckedFemale" name="sexCheckedFemale" value="female" checked/> Female
									</form>

                                    <form>
									<select name="mydropdown" id="mydropdown" label="mydropdown">
										<option value="Milk">Fresh Milk</option>
										<option value="Cheese" selected="yes">Old Cheese</option>
										<option value="Bread">Hot Bread</option>
									</select>
									</form>
                               </body></html>"""
        contextStub.setDefaultResponse(html)
        evaluator = new XPathEvaluator(html)
    }

    public void testMonsterXPath() {
        println evaluator.evaluateXPath("//select[@name='addressLine1Name']/option[@selected]/text() | //*[@name='addressLine1Name']/@value")
//        println evaluator.evaluateXPath("//input[@id='mydropdown']/@value | //select[@id='mydropdown']/@value")
    }

    //TODO: All the XPaths fail if we don't put /@value at the end. That's poor.

    public void testCheckValueContainsWithUnTypedInput() {
        plugin.invoke(antBuilder, [name : 'addressLine1Name', value : 'Adelaide'])
        plugin.invoke(antBuilder, [htmlId : 'addressLine1', value : 'e St'])
        plugin.invoke(antBuilder, [forLabel : 'addressLine1Label', value : 'aid'])
        plugin.invoke(antBuilder, [xpath : "//input[@id='addressLine1']/@value", value : 'St'])

        assertStepFails { plugin.invoke(antBuilder, [name : 'addressLine1Name', value : 'afe3wa']) }
        assertStepFails { plugin.invoke(antBuilder, [htmlId : 'addressLine1', value : 'adelaide']) }
        assertStepFails { plugin.invoke(antBuilder, [forLabel : 'addressLine1Label', value : 'ADELAIDE']) }
        assertStepFails { plugin.invoke(antBuilder, [xpath : "//input[@id='addressLine1']", value : 'piglets']) }
    }


    public void testCheckValueContainsWithButton() {
        plugin.invoke(antBuilder, [name : 'button1', value : 'value1'])
        plugin.invoke(antBuilder, [htmlId : 'button1Id', value : 'value\\d'])
        plugin.invoke(antBuilder, [forLabel : 'button1Label', value : '\\w*\\d'])
        plugin.invoke(antBuilder, [xpath : "//input[@id='button1Id']/@value", value : 'u'])

        assertStepFails { plugin.invoke(antBuilder, [name : 'button1', value : 'vaAlue']) }
        assertStepFails { plugin.invoke(antBuilder, [htmlId : 'button1Id', value : 'piglets']) }
        assertStepFails { plugin.invoke(antBuilder, [forLabel : 'button1Label', value : 'valoo']) }
        assertStepFails { plugin.invoke(antBuilder, [xpath : "//input[@id='button1Id']", value : 'eu']) }
    }

    public void testCheckValueContainsWithTextField() {
        plugin.invoke(antBuilder, [name : 'addressLine2', value : 'Queen St'])
        plugin.invoke(antBuilder, [htmlId : 'addressLine2', value : 'een S'])
        plugin.invoke(antBuilder, [forLabel : 'addressLine2', value : ' '])
        plugin.invoke(antBuilder, [xpath : "//input[@id='addressLine2']/@value", value : 'Q'])

        assertStepFails { plugin.invoke(antBuilder, [name : 'addressLine2', value : 'QueensSt']) }
        assertStepFails { plugin.invoke(antBuilder, [htmlId : 'addressLine2', value : 'eeb =']) }
        assertStepFails { plugin.invoke(antBuilder, [forLabel : 'addressLine2', value : 'ig']) }
        assertStepFails { plugin.invoke(antBuilder, [xpath : "//input[@id='addressLine2']", value : 'Q\\wSt']) }
    }

    public void testCheckValueContainsWithUnCheckedRadioButtons() {
        plugin.invoke(antBuilder, [name : 'sexMale', value : 'm'])
        plugin.invoke(antBuilder, [htmlId : 'sexMale', value : '\\w*'])
        plugin.invoke(antBuilder, [forLabel : 'sexMale', value : 'ale'])
        plugin.invoke(antBuilder, [xpath : "//input[@id='sexMale']/@value", value : 'l\\w'])

        assertStepFails { plugin.invoke(antBuilder, [name : 'sexMale', value : '2']) }
        assertStepFails { plugin.invoke(antBuilder, [htmlId : 'sexMale', value : 'm a']) }
        assertStepFails { plugin.invoke(antBuilder, [forLabel : 'sexMale', value : 'ales']) }
        assertStepFails { plugin.invoke(antBuilder, [xpath : "//input[@id='sexMale']", value : '\\d']) }
    }

    public void testCheckValueContainsWithCheckedRadioButtons() {
        plugin.invoke(antBuilder, [name : 'sexCheckedFemale', value : 'fe\\w\\wle'])
        plugin.invoke(antBuilder, [htmlId : 'sexCheckedFemale', value : '\\w*[ef]'])
        plugin.invoke(antBuilder, [forLabel : 'sexCheckedFemale', value : 'female'])
        plugin.invoke(antBuilder, [xpath : "//input[@id='sexCheckedFemale']/@value", value : 'fe'])

        assertStepFails { plugin.invoke(antBuilder, [name : 'sexCheckedFemale', value : 'femail']) }
        assertStepFails { plugin.invoke(antBuilder, [htmlId : 'sexCheckedFemale', value : 'djdj']) }
        assertStepFails { plugin.invoke(antBuilder, [forLabel : 'sexCheckedFemale', value : ' ']) }
        assertStepFails { plugin.invoke(antBuilder, [xpath : "//input[@id='sexCheckedFemale']", value : '\\d']) }
    }

    /**
     * NOTE: CheckValue and CheckValueContains don't get the selected value for selects
     *       if you use xpath. Sadly, there's not a lot we can do about that without some
     *       fairly heinous hacking.
     */
    public void testCheckValueContainsWithSelect() {
        plugin.invoke(antBuilder, [htmlId : 'mydropdown', value : 'Cheese'])
        plugin.invoke(antBuilder, [name : 'mydropdown', value : 'Cheese'])
        plugin.invoke(antBuilder, [forLabel : 'mydropdown', value : 'Cheese'])

        assertStepFails { plugin.invoke(antBuilder, [htmlId : 'mydropdown', value : 'read =']) }
        assertStepFails { plugin.invoke(antBuilder, [name : 'mydropdown', value : 'Bread']) }
        assertStepFails { plugin.invoke(antBuilder, [forLabel : 'mydropdown', value : 'ilk']) }

        //plugin.invoke(antBuilder, [xpath : "//select[@id='mydropdown']/@value", value : 'Cheese'])
        //assertStepFails { plugin.invoke(antBuilder, [xpath : "//select[@id='mydropdown']", value : '8d8sd8sd']) }
    }

    private void assertStepFails(Closure pluginCall) {
        println "Blargh"
        assertStepFailedException(pluginCall, 'Wrong')
    }
}
