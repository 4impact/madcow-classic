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

package com.projectmadcow.extension.webtest.xpath

import javax.xml.transform.TransformerException
import org.apache.xpath.XPathContext
import org.apache.xpath.functions.FunctionOneArg
import org.apache.xpath.objects.XObject
import org.apache.xpath.objects.XString

/**
 * NumbersOnly is an extension for the webtest xpath functions.
 * It will strip all non-numeric characters from the supplied xpath string.
 *
 * It can then be used within all xpath expressions by calling
 * <code>madcow:numbers-only(xpath)</code>
 *
 * @author gbunney
 */
public class NumbersOnly extends FunctionOneArg {

    /**
     * Syntax to use when invoking this function in xpath.
     */
    public static String XPATH_FUNCTION_NAME = "numbers-only"

    /**
     * Performs the actual numbers-only function; returns XString.
     */
    public XObject execute(final XPathContext context) throws TransformerException {
        final String xpathQuery = getArg0().execute(context).xstr().toString()

        // magical regex to only keep numberic characters
        return new XString(xpathQuery.replaceAll('[^\\d]', ''))
    }
}