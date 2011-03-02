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
import com.projectmadcow.engine.GroovyMadcowTestCase

/**
 * Example Groovy test case.
 */
public class AddressTest extends GroovyMadcowTestCase {

    void testCreateAddress() {

        testInfo = "Create a new address and check created address results"

        def testData = [addressLine1 : '320 Adelaide St',
                        addressLine2 : "Brisbane CBD ${System.currentTimeMillis()}",
                        postcode     : '4000',
                        suburb : 'BRISBANE',
                        suburb_id : '8154',
                        state : 'Queensland']

        // -----------------------------
        // Create the address
        // -----------------------------
        invokeUrl = "TEST_SITE/address/create"

        address_create_addressLine1.value = testData.addressLine1
        address_create_addressLine2.value = testData.addressLine2
        address_create_postCode.value = testData.postcode

        // setting the postcode will show a list of radio buttons, we want 8154 - BRISBANE
        setRadioButton = [xpath : "//input[@id='${testData.suburb_id}']"]

        address_create_wirelessAccessPointDetected.selectCheckbox

        address_create_create.clickButton

        // -----------------------------
        // Verify created address
        // -----------------------------

        verifyText = ['text' : '(Address).*?(\\d+).*?(created)', 'regex' : true]

        address_show_addressLine1.checkValue = testData.addressLine1
        address_show_addressLine2.checkValue = testData.addressLine2
        address_show_postCode.checkValue = testData.postcode
        address_show_suburb.checkValue = testData.suburb
        address_show_state.checkValue = testData.state
        address_show_wirelessAccessPointDetected.checkValue = 'true'

        // store the id of the address in a runtime parameter named addressId
        address_show_id.store = 'addressId'

        // show the address id on the report
        showOnReport = [xpath: "madcow:numbers-only(//*[@id='addressId']/text())",
                        value: 'CreatedAddressNumber',
                        valueFormatString: '<a href="http://test-site.projectmadcow.com:8080/madcow-test-site/address/show/CreatedAddressNumber">View CreatedAddressNumber</a>']

        // load the newly created address by id
        invokeUrl = "TEST_SITE/address/show/#{addressId}"

        // check that the suburb is as expected for postcode 4000
        address_show_id.checkValue = '#{addressId}'
    }
}
