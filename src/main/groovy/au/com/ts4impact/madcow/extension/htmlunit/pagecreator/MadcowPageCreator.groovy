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

package au.com.ts4impact.madcow.extension.htmlunit.pagecreator

import au.com.ts4impact.madcow.pagetransformers.PageTransformer
import au.com.ts4impact.madcow.pagetransformers.PageTransformerResolver

import com.gargoylesoftware.htmlunit.DefaultPageCreator
import com.gargoylesoftware.htmlunit.Page
import com.gargoylesoftware.htmlunit.WebResponse
import com.gargoylesoftware.htmlunit.WebWindow
import org.apache.log4j.Logger

/**
 * Base Madcow Page Creator.
 *
 * @author tfarr
 */
public class MadcowPageCreator extends DefaultPageCreator {

    private static final Logger LOG = Logger.getLogger(MadcowPageCreator.class);

    List<PageTransformer> pageTransformers = new ArrayList()

    public MadcowPageCreator() {
        pageTransformers = PageTransformerResolver.loadPageTransformers()
    }

    public Page createPage(WebResponse webResponse, WebWindow webWindow) {
        Page page = super.createPage(webResponse, webWindow)
        pageTransformers.each { PageTransformer transformer ->
            page = transformer.transformPage(page)
        }
        LOG.debug "Transformed page : $page"
        return page;
    }

}