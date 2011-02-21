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
import com.gargoylesoftware.htmlunit.html.HtmlPage
import org.apache.log4j.Logger
import org.codehaus.groovy.runtime.typehandling.GroovyCastException

/**
 * Executes a block of javascript against the current response's HTML Page.
 *
 * @author jneale
 */
public class ExecuteJavascript extends AbstractMadcowStep {
    
    private static final Logger LOG = Logger.getLogger(ExecuteJavascript.class)
    String javascript    

    public void doExecute() {
        try {
            final HtmlPage currentPage = this.context.currentResponse as HtmlPage
            currentPage.executeJavaScriptIfPossible(javascript, this.class.name, 1)
        } catch (GroovyCastException gce) {
            throw new StepFailedException("Unable to execute JavaScript on non-HTML Pages", this)
        }
    }    
    
    protected void verifyParameters() {       
        super.verifyParameters();   
        paramCheck(javascript == null, "\"javascript\" must be set!")
        
        nullResponseCheck();        
    }
    
    protected Logger getLog() {       
        return LOG;
    }    
}

