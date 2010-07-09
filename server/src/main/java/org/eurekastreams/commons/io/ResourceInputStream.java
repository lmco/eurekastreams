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
package org.eurekastreams.commons.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Parse a resource (packaged file) as an InputStream. The current class's class
 * loader is used to find the resource by name and wrap it as an InputStream.
 */
public class ResourceInputStream extends InputStream
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(ResourceInputStream.class);

    /**
     * The name of the resource, kept for logging.
     */
    private String resourceName;

    /**
     * The wrapped copy of the resource InputStream.
     */
    private InputStream inputStream;

    /**
     * Constructor that takes the inputStream - useful for unit testing.
     *
     * @param theInputStream
     *            the InputStream to wrap
     */
    protected ResourceInputStream(final InputStream theInputStream)
    {
        inputStream = theInputStream;
    }

    /**
     * Constructor taking the resource name to load.
     *
     * @param theResourceName
     *            the resource name/path to load.
     */
    public ResourceInputStream(final String theResourceName)
    {
        resourceName = theResourceName;

        if (log.isInfoEnabled())
        {
            log.info("Creating InputStream out of the resource with path: " + resourceName);
        }
        inputStream = ResourceInputStream.class.getResourceAsStream(resourceName);
    }

    /**
     * Reads the next byte of data from the input stream.
     *
     * @return the next byte of data from the input stream.
     * @throws IOException
     *             on error
     */
    @Override
    public int read() throws IOException
    {
        return inputStream.read();
    }

    /**
     * Returns the number of bytes that can be read (or skipped over) from this
     * input stream without blocking by the next caller of a method for this
     * input stream.
     *
     * @return the number of bytes that can be read (or skipped over) from this
     *         input stream without blocking by the next caller of a method for
     *         this input stream.
     * @throws IOException
     *             on error
     */
    @Override
    public int available() throws IOException
    {
        return inputStream.available();
    }

    /**
     * Closes this input stream and releases any system resources associated
     * with the stream.
     *
     * @throws IOException
     *             on error
     */
    @Override
    public void close() throws IOException
    {
        if (log.isInfoEnabled())
        {
            log.info("Closing the InputStream out of the resource with path: " + resourceName);
        }
        inputStream.close();
    }

    /**
     * Marks the current position in this input stream.
     *
     * @param readlimit
     *            allow this many bytes to be read before the mark position gets
     *            invalidated
     */
    @Override
    public synchronized void mark(final int readlimit)
    {
        inputStream.mark(readlimit);
    }

    /**
     * Tests if this input stream supports the 'mark' and 'reset' methods.
     *
     * @return whether the input stream supports the 'mark' and 'reset' methods.
     */
    @Override
    public boolean markSupported()
    {
        return inputStream.markSupported();
    }

    /**
     * Reads up to len bytes of data from the input stream into an array of
     * bytes.
     *
     * @param b
     *            buffer to store the bytes in
     * @param off
     *            the starting offset
     * @param len
     *            the number of bytes to load
     * @return the number of bytes read
     * @throws IOException
     *             on error
     */
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException
    {
        return inputStream.read(b, off, len);
    }

    /**
     * Reads some number of bytes from the input stream and stores them into the
     * buffer array b.
     *
     * @param b
     *            buffer to store the bytes in
     * @return the number of bytes read
     * @throws IOException
     *             on error
     */
    @Override
    public int read(final byte[] b) throws IOException
    {
        return inputStream.read(b);
    }

    /**
     * Repositions the stream to the position at the time the mark method was
     * last called on this input stream.
     *
     * @throws IOException
     *             on error
     */
    @Override
    public synchronized void reset() throws IOException
    {
        inputStream.reset();
    }

    /**
     * Skips over and discards n bytes of data from this input stream.
     *
     * @param n
     *            the number of bytes to skip over and discard
     * @return the actual number of bytes skipped.
     * @throws IOException
     *             on error
     */
    @Override
    public long skip(final long n) throws IOException
    {
        return inputStream.skip(n);
    }

}
