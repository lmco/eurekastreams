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
package org.eurekastreams.server.persistence.mappers.cache.testhelpers;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.cache.Cache;

/**
 * Simple Cache implementation for integration tests, storing the cached information in memory, rather than engaging an
 * external memcached server.
 */
public class SimpleMemoryCache implements Cache
{
    /**
     * Map to hold the ModelViews.
     */
    private Map<String, Object> cache = new HashMap<String, Object>();

    /**
     * Maximum number of items to keep in any memcached list.
     */
    private static final int MAX_LIST_SIZE = 10000;

    /**
     * Instance of the logger.
     */
    private final Log log = LogFactory.getLog(SimpleMemoryCache.class);

    /**
     * Clear the cache - call this in the setup method.
     */
    public void clear()
    {
        cache.clear();
    }

    /**
     * {@inheritDoc}
     */
    public int getMaximumListSize()
    {
        return MAX_LIST_SIZE;
    }

    /**
     * Gets a value from the cache.
     * 
     * @param inKey
     *            the key of the object to get.
     * @return the Object from the cache matching inKey (or null if nothing found).
     */
    public Object get(final String inKey)
    {
        return cache.get(inKey);
    }

    /**
     * {@inheritDoc}
     */
    public ArrayList<Long> getList(final String inKey)
    {
        return this.getList(inKey, this.getMaximumListSize());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Long> getList(final String inKey, final int inMaximumEntries)
    {
        ArrayList toReturn = null;
        byte[] bytes = (byte[]) cache.get(inKey);
        
        if (bytes != null)
        {
            try
            {
                toReturn = getListFromBytes(bytes);
            }
            catch (IOException e)
            {
                // problem getting the key .. return a null so the client goes
                // to the database
                log.error("Unable to retrieve LIST key " + inKey + " from memcached.  Exception " + e.getMessage());
                return null;
            }
        }

        // check list size and trim if necessary
        if (toReturn != null && toReturn.size() > inMaximumEntries)
        {
            ArrayList<Long> trimmed = (ArrayList<Long>) toReturn.subList(0, inMaximumEntries - 1);

            toReturn = trimmed;

            try
            {
                this.set(inKey, getBytesFromList(trimmed));
            }
            catch (IOException e)
            {
                log.error("Error getBytesFromList getList memcached list with passed in value for key " + inKey 
                        + ".  Exception : " + e.toString());
            }
        }

        return toReturn;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> multiGet(final Collection<String> inKeys)
    {
        Map<String, Object> results = new HashMap<String, Object>();
        for (String key : inKeys)
        {
            if (cache.containsKey(key))
            {
                results.put(key, cache.get(key));
            }
        }
        return results;
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
                ArrayList<Long> value = this.getListFromBytes((byte[]) multiGetMap.get(key));
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
    public void set(final String inKey, final Object inValue)
    {
        if (inValue != null)
        {
            cache.put(inKey, inValue);
        }
        else
        {
            throw new RuntimeException("DO NOT SET NULL, INSTEAD USE DELETE");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public ArrayList<Long> setListCAS(final String inKey, final List<Long> inValue)
    {
        if (inValue == null)
        {
            throw new RuntimeException("DO NOT setListCAS NULL, INSTEAD USE DELETE");
        }

        ArrayList<Long> toReturn = null;
        
        try
        {
            toReturn = getListFromBytes(cache.get(inKey));
        }
        catch (IOException e)
        {
            log.error("Error getListFromBytes setListCAS memcached list with passed in value for key " + inKey 
                    + ".  Exception : " + e.toString());
            return null;
        }
        
        try
        {
            cache.put(inKey, getBytesFromList(inValue));
        }
        catch (IOException e)
        {
            log.error("Error getBytesFromList setListCAS memcached list with passed in value for key " + inKey 
                    + ".  Exception : " + e.toString());
            return null;
        }
        
        return toReturn;
    }

    /**
     * {@inheritDoc}
     */
    public void setList(final String inKey, final List<Long> inValue)
    {
        if (inValue == null)
        {
            throw new RuntimeException("DO NOT setList NULL, INSTEAD USE DELETE");
        }
        
        try
        {
            cache.put(inKey, getBytesFromList(inValue));
        }
        catch (IOException e)
        {
            log.error("Error setting memcached list with passed in value for key " + inKey 
                    + ".  Exception : " + e.toString());
        }
    }
    
    /**
     * Get the byte[] from a ArrayList&lt;Long&gt;.
     * 
     * @param inListOfLongs
     *            the list of longs to convert
     * @return the byte[] representation of the ArrayList
     * @throws IOException thrown if any errors encountered
     */
    protected byte[] getBytesFromList(final List<Long> inListOfLongs) throws IOException
    {
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
     * @throws IOException thrown if any errors
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
    public void delete(final String inKey)
    {
        cache.remove(inKey);
    }
    
    /**
     * {@inheritDoc}
     */
    public void deleteList(final String inKey)
    {
        // for simple memory cache this simply
        // calls our existing delete ....
        // for 'real' cache this also
        // deletes a marker key
        this.delete(inKey);
    }

    /**
     * {@inheritDoc}
     */
    public void addToTopOfList(final String inKey, final List<Long> inValue)
    {
        if (inValue == null)
        {
            throw new RuntimeException("DO NOT addToTopOfList NULL, INSTEAD USE DELETE");
        }
        
        
        if (!cache.containsKey(inKey))
        {
            return;
        }
        
        ArrayList<Long> existingList = new ArrayList<Long>();
        try
        {
            existingList = getListFromBytes(cache.get(inKey));
        }
        catch (IOException e1)
        {
            log.error("Exception in addToTopOfList getListFromBytes for key " + inKey + e1.toString());
            return;
        }

        ArrayList<Long> listOfLongs = new ArrayList<Long>();        
        listOfLongs.addAll(inValue); // add new first (prepend)
        listOfLongs.addAll(existingList); // add the previously existing list second

        try
        {
            cache.put(inKey, this.getBytesFromList(listOfLongs));
        }
        catch (IOException e2)
        {
            log.error("Exception in addToTopOfList getBytesFromList for key " + inKey + e2.toString());
            return;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addToTopOfList(final String inKey, final Long inValue)
    {
        ArrayList<Long> longs = new ArrayList<Long>();
        longs.add(inValue);
        this.addToTopOfList(inKey, longs);
    }

    /**
     * {@inheritDoc}
     */
    public void removeFromList(final String inKey, final Long inValue)
    {
        if (!cache.containsKey(inKey))
        {
            return;
        }
        
        List<Long> list;
        try
        {
            list = getListFromBytes(cache.get(inKey));
            list.remove(inValue);
        }
        catch (IOException e)
        {
            log.error("Exception in removeFromList getListFromBytes for key " + inKey + e.toString());
            return;
        }

        try
        {
            cache.put(inKey, getBytesFromList(list));
        }
        catch (IOException e)
        {
            log.error("Exception in removeFromList getBytesFromList for key " + inKey + e.toString());
            return;
        }
    
    }

    /**
     * {@inheritDoc}
     */
    public void removeFromLists(final List<String> inKeys, final List<Long> inValues)
    {
        Map<String, ArrayList<Long>> results = multiGetList(inKeys);
        for (String key : results.keySet())
        {
            for (long value : inValues)
            {
                removeFromList(key, value);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Set<Long> addToSet(final String inKey, final Long inValue)
    {
        if (inValue == null)
        {
            throw new RuntimeException("DO NOT addToSet NULL, INSTEAD USE DELETE");
        }
        
        Set<Long> set;
        if (cache.containsKey(inKey))
        {
            set = (Set<Long>) cache.get(inKey);
        }
        else
        {
            set = new HashSet<Long>();
        }
        set.add(inValue);
        cache.put(inKey, set);
        return set;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void removeFromSet(final String inKey, final Long inValue)
    {
        if (inValue == null)
        {
            throw new RuntimeException("DO NOT removeFromSet NULL, INSTEAD USE DELETE");
        }
        
        
        if (!cache.containsKey(inKey))
        {
            return;
        }
        Set<Long> set = (Set<Long>) cache.get(inKey);
        set.remove(inValue);
    }
}
