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

package au.com.ts4impact.madcow.engine.tabular

import org.apache.log4j.Logger
import static fj.P.p
import org.apache.poi.ss.usermodel.*

/**
 * Spreadsheet File Runner for running Spreadsheet file tests.
 *
 * @author mcallon
 */
public class SpreadsheetTestRunner extends AbstractTabularTestRunner {

    private static final Logger LOG = Logger.getLogger(SpreadsheetTestRunner.class);

    protected String getTestNameProperty() {
        return 'madcow.test.spreadsheet'
    }

    protected String getTestFileExtension() {
        return '.xlsx'
    }

    protected Map parseFile(File file) {
        Workbook wb = WorkbookFactory.create(new FileInputStream(file))
        def sheetsWithLines = (0..(wb.getNumberOfSheets() - 1)).collect({sheetIndex ->

            Sheet sheet = wb.getSheetAt(sheetIndex)
            if (sheet.getLastRowNum() > 0) {
                final short sheetSize = sheet.getRow(0).getLastCellNum()
                p(sheet, convertSheetToListOfStringArrays(sheet.rowIterator(), sheetSize))
            } else {
                p(sheet, [])
            }

        }).findAll {!it._2().isEmpty()}

        Map tests = new HashMap();
        sheetsWithLines.each {sheetAndLines ->
            tests.putAll(TestGenerator.generateTests(sheetAndLines._2(), sheetAndLines._1().sheetName))
        }
        return tests
    }

    private List<String[]> convertSheetToListOfStringArrays(Iterator<Row> rowIterator, short sheetSize) {
        return rowIterator.collect {row ->
            LOG.trace("row ${row.rowNum} : $row")
            (0..sheetSize - 1).collect {i ->
                convertCellToString(row, i)
            }
        }
    }

    private String convertCellToString(row, i) {
        Cell cell = row.getCell(i)
        if (cell) {
            try {
                return cell.getStringCellValue()
            } catch (Exception e) {
                LOG.error "Unable to get string value from cell : $cell [Make sure all cells containing numbers are either preceded with a single quote \"'\" or have a cell format of type TEXT ", e
                throw e;
            }
        } else {
            return ''
        }
    }
}