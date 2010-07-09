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
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;

/**
 * Directory class that wraps an input persistent Directory with a fast-read
 * Directory - most likely a RAMDirectory. All write operations are performed on
 * both the input persistent Directory and the wrapping fast-read Directory
 * while all read operations are performed on the fast-read Directory for speed.
 */
public class CompositeDirectory extends org.apache.lucene.store.Directory
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(CompositeDirectory.class);

    /**
     * Directory used for fast reading.
     */
    private Directory fastReadDirectory;

    /**
     * Directory used for main persistent storage.
     */
    private Directory persistentDirectory;

    /**
     * Constructor, taking a fast-read Directory and a persistent Directory.
     *
     * @param inFastReadDirectory
     *            a read-optimized Directory. All read and write operations will
     *            be passed through to it.
     * @param inPersistentDirectory
     *            a persistent Directory. All write operations will be passed
     *            through to it.
     */
    public CompositeDirectory(final Directory inFastReadDirectory,
            final Directory inPersistentDirectory)
    {
        if (log.isDebugEnabled())
        {
            log
                    .debug("Instantiating CompositeDirectory with fastReadDirectory: "
                            + inFastReadDirectory.toString()
                            + ", persistentDirectory: "
                            + inPersistentDirectory.toString());
        }
        fastReadDirectory = inFastReadDirectory;
        persistentDirectory = inPersistentDirectory;
    }

    /**
     * Load the fast-read Directory with the contents of the persistent
     * Directory.
     *
     * @throws IOException
     *             on I/O error
     */
    public void initializeFastReadDirectory() throws IOException
    {
        if (log.isInfoEnabled())
        {
            log
                    .info("Loading persisted Directory into the fast-read Directory");
        }
        Directory.copy(persistentDirectory, fastReadDirectory, false);
    }

    /**
     * Close the store, closing both the fast-read and persistent Directories.
     *
     * @throws IOException
     *             on I/O error.
     */
    @Override
    public void close() throws IOException
    {
        if (log.isTraceEnabled())
        {
            log.trace("close()");
        }
        persistentDirectory.close();
        fastReadDirectory.close();
    }

    /**
     * Creates a new, empty file in both the read-fast and persistent
     * directories with the given name, returning a composite stream to both
     * directories for writing to this file.
     *
     * @param name
     *            the file to create
     *
     * @return a new, empty file in both the read-fast and persistent
     *         directories with the given name, represented by a composite
     *         stream which writes to both directories
     *
     * @throws IOException
     *             on I/O error.
     */
    @Override
    public IndexOutput createOutput(final String name) throws IOException
    {
        IndexOutput result = new CompositeIndexOutput(fastReadDirectory
                .createOutput(name), persistentDirectory.createOutput(name));
        if (log.isTraceEnabled())
        {
            log
                    .trace("createOutput(" + name + ") returns "
                            + result.toString());
        }

        return result;
    }

    /**
     * Removes an existing file in both the read-fast and persistent
     * directories.
     *
     * @param name
     *            the file to delete
     *
     * @throws IOException
     *             on I/O error.
     */
    @Override
    public void deleteFile(final String name) throws IOException
    {
        if (log.isTraceEnabled())
        {
            log.trace("deleteFile(" + name + ")");
        }
        persistentDirectory.deleteFile(name);
        fastReadDirectory.deleteFile(name);
    }

    /**
     * Returns true if a file with the given name exists in the fast-read
     * directory.
     *
     * @param name
     *            the file to check existence of
     *
     * @return whether the file with the input name exists
     *
     * @throws IOException
     *             on I/O error.
     */
    @Override
    public boolean fileExists(final String name) throws IOException
    {
        boolean result = fastReadDirectory.fileExists(name);
        if (log.isTraceEnabled())
        {
            log.trace("fileExists(" + name + ") returns" + result);
        }
        return result;
    }

    /**
     * Returns the length of the file in the fast-read directory.
     *
     * @param name
     *            the file to check the length of
     *
     * @return the length of the file in the fast-read directory
     *
     * @throws IOException
     *             on I/O error
     */
    @Override
    public long fileLength(final String name) throws IOException
    {
        long result = fastReadDirectory.fileLength(name);
        if (log.isTraceEnabled())
        {
            log.trace("fileLength(" + name + ") returns " + result);
        }
        return result;
    }

    /**
     * Returns the time the named file was last modified in the fast-read
     * directory.
     *
     * @param name
     *            the file to check the modified time of.
     *
     * @return the time the named file was last modified in the fast-read
     *         directory
     *
     * @throws IOException
     *             on I/O error.
     */
    @Override
    public long fileModified(final String name) throws IOException
    {
        long result = fastReadDirectory.fileModified(name);
        if (log.isTraceEnabled())
        {
            log.trace("fileModified(" + name + ") returns " + result);
        }
        return result;
    }

    /**
     * Returns a list of index files in the fast-read directory.
     *
     * @return a list of index files in the fast-read directory.
     *
     * @throws IOException
     *             on I/O error.
     */
    @Override
    public String[] list() throws IOException
    {
        String[] result = fastReadDirectory.list();
        if (log.isTraceEnabled())
        {
            log.trace("list() returns " + result.length + " items: "
                    + Arrays.toString(result));
        }
        return result;
    }

    /**
     * Returns a stream reading an existing file in the read-fast directory.
     *
     * @param name
     *            the file to open
     *
     * @return a stream reading an existing file in the read-fast directory
     *
     * @throws IOException
     *             on I/O error.
     */
    @Override
    public IndexInput openInput(final String name) throws IOException
    {
        IndexInput result = fastReadDirectory.openInput(name);
        if (log.isTraceEnabled())
        {
            log.trace("openInput(" + name + ") returns " + result);
        }
        return result;
    }

    /**
     * Returns a stream reading an existing file in the read-fast directory with
     * the input buffer size.
     *
     * @param name
     *            the file to open
     *
     * @param bufferSize
     *            the buffer size to use
     *
     * @return a stream reading an existing file in the read-fast directory
     *
     * @throws IOException
     *             on I/O error.
     */
    @Override
    public IndexInput openInput(final String name, final int bufferSize)
            throws IOException
    {
        if (log.isTraceEnabled())
        {
            log.trace("openInput(" + name + ", " + bufferSize + ")");
        }
        return fastReadDirectory.openInput(name, bufferSize);
    }

    /**
     * Renames an existing file in both the fast-read and persistent
     * directories.
     *
     * @param from
     *            the file to rename
     * @param to
     *            the new file name
     *
     * @throws IOException
     *             on I/O error.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void renameFile(final String from, final String to)
            throws IOException
    {
        if (log.isTraceEnabled())
        {
            log.trace("renameFile(" + from + ", " + to + ")");
        }
        persistentDirectory.renameFile(from, to);
        fastReadDirectory.renameFile(from, to);
    }

    /**
     * Set the modified time to an existing file to now in both the fast-read
     * and persistent directories.
     *
     * @param name
     *            the name of the file to touch
     *
     * @throws IOException
     *             on I/O error.
     */
    @Override
    public void touchFile(final String name) throws IOException
    {
        if (log.isTraceEnabled())
        {
            log.trace("touchFile(" + name + ")");
        }
        persistentDirectory.touchFile(name);
        fastReadDirectory.touchFile(name);
    }

    /**
     * Attempt to clear (forcefully unlock and remove) the specified lock on the
     * persistent directory. Only call this at a time when you are certain that
     * this lock is no longer in use.
     *
     * @param name
     *            the name of the lock to clear
     *
     * @throws IOException
     *             on I/O error.
     */
    @Override
    public void clearLock(final String name) throws IOException
    {
        if (log.isTraceEnabled())
        {
            log.trace("clearLock(" + name + ")");
        }
        persistentDirectory.clearLock(name);
    }

    /**
     * Return a string identifier that uniquely differentiates this Directory
     * instance from other instances, passing through to the persistent
     * directory for a value. This ID should be the same if two Directory
     * indexes (even in different JVMs and/or on different machines) are
     * considered "the same index". This is how locking "scopes" to the right
     * index.
     *
     * @return a string identifier that uniquely differentiates this directory
     *         instance from other instances.
     */
    @Override
    public String getLockID()
    {
        if (log.isTraceEnabled())
        {
            log.trace("getLockID()");
        }
        return persistentDirectory.getLockID();
    }

    /**
     * Construct a lock by passing through to the persistent directory.
     *
     * @param name
     *            the name of the lock to create
     *
     * @return a Lock
     */
    @Override
    public Lock makeLock(final String name)
    {
        if (log.isTraceEnabled())
        {
            log.trace("makeLock(" + name + ")");
        }
        return persistentDirectory.makeLock(name);
    }

    /**
     * Ensure that any writes to this file are moved to stable storage on both
     * the persistent and read-fast directories. Lucene uses this to properly
     * commit changes to the index, to prevent a machine/OS crash from
     * corrupting the index.
     *
     * @param name
     *            the name of the file to sync
     *
     * @throws IOException
     *             on I/O error
     *
     */
    @Override
    public void sync(final String name) throws IOException
    {
        if (log.isTraceEnabled())
        {
            log.trace("sync(" + name + ")");
        }
        persistentDirectory.sync(name);
        fastReadDirectory.sync(name);
    }

}
