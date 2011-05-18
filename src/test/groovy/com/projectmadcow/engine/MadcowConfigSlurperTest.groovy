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

/*
 * Uses the files src/test/resources/madcow.url.properties and src/test/resources/dev.madcow.url.properties
 */
class MadcowConfigSlurperTest extends GroovyTestCase {

    public void testParseWithoutFilePrefixPropertySet() {
        MadcowConfigSlurper slurper = new MadcowConfigSlurper(MadcowConfigSlurper.URL)
        def configObject = slurper.parse()
        assert configObject.TEST_SITE == 'http://test-site.projectmadcow.com:8080/madcow-test-site'
    }

    public void testParseWithFilePrefixPropertySet() {
        System.setProperty('madcow.url.properties.file', 'dev')
        MadcowConfigSlurper slurper = new MadcowConfigSlurper(MadcowConfigSlurper.URL)
        def configObject = slurper.parse()
        assert configObject.TEST_SITE == 'http://localhost:8080/madcow-test-site'

        //Err - just in case.
        System.setProperty('madcow.url.properties.file', '')
    }

}
