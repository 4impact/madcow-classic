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

/**
Called from webTest.xml.
Generates the Project.dtd file from the content of the includes dir
*/

def templateText = '''<?xml version="1.0" encoding="ISO-8859-1" ?>

<!--
This file is generated automatically from the information contained in the folder includes.
Do not edit it else you risk to lose your changes.
-->

<% entities.each { 
%><!ENTITY ${it.key} SYSTEM "${it.value}">
<% } %>
'''
def readMandatoryAntProperty = {
propName ->
	def value = project.properties[propName]
	if (!value)
		throw new IllegalArgumentException("Bad configuration: required ant property '${propName}' not set!") 
	return value
}

def entitiesScanner = new org.codehaus.groovy.ant.FileScanner(project)
entitiesScanner.addFileset(project.getReference('wt.generateDtd.entities.fileset'))

def dtdFile = new File(readMandatoryAntProperty("wt.generateDtd.file"))
if (!dtdFile.parentFile.exists())
{
	println "Creating dir ${dtdFile.parentFile}" 
	dtdFile.parentFile.mkdirs()
}

def baseDirURL = dtdFile.parentFile.toURL().toExternalForm()
def computePath = {
it ->
	def currentPath = it.toURL().toExternalForm()
	def dtdDirPath = dtdFile.parentFile.toURL().toExternalForm()

	def i = 0
	def lastSlash = 0
	while (i < dtdDirPath.size() && i < currentPath.size())
	{
		if (dtdDirPath[i] != currentPath[i])
			break
		if (dtdDirPath[i] == '/')
			lastSlash = i
		++i
	}
	
	def nbUp = dtdDirPath.substring(i).count("/")
	return ('../' * nbUp) + currentPath.substring(lastSlash + 1)
}

//def baseDirURI = includesDir.toURI() as String
def entities = new TreeMap() // as TreeMap to have elements alphabetically sorted

println "Scanning for entities..."
entitiesScanner.each {
	def entityName = it.name.replaceFirst(/\..*/, '').replaceAll(/\W/, "__")
	entities[entityName] = computePath(it)
}

println "${entities.size()} entities found"

def binding = ["entities": entities]
def engine = new groovy.text.GStringTemplateEngine()
def template = engine.createTemplate(templateText)

def newDtd = template.make(binding) as String

// test if this would generate a new version 
// (this avoid to make file apear as modified for the SCM when it is not)
if (!dtdFile.exists() || newDtd != dtdFile.text)
{
	println "Generating ${dtdFile}"
	dtdFile.withWriter
	{
		it << newDtd
	}
}
else
{
	println "Already uptodate: ${dtdFile}"
}