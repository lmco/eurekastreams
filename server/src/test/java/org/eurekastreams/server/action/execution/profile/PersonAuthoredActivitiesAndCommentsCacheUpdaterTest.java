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

import java.util.ArrayList;
import java.util.List;

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
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for PersonAuthoredActivitiesAndCommentsCacheUpdater.
 */
public class PersonAuthoredActivitiesAndCommentsCacheUpdaterTest
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
     * System under test.
     */
    private PersonAuthoredActivitiesAndCommentsCacheUpdater sut;

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
    private UpdateAuthorInfoInCachedActivities updateAuthorInfoInCachedActivitiesCacheM;

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
     * Setup method.
     */
    @Before
    public void setup()
    {
        getActivityIdsAuthordedByEntityDbMapper = context
                .mock(GetActivityIdsAuthoredByOrOriginallyAuthoredByEntity.class);
        getActivityCommentIdsAuthoredByPersonIdDbMapper = context.mock(GetActivityCommentIdsAuthoredByPersonId.class);
        getActivityIdsWithFirstOrLastCommentsAuthoredByPersonIdDbMapper = context
                .mock(GetActivityIdsWithFirstOrLastCommentsAuthoredByPersonId.class);
        updateAuthorInfoInCachedActivitiesCacheM = context.mock(UpdateAuthorInfoInCachedActivities.class);
        updateAuthorInfoInCachedCommentsCacheMapper = context.mock(UpdateAuthorInfoInCachedComments.class);
        updateAuthorInfoInActivityEmbeddedCachedCommentsCacheMapper = context
                .mock(UpdateAuthorInfoInActivityEmbeddedCachedComments.class);
        personMapper = context.mock(PersonMapper.class);

        sut = new PersonAuthoredActivitiesAndCommentsCacheUpdater(getActivityIdsAuthordedByEntityDbMapper,
                getActivityCommentIdsAuthoredByPersonIdDbMapper,
                getActivityIdsWithFirstOrLastCommentsAuthoredByPersonIdDbMapper,
                updateAuthorInfoInCachedActivitiesCacheM, updateAuthorInfoInCachedCommentsCacheMapper,
                updateAuthorInfoInActivityEmbeddedCachedCommentsCacheMapper, personMapper);

    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        final Long personId = 28381L;
        final Person person = context.mock(Person.class);
        final String accountId = "sdlkjfsdlkj";
        final ActionContext actionContext = context.mock(ActionContext.class);

        final List<Long> activityIdsAuthoredByPerson = new ArrayList<Long>();
        final List<Long> commentIdsAuthoredByPerson = new ArrayList<Long>();
        final List<Long> activityIdsWithEmbeddedCommentsAuthoredByPerson = new ArrayList<Long>();

        context.checking(new Expectations()
        {
            {
                allowing(person).getAccountId();
                will(returnValue(accountId));

                oneOf(actionContext).getParams();
                will(returnValue(personId));

                oneOf(personMapper).findById(personId);
                will(returnValue(person));

                allowing(person).getId();
                will(returnValue(personId));

                // activity ids authored by
                oneOf(getActivityIdsAuthordedByEntityDbMapper).execute(accountId, EntityType.PERSON);
                will(returnValue(activityIdsAuthoredByPerson));

                oneOf(updateAuthorInfoInCachedActivitiesCacheM)
                        .execute(with(activityIdsAuthoredByPerson), with(person));

                // comment ids authored by
                oneOf(getActivityCommentIdsAuthoredByPersonIdDbMapper).execute(personId);
                will(returnValue(commentIdsAuthoredByPerson));

                oneOf(updateAuthorInfoInCachedCommentsCacheMapper).execute(commentIdsAuthoredByPerson, person);

                // activity ids of embedded comments
                oneOf(getActivityIdsWithFirstOrLastCommentsAuthoredByPersonIdDbMapper).execute(personId);
                will(returnValue(activityIdsWithEmbeddedCommentsAuthoredByPerson));

                oneOf(updateAuthorInfoInActivityEmbeddedCachedCommentsCacheMapper).execute(
                        activityIdsWithEmbeddedCommentsAuthoredByPerson, person);

            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }
}
