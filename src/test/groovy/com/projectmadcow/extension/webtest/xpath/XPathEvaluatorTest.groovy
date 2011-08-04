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

import com.projectmadcow.extension.webtest.xpath.XPathEvaluator

class XPathEvaluatorTest extends GroovyTestCase {

    private XPathEvaluator xPathEvaluator

    public void setUp() {
        final String html = """<html><body><form>
                                    <div id="Blargh">Some Text!</div>
                               </form></body></html>"""
        xPathEvaluator = new XPathEvaluator(html)
    }

    public void testEvaluateXPath() {
        assertEquals('Some Text!', xPathEvaluator.evaluateXPath("//div[@id='Blargh']"))
        assertEquals('', xPathEvaluator.evaluateXPath("//div[@id='non existent node']"))
    }

    public void testDoesNodeExist() {
        assertTrue xPathEvaluator.doesNodeExist("//div[@id='Blargh']")
        assertFalse xPathEvaluator.doesNodeExist("//div[@id='non existent node']")
    }
}
