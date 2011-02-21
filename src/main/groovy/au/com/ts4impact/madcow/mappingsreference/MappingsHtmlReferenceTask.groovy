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

package au.com.ts4impact.madcow.mappingsreference

import au.com.ts4impact.madcow.engine.mappings.MappingsHelper
import groovy.text.GStringTemplateEngine
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import org.apache.tools.ant.Task
import org.apache.tools.ant.types.Path
import org.apache.tools.ant.util.ClasspathUtils
import org.springframework.core.io.Resource

/**
 * Ant Task to generate the Mappings HTML file.
 *
 * @author mcallon
 */
class MappingsHtmlReferenceTask extends Task {

    static final Logger LOG = Logger.getLogger(MappingsHtmlReferenceTask.class);

    def mappingsHelper = new MappingsHelper()
    File dir

    ClasspathUtils.Delegate cpDelegate

    public void init() {
        this.cpDelegate = ClasspathUtils.getDelegate(this)
        super.init()
    }

    public void execute() {

        def resources = mappingsHelper.getAllMappingsFromClasspath(cpDelegate.classLoader)
        def mappingsMap = [:]
        def menuMap = [:]
        resources.each { resource ->
            Properties properties = new Properties()
            properties.load(resource.getInputStream())
            mappingsHelper.applyMappingNamespace(resource, properties)
            appendTreeStructureToMappings(resource, properties, mappingsMap)
            appendTreeStructureToMappings(resource, properties, menuMap, true)
        }

        // generate the menu tree structure using the menu template
        def menuTemplateParams = [menuItems : generateMenuItems(menuMap) ]
        def menuTemplate = new File("${dir.canonicalPath}/menu.html")
        def templateEngine = new GStringTemplateEngine()
        def menuHtml = templateEngine.createTemplate(menuTemplate).make(menuTemplateParams)
        new File("${dir.canonicalPath}/menu.html").text = menuHtml.toString()

        walkTheMappingsTableStyle(mappingsMap)

        // process the about page template
        def aboutTemplate = new File("${dir.canonicalPath}/about.html")
        def aboutHtml = templateEngine.createTemplate(aboutTemplate).make()
        new File("${dir.canonicalPath}/about.html").text = aboutHtml.toString()

    }

    def appendTreeStructureToMappings(Resource resource, Properties properties, def mappings, boolean generateMenu = false) {

        // create initialised nested map structure
        properties.each { prop ->
            def key = removeMappingKeywords(prop.key)
            def keyTokens = key.tokenize('_')
            def keyBuilder
            keyTokens.each { token ->
                if (!keyBuilder){
                    keyBuilder = token
                }
                else {

                    if (Eval.x(mappings, "if (!(x.$keyBuilder instanceof Map)) return true else return false"))
                      return

                    keyBuilder += ".$token"
                }
                Eval.x mappings, "if (!x.$keyBuilder) x.$keyBuilder=[:]"
            }
        }

        // populate map structure using Eval resulting in the executation lines such as
        // mappings.testsite.create = testsite_create_button1
        properties.each { prop ->
            def key
            if (!generateMenu) {
                key = removeMappingKeywords(prop.key)
            } else {
                String cleanKey = removeMappingKeywords(prop.key)
                key = cleanKey.substring(0, cleanKey.lastIndexOf('_') > 0 ? cleanKey.lastIndexOf('_') : cleanKey.length())
            }

            def mapKey = key.replace('_', '.')
            def value = "${key}"

            if (!generateMenu)
                Eval.xy mappings, value, "x.$mapKey=y"
            else
                Eval.x mappings, "x.$mapKey='mappings/${key}.html'"
        }
    }

    def removeMappingKeywords(def key){
        key = key.replace('.xpath', '')
        key = key.replace('.htmlId', '')
        key = key.replace('.name', '')
        key = key.replace('.href', '')
        key
    }

    def generateMenuItems(def menuMap) {

        def list = []
        walkTheMappingsMenuStyle(menuMap, list)

        def html = ""
        list.each { line -> html += "$line \n" }
        return html
    }

    static String deCamelCase(String s) {

        String[] camelCaseSplit = StringUtils.splitByCharacterTypeCamelCase(s)
        String deCamel = ''
        camelCaseSplit.eachWithIndex { word, idx ->
            deCamel += idx == 0 ? StringUtils.capitalize(word) : " $word"
        }
        return deCamel
    }

    def walkTheMappingsMenuStyle(def mappings, def htmlList = [], def i = 1){

        mappings.each { key, value ->

            key = deCamelCase(key)

            if (value instanceof String || value instanceof GString){
                htmlList.add "{'$key' : '$value'},"
            } else {
                i++
                htmlList.add "{'$key' : ["
                walkTheMappingsMenuStyle(value, htmlList, i)
                htmlList.add "]},"
            }
            i--
        }
    }

    def walkTheMappingsTableStyle(def mappings, String filename = '', String tableOfContents = '', String body = '', def i = 1){
        boolean creatingNewFile = false

        mappings = mappings.sort()
        mappings.each { key, value ->

            if (value instanceof String || value instanceof GString) {

                String readableKey = deCamelCase(key)

                if (!creatingNewFile) {
                    creatingNewFile = true
                }

                tableOfContents += "<li><a href=\"#${key}\">$readableKey</a></li>\n"
                body += "<h2 id=\"${key}\">$readableKey</h2>\n"
                body += "<p>$value</p>"
            } else {
                i++
                filename += filename != '' ? "_$key" : key
                filename = walkTheMappingsTableStyle(value, filename, '', body, i)
            }
            i--
        }

        if (creatingNewFile) {

            // make a friendly breadcrumb esq title; testsite_create becomes Testsite >> Create
            def pageTitle = StringUtils.capitaliseAllWords(filename.replaceAll('_', ' &#187; '))

            def templateParams = [title : pageTitle,
                                  tableOfContents : tableOfContents,
                                  body : body]
            def template = new File("${dir.canonicalPath}/resources/template.html")
            def templateEngine = new GStringTemplateEngine()
            def templateHtml = templateEngine.createTemplate(template).make(templateParams)
            new File("${dir.canonicalPath}/mappings/${filename}.html").text = templateHtml
        }

        // chop off the last part of the filename, so if there are more maps in the parent it wont keep
        // appending their siblings
        def lastIdx = filename.lastIndexOf('_')
        filename = filename.substring(0, lastIdx > 0 ? lastIdx : 0 )
        return filename
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
