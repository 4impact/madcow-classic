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

import com.projectmadcow.engine.plugin.Plugin
import com.projectmadcow.engine.plugin.PluginResolver
import org.apache.log4j.Logger

/**
 * Just bringing a lot of the table-related XPath generation into one place in order to ease maintenance 
 * (separating the plugin-calling aspect from the detail of the XPath generation).
 * Just a static util class.
 */
public class TableXPath {

    static final Logger LOG = Logger.getLogger(TableXPath.class)

		
	/**
	 *
	 * @return 1 if column does not exist, 1 or more if it does. i.e. undistinguished
	 */
	public static String getColumnPositionXPath(String prefixXPath, def columnHeader) {
		def result = columnSieve(columnHeader) { getCellXPathRef(it) } { getUncheckedPositionXPath( getColumnXPath(prefixXPath, it) ) }
		if (LOG.isDebugEnabled()) LOG.debug "getColumnPositionXPath($columnHeader) --> ${result}"
		return result
	}

	/**
	 * Use the return value of '0' to distinguish the case when the column does not exist.
	 * (Normal column indices begin at 1.)
	 * The 0 index will fail in Webtest if it is ever used.
	 *
	 * @return 0 if column does not exist, 1 or more if it does.
	 */
	public static String getColumnPositionCheckedXPath(String prefixXPath, def columnHeader) {
		def result = columnSieve(columnHeader) { getCellXPathRef(it) }
		result = getCheckedPositionXPath( getColumnXPath(prefixXPath, result) )
		if (LOG.isDebugEnabled()) LOG.debug "getColumnPositionCheckedXPath($columnHeader) --> ${result}"
		return result
	}
	
	/**
	 * Returns an xpath expression to get the row number within the table, with the specific cellText.
	 * Parameter can be first, last, rowNN, or a map of [columnHeaderText : cellText]
	 * @return 1 if row does not exist, 1 or more if it does. i.e. undistinguished
	 */
	public static def getRowPositionXPath(String prefixXPath, def criteria) {
		def rowXPath = rowSieve(criteria) { getRowReferenceXPath(prefixXPath, it) } { getUncheckedPositionXPath(it) } { it } { getRowXPath(prefixXPath, it ) }
		if (LOG.isDebugEnabled()) LOG.debug("getRowPositionXPath(${criteria})  = ${rowXPath}")
		return rowXPath
	}

	/**
	 * @return 0 if row does not exist, 1 or more if it does.
	 */
	public static def getRowPositionCheckedXPath(String prefixXPath, def criteria) {
		def rowXPath = rowSieve(criteria) { getRowReferenceCheckedXPath(prefixXPath, it) } { it } { getRowXPath(prefixXPath, it) } 
		rowXPath = getCheckedPositionXPath( rowXPath )
		if (LOG.isDebugEnabled()) LOG.debug("getRowPositionCheckedXPath(${criteria})  = ${rowXPath}")
	   return rowXPath
	}

	public static String getRowCheckedXPathAccess(String prefixXPath, String rowPositionXPath) {
		 return getCheckedXPathAccess(getRowXPath(prefixXPath, rowPositionXPath))
	}

    /**
     * Returns an xpath expression for a particular cell on a particular row
     */
    public static def getCellXPath(String prefixXPath, String rowPositionXPath, def columnHeaderText) {
        def xpath = "${getRowXPath(prefixXPath, rowPositionXPath)}/td[${getColumnPositionXPath(prefixXPath, columnHeaderText)}]"
        if (LOG.isDebugEnabled()) LOG.debug("getCellXPath(${rowPositionXPath}) = ${xpath}")
		return xpath
    }

    public static def getCellXPath(String prefixXPath, Map rowPositionMap, def columnHeaderText) {
        return getCellXPath(prefixXPath, getRowPositionXPath(prefixXPath, rowPositionMap), columnHeaderText)
    }
	
	public static String getRowReferenceXPath(String prefixXPath, def selectionCriteria) {
		return rowSieve(selectionCriteria) { getRowReferenceXPathMapped(prefixXPath, it) {p,c -> getColumnPositionXPath(p,c)} }
	}

