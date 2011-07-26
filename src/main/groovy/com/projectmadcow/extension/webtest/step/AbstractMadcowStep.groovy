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

import com.canoo.webtest.boundary.HtmlUnitBoundary
import com.canoo.webtest.extension.StoreElementAttribute
import com.canoo.webtest.steps.Step
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage
import org.apache.log4j.Logger

public abstract class AbstractMadcowStep extends Step {

    abstract protected Logger getLog()

    protected HtmlElement findElement(String htmlId, String xpath) {
        final HtmlPage currentResp = getContext().getCurrentHtmlResponse(this)
        StoreElementAttribute.findElement(currentResp, htmlId, xpath, getLog(), this)
    }

    //Same as findElement, but it doesn't throw an exception if the element can't be found
    protected HtmlElement findElementOrNull(String htmlId, String xpath) {
        final HtmlPage currentResp = getContext().getCurrentHtmlResponse(this)
        if (xpath) {
            return HtmlUnitBoundary.trySelectSingleNodeByXPath(xpath, currentResp, this) as HtmlElement
        } else {
            return currentResp.getAllHtmlChildElements().find { it.id == htmlId} as HtmlElement
        }
    }
}