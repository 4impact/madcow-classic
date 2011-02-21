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


import com.canoo.webtest.engine.StepExecutionException
import com.canoo.webtest.steps.BaseStepTestCase
import com.canoo.webtest.steps.Step
import com.gargoylesoftware.htmlunit.html.HtmlPage

public class WaitSecondsTest extends BaseStepTestCase {

    WaitSeconds fStep

    protected Step createStep() {
        return new WaitSeconds()
    }

    protected void setUp() throws Exception {
        super.setUp()
        fStep = (WaitSeconds) getStep()
        setCurrentPageHtml "<html></html>"
    }

    void setCurrentPageHtml(String html){
        getContext().setDefaultResponse(html)
        final HtmlPage page = getContext().getCurrentHtmlResponse(fStep)
    }

    public void testWaitSecondsNoValue(){

        def expectedMessage = '"value" must be set'
        try {            
            fStep.execute()
            fail "expect StepExecutionError with message : $expectedMessage"
        } catch (StepExecutionException e){
            // success
            assert e.message.contains(expectedMessage)
        }
    }
    
    public void testWaitSeconds(){
        def waitInSeconds = 1        
        fStep.value = waitInSeconds
        def timeBeforeTest = System.currentTimeMillis()
        
        fStep.execute()
        
        def timeAfterTest = System.currentTimeMillis()        
        def timeTakenSeconds = (timeAfterTest - timeBeforeTest)                
        def waitTimeMilliseconds = waitInSeconds * 1000
        
        assert (timeTakenSeconds >= waitTimeMilliseconds && timeTakenSeconds <= (waitTimeMilliseconds + 350))
    }
}