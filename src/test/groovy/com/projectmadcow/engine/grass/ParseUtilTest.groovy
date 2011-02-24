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

/**
 * Test class for ParseUtil.
 */
class ParseUtilTest extends GroovyTestCase {

    void testConvertMapToString() {
        Map testMap = [htmlId : 'addressLine1', value : 'Adelaide St']
        String testMapAsString = ParseUtil.convertMapToString(testMap)
        assert testMapAsString == '[\'htmlId\' : \'addressLine1\', \'value\' : \'Adelaide St\', ]'
        assert Eval.me(testMapAsString) == testMap
    }

    void testConvertMapWithListValueToString() {
        Map testMap = [htmlId : 'addressLine1', value : ['Queensland', 'Victoria']]
        String testMapAsString = ParseUtil.convertMapToString(testMap)
        assert testMapAsString == '[\'htmlId\' : \'addressLine1\', \'value\' : [\'Queensland\', \'Victoria\', ], ]'
        assert Eval.me(testMapAsString) == testMap
    }

    void testConvertMapWithMapValueToString() {
        Map testMap = [htmlId : 'addressLine1', value : [state : 'Queensland']]
        String testMapAsString = ParseUtil.convertMapToString(testMap)
        assert testMapAsString == '[\'htmlId\' : \'addressLine1\', \'value\' : [\'state\' : \'Queensland\', ], ]'
        assert Eval.me(testMapAsString) == testMap
    }

    void testConvertListToString() {
        List testList = ['Queensland', 'Victoria']
        String testListAsString = ParseUtil.convertListToString(testList)
        assert testListAsString == '[\'Queensland\', \'Victoria\', ]'
        assert Eval.me(testListAsString) == testList
    }

    void testEvalMeString() {
        assert ParseUtil.evalMe('\'Queensland\'') == '\'\'Queensland\'\''
        assert ParseUtil.evalMe('"Queensland"') == '\'"Queensland"\''
        assert ParseUtil.evalMe('\'Queensland\'') == '\'\'Queensland\'\''
        assert ParseUtil.evalMe('Queensland') == '\'Queensland\''
    }

    void testUnquote() {
        assert ParseUtil.unquote('\'addressLine1\'') == 'addressLine1'
        assert ParseUtil.unquote('"addressLine1"') == 'addressLine1'
        assert ParseUtil.unquote('\'addressLine1\'') == 'addressLine1'
    }
}
