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

package au.com.ts4impact.madcow.pagetransformers

import org.apache.commons.io.IOUtils
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger

/**
 * Page Transformer Resolver to retrieve the page transformers
 *
 * @author tfarr
 */
public class PageTransformerResolver {

    private static final Logger LOG = Logger.getLogger(PageTransformerResolver.class);

    static boolean shouldIgnore(def transformerClassName){
        StringUtils.isWhitespace(transformerClassName) || transformerClassName.startsWith('#')
    }
    
    public static List loadPageTransformers() {
        List<PageTransformer> pageTransformers = new ArrayList()
        getTransformerClassNames().each {String transformerClassName ->
            PageTransformer transformer = createInstanceOfTransformer(transformerClassName)
            if (transformer) { pageTransformers.add(transformer) }
        }
        return pageTransformers
    }

    protected static Set getTransformerClassNames() {
        Set transformerClasses = [] as Set
        GroovyClassLoader loader = new GroovyClassLoader(PageTransformerResolver.getClassLoader())
        Enumeration<URL> configFiles = loader.getResources("madcow.pagetransformers.properties")
        configFiles.each {URL url ->
            LOG.debug("Loading transformer names from file ${url.toString()}")
            transformerClasses.addAll(parsePageTransformerConfigFile(url))
        }
        return transformerClasses
    }
    
    static def parsePageTransformerConfigFile(URL url){
        def allLines = IOUtils.readLines(url.openStream())
        
        return allLines.findAll{ !shouldIgnore(it) }
    }

    protected static PageTransformer createInstanceOfTransformer(String transformerClassName) {
        try {
            LOG.debug "Attempting to create an instance of ${transformerClassName}"
            return (PageTransformer) Class.forName(transformerClassName).newInstance()
        } catch (ClassNotFoundException e) {
            LOG.error "Could not create instance of transformer class ${transformerClassName} - class not found."
            System.exit(1)
        } catch (ClassCastException e) {
            LOG.error "Could not create instance of transformer class ${transformerClassName} - class does not" +
                    " implement interface au.com.ts4impact.madcow.pagetransformers.PageTransformer"
            System.exit(1)
        }
        return null
    }

}