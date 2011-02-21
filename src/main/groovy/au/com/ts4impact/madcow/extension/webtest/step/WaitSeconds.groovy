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

/**
 * 
 */
package au.com.ts4impact.madcow.extension.webtest.step

import org.apache.log4j.Logger

/**
 * WaitSeconds
 *
 * @author mcallon
 * 25/09/2009 2:26:07 PM
 *
 */
public class WaitSeconds extends AbstractMadcowStep {

    private static final Logger LOG = Logger.getLogger(WaitSeconds.class);
        
    String value
    
    public void doExecute() {
        LOG.info "WaitSeconds.doExecute value : " + value
        sleep(Integer.parseInt(value) * 1000)
    }    

    protected Logger getLog() {
      return LOG;
    }
    
    protected void verifyParameters() {      
        nullResponseCheck();
        paramCheck(value == null, "\"value\" must be set!");
    }
    
    /**
     * Called by Ant to set the text nested between opening and closing tags.
     * @param text the text to set
     * @webtest.nested.parameter
     *    required="no"
     *    description="Alternative way to set the 'text' attribute."
     */
    public void addText(final String text) {
        value = text;   
     }
}
