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
 * Base class for Groovy based Tests.
 *
 * @author chris
 */
public class GroovyMadcowTestCase extends AbstractMadcowTestCase {

    protected static final Logger LOG = Logger.getLogger(GroovyMadcowTestCase.class);

    /**
     * Derive the simple name of the test
     */
    public String getTestName(String test) {
        return test.split('\\(')[0];
    }

    /**
     * Execute the test case.
     */
    public void runTest() {        
        
        def testMethodName = getTestName(this.toString());

        this.executeTest(runtimeContext.antBuilder, this.toString(), {
            try {
                "${testMethodName}"()
            } catch (WebTestException wte) {
                throw wte
            } catch (e) {
                LOG.debug "Error executing test : $this", e
                runtimeContext.antBuilder.executionError(message: e.message, stacktrace: e.stackTrace.toString())
            }
        })
    }
}