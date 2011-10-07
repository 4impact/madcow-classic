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

import org.apache.log4j.Logger;

import com.projectmadcow.extension.webtest.xpath.XPathEvaluator 

import com.projectmadcow.plugins.table.TableHeaderColumnCount
import com.projectmadcow.plugins.AbstractPluginTestCase


/**
 * Test class for the Table plugin.
 */
public class TableTest extends AbstractPluginTestCase {  
	
    Table tablePlugin
    String html
    XPathEvaluator xPathEvaluator
	
	Closure checkColumnPosition
	Closure checkRowPosition
	Closure checkSelectRowEquivalentToRowPosition
	
	Closure checkRowPositionOnly
	
	Closure checkColumnCheckedPosition
	Closure checkRowCheckedPosition
	
	// *Checked* row and column positions will return '0' if the element is not present
	static final String NOT_PRESENT_VALUE = '0'
	static final String ROW_NOT_PRESENT_VALUE = NOT_PRESENT_VALUE
	static final String COLUMN_NOT_PRESENT_VALUE = NOT_PRESENT_VALUE
	
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
                                    <td><a href="/madcow-test-site/address/show/3">3</a></td>
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

		
		checkColumnPosition = { String column, String position ->
			String columnPositionXPath = tablePlugin.getColumnPositionXPath(column)
			assert xPathEvaluator.evaluateXPath(columnPositionXPath) == position
		}
		checkColumnCheckedPosition = { String column, String position ->
			String columnPositionXPath = tablePlugin.getColumnPositionCheckedXPath(column)
			assert xPathEvaluator.evaluateXPath(columnPositionXPath) == position
		}
		
		
		checkRowPositionOnly = { Map attributeMap, String position ->
			String rowPositionXPath = tablePlugin.getRowPositionXPath(attributeMap)
			assert xPathEvaluator.evaluateXPath(rowPositionXPath) == position
		}
		// double-check against the getRowPositionCheckedXPath:
		checkRowPosition = { Map attributeMap, String position ->
			String rowPositionXPath = tablePlugin.getRowPositionXPath(attributeMap)
			assert xPathEvaluator.evaluateXPath(rowPositionXPath) == position
			String rowPositionCheckedXPath = tablePlugin.getRowPositionCheckedXPath(attributeMap)
			String checkedPosition = xPathEvaluator.evaluateXPath(rowPositionCheckedXPath)
			if (checkedPosition == '0') {
				assert position == '1'
			} else {
				assert checkedPosition == position
			}
		}
		checkRowCheckedPosition = { Map attributeMap, String position ->
			String rowPositionXPath = tablePlugin.getRowPositionCheckedXPath(attributeMap)
			assert xPathEvaluator.evaluateXPath(rowPositionXPath) == position
		}

