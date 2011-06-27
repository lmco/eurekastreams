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
package org.eurekastreams.server.domain.strategies;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.domain.dto.DisplayInfoSettable;
import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetItemsByPointerIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for DisplayInfoSettableDataPopulator.
 * 
 */
@SuppressWarnings("unchecked")
public class DisplayInfoSettableDataPopulatorTest
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
     * Mapper to get a list of PersonModelViews from a list of AccountIds.
     */
    private DomainMapper<List<String>, List<PersonModelView>> getPersonModelViewsByAccountIdsMapper = context.mock(
            DomainMapper.class, "getPersonModelViewsByAccountIdsMapper");

    /**
     * Mapper to get a list of PersonModelViews from a list of AccountIds.
     */
    private GetItemsByPointerIds<DomainGroupModelView> getGroupModelViewsByShortNameMapper = context.mock(
            GetItemsByPointerIds.class, "getGroupModelViewsByShortNameMapper");

    /**
     * System under test.
     */
    private DisplayInfoSettableDataPopulator sut = new DisplayInfoSettableDataPopulator(
            getPersonModelViewsByAccountIdsMapper, getGroupModelViewsByShortNameMapper);

    /**
     * User Id.
     */
    private long currentUserId = 5L;

    /**
     * Test.
     */
    @Test
    public void test()
    {
        String personUniqueKey = "personUniqueKey";
        String personDisplayName = "personDisplayName";
        String personAvatarId = "personAvatarId";

        String groupUniqueKey = "groupUniqueKey";
        String groupDisplayName = "groupDisplayName";
        String groupAvatarId = "groupAvatarId";

        FeaturedStreamDTO personStream = new FeaturedStreamDTO();
        FeaturedStreamDTO groupStream = new FeaturedStreamDTO();
        personStream.setStreamType(ScopeType.PERSON);
        personStream.setStreamUniqueKey(personUniqueKey);
        groupStream.setStreamType(ScopeType.GROUP);
        groupStream.setStreamUniqueKey(groupUniqueKey);

        final PersonModelView p = new PersonModelView();
        p.setDisplayName(personDisplayName);
        p.setAccountId(personUniqueKey);
        p.setAvatarId("personAvatarId");

        final DomainGroupModelView g = new DomainGroupModelView();
        g.setName(groupDisplayName);
        g.setShortName(groupUniqueKey);
        g.setAvatarId(groupAvatarId);

        final List<DisplayInfoSettable> fsList = new ArrayList<DisplayInfoSettable>(Arrays.asList(personStream,
                groupStream));

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonModelViewsByAccountIdsMapper).execute(with(any(List.class)));
                will(returnValue(new ArrayList<PersonModelView>(Arrays.asList(p))));

                oneOf(getGroupModelViewsByShortNameMapper).execute(with(any(List.class)));
                will(returnValue(new ArrayList<DomainGroupModelView>(Arrays.asList(g))));
            }
        });

        List<DisplayInfoSettable> results = sut.execute(currentUserId, fsList);
        assertEquals(2, results.size());
        assertEquals(personDisplayName, results.get(0).getDisplayName());
        assertEquals(personAvatarId, ((FeaturedStreamDTO) results.get(0)).getAvatarId());

        assertEquals(groupDisplayName, results.get(1).getDisplayName());
        assertEquals(groupAvatarId, ((FeaturedStreamDTO) results.get(1)).getAvatarId());

        context.assertIsSatisfied();
    }
}
