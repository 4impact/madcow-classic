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

class CheckValueEmpty extends Plugin {

    def invoke(AntBuilder antBuilder, Map pluginParameters) {
        LOG.debug ("-- CHECK VALUE EMPTY ------------- pluginParameters : $pluginParameters")

        pluginParameters.text = '^\\s*$'
        pluginParameters.regex = true

        //pluginParameters.xpath = "//*[@name='${pluginParameters.name}']//text()"
        if (pluginParameters.htmlId != null) {
			antBuilder.verifyElementText(pluginParameters)
        } else if (pluginParameters.name != null) {
            pluginParameters.xpath = modifyXPathToGetTextAndValue("//*[@name='${pluginParameters.name}']")
            pluginParameters.remove('name')
			antBuilder.verifyXPath(pluginParameters)
		} else if (pluginParameters.xpath != null) {
            pluginParameters.xpath = modifyXPathToGetTextAndValue(pluginParameters.xpath)
			antBuilder.verifyXPath(pluginParameters)
		} else {
            throw new RuntimeException("CheckValueEmpty plugin requires one of the following attributes: htmlId; name; xpath.")
        }

        return null
    }

    private String modifyXPathToGetTextAndValue(String originalXPath) {
        return "$originalXPath//text() | $originalXPath//@value"
    }

}
