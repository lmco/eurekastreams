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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.cache.UpdateAuthorInfoInActivityEmbeddedCachedComments;
import org.eurekastreams.server.persistence.mappers.cache.UpdateAuthorInfoInCachedActivities;
import org.eurekastreams.server.persistence.mappers.cache.UpdateAuthorInfoInCachedComments;
import org.eurekastreams.server.persistence.mappers.db.GetActivityCommentIdsAuthoredByPersonId;
import org.eurekastreams.server.persistence.mappers.db.GetActivityIdsAuthoredByOrOriginallyAuthoredByEntity;
import org.eurekastreams.server.persistence.mappers.db.GetActivityIdsWithFirstOrLastCommentsAuthoredByPersonId;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for PersonActivityAvatarUpdaterExecution.
 */
public class PersonActivityAvatarUpdaterExecutionTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mocked GetActivityIdsAuthoredByEntity mapper.
     */
    private final GetActivityIdsAuthoredByOrOriginallyAuthoredByEntity getActivityIdsAuthordedByEntityDbMapper = context
            .mock(GetActivityIdsAuthoredByOrOriginallyAuthoredByEntity.class);

    /**
     * Mocked GetActivityCommentIdsAuthoredByPersonId mapper.
     */
    private final GetActivityCommentIdsAuthoredByPersonId getActivityCommentIdsAuthoredByPersonIdDbMapper = context
            .mock(GetActivityCommentIdsAuthoredByPersonId.class);

    /**
     * DB Mapper to get all the activity ids where the current user authored either their first or last comment.
     */
    private final GetActivityIdsWithFirstOrLastCommentsAuthoredByPersonId
    // line break
    getActivityIdsWithFirstOrLastCommentsAuthoredByPersonIdDbMapper = context
            .mock(GetActivityIdsWithFirstOrLastCommentsAuthoredByPersonId.class);

    /**
     * Mocked UpdateAuthorAvatarInCachedActivities mapper.
     */
    private final UpdateAuthorInfoInCachedActivities updateAuthorAvatarInCachedActivitiesCacheMapper = context
            .mock(UpdateAuthorInfoInCachedActivities.class);

    /**
     * Mocked UpdateAuthorAvatarInCachedComments mapper.
     */
    private final UpdateAuthorInfoInCachedComments updateAuthorAvatarInCachedCommentsCacheMapper = context
            .mock(UpdateAuthorInfoInCachedComments.class);

    /**
     * Cache mapper that updates cached activity dtos by making the embedded comment dtos' author avatar ids current if
     * the input user authored either of them.
     */
    private final UpdateAuthorInfoInActivityEmbeddedCachedComments
    // line break
    updateAuthorAvatarInActivityEmbeddedCachedCommentsCacheMapper = context
            .mock(UpdateAuthorInfoInActivityEmbeddedCachedComments.class);

    /**
     * Mocked PersonMapper mapper.
     */
    private final PersonMapper personMapper = context.mock(PersonMapper.class);

    /**
     * Mocked user details.
     */
    private final ExtendedUserDetails userMock = context.mock(ExtendedUserDetails.class);

    /**
     * Mocked person.
     */
    private final Person personMock = context.mock(Person.class);

    /**
     * Person id.
     */
    private final Long personId = 928372L;

    /**
     * System under test.
     */
    private PersonAuthoredActivitiesAndCommentsCacheUpdater sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new PersonAuthoredActivitiesAndCommentsCacheUpdater(getActivityIdsAuthordedByEntityDbMapper,
                getActivityCommentIdsAuthoredByPersonIdDbMapper,
                getActivityIdsWithFirstOrLastCommentsAuthoredByPersonIdDbMapper,
                updateAuthorAvatarInCachedActivitiesCacheMapper, updateAuthorAvatarInCachedCommentsCacheMapper,
                updateAuthorAvatarInActivityEmbeddedCachedCommentsCacheMapper, personMapper);
    }

    /**
     * Test performAction.
     */
    @Test
    public void testPerformAction()
    {
        final String userAccountId = "lskdfj";
        final String avatarId = "someAvatarId";
        final Person p = new Person();
        p.setAvatarId(avatarId);

        final List<Long> activityIds = new ArrayList<Long>();
        activityIds.add(1L);

        final List<Long> commentIds = new ArrayList<Long>();
        commentIds.add(2L);

        final List<Long> activitiesWithEmbeddedComments = new ArrayList<Long>();
        activitiesWithEmbeddedComments.add(3L);

        context.checking(new Expectations()
        {
            {
                // setup the user details
                allowing(userMock).getPerson();
                will(returnValue(personMock));

                allowing(personMock).getAccountId();
                will(returnValue(userAccountId));

                allowing(personMock).getId();
                will(returnValue(personId));

                // when the person mapper is asked for the user, return the mock
                oneOf(personMapper).findById(with(personId));
                will(returnValue(personMock));

                // ... and will need to get the avatar from the person
                allowing(personMock).getAvatarId();
                will(returnValue(avatarId));

                // ---
                // action must ask for the activity ids for this person
                oneOf(getActivityIdsAuthordedByEntityDbMapper).execute(with(userAccountId), with(EntityType.PERSON));
                will(returnValue(activityIds));

                // those activity ids should be passed into the activity updater
                oneOf(updateAuthorAvatarInCachedActivitiesCacheMapper).execute(with(activityIds), with(personMock));

                // ---
                // action must ask for the comment ids for this person
                oneOf(getActivityCommentIdsAuthoredByPersonIdDbMapper).execute(with(personId));
                will(returnValue(commentIds));

                // action must pass those ids to the comment updater
                oneOf(updateAuthorAvatarInCachedCommentsCacheMapper).execute(with(commentIds), with(personMock));

                // ---
                // action must ask for the activity ids with embedded comments
                // authored by the current user
                oneOf(getActivityIdsWithFirstOrLastCommentsAuthoredByPersonIdDbMapper).execute(with(personId));
                will(returnValue(activitiesWithEmbeddedComments));

                // action must pass those ids into the embedded commment updater
                oneOf(updateAuthorAvatarInActivityEmbeddedCachedCommentsCacheMapper).execute(
                        activitiesWithEmbeddedComments, personMock);
            }
        });

        // perform sut
        sut.execute(new ActionContext()
        {
            @Override
            public Serializable getParams()
            {
                return personId;
            }

            @Override
            public Map<String, Object> getState()
            {
                return null;
            }

        });

        context.assertIsSatisfied();
    }
}
