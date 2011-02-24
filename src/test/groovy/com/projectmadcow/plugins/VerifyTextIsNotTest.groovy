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

import org.apache.tools.ant.RuntimeConfigurable
import org.apache.tools.ant.Task

/**
 * Test class for the VerifyTextIsNot plugin.
 */
class VerifyTextIsNotTest extends AbstractPluginTestCase {

    VerifyTextIsNot verifyTextIsNotPlugin = new VerifyTextIsNot()

    void setUp() {
        super.setUp()

        final String html = """<html><body><form>
                                     Address Details
                               </form></body></html>"""
        contextStub.setDefaultResponse(html)
    }

    void testTextDoesntExist() {
        verifyTextIsNotPlugin.invoke(antBuilder, [value : 'Address Line 1'])
        Task notWrapper = findTask('not')
        RuntimeConfigurable verifyTextTask = notWrapper.runtimeConfigurableWrapper.children.nextElement() as RuntimeConfigurable
        assert verifyTextTask.attributeMap.get('text') == 'Address Line 1'
    }
}
