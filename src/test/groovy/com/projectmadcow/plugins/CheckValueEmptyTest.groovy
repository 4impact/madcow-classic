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

class CheckValueEmptyTest extends AbstractPluginTestCase {

    CheckValueEmpty checkValueEmptyPlugin = new CheckValueEmpty()

    void setUp() {
        super.setUp()

        final String html = """<html><body>
                <table id="searchResults">
                    <thead>
                        <tr>
                            <th>Id</a></th>
                   	        <th>Address Line 1</a></th>
                            <th>Address Line 2</a></th>
                   	        <th>Suburb</a></th>
                   	        <th>State</a></th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr class="odd">
                            <td><a href="http://test-site.projectmadcow.com:8080/madcow-test-site/address/show/1">1</a></td>
                            <td>NotEmpty</td>
                            <td><a>nestedText</a></td>
                            <td><input type="text" value="shibby!"/></td>
                            <td><a><p>nestedNestedText</p></a></td>
                        </tr>
                        <tr class="even">
                            <td><a href="http://test-site.projectmadcow.com:8080/madcow-test-site/address/show/5"></a></td>
                            <td></td>
                            <td><a></a></td>
                            <td><input type="text" value=""/></td>
                            <td><a><p></p></a></td>
                        </tr>
                    </tbody>
                </table>

                <form>
                    <input id="inputWithNoValue" name="inputWithNoValue" label="inputWithNoValue" value="   "/>
                    <div id="divWithNoText" name="divWithNoText" style="divWithNoText"></div>

                    <input id="inputWithValue" name="inputWithValue" label="inputWithValue" value="a value"/>
                    <div id="divWithText" name="divWithText" style="divWithText">Here's some text, buddy</div>
                </form>
           </body></html>"""
        contextStub.setDefaultResponse(html)
    }

    void testFindingEmptyElementById() {
        checkValueEmptyPlugin.invoke(antBuilder, [htmlId: 'inputWithNoValue'])
        checkValueEmptyPlugin.invoke(antBuilder, [htmlId: 'divWithNoText'])
    }

    void testFindingEmptyElementByName() {
        checkValueEmptyPlugin.invoke(antBuilder, [name: 'inputWithNoValue'])
        checkValueEmptyPlugin.invoke(antBuilder, [name: 'divWithNoText'])
    }

    void testFindingEmptyElementByXPath() {
        checkValueEmptyPlugin.invoke(antBuilder, [xpath: '//input[@label=\'inputWithNoValue\']'])
        checkValueEmptyPlugin.invoke(antBuilder, [xpath: '//div[@style=\'divWithNoText\']'])
    }

    void testEmptyNestedElementsAreIgnored() {
        checkValueEmptyPlugin.invoke(antBuilder, [xpath: "//table[@id='searchResults']/tbody/tr[2]/td[1]"])
        checkValueEmptyPlugin.invoke(antBuilder, [xpath: "//table[@id='searchResults']/tbody/tr[2]/td[2]"])
        checkValueEmptyPlugin.invoke(antBuilder, [xpath: "//table[@id='searchResults']/tbody/tr[2]/td[3]"])
        checkValueEmptyPlugin.invoke(antBuilder, [xpath: "//table[@id='searchResults']/tbody/tr[2]/td[4]"])
        checkValueEmptyPlugin.invoke(antBuilder, [xpath: "//table[@id='searchResults']/tbody/tr[2]/td[5]"])
    }

    void testFindingNonEmptyElementById() {
        shouldFail { checkValueEmptyPlugin.invoke(antBuilder, [htmlId: 'inputWithValue']) }
        shouldFail { checkValueEmptyPlugin.invoke(antBuilder, [htmlId: 'divWithText']) }
    }

    void testFindingNonEmptyElementByName() {
        shouldFail { checkValueEmptyPlugin.invoke(antBuilder, [name: 'inputWithValue']) }
        shouldFail { checkValueEmptyPlugin.invoke(antBuilder, [name: 'divWithText']) }
    }

    void testFindingNonEmptyElementByXPath() {
        shouldFail { checkValueEmptyPlugin.invoke(antBuilder, [xpath: '//input[@label=\'inputWithValue\']']) }
        shouldFail { checkValueEmptyPlugin.invoke(antBuilder, [xpath: '//div[@style=\'divWithText\']']) }
    }

    void testValueIsFoundInNonEmptyNestedElements() {
        shouldFail { checkValueEmptyPlugin.invoke(antBuilder, [xpath: "//table[@id='searchResults']/tbody/tr[1]/td[1]"]) }
        shouldFail { checkValueEmptyPlugin.invoke(antBuilder, [xpath: "//table[@id='searchResults']/tbody/tr[1]/td[2]"]) }
        shouldFail { checkValueEmptyPlugin.invoke(antBuilder, [xpath: "//table[@id='searchResults']/tbody/tr[1]/td[3]"]) }
        shouldFail { checkValueEmptyPlugin.invoke(antBuilder, [xpath: "//table[@id='searchResults']/tbody/tr[1]/td[4]"]) }
        shouldFail { checkValueEmptyPlugin.invoke(antBuilder, [xpath: "//table[@id='searchResults']/tbody/tr[1]/td[5]"]) }
    }
}
