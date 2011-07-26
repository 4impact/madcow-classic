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
 * This plugin checks the attributes of an element.
 */
public class CheckAttribute extends Plugin {

    def invoke(AntBuilder antBuilder, Map pluginParameters) {

        // save the element details and remove the parameters from the map
        String htmlid = pluginParameters.htmlId
        String name = pluginParameters.name
        String xpath = pluginParameters.xpath
        String description = pluginParameters.description

        Map attributes = pluginParameters.value as Map

        if (htmlid != null)
            attributes.each{ k, v -> antBuilder.checkAttribute(htmlId : htmlid, attributeName : k, attributeValue : v, description : description) }
        else if (xpath != null)
            attributes.each{ k, v -> antBuilder.checkAttribute(xpath : xpath, attributeName : k, attributeValue : v, description : description) }
        else if (name != null)
            attributes.each{ k, v -> antBuilder.checkAttribute(name : name, attributeName : k, attributeValue : v, description : description) }
    }
}
