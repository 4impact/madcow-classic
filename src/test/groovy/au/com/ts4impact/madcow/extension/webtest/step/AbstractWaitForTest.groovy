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

package au.com.ts4impact.madcow.extension.webtest.step;


import com.canoo.webtest.engine.StepFailedException
import org.junit.Ignore

@Ignore
public abstract class AbstractWaitForTest extends AbstractXpathHtmlIdStepTest {

    void setCurrentPageHtml(String html){
        getContext().setDefaultResponse(html)
    }

    public void testWaitFor(def theStep, def htmlId, def value = null, def milliseconds = 2){
        theStep.htmlId = htmlId
        theStep.value = value
        theStep.milliseconds = milliseconds
        theStep.execute()
    }

    public void testWaitForExpectException(def theStep, def htmlId, def value = null, def milliseconds = 2){
        try {
            testWaitFor theStep, htmlId, value, milliseconds
        } catch (StepFailedException e){
            // success
            assert e.message.contains("Value $value did not appear within timeout for element id")
        }
    }
}