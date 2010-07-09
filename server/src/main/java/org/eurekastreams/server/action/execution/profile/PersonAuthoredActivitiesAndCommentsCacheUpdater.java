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
package org.eurekastreams.server.action.execution.profile;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.cache.UpdateAuthorInfoInActivityEmbeddedCachedComments;
import org.eurekastreams.server.persistence.mappers.cache.UpdateAuthorInfoInCachedActivities;
import org.eurekastreams.server.persistence.mappers.cache.UpdateAuthorInfoInCachedComments;
import org.eurekastreams.server.persistence.mappers.db.GetActivityCommentIdsAuthoredByPersonId;
import org.eurekastreams.server.persistence.mappers.db.GetActivityIdsAuthoredByOrOriginallyAuthoredByEntity;
import org.eurekastreams.server.persistence.mappers.db.GetActivityIdsWithFirstOrLastCommentsAuthoredByPersonId;

/**
 * Update person info in cache for all activities and activity comments authored by a user, or originally authored by a
 * user.
 */
public class PersonAuthoredActivitiesAndCommentsCacheUpdater implements ExecutionStrategy<ActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * DB Mapper to get the activity ids authored by the current user.
     */
    private GetActivityIdsAuthoredByOrOriginallyAuthoredByEntity getActivityIdsAuthordedByEntityDbMapper;

    /**
     * DB Mapper to get all the comment ids authored by the current user.
     */
    private GetActivityCommentIdsAuthoredByPersonId getActivityCommentIdsAuthoredByPersonIdDbMapper;

    /**
     * DB Mapper to get all the activity ids where the current user authored either their first or last comment.
     */
    private GetActivityIdsWithFirstOrLastCommentsAuthoredByPersonId
    // line break
    getActivityIdsWithFirstOrLastCommentsAuthoredByPersonIdDbMapper;

    /**
     * Cache mapper to update the info for the author of activities.
     */
    private UpdateAuthorInfoInCachedActivities updateAuthorInfoInCachedActivitiesCacheMapper;

    /**
     * Cache mapper to update the info for the author of activity comments.
     */
    private UpdateAuthorInfoInCachedComments updateAuthorInfoInCachedCommentsCacheMapper;

    /**
     * Cache mapper that updates cached activity dtos by making the embedded comment dtos' author info current if the
     * input user authored either of them.
     */
    private UpdateAuthorInfoInActivityEmbeddedCachedComments
    // line break
    updateAuthorInfoInActivityEmbeddedCachedCommentsCacheMapper;

    /**
     * Mapper to load the activity author from database.
     */
    private PersonMapper personMapper;

    /**
     * Constructor.
     *
     * @param inGetActivityIdsAuthordedByEntityDbMapper
     *            DB mapper to get all activity ids authored by a user
     * @param inGetActivityCommentIdsAuthoredByPersonIdDbMapper
     *            DB mapper to get all activity comment ids authored by a user
     * @param inGetActivityIdsWithFirstOrLastCommentsAuthoredByPersonIdDbMapper
     *            DB mapper to get a list of all activity ids that a user has authored either the first or last comment
     *            for
     * @param inUpdateAuthorInfoInCachedActivitiesCacheMapper
     *            cache mapper to update all activities already in cache with the user's info
     * @param inUpdateAuthorInfoInCachedCommentsCacheMapper
     *            cache mapper to update all activities' comments already in cache with the user's info
     * @param inUpdateAuthorInfoInActivityEmbeddedCachedCommentsCacheMapper
     *            cache mapper to update embedded comments inside activityDTOs (first/last comments)
     * @param inPersonMapper
     *            DB mapper to get the person from the database
     */
    public PersonAuthoredActivitiesAndCommentsCacheUpdater(
            final GetActivityIdsAuthoredByOrOriginallyAuthoredByEntity inGetActivityIdsAuthordedByEntityDbMapper,
            final GetActivityCommentIdsAuthoredByPersonId inGetActivityCommentIdsAuthoredByPersonIdDbMapper,
            final GetActivityIdsWithFirstOrLastCommentsAuthoredByPersonId
            // line break
            inGetActivityIdsWithFirstOrLastCommentsAuthoredByPersonIdDbMapper,
            final UpdateAuthorInfoInCachedActivities inUpdateAuthorInfoInCachedActivitiesCacheMapper,
            final UpdateAuthorInfoInCachedComments inUpdateAuthorInfoInCachedCommentsCacheMapper,
            final UpdateAuthorInfoInActivityEmbeddedCachedComments
            // line break
            inUpdateAuthorInfoInActivityEmbeddedCachedCommentsCacheMapper, final PersonMapper inPersonMapper)
    {
        getActivityIdsAuthordedByEntityDbMapper = inGetActivityIdsAuthordedByEntityDbMapper;
        getActivityCommentIdsAuthoredByPersonIdDbMapper = inGetActivityCommentIdsAuthoredByPersonIdDbMapper;

        getActivityIdsWithFirstOrLastCommentsAuthoredByPersonIdDbMapper
        // line break
        = inGetActivityIdsWithFirstOrLastCommentsAuthoredByPersonIdDbMapper;

        updateAuthorInfoInCachedActivitiesCacheMapper = inUpdateAuthorInfoInCachedActivitiesCacheMapper;
        updateAuthorInfoInCachedCommentsCacheMapper = inUpdateAuthorInfoInCachedCommentsCacheMapper;

        updateAuthorInfoInActivityEmbeddedCachedCommentsCacheMapper
        // line break
        = inUpdateAuthorInfoInActivityEmbeddedCachedCommentsCacheMapper;
        personMapper = inPersonMapper;
    }

    /**
     * Update the cache for all activities and comments authored by the user making this request, setting the info to
     * the user's current info.
     *
     * @param inActionContext
     *            the context, containing the user id
     * @return nothing special
     */
    @Override
    public Serializable execute(final ActionContext inActionContext)
    {
        Long personId = (Long) inActionContext.getParams();
        log.info("Updating the info for user with person id '" + personId + "' in all cached activities and comments.");

        // Look up the info id to use
        Person p = personMapper.findById(personId);
        updateActivityInfo(p);
        updateCommentInfo(p);
        updateEmbeddedCommentInfo(p);
        return null;
    }

    /**
     * Update a person's info in all of the cached activities that she authored.
     *
     * @param inPerson
     *            the person to update activity info for
     */
    private void updateActivityInfo(final Person inPerson)
    {
        // find all the activity ids to update
        List<Long> activityIds = getActivityIdsAuthordedByEntityDbMapper.execute(inPerson.getAccountId(),
                EntityType.PERSON);

        if (log.isInfoEnabled())
        {
            log.info("Found info for '" + inPerson.getAccountId() + "' - applying it to " + activityIds.size()
                    + " activities.");
        }

        // update them in cache
        updateAuthorInfoInCachedActivitiesCacheMapper.execute(activityIds, inPerson);
    }

    /**
     * Update a person's info in all of the cached comments that she authored.
     *
     * @param inPerson
     *            the person to update comment info for
     */
    private void updateCommentInfo(final Person inPerson)
    {
        List<Long> commentIds = getActivityCommentIdsAuthoredByPersonIdDbMapper.execute(inPerson.getId());

        if (log.isInfoEnabled())
        {
            log.info("Found info for '" + inPerson.getAccountId() + "' " + " - applying it to " + commentIds.size()
                    + " activity comments.");
        }
        updateAuthorInfoInCachedCommentsCacheMapper.execute(commentIds, inPerson);
    }

    /**
     * Update a person's info in all of the embedded comments in ActivityDTOs as the first or last Comment.
     *
     * @param inPerson
     *            the person to update activity DTOs for
     */
    private void updateEmbeddedCommentInfo(final Person inPerson)
    {
        List<Long> activityIds = getActivityIdsWithFirstOrLastCommentsAuthoredByPersonIdDbMapper.execute(inPerson
                .getId());

        if (log.isInfoEnabled())
        {
            log.info("Found info for '" + inPerson.getAccountId() + "' "
                    + " - applying it to the embedded first/last comments in " + activityIds.size() + " activities.");
        }

        updateAuthorInfoInActivityEmbeddedCachedCommentsCacheMapper.execute(activityIds, inPerson);
    }

}
