/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.testing;

import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

/**
 * Helper class for loading DBUnit datasource xml files embedded in the project as resources.
 *
 * The main function of this class is to make it easier to set up the data mappers.
 *
 * Note: This is very specific to our configuration - it's loading all Spring files found in
 * "classpath:applicationContext*-test.xml", and looking for the bean named "dataSource". This is no more hard-coded
 * than annotations in our tests, and does make our setUp methods much easier to manage.
 */
public final class DBUnitFixtureSetup
{
    /** logger instance. */
    private static Log logger = LogFactory.getLog(DBUnitFixtureSetup.class);

    /**
     * Locations of the application context files to load for finding the data source.
     */
    private static final String[] APPLICATION_CONTEXT_LOCATIONS = { "classpath:applicationContext*-test.xml" };

    /**
     * Name of the data source bean in the Spring config.
     */
    private static final String DATA_SOURCE_BEAN_NAME = "dataSource";

    /**
     * Keeps track of whether it's already been loaded.
     */
    private static String currentlyLoadedDatasetPath = null;

    /**
     * Private default constructor. Utility classes should not have public default constructor.
     */
    private DBUnitFixtureSetup()
    {
        // no-op
    }

    /**
     * Load up the XML dataset found at the input file path, using the data source found in the bean "dataSource" in
     * Spring files found in "classpath:applicationContext*-test.xml".
     *
     * @param newDatasetPath
     *            The resource path of the dataset xml file - for example: "/dataset.xml"
     * @param forceReload
     *            whether to force the data to load, ignoring the cache
     * @throws Exception
     *             on any errors
     */
    public static void loadDataSet(final String newDatasetPath, final Boolean forceReload) throws Exception
    {
        currentlyLoadedDatasetPath = null;
        loadDataSet(newDatasetPath);
    }

    /**
     * Load up the XML dataset found at the input file path, using the data source found in the bean "dataSource" in
     * Spring files found in "classpath:applicationContext*-test.xml".
     *
     * @param newDatasetPath
     *            The resource path of the dataset xml file - for example: "/dataset.xml"
     * @throws Exception
     *             on any errors
     */
    public static void loadDataSet(final String newDatasetPath) throws Exception
    {
        if (currentlyLoadedDatasetPath != null && currentlyLoadedDatasetPath.equalsIgnoreCase(newDatasetPath))
        {
            System.out.println("Dataset '" + newDatasetPath + "' is currently loaded - skipping.");
            return;
        }
        long start = System.currentTimeMillis();
        System.out.println("Loading dataset '" + newDatasetPath + "'.");

        // Get the Spring application context
        ApplicationContext appContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_LOCATIONS);

        // Get the data source from Spring
        DataSource dataSource = (DataSource) appContext.getBean(DATA_SOURCE_BEAN_NAME);

        logger.info("About to load up the test dataset at path '" + newDatasetPath + "'");

        // Get the connection to the database
        Connection connection = DataSourceUtils.getConnection(dataSource);
        IDatabaseConnection dbUnitCon = new DatabaseConnection(connection);

        // Get the data from the file at the input resource path
        IDataSet dataSet = new FlatXmlDataSet(DBUnitFixtureSetup.class.getResourceAsStream(newDatasetPath));

        // insert the data set into the database with a clean insert
        try
        {
            DatabaseOperation.CLEAN_INSERT.execute(dbUnitCon, dataSet);
        }
        finally
        {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        System.out.println("Loaded dataset '" + newDatasetPath + "' in " + (System.currentTimeMillis() - start) + "ms");
        currentlyLoadedDatasetPath = newDatasetPath;
    }
}
