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
import org.apache.xpath.functions.Function2Args
import org.apache.xpath.objects.XBoolean
import org.apache.xpath.objects.XObject

/**
 * StartsWith is an extension for the webtest xpath functions.
 * It implements the XPath 2.0 starts-with function, to allow testing the
 * start of a string contents.
 *
 * It can then be used within all xpath expressions by calling
 * <code>madcow:starts-with(xpath, text)</code>
 *
 */
public class StartsWith extends Function2Args {

    /**
     * Syntax to use when invoking this function in xpath.
     */
    public static String XPATH_FUNCTION_NAME = "starts-with"

    /**
     * Performs the actual starts-with function; returns XPath Boolean.
     */
    public XObject execute(final XPathContext context) throws TransformerException {
        final String xpathQuery = getArg0().execute(context).xstr().toString();
        final String startsWithString = getArg1().execute(context).xstr().toString();

        // returns an xpath boolean, testing that the xpath query starts with the specified string
        return new XBoolean(xpathQuery.startsWith(startsWithString));
    }
}