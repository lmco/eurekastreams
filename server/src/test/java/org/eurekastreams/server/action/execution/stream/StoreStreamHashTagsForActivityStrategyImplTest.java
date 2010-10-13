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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.strategies.HashTagExtractor;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.HashTag;
import org.eurekastreams.server.domain.stream.StreamHashTag;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.chained.DecoratedPartialResponseDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.eurekastreams.server.persistence.mappers.stream.ActivityContentExtractor;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for StoreStreamHashTagsForActivityStrategyImpl.
 */
public class StoreStreamHashTagsForActivityStrategyImplTest
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
     * Hashtag extractor.
     */
    private final HashTagExtractor hashTagExtractor = context.mock(HashTagExtractor.class);

    /**
     * Content extractor - pulls out content for hashtag parsing.
     */
    private final ActivityContentExtractor contentExtractor = context.mock(ActivityContentExtractor.class);

    /**
     * Mapper to store hash tags to an activity.
     */
    private final DecoratedPartialResponseDomainMapper<List<String>, List<HashTag>> hashTagMapper = context
            .mock(DecoratedPartialResponseDomainMapper.class);

    /**
     * Mapper to insert stream hashtags.
     */
    private final InsertMapper<StreamHashTag> streamHashTagInsertMapper = context.mock(InsertMapper.class);

    /**
     * mapper to get all parent org ids for an org id.
     */
    private DomainMapper<Long, List<Long>> getRecursiveParentOrgIds = context.mock(DomainMapper.class,
            "getRecursiveParentOrgIds");

    /**
     * Get organizations by ids mapper.
     */
    private final DomainMapper<List<Long>, List<OrganizationModelView>> organizationsByIdsMapper = context.mock(
            DomainMapper.class, "organizationsByIdsMapper");

    /**
     * Mock activity.
     */
    private final Activity activity = context.mock(Activity.class);

    /**
     * Stream scope.
     */
    private StreamScope streamScope = context.mock(StreamScope.class);

    /**
     * base object.
     */
    private HashMap<String, String> baseObject = context.mock(HashMap.class);

    /**
     * Test execute on a stream type we don't handle.
     */
    @Test
    public void testExecuteUnhandledStreamType()
    {
        context.checking(new Expectations()
        {
            {
                allowing(activity).getId();
                will(returnValue(3L));

                allowing(activity).getRecipientStreamScope();
                will(returnValue(streamScope));

                oneOf(streamScope).getScopeType();
                will(returnValue(ScopeType.ORGANIZATION));
            }
        });

        StoreStreamHashTagsForActivityStrategy sut = buildSut();
        sut.execute(activity);
        context.assertIsSatisfied();
    }

    /**
     * Test execute with no hashtags.
     */
    @Test
    public void testExecuteWithNoHashTags()
    {
        final String groupShortName = "sdlkjfsd";
        final String content = "hi #there #potato";
        final List<String> hashTagContents = new ArrayList<String>();

        context.checking(new Expectations()
        {
            {
                allowing(activity).getId();
                will(returnValue(3L));

                allowing(activity).getRecipientStreamScope();
                will(returnValue(streamScope));

                oneOf(streamScope).getScopeType();
                will(returnValue(ScopeType.GROUP));

                oneOf(streamScope).getUniqueKey();
                will(returnValue(groupShortName));

                oneOf(activity).getBaseObjectType();
                will(returnValue(BaseObjectType.NOTE));

                oneOf(activity).getBaseObject();
                will(returnValue(baseObject));

                oneOf(contentExtractor).extractContent(BaseObjectType.NOTE, baseObject);
                will(returnValue(content));

                oneOf(hashTagExtractor).extractAll(content);
                will(returnValue(hashTagContents));

            }
        });

        StoreStreamHashTagsForActivityStrategy sut = buildSut();
        sut.execute(activity);
        context.assertIsSatisfied();
    }

    /**
     * Test execute with no hashtags.
     */
    @Test
    public void testExecuteOnPrivateActivity()
    {
        final String groupShortName = "sdlkjfsd";
        final String content = "hi #there #potato";
        final List<String> hashTagContents = new ArrayList<String>();
        hashTagContents.add("#there");
        hashTagContents.add("#potato");

        final List<HashTag> hashTags = new ArrayList<HashTag>();
        hashTags.add(new HashTag("#there"));
        hashTags.add(new HashTag("#potato"));

        context.checking(new Expectations()
        {
            {
                allowing(activity).getId();
                will(returnValue(3L));

                allowing(activity).getRecipientStreamScope();
                will(returnValue(streamScope));

                oneOf(streamScope).getScopeType();
                will(returnValue(ScopeType.GROUP));

                oneOf(streamScope).getUniqueKey();
                will(returnValue(groupShortName));

                oneOf(activity).getBaseObjectType();
                will(returnValue(BaseObjectType.NOTE));

                oneOf(activity).getBaseObject();
                will(returnValue(baseObject));

                allowing(activity).getPostedTime();
                will(returnValue(new Date()));

                oneOf(contentExtractor).extractContent(BaseObjectType.NOTE, baseObject);
                will(returnValue(content));

                oneOf(hashTagExtractor).extractAll(content);
                will(returnValue(hashTagContents));

                allowing(activity).getIsDestinationStreamPublic();
                will(returnValue(false));

                oneOf(hashTagMapper).execute(hashTagContents);
                will(returnValue(hashTags));

                // two hashtag inserts - one for each hashtag to the group
                oneOf(streamHashTagInsertMapper).execute(with(any(PersistenceRequest.class)));
                oneOf(streamHashTagInsertMapper).execute(with(any(PersistenceRequest.class)));
            }
        });

        StoreStreamHashTagsForActivityStrategy sut = buildSut();
        sut.execute(activity);
        context.assertIsSatisfied();
    }

    /**
     * Test execute with no hashtags.
     */
    @Test
    public void testExecuteOnPublicActivity()
    {
        final String groupShortName = "sdlkjfsd";
        final String content = "hi #there #potato";
        final List<String> hashTagContents = new ArrayList<String>();
        hashTagContents.add("#there");
        hashTagContents.add("#potato");

        final List<HashTag> hashTags = new ArrayList<HashTag>();
        hashTags.add(new HashTag("#there"));
        hashTags.add(new HashTag("#potato"));

        final Organization activityParentOrg = context.mock(Organization.class);

        final Long orgId = 823L;
        final List<Long> parentOrgIds = new ArrayList<Long>();

        final List<OrganizationModelView> orgModelViews = new ArrayList<OrganizationModelView>();
        OrganizationModelView org1 = new OrganizationModelView();
        OrganizationModelView org2 = new OrganizationModelView();
        OrganizationModelView org3 = new OrganizationModelView();
        OrganizationModelView org4 = new OrganizationModelView();
        orgModelViews.add(org1);
        orgModelViews.add(org2);
        orgModelViews.add(org3);
        orgModelViews.add(org4);
        org1.setShortName("foo");
        org2.setShortName("bar");
        org2.setShortName("potato");
        org2.setShortName("carrot");

        context.checking(new Expectations()
        {
            {
                allowing(activity).getId();
                will(returnValue(3L));

                allowing(activity).getRecipientStreamScope();
                will(returnValue(streamScope));

                oneOf(streamScope).getScopeType();
                will(returnValue(ScopeType.GROUP));

                oneOf(streamScope).getUniqueKey();
                will(returnValue(groupShortName));

                oneOf(activity).getBaseObjectType();
                will(returnValue(BaseObjectType.NOTE));

                oneOf(activity).getBaseObject();
                will(returnValue(baseObject));

                allowing(activity).getPostedTime();
                will(returnValue(new Date()));

                oneOf(contentExtractor).extractContent(BaseObjectType.NOTE, baseObject);
                will(returnValue(content));

                oneOf(hashTagExtractor).extractAll(content);
                will(returnValue(hashTagContents));

                allowing(activity).getIsDestinationStreamPublic();
                will(returnValue(true));

                oneOf(hashTagMapper).execute(hashTagContents);
                will(returnValue(hashTags));

                oneOf(activity).getRecipientParentOrg();
                will(returnValue(activityParentOrg));

                oneOf(activityParentOrg).getId();
                will(returnValue(orgId));

                oneOf(getRecursiveParentOrgIds).execute(orgId);
                will(returnValue(parentOrgIds));

                oneOf(organizationsByIdsMapper).execute(parentOrgIds);
                will(returnValue(orgModelViews));

                // 10 hashtag inserts - two for hashtags, 4*2 for the 4 parent orgs and two hashtags
                exactly(5 + 5).of(streamHashTagInsertMapper).execute(with(any(PersistenceRequest.class)));
            }
        });

        StoreStreamHashTagsForActivityStrategy sut = buildSut();
        sut.execute(activity);
        context.assertIsSatisfied();
    }

    /**
     * Build the system under test.
     *
     * @return the system under test
     */
    private StoreStreamHashTagsForActivityStrategy buildSut()
    {
        return new StoreStreamHashTagsForActivityStrategyImpl(hashTagExtractor, contentExtractor, hashTagMapper,
                streamHashTagInsertMapper, getRecursiveParentOrgIds, organizationsByIdsMapper);
    }
}
