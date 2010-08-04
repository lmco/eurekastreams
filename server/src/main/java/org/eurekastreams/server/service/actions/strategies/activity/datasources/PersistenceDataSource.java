/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.actions.strategies.activity.datasources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.service.actions.strategies.activity.ListCollider;

/**
 * Gets activity IDs from memcache based on the query.
 *
 */
public class PersistenceDataSource implements DescendingOrderDataSource
{
    /**
     * A map of search params and key generators.
     */
    private HashMap<String, DomainMapper<Object, List<Long>>> memcacheKeyGens;

    /**
     * Transformers.
     */
    private HashMap<String, PersistenceDataSourceRequestTransformer> transformers;

    /**
     * Everyone mapper.
     */
    private DomainMapper<Object, List<Long>> everyoneMapper;
    /**
     * The or collider.
     */
    private ListCollider orCollider;


    /**
     * The max we want this data source to return.
     */
    private static final int MAXITEMS = 10000;

    /**
     * Default constructor.
     *
     * @param inEveryoneMapper the everyoneMapper
     * @param inMemcacheKeyGens
     *            the key generators.
     * @param inTransformers the transformers.
     * @param inOrCollider
     *            collider.
     */
    public PersistenceDataSource(final DomainMapper<Object, List<Long>> inEveryoneMapper,
            final HashMap<String, DomainMapper<Object, List<Long>>> inMemcacheKeyGens,
            final HashMap<String, PersistenceDataSourceRequestTransformer> inTransformers,
            final ListCollider inOrCollider)
    {
        everyoneMapper = inEveryoneMapper;
        memcacheKeyGens = inMemcacheKeyGens;
        transformers = inTransformers;
        orCollider = inOrCollider;
    }

    /**
     * Given the request, give me back all the results relevant from memcache.
     *
     * @param request
     *            the JSON request from the user.
     * @return the list of activity longs.
     */
    public List<Long> fetch(final JSONObject request)
    {
        boolean unHandled = false;
        List<List<Long>> returnedDataSets = new ArrayList<List<Long>>();

        if (request.getJSONObject("query").size() == 0)
        {
            // get everyone list
            returnedDataSets.add(everyoneMapper.execute(null));
        }
        else
        {
            for (Object objParam : request.getJSONObject("query").keySet())
            {


                DomainMapper<Object, List<Long>> mapper = memcacheKeyGens.get(objParam);

                if (mapper != null)
                {
                    if (transformers.containsKey(objParam) && transformers.get(objParam) != null)
                    {
                        returnedDataSets.add(mapper.execute(
                                transformers.get(objParam).transform(request.getJSONObject("query"))));
                    }
                    else
                    {
                        returnedDataSets.add(mapper.execute(request.getJSONObject("query")));
                    }
                }
                else
                {
                    unHandled = true;
                }
            }
            if (returnedDataSets.size() == 0)
            {
                // if the query isn't empty, but we don't handle any of it, return null, stating such
                return null;
            }
        }

        List<Long> returned = new ArrayList<Long>();

        for (List<Long> dataSet : returnedDataSets)
        {
            Integer maxCount = request.getInt("count");

            if (unHandled)
            {
                maxCount = MAXITEMS;
            }

            returned = orCollider.collide(dataSet, returned, maxCount);
        }

        return returned;
    }

}
