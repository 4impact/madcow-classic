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
import org.apache.commons.lang.StringUtils

/**
 * ClickLink
 *
 * @author gbunney
 */
public class ClickLink extends Plugin {

    def invoke(AntBuilder antBuilder, Map pluginParameters) {

        if (pluginParameters.name != null) {
            pluginParameters.xpath = "//*[@name='${pluginParameters.name}']"
            pluginParameters.remove('name')
        }

        if (!(pluginParameters.value instanceof String) || StringUtils.isEmpty(pluginParameters.value)) {
            antBuilder.clickLink(pluginParameters)
            return
        }

        remapPluginParameter pluginParameters, 'value', 'label'
        antBuilder.clickLink(pluginParameters)
    }
}
