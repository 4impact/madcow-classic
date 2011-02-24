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

package com.projectmadcow.engine

import com.projectmadcow.engine.properties.GlobalDataParametersHelper
import org.apache.log4j.Logger
import com.projectmadcow.engine.plugin.PluginParameters
import com.projectmadcow.engine.plugin.PluginResolver

/**
 * Page Model provides dynamic invokation of the plugins/antbuilder;
 * this is accomplished through the propertyMissing Groovy hooks.
 * Page Model is invoked from the AbstractMadcowTestCase.
 *
 * Static loading of the Global Data Parameters.
 */
public class PageModel {

    protected static final Logger LOG = Logger.getLogger(PageModel.class);

    AntBuilder antBuilder
    String property
    PluginParameters pluginParameters = new PluginParameters()

    static Map globalDataParameters

    static {
        def globalDataParametersHelper = new GlobalDataParametersHelper()
        globalDataParameters = globalDataParametersHelper.initProcessProperties()
    }

    def PageModel(String property, AntBuilder antBuilder) {
        this.property = property;
        this.antBuilder = antBuilder;

    }

    def propertyMissing(String antBuilderMethodName) {
        def plugin = PluginResolver.resolvePlugin(antBuilderMethodName, this.property);

        if (plugin) {
            plugin.invoke(antBuilder, pluginParameters.getPluginParameters(antBuilderMethodName, this.property));
        } else {
            LOG.debug "PageModel.propertyMissing($antBuilderMethodName), calling antBuilder directly"
            antBuilder."$antBuilderMethodName"(pluginParameters.getPluginParameters(antBuilderMethodName, this.property))
        }
    }

    def propertyMissing(String antBuilderMethodName, def value) {
        def plugin = PluginResolver.resolvePlugin(antBuilderMethodName, this.property);

        if (plugin) {
            plugin.invoke(antBuilder, pluginParameters.getPluginParameters(antBuilderMethodName, this.property, value));
        } else {
            LOG.debug "PageModel.propertyMissing($antBuilderMethodName, $value), calling antBuilder directly"
            antBuilder."$antBuilderMethodName"(pluginParameters.addKeyValuePairToPluginParameters(pluginParameters.getPluginParameters(antBuilderMethodName, this.property, value), value, "value"))
        }
    }
}