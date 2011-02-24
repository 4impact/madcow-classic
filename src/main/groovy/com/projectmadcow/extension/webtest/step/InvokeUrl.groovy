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

package com.projectmadcow.extension.webtest.step

import com.canoo.webtest.steps.request.InvokePage
import org.apache.log4j.Logger
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver


/**
 * InvokeUrl
 *
 * @author mcallon
 */
public class InvokeUrl extends InvokePage {

    protected static final Logger LOG = Logger.getLogger(InvokeUrl.class);

    public static final String URL_CONFIG_FILE_SUFFIX = 'madcow.url.properties'

    static Map urlMappings;

    static {
        urlMappings = initUrlMappings()
    }

    static def initUrlMappings() {

        String urlConfigFilename = System.getProperty('madcow.url.properties.file', '')
        urlConfigFilename = urlConfigFilename != '' ? "${urlConfigFilename}.${URL_CONFIG_FILE_SUFFIX}" : URL_CONFIG_FILE_SUFFIX
        LOG.info("Using url config file '${urlConfigFilename}'")

        // TODO : Check that all url properties are unique
        Properties localProperties = new Properties()
        PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver()
        Resource[] resources = resourceLoader.getResources("classpath*:${urlConfigFilename}")

        if (resources.length < 1) {
            LOG.error("Error could not find a url config file with name '${urlConfigFilename}'")
            throw new RuntimeException("Error could not find a url config file with name '${urlConfigFilename}'")
        }

        resources.each {Resource r ->
            Properties properties = new Properties()
            properties.load(r.getInputStream())
            localProperties.putAll(properties)
        }
        overwriteWithEnvironmentVariables(localProperties)
        return localProperties
    }

    static def overwriteWithEnvironmentVariables(Properties properties){
        properties.propertyNames().each { String propertyName ->

            if (System.getenv(propertyName)){
                properties.put propertyName, System.getenv(propertyName)
            }
        }
    }
    
    String value

    @Override
    public String getUrl() {
        resolveUrlMappings(value)
    }

    def resolveUrlMappings(url){
        urlMappings.each { key, value ->
            url = url.replaceAll(key, value)
        }
        url
    }
}
