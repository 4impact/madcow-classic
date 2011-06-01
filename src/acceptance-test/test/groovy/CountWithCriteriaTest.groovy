
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

public class CountWithCriteriaTest extends AbstractTestCase {


    void testCountCriteria(){
        // create an address to search for
        def addressLine1 = 'Identical Line 1 ' + System.currentTimeMillis()
        def addr1Line2 = 'New Farm ' + System.currentTimeMillis()
        sleep 1
        def addr2Line2 = 'New Farm ' + System.currentTimeMillis()
        createAddress([addressLine1 : addressLine1, addressLine2 : addr1Line2, postCode : '4005']){
            testsite_create_postCode4005NewFarm.setRadioButton
        }
        createAddress([addressLine1 : addressLine1, addressLine2 : addr2Line2, postCode : '4005']){
            testsite_create_postCode4005NewFarm.setRadioButton
        }

        invokeSearchAddressUrl()

        // identify addressLines element by the html name value in a mapping file
        testsite_search_addressLines.value = addressLine1

        // use xpath in the mapping file to identify the search button
        testsite_search_addressSearchButton.clickButton

        // now use count rows plugin to verify there is only one unique address found.
        searchResults.table.countRows.equals = 2

        searchResults.table.countRows(['Address Line 2' : addr1Line2]).equals = 1


        searchResults.table.headerColumnCount.equals = 6

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
