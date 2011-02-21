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
Generates the definitions.xml file from the content of the definitions dir
*/

def templateText = '''<?xml version="1.0"?>

<!--
This file is generated automatically from the information contained in the folder definitions.
Do not edit it else you risk to lose your changes.
-->

<!DOCTYPE project SYSTEM "dtd/Project.dtd"
[
<% entities.each { 
%> <!ENTITY ${it.key} SYSTEM "${it.value}">
<% } %>
]
>

<project name="WebTest-projectDefinitions" basedir="." default="wt.nothing">

	<!-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->
	<target name="wt.defineMacros" description="Defines macros and project specific Steps" unless="macroDefined">
		<property name="macroDefined" value="true"/>
		<echo message="Defining project macros and custom steps (if any)"/>

<% entities.each { 
%> &${it.key}; 
<% } %>
	</target>
	
	<target name="wt.nothing">
		
	</target>

	
</project>
'''


def definitionsDir = new File(properties["wt.generateDefinitions.dir"]) // TODO: use a fileset
if (!definitionsDir.exists())
{
	println "Definitions dir not found: ${definitionsDir}. Ignoring."
	return
}
def definitionsFile = new File(properties["wt.generateDefinitions.file"])

def baseDirURI = definitionsDir.parentFile.toURI() as String
def entities = new TreeMap() // as TreeMap to have elements alphabetically sorted

println "Scanning ${definitionsDir} for definitions..."
definitionsDir.eachFileRecurse
{
	if (it.file && it.name ==~ /.*\.xml/)
	{
		def relPath = it.toURI().toString() - baseDirURI
		def entityName = relPath.replaceAll(/\W/, "__")
		entities[entityName] = relPath
	}
}
println "${entities.size()} definitions found"

def binding = ["entities": entities]
def engine = new groovy.text.GStringTemplateEngine()
def template = engine.createTemplate(templateText)

def newDefinitions = template.make(binding) as String

// test if this would generate a new version
if (!definitionsFile.exists() || newDefinitions != definitionsFile.text)
{
	println "Generating ${definitionsFile}"
	definitionsFile.withWriter
	{
		it << newDefinitions
	}
}
else
{
	println "Already uptodate: ${definitionsFile}"
}