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

import java.util.Map;

import org.apache.log4j.Logger;

import com.projectmadcow.extension.webtest.xpath.XPathEvaluator 

import com.projectmadcow.plugins.AbstractPluginTestCase
import com.projectmadcow.plugins.table.TableXPath;


/**
 * Test class for the Table XPath (static util) "parsing" and generation.
 */
public class TableXPathTest extends AbstractPluginTestCase {  

	static final String PREFIX = 'PREFIX'
	static final String COL_HDR = 'COL_HDR'
	static final String ROW_VALUE = 'ROW_VALUE'
	static final String ROW_POS = 'ROW_POS'
	static final String COL_HDR_2 = 'COL_HDR_2'
	static final String ROW_VALUE_2 = 'ROW_VALUE_2'
	static final String COL_HDR_3 = 'COL_HDR_3'
	static final String ROW_VALUE_3 = 'ROW_VALUE_3'

	// TODO: it would be nice to have a bracket check for both () and []
	/*
	 * actually do properly - the sets of brackets are coupled i.e. the opens and closes must be on corresponding depths of the other
	 */
    boolean bracketsMatchOrthogonal(String str) {
		return bracketsMatch(str, '(', ')') && bracketsMatch(str, '[', ']')
	}
    boolean bracketsMatch(String str, String open, String close) {
		int depth = 0
		for (int i = 0; (i < str.size() && depth >= 0); i++) {
			if (str.getAt(i) == open) depth++
			if (str.getAt(i) == close) depth--
		}
		return depth == 0
	}
	boolean bracketsMatch(String str) {
		return bracketsMatch(str, '(', ')', '[', ']')
	}

    void testTableReferenceXPath() {
		String prefixXPath = (new TableXPath()).getTableReferenceXPath('searchResults')
		LOG.debug "testTableReferenceXPath() --> ${prefixXPath}"
		
		assert bracketsMatchOrthogonal(prefixXPath)
		assert prefixXPath == "//table[@id='searchResults']"
	}


	
	void testColumnPositionXPathFirst() {
		String ref = (new TableXPath()).getColumnPositionXPath(PREFIX, 'firstColumn')
		LOG.debug "testColumnPositionXPathFirst() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "1"
	}
	
	void testColumnPositionXPathLast() {
		String ref = (new TableXPath()).getColumnPositionXPath(PREFIX, 'lastColumn')
		LOG.debug "testColumnPositionXPathLast() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/thead/tr/th[position() = last()]/preceding-sibling::*)+1"
	}
	
	void testColumnPositionXPathNumber() {
		String ref = (new TableXPath()).getColumnPositionXPath(PREFIX, 'column27')
		LOG.debug "testColumnPositionXPath() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "27"
	}

	void testColumnPositionXPathCriteria() {
		String ref = (new TableXPath()).getColumnPositionXPath(PREFIX, COL_HDR)
		LOG.debug "testColumnPositionXPathCriteria() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+1"
	}
	
	void testColumnPositionCheckedXPathFirst() {
		String ref = (new TableXPath()).getColumnPositionCheckedXPath(PREFIX, 'firstColumn')
		LOG.debug "testColumnPositionCheckedXPathFirst() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/thead/tr/th[1]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[1]))"
	}
	
	void testColumnPositionCheckedXPathLast() {
		String ref = (new TableXPath()).getColumnPositionCheckedXPath(PREFIX, 'lastColumn')
		LOG.debug "testColumnPositionXPathLast() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/thead/tr/th[position() = last()]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[position() = last()]))"
	}
	
	void testColumnPositionCheckedXPathNumber() {
		String ref = (new TableXPath()).getColumnPositionCheckedXPath(PREFIX, 'column27')
		LOG.debug "testColumnPositionCheckedXPathNumber() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/thead/tr/th[27]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[27]))"
	}

	void testColumnPositionCheckedXPathCriteria() {
		String ref = (new TableXPath()).getColumnPositionCheckedXPath(PREFIX, COL_HDR)
		LOG.debug "testColumnPositionCheckedXPathCriteria() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]))"
	}
	
	
	
