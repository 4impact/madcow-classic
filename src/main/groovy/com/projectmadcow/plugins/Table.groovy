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
import com.projectmadcow.plugins.table.Column
import com.projectmadcow.plugins.table.TableCountRows
import com.projectmadcow.plugins.table.TableCountRowsWithCriteria
import com.projectmadcow.plugins.table.TableHeaderColumnCount
import org.apache.log4j.Logger

/**
 * Ideally modify this class to dynamically invoke any madcow plugin or webtest step
 * using the syntax myTable.table.currentRow.blah where blah is dynamically resolved and
 * invoked instead of this class having to implement a setBlah or getBlah method.
 * Biggest issue currently with this is that we do not know excatly what the html
 * element is that we wish to select and then run the plugin/step against.
 * For example currently for clickLink step we append '//a[1]' to the xpath and for value plugin
 * we append '//input[1] and for the checkValue plugin we do not need to append to the xpath.
 * Without statically defining what each plugin/step requires we gain very little by
 * using dynamic discovery as we still need to update a config file in order to wire
 * in a step/plugin which is pretty much the same as writing one line of wiring code.
 */
public class Table extends Plugin {

    static final Logger LOG = Logger.getLogger(Table.class)

    Map attributes
    AntBuilder antBuilder

    public invoke(AntBuilder antBuilder, Map pluginParameters) {
        this.antBuilder = antBuilder;
        this.attributes = pluginParameters
        return this
    }

    protected def getColumnPositionXPath(def columnHeaderText) {
        String xpath = new Column(prefixXPath, columnHeaderText).getColumnPositionXPath()
        LOG.debug("getColumnPositionXPath(${columnHeaderText} = ${xpath}")
        return xpath
    }

    /**
     * Returns an xpath expression to get the row number within the table, with the specific cellText.
     * Parameter must be a map of [columnHeaderText : cellText]
     */
    protected def getRowPositionXPath(Map columnHeaderTextCellTextMap) {
        String rowXPath = "count(${getPrefixXPath()}/tbody/tr"

        columnHeaderTextCellTextMap.each { columnText, cellText ->
            rowXPath += "/td[position() = (${getColumnPositionXPath(columnText)}) and (.//text() = '${cellText}' or .//@value = '${cellText}')]/parent::*"
        }
        rowXPath += "/preceding-sibling::*)+1"

        return rowXPath
    }

    protected def getRowXPath(def rowPositionXPath) {
        return "${getPrefixXPath()}/tbody/tr[${rowPositionXPath}]"
    }

    /**
     * Returns an xpath expression for a particular cell on a particular row
     */
    protected def getCellXPath(def rowPositionXPath, def columnHeaderText) {
        def xpath = "${getRowXPath(rowPositionXPath)}/td[${getColumnPositionXPath(columnHeaderText)}]"
        LOG.debug("getCellXPath(${rowPositionXPath}) = ${xpath}")
        return xpath
    }

    protected def getCellXPath(Map rowPositionMap, def columnHeaderText) {
        return getCellXPath(getRowPositionXPath(rowPositionMap), columnHeaderText)
    }

    protected def getFirstRowPositionXPath() {
        return "1"
    }

    protected def getLastRowPositionXPath() {
        return "count(${getPrefixXPath()}/tbody/tr[position() = last()]/preceding-sibling::*)+1"
    }

    /**
     * selectionCriteria is expected to be a map of <column header text> : <cell text>
     * which will uniquely identify a particular row
     */
    def setSelectRow(def selectionCriteria) {
        LOG.debug "setSelectRow($selectionCriteria)"

        String rowXPositionPath = ""
        if (selectionCriteria == "first")
            rowXPositionPath = getFirstRowPositionXPath()
        else if (selectionCriteria == "last")
            rowXPositionPath = getLastRowPositionXPath()
        else if (selectionCriteria.toString().toLowerCase() ==~ /row\d*/)
             rowXPositionPath = selectionCriteria.toString().substring(3)
        else
            rowXPositionPath = getRowPositionXPath(selectionCriteria)

        antBuilder.plugin(description: getDescription('selectRow', selectionCriteria, false)) {
            verifyXPath(xpath: rowXPositionPath)
            storeXPath(property : getPropertyName(), xpath : rowXPositionPath, propertyType : 'dynamic')
        }
    }

