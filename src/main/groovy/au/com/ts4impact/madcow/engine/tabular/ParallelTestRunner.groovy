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

package au.com.ts4impact.madcow.engine.tabular

import au.com.ts4impact.madcow.engine.AbstractMadcowTestCase
import au.com.ts4impact.madcow.engine.AntBuilderFactory
import au.com.ts4impact.madcow.engine.RuntimeContext
import au.com.ts4impact.madcow.engine.grass.GrassExecutor
import fj.Effect
import fj.P2
import fj.control.parallel.Actor
import fj.control.parallel.Strategy
import fj.data.Option
import org.apache.log4j.Logger
import static fj.data.Option.none
import static fj.data.Option.some

/**
 * Parallel Test Runner is used to run multiple CSV/Spreadsheet in... Parallel!
 *
 * Constructor generates a list of Actors which are invoked through the act function.
 */
public class ParallelTestRunner {

    protected static final Logger LOG = Logger.getLogger(ParallelTestRunner.class);

    private final Actor<Option<Exception>> callback;
    private final Actor<P2<String, List>> parallelActor;

    def ParallelTestRunner(final Strategy strategy, final def callback) {
        this.callback = callback;

        this.parallelActor = Actor.actor(strategy, {P2<String, List> parameters ->

            AntBuilder antBuilder = AntBuilderFactory.createAntBuilder();
            def testName = parameters._1()

            try {
                if (isTestToBeIgnored(testName)) {
                    LOG.info "Ignoring test ${testName}"
                } else {
                    AbstractMadcowTestCase.executeTest(antBuilder, testName, {
                        GrassExecutor.executeCode(new RuntimeContext(antBuilder), parameters._2(), testName)
                    })
                }
                callback.act none()
            } catch (e) {
                callback.act some(e)
            }
        } as Effect)
    }

    def act(P2<String, List> parameters) {
        parallelActor.act(parameters)
    }

    static boolean isTestToBeIgnored(String testName){
        testName.trim().startsWith('#') || testName.trim().startsWith('//')
    }
}