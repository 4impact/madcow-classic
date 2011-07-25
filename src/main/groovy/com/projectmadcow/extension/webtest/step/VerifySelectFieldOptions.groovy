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

/**
 * Verify that the list of selected options exist for the specified
 * select field.
 */
class VerifySelectFieldOptions extends AbstractVerifySelectFieldStep {

    private static Logger LOG = Logger.getLogger(VerifySelectFieldOptions.class)

    protected Logger getLog() {
        return LOG
    }

    protected def checkOptionsList(List optionsList, HtmlSelect selectElement) {
        List missingInOptions = selectElement.options*.asText()
        optionsList.each{missingInOptions.remove(it)}

        List missingInElement = optionsList.clone() as List
        selectElement.options*.asText().each{ missingInElement.remove(it)}

        if ((missingInElement.size() != 0) || (missingInOptions.size() != 0)) {
            String errorMessage = "Select field options do not match!"
            if (missingInElement.size() != 0)
                errorMessage += "\n\nMissing in Page: $missingInElement"

            if (missingInOptions.size() != 0)
                errorMessage += "\n\nMissing in Test: $missingInOptions"

            throw new StepFailedException(errorMessage, this)
        }
    }
}
