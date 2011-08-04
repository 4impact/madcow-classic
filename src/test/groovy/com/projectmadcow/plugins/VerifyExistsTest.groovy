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

package com.projectmadcow.plugins

import com.projectmadcow.extension.webtest.xpath.XPathEvaluator

class VerifyExistsTest extends AbstractPluginTestCase {

    VerifyExists verifyExistsPlugin = new VerifyExists()
    final String antTaskName = 'verifyXPath'
    
    XPathEvaluator xPathEvaluator

    void setUp() {
        super.setUp()

        final String html = """
                <input type="button" name="button1" value="value1" />
                <input type="button" id="button2" value="value2" />
                <input type="button" label="button3" value="value3" />
                <input type="button" name="button4" value="value4" />
                """
        contextStub.setDefaultResponse(html)
        xPathEvaluator = new XPathEvaluator(html)
    }

    void testVerifyExistsWithName() {
        verifyExistsPlugin.invoke(antBuilder, [name : 'button1'])
    }

    void testVerifyExistsWithHtmlId() {
        verifyExistsPlugin.invoke(antBuilder, [htmlId : 'button2'])
    }

    void testVerifyExistsWithLabel() {
        verifyExistsPlugin.invoke(antBuilder, [forLabel : 'button3'])
    }

    void testVerifyExistsWithXPath() {
        verifyExistsPlugin.invoke(antBuilder, [xpath : "//input[@name=\'button4\']"])
    }

    void testVerifyExistsWithNameFailsIfElementDoesntExist() {
        assertStepFailedException({
            verifyExistsPlugin.invoke(antBuilder, [name : 'button5'])
        }, 'matched no nodes')
    }

    void testVerifyExistsWithHtmlIdFailsIfElementDoesntExist() {
        assertStepFailedException({
            verifyExistsPlugin.invoke(antBuilder, [htmlId : 'button5'])
        }, 'matched no nodes')
    }

    void testVerifyExistsWithLabelFailsIfElementDoesntExist() {
        assertStepFailedException({
            verifyExistsPlugin.invoke(antBuilder, [forLabel : 'button5'])
        }, 'matched no nodes')
    }

    void testVerifyExistsWithXPathFailsIfElementDoesntExist() {
        assertStepFailedException({
            verifyExistsPlugin.invoke(antBuilder, [xpath : "//input[@name=\'button5\']"])
        }, 'matched no nodes')
    }
}
