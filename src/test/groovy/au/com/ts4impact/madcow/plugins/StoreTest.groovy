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

package au.com.ts4impact.madcow.plugins

import org.apache.tools.ant.Task

/**
 * Test class for the Store plugin.
 */
public class StoreTest extends AbstractPluginTestCase {

    Store storePlugin = new Store()
    final String antTaskName = 'storeXPath'

    void setUp() {
        super.setUp()

        final String html = """<html><body>
                               <div id="address" name="address">Adelaide St, Brisbane</div>
                               </body></html>"""
        contextStub.setDefaultResponse(html)
    }

    void testStoreByHtmlId() {

        def attributes = [htmlId: 'address', value : 'Address Details']

        storePlugin.invoke(antBuilder, attributes)

        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'xpath').contains(attributes.htmlId)
        assert findAttribute(pluginTask, 'property') == 'Address Details'
    }

    void testStoreByName() {

        def attributes = [name: 'address', value : 'Address Details']

        storePlugin.invoke(antBuilder, attributes)

        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'xpath').contains(attributes.name)
        assert findAttribute(pluginTask, 'property') == 'Address Details'
    }

    void testStoreByXPath() {

        def attributes = [xpath: '//div[1]', value : 'Address Details']

        storePlugin.invoke(antBuilder, attributes)

        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'xpath').contains(attributes.xpath)
        assert findAttribute(pluginTask, 'property') == 'Address Details'
    }

    void testAttributesMandatoryMissing() {
        assertStepExecutionException({
            storePlugin.invoke(antBuilder, [:])
        }, 'Required parameter "property" not set or set to empty string!')
    }

    void testAttributeUnsupported() {
        assertUnsupportedAttribute({
            storePlugin.invoke(antBuilder, [unsupported : 'willfail'])
        }, antTaskName, 'unsupported')
    }

    void testMissingIdentifier() {
        assertStepFailedException({
            storePlugin.invoke(antBuilder, [htmlId: 'state', value : 'Address Details'])
        }, 'No match for xpath expression')
    }
}