		// double-check against the selectRow:
		checkSelectRowEquivalentToRowPosition = { def selectionCriteria ->
			String position = xPathEvaluator.evaluateXPath(tablePlugin.getRowPositionCheckedXPath(selectionCriteria))
			if (position > 0) {
				tablePlugin.selectRow = selectionCriteria
				assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == position
			} else {
				shouldFail { tablePlugin.selectRow = selectionCriteria }
			}
        }
    }
	
    void testColumnPositionXPath() {
        checkColumnPosition.call('Id', '1')
        checkColumnPosition.call('Address Line 1', '2')
        checkColumnPosition.call('Address Line 2', '3')
        checkColumnPosition.call('Suburb', '4')
        checkColumnPosition.call('State', '5')
        checkColumnPosition.call('Postcode', '6')

        checkColumnPosition.call('firstColumn', '1')
        checkColumnPosition.call('lastColumn', '6')

        checkColumnPosition.call('COLUMN4','4')
        checkColumnPosition.call('coLumn2','2')
    }

	void testColumnPositionXPathNotPresent() {
		checkColumnCheckedPosition.call('Purple Monkey Dishwasher', COLUMN_NOT_PRESENT_VALUE)
		checkColumnCheckedPosition.call('coLumn21', COLUMN_NOT_PRESENT_VALUE)
	}
	
	void testColumnPositionXPathPartialPresent() {
		checkColumnCheckedPosition.call('Address', COLUMN_NOT_PRESENT_VALUE)
		checkColumnCheckedPosition.call('COL5', COLUMN_NOT_PRESENT_VALUE)
	}

    void testRowPositionXPath() {
//        checkRowPosition.call(['firstColumn' : '1'], '1')
//        checkRowPosition.call(['lastColumn' : '4000'], '4')
//		checkRowPosition.call(['lastColumn' : '4101'], '3')
//		
//        checkRowPosition.call(['coLumn2' :'Unit A'], '3')

        checkRowPosition.call(['Id' : '1'], '1')
        checkRowPosition.call(['Id' : '2'], '2')
        checkRowPosition.call(['Suburb' : 'TENERIFFE'], '1')
        checkRowPosition.call(['Suburb' : 'WEST END'], '3')
		
// count(//table[@id='searchResults']/tbody/tr/td[position() = (count(//table[@id='searchResults']/thead/tr/th[(wt:cleanText(.//text()) = 'Suburb' or wt:cleanText(.//@value) = 'Suburb')]/preceding-sibling::*)+1) and (wt:cleanText(.//text()) = 'WEST END' or wt:cleanText(.//@value) = 'WEST END')]/parent::*/preceding-sibling::*)+1
		
		
        // checkRowPosition.call(['Suburb' : 'BRISBANE'], '2')
		// seems to only return the last row position with that value:
        checkRowPosition.call(['Suburb' : 'BRISBANE'], '4')
        checkRowPosition.call(['Postcode' : '4005'], '1')

		// returns last occurrence again (not row 2):
        checkRowPosition.call(['State' : 'Queensland', 'Suburb' : 'BRISBANE'], '4')
		checkRowPosition.call(['Suburb' : 'BRISBANE', 'State' : 'Queensland'], '4')
    }

	/**
	 * Don't know whether the following are used anywhere!
	 */
