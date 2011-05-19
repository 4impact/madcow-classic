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

/**
 * Test class for GrassExecutor.
 * 
 * @author gbunney
 */
public class GrassParserTest extends GroovyTestCase {

    protected def grassParser = new GrassParser(new RuntimeContext(new AntBuilder()))

	void testStatementsOnly() {
		List unparsedCode = [ 'invokeUrl = http://google.com',
						      'verifyText = Google',
                              'search.value = $5000']
		List parsedCode = grassParser.parseCode(unparsedCode)
		assert parsedCode == [ 'invokeUrl=\'http://google.com\'',
							   'verifyText=\'Google\'',
                               'search.value=\'$5000\'']

        unparsedCode = [ 'verifyText = Dr Bunney\'s Emporium' ]
		parsedCode = grassParser.parseCode(unparsedCode)
		assert parsedCode == ['verifyText=\'Dr Bunney\'s Emporium\'']
	}
	
	void testStatementsOnlyWithClosures() {
		List unparsedCode = [ 'invokeUrl = madcow.eval("\'http://google.com\'")',
							  'verifyText = madcow.eval({return "Google\'s"})']
		
		List parsedCode = grassParser.parseCode(unparsedCode)
		
		assert parsedCode == [ 'invokeUrl=\'http://google.com\'',
							   'verifyText=\'Google\'s\'']
	}
	
	void testStatementsOnlyWithDataParameters() {
		List unparsedCode = [ '@url = madcow.eval({return \'http://google.com\'})',
							  '@partOfGoogle = oog',
							  'invokeUrl = @url',
							  'verifyText = G@{partOfGoogle}le']
		List parsedCode = grassParser.parseCode(unparsedCode)
		assert parsedCode == [ 'invokeUrl=\'http://google.com\'',
							   'verifyText=\'Google\'']
	}

    void testStatementsOnlyWithEmbeddedParameters() {
        List unparsedCode = [ '@global.currentDate = madcow.eval({new Date().format(\'dd/MM/yyyy\')})',
                              '@currentDate = @global.currentDate',
							  'addressLine1.value = @currentDate' ]
		List parsedCode = grassParser.parseCode(unparsedCode)
		assert parsedCode == [ 'addressLine1.value=\''+ new Date().format('dd/MM/yyyy')+'\'']

    }

    void testParameterValuesAreUsedInPreferenceOfDefaultValues() {
        List unparsedCode = [ '@addrValue = 55 Queen Street',
                              '@addrValue.default = 28 Blargh Street',
							  'addressLine1.value = @addrValue' ]
		List parsedCode = grassParser.parseCode(unparsedCode)
		assert parsedCode == [ "addressLine1.value='55 Queen Street'"]
    }

    void testParameterDefaultValuesAreUsedIfParameterIsNotDefined() {
        List unparsedCode = [ '@someOtherParameter = This is just here for effect',
                              '@addrValue.default = 28 Blargh Street',
							  'addressLine1.value = @addrValue' ]
		List parsedCode = grassParser.parseCode(unparsedCode)
		assert parsedCode == [ "addressLine1.value='28 Blargh Street'"]
    }

    void testParameterValuesCanBeDefinedAfterDefault() {
        List unparsedCode = [ '@addrValue.default = 28 Blargh Street',
                              '@addrValue = 55 Queen Street',
							  'addressLine1.value = @addrValue' ]
		List parsedCode = grassParser.parseCode(unparsedCode)
		assert parsedCode == [ "addressLine1.value='55 Queen Street'"]
    }
	
	void testMaps() {
		List unparsedCode = ['clickLink = [xpath : \'//a\']']
		List parsedCode = grassParser.parseCode(unparsedCode)
		assert parsedCode == [ 'clickLink=[\'xpath\' : \'//a\', ]']
		
		unparsedCode = ['clickLink = [xpath : \'//a\',text : "Search"]']
		parsedCode = grassParser.parseCode(unparsedCode)
		assert parsedCode == [ 'clickLink=[\'xpath\' : \'//a\', \'text\' : \'Search\', ]']

        unparsedCode = ['clickLink = [xpath : \'//a\',text : "Dr Jones\'"]']
		parsedCode = grassParser.parseCode(unparsedCode)
		assert parsedCode == [ 'clickLink=[\'xpath\' : \'//a\', \'text\' : \'Dr Jones\\\'\', ]']
	}