    def getPropertyName() {
        "madcow.table.${this.callingProperty}"
    }

    def getCurrentRow() {
        return this
    }

    def setClickLink(def column){
        antBuilder.plugin(description: getDescription('clickLink', column)) {
            antBuilder.verifyDynamicProperty (name: getPropertyName())
            antBuilder.clickLink(xpath: getCellXPath("#{${getPropertyName()}}", column) + "//a[1]")
        }
    }

    def getClickRow() {
        antBuilder.plugin(description: getDescription('clickRow')) {
            antBuilder.verifyDynamicProperty (name: getPropertyName())
            antBuilder.clickElement(xpath: getRowXPath("#{${getPropertyName()}}"))
        }
    }

    def setCheckValue(def valueMap){
        invokePlugin('checkValue', valueMap)
    }

    def setValue(def valueMap){
        invokePlugin('value', valueMap, '//input[1]')
    }

    def setSelectField(def valueMap){
        invokePlugin('selectField', valueMap, '//select[1]')
    }

    def setSelectCheckbox(String column){
        invokePlugin('selectCheckbox', column, "//input[@type='checkbox']")
    }

    def setUnselectCheckbox(String column){
        invokePlugin('unselectCheckbox', column, "//input[@type='checkbox']")
    }

    def setCheckValueEmpty(String column){
        invokePlugin('checkValueEmpty', column)
    }

    def setVerifySelectFieldOptions(def valueMap){
        invokePlugin('verifySelectFieldOptions', valueMap, '//select[1]')
    }

    def setSetRadioButton(def valueMap) {
        antBuilder.plugin(description: getDescription('setRadioButton', valueMap)) {
            antBuilder.verifyDynamicProperty (name: getPropertyName())
            valueMap.each { column, value ->
                def attributes = [xpath : getCellXPath("#{${getPropertyName()}}", column) + "//*[text() = '$value']//input[@type='radio']"]
                antBuilder.setRadioButton(attributes)
            }
        }
    }

    def invokePlugin(def pluginName, String column, def cellXPathSuffix = ''){
        def plugin = PluginResolver.resolvePlugin(pluginName, this.callingProperty);

        def attributes = [xpath : getCellXPath("#{${getPropertyName()}}", column) + cellXPathSuffix]

        antBuilder.plugin(description: getDescription(pluginName, column)) {
            antBuilder.verifyDynamicProperty (name: getPropertyName())
            plugin.invoke(antBuilder, attributes)
        }

    }

    def invokePlugin(def pluginName, def valueMap, def cellXPathSuffix = ''){
        def plugin = PluginResolver.resolvePlugin(pluginName, this.callingProperty);

        // iterate over each key in the map using the key as the column name and value as the value to "apply" in the column
        antBuilder.plugin(description: getDescription(pluginName, valueMap)) {
            antBuilder.verifyDynamicProperty (name: getPropertyName())
            valueMap.each { column, value ->
                def attributes = [xpath : getCellXPath("#{${getPropertyName()}}", column) + cellXPathSuffix, value : value]
                plugin.invoke(antBuilder, attributes)
            }
        }
    }

    def getCountRows() {
        return new TableCountRows(getPrefixXPath(), antBuilder, getDescription('countRows', null, false))
    }

    def countRows(def parameters) {
        def description = getDescription("countRows${parameters}", null, false)
        return new TableCountRowsWithCriteria(getPrefixXPath(), antBuilder, description, parameters)
    }

    def

    protected String getPrefixXPath() {
        if (attributes.htmlId) {
            "//table[@id='${attributes.htmlId}']"
        } else {
            attributes.xpath
        }
    }

    def getHeaderColumnCount() {
        return new TableHeaderColumnCount(getPrefixXPath(), antBuilder, getDescription('headerColumnCount', null, false))
    }

    protected String getPluginName() {
        return "table"
    }

    protected String getDescription(String functionName, def value = null, boolean currentRow = true) {
        return "${this.callingProperty}.${getPluginName()}${currentRow ? '.currentRow' : ''}.$functionName${value != null ? '=' + value : ''}"
    }
}
