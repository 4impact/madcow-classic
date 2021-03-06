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
import com.projectmadcow.engine.MadcowConfigSlurper
import org.apache.log4j.Logger

/**
 * InvokeUrl
 *
 * @author mcallon
 */
public class InvokeUrl extends InvokePage {

    protected static final Logger LOG = Logger.getLogger(InvokeUrl.class);

    public static final String URL_CONFIG_FILE_SUFFIX = 'madcow.url.properties'

    static ConfigObject urlMappings;

    static {
        urlMappings = new MadcowConfigSlurper(MadcowConfigSlurper.URL).parse()
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
