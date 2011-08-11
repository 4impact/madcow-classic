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

package com.projectmadcow.plugins

import com.canoo.webtest.engine.StepExecutionException
import com.canoo.webtest.engine.StepFailedException
import com.canoo.webtest.self.ContextStub
import org.apache.tools.ant.BuildException
import org.apache.tools.ant.Project
import org.apache.tools.ant.Target
import org.apache.tools.ant.Task
import org.junit.Ignore

/**
 * Abstract base class for Plugin tests.
 */
@Ignore
public abstract class AbstractPluginTestCase extends GroovyTestCase {

    AntBuilder antBuilder
    Project antProject
    ContextStub contextStub
    Target containerTarget

    void setUp() {
        super.setUp()

        def antBuilderFactory = AntBuilder.class.getDeclaredMethod('createProject');
        antBuilderFactory.setAccessible(true);
        antProject = antBuilderFactory.invoke(null) as Project

        contextStub = new ContextStub()
        contextStub.webtest.project = antProject

        containerTarget = new Target()
        containerTarget.setProject(antProject)

        antBuilder = new AntBuilder(antProject, containerTarget)
        antBuilder.taskdef(resource: 'webtest.taskdef')
        antBuilder.taskdef(format: 'xml', resource: 'madcow.taskdef.xml')
    }

    protected Task findTask(String name) {
        return (containerTarget.tasks.findAll { task -> task.taskName == name } as Collection<Task>).asList().last()
    }

    protected String findAttribute(Task task, String name) {
        return task.runtimeConfigurableWrapper.attributeMap.get(name)
    }

    protected void assertUnsupportedAttribute(Closure pluginInvokation, String taskName, String attribute) {
        try {
            pluginInvokation.call()
            assert false // should always throw BuildException
        } catch (BuildException e) {
            assert e.message.contains("$taskName doesn't support the \"$attribute\" attribute")
        }
    }

    protected void assertStepExecutionException(Closure pluginInvocation, String errorMessage) {
        assertThrowsExceptionOfType(StepExecutionException, pluginInvocation, errorMessage)
    }

    protected void assertStepFailedException(Closure pluginInvocation, String errorMessage) {
        assertThrowsExceptionOfType(StepFailedException, pluginInvocation, errorMessage)
    }

    protected void assertRuntimeException(Closure pluginInvocation, String errorMessage) {
        assertThrowsExceptionOfType(RuntimeException, pluginInvocation, errorMessage)
    }

    private void assertThrowsExceptionOfType(Class exceptionClass, Closure pluginInvocation, String errorMessage) {
        try {
            pluginInvocation.call()
            fail "Expected to have exception of type ${exceptionClass.canonicalName} thrown. No exception was thrown."
        } catch (Exception e) {
            assert e.getClass() == exceptionClass
            assert e.message.contains(errorMessage)
        }
    }
}
