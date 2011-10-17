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

import com.projectmadcow.engine.plugin.Plugin
import com.projectmadcow.engine.plugin.PluginResolver
import com.projectmadcow.plugins.table.TableCountRows
import com.projectmadcow.plugins.table.TableCountRowsWithCriteria
import com.projectmadcow.plugins.table.TableHeaderColumnCount
import com.projectmadcow.plugins.table.TableXPath;

import org.apache.log4j.Logger

/**
 * Ideally modify this class to dynamically invoke any madcow plugin or webtest step
 * using the syntax myTable.table.currentRow.blah where blah is dynamically resolved and
 * invoked instead of this class having to implement a setBlah or getBlah method.
 * Biggest issue currently with this is that we do not know exactly what the html
 * element is that we wish to select and then run the plugin/step against.
 * For example currently for clickLink step we append '//a[1]' to the xpath and for value plugin
 * we append '//input[1] and for the checkValue plugin we do not need to append to the xpath.
 * Without statically defining what each plugin/step requires we gain very little by
 * using dynamic discovery as we still need to update a config file in order to wire
 * in a step/plugin which is pretty much the same as writing one line of wiring code.
 */
public class Table extends Plugin {

    static Logger LOG = Logger.getLogger(Table.class)

    Map attributes
    AntBuilder antBuilder
	TableXPath txp = getTableXPath()

	def getTableXPath() {
		return new TableXPath()
	}
	
    public invoke(AntBuilder antBuilder, Map pluginParameters) {
        this.antBuilder = antBuilder;
        this.attributes = pluginParameters
        return this
    }
	
	protected String getPrefixXPath() {
		if (attributes.htmlId) {
			txp.getTableReferenceXPath(attributes.htmlId)
		} else {
			attributes.xpath
		}
	}

	protected String getPluginName() {
		return "table"
	}
	
	/**
	 * 
	 * @return ant-cleansed property name in format:   madcow.pluginName.callingProperty
	 */
	def getPropertyName() {
		// ant/groovy does not like ":" in the property names!
		String cleansedPropertyName = this.callingProperty.replace(":", ".")
		"madcow.${this.pluginName}.${cleansedPropertyName}"
	}

    /**
     * selectionCriteria is expected to be a map of <column header text> : <cell text>
     * which will uniquely identify a particular row
     */
    def setSelectRow(def selectionCriteria) {
        if (LOG.isDebugEnabled()) LOG.debug "setSelectRow($selectionCriteria)"
		String prefixXPath = getPrefixXPath()
        String rowXPositionPath = txp.getRowPositionXPath(prefixXPath, selectionCriteria)
		String rowXVerifyPath = txp.getRowXPath(prefixXPath, txp.getRowPositionCheckedXPath(prefixXPath, selectionCriteria))

        antBuilder.plugin(description: getDescription('selectRow', selectionCriteria, false)) {
			// first check that accessing the row would work - fail fast
			if (LOG.isDebugEnabled()) LOG.debug "setSelectRow($selectionCriteria)  verifyXPath    getPropertyName(): ${getPropertyName()}   xpath: ${rowXVerifyPath}"
			verifyXPath(xpath: rowXVerifyPath, description: "Verify row exists.")
			
            storeXPath(property : getPropertyName(), xpath : rowXPositionPath, propertyType : 'dynamic')
			
			// if (LOG.isDebugEnabled()) LOG.debug("setSelectRow($selectionCriteria)  AFTER  storeXPath    antBuilder.project.properties.getProperty(${getPropertyName()}): ${antBuilder.project.getProperty(getPropertyName())}     antBuilder.project.properties: ${antBuilder.project.properties}")
        }
    }


    def getCurrentRow() {
        return this
    }

