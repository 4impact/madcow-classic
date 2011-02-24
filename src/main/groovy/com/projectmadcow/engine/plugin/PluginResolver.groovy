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

package com.projectmadcow.engine.plugin

import org.apache.commons.io.IOUtils
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import org.codehaus.groovy.runtime.typehandling.GroovyCastException

/**
 * Statically loads all plugin package names defined within 'madcow.plugins' files.
 * resolvePlugin method is then used to call a specific plugin method.
 *
 * @author mcallon
 */
public class PluginResolver {

    static final Logger LOG = Logger.getLogger(PluginResolver.class)
    static final BASE_PLUGINS = "com.projectmadcow.plugins"
	static Collection pluginPackages
	
	static {
		pluginPackages = initPluginPackages();       
	}
	
	static Collection initPluginPackages() {
		GroovyClassLoader loader = new GroovyClassLoader(PluginResolver.getClassLoader())
		Enumeration pluginFiles = loader.getResources("madcow.plugins")
		List packages = []
		pluginFiles.each {URL url -> packages.addAll(IOUtils.readLines(url.openStream())) }
        LOG.info("Plugin Packages: $packages")
		return packages
	}

    /**
     * Resolve the specified method within the plugin packages.
     * packageFilter can also be supplied, as a java regex, to filter out
     * non-matching packages.
     */
	static Plugin resolvePlugin(String methodName, String invokingPropertyName, String packageFilter = '') {
		LOG.debug("resolvePlugin(methodName : $methodName, invokingPropertyName : $invokingPropertyName, packageFilter : $packageFilter")
        
        String className = StringUtils.capitalize(methodName)
		for (packageName in pluginPackages) {
            if ((packageFilter != '') && (!(packageName as String).matches(packageFilter)))
                continue

            try {
                def pluginClass = Class.forName("$packageName.$className").newInstance()

                // if not filtering, only allow NamespacePlugins outside of the base plugin set
                if (   ((packageFilter ?: '') == '')
                    && (packageName != BASE_PLUGINS)
                    && (!NamespacePlugin.class.isAssignableFrom(Class.forName("$packageName.$className")))) {
                   continue
                }

                (pluginClass as Plugin).callingProperty = invokingPropertyName
                return pluginClass as Plugin

			} catch (ClassNotFoundException ignore) {
			} catch (GroovyCastException gce) {
                LOG.error("$packageName.$className doesn't extend com.projectmadcow.engine.plugin.Plugin")
                throw gce
            }
		}

		return null;
	}
}
