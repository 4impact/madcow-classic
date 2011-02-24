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

package com.projectmadcow.extension.webtest.step;


import static com.googlecode.instinct.expect.Expect.expect as expectation

import com.canoo.webtest.steps.Step
import com.gargoylesoftware.htmlunit.ScriptResult
import com.gargoylesoftware.htmlunit.SgmlPage
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.javascript.host.Event

public class ExecuteEventTest extends AbstractXpathHtmlIdStepTest {

    ExecuteEvent fStep

    protected Step createStep() {
        return new ExecuteEvent(){
            def firedEvent
            protected HtmlElement findElement(String htmlId, String xpath) {

                final String html = "<html><body>" + "<form name='testForm'>" + "<label for='theFirstName'>First Name</label>: <input name='name' id='theFirstName' type='text'/>" + "<label for='theAddress'>Address</label>: <input name='address' id='theAddress' type='text'/>" + "</form>" + "</body></html>"
                getContext().setDefaultResponse(html)
                final HtmlPage page = getContext().getCurrentHtmlResponse(fStep)


                return new HtmlElement(null, "", (SgmlPage)page, null) {
                    public ScriptResult fireEvent(final Event event) {
                        firedEvent = event
                        return super.fireEvent(event)
                    }
                }
            }
        }
    }

    protected void setUp() throws Exception {
        super.setUp()
        fStep = (ExecuteEvent) getStep()
    }

    public void testExecuteEventWithNoData(){
        fStep.eventType = Event.TYPE_RESET
        fStep.htmlId = "dummyId"
        fStep.execute()

        expectation.that(fStep.firedEvent.jsxGet_type()).isEqualTo Event.TYPE_RESET
    }

    public void testExecuteEventWithData(){
        fStep.eventType = Event.TYPE_BLUR
        fStep.eventData = 15
        fStep.xpath = "dummyId"
        fStep.execute()

        expectation.that(fStep.firedEvent.jsxGet_type()).isEqualTo Event.TYPE_BLUR
        expectation.that(fStep.firedEvent.jsxGet_keyCode()).isEqualTo 15
    }

   
}
