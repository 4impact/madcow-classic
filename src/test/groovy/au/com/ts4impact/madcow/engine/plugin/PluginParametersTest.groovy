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

package au.com.ts4impact.madcow.engine.plugin

/**
 * @author mcallon
 */
class PluginParametersTest extends GroovyTestCase {

    PluginParameters pluginParameters = new PluginParameters()


    void testGetPluginParametersForElementLevelPluginWithValue() {

        def pluginName = 'thePlugin'
        def elementReference = 'theReference'
        def value = 'theValue'

        def results = pluginParameters.getPluginParameters(pluginName, elementReference, value)
        assertEquals results.htmlId, elementReference
        assertEquals results.value, value
        assertEquals results.description, "$elementReference.$pluginName=$value".toString()

    }


    void testGetPluginParametersForElementLevelPluginWithNoValue() {
        def pluginName = 'thePlugin'
        def elementReference = 'theReference'

        def results = pluginParameters.getPluginParameters(pluginName, elementReference)
        assertEquals results.htmlId, elementReference
        assertNull results.value
        assertEquals results.description, "$elementReference.$pluginName".toString()
    }

    void testGetPluginParametersForPageLevelPluginWithValue() {

        def pluginName = 'thePlugin'
        def value = 'theValue'

        def results = pluginParameters.getPluginParameters(pluginName, null, value)
        assertNull results.htmlId
        assertEquals results.value, value
        assertEquals results.description, "$pluginName=$value".toString()
    }

    void testGetPluginParametersForPageLevelPluginWithNoValue() {

        def pluginName = 'thePlugin'

        def results = pluginParameters.getPluginParameters(pluginName, null)
        assertNull results.htmlId
        assertNull results.value
        assertEquals results.description, "$pluginName".toString()
    }


    void testAddKeyValuePairToPluginParametersWhereValueIsAList(){
        def value = ['one', 'two', 'three']
        def elementReference = 'theReference'
        def results = ["$elementReference" : 'this should be overridden', 'anotherItem' : 'anotherValue' ]

        pluginParameters.addKeyValuePairToPluginParameters results, value, elementReference

        assertEquals results."$elementReference", "['one','two','three',]"
        assertEquals results.anotherItem, 'anotherValue'
    }

    void testAddKeyValuePairToPluginParametersWhereValueIsMap(){
        def value = ['one' : 'valueOne', 'two' : 'valueTwo']
        def elementReference = 'theReference'
        def results = ["$elementReference" : elementReference, 'anotherItem' : 'anotherValue' ]

        pluginParameters.addKeyValuePairToPluginParameters results, value, elementReference

        assertEquals results.one, 'valueOne'
        assertEquals results.anotherItem, 'anotherValue'
    }
    
     void testAddKeyValuePairToPluginParametersWhereValueIsAString(){
        def value = 'theValue'
        def elementReference = 'theReference'
        def results = ["$elementReference" : elementReference, 'anotherItem' : 'anotherValue' ]

        pluginParameters.addKeyValuePairToPluginParameters results, value, elementReference

        assertEquals results."$elementReference", value
        assertEquals results.anotherItem, 'anotherValue'
    }

}
