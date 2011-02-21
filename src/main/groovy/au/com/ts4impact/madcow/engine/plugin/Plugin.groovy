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
 * Base class for all plugins.
 *
 * @author gbunney
 */
public abstract class Plugin {

    protected static final Logger LOG = Logger.getLogger(Plugin.class)

    /**
     * This is the name of the base property which called the plugin;
     * e.g. someElement.clickLink, callingProperty would be 'someElement'
     */
    public String callingProperty

    PluginParameters pluginParametersHelper = new PluginParameters()

    abstract invoke(AntBuilder antBuilder, Map pluginParameters)

    /**
     * Remap the specified parameter to a new parameter.
     * The old parameter will be removed from the pluginParameters map, with the new one
     * added.
     */
    void remapPluginParameter(Map pluginParameters, String currentParameterName, String newParameterName) {

        def value = pluginParameters."$currentParameterName"
        pluginParameters.remove currentParameterName

        pluginParametersHelper.addKeyValuePairToPluginParameters(pluginParameters, value, newParameterName)
    }

}
