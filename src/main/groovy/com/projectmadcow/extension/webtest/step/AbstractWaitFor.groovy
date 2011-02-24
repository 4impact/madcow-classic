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

import com.canoo.webtest.engine.StepFailedException
import com.gargoylesoftware.htmlunit.html.HtmlElement

/**
 * @author chris
 */
public abstract class AbstractWaitFor extends AbstractMadcowStep {

    String htmlId
    String xpath
    String value
    String seconds
    String milliseconds

    abstract def List checkCondition(HtmlElement waitForElement)

    /**
     * Perform the step's actual work. The minimum you need to implement.
     *
     * @throws com.canoo.webtest.engine.StepFailedException
     *          if step was not successful
     */
    public void doExecute() {

        final boolean found = (1..Integer.parseInt(seconds ? seconds : milliseconds)).any {
            HtmlElement waitForElement = null

            try {
                waitForElement = findElement(htmlId, xpath)
            } catch (Exception ignore) {

            }

            def (result, message) = checkCondition(waitForElement)
            if (result) {
                getLog().info(message);
                return true;
            }

            Thread.sleep(seconds ? 1000 : 1)
            return false;
        }
        if (!found) {
            throw new StepFailedException("Value " + value + " did not appear within timeout for element id=" + htmlId + " or xpath=" + xpath, this);
        }
    }

    protected void verifyParameters() {
        super.verifyParameters();
        nullResponseCheck();
        paramCheck(htmlId == null && xpath == null, "\"htmlId\" or \"xPath\" must be set!");
        paramCheck(seconds == null && milliseconds == null, '"seconds" or "milliseconds" must be set!');
        paramCheck(seconds != null && milliseconds != null, '"seconds" and "milliseconds" cannot both be set!');
    }
}
