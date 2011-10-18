
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

            // now wait for the suburb radio buttons to appear and in particular the first one (BRISBANE) to be selected
            // TODO MWC - this doesn't seem to work for some reason WebTest renders the radio button unselected!
            // postCode400Brisbane.waitForRadioButtonSelected  // mapped from id = 8154 to postCode400Brisbane

            // select radio button
            testsite_create_postCode4000Brisbane.setRadioButton  // mapped from id = 8154 to postCode400Brisbane
        }

        // get the id of the newly created address

        // JIRA  MADCOW-174
        storeXPath = [property : 'addressId', xpath : "madcow:numbers-only(//*[@id='addressId']/text())"]
        storeXPath = [property : 'addressIdFormatted', xpath : "//*[@id='addressId']"]
        testsite_create_addressId.store = 'addressId'
        testsite_create_addressIdFormatted.store = 'addressIdFormatted'




        // show the address id on the report
        showOnReport = [xpath: "madcow:numbers-only(//*[@id='addressId']/text())",
                        value: 'CreatedAddressNumber',
                        valueFormatString: '<a href="http://50.16.241.31:8080/madcow-test-site/address/show/CreatedAddressNumber">View CreatedAddressNumber</a>']

        // load the newly created address by id
        invokeShowAddressUrl('#{addressId}')

        // check that the retrieved address id matches
        addressId.checkValue = '#{addressIdFormatted}'

        // check that the suburb is as expected for postcode 4000
        suburb.checkValue = 'BRISBANE'
        suburb.checkValueContains = 'RIS'

        // show the suburb on the report
        suburb.showOnReport = 'SelectedSuburb'
    }

    void testSearchAddress(){
        // create an address to search for
        def addressLine2 = 'New Farm ' + System.currentTimeMillis()
        createAddress([addressLine1 : '320 Adelaide St', addressLine2 : addressLine2, postCode : '4005']){
            testsite_create_postCode4005NewFarm.setRadioButton
        }

        invokeSearchAddressUrl()

        // identify addressLines element by the html name value in a mapping file
        testsite_search_addressLines.value = addressLine2

        // use xpath in the mapping file to identify the search button
        testsite_search_addressSearchButton.clickButton

        // now use count rows plugin to verify there is only one unique address found.
        searchResults.table.countRows.equals = 1


    }

    void testSearchTableLayoutAddress(){

        invokeUrl = "TEST_SITE/address/searchTableLayout"

        // use the table.selectRow command to select the first row
        searchTable.table.selectRow = 'first'

        searchTable.table.currentRow.verifySelectFieldOptions = ['State' : ['Select One...',
            'Australian Capital Territories',
            'New South Wales',
            'Northern Territory',
            'Queensland',
            'South Australia',
            'Tasmania',
            'Victoria',
            'Western Australia']]

        // select the Queensland field within the state column for the current row
        searchTable.table.currentRow.selectField = ['State' : 'Queensland']

        // use xpath in the mapping file to identify the search button
        testsite_search_addressSearchButton.clickButton

        // now use count rows plugin to verify there is only one unique address found.
        searchResults.table.countRows.greaterThanOrEquals = 1
    }

    void test4impactLinkInSearchPage(){
        invokeSearchAddressUrl()

        // use href in mapping file to idendity 4impact link
        testsite_search_searchPage4impactLink.clickLink

        // check 4impact site has opened
        verifyTitle = 'People, Projects, Technology - 4impact Group'
    }

    void testSearchByStateUsingTextValue(){
        invokeSearchAddressUrl()

        // select from combo box with id 'state' the text value 'QLD'
        state.selectField = [text : 'Queensland']

        // submit the form with id 'searchForm'
        testsite_search_searchForm.submitForm

        // ensure the search results are greater than 1
        searchResults.table.countRows.greaterThanOrEquals = 1

        // TODO : TABLE - now select a row in the table (currently hardcoded to ignore the incoming args, but
        // this will be implemented to work correctly soon and some more test logic can be
        // written below
        searchResults.table.selectRow = ['State' : 'Queensland', 'Address Line 1' : '320 Adelaide St']

        // able to check either one or many values for the currently selected row
        searchResults.table.currentRow.checkValue = ['State' : 'Queensland']
        searchResults.table.currentRow.checkValue = ['State' : 'Queensland', 'Address Line 1' : '320 Adelaide St']

        searchResults.table.currentRow.clickLink = "Id"
    }

    void testSearchByStateClickIdLinkInLastRow() {
        // test selecting the last row in the table
        invokeSearchAddressUrl()
        state.selectField = [text : 'Queensland']
        testsite_search_searchForm.submitForm
        searchResults.table.selectRow = "last"
        searchResults.table.currentRow.clickLink = "Id"
    }

    void testSearchByStateClickLinkInLastRowFirstColumn() {
        // test invoking a plugin on the "lastColumn"
        invokeSearchAddressUrl()
        state.selectField = [text : 'Queensland']
        testsite_search_searchForm.submitForm
        searchResults.table.selectRow = "last"
        searchResults.table.currentRow.clickLink = "firstColumn"
    }

    void testSearchByStateCheckValueInFirstRowLastColumn() {
        // test invoking a plugin on the "lastColumn"
        invokeSearchAddressUrl()
        state.selectField = [text : 'Queensland']
        testsite_search_searchForm.submitForm
        searchResults.table.selectRow = 'first'
        searchResults.table.currentRow.checkValue = ['lastColumn' : '4000']
    }

    void testSearchByStateUsingOptionValue(){
        invokeSearchAddressUrl()

        state.verifySelectFieldOptions = ['Select One...',
                                          'Australian Capital Territories',
                                          'New South Wales',
                                          'Northern Territory',
                                          'Queensland',
                                          'South Australia',
                                          'Tasmania',
                                          'Victoria',
                                          'Western Australia']

        // select from combo box with id 'state' the text value 'Queensland'
        state.selectField = 'Queensland'

        // submit the form with id 'searchForm'
        testsite_search_searchForm.submitForm

        // ensure the search results are greater than 1
        searchResults.table.countRows.greaterThanOrEquals = 1

        // click the first row in the search results table using the table click link
        searchResults.table.selectRow = "first"
        searchResults.table.currentRow.clickLink = "Id"
        //searchResults.clickTableLink = [row : "1", column : "1"]

        // now check that we are on the show address page
        verifyText = 'Show Address'
        verifyTextIsNot = 'Show Addresses'

        // and check that the state value is infact 'QLD'
        state.checkValue = 'Queensland'
    }

    void createAddressWithinInputInATable(def wirelessAccessPointDetected = false) {

        def addressValues = [
            'addressLine1' : 'Sydney Opera House',
            'addressLine2' : 'GPO Box R239 Royal Exchange',
            'postCode' : '1225']

        addressValues.wirelessAccessPointDetected = wirelessAccessPointDetected

        invokeUrl = "TEST_SITE/address/createTableLayout"

        createTable.table.selectRow = 'first'

        // test the value for a single column being set for the current row
        createTable.table.currentRow.value = ['Address Line 1' : addressValues.addressLine1]

        // test the value for a multiple columns being set for the current row
        createTable.table.currentRow.value = [
            'Address Line 2' : addressValues.addressLine2,
            'Post Code' : addressValues.postCode]

        createTable.table.currentRow.setRadioButton = ['Suburb' : 'ROYAL EXCHANGE']

        if (addressValues.wirelessAccessPointDetected){
            createTable.table.currentRow.selectCheckbox = 'Wireless Access Point Detected'
        }
        else {
            createTable.table.currentRow.unselectCheckbox = 'Wireless Access Point Detected'
        }

        createTableLayout.clickButton

        // verify regular expression text exists on the page
        verifyText = ['text' : '(Address).*?(\\d+).*?(created)', 'regex' : true]
        checkValues addressValues
    }

    void testCreateAddressWAPDetectedWithinInputInATable() {
        createAddressWithinInputInATable(true)
    }

    void testCreateAddressWithinInputInATable() {
        createAddressWithinInputInATable()
    }
}
