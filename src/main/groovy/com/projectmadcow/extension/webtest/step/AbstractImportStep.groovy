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

import com.canoo.webtest.steps.Step
import org.apache.log4j.Logger

/**
 * Shows a friendly message on the report when importing files
 * for properties/spreadsheet tests.
 *
 * @author gbunney
 */
abstract public class AbstractImportStep extends Step {

    private static final Logger LOG = Logger.getLogger(AbstractImportStep.class);

    String value;
    boolean startOfImport;
    boolean endOfImport;

    /**
     * Retrieve the message to show on the console.
     */
    protected abstract String getMessage();

    /**
     * Step entry point.
     */
	public void doExecute() {
        LOG.info(this.getMessage())
	}

    /**
     * Verify the required parameters for this step.
     */
	protected void verifyParameters() {
        nullParamCheck(value, "value");
	}

    /**
     * Only a reporting step, so no action being performed
     * @return false
     */
    public boolean isPerformingAction() {
    	return false;
    }
}
