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

/*
Small script removing the variable parts in a WebTest report (like date, duration, ...) to allow comparison with a reference report
Call from WebTest root dir with:
groovy resources/build/cleanWebTestReport.groovy build/selftests/reports/WebTestReport.xml > build/selftests/reports/WebTestReport.cleaned.xml
*/

def currentReport = new File(args[0])

def WebTestHome = ""

def catchWebTestHome = ~/.*location="(.*?).selftests.tests.*/

def toDelete = ~/.*name="method" value="GET".*/
def toFix = ~/(duration|starttime|endtime)="[^"]*"/
def paramToFix = ~/parameter name="resultFilename" value="[^"]*"/                                                              

def failureResultPageFix = ~/(<failure message=.*resultFilename=&quot;)[^&]*(\.\w+&quot;.*)/
def i = 0

currentReport.eachLine 
{
line ->
	if (toDelete.matcher(line))
	{
		// ignore
	}
	else
	{
		def newLine = line
		if (!WebTestHome && catchWebTestHome.isCase(line))
		{
			def matcher = catchWebTestHome.matcher(line)
			matcher.find()
			WebTestHome = matcher.group(1)
//			println WebTestHome
		}
			
		newLine = newLine - WebTestHome
		newLine = toFix.matcher(newLine).replaceFirst(/$1="fixed for report comparison"/)
		newLine = paramToFix.matcher(newLine).replaceFirst(/parameter name="resultFilename" value="fixed"/)
		newLine = failureResultPageFix.matcher(newLine).replaceFirst('$1fixedForReportComparison$2')
		if (newLine)
			println newLine
	}
}
