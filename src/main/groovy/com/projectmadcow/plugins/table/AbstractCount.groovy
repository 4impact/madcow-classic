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

package com.projectmadcow.plugins.table

/**
 * Base abstract class for table count functions.
 *
 * @author chris
 */
public abstract class AbstractCount {

    AntBuilder antBuilder
    String prefixXPath
    String descriptionPrefix

    def AbstractCount(prefixXPath, antBuilder, descriptionPrefix) {
        this.prefixXPath = prefixXPath
        this.antBuilder = antBuilder
        this.descriptionPrefix = descriptionPrefix
    }

    def setEquals(value) {
        doCount("=", value, "${descriptionPrefix}.equals=$value")
    }

    def setGreaterThan(value) {
        doCount(">", value, "${descriptionPrefix}.greaterThan=$value")
    }

    def setGreaterThanOrEquals(value) {
        doCount(">=", value, "${descriptionPrefix}.greaterThanOrEquals=$value")
    }

    def setLessThan(value) {
        doCount("<", value, "${descriptionPrefix}.lessThan=$value")
    }

    def setLessThanOrEquals(value) {
        doCount("<=", value, "${descriptionPrefix}.lessThanOrEquals=$value")
    }

    abstract def doCount(operator, value, description)
}