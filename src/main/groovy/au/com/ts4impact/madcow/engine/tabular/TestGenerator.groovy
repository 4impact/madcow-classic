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

import org.apache.log4j.Logger

/**
 * Creates tests based on the data in the CSV/Spreadsheet files.
 * This performs the transformation from tabular data to flat data, like that of a
 * Properties file test.
 *
 * @author chris
 */
public class TestGenerator {

    private static final Logger LOG = Logger.getLogger(TestGenerator.class)

    static Map generateTests(List<String[]> testMatrix) {
        generateTests(testMatrix, "")
    }

    static Map generateTests(List<String[]> testMatrix, String sheetName) {
        createTests(transposeIfInversed(testMatrix), sheetName)
    }

    static Map createTests(List<String[]> testMatrix, sheetName) {
        final String[] header = testMatrix.first()
        final List code = testMatrix.tail()
        Map tests = new HashMap();
        header.eachWithIndex {testName, i ->
            if (i > 0 && testName != null && testName != "") {
                final List test = code.collect {line -> createCodeLine(line, i) }
                //If you change the format of the label, be sure to change AbstractTabularTestRunner.filterOutUnwantedTests
                def label = sheetName == "" ? testName : testName + " [$sheetName]"
                tests."$label" = test;
            }
        }
        tests
    }

    static List transpose(List<String[]> matrix) {
        def transposedMatrix = [];
        matrix.eachWithIndex {row, i ->
            row.eachWithIndex {String cell, j ->
                if (transposedMatrix.size() <= j) {
                    transposedMatrix.add([cell])
                } else {
                    transposedMatrix.get(j).add(cell)
                }
            }
        }
        transposedMatrix
    }

    static List<String[]> transposeIfInversed(List csvFileTests) {
        if (csvFileTests.first()[0] == "inverse") {
            transpose(csvFileTests)
        } else {
            csvFileTests
        }
    }

    static def createCodeLine(line, i) {
        LOG.trace "createCodeLine($line, $i)"

		final def value = line[i]
		if (value != null && value =~ /madcowSkip/) {
			"# skip step"
		} else if (value != null && !value.equals("")) {
		    LOG.trace " *** value is not null and not blank so convert to link=sanitiseValue : ${line[0] + '=' + sanitiseValue(value)}"
			line[0] + "=" + sanitiseValue(value)
		} else if (line[1] != null && !line[1].equals("") && !(line[1] =~ /madcowSkip/)) {
		    LOG.trace " *** line[1] is not null, not blank and not madcowSkip so convert to link=sanitiseValue : ${line[0] + '=' + sanitiseValue(line[1])}"
			line[0] + "=" + sanitiseValue(line[1])
		} else {
		    LOG.trace " *** default ELSE line : ${line[0]}"
			line[0]
		}
	}

	static String sanitiseValue(def value) {
        value.replaceAll('“|”', '"')
	}

}