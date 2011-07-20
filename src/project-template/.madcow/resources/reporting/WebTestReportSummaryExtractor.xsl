<?xml version="1.0"?>
<!--
  ~ Copyright 2008-2011 4impact Technology Services, Brisbane, Australia
  ~
  ~ Licensed to the Apache Software Foundation (ASF) under one
  ~ or more contributor license agreements.  See the NOTICE file
  ~ distributed with this work for additional information
  ~ regarding copyright ownership.  The ASF licenses this file
  ~ to you under the Apache License, Version 2.0 (the
  ~ "License"); you may not use this file except in compliance
  ~ with the License.  You may obtain a copy of the License at
  ~
  ~          http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied.  See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  -->

<!--
This xsl extract the information needed for the overview report from the report
for a single webtest.
 -->

<!DOCTYPE xsl:stylesheet>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="xml" indent="yes"/>

	<xsl:template match="testresult">
	    <xsl:variable name="duration.total"
                  select="sum(results/step[@result = 'completed' or @result ='failed']/@duration)"/>
		<summary 
			successful="{@successful}" 
			name="{@testspecname}"
			duration="{$duration.total}"
			starttime="{@starttime}">

	        <xsl:variable name="topsteps.total" select="count(results/step)"/>
	        <xsl:variable name="topsteps.ok" select="count(results/step[@result = 'completed'])"/>
	        <xsl:variable name="topsteps.failed" select="count(results/step[@result = 'failed'])"/>
	        <xsl:variable name="topsteps.else" select="count(results/step[@result = 'notexecuted'])"/>
			<topsteps total="{$topsteps.total}" 
				successful="{$topsteps.ok}" 
				failed="{$topsteps.failed}" 
				notexecuted="{$topsteps.else}"/>

	        <xsl:variable name="steps.total" select="count(//step)"/>
	        <xsl:variable name="steps.ok" select="count(//step[@result = 'completed'])"/>
	        <xsl:variable name="steps.failed" select="count(//step[@result = 'failed'])"/>
	        <xsl:variable name="steps.else" select="count(//step[@result = 'notexecuted'])"/>
			<steps total="{$steps.total}" 
				successful="{$steps.ok}" 
				failed="{$steps.failed}" 
				notexecuted="{$steps.else}"/>

			<!-- timing profile -->
			<xsl:call-template name="timing-profile"/>

            <xsl:apply-templates select="results/step[@result = 'failed']" mode="failurecause"/>

            <!-- Madcow: added ignoreTest -->
            <xsl:apply-templates select="results/step[@taskName = 'ignoreTest']" mode="ignoreTest" />

            <!-- Madcow: added showOnReport -->
            <xsl:apply-templates select="results/step[@taskName = 'showOnReport']" mode="showOnReport" />

		</summary>
	</xsl:template>

	<xsl:template match="step" mode="failurecause">
		<failingstep>
			<xsl:attribute name="name">
			    <xsl:value-of select="@taskName"/>
			</xsl:attribute>
			<xsl:attribute name="description">
			    <xsl:value-of select="@description"/>
			</xsl:attribute>
			
            <xsl:apply-templates select="step[(@result = 'failed' and @taskName != 'not') or (@result = 'completed' and @taskName = 'not')]" mode="failurecause"/>
			
		</failingstep>
    </xsl:template>

    <!-- Madcow: added ignoreTest -->
    <xsl:template match="step" mode="ignoreTest">
        <ignoreTest/>
    </xsl:template>

    <!-- Madcow: added showOnReport -->
    <xsl:template match="step" mode="showOnReport">
        <showOnReport>
            <xsl:attribute name="name">
                <xsl:value-of select="parameter[@name='value']/@value" />
            </xsl:attribute>
            <xsl:attribute name="value">
                <xsl:value-of select="parameter[@name='=> value']/@value" />
            </xsl:attribute>
        </showOnReport>
    </xsl:template>

	<xsl:template name="timing-profile">
        <xsl:variable name="requestSteps"
                      select="//step[starts-with(@taskName, 'sqlunit') or @taskName = 'invoke' or @taskName = 'clickLink' or @taskName = 'clickButton' or @taskName='clickElement'][@result = 'completed' or @result = 'failed']"/>

        <xsl:variable name="nbSteps" select="count($requestSteps)"/>
        <xsl:variable name="totalDuration" select="sum($requestSteps/@duration)"/>
        <xsl:variable name="averageDuration" select="round($totalDuration div $nbSteps)"/>
        <xsl:variable name="steps.30" select="count($requestSteps[@duration > 30000])"/>
        <xsl:variable name="steps.10_30" select="count($requestSteps[@duration > 10000][30000 >= @duration])"/>
        <xsl:variable name="steps.5_10" select="count($requestSteps[@duration > 5000][100000 >= @duration])"/>
        <xsl:variable name="steps.3_5" select="count($requestSteps[@duration > 3000][50000 >= @duration])"/>
        <xsl:variable name="steps.1_3" select="count($requestSteps[@duration > 1000][3000 >= @duration])"/>
        <xsl:variable name="steps.0_1" select="count($requestSteps[1000 >= @duration])"/>

		<timingprofile average="{$averageDuration}" number="{$nbSteps}">
			<range from="0" to="1" number="{$steps.0_1}"/>
			<range from="1" to="3" number="{$steps.1_3}"/>
			<range from="3" to="5" number="{$steps.3_5}"/>
			<range from="5" to="10" number="{$steps.5_10}"/>
			<range from="10" to="30" number="{$steps.10_30}"/>
			<range from="30" number="{$steps.30}"/>
		</timingprofile>

	</xsl:template>
</xsl:stylesheet>
