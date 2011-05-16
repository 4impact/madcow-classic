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

package com.projectmadcow.util

/*
 * A home for re-usable slivers of XPath that we don't want lying around the application
 */
class XPathHelper {

    /**
     * Returns an xpath expression to get a columns position within the table, with the specific column heading text
     */
    static def getColumnPositionXPath(String prefixXPath, def columnHeaderText) {
        if (columnHeaderText == "firstColumn")
            return "1"
        else if (columnHeaderText == "lastColumn")
            return "count(${prefixXPath}/thead/tr/th[position() = last()]/preceding-sibling::*)+1"
        else if (columnHeaderText.toString().toLowerCase() ==~ /column\d*/)
             return columnHeaderText.toString().substring(6)
        else
            return "count(${prefixXPath}/thead/tr/th[.//text() = '${columnHeaderText}' or .//@value = '${columnHeaderText}']/preceding-sibling::*)+1"
    }
}
