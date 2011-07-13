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

import groovyjarjarcommonscli.Option
import groovyjarjarcommonscli.ParseException
import org.apache.log4j.Logger
import org.apache.tools.ant.BuildException
import org.apache.tools.ant.DefaultLogger
import org.apache.tools.ant.Project
import org.apache.tools.ant.ProjectHelper
import com.projectmadcow.engine.MadcowConfigSlurper

/**
 * This is the central Ant Project hook for running Madcow
 * from the command line, using the runMadcow scripts.
 *
 * Loads in the build.xml from the root of the project,
 * setting any applicable ant properties based on the arguments passed
 * from the command line.
 */
public class MadcowAntProject {

    protected static final Logger LOG = Logger.getLogger(MadcowAntProject.class);
    protected static def CONFIG_FILE = MadcowConfigSlurper.CONFIG

    Project antProject
    def specifiedTests
    def target = 'run-all-tests'
    def browser = 'Firefox3'
    def madcowSuitesPattern = ''
    def urlProperties = ''
    def databaseProperties = ''
    def threads = '10'

    public MadcowAntProject(){
    }

    void executeAnt() throws BuildException {

        File buildFile = new File("build.xml")

        antProject = new Project()
        antProject.setBaseDir new File("./")
        DefaultLogger consoleLogger = new DefaultLogger()
        consoleLogger.setErrorPrintStream(System.err)
        consoleLogger.setOutputPrintStream(System.out)
        consoleLogger.setMessageOutputLevel(Project.MSG_INFO)
        antProject.addBuildListener(consoleLogger)

        antProject.fireBuildStarted()
        antProject.init()
        ProjectHelper helper = ProjectHelper.getProjectHelper()
        antProject.addReference("ant.projectHelper", helper)
        helper.parse(antProject, buildFile)
        antProject.setProperty 'ant.file', buildFile.getAbsolutePath()

        if (specifiedTests) antProject.setProperty 'madcow.tests', specifiedTests

        antProject.setProperty 'madcow.suites.pattern', madcowSuitesPattern ?: ''
        antProject.setProperty 'madcow.browser', browser
        antProject.setProperty 'madcow.url.properties.file', urlProperties
        antProject.setProperty 'madcow.database.properties.file', databaseProperties
        antProject.setProperty 'madcow.threads', threads

        LOG.info "Invoking ant with target : $target"
        antProject.executeTarget(target)
    }



    private def parseArgs(incomingArgs) throws ParseException {
        def cli = new CliBuilder(usage:'runMadcow [options]', header:'Options:')

        cli.with {
            h(longOpt : 'help', 'Show Usage Information')
            t(longOpt : 'test', args: Option.UNLIMITED_VALUES, valueSeparator: ',', argName : 'testname', 'comma seperated list of test names')
            b(longOpt : 'browser', args: 1, argName: 'browserName', 'specific target browser out of [InternetExplorer6, InternetExplorer7, Firefox2, Firefox3], default is Firefox3')
            s(longOpt : 'suite', args: 1, argName: 'suitePattern', 'specify matching pattern of the suite(s) to run - e.g. stubbed/finance')
            u(longOpt : 'url', args: 1, argName: 'urlPropertiesName', 'specific url properties file to use when running tests')
            d(longOpt : 'database', args: 1, argName: 'dbPropertiesName', 'specific database properties file to use when running tests')
            m(longOpt : 'mappings', 'Regenerate the Mappings Reference files')
        }

        cli.stopAtNonOption = false

        def options = cli.parse(incomingArgs)

        if (options.help){
            cli.usage()
            System.exit(1)
        }

        if (options.m) {
            target = 'generateMappingsReference'
            return
        }

        if (options.b){
            browser = options.b
        }

        if (options.ts){
            specifiedTests = options.ts.toString()
            specifiedTests = specifiedTests.substring(1, specifiedTests.length()-1).replaceAll(', ', ',')

            target = 'run-tests'
        }

        if (options.s){
            madcowSuitesPattern = options.s
            target = 'run-all-tests'
        }

        if (options.u)
            urlProperties = options.u

        if (options.d)
            databaseProperties = options.d
    }

    //Reads the madcow config file madcow.config.properties
    private void readConfigFile() {
        ConfigObject conf = new MadcowConfigSlurper(CONFIG_FILE).parse()

        browser = conf.madcow.browser ?: browser
        urlProperties = conf.madcow.default.url ?: urlProperties
        databaseProperties = conf.madcow.default.database ?: databaseProperties
        threads = conf.madcow.threads ?: threads
    }

    //we read the config file first, then override with the command line switches
    def readConfigurations(incomingArgs) {
        readConfigFile()
        parseArgs incomingArgs
    }


    static main(args){
        try {
            def madcowAntProject = new MadcowAntProject()
            madcowAntProject.readConfigurations(args)
            madcowAntProject.executeAnt()

        } catch (e){
            LOG.debug e
            System.exit(1);
        }
    }
}
