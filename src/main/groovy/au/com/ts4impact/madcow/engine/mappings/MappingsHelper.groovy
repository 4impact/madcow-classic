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

package au.com.ts4impact.madcow.engine.mappings

import org.apache.log4j.Logger
import org.springframework.core.io.Resource
import au.com.ts4impact.madcow.engine.properties.AbstractMadcowPropertiesHelper

/**
 * Helper class for Mappings file reading/mapping;
 *
 * Locates the Mappings files and applies the relevant namespace prefix.
 */
class MappingsHelper extends AbstractMadcowPropertiesHelper {

	protected static final Logger LOG = Logger.getLogger(MappingsHelper.class);

	/**
	 * Root namespace package to search for mappings files.
	 */
	public static final String ROOT_MAPPINGS_NAMESPACE = 'mappings';
    
    Logger getLog(){
        LOG
    }
    
    String getPropertiesFilePrettyName(){
        "Mappings"
    }
    
    String getResourcePatternMatchingClasspath(){
        "classpath*:${ROOT_MAPPINGS_NAMESPACE}/**/*.madcow.mappings.properties"
    }    


	/**
	 * Apply the mapping namespace to the list of properties.
	 * The creates the prefixed folder_package_structure_* one the property keys.
	 *
	 * @param resource File resource properties loaded from
	 * @param baseProperties Collection of properties from the resource file
	 */
	def applyMappingNamespace(Resource resource, Properties properties) {
		def fileURLSplit = resource.URL.path.split('/')
		def mappingNamespace = "";
		for (def i = fileURLSplit.length - 1; i > -1; --i) {
			if (fileURLSplit[i] == ROOT_MAPPINGS_NAMESPACE)
				break;

			if (mappingNamespace == ''){
				fileURLSplit[i] = fileURLSplit[i].substring(0, fileURLSplit[i].lastIndexOf('.madcow.mappings.properties'))
			}
            
			mappingNamespace = fileURLSplit[i] + (mappingNamespace != '' ? '_' : '') + mappingNamespace
		}
		LOG.info("Mapping Namespace being applied '${mappingNamespace}'")
		properties.each { prop -> prop.key = (mappingNamespace != '' ? mappingNamespace + '_' : '') + prop.key; }
	}
    
	def loadMappingProperties(Resource resource){
		Properties properties = super.loadMappingProperties(resource)
		applyMappingNamespace(resource, properties)
		properties
	}

	Map processProperties(Properties properties) {
		Map mappings = new HashMap<String, Map>();
		properties.each {String key, String value ->

            String id = null, prop = null
            (id, prop) = key.tokenize(".")
			if (prop == null) {
				prop = "htmlId";
			}
			Map attr = new HashMap<String, String>()
			attr."$prop" = value
			mappings.put(id, attr)
		}

		LOG.debug "Processed mappings : $mappings"

		return mappings;
	}
}
