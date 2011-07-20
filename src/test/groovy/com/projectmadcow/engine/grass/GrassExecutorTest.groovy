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

package com.projectmadcow.engine.grass

import com.projectmadcow.engine.RuntimeContext
import com.projectmadcow.plugins.AbstractPluginTestCase

class GrassExecutorTest extends AbstractPluginTestCase {

    void testIgnoreStepIsFoundIfFirstLine() {
        def runtimeContext = new RuntimeContext(antBuilder)
        GrassExecutor.executeCode(runtimeContext,
                                ['madcow.ignore',
                                 'verifyText = FAIL NOW',
                                 'pigs.goat=ksajdkasd'], 'TestName')
    }

    void testIgnoreStepIsFoundNoMatterWhereItIs() {
        def runtimeContext = new RuntimeContext(antBuilder)
        GrassExecutor.executeCode(runtimeContext,
                                ['verifyText = FAIL NOW',
                                 'pigs.goat=ksajdkasd',
                                 'madcow.ignore'], 'TestName')
    }

    void testIgnoreStepHandlesCapitalsAndTrailingSpaces() {
        def runtimeContext = new RuntimeContext(antBuilder)
        GrassExecutor.executeCode(runtimeContext,
                                ['   MADCOW.Ignore    ',
                                 'verifyText = FAIL NOW',
                                 'pigs.goat=ksajdkasd'], 'TestName')

    }

    void testOtherTestsWouldFailIfIgnoreWasntWorking() {
        shouldFail {
            def runtimeContext = new RuntimeContext(antBuilder)
            GrassExecutor.executeCode(runtimeContext,
                                    ['verifyText = FAIL NOW',
                                     'pigs.goat=ksajdkasd'], 'TestName')
        }
    }
}
