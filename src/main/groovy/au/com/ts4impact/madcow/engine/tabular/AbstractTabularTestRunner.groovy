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

import com.canoo.webtest.engine.WebTestException
import fj.Effect
import fj.data.Option
import org.apache.log4j.Logger
import fj.Unit
import fj.control.parallel.QueueActor
import fj.control.parallel.Strategy
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import static fj.P.p

import au.com.ts4impact.madcow.engine.AbstractMadcowTestCase
import au.com.ts4impact.madcow.engine.FileFinder

/**
 * Base class for CSV and Spreadsheet file runners.
 */
public abstract class AbstractTabularTestRunner extends AbstractMadcowTestCase {

    protected static final Logger LOG = Logger.getLogger(AbstractTabularTestRunner.class)

    abstract protected String getTestNameProperty()

    abstract protected String getTestFileExtension()

    abstract protected Map parseFile(File file)

    /**
     * Loads the file under test, extras the required test cases and executes each in parallel.
     */
    public void runTest() {
        try {
            def numThreads = Integer.parseInt(System.getProperty("madcow.threads", "10"))
            List<File> loadedFile = FileFinder.loadFilesFromProperty(getTestNameProperty(), getTestFileExtension())
            loadedFile.each {File file ->
                def testsToRun = parseFile(file)
                testsToRun = filterOutUnwantedTests(testsToRun)
                this.name = file.name
                executeTestsInParallel(file.name, testsToRun, numThreads)
            }
        } catch (Exception e) {
            if (!(e instanceof WebTestException)) { LOG.error("Exception while running test.", e) }
            throw e
        }
    }

    /**
     * Stubbed function to ensure junit will run this as a test.
     */
    void testFile() {
        // nothing
    }

    /**
     * Single tests or worksheets can be specified as "testFile!testName" to run a single test in a file.
     */
    protected Map filterOutUnwantedTests(Map tests) {
        String testProperty = System.getProperty(getTestNameProperty())
        if (testProperty && testProperty.split('!').length > 1) {
            LOG.debug "Tests before filtration: ${tests}"
            String testOrSheetToExecute = testProperty.split('!')[1]
            tests = tests.findAll { it.key ==~ /(?i)$testOrSheetToExecute( \[.*)?/ || it.key ==~ /(?i).*\[$testOrSheetToExecute\]/ }
            LOG.debug "Tests after filtration: ${tests}"
        }
        return tests
    }

    /**
     * Function will execute all the tests specified in the testToRun map in Parallel.
     */
    protected def executeTestsInParallel(String testNameSuffix, Map testToRun, Integer numThreads) {
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        Strategy<Unit> strategy = Strategy.executorStrategy(pool);
        def numberOfTestsRan = 0
        def exceptions = [];

        def callback = QueueActor.queueActor(strategy, {Option<Exception> result ->
            numberOfTestsRan++;
            result.foreach({Exception e -> exceptions.add(e)} as Effect)
            if (numberOfTestsRan >= testToRun.size()) {
                pool.shutdown()
            }
        } as Effect).asActor()

        testToRun.each {String name, List code ->
            new ParallelTestRunner(strategy, callback).act(p("$name : ($testNameSuffix)".toString(), code))
        }

        while (numberOfTestsRan < testToRun.size()) {
            Thread.sleep(500)
        }

        if (!exceptions.isEmpty()) {
            LOG.error(exceptions.first())
            throw exceptions.first()
        }
    }
}