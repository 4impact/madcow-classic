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

package com.projectmadcow.ant

import com.projectmadcow.engine.MadcowConfigSlurper

class MadcowAntProjectTest extends GroovyTestCase {

	/**
	* Test the config reading at config scope at the GLOBAL level only.
	*/
   
    //This test uses the file madcow.config.properties in the test/resources dir
    public void testConfigFileIsRead() {
        MadcowAntProject proj = new MadcowAntProject()
        proj.readConfigurations(['-c','globalonly'])

        //values from the config file
        assert proj.browser == 'InternetExplorer7'
        assert proj.urlProperties == 'test'
        assert proj.databaseProperties == 'testDB'
        assert proj.threads == '4'
        assert proj.proxyURL == 'http://localhost'
        assert proj.proxyPort == '8181'
        assert proj.proxyUser == 'joeschmoe'
        assert proj.proxyPassword == 'awesomepassword28'
        
        //Not in the config file, so null
        assert !proj.specifiedTests
        assert !proj.madcowSuitesPattern
    }

    //This test creeps into MadcowAntProject and messes up a the
    //reference to the configuration file. This enables us to
    //test the scenario when there's no config file found. Nasty!
    public void testCommandLineSwitchesWorkWhenTheresNoConfigFile() {
        try {
            MadcowAntProject.CONFIG_FILE = MadcowConfigSlurper.URL
            MadcowAntProject proj = new MadcowAntProject()
            proj.readConfigurations(['-b','Firefox2',
                                     '-t','someTest',
                                     '-s','someSuite',
                                     '-u','local',
                                     '-d','localdb',
									 '-c','globalonly'])

            //specified in the command line switches
            assert proj.browser == 'Firefox2'
            assert proj.specifiedTests == 'someTest'
            assert proj.madcowSuitesPattern == 'someSuite'
            assert proj.urlProperties == 'local'
            assert proj.databaseProperties == 'localdb'

            //Not available in command line switches so we get the default
            assert proj.threads == '10'
            assert proj.proxyURL == ''
            assert proj.proxyPort == '80'
            assert proj.proxyUser == ''
            assert proj.proxyPassword == ''
        } finally {
            MadcowAntProject.CONFIG_FILE = MadcowConfigSlurper.CONFIG
        }
    }

    public void testCommandLineSwitchesAreUsedInPreferenceOfConfigFile() {
        MadcowAntProject proj = new MadcowAntProject()
        proj.readConfigurations(['-b','Firefox2',
                                 '-t','someTest',
                                 '-s','someSuite',
								 '-c','globalonly'])

        //specified in the command line switches
        assert proj.browser == 'Firefox2'
        assert proj.specifiedTests == 'someTest'
        assert proj.madcowSuitesPattern == 'someSuite'

        //Not set in command line, so we use the config file value
        assert proj.urlProperties == 'test'
        assert proj.databaseProperties == 'testDB'
        
        //Not available in command line switches, so taken from the config file
        assert proj.threads == '4'
        assert proj.proxyURL == 'http://localhost'
        assert proj.proxyPort == '8181'
        assert proj.proxyUser == 'joeschmoe'
        assert proj.proxyPassword == 'awesomepassword28'
    }

    public void testReadingConfigWhenTheresNoFileOrSwitches() {
        try {
            MadcowAntProject.CONFIG_FILE = MadcowConfigSlurper.DATABASE
            MadcowAntProject proj = new MadcowAntProject()
            proj.readConfigurations(['-c','globalonly'])

            //No configs specified, so we get the defaults
            assert proj.browser == 'Firefox3'
            assert proj.threads == '10'
            assert proj.proxyPort == '80'

            //No configs specified, so no value..
            assert !proj.specifiedTests
            assert !proj.madcowSuitesPattern
            assert !proj.urlProperties
            assert !proj.databaseProperties
            assert !proj.proxyURL
            assert !proj.proxyUser
            assert !proj.proxyPassword
        } finally {
            MadcowAntProject.CONFIG_FILE = MadcowConfigSlurper.CONFIG
        }
    }

		
	/**
	 * Now test the config overriding to the LOCAL level only.
	 */	
	
