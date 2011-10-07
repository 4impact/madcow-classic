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
import com.projectmadcow.plugins.Table;
import com.projectmadcow.extension.webtest.xpath.XPathEvaluator

class TableCountRowsWithCriteriaTest  extends AbstractPluginTestCase {

	Table tablePlugin
    String html
    XPathEvaluator xPathEvaluator

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
                                <tr>
                                    <td><a href="/madcow-test-site/address/show/7">4</a></td>
                                    <td></td>
                                    <td>300 Adelaide St</td>
                                    <td>BRISBANE</td>
                                    <td>Queensland</td>
                                    <td>4000</td>
                                </tr>
                            </tbody>
                        </table>
                       </body></html>"""
        contextStub.setDefaultResponse(html)
		
		tablePlugin = (new Table()).invoke(antBuilder, [htmlId : 'searchResults'])
		tablePlugin.callingProperty = 'searchResults'

        xPathEvaluator = new XPathEvaluator(html)
    }

    void testCountRowsXPath() {
        def parameters = ['State' : 'Queensland']
        TableCountRowsWithCriteria rowCounter = new TableCountRowsWithCriteria( "//table[@id='searchResults']", antBuilder, '', parameters)

        assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('=', '4')) == "true"
        assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('<', '5')) == "true"
        assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('>', '3')) == "true"

        assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('=', '8')) == "false"
        assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('<', '3')) == "false"
        assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('>', '5')) == "false"
    }
	
	void testCountRowsXPathNotAll() {
		def parameters = ['Suburb' : 'BRISBANE']
		TableCountRowsWithCriteria rowCounter = new TableCountRowsWithCriteria( "//table[@id='searchResults']", antBuilder, '', parameters)

		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('!=', '0')) == "true"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('=', '2')) == "true"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('<', '3')) == "true"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('>', '1')) == "true"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('>=', '2')) == "true"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('<=', '2')) == "true"

		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('=', '8')) == "false"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('<', '2')) == "false"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('>', '2')) == "false"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('>=', '3')) == "false"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('<=', '1')) == "false"
	}

    void testCountRowsXPathWithMultipleCriteria() {
        def parameters = ['State' : 'Queensland', 'Suburb' : 'TENERIFFE']
        TableCountRowsWithCriteria rowCounter = new TableCountRowsWithCriteria( "//table[@id='searchResults']", antBuilder, '', parameters)
        assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('=', '1')) == "true"
    }

    void testCountRowsXPathWithSpecialColumnValues() {
        def parameters = ['firstColumn' : '2', 'column4' : 'BRISBANE', 'lastColumn' : '4000']
        TableCountRowsWithCriteria rowCounter = new TableCountRowsWithCriteria( "//table[@id='searchResults']", antBuilder, '', parameters)
        assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('=', '1')) == "true"
    }
	
	void testCountRowsXPathRowNotPresent() {
		def parameters = ['State' : 'NSW']
		TableCountRowsWithCriteria rowCounter = new TableCountRowsWithCriteria( "//table[@id='searchResults']", antBuilder, '', parameters)

		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('!=', '0')) == "false"
		
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('=', '0')) == "true"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('<', '4')) == "true"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('>=', '0')) == "true"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('<=', '0')) == "true"

		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('>', '2')) == "false"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('=', '8')) == "false"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('<', '2')) == "true"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('>', '4')) == "false"
	}
	
	void testCountRowsXPathWithSpecialColumnValuesRowNotPresent() {
		def parameters = ['firstColumn' : '2', 'column4' : 'BRISBANE', 'lastColumn' : '4101']
		TableCountRowsWithCriteria rowCounter = new TableCountRowsWithCriteria( "//table[@id='searchResults']", antBuilder, '', parameters)
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('=', '0')) == "true"
	}

	/**
	 * TODO: perhaps this test should fail rather than return 0 - unless it is wrapped in something that will force failure
	 */
	void testCountRowsXPathColumnNotPresent() {
		def parameters = ['Purple Monkey Dishwasher' : 'NSW']
		TableCountRowsWithCriteria rowCounter = new TableCountRowsWithCriteria( "//table[@id='searchResults']", antBuilder, '', parameters)

		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('!=', '0')) == "false"
		
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('=', '0')) == "true"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('<', '4')) == "true"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('>=', '0')) == "true"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('<=', '0')) == "true"

		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('>', '2')) == "false"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('=', '8')) == "false"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('<', '2')) == "true"
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('>', '4')) == "false"
	}
	
	/**
	 * TODO: perhaps this test should fail rather than return 0 - unless it is wrapped in something that will force failure
	 */
	void testCountRowsXPathWithSpecialColumnValuesColumnNotPresent() {
		def parameters = ['firstColumn' : '2', 'column7' : 'BRISBANE', 'lastColumn' : '4101']
		TableCountRowsWithCriteria rowCounter = new TableCountRowsWithCriteria( "//table[@id='searchResults']", antBuilder, '', parameters)
		assert xPathEvaluator.evaluateXPath(rowCounter.buildRowCountXPath('=', '0')) == "true"
	}
	
	void testCountRows() {
		tablePlugin.countRows.equals = 4
		tablePlugin.countRows.notEquals = 0
		tablePlugin.countRows.greaterThan = 3
		tablePlugin.countRows.greaterThanOrEquals = 4
		tablePlugin.countRows.lessThan = 5
		tablePlugin.countRows.lessThanOrEquals = 4
	}

	void testCountRowsIncorrect() {
		shouldFail { tablePlugin.countRows.equals = 5 }
		shouldFail { tablePlugin.countRows.notEquals = 4 }
		shouldFail { tablePlugin.countRows.lessThan = 4 }
	}

}
