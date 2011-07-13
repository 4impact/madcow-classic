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

    //This test uses the file madcow.config.properties in the test/resources dir
    public void testConfigFileIsRead() {
        MadcowAntProject proj = new MadcowAntProject()
        proj.readConfigurations([])

        //values from the config file
        assert proj.browser == 'InternetExplorer7'
        assert proj.urlProperties == 'test'
        assert proj.databaseProperties == 'testDB'
        assert proj.threads == '4'
        
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
                                     '-d','localdb'])

            //specified in the command line switches
            assert proj.browser == 'Firefox2'
            assert proj.specifiedTests == 'someTest'
            assert proj.madcowSuitesPattern == 'someSuite'
            assert proj.urlProperties == 'local'
            assert proj.databaseProperties == 'localdb'
            
            //Not available in command line switches so we get the default
            assert proj.threads == '10'
        } finally {
            MadcowAntProject.CONFIG_FILE = MadcowConfigSlurper.CONFIG
        }
    }

    public void testCommandLineSwitchesAreUsedInPreferenceOfConfigFile() {
        MadcowAntProject proj = new MadcowAntProject()
        proj.readConfigurations(['-b','Firefox2',
                                 '-t','someTest',
                                 '-s','someSuite'])

        //specified in the command line switches
        assert proj.browser == 'Firefox2'
        assert proj.specifiedTests == 'someTest'
        assert proj.madcowSuitesPattern == 'someSuite'

        //Not set in command line, so we use the config file value
        assert proj.urlProperties == 'test'
        assert proj.databaseProperties == 'testDB'
        
        //Not available in command line switches, so taken from the config file
        assert proj.threads == '4'
    }

    public void testReadingConfigWhenTheresNoFileOrSwitches() {
        try {
            MadcowAntProject.CONFIG_FILE = MadcowConfigSlurper.DATABASE
            MadcowAntProject proj = new MadcowAntProject()
            proj.readConfigurations([])

            //No configs specified, so we get the defaults
            assert proj.browser == 'Firefox3'
            assert proj.threads == '10'

            //No configs specified, so no value..
            assert !proj.specifiedTests
            assert !proj.madcowSuitesPattern
            assert !proj.urlProperties
            assert !proj.databaseProperties
        } finally {
            MadcowAntProject.CONFIG_FILE = MadcowConfigSlurper.CONFIG
        }
    }
}
