/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.GetAllFollowedByActivityIdsRequest;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for AllFollowedByPersistenceRequestTransformer.
 * 
 */
public class AllFollowedByPersistenceRequestTransformerTest
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
     * Mapper to get a person id by account id.
     */
    private DomainMapper<String, Long> getPersonIdByAccountId = context.mock(DomainMapper.class,
            "getPersonIdByAccountId");

    /**
     * Followed groups mapper.
     */
    private DomainMapper<Long, List<Long>> followeGroupsMapper = context.mock(DomainMapper.class);

    /**
     * Group mapper.
     */
    private DomainMapper<List<Long>, List<DomainGroupModelView>> groupMapper = context.mock(DomainMapper.class,
            "groupMapper");

    /**
     * System under test.
     */
    private AllFollowedByPersistenceRequestTransformer sut;

    /**
     * Setup test fixtures.
     */
    @Before
    public void setup()
    {
        sut = new AllFollowedByPersistenceRequestTransformer(getPersonIdByAccountId, followeGroupsMapper, groupMapper);
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
        jsonReq.accumulate("followedBy", entityAcctName);

        final List<Long> followedGroups = Arrays.asList(1L, 2L, 3L);

        DomainGroupModelView group1 = new DomainGroupModelView();
        group1.setEntityId(1L);
        group1.setStreamId(4L);

        DomainGroupModelView group2 = new DomainGroupModelView();
        group2.setEntityId(2L);
        group2.setStreamId(5L);

        DomainGroupModelView group3 = new DomainGroupModelView();
        group3.setEntityId(3L);
        group3.setStreamId(6L);

        final List<DomainGroupModelView> groups = Arrays.asList(group1, group2, group3);

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdByAccountId).execute(entityAcctName);
                will(returnValue(entityId));

                oneOf(followeGroupsMapper).execute(entityId);
                will(returnValue(followedGroups));

                oneOf(groupMapper).execute(followedGroups);
                will(returnValue(groups));
            }
        });

        GetAllFollowedByActivityIdsRequest result = (GetAllFollowedByActivityIdsRequest) sut.transform(jsonReq,
                entityId);
        Assert.assertEquals(Arrays.asList(group1.getStreamId(), group2.getStreamId(), group3.getStreamId()), result
                .getGroupStreamIds());

        Assert.assertEquals(entityId, result.getUserId());

        context.assertIsSatisfied();
    }

    /**
     * Tests the transformation.
     */
    @Test
    public void transformTestNotMatchingId()
    {
        final Long entityId = 10L;
        final String entityAcctName = "acctName";

        final JSONObject jsonReq = new JSONObject();
        jsonReq.accumulate("followedBy", entityAcctName);

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdByAccountId).execute(entityAcctName);
                will(returnValue(entityId - 1L));
            }
        });

        GetAllFollowedByActivityIdsRequest result = (GetAllFollowedByActivityIdsRequest) sut.transform(jsonReq,
                entityId);
        List<Long> groups = result.getGroupStreamIds();
        Assert.assertEquals(0, groups.size());

        Assert.assertEquals((entityId - 1L), result.getUserId().longValue());

        context.assertIsSatisfied();
    }

}
