
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

public class AddressTest extends AbstractTestCase {

    void testCreateAddress() {
        testInfo = "Create Address"
        createAddress([addressLine1 : '320 Adelaide St', addressLine2 : 'Brisbane CBD' + System.currentTimeMillis(), postCode : '4000', wirelessAccessPointDetected : true]){
            // select radio button
            testsite_create_postCode4000Brisbane.setRadioButton  // mapped from id = 8154 to postCode400Brisbane
        }

        testsite_create_addressId.store = 'addressId'

        // show the address id on the report
        showOnReport = [xpath: "madcow:numbers-only(//*[@id='addressId']/text())",
                        value: 'CreatedAddressNumber',
                        valueFormatString: '<a href="http://50.16.241.31:8080/madcow-test-site/address/show/CreatedAddressNumber">View CreatedAddressNumber</a>']

        // load the newly created address by id
        invokeShowAddressUrl('#{addressId}')

        // check that the suburb is as expected for postcode 4000
        suburb.checkValue = 'BRISBANE'

        // show the suburb on the report
        suburb.showOnReport = 'SelectedSuburb'
    }

    def createAddress(def addressValues, Closure setSuburb = {} ) {
        invokeCreateAddressUrl()
        setValues addressValues, setSuburb
        create.clickButton

        // verify regular expression text exists on the page
        verifyText = ['text' : '(Address).*?(\\d+).*?(created)', 'regex' : true]
        checkValues addressValues
    }

    def setValues(values, Closure setSuburb = {} ) {
        // set values using convention of html id to identify the html elements.
        addressLine1.value = values.addressLine1
        addressLine2.value = values.addressLine2
        postCodeEntry.value = values.postCode

        // example of select and unselect checkbox
        values.wirelessAccessPointDetected ? wirelessAccessPointDetected.selectCheckbox : wirelessAccessPointDetected.unselectCheckbox

        setSuburb()
    }

    def checkValues(values) {
        addressLine1.checkValue = values.addressLine1
        addressLine2.checkValue = values.addressLine2
        postCode.checkValue = values.postCode
        wirelessAccessPointDetected.checkValue = "${values.wirelessAccessPointDetected ? true : false}"
    }
}
