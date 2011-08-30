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
        try {
	        System.setProperty('madcow.url.properties.file', 'dev')
	        MadcowConfigSlurper slurper = new MadcowConfigSlurper(MadcowConfigSlurper.URL)
	        def configObject = slurper.parse()
	        assert configObject.TEST_SITE == 'http://localhost:8080/madcow-test-site'
        } finally {
			//Err - just in case.
			System.setProperty('madcow.url.properties.file', '')
        }
    }

/*
 * Expects NOT to find the file /madcow/src/test/resources/dev.local.madcow.url.properties
 */
    public void testParseWithFilePrefixPropertySetLocal() {
        try {
	        System.setProperty('local.madcow.url.properties.file', 'mcdev')
	        MadcowConfigSlurper slurper = new MadcowConfigSlurper(MadcowConfigSlurper.URL, MadcowConfigSlurper.LOCAL)
	        def configObject = slurper.parse()
			assert configObject == [:]
        } finally {
			System.setProperty('madcow.url.properties.file', '')
        }
    }

/*
 * Uses the file /madcow/src/test/resources/madcow.config.properties
 */
	public void testGlobalConfigDefault() {
		MadcowConfigSlurper slurper = new MadcowConfigSlurper(MadcowConfigSlurper.CONFIG)
		def configObject = slurper.parse()
		assert configObject.madcow.browser == 'InternetExplorer7'
	}
	
	public void testGlobalConfigSpecific() {
		MadcowConfigSlurper slurper = new MadcowConfigSlurper(MadcowConfigSlurper.CONFIG, MadcowConfigSlurper.GLOBAL)
		def configObject = slurper.parse()
		assert configObject.madcow.browser == 'InternetExplorer7'
	}

/*
 * Expects NOT to find the file /madcow/src/test/resources/local.madcow.url.properties
 */
	public void testLocalConfigNoResourcesFound() {
		MadcowConfigSlurper slurper = new MadcowConfigSlurper(MadcowConfigSlurper.URL, MadcowConfigSlurper.LOCAL)
		def configObject = slurper.parse()
		assert configObject == [:]
	}
	
	
/*
 * Uses the file /madcow/src/test/resources/local.madcow.config.properties
 */
	public void testLocalConfig() {
		MadcowConfigSlurper slurper = new MadcowConfigSlurper(MadcowConfigSlurper.CONFIG, MadcowConfigSlurper.LOCAL)
		def configObject = slurper.parse()
		assert configObject.madcow.browser == 'Firefox1'
	}
	
/*
 * Resets the system property for user.name for the tests to pick up the standard PERSONAL test file.
 * 
 * Uses the file /madcow/src/test/resources/username.madcow.config.properties
 */
	public void testPersonalConfig() {
		String propertyUser = System.getProperty("user.name", 'no user name')
		String userHome = System.getProperty("user.home", 'no home')
		String os = System.getProperty("os.name", 'no os')
		println "testLocalConfig()   user.name: ${propertyUser}  ||| user.home: ${userHome}  ||| os.name: ${os}"

		String userName = System.getProperty('user.name')
		try {
			System.setProperty('user.name', 'username')
			MadcowConfigSlurper slurper = new MadcowConfigSlurper(MadcowConfigSlurper.CONFIG, MadcowConfigSlurper.PERSONAL)
			def configObject = slurper.parse()
			assert configObject.madcow.browser == 'Firefluff'
        } finally {
			System.setProperty('user.name', userName)
        }
	}

}
