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

package au.com.ts4impact.madcow.engine.plugin

import au.com.ts4impact.madcow.engine.mappings.MadcowMappings

/**
 * Helper class to manage the plugin parameters.
 *
 * @author mcallon
 */
class PluginParameters {

    Map<String, String> getPluginParameters(String antBuilderMethodName, String htmlElementReference, def value = null) {

        Map<String, String> pluginParameters = [:]
        String descValuePostfix = value != null ? "=$value" : ""

        // element level plugins
        if (htmlElementReference != null) {
            pluginParameters = MadcowMappings.getMappedHtmlElementReference(htmlElementReference)

            // add the value to the pluginParameters
            if (value != null) {
                pluginParameters.value = value
            }

            // set the description
            pluginParameters.description = "$htmlElementReference.$antBuilderMethodName$descValuePostfix"
            return pluginParameters
        }

        // implicit else - page level plugin
        pluginParameters = addKeyValuePairToPluginParameters(pluginParameters, value, "value")
        pluginParameters.description = "$antBuilderMethodName$descValuePostfix"

        return pluginParameters
    }

    Map addKeyValuePairToPluginParameters(Map pluginParameters, def value, String key) {

        if (value instanceof List) {

            // since groovy toString on List removes the quotes, we need them still
            // to be there so toString it ourselves
            String quotedList = '';
            value.each { val -> quotedList += "'$val'," }

            pluginParameters."$key" = "[$quotedList]"
            return pluginParameters
        }

        if (value instanceof Map) {
            pluginParameters.putAll(value)
            return pluginParameters
        }

        pluginParameters."$key" = value
        return pluginParameters
    }
}