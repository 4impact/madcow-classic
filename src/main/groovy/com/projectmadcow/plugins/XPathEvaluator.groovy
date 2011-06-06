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

import com.canoo.webtest.engine.xpath.XPathHelper
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.MockWebConnection
import com.gargoylesoftware.htmlunit.Page
import com.projectmadcow.engine.XPathExtensionRegister

/**
 * A helper class that we can use to evaluate XPath. This is being used
 * in place of the more simple javax.xml.Xpath API because it allows us to test
 * XPath with Webtest and Madcow XPath functions. As an added bonus, it also means
 * that we're using the same XPath parser for both our tests and execution.
 *
 * This class has been moved from the test to the source in order to get re-use in
 * plugins.
 */
class XPathEvaluator {

    private Page page

    public XPathEvaluator(String html) {
        assert html, 'XPathEvaluator cannot be instantiated without html to operate on.'
        URL url = new URL("http://localhost/")
        WebClient client = createTestWebClient(url, html)
        page = client.getPage(url)
        XPathExtensionRegister.registerExtensions()
    }

    private WebClient createTestWebClient(URL url, String html) {
        def connection = new MockWebConnection()
        connection.setResponse(url, html)
        def client = new WebClient()
        client.setWebConnection(connection)
        return client
    }

    public String evaluateXPath(String xpath) {
        XPathHelper helper = new XPathHelper()
        return helper.stringValueOf(page, xpath)
    }

    public boolean doesNodeExist(String xpath) {
        XPathHelper helper = new XPathHelper()
        return helper.selectFirst(page, xpath) == null
    }
    
}
