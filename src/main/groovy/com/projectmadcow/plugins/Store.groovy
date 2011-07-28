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

import org.apache.commons.lang.StringUtils

import com.projectmadcow.engine.plugin.Plugin

/**
 * Store plugin to allow dynamic storing of properties
 */
class Store extends Plugin {

    def invoke(AntBuilder antBuilder, Map pluginParameters) {

        if (!pluginParameters.value){
             antBuilder.storeXPath(pluginParameters)
             return
        }

         if (!StringUtils.isEmpty(pluginParameters.htmlId)) {
            pluginParameters.xpath = createXPathToStore("@id='${pluginParameters.htmlId}'")
        } else if (!StringUtils.isEmpty(pluginParameters.name)) {
            pluginParameters.xpath = createXPathToStore("@name='${pluginParameters.name}'")
         }

        antBuilder.storeXPath(description: pluginParameters.description,
                              xpath: pluginParameters.xpath,
                              property: pluginParameters.value)
   	}

    String createXPathToStore(String xPathToIdentifyNode) {
        return "//*[${xPathToIdentifyNode} and @value and not(text()) and not(self::select)]/@value " +
                "| //*[${xPathToIdentifyNode} and text()  and not(self::select)]/text() " +
                "| //select[${xPathToIdentifyNode}]/option[@selected]/text()"

    }
}
