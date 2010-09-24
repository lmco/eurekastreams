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

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;
import net.sf.json.JSONObject;

import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByIds;
import org.eurekastreams.server.persistence.mappers.stream.GetFollowedGroupIds;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the followed group transformer.
 */
public class FollowedGroupsPersistenceRequestTransformerTest
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
     * Person mapper.
     */
    private GetPeopleByAccountIds personMapper = context.mock(GetPeopleByAccountIds.class);

    /**
     * Followed groups mapper.
     */
    private GetFollowedGroupIds followeGroupsMapper = context.mock(GetFollowedGroupIds.class);

    /**
     * Group mapper.
     */
    private GetDomainGroupsByIds groupMapper = context.mock(GetDomainGroupsByIds.class);

    /**
     * System under test.
     */
    private FollowedGroupsPersistenceRequestTransformer sut;

    /**
     * Setup test fixtures.
     */
    @Before
    public void setup()
    {
        sut = new FollowedGroupsPersistenceRequestTransformer(personMapper, followeGroupsMapper, groupMapper);
    }

    /**
     * Tests the transformation.
     */
    @Test
    public void transformTestMatchingId()
    {
        final Long entityId = 10L;
        final String entityAcctName = "acctName";

        final JSONObject jsonReq = new JSONObject();
        jsonReq.accumulate("joinedGroups", entityAcctName);

        final List<Long> followedGroups = Arrays.asList(1L, 2L, 3L);

        DomainGroupModelView group1 = new DomainGroupModelView();
        group1.setEntityId(1L);
        group1.setCompositeStreamId(4L);

        DomainGroupModelView group2 = new DomainGroupModelView();
        group2.setEntityId(2L);
        group2.setCompositeStreamId(5L);

        DomainGroupModelView group3 = new DomainGroupModelView();
        group3.setEntityId(3L);
        group3.setCompositeStreamId(6L);

        final List<DomainGroupModelView> groups = Arrays.asList(group1, group2, group3);

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).fetchId(entityAcctName);
                will(returnValue(entityId));

                oneOf(followeGroupsMapper).execute(entityId);
                will(returnValue(followedGroups));

                oneOf(groupMapper).execute(followedGroups);
                will(returnValue(groups));
            }
        });

        Assert.assertEquals(Arrays.asList(group1.getCompositeStreamId(), group2.getCompositeStreamId(), group3
                .getCompositeStreamId()), sut.transform(jsonReq, entityId));

        context.assertIsSatisfied();
    }

    /**
     * Tests the transformation.
     */
    @Test(expected = AuthorizationException.class)
    public void transformTestNotMatchingId()
    {
        final Long entityId = 10L;
        final String entityAcctName = "acctName";

        final JSONObject jsonReq = new JSONObject();
        jsonReq.accumulate("joinedGroups", entityAcctName);

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).fetchId(entityAcctName);
                will(returnValue(entityId - 1L));
            }
        });

        sut.transform(jsonReq, entityId);

        context.assertIsSatisfied();
    }
}
