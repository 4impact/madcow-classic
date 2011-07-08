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

import org.apache.log4j.Logger
import com.gargoylesoftware.htmlunit.html.HtmlSelect
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import com.canoo.webtest.engine.StepFailedException

/**
 * This is a base class for steps that need to check the options in a select field.
 */
abstract class AbstractVerifySelectFieldStep extends AbstractMadcowStep {

    String htmlId
    String name
    String xpath
    String options

    abstract protected Logger getLog()

    public void doExecute() {

        if (name != null)
            xpath = "//select[@name='${name}']"

        HtmlSelect selectElement
        try {
            selectElement = this.findElement(htmlId, xpath) as HtmlSelect
        } catch (GroovyCastException gce) {
            throw new StepFailedException("Unable to find a SELECT element using " + htmlId ?: xpath)
        }

        List optionsList = Eval.me(options) as List
        checkOptionsList(optionsList, selectElement)
    }

    //This method does the actual verification of the select field options
    protected abstract def checkOptionsList(List optionsList, HtmlSelect selectElement)

    protected void verifyParameters() {
        nullParamCheck(options, "options")
        paramCheck(htmlId == null && xpath == null && name == null, "\"htmlId\" or \"name\" or \"xpath\" must be set!")
        paramCheck(htmlId != null && xpath != null && name != null, "Only one from \"htmlId\", \"name\" or \"xpath\" can be set!");
        paramCheck(htmlId != null && xpath != null, "Only one from \"htmlId\", \"name\" or \"xpath\" can be set!");
        paramCheck(xpath  != null && name  != null, "Only one from \"htmlId\", \"name\" or \"xpath\" can be set!");
        paramCheck(htmlId  != null && name  != null, "Only one from \"htmlId\", \"name\" or \"xpath\" can be set!");
    }
    
}
