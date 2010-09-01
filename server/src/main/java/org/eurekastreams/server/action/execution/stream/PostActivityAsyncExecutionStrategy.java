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

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.strategies.HashTagExtractor;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.HashTag;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.chained.DecoratedPartialResponseDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.ActivityContentExtractor;
import org.eurekastreams.server.persistence.mappers.stream.PostCachedActivity;

/**
 * This class provides the Async ExecutionStrategy for the PostActivity action.
 *
 * Add the activity to cache and compute its hashtags.
 *
 */
public class PostActivityAsyncExecutionStrategy implements ExecutionStrategy<AsyncActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Mapper to perform cache post.
     */
    private final PostCachedActivity postCachedActivityMapper;

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
     * Mapper to find an activity by id.
     */
    private final FindByIdMapper<Activity> findByIdMapper;

    /**
     * Constructor for the PostActivityAsyncExecutionStrategy class.
     *
     * @param inPostCachedActivityMapper
     *            - instance of the {@link PostCachedActivity} mapper that will perform the cache updates.
     * @param inContentExtractor
     *            the activity content extractor
     * @param inHashTagExtractor
     *            hash tag extractor
     * @param inHashTagMapper
     *            mapper to get hashtags from the database
     * @param inFindByIdMapper
     *            mapper to find an activity by id
     */
    public PostActivityAsyncExecutionStrategy(final PostCachedActivity inPostCachedActivityMapper,
            final HashTagExtractor inHashTagExtractor, final ActivityContentExtractor inContentExtractor,
            final DecoratedPartialResponseDomainMapper<List<String>, List<HashTag>> inHashTagMapper,
            final FindByIdMapper<Activity> inFindByIdMapper)
    {
        postCachedActivityMapper = inPostCachedActivityMapper;
        hashTagExtractor = inHashTagExtractor;
        contentExtractor = inContentExtractor;
        hashTagMapper = inHashTagMapper;
        findByIdMapper = inFindByIdMapper;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Serializable execute(final AsyncActionContext inActionContext) throws ExecutionException
    {
        ActivityDTO currentActivity = ((PostActivityRequest) inActionContext.getParams()).getActivityDTO();

        log.info("Updating caches for activity #" + currentActivity.getId());
        postCachedActivityMapper.execute(currentActivity);

        Activity activity = findByIdMapper.execute(new FindByIdRequest("Activity", currentActivity.getId()));
        if (activity == null)
        {
            log.info("Activity #" + currentActivity.getId() + " was not found - no need to set its hashtags");
            return null;
        }

        log.info("Finding hashtags from strings.");
        List<String> hashTagStrings = getHashTags(currentActivity);
        if (hashTagStrings.size() > 0)
        {
            if (log.isInfoEnabled())
            {
                log.info("Found hash tags: " + hashTagStrings.toString() + " - attaching them to activity #"
                        + currentActivity.getId());
            }
            List<HashTag> hashTags = hashTagMapper.execute(hashTagStrings);
            activity.setHashTags(new HashSet<HashTag>(hashTags));
        }
        return null;
    }

    /**
     * Get a set of all of the hashtags for a the input activity, based on its content.
     *
     * @param inActivity
     *            the activity to look for hashtags for
     * @return the collection of hashtags
     */
    private List<String> getHashTags(final ActivityDTO inActivity)
    {
        String content = contentExtractor.extractContent(inActivity.getBaseObjectType(), inActivity
                .getBaseObjectProperties());

        return hashTagExtractor.extractAll(content);
    }

}
