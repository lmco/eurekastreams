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
package org.eurekastreams.server.action.execution.stream;

import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.strategies.HashTagExtractor;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.HashTag;
import org.eurekastreams.server.domain.stream.StreamHashTag;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.chained.DecoratedPartialResponseDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.eurekastreams.server.persistence.mappers.stream.ActivityContentExtractor;

/**
 * Parse an Activity's content, find any hashtags, then store the hashtags in each stream that the activity falls under,
 * including the person or group destination stream, as well as all orgs up the hierarchy, if the activity is public.
 */
public class StoreStreamHashTagsForActivityStrategyImpl implements StoreStreamHashTagsForActivityStrategy
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Hashtag extractor.
     */
    private final HashTagExtractor hashTagExtractor;

    /**
     * Content extractor - pulls out content for hashtag parsing.
     */
    private final ActivityContentExtractor contentExtractor;

    /**
     * Mapper to store hash tags to an activity.
     */
    private final DecoratedPartialResponseDomainMapper<List<String>, List<HashTag>> hashTagMapper;

    /**
     * Mapper to insert stream hashtags.
     */
    private final InsertMapper<StreamHashTag> streamHashTagInsertMapper;

    /**
     * Constructor for the PostActivityAsyncExecutionStrategy class.
     * 
     * @param inContentExtractor
     *            the activity content extractor
     * @param inHashTagExtractor
     *            hash tag extractor
     * @param inHashTagMapper
     *            mapper to get hashtags from the database
     * @param inStreamHashTagInsertMapper
     *            mapper to insert stream hashtags
     */
    public StoreStreamHashTagsForActivityStrategyImpl(final HashTagExtractor inHashTagExtractor,
            final ActivityContentExtractor inContentExtractor,
            final DecoratedPartialResponseDomainMapper<List<String>, List<HashTag>> inHashTagMapper,
            final InsertMapper<StreamHashTag> inStreamHashTagInsertMapper)
    {
        hashTagExtractor = inHashTagExtractor;
        contentExtractor = inContentExtractor;
        hashTagMapper = inHashTagMapper;
        streamHashTagInsertMapper = inStreamHashTagInsertMapper;
    }

    /**
     * Parse and insert any necessary StreamHashTags for the input activity.
     * 
     * @param inActivity
     *            the activity to parse for stream hashtags
     */
    public void execute(final Activity inActivity)
    {
        log.info("Finding hashtags for activity #" + inActivity.getId());

        ScopeType scopeType = inActivity.getRecipientStreamScope().getScopeType();
        if (scopeType != ScopeType.GROUP && scopeType != ScopeType.PERSON)
        {
            log.info("This activity isn't a group or person stream - not handled.");
            return;
        }

        String recipientStreamKey = inActivity.getRecipientStreamScope().getUniqueKey();

        // create stream hashtag entries for each of the tags being applied
        StreamHashTag streamHashTag;

        List<String> hashTagStrings = getHashTags(inActivity);
        if (hashTagStrings.size() == 0)
        {
            return;
        }

        for (HashTag hashTag : hashTagMapper.execute(hashTagStrings))
        {
            // insert the activity hashtag for the destination stream
            if (log.isDebugEnabled())
            {
                log.debug("Adding StreamHashTag " + hashTag.getContent() + " for direct recipient stream of type "
                        + scopeType + ", key: " + recipientStreamKey + ", activity id: #" + inActivity.getId());
            }

            streamHashTag = new StreamHashTag(hashTag, inActivity, recipientStreamKey, scopeType);
            streamHashTagInsertMapper.execute(new PersistenceRequest<StreamHashTag>(streamHashTag));
        }
    }

    /**
     * Get a set of all of the hashtags for a the input activity, based on its content.
     * 
     * @param inActivity
     *            the activity to look for hashtags for
     * @return the collection of hashtags
     */
    private List<String> getHashTags(final Activity inActivity)
    {
        String content = contentExtractor.extractContent(inActivity.getBaseObjectType(), inActivity.getBaseObject());
        return hashTagExtractor.extractAll(content);
    }
}
