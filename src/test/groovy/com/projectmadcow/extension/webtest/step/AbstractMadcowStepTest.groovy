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

package com.projectmadcow.extension.webtest.step

import com.canoo.webtest.steps.BaseStepTestCase
import com.canoo.webtest.steps.Step
import org.apache.log4j.Logger
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput

class AbstractMadcowStepTest extends BaseStepTestCase {

    AbstractMadcowStep step

    String html =
    """<html><body>
            <div id="search">
                <form action="http://www.google.de/search" id="searchForm">
                    <input type="hidden" name="sitesearch" value="webtest.canoo.com"><input type="submit" id="searchButton" name="go" value="Search" title="Search" alt="Search"><input type="text" id="searchText" name="q" onfocus="this.select();" title="Search">
                </form>
            </div>
       </body></html>"""

    VerifySelectFieldContains fStep

    protected void setUp() throws Exception {
        super.setUp()
        setCurrentPageHtml html
        step = createStep() as AbstractMadcowStep
    }

    void setCurrentPageHtml(String html){
        getContext().setDefaultResponse(html)
    }

    protected Step createStep() {
        return new GenericMadcowStep()
    }

    public void testFindElementOrNullWithRealElementByXPath() {
        HtmlElement element = step.findElementOrNull(null, "//*[@name='go']")
        assertNotNull element
        assert (element instanceof HtmlSubmitInput)
    }

    public void testFindElementOrNullWithRealElementByHtmlId() {
        HtmlElement element = step.findElementOrNull('searchButton', null)
        assertNotNull element
        assert (element instanceof HtmlSubmitInput)
    }

    public void testFindElementOrNullWithNonExistentElementByXPath() {
        assertNull step.findElementOrNull(null, "//*[@name='manBearPig']")
    }

    public void testFindElementOrNullWithNonExistentElementByHtmlId() {
        assertNull step.findElementOrNull('Blarghonauts', null)
    }

    //A simple concrete extension of the abstract class under test
    private class GenericMadcowStep extends AbstractMadcowStep {
        void doExecute() {}
        protected Logger getLog() {return null}
    }
}
