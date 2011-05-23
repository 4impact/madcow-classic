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

class TableCountRowsWithCriteria extends AbstractCount {

    private def columnHeadersAndCellValuesMap

    def TableCountRowsWithCriteria(prefixXPath, antBuilder, descriptionPrefix, columnHeadersAndCellValuesMap) {
        super(prefixXPath, antBuilder, descriptionPrefix)
        this.columnHeadersAndCellValuesMap = columnHeadersAndCellValuesMap
    }

    def doCount(operator, value, description) {
        String xpath = buildRowCountXPath(operator, value)
        antBuilder.verifyXPath(xpath: xpath, description: description)
    }

    protected String buildRowCountXPath(operator, value) {
        def xpath = "count(${prefixXPath}/tbody/tr"
        columnHeadersAndCellValuesMap.each { headerText, cellText ->
            Column column = new Column(prefixXPath, headerText)
            xpath += "/td[position() = (${column.getColumnPositionXPath()}) and (wt:cleanText(.//text()) = '${cellText}' or .//@value = '${cellText}')]/parent::*"
        }
        xpath += ")${operator}${value}"
        return xpath
    }
}
