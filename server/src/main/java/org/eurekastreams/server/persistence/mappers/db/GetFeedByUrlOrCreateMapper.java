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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.Date;
import java.util.List;

import org.eurekastreams.server.domain.stream.plugins.Feed;
import org.eurekastreams.server.domain.stream.plugins.PluginDefinition;
import org.eurekastreams.server.persistence.mappers.ReadMapper;
import org.eurekastreams.server.persistence.mappers.requests.GetFeedByUrlRequest;

/**
 * DB Mapper to fetch the IDs of all activities where the input person authored either the first or last comment.
 */
public class GetFeedByUrlOrCreateMapper extends ReadMapper<GetFeedByUrlRequest, Feed>
{
    /**
     * The number of milliseconds in a minute.
     */
    private static final int MS_IN_MIN = 60000;

    /**
     * Execute.
     *
     * @param inRequest
     *            the request.
     * @return the Feed.
     */

    @SuppressWarnings("unchecked")
    @Override
    public Feed execute(final GetFeedByUrlRequest inRequest)
    {
        String query = "FROM Feed WHERE streamPlugin.id=:pluginId AND url = :url";

        List<Feed> feeds = getEntityManager().createQuery(query).setParameter("pluginId", inRequest.getPluginId())
                .setParameter("url", inRequest.getUrl()).getResultList();

        if (feeds.size() > 0)
        {
            return feeds.get(0);
        }

        Feed feed = new Feed();
        feed.setLastUpdated(new Date().getTime() / MS_IN_MIN);
        feed.setLastPostDate(new Date());
        feed.setUrl(inRequest.getUrl());
        feed.setTitle("Undefined");
        feed.setPlugin(getEntityManager().find(PluginDefinition.class, inRequest.getPluginId()));
        getEntityManager().persist(feed);

        return feed;
    }
}
