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

import org.apache.log4j.Logger

/**
 * Namespace plugin base class to define plugin namespaces to search.
 *
 * When custom plugins are created outside of the default
 * au.com.ts4impact.madcow.plugin package, it must subclass this,
 * and define the namespaceRegex of the package to search within.
 *
 * @author gbunney
 */
class NamespacePlugin extends Plugin {

    static final Logger LOG = Logger.getLogger(NamespacePlugin.class)

    Map attributes
    AntBuilder antBuilder
    PluginParameters pluginParameters = new PluginParameters()
    String namespaceRegex = ""

    public NamespacePlugin() {

    }

    public invoke(AntBuilder antBuilder, Map attributes) {
        this.antBuilder = antBuilder;
        this.attributes = attributes
        return this
    }

    def propertyMissing(String antBuilderMethodName) {
        def plugin = PluginResolver.resolvePlugin(antBuilderMethodName, this.callingProperty, this.namespaceRegex);

        if (plugin) {
            plugin.invoke(antBuilder, pluginParameters.getPluginParameters(antBuilderMethodName, this.callingProperty));
        } else {
            LOG.debug "Plugin not found, calling ant directly for $antBuilderMethodName"
            antBuilder."$antBuilderMethodName"(pluginParameters.getPluginParameters(antBuilderMethodName, this.callingProperty))
        }
    }

    def propertyMissing(String antBuilderMethodName, Object value) {
        def plugin = PluginResolver.resolvePlugin(antBuilderMethodName, this.callingProperty, this.namespaceRegex);

        if (plugin) {
            plugin.invoke(antBuilder, pluginParameters.getPluginParameters(antBuilderMethodName, this.callingProperty, value));
        } else {
            LOG.debug "Plugin not found, calling ant directly for $antBuilderMethodName"
            antBuilder."$antBuilderMethodName"(pluginParameters.addKeyValuePairToPluginParameters(pluginParameters.getPluginParameters(antBuilderMethodName, this.callingProperty), value, "value"))
        }
    }

}
