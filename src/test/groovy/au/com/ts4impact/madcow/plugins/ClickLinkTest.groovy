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
 * Test class for the ClickLink plugin.
 */
public class ClickLinkTest extends AbstractPluginTestCase {

    ClickLink clickLinkPlugin = new ClickLink()

    void setUp() {
        super.setUp()

        final String html = """<html><body>
                               <a href="#" id="submit" name="submit" label="Submit">Submit</a>
                               </body></html>"""
        contextStub.setDefaultResponse(html)
    }

    void testClickLinkByHtmlId() {
        clickLinkPlugin.invoke(antBuilder, [htmlid: 'submit'])

        Task pluginTask = findTask('clickLink')
        assert findAttribute(pluginTask, 'htmlid') == 'submit'
    }

//    TODO - Raised Jira MADCOW-171
//    void testClickLinkByName() {
//        clickLinkPlugin.invoke(antBuilder, [name: 'submit'])
//
//        Task pluginTask = findTask('clickLink')
//        assert findAttribute(pluginTask, 'htmlid') == 'submit'
//    }

    void testClickLinkByXPath() {
        clickLinkPlugin.invoke(antBuilder, [xpath: '//a[1]'])

        Task pluginTask = findTask('clickLink')
        assert findAttribute(pluginTask, 'xpath') == '//a[1]'
    }

    void testPluginWithValue() {
        clickLinkPlugin.invoke(antBuilder, [description: 'Click the link', value : 'Submit'])
        Task pluginTask = findTask('clickLink')
        assert findAttribute(pluginTask, 'label') == 'Submit'
        assert findAttribute(pluginTask, 'description') == 'Click the link'

        clickLinkPlugin.invoke(antBuilder, [value :'Submit'])
        pluginTask = findTask('clickLink')
        assert findAttribute(pluginTask, 'label') == 'Submit'
    }

    void testAttributesMapUnsupported() {
        assertUnsupportedAttribute({
            clickLinkPlugin.invoke(antBuilder, [htmlid: 'submit', unknown : true])
        }, 'clickLink', 'unknown')

        assertUnsupportedAttribute({
            clickLinkPlugin.invoke(antBuilder, [htmlid: 'submit', unsupported : 'value'])
        }, 'clickLink', 'unsupported')
    }

    void testAttributesMandatoryMissing() {
        assertStepExecutionException({
            clickLinkPlugin.invoke(antBuilder, [:])
        }, '"htmlId" or "xpath" or "label" or "href" must be set!')
    }

    void testAttributesExclusiveCombinations() {

        String errorMessage = '"htmlId", "xpath" and "label" or "href" can\'t be combined!'

        assertStepExecutionException({
            clickLinkPlugin.invoke(antBuilder, [htmlId: 'submit', xpath: '//a[@id=\'submit\']', label: 'Submit', href:'#'])
        }, errorMessage)

        assertStepExecutionException({
            clickLinkPlugin.invoke(antBuilder, [htmlId: 'submit', xpath: '//a[@id=\'submit\']'])
        }, errorMessage)

        assertStepExecutionException({
            clickLinkPlugin.invoke(antBuilder, [htmlId: 'submit', label: 'Submit'])
        }, errorMessage)

        assertStepExecutionException({
            clickLinkPlugin.invoke(antBuilder, [htmlId: 'submit', href: '#'])
        }, errorMessage)

        assertStepExecutionException({
            clickLinkPlugin.invoke(antBuilder, [xpath: '//a[@id=\'submit\']', label: 'Submit'])
        }, errorMessage)

        assertStepExecutionException({
            clickLinkPlugin.invoke(antBuilder, [xpath: '//a[@id=\'submit\']', href: '#'])
        }, errorMessage)
    }
}
