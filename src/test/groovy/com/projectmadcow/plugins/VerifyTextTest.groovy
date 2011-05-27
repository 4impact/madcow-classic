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

import org.apache.tools.ant.Task

/**
 * Test class for the VerifyText plugin.
 */
class VerifyTextTest extends AbstractPluginTestCase {

    VerifyText verifyTextPlugin = new VerifyText()
    final String antTaskName = 'verifyText'

    void setUp() {
        super.setUp()

        final String html = """
                <td class="name" valign="top">
                  <input onclick="new Ajax.Request('/madcow-test-site/address/ajaxCheckForDuplicates',{asynchronous:true,evalScripts:true,onComplete:function(e){showNumberOfDuplicates(e)},parameters:Form.serialize(this.form)});return false" name="checkForDuplicates" value="Check For Duplicates" type="button">
                </td>
                <td valign="top">
                  <div valign="top" class="warning" id="duplicatesMessage">
                    <p>
                      0 duplicate addresses found
                    </p>
                  </div>
                </td>"""
        contextStub.setDefaultResponse(html)
    }

    void testTextExists() {
        verifyTextPlugin.invoke(antBuilder, [value : '0 duplicate addresses found'])
        Task pluginTask = findTask(antTaskName)
        assert findAttribute(pluginTask, 'text') == '0 duplicate addresses found'
    }


    void testTextDoesNotExist() {
        assertStepFailedException({
            verifyTextPlugin.invoke(antBuilder, [value : '55 duplicate addresses found'])
        }, 'Step[verifyText (1/0)]: Text not found in page. Expected <55 duplicate addresses found>')
    }

    void testAttributesMandatoryMissing() {
        assertStepExecutionException({
            verifyTextPlugin.invoke(antBuilder, [:])
        }, 'Required parameter "text" not set!')
    }

    void testAttributeUnsupported() {
        assertUnsupportedAttribute({
            verifyTextPlugin.invoke(antBuilder, [unsupported : 'willfail'])
        }, antTaskName, 'unsupported')
    }
}
