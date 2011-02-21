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

package au.com.ts4impact.madcow.extension.webtest.step

import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.javascript.host.Event
import org.apache.log4j.Logger

public class ExecuteEvent extends AbstractMadcowStep {
  
  private static final Logger LOG = Logger.getLogger(ExecuteEvent.class);

  String htmlId
  String xpath
  String eventType
  Integer eventData

  /**
   * Perform the step's actual work. The minimum you need to implement.
   *
   * @throws com.canoo.webtest.engine.StepFailedException
   *          if step was not successful
   */
  public void doExecute() {
    HtmlElement element = findElement(htmlId, xpath)
    Event event
    if (eventData) {
        event = new Event(element, eventType, eventData, false, false, false)
        
    } else {
        event = new Event(element, eventType)
    }
    element.fireEvent(event)
  }

  protected void verifyParameters() {
    super.verifyParameters()
    nullResponseCheck()
    paramCheck(htmlId == null && xpath == null, "\"htmlId\" or \"xPath\" must be set!")
    paramCheck(htmlId != null && xpath != null, "Only one from \"htmlId\" and \"xPath\" can be set!")
  }

  protected Logger getLog() {
    return LOG
  }
}
