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

package au.com.ts4impact.madcow.extension.webtest.xpath

import javax.xml.transform.TransformerException
import org.apache.xpath.XPathContext
import org.apache.xpath.functions.Function3Args
import org.apache.xpath.objects.XObject
import org.apache.xpath.objects.XString

/**
 * ReplaceAll is an extension for the webtest xpath functions.
 * It implements the ability to do a Java'esqe replaceAll on a xpath string.
 *
 * It can then be used within all xpath expressions by calling
 * <code>madcow:replace-all(xpath, matchingRegex, replaceWith)</code>
 *
 * @author gbunney
 */
public class ReplaceAll extends Function3Args {

    /**
     * Syntax to use when invoking this function in xpath.
     */
    public static String XPATH_FUNCTION_NAME = "replace-all"

    /**
     * Performs the actual replace-all function; returns an XString.
     */
    public XObject execute(final XPathContext context) throws TransformerException {
        final String xpathQuery = getArg0().execute(context).xstr().toString()
        final String matchingRegex = getArg1().execute(context).xstr().toString()
        final String replaceWithString = getArg2().execute(context).xstr().toString()

        return new XString(xpathQuery.replaceAll(matchingRegex, replaceWithString))
    }
}