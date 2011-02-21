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

package au.com.ts4impact.madcow.engine

import org.apache.log4j.Logger
import au.com.ts4impact.madcow.engine.plugin.PluginParameters
import au.com.ts4impact.madcow.engine.plugin.PluginResolver

/**
 * Runtime Context to hold the current list of the data parameters and
 * the running AntBuilder Context.
 *
 * @author gbunney
 */
public class RuntimeContext {

    protected static final Logger LOG = Logger.getLogger(RuntimeContext.class);

    Map<String, Object> dataParameters = new HashMap<String, Object>()

	AntBuilder antBuilder;
    PluginParameters pluginParameters = new PluginParameters()

	RuntimeContext(AntBuilder antBuilder = null) {
		this.antBuilder = antBuilder ?: AntBuilderFactory.createAntBuilder()
        
        dataParameters.putAll(PageModel.globalDataParameters.clone() as Map)
        LOG.debug "dataParameters after adding global : $dataParameters"
	}

	def propertyMissing(String name) {
		return new PageModel(name, antBuilder);
	}

	def propertyMissing(String name, Object value) {
		def plugin = PluginResolver.resolvePlugin(name, name)

        def attributeMap = pluginParameters.getPluginParameters(name, null, value)

		if (plugin){
			plugin.invoke(antBuilder, attributeMap);
		} else {
            LOG.debug "RuntimeContext.propertyMissing($name, $value), calling antBuilder directly"
            antBuilder."${name}"(attributeMap)
		}
	}
}