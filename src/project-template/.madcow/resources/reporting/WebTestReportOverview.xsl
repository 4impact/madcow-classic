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
This XSL allows to generate the report overview.
 -->

<!DOCTYPE xsl:stylesheet [
        <!ENTITY space "&#32;">
        <!ENTITY nbsp "&#160;">
        <!-- either '&#8470;' for No, or '#' -->
        <!ENTITY no "#">
        ]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" encoding="us-ascii" indent="yes"
                doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
                doctype-system="http://www.w3.org/TR/html4/loose.dtd"
            />

    <!-- Parameter passed from ant with the creation time -->
    <xsl:param name="reporttime"/>
    <xsl:param name="title" select="'Madcow Test Results'"/>

    <!-- Customization hook for site-specific differences -->
    <!-- path to the deployed resources (css, ...); absolute, or relative to the WebTestReport.html -->
    <xsl:param name="resources.dir" select="'.'"/>
    <xsl:variable name="company.logo.alt" select="'Madcow'"/>
    <xsl:param name="outputDir" select="'../reports'"/>


    <!-- global variable -->
    <xsl:variable name="duration.total" select="sum(/overview/folder/summary/@duration)"/>

    <xsl:variable name="img.ok" select="concat($resources.dir, '/images/ok.gif')"/>
    <xsl:variable name="img.todo" select="concat($resources.dir, '/images/todo.gif')"/>
    <xsl:variable name="img.optional" select="concat($resources.dir, '/images/optional.gif')"/>
    <xsl:variable name="img.expandPlus" select="concat($resources.dir, '/images/expandPlus.png')"/>
    <xsl:variable name="img.4impact" select="concat($resources.dir, '/images/4impact.jpg')"/>
    <xsl:variable name="img.favicon" select="concat($resources.dir, '/images/favicon.ico')"/>

	<xsl:variable name="webtestVersion" select="/overview/@Implementation-Version"/>

    <xsl:template match="/">
        <!-- HTML prefix -->
        <html lang="en">
            <head>
                <title>
                    <xsl:value-of select="$title"/>
                </title>
				<link href="{$img.favicon}" type="image/x-icon" rel="shortcut icon"/>
				<link href="{$img.favicon}" type="image/x-icon" rel="icon"/>
                <meta http-equiv="content-style-type" content="text/css"/>
                <link rel="stylesheet" type="text/css" href="{$resources.dir}/report.css"/>
                <link href="{$resources.dir}/madcow.css" type="text/css" rel="stylesheet"/>
                <script type="text/javascript" src="{$resources.dir}/sorttable.js"></script>
            </head>

            <!-- ###################################################################### -->
            <body>
                <div class="header">
                    <h1>Madcow Test Results</h1>
                    <p><xsl:text>Tests started at&space;</xsl:text>
                    <xsl:value-of select="/overview/folder[1]/summary/@starttime"/></p>
                </div>

                <!-- Header and summary table -->
                <xsl:call-template name="StepStatistics"/>

                <xsl:call-template name="OverviewTable"/>

                <!-- Footer & fun -->
                <hr/>

			<table width="100%">
			<tr>
			<td valign="top">
                <xsl:text>Created using&space;</xsl:text>
                <a href="http://www.4impact.com.au">Madcow</a><xsl:text>&space;&amp;&space;</xsl:text>
                <a href="http://webtest.canoo.com">
                    <xsl:value-of select="/overview/@Implementation-Title"/>
                </a>
                <xsl:text>&space;(</xsl:text>
                <xsl:value-of select="$webtestVersion"/>
                <xsl:text>). Report created at&space;</xsl:text>
                <xsl:value-of select="$reporttime"/>
			</td>
			<td align="right">
			<a href="http://www.4impact.com.au" target="_blank"><img title="4impact" alt="4impact" src="{$img.4impact}"/></a>
			</td>
			</tr>
			</table>
                <!-- HTML postfix -->
            </body>
        </html>

    </xsl:template>

    <xsl:template name="OverviewTable">

        <!--
            Create summary table entries choosing the td-class depending on successful yes/no
            and create a link to the appropriate detail section (e.g. #testspec1).
        -->
        <table cellpadding="5" border="0" cellspacing="0" width="100%" class="sortable">
            <caption><xsl:text>Test Scenario Overview (</xsl:text>
                <xsl:call-template name="time">
                    <xsl:with-param name="msecs" select="$duration.total"/>
                </xsl:call-template>
                <xsl:text>)</xsl:text></caption>
            <thead>
                <tr>
                    <th>&no;</th>
                    <th>Result</th>
                    <th>Name</th>
                    <th title="Number of successful executed steps / Total number of steps"># Steps</th>
                    <th>Duration</th>
                    <th>%</th>
                    <th>Graph</th>
                    <th>Failing step</th>
                </tr>
            </thead>
            <tbody>
                <xsl:apply-templates select="/overview/folder/summary" mode="summary"/>
            </tbody>
        </table>
    </xsl:template>

    <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->
    <xsl:template name="StepStatistics_tests">
        <xsl:variable name="webtests.total" select="count(/overview/folder/summary)"/>
        <xsl:variable name="webtests.ok" select="count(/overview/folder/summary[@successful='yes'])"/>
        <xsl:variable name="webtests.failed" select="count(/overview/folder/summary[@successful='no'])"/>

        <tbody>
            <tr>
                <th>Tests</th>
                <th align="right">#</th>
                <th align="right">%</th>
                <th>Graph</th>
            </tr>
            <tr>
                <td class="light">
                    <img src="{$img.ok}" alt="ok"/>
                </td>
                <td class="light" align="right">
                    <xsl:value-of select="$webtests.ok"/>
                </td>
                <td class="light" align="right">
                    <xsl:value-of select="round($webtests.ok * 100 div $webtests.total)"/>
                </td>
                <xsl:call-template name="colorBar">
                    <xsl:with-param name="percentage" select="$webtests.ok * 100 div $webtests.total"/>
                    <xsl:with-param name="color" select="'lightgreen'"/>
                    <xsl:with-param name="title" select="'Successful Tests'"/>
                </xsl:call-template>
            </tr>
            <tr>
                <td class="light">
                    <img src="{$img.todo}" alt="x"/>
                </td>
                <td class="light" align="right">
                    <xsl:value-of select="$webtests.failed"/>
                </td>
                <td class="light" align="right">
                    <xsl:value-of select="round($webtests.failed * 100 div $webtests.total)"/>
                </td>
                <xsl:call-template name="colorBar">
                    <xsl:with-param name="percentage" select="$webtests.failed * 100 div $webtests.total"/>
                    <xsl:with-param name="color" select="'red'"/>
                    <xsl:with-param name="title" select="'Failed Tests'"/>
                </xsl:call-template>
            </tr>
        </tbody>
        <tbody>
            <tr>
                <td class="light">
                    <b>Total</b>
                </td>
                <td class="light" align="right">
                    <b>
                        <xsl:text>&nbsp;</xsl:text>
                        <xsl:value-of select="$webtests.total"/>
                    </b>
                </td>
                <td class="light" align="right">
                    <b>&nbsp;100</b>
                </td>
                <td class="light">&nbsp;</td>
            </tr>
        </tbody>
    </xsl:template>

    <xsl:template name="StepStatistics_steps">
        <xsl:variable name="steps.total" select="sum(/overview/folder/summary/topsteps/@total)"/>
        <xsl:variable name="steps.ok" select="sum(/overview/folder/summary/topsteps/@successful)"/>
        <xsl:variable name="steps.failed" select="sum(/overview/folder/summary/topsteps/@failed)"/>
        <xsl:variable name="steps.else" select="sum(/overview/folder/summary/topsteps/@notexecuted)"/>

        <tbody>
            <tr>
                <th>Steps</th>
                <th align="right">#</th>
                <th align="right">%</th>
                <th>Graph</th>
            </tr>
            <tr>
                <td class="light">
                    <img src="{$img.ok}" alt="ok"/>
                </td>
                <td class="light" align="right">
                    <xsl:value-of select="$steps.ok"/>
                </td>
                <td class="light" align="right">
                    <xsl:value-of select="round($steps.ok * 100 div $steps.total)"/>
                </td>
                <xsl:call-template name="colorBar">
                    <xsl:with-param name="percentage" select="$steps.ok * 100 div $steps.total"/>
                    <xsl:with-param name="color" select="'lightgreen'"/>
                    <xsl:with-param name="title" select="'Successful steps'"/>
                </xsl:call-template>
            </tr>
            <tr>
                <td class="light">
                    <img src="{$img.todo}" alt="x"/>
                </td>
                <td class="light" align="right">
                    <xsl:value-of select="$steps.failed"/>
                </td>
                <td class="light" align="right">
                    <xsl:value-of select="round($steps.failed * 100 div $steps.total)"/>
                </td>
                <xsl:call-template name="colorBar">
                    <xsl:with-param name="percentage" select="$steps.failed * 100 div $steps.total"/>
                    <xsl:with-param name="color" select="'red'"/>
                    <xsl:with-param name="title" select="'Failed steps'"/>
                </xsl:call-template>
            </tr>
            <tr>
                <td class="light">
                    <img src="{$img.optional}" alt="o"/>
                </td>
                <td class="light" align="right">
                    <xsl:value-of select="$steps.else"/>
                </td>
                <td class="light" align="right">
                    <xsl:value-of select="round($steps.else * 100 div $steps.total)"/>
                </td>
                <xsl:call-template name="colorBar">
                    <xsl:with-param name="percentage" select="$steps.else * 100 div $steps.total"/>
                    <xsl:with-param name="color" select="'yellow'"/>
                    <xsl:with-param name="title" select="'Skipped steps'"/>
                </xsl:call-template>
            </tr>
        </tbody>
        <tbody>
            <tr>
                <td class="light">
                    <b>Total</b>
                </td>
                <td class="light" align="right">
                    <b>
                        <xsl:text>&nbsp;</xsl:text>
                        <xsl:value-of select="$steps.total"/>
                    </b>
                </td>
                <td class="light" align="right">
                    <b>&nbsp;100</b>
                </td>
                <td class="light">&nbsp;</td>
            </tr>
        </tbody>
    </xsl:template>

    <xsl:template name="StepStatistics_serverRoundtrips">

        <!--        ================================ server roundtrip statistic table =============================    -->

        <xsl:variable name="timingProfiles" select="/overview/folder/summary/timingprofile"/>
        <xsl:variable name="profileRanges" select="/overview/folder/summary/timingprofile/range"/>
        <xsl:variable name="steps.last" select="sum($profileRanges[@from=30]/@number)"/>
        <xsl:variable name="steps.fourth" select="sum($profileRanges[@from=10 and @to=30]/@number)"/>
        <xsl:variable name="steps.third" select="sum($profileRanges[@from=5 and @to=10]/@number)"/>
        <xsl:variable name="steps.second" select="sum($profileRanges[@from=3 and @to=5]/@number)"/>
        <xsl:variable name="steps.first" select="sum($profileRanges[@from=1 and @to=3]/@number)"/>
        <xsl:variable name="steps.begin" select="sum($profileRanges[@to=1]/@number)"/>
        <xsl:variable name="steps.total" select="sum($profileRanges/@number)"/>

        <xsl:variable name="averageTime" select="round(sum(//summary/@duration) div sum($timingProfiles/@number))"/>

        <table cellpadding="3" border="0" cellspacing="0" width="100%">
            <caption>Timing Profile</caption>
            <thead>
                <tr>
                    <th align="center">Secs</th>
                    <th align="right">#</th>
                    <th align="right">%</th>
                    <th>Histogram</th>
                </tr>
            </thead>
            <tfoot>
                <tr>
                    <td class="light">
                        <b>Total</b>
                    </td>
                    <td class="light" align="right">
                        <b>
                            <xsl:text>&nbsp;</xsl:text>
                            <xsl:value-of select="$steps.total"/>
                        </b>
                    </td>
                    <td class="light" align="right">
                        <b>&nbsp;100</b>
                    </td>
                    <td class="light">&nbsp;</td>
                </tr>
                <tr>
                    <td class="light">
                        <b>Avg</b>
                    </td>
                    <td class="light" align="right">
                        &nbsp;
                    </td>
                    <td class="light" align="right">
                        &nbsp;
                    </td>
                    <td class="light">
                        <b>
                            <xsl:value-of select="$averageTime"/>
                            <xsl:text> ms</xsl:text>
                        </b>
                    </td>
                </tr>
            </tfoot>
            <tbody>
                <xsl:call-template name="histogramRow">
                    <xsl:with-param name="label">0 - 1</xsl:with-param>
                    <xsl:with-param name="steps" select="$steps.begin"/>
                    <xsl:with-param name="total" select="$steps.total"/>
                </xsl:call-template>

                <xsl:call-template name="histogramRow">
                    <xsl:with-param name="label">1 - 3</xsl:with-param>
                    <xsl:with-param name="steps" select="$steps.first"/>
                    <xsl:with-param name="total" select="$steps.total"/>
                </xsl:call-template>

                <xsl:call-template name="histogramRow">
                    <xsl:with-param name="label">3 - 5</xsl:with-param>
                    <xsl:with-param name="steps" select="$steps.second"/>
                    <xsl:with-param name="total" select="$steps.total"/>
                </xsl:call-template>

                <xsl:call-template name="histogramRow">
                    <xsl:with-param name="label">5 - 10</xsl:with-param>
                    <xsl:with-param name="steps" select="$steps.third"/>
                    <xsl:with-param name="total" select="$steps.total"/>
                </xsl:call-template>

                <xsl:call-template name="histogramRow">
                    <xsl:with-param name="label">10 - 30</xsl:with-param>
                    <xsl:with-param name="steps" select="$steps.fourth"/>
                    <xsl:with-param name="total" select="$steps.total"/>
                </xsl:call-template>

                <xsl:call-template name="histogramRow">
                    <xsl:with-param name="label">&gt; 30</xsl:with-param>
                    <xsl:with-param name="steps" select="$steps.last"/>
                    <xsl:with-param name="total" select="$steps.total"/>
                </xsl:call-template>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template name="StepStatistics_results">
        <table cellpadding="3" border="0" cellspacing="0" width="100%">
            <caption>Result Summary</caption>
            <xsl:call-template name="StepStatistics_tests"/>
            <xsl:call-template name="StepStatistics_steps"/>
        </table>
    </xsl:template>

    <xsl:template name="StepStatistics">
        <table cellpadding="0" border="0" cellspacing="2" width="100%">
            <tr>
                <td valign="top" width="50%">
                    <xsl:call-template name="StepStatistics_results"/>
                </td>
                <td valign="top">
                    <xsl:call-template name="StepStatistics_serverRoundtrips"/>
                </td>
            </tr>
        </table>
    </xsl:template>

    <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->
    <xsl:template name="histogramRow">
        <xsl:param name="label"/>
        <xsl:param name="steps"/>
        <xsl:param name="total"/>
        <tr>
            <td class="light" nowrap="nowrap" align="center">
                <xsl:value-of select="$label"/>
            </td>
            <td class="light" align="right">
                <xsl:value-of select="$steps"/>
            </td>
            <td class="light" align="right">
                <xsl:value-of select="round($steps * 100 div $total)"/>
            </td>
            <xsl:call-template name="colorBar">
                <xsl:with-param name="percentage" select="$steps * 100 div $total"/>
                <xsl:with-param name="color" select="'lightblue'"/>
            </xsl:call-template>
        </tr>
    </xsl:template>

    <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->
    <xsl:template name="colorBar">
        <xsl:param name="percentage"/>
        <xsl:param name="color"/>
        <xsl:param name="title"/>
        <xsl:param name="width" select="'80%'"/>

        <td width="{$width}" class="light">
            <xsl:if test="$percentage > 0">
                <div class="colorBar" style="width: {$percentage}%; background: {$color};">
                    <xsl:if test="$title">
                        <xsl:attribute name="title">
                            <xsl:value-of select="$title"/>
                        </xsl:attribute>
                    </xsl:if>
                </div>
            </xsl:if>
        </td>
    </xsl:template>

    <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->
    <xsl:template name="time">
        <xsl:param name="msecs"/>
        <xsl:param name="detail"/>

        <xsl:choose>
            <xsl:when test="$msecs > 5000">
                <xsl:variable name="base" select="round($msecs div 1000)"/>
                <xsl:variable name="hours" select="floor($base div 3600)"/>
                <xsl:variable name="mins" select="floor(($base - $hours*3600) div 60)"/>
                <xsl:variable name="secs" select="floor(($base - $hours*3600) - $mins*60)"/>

                <xsl:if test="10 > $hours">0</xsl:if>
                <xsl:value-of select="$hours"/>
                <xsl:text>:</xsl:text>
                <xsl:if test="10 > $mins">0</xsl:if>
                <xsl:value-of select="$mins"/>
                <xsl:text>:</xsl:text>
                <xsl:if test="10 > $secs">0</xsl:if>
                <xsl:value-of select="$secs"/>

                <xsl:if test="$detail">
                    <xsl:text>&space;(</xsl:text>
                    <xsl:value-of select="$msecs"/>
                    <xsl:text>&space;ms)</xsl:text>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$msecs"/>
                <xsl:if test="$detail">&space;ms</xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="summary[@successful='no']" mode="successIndicator">
        <a>
            <xsl:attribute name="onclick">showTargetStep(this)</xsl:attribute>

            <xsl:attribute name="href">
                <xsl:apply-templates select="current()" mode="indexedFileName"/>
                <xsl:text>#testspec</xsl:text>
                <xsl:number/>
                <xsl:text>-error</xsl:text>
            </xsl:attribute>
            <img src="{$img.todo}" alt="x"/>
        </a>
    </xsl:template>

    <xsl:template match="summary[@successful='yes']" mode="successIndicator">
        <img src="{$img.ok}" alt="ok"/>
    </xsl:template>

    <xsl:template match="testresult" mode="indexedFileName">
        <xsl:text>File</xsl:text>
        <xsl:number/>
        <xsl:text>.html</xsl:text>
    </xsl:template>
    <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->
    <xsl:template match="summary" mode="summary">
        <tr>
            <td class="light" align="right">
                <xsl:number count="folder"/>
            </td>
            <td class="light" align="center">
                <xsl:apply-templates select="." mode="successIndicator"/>
            </td>
            <td class="light" nowrap="nowrap">
                <a href="{../@name}/WebTestReport.html">
                    <xsl:value-of select="@name"/>
                </a>

                <xsl:if test="count(showOnReport) > 0">
                    <table cellspacing="0" cellpadding="3">
                    <xsl:for-each select="showOnReport">
                        <tr><td><xsl:value-of select="@name"/></td><td><xsl:value-of select="@value" disable-output-escaping="yes" /></td></tr>
                    </xsl:for-each>
                    </table>
                </xsl:if>
            </td>
            <td class="light" align="right" style="white-space: nowrap">
                <xsl:value-of select="steps/@successful"/>
                <xsl:text> / </xsl:text>
                <xsl:value-of select="steps/@total"/>
            </td>
            <td class="light" align="right" nowrap="nowrap">
                <xsl:call-template name="time">
                    <xsl:with-param name="msecs" select="@duration"/>
                </xsl:call-template>
            </td>
            <td class="light" align="right">
                <xsl:value-of select="round(@duration * 100 div $duration.total)"/>
            </td>

            <xsl:call-template name="colorBar">
                <xsl:with-param name="percentage" select="@duration * 100 div $duration.total"/>
                <xsl:with-param name="color" select="'lightblue'"/>
                <xsl:with-param name="width" select="'20%'"/>
            </xsl:call-template>

            <td class="light"> <!--  the failing top step, if any -->
                <xsl:if test="failingstep">
                    <b>
                        <xsl:value-of select="failingstep/@name"/>
                    </b><br/>
                    <xsl:value-of select="failingstep/@description"/>
                </xsl:if>
            </td>
        </tr>
    </xsl:template>


</xsl:stylesheet>
