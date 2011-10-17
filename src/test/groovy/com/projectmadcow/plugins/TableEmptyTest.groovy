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

import groovy.lang.Closure;

import org.apache.log4j.Logger;

import com.projectmadcow.extension.webtest.xpath.XPathEvaluator;

/**
 * Test class for the Table plugin, where the table is empty: no columns and no rows.
 * [should check case where there are columns but no rows.]
 */
public class TableEmptyTest extends AbstractPluginTestCase {

    Table tablePlugin
    String html
    XPathEvaluator xPathEvaluator

	Closure checkColumnCheckedPosition
	Closure checkRowCheckedPosition
	
	static final String NOT_PRESENT_VALUE = '0'

    void setUp() {
        super.setUp()

        html = """<html><body>
                        <table id="searchResults">
                            <thead>
                                <tr>
                                </tr>
                            </thead>
                            <tbody>
                             </tbody>
                        </table>
                       </body></html>"""
        contextStub.setDefaultResponse(html)

        tablePlugin = (new Table()).invoke(antBuilder, [htmlId : 'searchResults'])
        tablePlugin.callingProperty = 'searchResults'

        xPathEvaluator = new XPathEvaluator(html)

		checkColumnCheckedPosition = { String column, String position ->
			String columnPositionXPath = tablePlugin.getColumnPositionCheckedXPath(column)
			assert xPathEvaluator.evaluateXPath(columnPositionXPath) == position
		}
		checkRowCheckedPosition = { Map attributeMap, String position ->
			String rowPositionXPath = tablePlugin.getRowPositionCheckedXPath(attributeMap)
			assert xPathEvaluator.evaluateXPath(rowPositionXPath) == position
		}

    }

    void testColumnNamePositionXPath() {
        checkColumnCheckedPosition.call('Id', NOT_PRESENT_VALUE)
        checkColumnCheckedPosition.call('Address Line 1', NOT_PRESENT_VALUE)
        checkColumnCheckedPosition.call('Address Line 2', NOT_PRESENT_VALUE)
        checkColumnCheckedPosition.call('Suburb', NOT_PRESENT_VALUE)
        checkColumnCheckedPosition.call('State', NOT_PRESENT_VALUE)
        checkColumnCheckedPosition.call('Postcode', NOT_PRESENT_VALUE)
    }
	
	void testColumnPositionXPath() {
		checkColumnCheckedPosition.call('firstColumn', NOT_PRESENT_VALUE)
		checkColumnCheckedPosition.call('lastColumn', NOT_PRESENT_VALUE)

		checkColumnCheckedPosition.call('COLUMN4', NOT_PRESENT_VALUE)
		checkColumnCheckedPosition.call('coLumn2', NOT_PRESENT_VALUE)
	}

    void testRowPositionXPath() {
        checkRowCheckedPosition.call(['Id' : '0'], NOT_PRESENT_VALUE)
        checkRowCheckedPosition.call(['Id' : '2'], NOT_PRESENT_VALUE)
        checkRowCheckedPosition.call(['Suburb' : 'BRISBANE'], NOT_PRESENT_VALUE)
        checkRowCheckedPosition.call(['Postcode' : '4005'], NOT_PRESENT_VALUE)

        checkRowCheckedPosition.call(['State' : 'Queensland', 'Suburb' : 'BRISBANE'], NOT_PRESENT_VALUE)
		checkRowCheckedPosition.call(['Suburb' : 'BRISBANE', 'State' : 'Queensland'], NOT_PRESENT_VALUE)
		
        checkRowCheckedPosition.call(['firstColumn' : '0'], NOT_PRESENT_VALUE)
        checkRowCheckedPosition.call(['lastColumn' : '4000'], NOT_PRESENT_VALUE)
		checkRowCheckedPosition.call(['lastColumn' : '4101'], NOT_PRESENT_VALUE)
		
        checkRowCheckedPosition.call(['coLumn2' :'Unit A'], NOT_PRESENT_VALUE)
    }
	
	/**
	 * Don't know whether the following are used anywhere!
	 */
//	void testRowPositionXPathFirst() {
//		String rowPositionXPath = tablePlugin.getRowCheckedXPathAccess(tablePlugin.getFirstRowPositionXPath())
//		assert xPathEvaluator.evaluateXPath(rowPositionXPath) == 'false'
//	}
//
//    void testRowPositionXPathLast() {
//        String rowPositionXPath = tablePlugin.getRowCheckedXPathAccess(tablePlugin.getLastRowPositionXPath())
//        assert xPathEvaluator.evaluateXPath(rowPositionXPath) == 'false'
//    }

    void testSelectRow() {
        shouldFail { tablePlugin.selectRow = ['Suburb' : 'BRISBANE'] }
        shouldFail { tablePlugin.selectRow = ['Suburb' : 'BRISBANE', 'State' : 'Queensland'] }
        shouldFail { tablePlugin.selectRow = ['firstColumn' : '0'] }
		shouldFail { tablePlugin.selectRow = ['lastColumn' : '4000'] }
        shouldFail { tablePlugin.selectRow = 'first' }
        shouldFail { tablePlugin.selectRow = 'last' }
		shouldFail { tablePlugin.selectRow = 'ROW3' }
        shouldFail { tablePlugin.selectRow = 'rOw32' }
    }
	
	void testHeaderColumnCountEqualsCorrect() {
		tablePlugin.headerColumnCount.equals = 0
	}

	void testHeaderColumnCountEqualsIncorrect() {
		shouldFail { tablePlugin.headerColumnCount.equals = 6 }
		shouldFail { tablePlugin.headerColumnCount.equals = 3 }
		shouldFail { tablePlugin.headerColumnCount.equals = 1 }
		shouldFail { tablePlugin.headerColumnCount.equals = -1 }
		shouldFail { tablePlugin.headerColumnCount.equals = -99 }
	}
	
	void testCountRowsNotPresentGreaterThanOrEquals() {
		def criteria = ['Postcode' : '4000']
		tablePlugin.countRows(criteria).greaterThanOrEquals = 0
		shouldFail { tablePlugin.countRows(criteria).greaterThanOrEquals = 1 }
		shouldFail { tablePlugin.countRows(criteria).greaterThanOrEquals = 2 }
		tablePlugin.countRows(criteria).greaterThanOrEquals = -99
	}
	
	void testCountRows() {
		tablePlugin.countRows.equals = 0
		tablePlugin.countRows.notEquals = 1
		tablePlugin.countRows.greaterThan = -1
		tablePlugin.countRows.greaterThanOrEquals = 0
		tablePlugin.countRows.lessThan = 1
		tablePlugin.countRows.lessThanOrEquals = 0
	}

	void testCountRowsIncorrect() {
		shouldFail { tablePlugin.countRows.equals = 1 }
		shouldFail { tablePlugin.countRows.notEquals = 0 }
		shouldFail { tablePlugin.countRows.lessThan = 0 }
	}

}
