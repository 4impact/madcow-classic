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

/**
 * This plugin checks that the specified element has the supplied value
 */
public class CheckValue extends Plugin {

    def invoke(AntBuilder antBuilder, Map pluginParameters) {
        LOG.debug("-- CHECK VALUE ------------- pluginParameters : $pluginParameters")
        if (pluginParameters.htmlId != null) {
            checkValueByHtmlId(pluginParameters, antBuilder)
        } else if (pluginParameters.xpath != null) {
            checkValueByXPath(pluginParameters, antBuilder)
        } else if (pluginParameters.name != null) {
            checkValueByName(pluginParameters, antBuilder)
        } else if (pluginParameters.forLabel != null) {
            checkValueByLabel(pluginParameters, antBuilder)
        } else {
            LOG.error("invoke() - UNKNOWN pluginParameter: " + pluginParameters)
            assert false: "CheckValue - UNKNOWN pluginParameter: " + pluginParameters
        }
    }

    protected def checkValueByLabel(Map pluginParameters, AntBuilder antBuilder) {
        pluginParameters.xpath = """//select[@label='${pluginParameters.forLabel}']/option[@selected]/text() | //*[@label='${pluginParameters.forLabel}']/@value"""
        pluginParameters.remove 'forLabel'
        remapPluginParameter pluginParameters, 'value', 'text'
        antBuilder.verifyXPath(pluginParameters)
    }

    private void checkValueByName(Map pluginParameters, AntBuilder antBuilder) {
        if (pluginParameters.type == "radio") {
            pluginParameters.remove 'type'
            antBuilder.verifyRadioButton(pluginParameters)
        } else if (pluginParameters.type == "select") {
            pluginParameters.remove 'type'
            antBuilder.verifySelectField(pluginParameters)
        } else if (pluginParameters.type == "input") {
            remapPluginParameter pluginParameters, 'value', 'text'
            antBuilder.verifyElementText(pluginParameters)
        } else if (pluginParameters.type == null) {
            remapPluginParameter pluginParameters, 'value', 'text'
            pluginParameters.xpath = """//select[@name='${pluginParameters.name}']/option[@selected]/text() | //*[@name='${pluginParameters.name}']/@value"""
            pluginParameters.remove 'name'
            antBuilder.verifyXPath(pluginParameters)
        } else {
            LOG.error("invoke() name reference - UNIMPLEMENTED type pluginParameter: " + pluginParameters)
            assert false: "CheckValue name reference - UNIMPLEMENTED type pluginParameter: " + pluginParameters
        }
    }

    private void checkValueByXPath(Map pluginParameters, AntBuilder antBuilder) {
        //If the mapping doesn't end with /@value or /text(), we'll need to add it so Webtest can find the value
        if (!(pluginParameters.xpath =~ /.*\/@value/ || pluginParameters.xpath =~ /.*\/text()/)) {
            pluginParameters.xpath = "$pluginParameters.xpath/text() | $pluginParameters.xpath/@value"
        }
        remapPluginParameter pluginParameters, 'value', 'text'
        antBuilder.verifyXPath(pluginParameters)
    }

    private void checkValueByHtmlId(Map pluginParameters, AntBuilder antBuilder) {
        remapPluginParameter pluginParameters, 'value', 'text'
        antBuilder.verifyElementText(pluginParameters)
    }
}
