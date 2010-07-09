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
package org.eurekastreams.commons.search.store;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.hibernate.search.engine.SearchFactoryImplementor;
import org.hibernate.search.store.DirectoryProvider;
import org.hibernate.search.store.FSDirectoryProvider;

/**
 * Composite Directory Provider class - consumes the hibernate configuration
 * parameters, creating a CompositeDirectory consisting of a FSDirectory and a
 * RAMDirectory.
 */
public class CompositeDirectoryProvider implements DirectoryProvider<Directory>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(CompositeDirectoryProvider.class);

    /**
     * FSDirectory provider created to setup our FSDirectory.
     */
    private FSDirectoryProvider fsDirectoryProvider = new FSDirectoryProvider();

    /**
     * The CompositeDirectory containing the FSDirectory and RAMDirectory.
     */
    private CompositeDirectory directory;

    /**
     * Empty constructor.
     *
     * TODO: eventually get rid of this constructor, and pass the
     * FSDirectoryProvider into the constructor. To do this, we'll need to move
     * this definition into application-context rather than persistence.xml.
     */
    public CompositeDirectoryProvider()
    {
    }

    /**
     * Constructor taking the FSDirectoryProvider used to setup the FSDirectory
     * - used only in unit testing. At some point, this should be the only
     * constructor, and this object should be moved to Spring application
     * context instead of hibernate persistence.xml.
     *
     * @param inFsDirProvider
     *            FSDirectoryProvider used to create the FSDirectory
     */
    protected CompositeDirectoryProvider(
            final FSDirectoryProvider inFsDirProvider)
    {
        fsDirectoryProvider = inFsDirProvider;
    }

    /**
     * Get the Directory for all lucene index storage and retrieval operations.
     *
     * @return the Directory for all lucene index storage and retrieval
     *         operations.
     */
    @Override
    public Directory getDirectory()
    {
        return directory;
    }

    /**
     * Protected setter for directory for unit testing.
     *
     * @param inDirectory
     *            the new Directory
     */
    protected void setDirectory(final CompositeDirectory inDirectory)
    {
        directory = inDirectory;
    }

    /**
     * Initialize the Directories with the parameters from hibernate
     * configuration.
     *
     * @param directoryProviderName
     *            the name of the directory provider
     * @param properties
     *            the properties for this directory provider
     * @param searchFactory
     *            the SearchFactoryImplementor
     */
    @Override
    public void initialize(final String directoryProviderName,
            final Properties properties,
            final SearchFactoryImplementor searchFactory)
    {
        if (log.isInfoEnabled())
        {
            log.info("Initializing the RAM/Filesystem directory provider.");
            log.info("Properties: " + properties.toString());
            log.info("SearchFactoryImplementor: " + searchFactory.toString());
        }

        fsDirectoryProvider.initialize(directoryProviderName, properties,
                searchFactory);

        directory = new CompositeDirectory(new RAMDirectory(),
                fsDirectoryProvider.getDirectory());

        if (log.isInfoEnabled())
        {
            log
                    .info("Finished initializing the RAM and Filesystem directories.");
        }
    }

    /**
     * Start the CompositeDirectory.
     */
    @Override
    public void start()
    {
        if (log.isInfoEnabled())
        {
            log.info("Starting CompositeDirectory.");
        }
        try
        {
            directory.initializeFastReadDirectory();
        }
        catch (IOException e)
        {
            throw new RuntimeException(
                    "Could not initialize fast-read directory", e);
        }

        if (log.isInfoEnabled())
        {
            log.info("CompositeDirectory complete.");
        }
    }

    /**
     * Stop the CompositeDirectory.
     */
    @Override
    public void stop()
    {
        if (log.isInfoEnabled())
        {
            log.info("Stopping RamFsHybridProvider");
        }
        try
        {
            directory.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not close the directory.");
        }
        if (log.isInfoEnabled())
        {
            log.info("RamFsHybridProvider stopped");
        }
    }
}
