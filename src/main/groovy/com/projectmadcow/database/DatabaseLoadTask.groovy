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

package com.projectmadcow.database

import org.apache.log4j.Logger
import org.apache.tools.ant.BuildException
import org.apache.tools.ant.Task
import org.apache.tools.ant.types.Path
import org.apache.tools.ant.util.ClasspathUtils

/**
 * DatabaseLoadTask
 *
 * @author mcallon
 */
public class DatabaseLoadTask extends Task {

    static final Logger LOG = Logger.getLogger(DatabaseLoadTask.class)

    private ClasspathUtils.Delegate cpDelegate

    public void init() {
        this.cpDelegate = ClasspathUtils.getDelegate(this)
        super.init()
    }
    
    public void execute() throws BuildException {
        new DatabaseHelper().loadXmlData(cpDelegate.getClassLoader())
    }

    public void setClasspathRef(Reference r) {
        this.cpDelegate.setClasspathRef(r)
    }

    public Path createClasspath() {
        return this.cpDelegate.createClasspath()
    }

    public void setClassname(String fqcn) {
        this.cpDelegate.setClassname(fqcn)
    }    
}