	public static String getRowReferenceCheckedXPath(String prefixXPath, def selectionCriteria) {
		if (LOG.isDebugEnabled()) LOG.debug("getRowReferenceCheckedXPath(${selectionCriteria})  going to sieveTransform...")
		return rowSieve(selectionCriteria) { getRowReferenceXPathMapped(prefixXPath, it) {p,c -> getColumnPositionCheckedXPath(p,c)} }
	}

	private static String getColumnReferenceXPath(def columnHeader) {
		def result = columnSieve(columnHeader) { getCellXPathRef(it) }
		if (LOG.isDebugEnabled()) LOG.debug "getColumnReferenceXPath($columnHeader) --> ${result}"
		return result
	}

	private static String rowSieve(def selectionCriteria, Closure criteriaMapper, Closure positionMapper = {it}, Closure nonCriteriaMapper = {it}, Closure lastMapper = {it}) {
		sieveTransform("first", "last", "row", selectionCriteria, criteriaMapper, positionMapper, nonCriteriaMapper, lastMapper)
	}

	private static String columnSieve(def selectionCriteria, Closure criteriaMapper = {it}, Closure positionMapper = {it}) {
		sieveTransform("firstColumn", "lastColumn", "column", selectionCriteria, criteriaMapper, positionMapper)
	}

	/**
	 * This higher-order function "parses" the term and applies various transforms depending on the type of reference element the term is.
	 * The criteria need to be transformed (mapped) in order to become an actual XPath selection criteria term (using the criteriaMapper).
	 * Indirect positional references (i.e. non-numbers) need to be transformed into something that will result in a number (using the positionMapper).
	 * (Numbers are already direct positional references so need no transformation.)
	 * Any transform that would be applied to <em>both</em> direct and indirect positional references would be applied to the <em>result</em> of this filtered transform.
	 * 
	 * @param firstLabel  a keyword (e.g. "first", "firstColumn")
	 * @param lastLabel  a keyword (e.g. "last", "lastColumn")
	 * @param numberLabel  a keyword (e.g. "row", "column")
	 * @param selectionCriteria  the term being matched for: either a keyword (firstLabel or lastLabel), a keyword\<number\>, or a real selection criteria text (e.g. column header name, or row contents value)
	 * @param criteriaMapper only applied to the actual criteria text
	 * @param positionMapper only applied to last and criteria terms since direct numbers are already positions!<br>
	 *                          (a transform applied to both direct and indirect terms would be applied to the result of this filtered transform).
	 * @return
	 */
	private static String sieveTransform(String firstLabel, String lastLabel, String numberLabel, def term, 
				Closure criteriaMapper = {it}, Closure positionMapper = {it}, Closure nonCriteriaMapper = {it}, Closure lastMapper = {it}) {
		if (term == firstLabel)
			return nonCriteriaMapper.call("1")
		else if (term == lastLabel)
			return nonCriteriaMapper.call( positionMapper.call( lastMapper.call( "position() = last()" ) ) )
		else if (term.toString().toLowerCase() ==~ /^($numberLabel)\d*$/) {
			def num = term.toString().substring(numberLabel.size())
			return nonCriteriaMapper.call( term.toString().substring(numberLabel.size()) )
		} else
			return positionMapper.call( criteriaMapper.call(term) )
	}

	
	/**
	* Returns an xpath expression to get the row number within the table, with the specific cellText.
	* Parameter must be a map of [columnHeaderText : cellText]
	*/
   protected static def getRowReferenceXPathMapped(String prefixXPath, Map columnHeaderTextCellTextMap, Closure columnPositionMapper) {
	   String rowXPath = "${prefixXPath}/tbody/tr"
	   columnHeaderTextCellTextMap.each { columnText, cellText ->
		   rowXPath += "/td[position() = (${columnPositionMapper.call(prefixXPath, columnText)}) and ${getCellXPathRef(cellText)}]/parent::*"
	   }
	   return rowXPath
   }

   
   /* =========================== some basic XPATH fragments =============================== */

	public static String quoteStringXPath(String str) {
		if ( ! str.contains("'"))
			return "'${str}'"
		else if ( ! str.contains('"'))
			return '"'+str+'"'
		else // contains both --> hard! - need something fancy!
			return concatQuoteStringXPath(str)
	}