	void testMapsWithClosures() {
		List unparsedCode = ['clickLink = [xpath : \'madcow.eval({ return "//a"})\', text : "madcow.eval({return \'Search\'})"]']
		List parsedCode = grassParser.parseCode(unparsedCode)
		assert parsedCode == [ 'clickLink=[\'xpath\' : \'//a\', \'text\' : \'Search\', ]']
	}

    void testMapsWithValueLists() {
		List unparsedCode = ['verifySelectFieldOptions = [htmlId: \'country\', options : [\'Australia\', \'New Zealand\']]']
		List parsedCode = grassParser.parseCode(unparsedCode)
		assert parsedCode == [ 'verifySelectFieldOptions=[\'htmlId\' : \'country\', \'options\' : [\'Australia\', \'New Zealand\', ], ]']
	}

    void testMapsWithValueListsWithParameters() {
		List unparsedCode = ['@aus = Australia', 'verifySelectFieldOptions = [htmlId: \'country\', options : [\'@aus\', \'New Zealand\']]']
		List parsedCode = grassParser.parseCode(unparsedCode)
		assert parsedCode == [ 'verifySelectFieldOptions=[\'htmlId\' : \'country\', \'options\' : [\'Australia\', \'New Zealand\', ], ]']
	}

    //table.currentRow.checkValue = ['@columnName' : '@columnValue']
	void testMapsWithDataParameters() {
		List unparsedCode = ['@columnName = suburb',
                             '@columnValue = BRISBANE',
						     'table.currentRow.checkValue = [\'@columnName\' : \'@columnValue\']']
		List parsedCode = grassParser.parseCode(unparsedCode)
		assert parsedCode == [ 'table.currentRow.checkValue=[\'suburb\' : \'BRISBANE\', ]']
	}
	
	void testLists() {
		List unparsedCode = ['country.verifySelectFieldOptions = [\'Australia\', \'New Zealand\']']
		List parsedCode = grassParser.parseCode(unparsedCode)
		assert parsedCode == [ 'country.verifySelectFieldOptions=[\'Australia\', \'New Zealand\', ]']
	}

	void testListsWithClosures() {
		List unparsedCode = ['country.verifySelectFieldOptions = [\'madcow.eval {return "Australia"}\', \'New Zealand\']']
		List parsedCode = grassParser.parseCode(unparsedCode)
		assert parsedCode == [ 'country.verifySelectFieldOptions=[\'Australia\', \'New Zealand\', ]']
	}

    void testListsWithDataParameters() {
		List unparsedCode = ['@aus = Australia',
                             'country.verifySelectFieldOptions = [\'@aus\', \'New Zealand\']']
		List parsedCode = grassParser.parseCode(unparsedCode)
		assert parsedCode == [ 'country.verifySelectFieldOptions=[\'Australia\', \'New Zealand\', ]']
	}

    void testMapInCommand() {
        List unparsedCode = ["sometable.table.countRows['Address line 1' : 'Unit A'].equals = 1"]
        List parsedCode = grassParser.parseCode(unparsedCode)
        assertEquals(["sometable.table.countRows(['Address line 1' : 'Unit A']).equals='1'"], parsedCode)
    }

    void testMapInUnsupportedCommand() {
        shouldFail {
            grassParser.parseCode(["sometable.table.currentRow['Address line 1' : 'Unit A'].clickLink = Id"])
        }
    }

    void testLinesWithNoOperationOrCommandWillFail() {
        shouldFail {
            grassParser.parseCode(["menu_newAddress"])
        }
    }
}
