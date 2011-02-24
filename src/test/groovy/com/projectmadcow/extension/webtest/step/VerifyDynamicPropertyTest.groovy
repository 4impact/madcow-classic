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


import com.canoo.webtest.engine.StepFailedException
import com.canoo.webtest.steps.BaseStepTestCase
import com.canoo.webtest.steps.Step

public class VerifyDynamicPropertyTest extends BaseStepTestCase {

    VerifyDynamicProperty fStep

    protected Step createStep() {
        return new VerifyDynamicProperty()
    }


    protected void setUp() throws Exception {
        super.setUp()
        fStep = (VerifyDynamicProperty) getStep()
    }

    void storeDynamicProperty(def name, def value) {
        getContext().getWebtest().setDynamicProperty name, value
    }

    private void testVerifyDynamicProperty(def nameToStore, def valueToStore, def nameToCheck, def valueToCheck){
        // store a property
        storeDynamicProperty nameToStore, valueToStore

        // then check its value
        fStep.name = nameToCheck
        fStep.value = valueToCheck

        fStep.execute()
    }

    private void testVerifyDynamicPropertyExpectStepFailedException(
    def nameToStore, def valueToStore, def nameToCheck, def valueToCheck){

        try {
            testVerifyDynamicProperty nameToStore, valueToStore, nameToCheck, valueToCheck
            fail('expected exception StepFailedException')
        } catch (StepFailedException e){
            //success
        }
    }

    public void testVerifyDynamicProperty(){
        testVerifyDynamicProperty 'name', 'value', 'name', 'value'
    }

    public void testVerifyDynamicPropertyNotFound(){
        testVerifyDynamicPropertyExpectStepFailedException 'name', 'value', 'nameMissing', 'valueIrrelevant'
    }
    
    public void testVerifyDynamicValueDoesNotMatch(){
        testVerifyDynamicPropertyExpectStepFailedException 'name', 'value', 'name', 'valueDoesNotMatch'
    }
}