   protected static def getRowXPath(String prefixXPath, String rowPositionXPath) {
	   return "${prefixXPath}/tbody/tr[${rowPositionXPath}]"
   }
   
   protected static def getColumnXPath(String prefixXPath, String columnPositionXPath) {
	   return "${prefixXPath}/thead/tr/th[${columnPositionXPath}]"
   }

   private static String getCellXPathRef(def cellText) {
	   String quotedString = quoteStringXPath(cellText)
	   return "(wt:cleanText(.//text()) = ${quotedString} or wt:cleanText(.//@value) = ${quotedString})"
   }

	private static String getCheckedXPathAccess(def cellAccess) {
		 return "boolean(${cellAccess})"
	}
 
   private static String getUncheckedPositionXPath(def positionXPath) {
		return "count(${positionXPath}/preceding-sibling::*)+1"
		/*
		 * method: count the number of rows or columns preceding (0 for first or non-existent element) the specified one. 
		 * Add 1 to give the index of the element again. However, if the element does not exist then the position is spurious.
		 */
   }

   private static String getCheckedPositionXPath(def positionXPath) {
		return "count(${positionXPath}/preceding-sibling::*)+number(boolean(${positionXPath}))"
		/*
		 * method: boolean() checks existence of the row, number() turns that boolean into a 1 or 0 -
		 * if 0 then the preceding siblings count of 0 will not be incremented, otherwise 1 will be added to give the true row index.
		 */
   }
   
   public static String getTableReferenceXPath(String tableHtmlId) {
	   String quotedString = quoteStringXPath(tableHtmlId)
	   return "//table[@id=${quotedString}]"
   }

	public static String getClickLinkOnCellXPath(String cellXPath) {
		return "${cellXPath}//a[1]"
	}
   
	public static String getSetRadioButtonOnCellXPath(String cellXPath, def value) {
		String quotedString = quoteStringXPath(value)
		return "${cellXPath}//*[wt:cleanText(text()) = ${quotedString}]//input[@type='radio']"
	}

	/* =========================== some XPATH selection suffices =============================== */
	
	public static String valueXPathSuffix() {
		return "//*[(local-name() = 'input' or local-name() = 'textarea') and position() = 1]"
	}
	
	public static String fieldXPathSuffix() {
		return '//select[1]'
	}
	
	public static String checkboxXPathSuffix() {
		return "//input[@type='checkbox']"
	}

	
	/* =========================== counting XPATH fragments =============================== */
	/*
	 * following should not need escapeSingleQuotes since the values are just numbers
	 */
	private static String getConstrainedRowCountXPath(String rowReferenceXPath, String operator, def value) {
		 return "count(${rowReferenceXPath})${operator}${value}"
	}

	private static String getColumnCountXPath(String prefixXPath, String operator, def value) {
		 return "${prefixXPath}/thead/tr[count(th)${operator}${value}]"
	}
	
	private static String getRowCountXPath(String prefixXPath, String operator, def value) {
		 return "${prefixXPath}/tbody[count(tr)${operator}${value}]"
	}
		
	/* ===== don't know whether these are needed: ---- (should be done by calls anyway) */
	
	protected static def getFirstRowPositionXPath() {
		return "1"
	}

	protected static def getLastRowPositionXPath(String prefixXPath) {
		return "count(${prefixXPath}/tbody/tr[position() = last()]/preceding-sibling::*)+1"
	}

	// http://kushalm.com/the-perils-of-xpath-expressions-specifically-escaping-quotes
	// string myXPathExpression = "books/book[@publisher = " + "concat('Single', "'", 'quote. Double', '"', 'quote.')]";
	//looks for a publisher called Single'quote. Double"quote
	/*
	 * following expects str to contain both single and double quotes
	 * 
	 * ref: http://www.w3.org/TR/xpath/#strings    look at the defn: [29] Literal
	 */
	static String concatQuoteStringXPath(String str) {
		// TODO: fill in this stub!
		LOG.debug "concatQuoteStringXPath($str)"
		return "'${str}'"
	}

}



























