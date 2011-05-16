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

package com.projectmadcow.plugins.table

import com.projectmadcow.plugins.AbstractPluginTestCase
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathFactory
import org.w3c.dom.Element
import javax.xml.xpath.XPathConstants

class TableCountRowsWithCriteriaTest  extends AbstractPluginTestCase {


    String html
    Element htmlAsDocumentElement
    XPath xpath

    void setUp() {
        super.setUp()

        html = """<html><body>
                        <table id="searchResults">
                            <thead>
                                <tr>
                                    <th>Id</th>
                                    <th>Address Line 1</th>
                                    <th>Address Line 2</th>
                                    <th>Suburb</th>
                                    <th>State</th>
                                    <th>Postcode</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><a href="/madcow-test-site/address/show/1">1</a></td>
                                    <td>one</td>
                                    <td>1</td>
                                    <td>TENERIFFE</td>
                                    <td>Queensland</td>
                                    <td>4005</td>
                                </tr>
                                <tr>
                                    <td><a href="/madcow-test-site/address/show/2">2</a></td>
                                    <td></td>
                                    <td>320 Adelaide St</td>
                                    <td>BRISBANE</td>
                                    <td>Queensland</td>
                                    <td>4000</td>
                                </tr>
                                <tr>
                                    <td><a href="/madcow-test-site/address/show/2">3</a></td>
                                    <td>Unit A</td>
                                    <td>186 Boundary Street</td>
                                    <td>WEST END</td>
                                    <td>Queensland</td>
                                    <td>4101</td>
                                </tr>
                            </tbody>
                        </table>
                       </body></html>"""
        contextStub.setDefaultResponse(html)

        def builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        htmlAsDocumentElement = builder.parse(new ByteArrayInputStream(html.bytes)).documentElement
        xpath = XPathFactory.newInstance().newXPath()


    }

    void testCountRowsXPath() {
        def parameters = ['State' : 'Queensland']
        TableCountRowsWithCriteria rowCounter = new TableCountRowsWithCriteria( "//table[@id='searchResults']", antBuilder, '', parameters)

        assert xpath.evaluate(rowCounter.buildRowCountXPath('=', '3'), htmlAsDocumentElement, XPathConstants.STRING) == "true"
        assert xpath.evaluate(rowCounter.buildRowCountXPath('<', '4'), htmlAsDocumentElement, XPathConstants.STRING) == "true"
        assert xpath.evaluate(rowCounter.buildRowCountXPath('>', '2'), htmlAsDocumentElement, XPathConstants.STRING) == "true"

        assert xpath.evaluate(rowCounter.buildRowCountXPath('=', '8'), htmlAsDocumentElement, XPathConstants.STRING) == "false"
        assert xpath.evaluate(rowCounter.buildRowCountXPath('<', '2'), htmlAsDocumentElement, XPathConstants.STRING) == "false"
        assert xpath.evaluate(rowCounter.buildRowCountXPath('>', '4'), htmlAsDocumentElement, XPathConstants.STRING) == "false"
    }

    void testCountRowsXPathWithMultipleCriterea() {
        def parameters = ['State' : 'Queensland', 'Suburb' : 'TENERIFFE']
        TableCountRowsWithCriteria rowCounter = new TableCountRowsWithCriteria( "//table[@id='searchResults']", antBuilder, '', parameters)
        assert xpath.evaluate(rowCounter.buildRowCountXPath('=', '1'), htmlAsDocumentElement, XPathConstants.STRING) == "true"
    }

    void testCountRowsXPathWithSpecialColumnValues() {
        def parameters = ['firstColumn' : '2', 'column4' : 'BRISBANE', 'lastColumn' : '4000']
        TableCountRowsWithCriteria rowCounter = new TableCountRowsWithCriteria( "//table[@id='searchResults']", antBuilder, '', parameters)
        assert xpath.evaluate(rowCounter.buildRowCountXPath('=', '1'), htmlAsDocumentElement, XPathConstants.STRING) == "true"
    }
}
