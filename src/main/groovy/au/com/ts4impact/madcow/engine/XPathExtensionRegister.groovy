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

package au.com.ts4impact.madcow.engine

import au.com.ts4impact.madcow.extension.webtest.xpath.ReplaceAll

import au.com.ts4impact.madcow.extension.webtest.xpath.EndsWith
import au.com.ts4impact.madcow.extension.webtest.xpath.NumbersOnly
import com.canoo.webtest.engine.xpath.XPathHelper

/**
 * Class used to register the custom madcow XPath extensions,
 * to the WebTest XPathHelper.
 *
 * @author gbunney
 */
public class XPathExtensionRegister {

    /**
     * The namespace of the XPath extensions.
     */
    public static final String XPathNamespaceURI = "http://4impact.com.au"

    /**
     * Registers the extensions.
     */
    public static void registerExtensions() {

        // register the root namespace, so all functions are prefixed with madcow:
        XPathHelper.registerGlobalNamespace("madcow", XPathNamespaceURI)

        // register our madcow functions
        XPathHelper.registerGlobalFunction(XPathNamespaceURI, EndsWith.XPATH_FUNCTION_NAME, EndsWith.class)
        XPathHelper.registerGlobalFunction(XPathNamespaceURI, ReplaceAll.XPATH_FUNCTION_NAME, ReplaceAll.class)
        XPathHelper.registerGlobalFunction(XPathNamespaceURI, NumbersOnly.XPATH_FUNCTION_NAME, NumbersOnly.class)
    }
}