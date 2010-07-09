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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a data store where values can be retrieved (typically faster than database access).
 */
public interface Cache
{
    /**
     * Clears out all data in the cache.
     */
    void clear();

    /**
     * Gets a value from the cache.
     * 
     * @param inKey
     *            the key of the object to get.
     * @return the Object from the cache matching inKey (or null if nothing found).
     */
    Object get(String inKey);

    /**
     * Retrieve a list from cache (trim list to the maximum records kept in cache).
     * 
     * @param inKey
     *            the key of the object to get
     * @param inMaximumEntries
     *            the maximum number of entries this list can contain (if the list is over this number it will be
     *            trimmed in cache)
     * @return the ArrayList&lt;Long&gt; value that matches the key, null if not found
     */
    ArrayList<Long> getList(String inKey, int inMaximumEntries);

    /**
     * Retrieve a list from cache (trimming list @ getMaximumListSize() elements).
     * 
     * @param inKey
     *            the key of the object to get
     * @return the ArrayList&lt;Long&gt; value that matches the key, null if not found
     */
    ArrayList<Long> getList(String inKey);

    /**
     * Gets a collection of objects from the cache.
     * WARNING: DO NOT USE THIS METHOD TO RETRIEVE
     *          ArrayList&lt;Long&gt; TYPES.  THEY ARE 
     *          STORED AS byte[] AND YOU WILL GET CAST ERRORS.
     *          INSTEAD USE multiGetList(...)
     * 
     * @param inKeys
     *            a collection of keys to be retrieved.
     * @return a map containing the key/value pairs retrieved from the cache.
     */
    Map<String, Object> multiGet(Collection<String> inKeys);

    /**
     * Gets a collection of ArrayList&lt;Long&gt; from the cache.
     * 
     * @param inKeys
     *            a collection of keys corresponding to ArrayLists to be retrieved.
     * @return a map containing the key/value pairs retrieved from the cache.
     */
    Map<String, ArrayList<Long>> multiGetList(Collection<String> inKeys);    
    
    /**
     * Sets a value in the cache.
     * 
     * @param inKey
     *            the key of the object to set.
     * @param inValue
     *            the object to store in the cache.
     */
    void set(String inKey, Object inValue);

    /**
     * Sets a value in the cache replacing whatever was there before.
     * 
     * @param inKey
     *            the key of the object to set.
     * @param inValue
     *            the List&lt;Long&gt; to store in the cache.
     */
    void setList(String inKey, List<Long> inValue);

    /**
     * Sets a value in the cache replacing whatever was there before 
     * and returning the original value to the caller.
     * 
     * @param inKey
     *            the key of the object to set.
     * @param inValue
     *            the List&lt;Long&gt; to store in the cache.
     * @return the original value BEFORE replacing with the inValue, null if not found
     *         NOTE: If you need a method to return the value AFTER CAS, please
     *               create a new method (suggestion setListCASPost).
     */
    ArrayList<Long> setListCAS(String inKey, List<Long> inValue); 
    
    /**
     * Deletes a value from the cache.
     * 
     * @param inKey
     *            the key of the object to delete
     */
    void delete(String inKey);
    
    /**
     * Deletes a list value from the cache.
     * NOTE: Use this for deleting keys that correspond to lists
     *       since it will properly cleanup marker keys as well.
     * 
     * @param inKey
     *            the key of the object to delete
     */
    void deleteList(String inKey);

    /**
     * Prepends a Long value to a cached List of Long values. If the value is not yet in cache, it will be created.
     * 
     * @param inKey
     *            the key of the cached list.
     * @param inValues
     *            the long values items to add to the list.
     */
    void addToTopOfList(String inKey, List<Long> inValues);

    /**
     * Prepends a Long value to a cached List of Long values. If the value is not yet in cache, it will be created.
     * NOTE: If you are inserting many elements to the list with the same key PLEASE use addToTopOfList(String,
     * ArrayList&lt;Long&gt;) it is much more efficient with the network.
     * 
     * @param inKey
     *            the key of the cached list.
     * @param inValue
     *            the long value item to add to the list.
     */
    void addToTopOfList(String inKey, Long inValue);

    /**
     * Removes a specified value from a cached List of Long values.
     * 
     * @param inKey
     *            the key of the cached list.
     * @param inValue
     *            thh long value to remove from the list.
     */
    void removeFromList(String inKey, Long inValue);

    /**
     * Removes specified values from a List of cached keys pointing to lists of Long.
     * 
     * @param inKeys
     *            the keys of the cached lists to delete from.
     * @param inValues
     *            the list of values to remove from the lists.
     */
    void removeFromLists(List<String> inKeys, List<Long> inValues);

    /**
     * Adds a specified value to a cached Set of Long values. Use this method when working with Sets that are stored in
     * cache.
     * 
     * Note: Sets are unordered but offer the convenience of duplicate value protection. Use a set only when you want to
     * ensure a unique set of values and not concerned about ordering.
     * 
     * @param inKey
     *            - key for the set within cache.
     * @param inValue
     *            - value to add to the set.
     * @return the resulting set. If a set is not found in cache, a new Set will be created and returned with the value.
     */
    Set<Long> addToSet(String inKey, Long inValue);

    /**
     * Removes a specified value from a cached Set of Long values.
     * 
     * @param inKey
     *            the key of the cached set.
     * @param inValue
     *            the long value to remove from the set.
     */
    void removeFromSet(String inKey, Long inValue);

}
