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

package com.projectmadcow.extension.webtest.xpath

import com.projectmadcow.plugins.XPathEvaluator

class StartsWithTest extends GroovyTestCase {

    XPathEvaluator evaluator

    public void testStartsWithReturnsTrueWhenStringMatches() {
        assertTrue evaluator.doesNodeExist("//div[madcow:starts-with(@id,'a')]")
        assertTrue evaluator.doesNodeExist("//div[madcow:starts-with(@id,'abcdef')]")
        assertTrue evaluator.doesNodeExist("//div[madcow:starts-with(@id,'abcdefghijklmnopqrstuvwxyz')]")
    }

    public void testStartsWithReturnsFalseWhenStringDoesntMatch() {
        assertFalse evaluator.doesNodeExist("//div[madcow:starts-with(@id,'b')]")
        assertFalse evaluator.doesNodeExist("//div[madcow:starts-with(@id,'bcdefghij')]")
        assertFalse evaluator.doesNodeExist("//div[madcow:starts-with(@id,'bcdefghijklmnopqrstuvwxyz')]")
        assertFalse evaluator.doesNodeExist("//div[madcow:starts-with(@id,'aabcdefghijklmnopqrstuvwxyz')]")
    }


    void setUp() {
        evaluator = new XPathEvaluator('<div id="abcdefghijklmnopqrstuvwxyz">blargh</div')
    }
}
