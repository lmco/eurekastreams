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
package org.eurekastreams.server.persistence.mappers.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.spy.memcached.CASMutation;
import net.spy.memcached.CASMutator;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.transcoders.SerializingTranscoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Cache implementation that stores data using memcached.
 */
public class MemcachedCache implements Cache
{
    /**
     * Instance of the logger.
     */
    private final Log log = LogFactory.getLog(MemcachedCache.class);

    /**
     * The memcached client.
     */
    private MemcachedClient client;

    /**
     * Maximum key expiration time allowed by memcached.
     */
    private static final int MAX_EXPIRATION_TIME = 60 * 60 * 24 * 30;

    /**
     * Maximum number of items to keep in any memcached list.
     */
    private int maxListSize;

    /**
     * Constructor.
     *
     * @param inClient
     *            the memcached client to set.
     * @param inMaxListSize
     *            the maximum size a list can grow to before it is trimmed
     */
    public MemcachedCache(final MemcachedClient inClient, final int inMaxListSize)
    {
        client = inClient;
        maxListSize = inMaxListSize;
    }

    /**
     * @return the maxListSize
     */
    public int getMaxListSize()
    {
        return maxListSize;
    }

    /**
     * @param inMaxListSize
     *            the maxListSize to set
     */
    public void setMaxListSize(final int inMaxListSize)
    {
        this.maxListSize = inMaxListSize;
    }

    /**
     * {@inheritDoc}
     */
    public void clear()
    {
        log.trace("Flushing client");
        client.flush();
    }

    /**
     * {@inheritDoc}
     */
    public Object get(final String inKey)
    {
        if (log.isTraceEnabled())
        {
            log.trace("Getting " + inKey);
        }

        return client.get(inKey);
    }