	// This test uses the file madcow.config.properties in the test/resources dir and then overrides it with local.madcow.config.properties
	public void testConfigFileIsReadScopeToLocal() {
		MadcowAntProject proj = new MadcowAntProject()
		proj.readConfigurations(['-c','globalandlocal'])

		// values from the GLOBAL config file
		assert proj.proxyPort == '8181'
		assert proj.proxyUser == 'joeschmoe'
		assert proj.proxyPassword == 'awesomepassword28'

		// overridden by values from the LOCAL config file
		assert proj.browser == 'Firefox1'
		assert proj.urlProperties == 'local_override'
		assert proj.databaseProperties == 'localtestDB'
		assert proj.threads == '8'
		assert proj.proxyURL == 'http://random.house'

		//Not in the config file, so null
		assert !proj.specifiedTests
		assert !proj.madcowSuitesPattern
	}

	//This test creeps into MadcowAntProject and messes up a the
	//reference to the configuration file. This enables us to
	//test the scenario when there's no config file found. Nasty!
	public void testCommandLineSwitchesWorkWhenTheresNoConfigFileScopeToLocal() {
		try {
			MadcowAntProject.CONFIG_FILE = MadcowConfigSlurper.URL
			MadcowAntProject proj = new MadcowAntProject()
			proj.readConfigurations(['-b','Firefox2',
									 '-t','someTest',
									 '-s','someSuite',
									 '-u','local',
									 '-d','localdb',
									 '-c','globalandlocal'])

			//specified in the command line switches
			assert proj.browser == 'Firefox2'
			assert proj.specifiedTests == 'someTest'
			assert proj.madcowSuitesPattern == 'someSuite'
			assert proj.urlProperties == 'local'
			assert proj.databaseProperties == 'localdb'

			//Not available in command line switches so we get the default
			assert proj.threads == '10'
			assert proj.proxyURL == ''
			assert proj.proxyPort == '80'
			assert proj.proxyUser == ''
			assert proj.proxyPassword == ''
		} finally {
			MadcowAntProject.CONFIG_FILE = MadcowConfigSlurper.CONFIG
		}
	}

	public void testCommandLineSwitchesAreUsedInPreferenceOfConfigFileScopeToLocal() {
		MadcowAntProject proj = new MadcowAntProject()
		proj.readConfigurations(['-b','Firefox2',
								 '-t','someTest',
								 '-s','someSuite',
								 '-c','globalandlocal'])

		//specified in the command line switches
		assert proj.browser == 'Firefox2'
		assert proj.specifiedTests == 'someTest'
		assert proj.madcowSuitesPattern == 'someSuite'

		//Not set in command line, so we use the config file values:

		// values from the LOCAL config file
		assert proj.urlProperties == 'local_override'
		assert proj.databaseProperties == 'localtestDB'
		assert proj.threads == '8'
		assert proj.proxyURL == 'http://random.house'

		//Not available in command line switches, so taken from the GLOBAL config file
		assert proj.proxyPort == '8181'
		assert proj.proxyUser == 'joeschmoe'
		assert proj.proxyPassword == 'awesomepassword28'
	}
	
	
	/**
	 * Now test ALL config overriding, right to the PERSONAL level.
	 * 
	 * Resets the system property for user.name for the tests to pick up the standard PERSONAL test file.
	 *
	 * Uses the file /madcow/src/test/resources/username.madcow.config.properties
	 */
   
	// This test uses the file madcow.config.properties in the test/resources dir and then overrides it with local.madcow.config.properties
	// and then overrides it with username.madcow.config.properties
	public void testConfigFileIsReadScopeToPersonal() {
		String userName = System.getProperty('user.name')
		try {
			System.setProperty('user.name', 'username')

			MadcowAntProject proj = new MadcowAntProject()
			proj.readConfigurations(['-c','all'])
		
			// values from the GLOBAL config file
			assert proj.proxyPort == '8181'
			assert proj.proxyUser == 'joeschmoe'
			assert proj.proxyPassword == 'awesomepassword28'
		
			// overridden by values from the LOCAL config file
			assert proj.urlProperties == 'local_override'
			assert proj.databaseProperties == 'localtestDB'
			assert proj.proxyURL == 'http://random.house'
			
			// overridden by values from the PERSONAL config file
			assert proj.browser == 'Firefluff'
			assert proj.threads == '99'
			
			//Not in the config file, so null
			assert !proj.specifiedTests
			assert !proj.madcowSuitesPattern
	   } finally {
		   System.setProperty('user.name', userName)
	   }
	}
	
