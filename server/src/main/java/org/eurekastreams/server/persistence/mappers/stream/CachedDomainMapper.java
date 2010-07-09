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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.Cache;

/**
 * Abstract BaseDomainMapper that allows for a common place to inject the Cache.
 */
public abstract class CachedDomainMapper extends BaseDomainMapper
{
    /**
     * Logger instance.
     */
    private Log log = LogFactory.getLog(CachedDomainMapper.class);
    /**
     * Default maximum number of items to keep in any memcachedlist.
     */
    private static final int DEFAULT_MAX_LIST_SIZE = 10000;

    /**
     * Maximum number of items to keep in any memcached list.
     */
    private int maxListSize = DEFAULT_MAX_LIST_SIZE;

    /**
     * The cache.
     */
    private Cache cache;

    /**
     * Cache setter.
     *
     * @param inCache
     *            The cache.
     */
    public void setCache(final Cache inCache)
    {
        cache = inCache;
    }

    /**
     * Cache getter.
     *
     * @return the Cache.
     */
    public Cache getCache()
    {
        return cache;
    }
    
    /**
     * Based on results from hibernate query, cache followers/following correctly.
     * @param inResults The results from a hibernate query
     * @param inFollowersKey the constant key to use for cache indexing (ex. CacheKeys.FOLLOWERS_BY_PERSON)
     * @param inFollowingKey the constant key to use for cache indexing (ex. CacheKeys.PEOPLE_FOLLOWED_BY_PERSON)
     */
    protected void storeResultsInCache(
    		final List<Object[]> inResults, 
    		final String inFollowersKey, 
    		final String inFollowingKey)
    {
        // call 'set' on memcached once we've organized
        // each collection
        Map<String, ArrayList<Long>> followers = new HashMap<String, ArrayList<Long>>();
        Map<String, ArrayList<Long>> following = new HashMap<String, ArrayList<Long>>();
        for (Object[] result : inResults)
        {
            long followerId = (Long) result[0];
            long followingId = (Long) result[1];
            
            String followersKey = inFollowersKey + followingId;
            if (!followers.containsKey(followersKey))
            {
                followers.put(followersKey, new ArrayList<Long>());
            }
            ArrayList<Long> followersList = followers.get(followersKey);
            followersList.add(followerId);

            
            String followingKey = inFollowingKey + followerId;
            if (!following.containsKey(followingKey))
            {
                following.put(followingKey, new ArrayList<Long>());
            }
            ArrayList<Long> followingList = following.get(followingKey);
            followingList.add(followingId);        
        }
        
        for (String followerKey : followers.keySet())
        {
            // for each followerkey, grab the ArrayList and stuff into memcached
            getCache().setList(
                    followerKey,
                    followers.get(followerKey));
        }
        
        for (String followingKey : following.keySet())
        {
            // for each followingKey, grab the ArrayList and stuff into memcached
            getCache().setList(
                    followingKey,
                    following.get(followingKey));
        }
    }      

    /**
     * Retrieve the maximum list size configured for this mapper.  Default is 10000.
     * @return - max list size configured for this mapper.
     */
    public int getMaxListSize()
    {
        return maxListSize;
    }

    /**
     * Set the maximum list size if a value other than the default is required.
     * @param inMaxListSize - maximum number of items to maintain within the cached list.
     */
    public void setMaxListSize(final int inMaxListSize)
    {
        maxListSize = inMaxListSize;
    }
}
