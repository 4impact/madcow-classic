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

package com.projectmadcow.database;

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import javax.sql.DataSource
import org.apache.log4j.Logger
import org.dbunit.DatabaseUnitException
import org.dbunit.database.DatabaseConnection
import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.CachedDataSet
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlProducer
import org.dbunit.operation.DatabaseOperation
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.xml.sax.InputSource

/**
 * DatabaseHelper is the main logic class for the database task.
 * Sets up and creates representations of a database based on XML files,
 * .madcow.data.xml, making it available within the executeSql function.
 */
public class DatabaseHelper {

    static final Logger LOG = Logger.getLogger(DatabaseHelper.class)

    def databaseConfigFileName

    public DatabaseHelper(def databaseConfigFileName) {
        this.databaseConfigFileName = databaseConfigFileName
    }

    /**
     * Setup database.
     *
     * @param connection the connection
     * @param xmlFileInputStream the input stream
     * @throws DatabaseUnitException the database unit exception
     * @throws SQLException          the SQL exception
     */
    private void setupDatabase(final IDatabaseConnection connection, InputStream xmlFileInputStream) throws DatabaseUnitException, SQLException {
        final FlatXmlProducer producer = new FlatXmlProducer(new InputSource(xmlFileInputStream), false);
        final IDataSet dataSet = new CachedDataSet(producer);
        final ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
        replacementDataSet.addReplacementObject("right-now", new DateTime().toDateTime(DateTimeZone.UTC).toLocalDateTime().toDateTime().toDate());
        DatabaseOperation.CLEAN_INSERT.execute(connection, replacementDataSet);
    }

    public synchronized void loadXmlData(ClassLoader classLoader = this.getClass().getClassLoader()) {

        // note that "classpath*:" will only work reliably with at least one root directory before the pattern starts,
        // unless the actual target files reside in the file system. This means that a pattern like "classpath*:*.xml"
        // will not retrieve files from the root of jar files but rather only from the root of expanded directories.
        PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver(classLoader)
        Resource[] resources = resourceLoader.getResources("classpath*:*.madcow.data.xml")

        // only load xml data if there are more than one data file found
        if (resources.length < 1) {
            LOG.info "No database resources found to load"
            return
        }

        def databaseConfig = loadDatabaseConfig(classLoader)
        Class.forName(databaseConfig.jdbc.className)
        Connection jdbcConnection =
        DriverManager.getConnection(databaseConfig.jdbc.url as String, databaseConfig.jdbc.user as String, databaseConfig.jdbc.password as String)
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection)

        // assumes that these data files are order independant, if there is order dependancy then it is
        // suggested that you place all your data within the one madcow.data.xml file.
        try {
            resources.each { resource ->
                LOG.info "Loading resource into database : $resource"
                this.setupDatabase(connection, resource.inputStream)
            }
        } finally {
            connection.close()
        }
    }

    private def loadDatabaseConfig(ClassLoader classLoader = this.getClass().getClassLoader()) {
        LOG.info "DatabaseHelper.loadDatabaseConfig databaseConfigFileName = $databaseConfigFileName"
        PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver(classLoader)
        Resource[] resources = resourceLoader.getResources("classpath*:$databaseConfigFileName")
        Properties databaseProps = new Properties()

        if (resources.length < 1) {
            throw new RuntimeException("Error could not find a single database config file with name '$databaseConfigFileName' on the classpath")
        }

        resources.each { resource ->
            LOG.info "Loading database config resource : $resource"
            databaseProps.load(resource.inputStream)
        }
        new ConfigSlurper().parse(databaseProps)
    }

    private DataSource getDataSource() {
        def databaseConfig = loadDatabaseConfig()
        DriverManagerDataSource dataSource = new DriverManagerDataSource()
        dataSource.driverClassName = databaseConfig.jdbc.className
        dataSource.url = databaseConfig.jdbc.url
        dataSource.username = databaseConfig.jdbc.user
        dataSource.password = databaseConfig.jdbc.password

        LOG.debug "getDataSource jdbc.className=$databaseConfig.jdbc.className jdbc.url=$databaseConfig.jdbc.url " +
                "jdbs.user=$databaseConfig.jdbc.user jdbc.password=$databaseConfig.jdbc.password"

        dataSource
    }

    public def executeSql(String sql) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource())
        jdbcTemplate.execute(sql)
    }
}