    def setClickLink(def column){
        antBuilder.plugin(description: getDescription('clickLink', column)) {
            antBuilder.verifyDynamicProperty (name: getPropertyName())
			String xPath = getCellXPath("#{${getPropertyName()}}", column)
			if (LOG.isDebugEnabled()) LOG.debug("setClickLink()  getPropertyName() = ${getPropertyName()}   xPath=${xPath}")
			antBuilder.verifyXPath(xpath: xPath, description: "Verify row exists: ${"#{${getPropertyName()}}"}")
            antBuilder.clickLink(xpath: txp.getClickLinkOnCellXPath(xPath) )
        }
    }

    def getClickRow() {
		String prefixXPath = getPrefixXPath()
        antBuilder.plugin(description: getDescription('clickRow')) {
            antBuilder.verifyDynamicProperty (name: getPropertyName())
            antBuilder.clickElement(xpath: txp.getRowXPath(prefixXPath,"#{${getPropertyName()}}"))
        }
    }

    def setCheckValue(def valueMap){
        invokePlugin('checkValue', valueMap, txp.checkValueXPathSuffix())
    }

    def setCheckValueContains(def valueMap){
        invokePlugin('checkValueContains', valueMap, txp.checkValueXPathSuffix())
    }
	
	def setCheckValueEmpty(String column){
		invokePlugin('checkValueEmpty', column)
	}

    def setValue(def valueMap){
        invokePlugin('value', valueMap, txp.setValueXPathSuffix())
    }

    def setSelectField(def valueMap){
        invokePlugin('selectField', valueMap, txp.fieldXPathSuffix())
    }

    def setSelectCheckbox(String column){
        invokePlugin('selectCheckbox', column, txp.checkboxXPathSuffix())
    }

    def setUnselectCheckbox(String column){
        invokePlugin('unselectCheckbox', column, txp.checkboxXPathSuffix())
    }

    def setVerifySelectFieldOptions(def valueMap){
        invokePlugin('verifySelectFieldOptions', valueMap, txp.fieldXPathSuffix())
    }

    def setVerifySelectFieldContains(def valueMap){
        invokePlugin('verifySelectFieldContains', valueMap, txp.fieldXPathSuffix())
    }

    def setSetRadioButton(def valueMap) {
        antBuilder.plugin(description: getDescription('setRadioButton', valueMap)) {
            antBuilder.verifyDynamicProperty (name: getPropertyName())
            valueMap.each { column, value ->
				String xPath = getCellXPath("#{${getPropertyName()}}", column)
                def attributes = [xpath : txp.getSetRadioButtonOnCellXPath(xPath, value) ]
                antBuilder.setRadioButton(attributes)
            }
        }
    }

    def setWaitForText(def valueMap){
        invokePlugin('waitForText', valueMap)
    }

    def invokePlugin(def pluginName, String column, def cellXPathSuffix = ''){
        def plugin = PluginResolver.resolvePlugin(pluginName, this.callingProperty);

        def attributes = [xpath : getCellXPath("#{${getPropertyName()}}" as String, column) + cellXPathSuffix]
		
        antBuilder.plugin(description: getDescription(pluginName, column)) {
            antBuilder.verifyDynamicProperty (name: getPropertyName())
			plugin.invoke(antBuilder, attributes)
        }

    }

    def invokePlugin(def pluginName, def valueMap, def cellXPathSuffix = ''){
        def plugin = PluginResolver.resolvePlugin(pluginName, this.callingProperty);

        // iterate over each key in the map using the key as the column name and value as the value to "apply" in the column
        antBuilder.plugin(description: getDescription(pluginName, valueMap)) {
			def propName = getPropertyName()

            antBuilder.verifyDynamicProperty (name: getPropertyName())
            valueMap.each { column, value ->
				String xPath = getCellXPath("#{${getPropertyName()}}" as String, column) + cellXPathSuffix
				if (LOG.isDebugEnabled()) LOG.debug("invokePlugin(${pluginName})  getPropertyName() = ${propName}   xPath=${xPath}")
                def attributes = [xpath : xPath, value : value]
                plugin.invoke(antBuilder, attributes)
            }
        }
    }
	
	/* ===================== Counting functions: =============================== */
	
    def getCountRows() {
        return new TableCountRows(txp, getPrefixXPath(), antBuilder, getDescription('countRows', null, false))
    }

    def countRows(def parameters) {
        def description = getDescription("countRows${parameters}", null, false)
        return new TableCountRowsWithCriteria(txp, getPrefixXPath(), antBuilder, description, parameters)
    }

    def getHeaderColumnCount() {
        return new TableHeaderColumnCount(txp, getPrefixXPath(), antBuilder, getDescription('headerColumnCount', null, false))
    }
	
	protected String getDescription(String functionName, def value = null, boolean currentRow = true) {
		return "${this.callingProperty}.${getPluginName()}${currentRow ? '.currentRow' : ''}.$functionName${value != null ? '=' + value : ''}"
	}

	/* ===== functions that delegate to txp (TableXPath): (could disappear and just go straight to txp, since that will be extended in the plugins) ====== */
	
	protected def getColumnPositionXPath(def columnHeaderText) {
		return txp.getColumnPositionXPath(getPrefixXPath(), columnHeaderText)
	}
	
	protected String getColumnPositionCheckedXPath(def columnHeaderText) {
		return txp.getColumnPositionCheckedXPath(getPrefixXPath(), columnHeaderText)
	}
	
	protected String getRowPositionXPath(def rowReference) {
		return txp.getRowPositionXPath(getPrefixXPath(), rowReference)
	}
	
	protected String getRowPositionCheckedXPath(def rowReference) {
		return txp.getRowPositionCheckedXPath(getPrefixXPath(), rowReference)
	}
	
	/**
	 * Returns an xpath expression for a particular cell on a particular row
	 */
	protected def getCellXPath(def rowPositionXPath, def columnHeaderText) {
		return txp.getCellXPath(getPrefixXPath(), rowPositionXPath, columnHeaderText)
	}

	protected def getCellXPath(Map rowPositionMap, def columnHeaderText) {
		return txp.getCellXPath(getPrefixXPath(), rowPositionMap, columnHeaderText)
	}

}
