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
Registers a listener to the Ant project and display a UI that informs of the progress of the tests execution
Called by useWebTest.xml#wt.monitor
*/

import javax.swing.SwingUtilities as DO
import javax.swing.ScrollPaneConstants as SPC

import com.canoo.webtest.ant.TestStepSequence
import com.canoo.webtest.ant.WebtestTask
import com.canoo.webtest.engine.NOPBuildListener
import groovy.swing.SwingBuilder
import java.awt.Color
import javax.swing.ImageIcon
import org.apache.tools.ant.BuildEvent
import org.apache.tools.ant.Task

class WebTestMonitorImpl extends NOPBuildListener
{
	def frame, swing
	def contentPanel, scrollPanel
	def pictureOk, pictureFailed, pictureSpinner
	def startLabel

	private mapWebTest2Label = [:]

	WebTestMonitorImpl(_project)
	{
		def icon 		= new ImageIcon(getClass().classLoader.getResource("WebTestIcon.png"))
		pictureSpinner 	= new ImageIcon(getClass().classLoader.getResource("spinner.gif"))
		pictureOk 		= new ImageIcon(_project.properties["~wt.webtestMonitor.img.ok"])
		pictureFailed 	= new ImageIcon(_project.properties["~wt.webtestMonitor.img.failed"])

		swing = new SwingBuilder()
		DO.invokeLater {
			frame = swing.frame(title: 'WebTest Monitor', iconImage: icon.image) {
				scrollPanel = scrollPane(verticalScrollBarPolicy: SPC.VERTICAL_SCROLLBAR_ALWAYS) {
					contentPanel = vbox() {
					    startLabel = label(text: "Setting up...", icon: pictureSpinner)
					}
				}
			}
			scrollPanel.viewport.background    = Color.WHITE
			scrollPanel.viewport.preferredSize = [600, 300]
			frame.pack()
			frame.visible = true
		}
		// place itself in project's reference to allow "customers" to retrieve it
		_project.addReference("wt.WebTestMonitor.ref", this)
	}

	/**
	* Called by Ant to notify a listener
	*/
	public void taskStarted(final BuildEvent event)
	{
		def task = event.task
		if (!task.taskName)
			return
		def taskClass = task.project.taskDefinitions[task.taskName]
		if (taskClass && WebtestTask.isAssignableFrom(taskClass))
		{
			notifyWebTestAdded(task)
			notifyWebTestStarted(task)
			
			// in normal mode we capture taskFinished of last WebTest's child, change it in map
			def jlabel = mapWebTest2Label[task.wrapper]
			mapWebTest2Label.remove(task.wrapper)
			mapWebTest2Label[task.wrapper] = jlabel
		}
	}
	
	/**
	* Called by Ant to notify a listener
	*/
	public void taskFinished(final BuildEvent event)
	{
		def task = event.task
		if (!(event.task instanceof Task))
			return;
		else if (event.task instanceof TestStepSequence)
		{
			def webtestTask = event.task.context.webtest
			if (mapWebTest2Label.containsKey(webtestTask.wrapper))
			{
				def ok = (event.exception == null)
				notifyWebTestFinished(webtestTask, ok)
			}
		}
	}

	/**
	* @param _webTest 
	*/
	public notifyWebTestAdded(task)
	{
		def wrapper = task.getWrapper()
		def webtestName = task.getProject().replaceProperties(wrapper.attributeMap['name']);
		def webTestLabel = swing.label(text: "${webtestName}...", 
								toolTipText: task.getLocation().toString() - ": ")
		
		mapWebTest2Label[task.wrapper] = webTestLabel

		DO.invokeLater {
			startLabel.icon = pictureOk
			contentPanel.add webTestLabel
			
			// The next two events could be replaced with contentPanel.revalidate() 
			// but then we would need to make sure that they are queued in the EDT
			// before updating the scrollbar. Calling manually ensures proper sequence.
			contentPanel.invalidate() 
			scrollPanel.validate()

			scrollPanel.verticalScrollBar.value = scrollPanel.verticalScrollBar.maximum			
		}			
	}
	
	public notifyWebTestStarted(_webTest)
	{
		def jlabel = mapWebTest2Label[_webTest.wrapper]
		DO.invokeLater {
			jlabel.icon = pictureSpinner
		}			
	}
	
	public notifyWebTestFinished(_webTest, _success)
	{
		def jlabel = mapWebTest2Label[_webTest.wrapper]
		DO.invokeLater {
			jlabel.text += " ${_success ? 'success' : 'failed'}"
			jlabel.icon = (_success ? pictureOk : pictureFailed)
		}
		mapWebTest2Label.remove(_webTest.wrapper)
	}

	/**
	 * Releases all resources (like the graphic interface)
	 */
	public shutdown()
	{
		frame.dispose()
	}
}

// register an instance of WebTestMonitor as build listener
project.addBuildListener(new WebTestMonitorImpl(project))