	//This test creeps into MadcowAntProject and messes up a the
	//reference to the configuration file. This enables us to
	//test the scenario when there's no config file found. Nasty!
	public void testCommandLineSwitchesWorkWhenTheresNoConfigFileScopeToPersonal() {
		try {
			MadcowAntProject.CONFIG_FILE = MadcowConfigSlurper.URL
			MadcowAntProject proj = new MadcowAntProject()
			proj.readConfigurations(['-b','Firefox2',
									 '-t','someTest',
									 '-s','someSuite',
									 '-u','local',
									 '-d','localdb',
									 '-c','all'])
	
			//specified in the command line switches
			assert proj.browser == 'Firefox2'
			assert proj.specifiedTests == 'someTest'
			assert proj.madcowSuitesPattern == 'someSuite'
			assert proj.urlProperties == 'local'
			assert proj.databaseProperties == 'localdb'
	
			//Not available in command line switches so we get the default
			assert proj.threads == '10'
			assert proj.proxyURL == ''
			assert proj.proxyPort == '80'
			assert proj.proxyUser == ''
			assert proj.proxyPassword == ''
		} finally {
			MadcowAntProject.CONFIG_FILE = MadcowConfigSlurper.CONFIG
		}
	}
	
	public void testCommandLineSwitchesAreUsedInPreferenceOfConfigFileScopeToPersonal() {
	   String userName = System.getProperty('user.name')
	   try {
		   System.setProperty('user.name', 'username')
		   
			MadcowAntProject proj = new MadcowAntProject()
			proj.readConfigurations(['-b','Firefox2',
									 '-t','someTest',
									 '-s','someSuite',
									 '-c','all'])
		
			// specified in the command line switches
			assert proj.browser == 'Firefox2'
			assert proj.specifiedTests == 'someTest'
			assert proj.madcowSuitesPattern == 'someSuite'
		
			// Not set in command line, so we use the config file values:
			
			// values from the PERSONAL config file
			assert proj.threads == '99'
	
			// values from the LOCAL config file
			assert proj.urlProperties == 'local_override'
			assert proj.databaseProperties == 'localtestDB'
			assert proj.proxyURL == 'http://random.house'
	
			// values from the GLOBAL config file
			assert proj.proxyPort == '8181'
			assert proj.proxyUser == 'joeschmoe'
			assert proj.proxyPassword == 'awesomepassword28'
	   } finally {
		   System.setProperty('user.name', userName)
	   }
	}
	
	public void testCommandLineSwitchesAreUsedInPreferenceOfConfigFileDefaultScopeToPersonal() {
	   String userName = System.getProperty('user.name')
	   try {
		   System.setProperty('user.name', 'username')
		   
			MadcowAntProject proj = new MadcowAntProject()
			proj.readConfigurations(['-b','Firefox2',
									 '-t','someTest',
									 '-s','someSuite'])
		
			// specified in the command line switches
			assert proj.browser == 'Firefox2'
			assert proj.specifiedTests == 'someTest'
			assert proj.madcowSuitesPattern == 'someSuite'
		
			// Not set in command line, so we use the config file values:
			
			// values from the PERSONAL config file
			assert proj.threads == '99'
	
			// values from the LOCAL config file
			assert proj.urlProperties == 'local_override'
			assert proj.databaseProperties == 'localtestDB'
			assert proj.proxyURL == 'http://random.house'
	
			// values from the GLOBAL config file
			assert proj.proxyPort == '8181'
			assert proj.proxyUser == 'joeschmoe'
			assert proj.proxyPassword == 'awesomepassword28'
	   } finally {
		   System.setProperty('user.name', userName)
	   }
	}

}
