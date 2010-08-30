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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.HashTag;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.chained.RefreshStrategy;

/**
 * Refresh strategy to insert HashTags in the database, lowercasing and making sure they start with octothorpe.
 */
public class HashTagDbRefreshStrategy extends BaseDomainMapper implements
        RefreshStrategy<Collection<String>, Collection<HashTag>>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Refresh the cache with the input content and hash tag.
     *
     * @param inContents
     *            the contents of the hashtags
     * @param inHashTags
     *            HashTags retrieved from another data source
     */
    @Override
    public void refresh(final Collection<String> inContents, final Collection<HashTag> inHashTags)
    {
        // scrub the inputs
        List<String> contents = new ArrayList<String>();
        for (String content : inContents)
        {
            if (!content.startsWith("#"))
            {
                content = "#" + content.toLowerCase();
            }
            else
            {
                content = content.toLowerCase();
            }
            contents.add(content);
        }

        // get all of the existing hashtag contents
        List<String> foundHashTagContents = getEntityManager().createQuery(
                "SELECT content FROM HashTag WHERE content IN (:contents)").setParameter("contents", contents)
                .getResultList();

        for (String content : contents)
        {
            if (foundHashTagContents.contains(content))
            {
                log.info("HashTag " + content + " already exists - skipping.");
                continue;
            }
            log.info("Inserting hashtag " + content + " in the database.");

            // INSERT the hastag
            HashTag ht = new HashTag(content);
            getEntityManager().persist(ht);
        }
    }
}
