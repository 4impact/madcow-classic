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
import com.canoo.webtest.engine.StepFailedException

/**
 * This step verifies that all the supplied options appear in the specified select field.
 * This differs from VerifySelectFieldOptions in that it won't fail if the supplied list
 * doesn't contain ALL the options that are in the select field on the web page.
 */
class VerifySelectFieldContains extends AbstractVerifySelectFieldStep {

    private static Logger LOG = Logger.getLogger(VerifySelectFieldContains.class)

    protected Logger getLog() {
        return LOG
    }

    protected def checkOptionsList(List optionsList, HtmlSelect selectElement) {
        List missingInElement = optionsList.findAll { option ->
            selectElement.options.find { opt -> opt.asText() == option } == null
        }

        if ((missingInElement.size() != 0)) {
            throw new StepFailedException("Select field options do not match!\n\nMissing in Page: $missingInElement" , this)
        }
    }
}
