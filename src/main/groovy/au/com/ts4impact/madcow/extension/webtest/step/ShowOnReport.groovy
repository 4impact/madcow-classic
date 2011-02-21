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

package au.com.ts4impact.madcow.extension.webtest.step

import com.canoo.webtest.engine.StepFailedException
import com.canoo.webtest.engine.xpath.XPathHelper
import com.canoo.webtest.interfaces.IComputeValue
import com.canoo.webtest.steps.Step
import com.gargoylesoftware.htmlunit.Page
import javax.xml.xpath.XPathException
import org.apache.log4j.Logger

/**
 * Shows the specified xpath expression text value on the report.
 *
 * @author gbunney
 */
public class ShowOnReport extends Step implements IComputeValue {

    private static final Logger LOG = Logger.getLogger(ShowOnReport.class);

    String xpath;
    String htmlId;

    // value is used to store the report key when displaying on the report
    String value;

    // reportValue is the value returned by the xpath
    protected String reportValue;

    // format to return the getComputedValue in
    String valueFormatString;

    /**
     * Step entry point.
     */
	public void doExecute() throws XPathException {

        if (htmlId ?: '' != '')
            xpath = "//*[@id='${htmlId}']/text()";

        reportValue = evaluateXPath();
	}

    /**
     * Evaluate the xpath given in the xpath variable, returning the result as its string contents
     */
    protected String evaluateXPath() throws XPathException {
		final Page currentResponse = getContext().getCurrentResponse();
		final XPathHelper xpathHelper = getContext().getXPathHelper();
		final String result = xpathHelper.stringValueOf(currentResponse, xpath);

		// seems that result is "" and not null when nothing is found
		if (result == null
			|| (result.length() == 0 && xpathHelper.selectFirst(currentResponse, xpath) == null)) {

			throw new StepFailedException("No match for xpath expression <" + xpath + ">", this);
		}

		LOG.debug("Xpath result: " + result);
		return result;
	}


    /**
     * Verify the required parameters for this step.
     */
	protected void verifyParameters() {
        nullParamCheck(value, "value");
        paramCheck(htmlId == null && xpath == null, "\"htmlId\" or \"xpath\" must be set!");
        paramCheck(htmlId != null && xpath != null, "Only one from \"htmlId\" and \"xpath\" can be set!");
	}

    /**
     * Required by the IComputeValue interface; used to return the value of this step in the value => part of the report.
     * If a valueFormatString is supplied, all matching instances of 'value' are replaced with 'reportValue'.
     */
    public String getComputedValue() {

        if ((valueFormatString ?: '') != '')
            return valueFormatString.replace(value, reportValue)
        else
		    return reportValue;
	}

    /**
     * Only a reporting step, so no action being performed
     * @return false
     */
    public boolean isPerformingAction() {
    	return false;
    }
}