	void testRowReferenceXPathFirst() {
		String ref = (new TableXPath()).getRowReferenceXPath(PREFIX, 'first')
		LOG.debug "testRowReferenceXPathFirst() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "1"
	}
	
	void testRowReferenceXPathFirstCriteria() {
		String ref = (new TableXPath()).getRowReferenceXPath(PREFIX, ['firstColumn' : ROW_VALUE])
		LOG.debug "testRowReferenceXPathFirstCriteria() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "PREFIX/tbody/tr/td[position() = (1) and (wt:cleanText(.//text()) = 'ROW_VALUE' or wt:cleanText(.//@value) = 'ROW_VALUE')]/parent::*"
	}

	void testRowPositionXPathFirst() {
		String ref = (new TableXPath()).getRowPositionXPath(PREFIX, 'first')
		LOG.debug "testRowPositionXPathFirst() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "1"
	}
	
	void testRowPositionXPathLast() {
		String ref = (new TableXPath()).getRowPositionXPath(PREFIX, 'last')
		LOG.debug "testRowPositionXPathLast() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/tbody/tr[position() = last()]/preceding-sibling::*)+1"
	}
	
	void testRowPositionXPathNumber() {
		String ref = (new TableXPath()).getRowPositionXPath(PREFIX, 'row27')
		LOG.debug "testRowPositionXPath() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "27"
	}

	void testRowPositionXPathCriteriaCol1() {
		String ref = (new TableXPath()).getRowPositionXPath(PREFIX, ['firstColumn' : ROW_VALUE])
		LOG.debug "testRowPositionXPathCriteriaCol1() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/tbody/tr/td[position() = (1) and (wt:cleanText(.//text()) = 'ROW_VALUE' or wt:cleanText(.//@value) = 'ROW_VALUE')]/parent::*/preceding-sibling::*)+1"
	}

	void testRowPositionXPathCriteriaColLast() {
		String ref = (new TableXPath()).getRowPositionXPath(PREFIX, ['lastColumn' : ROW_VALUE])
		LOG.debug "testRowPositionXPathCriteriaColLast() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/tbody/tr/td[position() = (count(PREFIX/thead/tr/th[position() = last()]/preceding-sibling::*)+1) and (wt:cleanText(.//text()) = 'ROW_VALUE' or wt:cleanText(.//@value) = 'ROW_VALUE')]/parent::*/preceding-sibling::*)+1"
	}
	
	void testRowPositionXPathCriteriaColNum() {
		String ref = (new TableXPath()).getRowPositionXPath(PREFIX, ['column27' : ROW_VALUE])
		LOG.debug "testRowPositionXPathCriteriaColNum() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/tbody/tr/td[position() = (27) and (wt:cleanText(.//text()) = 'ROW_VALUE' or wt:cleanText(.//@value) = 'ROW_VALUE')]/parent::*/preceding-sibling::*)+1"
	}
	
	void testRowPositionXPathCriteria() {
		String ref = (new TableXPath()).getRowPositionXPath(PREFIX, [COL_HDR : ROW_VALUE])
		LOG.debug "testRowPositionXPathCriteria() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/tbody/tr/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+1) and (wt:cleanText(.//text()) = 'ROW_VALUE' or wt:cleanText(.//@value) = 'ROW_VALUE')]/parent::*/preceding-sibling::*)+1"
	}
	
	void testRowPositionXPathCriteria2() {
		String ref = (new TableXPath()).getRowPositionXPath(PREFIX, [COL_HDR : ROW_VALUE, COL_HDR_2 : ROW_VALUE_2])
		LOG.debug "testRowPositionXPathCriteria2() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/tbody/tr/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+1) and (wt:cleanText(.//text()) = 'ROW_VALUE' or wt:cleanText(.//@value) = 'ROW_VALUE')]/parent::*/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_2' or wt:cleanText(.//@value) = 'COL_HDR_2')]/preceding-sibling::*)+1) and (wt:cleanText(.//text()) = 'ROW_VALUE_2' or wt:cleanText(.//@value) = 'ROW_VALUE_2')]/parent::*/preceding-sibling::*)+1"
	}
	
	void testRowPositionXPathCriteria3() {
		String ref = (new TableXPath()).getRowPositionXPath(PREFIX, [COL_HDR : ROW_VALUE, COL_HDR_2 : ROW_VALUE_2, COL_HDR_3 : ROW_VALUE_3])
		LOG.debug "testRowPositionXPathCriteria3() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/tbody/tr/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+1) and (wt:cleanText(.//text()) = 'ROW_VALUE' or wt:cleanText(.//@value) = 'ROW_VALUE')]/parent::*/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_2' or wt:cleanText(.//@value) = 'COL_HDR_2')]/preceding-sibling::*)+1) and (wt:cleanText(.//text()) = 'ROW_VALUE_2' or wt:cleanText(.//@value) = 'ROW_VALUE_2')]/parent::*/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_3' or wt:cleanText(.//@value) = 'COL_HDR_3')]/preceding-sibling::*)+1) and (wt:cleanText(.//text()) = 'ROW_VALUE_3' or wt:cleanText(.//@value) = 'ROW_VALUE_3')]/parent::*/preceding-sibling::*)+1"
	}

	void testRowPositionCheckedXPathFirst() {
		String ref = (new TableXPath()).getRowPositionCheckedXPath(PREFIX, 'first')
		LOG.debug "testRowPositionCheckedXPathFirst() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/tbody/tr[1]/preceding-sibling::*)+number(boolean(PREFIX/tbody/tr[1]))"
	}
	
	void testRowPositionCheckedXPathLast() {
		String ref = (new TableXPath()).getRowPositionCheckedXPath(PREFIX, 'last')
		LOG.debug "testRowPositionCheckedXPathLast() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/tbody/tr[position() = last()]/preceding-sibling::*)+number(boolean(PREFIX/tbody/tr[position() = last()]))"
	}
	
	void testRowPositionCheckedXPathNumber() {
		String ref = (new TableXPath()).getRowPositionCheckedXPath(PREFIX, 'row27')
		LOG.debug "testRowPositionCheckedXPathNumber() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/tbody/tr[27]/preceding-sibling::*)+number(boolean(PREFIX/tbody/tr[27]))"
	}

	void testRowPositionCheckedXPathCriteria() {
		String ref = (new TableXPath()).getRowPositionCheckedXPath(PREFIX, [COL_HDR : ROW_VALUE])
		LOG.debug "testRowPositionCheckedXPathCriteria() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/tbody/tr/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE' or wt:cleanText(.//@value) = 'ROW_VALUE')]/parent::*/preceding-sibling::*)+number(boolean(PREFIX/tbody/tr/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE' or wt:cleanText(.//@value) = 'ROW_VALUE')]/parent::*))"
	}
	
	void testCellXPath() {
		String ref = (new TableXPath()).getCellXPath(PREFIX, ROW_POS, COL_HDR)
		LOG.debug "testCellXPath() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "PREFIX/tbody/tr[ROW_POS]/td[count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+1]"
	}
	
	void testRowCheckedXPathAccess() {
		String ref = (new TableXPath()).getRowCheckedXPathAccess(PREFIX, ROW_POS)
		LOG.debug "testColumnPositionXPath() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "boolean(PREFIX/tbody/tr[ROW_POS])"
	}

	void testCellXPathMap() {
		String ref = (new TableXPath()).getCellXPath(PREFIX, [COL_HDR : ROW_VALUE], COL_HDR)
		LOG.debug "testCellXPathMap() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "PREFIX/tbody/tr[count(PREFIX/tbody/tr/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+1) and (wt:cleanText(.//text()) = 'ROW_VALUE' or wt:cleanText(.//@value) = 'ROW_VALUE')]/parent::*/preceding-sibling::*)+1]/td[count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+1]"
	}

	
	void testRowPositionXPath2() {
		String ref = (new TableXPath()).getRowPositionXPath(PREFIX, [COL_HDR : ROW_VALUE, COL_HDR_2 : ROW_VALUE_2])
		LOG.debug "testRowPositionXPath2() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/tbody/tr/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+1) and (wt:cleanText(.//text()) = 'ROW_VALUE' or wt:cleanText(.//@value) = 'ROW_VALUE')]/parent::*/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_2' or wt:cleanText(.//@value) = 'COL_HDR_2')]/preceding-sibling::*)+1) and (wt:cleanText(.//text()) = 'ROW_VALUE_2' or wt:cleanText(.//@value) = 'ROW_VALUE_2')]/parent::*/preceding-sibling::*)+1"
	}
	
	void testRowPositionCheckedXPath2() {
		String ref = (new TableXPath()).getRowPositionCheckedXPath(PREFIX, [COL_HDR : ROW_VALUE, COL_HDR_2 : ROW_VALUE_2])
		LOG.debug "testRowPositionCheckedXPath2() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/tbody/tr/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE' or wt:cleanText(.//@value) = 'ROW_VALUE')]/parent::*/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_2' or wt:cleanText(.//@value) = 'COL_HDR_2')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_2' or wt:cleanText(.//@value) = 'COL_HDR_2')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE_2' or wt:cleanText(.//@value) = 'ROW_VALUE_2')]/parent::*/preceding-sibling::*)+number(boolean(PREFIX/tbody/tr/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE' or wt:cleanText(.//@value) = 'ROW_VALUE')]/parent::*/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_2' or wt:cleanText(.//@value) = 'COL_HDR_2')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_2' or wt:cleanText(.//@value) = 'COL_HDR_2')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE_2' or wt:cleanText(.//@value) = 'ROW_VALUE_2')]/parent::*))"
	}

	void testCellXPathMap2() {
		String ref = (new TableXPath()).getCellXPath(PREFIX, [COL_HDR : ROW_VALUE, COL_HDR_2 : ROW_VALUE_2], COL_HDR)
		LOG.debug "testCellXPathMap2() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "PREFIX/tbody/tr[count(PREFIX/tbody/tr/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+1) and (wt:cleanText(.//text()) = 'ROW_VALUE' or wt:cleanText(.//@value) = 'ROW_VALUE')]/parent::*/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_2' or wt:cleanText(.//@value) = 'COL_HDR_2')]/preceding-sibling::*)+1) and (wt:cleanText(.//text()) = 'ROW_VALUE_2' or wt:cleanText(.//@value) = 'ROW_VALUE_2')]/parent::*/preceding-sibling::*)+1]/td[count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+1]"
	}
	
	void testRowPositionCheckedXPath3() {
		String ref = (new TableXPath()).getRowPositionCheckedXPath(PREFIX, [COL_HDR : ROW_VALUE, COL_HDR_2 : ROW_VALUE_2, COL_HDR_3 : ROW_VALUE_3])
		LOG.debug "testRowPositionCheckedXPath3() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/tbody/tr/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE' or wt:cleanText(.//@value) = 'ROW_VALUE')]/parent::*/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_2' or wt:cleanText(.//@value) = 'COL_HDR_2')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_2' or wt:cleanText(.//@value) = 'COL_HDR_2')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE_2' or wt:cleanText(.//@value) = 'ROW_VALUE_2')]/parent::*/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_3' or wt:cleanText(.//@value) = 'COL_HDR_3')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_3' or wt:cleanText(.//@value) = 'COL_HDR_3')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE_3' or wt:cleanText(.//@value) = 'ROW_VALUE_3')]/parent::*/preceding-sibling::*)+number(boolean(PREFIX/tbody/tr/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE' or wt:cleanText(.//@value) = 'ROW_VALUE')]/parent::*/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_2' or wt:cleanText(.//@value) = 'COL_HDR_2')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_2' or wt:cleanText(.//@value) = 'COL_HDR_2')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE_2' or wt:cleanText(.//@value) = 'ROW_VALUE_2')]/parent::*/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_3' or wt:cleanText(.//@value) = 'COL_HDR_3')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_3' or wt:cleanText(.//@value) = 'COL_HDR_3')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE_3' or wt:cleanText(.//@value) = 'ROW_VALUE_3')]/parent::*))"
	}
	
	void testRowPositionCheckedXPath4() {
		String ref = (new TableXPath()).getRowPositionCheckedXPath(PREFIX, [COL_HDR : ROW_VALUE, COL_HDR_2 : ROW_VALUE_2, COL_HDR_3 : ROW_VALUE_3, 'COL_HDR_4' : 'ROW_VALUE_4'])
		LOG.debug "testRowPositionCheckedXPath4() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(PREFIX/tbody/tr/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE' or wt:cleanText(.//@value) = 'ROW_VALUE')]/parent::*/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_2' or wt:cleanText(.//@value) = 'COL_HDR_2')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_2' or wt:cleanText(.//@value) = 'COL_HDR_2')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE_2' or wt:cleanText(.//@value) = 'ROW_VALUE_2')]/parent::*/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_3' or wt:cleanText(.//@value) = 'COL_HDR_3')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_3' or wt:cleanText(.//@value) = 'COL_HDR_3')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE_3' or wt:cleanText(.//@value) = 'ROW_VALUE_3')]/parent::*/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_4' or wt:cleanText(.//@value) = 'COL_HDR_4')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_4' or wt:cleanText(.//@value) = 'COL_HDR_4')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE_4' or wt:cleanText(.//@value) = 'ROW_VALUE_4')]/parent::*/preceding-sibling::*)+number(boolean(PREFIX/tbody/tr/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR' or wt:cleanText(.//@value) = 'COL_HDR')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE' or wt:cleanText(.//@value) = 'ROW_VALUE')]/parent::*/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_2' or wt:cleanText(.//@value) = 'COL_HDR_2')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_2' or wt:cleanText(.//@value) = 'COL_HDR_2')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE_2' or wt:cleanText(.//@value) = 'ROW_VALUE_2')]/parent::*/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_3' or wt:cleanText(.//@value) = 'COL_HDR_3')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_3' or wt:cleanText(.//@value) = 'COL_HDR_3')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE_3' or wt:cleanText(.//@value) = 'ROW_VALUE_3')]/parent::*/td[position() = (count(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_4' or wt:cleanText(.//@value) = 'COL_HDR_4')]/preceding-sibling::*)+number(boolean(PREFIX/thead/tr/th[(wt:cleanText(.//text()) = 'COL_HDR_4' or wt:cleanText(.//@value) = 'COL_HDR_4')]))) and (wt:cleanText(.//text()) = 'ROW_VALUE_4' or wt:cleanText(.//@value) = 'ROW_VALUE_4')]/parent::*))"
	}

	
	/* ======== SOME REALISTIC ONES ============== */

	void testColumnPositionCheckedXPathRealistic() {
		String prefixXPath = (new TableXPath()).getTableReferenceXPath('searchResults')
		String ref = (new TableXPath()).getColumnPositionCheckedXPath(prefixXPath, 'Suburb')
		LOG.debug "testColumnPositionCheckedXPath() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(//table[@id='searchResults']/thead/tr/th[(wt:cleanText(.//text()) = 'Suburb' or wt:cleanText(.//@value) = 'Suburb')]/preceding-sibling::*)+number(boolean(//table[@id='searchResults']/thead/tr/th[(wt:cleanText(.//text()) = 'Suburb' or wt:cleanText(.//@value) = 'Suburb')]))"
	}
	
	void testRowPositionXPathCriteriaRealistic1() {
		String prefixXPath = (new TableXPath()).getTableReferenceXPath('searchResults')
		String ref = (new TableXPath()).getRowPositionXPath(prefixXPath, ['Suburb' : 'WEST END'])
		LOG.debug "testRowPositionXPathCriteria() --> ${ref}"
		assert bracketsMatchOrthogonal(ref)
		assert ref == "count(//table[@id='searchResults']/tbody/tr/td[position() = (count(//table[@id='searchResults']/thead/tr/th[(wt:cleanText(.//text()) = 'Suburb' or wt:cleanText(.//@value) = 'Suburb')]/preceding-sibling::*)+1) and (wt:cleanText(.//text()) = 'WEST END' or wt:cleanText(.//@value) = 'WEST END')]/parent::*/preceding-sibling::*)+1"
	}

}
