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

import org.apache.commons.io.FileUtils
import org.apache.log4j.Logger
import com.projectmadcow.engine.grass.GrassExecutor

/**
 * Class to execute a properties file based test.
 *
 * @author chris
 */
public class PropertiesFileTestRunner extends AbstractMadcowTestCase {

    protected static final Logger LOG = Logger.getLogger(PropertiesFileTestRunner.class);

    /**
     * Execute the test case.
     */
    public void runTest() {
        loadFile().each {File currentTest ->
            try {
                LOG.debug("***** run test $currentTest")

                this.setName(currentTest.name)
                executeTest(runtimeContext.antBuilder, currentTest.name, {
                    def createdCodeLines = FileUtils.readLines(currentTest)
                    GrassExecutor.executeCode(runtimeContext, createdCodeLines, currentTest.name)
                })
            } catch (e) {
                throw e
            }
        }
    }

    private List<File> loadFile() {
        final String madcowTestName = System.getProperty("madcow.test.property")

        def files = FileFinder.findPropertyFiles(madcowTestName, 'test')

        LOG.debug ("******* loadFile - files : $files")
        return files
    }

    /**
     * Stubbed function to ensure junit will run this as a test.
     */
    void testProperties() {
    }

}