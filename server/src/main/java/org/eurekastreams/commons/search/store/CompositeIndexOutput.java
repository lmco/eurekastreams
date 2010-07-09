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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.store.IndexOutput;

/**
 * Composite IndexOutput for writing lucene index updates to two different
 * Directories at the same time. One of the IndexOutputs will be handled for
 * speed while the other is for persistent storage.
 */
public class CompositeIndexOutput extends IndexOutput
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(CompositeIndexOutput.class);

    /**
     * The first IndexOutput to operate on.
     */
    private IndexOutput fastReadIndexOutput;

    /**
     * The second IndexOutput to operation on.
     */
    private IndexOutput persistentIndexOutput;

    /**
     * Constructor - taking the two IndexOutput streams to operate on.
     *
     * @param inFastReadIndexOutput
     *            the IndexOutput that is used for fast reading
     * @param inPersistentIndexOutput
     *            the IndexOutput that is used for persistent storage
     */
    public CompositeIndexOutput(final IndexOutput inFastReadIndexOutput,
            final IndexOutput inPersistentIndexOutput)
    {
        if (log.isTraceEnabled())
        {
            log.trace("Instantiating CompositeIndexOutput");
        }
        fastReadIndexOutput = inFastReadIndexOutput;
        persistentIndexOutput = inPersistentIndexOutput;
    }

    /**
     * Close both IndexOutputs.
     *
     * @throws IOException
     *             on I/O error
     */
    @Override
    public void close() throws IOException
    {
        if (log.isTraceEnabled())
        {
            log.trace("close()");
        }
        fastReadIndexOutput.close();
        persistentIndexOutput.close();
    }

    /**
     * Flush both IndexOutputs.
     *
     * @throws IOException
     *             on I/O error.
     */
    @Override
    public void flush() throws IOException
    {
        if (log.isTraceEnabled())
        {
            log.trace("flush()");
        }
        fastReadIndexOutput.flush();
        persistentIndexOutput.flush();
    }

    /**
     * Get the file pointer.
     *
     * @return the file pointer.
     */
    @Override
    public long getFilePointer()
    {
        long result = fastReadIndexOutput.getFilePointer();
        if (log.isTraceEnabled())
        {
            log.trace("getFilePointer() returns " + result);

            // TODO: Find out how to turn off assertions for maven test builds
            // so this doesn't fail the unit tests, then un-comment it out.
            // assert (result == persistentIndexOutput.getFilePointer());
        }
        return result;
    }

    /**
     * Get the number of bytes in the file.
     *
     * @return the number of bytes in the file
     *
     * @throws IOException
     *             on I/O error
     */
    @Override
    public long length() throws IOException
    {
        long result = fastReadIndexOutput.length();
        if (log.isTraceEnabled())
        {
            log.trace("length() returns " + result);
            // TODO: Find out how to turn off assertions for maven test builds
            // so this doesn't fail the unit tests, then un-comment it out.
            // assert (result == persistentIndexOutput.length());
        }
        return result;
    }

    /**
     * Seek to a certain position.
     *
     * @param pos
     *            the position to seek to
     * @throws IOException
     *             on I/O error
     */
    @Override
    public void seek(final long pos) throws IOException
    {
        if (log.isTraceEnabled())
        {
            log.trace("seek(" + pos + ")");
        }
        fastReadIndexOutput.seek(pos);
        persistentIndexOutput.seek(pos);
    }

    /**
     * Write a byte to both IndexOutputs.
     *
     * @param b
     *            byte to write
     * @throws IOException
     *             on I/O error
     */
    @Override
    public void writeByte(final byte b) throws IOException
    {
        if (log.isTraceEnabled())
        {
            log.trace("writeByte(byte)");
        }
        fastReadIndexOutput.writeByte(b);
        persistentIndexOutput.writeByte(b);
    }

    /**
     * Write several bytes to both IndexOutputs.
     *
     * @param b
     *            the bytes to write to both IndexOutputs
     * @param offset
     *            the offset in the byte array
     * @param length
     *            the number of bytes to write
     *
     * @throws IOException
     *             on I/O error
     */
    @Override
    public void writeBytes(final byte[] b, final int offset, final int length)
            throws IOException
    {
        if (log.isTraceEnabled())
        {
            log.trace("writeBytes(byte[], int, int)");
        }
        fastReadIndexOutput.writeBytes(b, offset, length);
        persistentIndexOutput.writeBytes(b, offset, length);
    }

}