//	void testRowPositionXPathFirst() {
//		String rowPositionXPath = tablePlugin.getFirstRowPositionXPath()
//		assert xPathEvaluator.evaluateXPath(rowPositionXPath) == '1'
//	}
//
//    void testRowPositionXPathLast() {
//        String rowPositionXPath = tablePlugin.getLastRowPositionXPath()
//        assert xPathEvaluator.evaluateXPath(rowPositionXPath) == '4'
//    }

	// Why do we check? Bogus columns behave as column 1 - can lead to incorrect results (spotted by Tim - MADCOW-217)
	void testRowPositionXPathHeadersSingleNotPresentUnchecked() {
		checkRowPositionOnly.call(['Purple Monkey Dishwasher' : '1'], '1')
		checkRowPositionOnly.call(['Purple Monkey Dishwasher' : '2'], '2')
		checkRowPositionOnly.call(['Purple Monkey Dishwasher' : 'BRISBANE'], '1')
		checkRowPositionOnly.call(['Purple Monkey Dishwasher' : '4'], '4')
	}

	void testRowPositionXPathHeadersSingleNotPresent() {
		checkRowCheckedPosition.call(['Purple Monkey Dishwasher' : '1'], ROW_NOT_PRESENT_VALUE)
		checkRowCheckedPosition.call(['Purple Monkey Dishwasher' : '2'], ROW_NOT_PRESENT_VALUE)
		checkRowCheckedPosition.call(['Purple Monkey Dishwasher' : 'BRISBANE'], ROW_NOT_PRESENT_VALUE)
		checkRowCheckedPosition.call(['Purple Monkey Dishwasher' : '4005'], ROW_NOT_PRESENT_VALUE)
	}
	
	void testRowPositionXPathHeadersMultipleNotPresent() {
		checkRowCheckedPosition.call(['Purple Monkey Dishwasher' : 'Queensland', 'Blue Gerbel Clothesdryer' : 'BRISBANE'], ROW_NOT_PRESENT_VALUE)
		checkRowCheckedPosition.call(['Purple Monkey Dishwasher' : 'Queensland', 'Suburb' : 'BRISBANE'], ROW_NOT_PRESENT_VALUE)
		checkRowCheckedPosition.call(['State' : 'Queensland', 'Blue Gerbel Clothesdryer' : 'BRISBANE'], ROW_NOT_PRESENT_VALUE)
	}
	
	void testRowPositionXPathSingleValuesNotPresent() {
		checkRowCheckedPosition.call(['firstColumn' : 'XXXX'], ROW_NOT_PRESENT_VALUE)
		checkRowCheckedPosition.call(['lastColumn' : '9999'], ROW_NOT_PRESENT_VALUE)

		checkRowCheckedPosition.call(['coLumn2' : 'Brickstop B'], ROW_NOT_PRESENT_VALUE)
	}

	void testRowPositionXPathSingleValuesPresent() {
		checkRowCheckedPosition.call(['firstColumn' : '1'], '1')
		checkRowCheckedPosition.call(['lastColumn' : '4101'], '3')

		checkRowCheckedPosition.call(['coLumn2' : 'one'], '1')
	}
	
	void testRowPositionXPathMultipleValuesNotPresent() {
		checkRowCheckedPosition.call(['State' : 'Queensland', 'Suburb' : 'SYDNEY'], ROW_NOT_PRESENT_VALUE)
		checkRowCheckedPosition.call(['State' : 'New South Wales', 'Suburb' : 'BRISBANE'], ROW_NOT_PRESENT_VALUE)
		checkRowCheckedPosition.call(['Suburb' : 'BRISBANE', 'State' : 'Queensland', 'Postcode' : '9999'], ROW_NOT_PRESENT_VALUE)
	}

	void testRowPositionXPathSinglePartialValuesPresent() {
        checkRowCheckedPosition.call(['Id' : '7'], ROW_NOT_PRESENT_VALUE)
        checkRowCheckedPosition.call(['Suburb' : 'ISBAN'], ROW_NOT_PRESENT_VALUE)
        checkRowCheckedPosition.call(['Postcode' : '400'], ROW_NOT_PRESENT_VALUE)
		
        checkRowCheckedPosition.call(['firstColumn' : 'madcow'], ROW_NOT_PRESENT_VALUE)
        checkRowCheckedPosition.call(['lastColumn' : '00'], ROW_NOT_PRESENT_VALUE)
		checkRowCheckedPosition.call(['lastColumn' : '101'], ROW_NOT_PRESENT_VALUE)
		
        checkRowCheckedPosition.call(['coLumn2' :'a'], ROW_NOT_PRESENT_VALUE)
	}
	
	void testRowPositionXPathMultiplePartialValuesPresent() {
		checkRowCheckedPosition.call(['State' : 'Queensland', 'Suburb' : 'BAN'], ROW_NOT_PRESENT_VALUE)
		checkRowCheckedPosition.call(['Suburb' : 'RIS', 'State' : 'Queens'], ROW_NOT_PRESENT_VALUE)
        checkRowCheckedPosition.call(['State' : 'Queensland', 'Suburb' : 'BRISBANE', 'Postcode' : '4'], ROW_NOT_PRESENT_VALUE)
	}
	
	void testSelectRowDirect() {
		tablePlugin.selectRow = 'ROW3'
		assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == '3'

		tablePlugin.selectRow = 'first'
		assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == '1'

		tablePlugin.selectRow = 'last'
		assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == '4'
	}

	void testSelectRowSearch() {
		
		tablePlugin.selectRow = ['Suburb' : 'WEST END']
		assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == '3'
		
		tablePlugin.selectRow = ['Suburb' : 'BRISBANE']
		// assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == '2'
		// once again returns last occurrence:
		assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == '4'
		
		tablePlugin.selectRow = ['Suburb' : 'BRISBANE', 'State' : 'Queensland']
		String result = contextStub.webtest.dynamicProperties.get('madcow.table.searchResults')
		// looks like last all the time:
		assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == '4'

		tablePlugin.selectRow = ['firstColumn' : '1']
		assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == '1'
		
		tablePlugin.selectRow = ['lastColumn' : '4000']
		assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == '4'
	}
	
	void testSelectRowNotPresent() {
		shouldFail { tablePlugin.selectRow = 'rOw32' }
		shouldFail { tablePlugin.selectRow = ['firstColumn' : '4000'] }
		shouldFail { tablePlugin.selectRow = ['Column1' : '4000'] }
		shouldFail { tablePlugin.selectRow = ['Column4' : 'CRAZY VALUE'] }
		shouldFail { tablePlugin.selectRow = ['lastColumn' : 'octopus'] }
		shouldFail { tablePlugin.selectRow = ['Purple Monkey Dishwasher' : '1'] }
		shouldFail { tablePlugin.selectRow = ['State' : 'Queensland', 'Blue Gerbel Clothesdryer' : 'BRISBANE'] }
	}

	void testSelectRowEquivalentToRowPositionPresent() {
		checkSelectRowEquivalentToRowPosition.call(['Suburb' : 'BRISBANE'])
		checkSelectRowEquivalentToRowPosition.call(['Suburb' : 'BRISBANE', 'State' : 'Queensland'])
		checkSelectRowEquivalentToRowPosition.call(['firstColumn' : '1'])
		checkSelectRowEquivalentToRowPosition.call(['COLUMN1' : '1'])
		checkSelectRowEquivalentToRowPosition.call(['lastColumn' : '4000'])
		checkSelectRowEquivalentToRowPosition.call(['column6' : '4000'])
		
		// NO EQUIVALENCE: groovy.lang.MissingMethodException: No signature of method: com.projectmadcow.plugins.Table.getRowPositionXPath() is applicable for argument types: (java.lang.String) values: [first]
		// checkSelectRowEquivalentToRowPosition.call('first')
		// checkSelectRowEquivalentToRowPosition.call('last')
		// checkSelectRowEquivalentToRowPosition.call('ROW3')
		
//		tablePlugin.selectRow = 'first'
//		assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == xPathEvaluator.evaluateXPath(tablePlugin.getFirstRowPositionXPath())
//
//		tablePlugin.selectRow = 'last'
//		assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == xPathEvaluator.evaluateXPath(tablePlugin.getLastRowPositionXPath())

		// NO ALTERNATIVE
		// tablePlugin.selectRow = 'ROW3'
		// assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == xPathEvaluator.evaluateXPath(tablePlugin.?????????????())
	}

	void testSelectRowEquivalentToRowPositionNotPresent() {
		// NO EQUIVALENCE: groovy.lang.MissingMethodException: No signature of method: com.projectmadcow.plugins.Table.getRowPositionXPath() is applicable for argument types: (java.lang.String) values: [rOw32]
		// checkSelectRowEquivalentToRowPosition.call('rOw32')
		shouldFail { checkSelectRowEquivalentToRowPosition.call(['firstColumn' : '4000']) }
		shouldFail { checkSelectRowEquivalentToRowPosition.call(['Column1' : '4000']) }
		shouldFail { checkSelectRowEquivalentToRowPosition.call(['Column4' : 'CRAZY VALUE']) }
		shouldFail { checkSelectRowEquivalentToRowPosition.call(['lastColumn' : 'octopus']) }
		shouldFail { checkSelectRowEquivalentToRowPosition.call(['Purple Monkey Dishwasher' : '1']) }
		shouldFail { checkSelectRowEquivalentToRowPosition.call(['State' : 'Queensland', 'Blue Gerbel Clothesdryer' : 'BRISBANE']) }
	}

	void testCountRowsPresentEqualsCorrect() {
		def criteria = ['Address Line 2' : '186 Boundary Street']
		tablePlugin.countRows(criteria).equals = 1
	}

	void testCountRowsPresentEqualsIncorrect() {
		def criteria = ['Address Line 2' : '186 Boundary Street']
		shouldFail { tablePlugin.countRows(criteria).equals = 0 }
		shouldFail { tablePlugin.countRows(criteria).equals = 2 }
		shouldFail { tablePlugin.countRows(criteria).equals = 99 }
	}

	void testCountRowsNotPresentEqualsCorrect() {
		def criteria = ['Address Line 2' : '23 No Street']
		tablePlugin.countRows(criteria).equals = 0
	}
	
	void testCountRowsNotPresentEqualsIncorrect() {
		def criteria = ['Address Line 2' : '23 No Street']
		shouldFail { tablePlugin.countRows(criteria).equals = 1 }
		shouldFail { tablePlugin.countRows(criteria).equals = 4 }
		shouldFail { tablePlugin.countRows(criteria).equals = -2 }
	}

	void testCountRowsPresentGreaterThanOrEqualsCorrect() {
		def criteria = ['Postcode' : '4000']
		tablePlugin.countRows(criteria).greaterThanOrEquals = 0
		tablePlugin.countRows(criteria).greaterThanOrEquals = 1
		tablePlugin.countRows(criteria).greaterThanOrEquals = 2
		tablePlugin.countRows(criteria).greaterThanOrEquals = -99
	}
	
	void testCountRowsPresentGreaterThanOrEqualsIncorrect() {
		shouldFail { tablePlugin.countRows(['Postcode' : '4000']).greaterThanOrEquals = 3 }
	}

	void testCountRowsPresentLessThanOrEqualsCorrect() {
		def criteria = ['Postcode' : '4000']
		tablePlugin.countRows(criteria).lessThanOrEquals = 2
		tablePlugin.countRows(criteria).lessThanOrEquals = 3
		tablePlugin.countRows(criteria).lessThanOrEquals = 99
	}

	void testCountRowsPresentLessThanOrEqualsIncorrect() {
		def criteria = ['Postcode' : '4000']
		shouldFail { tablePlugin.countRows(criteria).lessThanOrEquals = 0 }
		shouldFail { tablePlugin.countRows(criteria).lessThanOrEquals = 1 }
		shouldFail { tablePlugin.countRows(criteria).lessThanOrEquals = -99 }
	}
	
	void testCountRowsMultiCriteriaPresentEqualsCorrect() {
		def criteria = ['Address Line 2' : '320 Adelaide St']
		tablePlugin.countRows(criteria).equals = 1
	}

	void testCountRowsMultiCriteriaPresentEqualsIncorrect() {
		def criteria = ['Postcode' : '4000', 'Address Line 2' : '320 Adelaide St']
		shouldFail { tablePlugin.countRows(criteria).equals = 0 }
		shouldFail { tablePlugin.countRows(criteria).equals = 2 }
		shouldFail { tablePlugin.countRows(criteria).equals = 99 }
	}

	void testCountRowsMultiCriteriaNotPresentEqualsCorrect() {
		tablePlugin.countRows(['Postcode' : '4000', 'Address Line 2' : '23 No Street']).equals = 0
	}
	
	void testCountRowsMultiCriteriaNotPresentEqualsIncorrect() {
		def criteria = ['Postcode' : '4000', 'Address Line 2' : '23 No Street']
		shouldFail { tablePlugin.countRows(criteria).equals = 1 }
		shouldFail { tablePlugin.countRows(criteria).equals = 4 }
		shouldFail { tablePlugin.countRows(criteria).equals = -2 }
	}

	void testHeaderColumnCountEqualsCorrect() {
		tablePlugin.headerColumnCount.equals = 6
	}

	void testHeaderColumnCountEqualsIncorrect() {
		shouldFail { tablePlugin.headerColumnCount.equals = 7 }
		shouldFail { tablePlugin.headerColumnCount.equals = 5 }
		shouldFail { tablePlugin.headerColumnCount.equals = 1 }
		shouldFail { tablePlugin.headerColumnCount.equals = 0 }
		shouldFail { tablePlugin.headerColumnCount.equals = -99 }
	}
	
	void testHeaderColumnCountNotEqualsCorrect() {
		tablePlugin.headerColumnCount.notEquals = 0
		tablePlugin.headerColumnCount.notEquals = 1
		tablePlugin.headerColumnCount.notEquals = 5
		tablePlugin.headerColumnCount.notEquals = 7
	}
	
	void testHeaderColumnCountNotEqualsIncorrect() {
		shouldFail { tablePlugin.headerColumnCount.notEquals = 6 }
	}

	void testHeaderColumnCountLessThanOrEqualsCorrect() {
		tablePlugin.headerColumnCount.lessThanOrEquals = 6
		tablePlugin.headerColumnCount.lessThanOrEquals = 7
		tablePlugin.headerColumnCount.lessThanOrEquals = 9
	}

	void testHeaderColumnCountLessThanOrEqualsIncorrect() {
		shouldFail { tablePlugin.headerColumnCount.lessThanOrEquals = 5 }
		shouldFail { tablePlugin.headerColumnCount.lessThanOrEquals = 1 }
		shouldFail { tablePlugin.headerColumnCount.lessThanOrEquals = 0 }
		shouldFail { tablePlugin.headerColumnCount.lessThanOrEquals = -99 }
	}
	
	void testHeaderColumnCountLessThanCorrect() {
		tablePlugin.headerColumnCount.lessThan = 7
		tablePlugin.headerColumnCount.lessThan = 9
	}

	void testHeaderColumnCountLessThanIncorrect() {
		shouldFail { tablePlugin.headerColumnCount.lessThan = 6 }
		shouldFail { tablePlugin.headerColumnCount.lessThan = 5 }
		shouldFail { tablePlugin.headerColumnCount.lessThan = 1 }
		shouldFail { tablePlugin.headerColumnCount.lessThan = 0 }
		shouldFail { tablePlugin.headerColumnCount.lessThan = -99 }
	}

	void testHeaderColumnCountGreaterThanOrEqualsCorrect() {
		tablePlugin.headerColumnCount.greaterThanOrEquals = 6
		tablePlugin.headerColumnCount.greaterThanOrEquals = 5
		tablePlugin.headerColumnCount.greaterThanOrEquals = 1
		tablePlugin.headerColumnCount.greaterThanOrEquals = 0
		tablePlugin.headerColumnCount.greaterThanOrEquals = -99
	}

	void testHeaderColumnCountGreaterThanOrEqualsIncorrect() {
		shouldFail { tablePlugin.headerColumnCount.greaterThanOrEquals = 7 }
		shouldFail { tablePlugin.headerColumnCount.greaterThanOrEquals = 9 }
	}
	
	void testHeaderColumnCountGreaterThanCorrect() {
		tablePlugin.headerColumnCount.greaterThan = 5
		tablePlugin.headerColumnCount.greaterThan = 0
		tablePlugin.headerColumnCount.greaterThan = -99
	}

	void testHeaderColumnCountGreaterThanIncorrect() {
		shouldFail { tablePlugin.headerColumnCount.greaterThan = 6 }
		shouldFail { tablePlugin.headerColumnCount.greaterThan = 7 }
		shouldFail { tablePlugin.headerColumnCount.greaterThan = 99 }
	}

}
