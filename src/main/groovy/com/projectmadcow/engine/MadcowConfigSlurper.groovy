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

import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.Resource
import org.apache.log4j.Logger

/**
 * This class has arisen because different types of Madcow properties files
 * are read in exactly the same fashion. Pass in the properties file type that
 * you want, and this class will determine the file name, read it, and slurp
 * the properties into a ConfigObject for use.
 * PLEASE NOTE: If any of the properties specified in the file also have a
 * value set in an environment variable, we'll take the value from the env
 * variable instead.
 */
class MadcowConfigSlurper extends ConfigSlurper {

    static final Logger LOG = Logger.getLogger(MadcowConfigSlurper.class)
	
	private static enum ScopeType {
		GLOBAL,
		LOCAL,
		PERSONAL
	}
	public static final ScopeType GLOBAL = ScopeType.GLOBAL
	public static final ScopeType LOCAL = ScopeType.LOCAL
	public static final ScopeType PERSONAL = ScopeType.PERSONAL
	
    private static enum PropertiesType {
        DATABASE,
        URL,
        CONFIG
    }

    public static final PropertiesType DATABASE = PropertiesType.DATABASE
    public static final PropertiesType URL = PropertiesType.URL
    public static final PropertiesType CONFIG = PropertiesType.CONFIG

    private String propertiesFileName
    private PropertiesType type
	private ScopeType scope
	
    
    public MadcowConfigSlurper(PropertiesType type) {
		this(type, ScopeType.GLOBAL)
    }
	
	public MadcowConfigSlurper(PropertiesType type, ScopeType scope) {
        this.type = type
        this.scope = scope
        this.propertiesFileName = determinePropertiesFileName(type, scope)
	}
	
	private static def scopePrefix(scope) {
		switch (scope) {
			case ScopeType.GLOBAL:
				return ""
			case ScopeType.LOCAL:
				return "local."
			case ScopeType.PERSONAL:
				return System.getProperty("user.name", 'user')+"."
		}
	}

    private static def determinePropertiesFileName(type, scope) {
		String fileNameSuffix = "${scopePrefix(scope)}madcow.${type.name().toLowerCase()}.properties"
        String propertyToCheckForFileName = "${fileNameSuffix}.file"
		
        String propertyFilePrefix = System.getProperty(propertyToCheckForFileName, '')
        return propertyFilePrefix != '' ? "${propertyFilePrefix}.${fileNameSuffix}" : fileNameSuffix
    }

    public ConfigObject parse() {
       def properties = loadConfigFile()
       overwriteWithEnvironmentVariables(properties)
       return super.parse(properties)
    }


    private Properties loadConfigFile(ClassLoader classLoader = this.getClass().getClassLoader()) {
        LOG.info "Attempting to load config file $propertiesFileName"
        PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver(classLoader)
        Resource[] resources = resourceLoader.getResources("classpath*:$propertiesFileName")

        if (resources.size() < 1) LOG.warn("No resources found for filename $propertiesFileName")

        Properties properties = new Properties()
        resources.each { resource ->
            LOG.info "Loading ${type.name().toLowerCase()} resource : $resource"
            properties.load(resource.inputStream)
        }

        return properties
    }

    private def overwriteWithEnvironmentVariables(Properties properties){
        properties.propertyNames().each { String propertyName ->

            if (System.getenv(propertyName)){
                properties.put propertyName, System.getenv(propertyName)
            }
        }
    }

}
