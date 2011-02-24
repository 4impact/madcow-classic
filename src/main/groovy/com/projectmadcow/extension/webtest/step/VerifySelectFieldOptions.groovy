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
import com.gargoylesoftware.htmlunit.html.HtmlSelect
import org.apache.log4j.Logger
import org.codehaus.groovy.runtime.typehandling.GroovyCastException

/**
 * Verify that the list of selected options exist for the specified
 * select field.
 *
 * @author gbunney
 */
class VerifySelectFieldOptions extends AbstractMadcowStep {

    private static Logger LOG = Logger.getLogger(VerifySelectFieldOptions.class)

    String htmlId
    String name
    String xpath
    String options

    protected Logger getLog() {
        return LOG
    }

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
        List missingInElement = optionsList.findAll { option ->
            selectElement.options.find { opt -> opt.asText() == option } == null
        }

        List missingInOptions = selectElement.options.findAll { option ->
            !optionsList.contains(option.asText())
        }*.asText()

        if ((missingInElement.size() != 0) || (missingInOptions.size() != 0))
        {
            String errorMessage = "Select field options do not match!"
            if (missingInElement.size() != 0)
                errorMessage += "\n\nMissing in Page: $missingInElement"

            if (missingInOptions.size() != 0)
                errorMessage += "\n\nMissing in Test: $missingInOptions"

            throw new StepFailedException(errorMessage, this)
        }
    }

    protected void verifyParameters() {
        nullParamCheck(options, "options")
        paramCheck(htmlId == null && xpath == null && name == null, "\"htmlId\" or \"name\" or \"xpath\" must be set!")
        paramCheck(htmlId != null && xpath != null && name != null, "Only one from \"htmlId\", \"name\" or \"xpath\" can be set!");
        paramCheck(htmlId != null && xpath != null, "Only one from \"htmlId\", \"name\" or \"xpath\" can be set!");
        paramCheck(xpath  != null && name  != null, "Only one from \"htmlId\", \"name\" or \"xpath\" can be set!");
        paramCheck(htmlId  != null && name  != null, "Only one from \"htmlId\", \"name\" or \"xpath\" can be set!");
    }
}