    /**
     * {@inheritDoc}
     */
    public void set(final String inKey, final Object inValue)
    {
        if (log.isTraceEnabled())
        {
            log.trace("Setting to cache '" + inKey + "' - " + inValue);
        }

        if (inValue != null)
        {
            client.set(inKey, MAX_EXPIRATION_TIME, inValue);
        }
        else
        {
            // a null really should be a delete of the key
            log.warn("null passed in as parameter for key " + inKey
                    + ".  Deleting key from memcached to force client reload.");
            this.delete(inKey);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ArrayList<Long> setListCAS(final String inKey, final List<Long> inValue)
    {
        // get the collection in the right shape
        // to assign to 'final' newInValue
        List<Long> tempInValue = inValue;
        if (tempInValue == null)
        {
            tempInValue = new ArrayList<Long>();
        }

        // use newInValue moving forward
        final List<Long> newInValue = tempInValue;

        if (log.isTraceEnabled())
        {
            log.trace("Comparing and swapping list  '" + inKey + "', value: " + newInValue);
        }

        final ArrayList<Long> toReturn = new ArrayList<Long>();
        CASMutation<Object> mutation = new CASMutation<Object>()
        {
            // This is only invoked when the list already exists.
            public Object getNewValue(final Object current)
            {
                Object returnToMemcache = null;

                // store the current value in memcache (to return to caller)
                // but return to memcache the return from getBytesFromList since that's
                // really what the caller wants in memcache
                try
                {
                    ArrayList<Long> list = getListFromBytes(current);
                    if (list != null)
                    {
                        toReturn.addAll(list);
                    }
                }
                catch (IOException e)
                {
                    log.error("Error in getListFromBytes setting memcached list with passed in value for key " + inKey
                            + ".  Exception : " + e.toString());
                }

                try
                {
                    // grab the byte[] from what the user passed in to assign to memcache
                    returnToMemcache = getBytesFromList(newInValue);
                }
                catch (IOException e)
                {
                    log.error("Error in getBytesFromList setting memcached list with passed in value for key " + inKey
                            + ".  Exception : " + e.toString());
                }

                return returnToMemcache;
            }
        };

        SerializingTranscoder transcoder = new SerializingTranscoder();
        CASMutator<Object> mutator = new CASMutator<Object>(client, transcoder);

        // This returns whatever value was successfully stored within the cache,
        // either the initial list (as bytes) or a mutated existing one
        try
        {
            // The initial value -- only used when there's no list stored under the
            // key.
            byte[] initialValue = getBytesFromList(inValue);

            ArrayList<Long> list = getListFromBytes(mutator.cas(inKey, initialValue, MAX_EXPIRATION_TIME, mutation));

            return toReturn;
        }
        catch (Exception e)
        {
            // CAS timeout exceeded, return null because nothing was
            // stored in cache.
            log.error("Error in setListCAS communicating with memcache.  Key " + inKey + " Exception " + e.toString());
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setList(final String inKey, final List<Long> inValue)
    {
        if (log.isTraceEnabled())
        {
            log.trace("Setting List<Long> (serialized as byte[]) to cache '" + inKey + "' - " + inValue);
        }

        if (inValue == null)
        {
            // cannot pass null to memcached
            log.warn("In setList, attempting to pass in NULL to memcached for key " + inKey);
            return;
        }

        try
        {
            byte[] bytesToSet = getBytesFromList(inValue);
            if (bytesToSet != null)
            {
                this.set(inKey, bytesToSet);
            }
            else
            {
                // a null here really means delete the key
                this.delete(inKey);
            }
        }
        catch (IOException e)
        {
            log.error("Error setting memcached list with passed in value for key " + inKey + ".  Exception : "
                    + e.toString());
        }

    }

    /**
     * {@inheritDoc}
     */
    public void delete(final String inKey)
    {
        if (log.isTraceEnabled())
        {
            log.trace("Deleting: '" + inKey + "'");
        }
        client.delete(inKey);
    }

    /**
     * {@inheritDoc}
     */
    public void deleteList(final String inKey)
    {
        this.delete(inKey);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> multiGet(final Collection<String> inKeys)
    {
        if (log.isTraceEnabled())
        {
            log.trace("Getting bulk: " + inKeys.toString());
        }
        return client.getBulk(inKeys);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, ArrayList<Long>> multiGetList(final Collection<String> inKeys)
    {
        Map<String, Object> multiGetMap = this.multiGet(inKeys);

        Map<String, ArrayList<Long>> toReturn = new HashMap<String, ArrayList<Long>>();
        for (String key : multiGetMap.keySet())
        {
            // look up the value, convert it to an ArrayList and add it to the return
            try
            {
                ArrayList<Long> value = this.getListFromBytes(multiGetMap.get(key));
                toReturn.put(key, value);
            }
            catch (IOException e)
            {
                log.error("Error getListFromBytes multiGetList memcached list with passed in value for key " + key
                        + ".  Exception : " + e.toString());
                toReturn.put(key, null); // return null to force client to reload
            }
        }

        return toReturn;
    }

    /**
     * {@inheritDoc}
     */
    public void addToTopOfList(final String inKey, final Long inValue)
    {
        ArrayList<Long> toAdd = new ArrayList<Long>();
        toAdd.add(inValue);

        this.addToTopOfList(inKey, toAdd);
    }

    /**
     * {@inheritDoc}
     */
    public void addToTopOfList(final String inKey, final List<Long> inValue)
    {
        if (inValue == null)
        {
            // cannot pass null to memcached
            log.warn("In addToTopOfList, attempting to pass in NULL to memcached for key " + inKey);
            return;
        }

        if (log.isTraceEnabled())
        {
            StringBuilder allLongs = new StringBuilder();
            for (Long lng : inValue)
            {
                allLongs.append(lng.toString());
                allLongs.append(',');
            }

            // trim the last , just for clarity in the log
            if (allLongs.charAt(allLongs.length() - 1) == ',')
            {
                allLongs.deleteCharAt(allLongs.length() - 1);
            }

            log.trace("Prepending to list '" + inKey + "', values: " + allLongs.toString());
        }

        // NOTE: memcached will NOT create a List if one does
        // not exist already, it will silently fail.
        // THIS IS WHAT WE WANT!

        // try to prepend data, NOTE the cas key is ignored
        // in ASCII protocol which we are using
        byte[] bytesToPrepend;
        try
        {
            bytesToPrepend = this.getBytesFromList(inValue);
        }
        catch (IOException e)
        {
            // problem getting the key .. set a null so the client goes
            // to the database and clear marker so next time we'll try again
            log.error("Unable to prepend LIST key " + inKey + " into memcached.  Exception " + e.getMessage());
            bytesToPrepend = null;
        }

        if (bytesToPrepend != null)
        {
            client.prepend(0, inKey, bytesToPrepend);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ArrayList<Long> getList(final String inKey)
    {
        return this.getList(inKey, this.getMaxListSize());
    }

    /**
     * {@inheritDoc}
     */
    public ArrayList<Long> getList(final String inKey, final int inMaximumEntries)
    {
        // This is how we modify a list when we find one in the cache.
        CASMutation<Object> mutation = new CASMutation<Object>()
        {

            // This is only invoked when a value actually exists.
            public Object getNewValue(final Object current)
            {
                // retrieve the list from the bytes stored in memcache
                ArrayList<Long> toReturn = new ArrayList<Long>();
                try
                {
                    toReturn = getListFromBytes(current);
                }
                catch (IOException e)
                {
                    // problem getting the key .. return a null so the client goes
                    // to the database
                    log.error("Unable to retrieve LIST key " + inKey + " from memcached.  Exception " + e.getMessage());
                    toReturn = null;
                }

                // check list size and trim if necessary
                if (toReturn != null && toReturn.size() > inMaximumEntries)
                {
                    ArrayList<Long> trimmed = (ArrayList<Long>) toReturn.subList(0, inMaximumEntries - 1);

                    // set the trimmed list back to memcached
                    toReturn = trimmed;

                    if (log.isInfoEnabled())
                    {
                        log.info("Trimming list " + inKey + " to size of " + inMaximumEntries);
                    }
                }

                try
                {
                    return getBytesFromList(toReturn);
                }
                catch (IOException e)
                {
                    // CAS timeout exceeded, return null because nothing was
                    // stored in cache.
                    log.error("Error in getList in getBytesFromList.  Key " + inKey + " Exception " + e.toString());
                    return null;
                }
            }
        };

        // The mutator who'll do all the low-level stuff.
        SerializingTranscoder transcoder = new SerializingTranscoder();
        CASMutator<Object> mutator = new CASMutator<Object>(client, transcoder);

        // This returns whatever value was successfully stored within the
        // cache -- either the initial list as above, or a mutated existing
        // one
        try
        {
            // The initial value -- only used when there's no list stored under
            // the key.
            byte[] initialValue = null;

            return getListFromBytes(mutator.cas(inKey, initialValue, MAX_EXPIRATION_TIME, mutation));
        }
        catch (Exception e)
        {
            // error, return null because nothing was stored in cache.
            log.error("Error in getList, general exception.  Key " + inKey + " Exception " + e.toString());
            return null;
        }
    }

    /**
     * Get the byte[] from a ArrayList&lt;Long&gt;.
     *
     * @param inListOfLongs
     *            the list of longs to convert
     * @return the byte[] representation of the ArrayList
     * @throws IOException
     *             thrown if any errors encountered
     */
    protected byte[] getBytesFromList(final List<Long> inListOfLongs) throws IOException
    {
        if (inListOfLongs == null)
        {
            return null;
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);
        byte[] toReturn = null;
        try
        {
            for (Long oneLong : inListOfLongs)
            {
                out.writeLong(oneLong);
                out.flush();
            }

            toReturn = bytes.toByteArray();
        }
        finally
        {
            out.close();
        }

        return toReturn;
    }

    /**
     * Convert the memcached object into a List&lt;Long&gt;.
     *
     * @param inBytesOfLongs
     *            the byte[] to convert
     * @return the byte[] as List&lt;Long&gt;, null if not valid or empty bytes
     * @throws IOException
     *             thrown if any errors
     */
    protected ArrayList<Long> getListFromBytes(final Object inBytesOfLongs) throws IOException
    {
        if (inBytesOfLongs == null)
        {
            return null;
        }

        ArrayList<Long> toReturn = new ArrayList<Long>();

        ByteArrayInputStream bytes = new ByteArrayInputStream((byte[]) inBytesOfLongs);
        DataInputStream input = new DataInputStream(bytes);
        try
        {
            while (input.available() > 0)
            {
                toReturn.add(input.readLong());
            }
        }
        finally
        {
            input.close();
        }

        return toReturn;
    }

    /**
     * {@inheritDoc}
     */
    public void removeFromList(final String inKey, final Long inValue)
    {
        if (log.isTraceEnabled())
        {
            log.trace("Removing from list '" + inKey + "', value: " + inValue);
        }

        CASMutation<Object> mutation = new CASMutation<Object>()
        {
            // This is only invoked when a list already exists.
            public Object getNewValue(final Object current)
            {
                Object toReturn = current;
                List<Long> list;
                try
                {
                    list = getListFromBytes(current);
                    if (list != null)
                    {
                        list.remove(inValue);
                    }

                    // always convert back to byte[] to return to memcached
                    toReturn = getBytesFromList(list);
                }
                catch (IOException e)
                {
                    // we had a problem serializing from cache ... log an error
                    // and continue assuming the app
                    // will reload from database for the next reader
                    log.error("Unable to retrieve key " + inKey + " from memcached.  Not able to delete " + inValue
                            + " from list.");
                }
                return toReturn;
            }
        };

        SerializingTranscoder transcoder = new SerializingTranscoder();
        CASMutator<Object> mutator = new CASMutator<Object>(client, transcoder);

        // This returns whatever value was successfully stored within the cache,
        // either the
        // initial list or a mutated existing one
        try
        {
            byte[] initialValue = null;
            mutator.cas(inKey, initialValue, MAX_EXPIRATION_TIME, mutation);
        }
        catch (Exception e)
        {
            log.error("Nothing to delete from memcached for key - " + inKey, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeFromLists(final List<String> inKeys, final List<Long> inValues)
    {
        Map<String, ArrayList<Long>> results = multiGetList(inKeys);
        for (final String key : results.keySet())
        {
            if (log.isTraceEnabled())
            {
                log.trace("Removing from list '" + key + "', values: " + inValues);
            }

            CASMutation<Object> mutation = new CASMutation<Object>()
            {
                // This is only invoked when a list already exists.
                public Object getNewValue(final Object current)
                {
                    Object toReturn = current;
                    List<Long> list;
                    try
                    {
                        list = getListFromBytes(current);
                        if (list != null)
                        {
                            list.removeAll(inValues);
                        }

                        // always convert back to byte[] to return to memcached
                        toReturn = getBytesFromList(list);
                    }
                    catch (IOException e)
                    {
                        // we had a problem serializing from cache ... log an error
                        // and continue assuming the app will reload from database for the next reader
                        log.error("Unable to retrieve key " + key + " from memcached.  Not able to delete " + inValues
                                + " from list.");
                    }
                    return toReturn;
                }
            };

            SerializingTranscoder transcoder = new SerializingTranscoder();
            CASMutator<Object> mutator = new CASMutator<Object>(client, transcoder);

            // This returns whatever value was successfully stored within the cache,
            // either the initial list or a mutated existing one
            try
            {
                byte[] initialValue = null;
                mutator.cas(key, initialValue, MAX_EXPIRATION_TIME, mutation);
            }
            catch (Exception e)
            {
                log.error("Nothing to delete from memcached for key - " + key, e);
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Set<Long> addToSet(final String inKey, final Long inValue)
    {
        if (log.isTraceEnabled())
        {
            log.trace("Adding to set '" + inKey + "', value: " + inValue);
        }
        CASMutation<Object> mutation = new CASMutation<Object>()
        {
            // This is only invoked when a set already exists.
            public Object getNewValue(final Object current)
            {
                Set<Long> set = (Set<Long>) current;
                HashSet<Long> hashSet = new HashSet<Long>(set);
                hashSet.add(inValue);

                return hashSet;
            }
        };

        SerializingTranscoder transcoder = new SerializingTranscoder();
        CASMutator<Object> mutator = new CASMutator<Object>(client, transcoder);

        // This returns whatever value was successfully stored within the cache,
        // either the
        // initial set or a mutated existing one
        try
        {
            // Use null for initial value in this call. If set is not cached, leave it as null
            // so set is entirely initialized when requested.
            return (HashSet<Long>) mutator.cas(inKey, null, MAX_EXPIRATION_TIME, mutation);
        }
        catch (Exception e)
        {
            // CAS timeout exceeded, return empty set because nothing was
            // stored in cache.
            return new HashSet<Long>();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeFromSet(final String inKey, final Long inValue)
    {
        if (log.isTraceEnabled())
        {
            log.trace("Removing from set '" + inKey + "', value: " + inValue);
        }
        CASMutation<Object> mutation = new CASMutation<Object>()
        {
            // This is only invoked when a set already exists.
            @SuppressWarnings("unchecked")
            public Object getNewValue(final Object current)
            {
                Set<Long> set = (Set<Long>) current;
                HashSet<Long> hashSet = new HashSet<Long>(set);
                hashSet.remove(inValue);
                return hashSet;
            }
        };

        SerializingTranscoder transcoder = new SerializingTranscoder();
        CASMutator<Object> mutator = new CASMutator<Object>(client, transcoder);

        // This returns whatever value was successfully stored within the cache,
        // either the
        // initial set or a mutated existing one
        try
        {
            mutator.cas(inKey, new HashSet<Long>(), MAX_EXPIRATION_TIME, mutation);
        }
        catch (Exception e)
        {
            log.error("Nothing to delete from memcached for key - " + inKey, e);
        }
    }
}
