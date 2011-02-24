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

import com.canoo.webtest.engine.WebTestException
import org.apache.log4j.Logger

/**
 * Base class for all Madcow Test Cases.
 */
public abstract class AbstractMadcowTestCase extends GroovyTestCase {

    protected static final Logger LOG = Logger.getLogger(AbstractMadcowTestCase.class);

    RuntimeContext runtimeContext
    
    /**
     * WebTest Environment Configuration - see http://webtest.canoo.com/webtest/manual/config.html
     */
    public static String BASEDIR = System.getProperty('basedir', '.');
    public def static configMap = [
        summary: 'true',
        saveresponse: 'true',
        resultPath: System.getProperty('wt.config.resultpath', BASEDIR + '/build/webtest-results'),
        browser: System.getProperty('madcow.browser', 'Firefox3'),
        timeout : System.getProperty('wt.config.timeout','30') // default 30 seconds
    ];
    
    /**
     * Called on each test execution.
     */
    public void setUp() {    
        super.setUp()        

        runtimeContext = new RuntimeContext()

        XPathExtensionRegister.registerExtensions()
    }
    
    def propertyMissing(String name) {
        runtimeContext.propertyMissing(name)
    }
    
    def propertyMissing(String name, Object value) {
        runtimeContext.propertyMissing(name, value)
    }

    /**
     * Execute a collection of executionSteps.
     * This will create the WebTest steps for the supplied antBuilder.
     */
    static def executeTest(AntBuilder antBuilder, String testName, Closure executionSteps) {
        def configWithSavePrefix = [saveprefix : testName.replaceAll(" ", "_").replaceAll(':', '_')]
        configWithSavePrefix.putAll(AbstractMadcowTestCase.configMap)
        LOG.info "Running ${testName}"
        try {
            antBuilder.webtest(name: "${testName}") {
                config(configWithSavePrefix) {
                    option(name: 'ThrowExceptionOnScriptError', value: false);
                    option(name: 'ThrowExceptionOnFailingStatusCode', value: false);
                }
                steps {
                    antBuilder.group(description: "Setup Madcow Test") {
                        antBuilder.groovy("step.context.webClient.setAjaxController(new com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController())")
                        antBuilder.groovy("step.context.webClient.setPageCreator(new com.projectmadcow.extension.htmlunit.pagecreator.MadcowPageCreator())")
                    }
                    executionSteps.call()
                }
            }
            LOG.info "Passed $testName"
        } catch (WebTestException exception) {
            LOG.info "Failed $testName"
            throw exception
        } catch (Exception exception) {
            LOG.error "Failed $testName - exception while running test ${exception.message}"
            throw exception
        }
    }

}
