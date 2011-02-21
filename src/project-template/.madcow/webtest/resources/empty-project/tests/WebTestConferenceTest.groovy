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

import com.canoo.webtest.WebtestCase

/**
The same tests as in webTestConference.groovy but expressed as Groovy code
*/
public class WebTestConferenceTest extends WebtestCase
{

	void testRegisterCommittersToWebtestConference()
	{
		webtest("[should fail!] Example of WebTest usage in TDD for a not yet existing website [test written as Groovy code]")
		{
			invoke "http://conference.webtest.canoo.com", description: "Go to the conference site"
			verifyTitle "WebTest Conference"
			verifyText "The most effective way to test your web application"

			clickLink "Register..."

			group description: "Register WebTest committers for the conference (aren't they already speakers?)",
			{
				setInputField forLabel: "Number of participant",  "4"
				clickButton "continue"
				fillParticipantData firstName: "Denis", lastName: "Antonioli"
				fillParticipantData firstName: "Dierk", lastName: "K�nig", index: "2"
				fillParticipantData firstName: "Marc", lastName: "Guillemot", index: "3"
				fillParticipantData firstName: "Paul", lastName: "King", index: "4"

				clickLink "Complete registration"
				verifyText "Registration completed"
			}
		}
	}
}
