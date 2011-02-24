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
package com.projectmadcow.extension.webtest.step

import com.projectmadcow.database.DatabaseHelper
import com.projectmadcow.database.DatabaseLoadTask
import org.apache.log4j.Logger

/**
 * ExecuteSql
 *
 * @author mcallon
 * 06/11/2009 3:02:26 PM
 *
 */
public class ExecuteSql  extends AbstractMadcowStep {
    
    private static final Logger LOG = Logger.getLogger(ExecuteJavascript.class);
    String sql    
    
    /**
     * Perform the step's actual work. The minimum you need to implement.
     *
     * @throws com.canoo.webtest.engine.StepFailedException
     *          if step was not successful
     */
    
    public void doExecute() {
         new DatabaseHelper(DatabaseLoadTask.DEFAULT_DATABASE_CONFIG_FILE_NAME).executeSql(sql)        
    }    
    
    protected void verifyParameters() {       
        super.verifyParameters();        
        nullResponseCheck();        
    }
    
    protected Logger getLog() {       
        return LOG;
    }    
}