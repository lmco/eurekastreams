/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.search.directory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByIds;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for CachedModelViewResultTransformer.
 */
public class CachedModelViewResultTransformerTest
{
    /**
     * The property name that Hibernate uses to store the Hibernate Class of the entity.
     */
    private static final String HIBERNATE_CLASS_PROPERTY_NAME = "_hibernate_class";

    /**
     * The property name of the id.
     */
    private static final String HIBERNATE_ID_PROPERTY_NAME = "__HSearch_id";

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
     * The mapper to get the organizations by ids.
     */
    private GetOrganizationsByIds getOrgsByIdsMapperMock = context.mock(GetOrganizationsByIds.class);

    /**
     * The mapper to get the domain groups by ids.
     */
    private GetDomainGroupsByIds getDomainGroupsByIdsMapperMock = context.mock(GetDomainGroupsByIds.class);

    /**
     * The mapper to get the people by Ids.
     */
    private GetPeopleByIds getPeopleByIdsMapperMock = context.mock(GetPeopleByIds.class);

    /**
     * Test transformTuple.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testTransformTuple()
    {
        final Class<?> entityClass = DomainGroup.class;
        final Long entityId = 183874L;

        CachedModelViewResultTransformer sut = new CachedModelViewResultTransformer();
        Map<String, Object> map =
                (Map<String, Object>) sut.transformTuple(new Object[] { entityClass, entityId }, new String[] {
                        HIBERNATE_CLASS_PROPERTY_NAME, HIBERNATE_ID_PROPERTY_NAME });

        assertEquals(entityClass, map.get(HIBERNATE_CLASS_PROPERTY_NAME));
        assertEquals(entityId, map.get(HIBERNATE_ID_PROPERTY_NAME));
    }

    /**
     * Test transformList.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testTransformList()
    {
        // create the input list:
        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        maps.add(buildMap(Organization.class, 9L));
        maps.add(buildMap(Person.class, 3L));
        maps.add(buildMap(Organization.class, 8L));
        maps.add(buildMap(DomainGroup.class, 9L));
        maps.add(buildMap(Person.class, 1L));
        maps.add(buildMap(Organization.class, 3L));
        maps.add(buildMap(DomainGroup.class, 1L));

        // ModelViews that the mocked mappers will return - numbers describe their order
        final ModelView org1 = buildModelView(new OrganizationModelView(), 9L);
        final ModelView person2 = buildModelView(new PersonModelView(), 3L);
        final ModelView org3 = buildModelView(new OrganizationModelView(), 8L);
        final ModelView group4 = buildModelView(new DomainGroupModelView(), 9L);
        final ModelView person5 = buildModelView(new PersonModelView(), 1L);
        final ModelView org6 = buildModelView(new OrganizationModelView(), 3L);
        final ModelView group7 = buildModelView(new DomainGroupModelView(), 1L);

        // the list of Org IDs we expect to be sent to the org mapper
        final List<Long> expectedOrgIdsList = new ArrayList<Long>();
        expectedOrgIdsList.add(9L);
        expectedOrgIdsList.add(8L);
        expectedOrgIdsList.add(3L);

        // the org ModelViews returned by org mapper
        final List<ModelView> returnedOrgModelViews = new ArrayList<ModelView>();
        returnedOrgModelViews.add(org3);
        returnedOrgModelViews.add(org6);
        returnedOrgModelViews.add(org1);

        // the list of Group IDs we expect to be sent to the group mapper
        final List<Long> expectedGroupIdList = new ArrayList<Long>();
        expectedGroupIdList.add(9L);
        expectedGroupIdList.add(1L);

        // the group ModelViews returned by the group mapper
        final List<ModelView> returnedGroupModelViews = new ArrayList<ModelView>();
        returnedGroupModelViews.add(group7);
        returnedGroupModelViews.add(group4);

        // the list of Person IDs we expect to be sent to the person mapper
        final List<Long> expectedPersonIdList = new ArrayList<Long>();
        expectedPersonIdList.add(3L);
        expectedPersonIdList.add(1L);

        // the person ModelViews returned by the person mapper
        final List<ModelView> returnedpersonModelViews = new ArrayList<ModelView>();
        returnedpersonModelViews.add(person2);
        returnedpersonModelViews.add(person5);

        // wire up the input/output to the mapper mocks
        context.checking(new Expectations()
        {
            {
                oneOf(getOrgsByIdsMapperMock).execute(expectedOrgIdsList);
                will(returnValue(returnedOrgModelViews));

                oneOf(getDomainGroupsByIdsMapperMock).execute(expectedGroupIdList);
                will(returnValue(returnedGroupModelViews));

                oneOf(getPeopleByIdsMapperMock).execute(expectedPersonIdList);
                will(returnValue(returnedpersonModelViews));
            }
        });

        // wire up the SUT with the mocked mappers
        CachedModelViewResultTransformer sut = new CachedModelViewResultTransformer();
        sut.setGetOrgsByIdsMapper(getOrgsByIdsMapperMock);
        sut.setGetDomainGroupsByIdsMapper(getDomainGroupsByIdsMapperMock);
        sut.setGetPeopleByIdsMapper(getPeopleByIdsMapperMock);

        // perform SUT
        List results = sut.transformList(maps);

        // make sure the result count equals the input count
        assertEquals(maps.size(), results.size());

        // make sure the SUT wired the model views in the same order as the input map
        assertSame(org1, results.get(0));
        assertSame(person2, results.get(1));
        assertSame(org3, results.get(2));
        assertSame(group4, results.get(3));
        assertSame(person5, results.get(4));
        assertSame(org6, results.get(5));
        assertSame(group7, results.get(6));

        // make sure the mappers were called with the expected lists
        context.assertIsSatisfied();
    }

    /**
     * Build a Map of String,Object with the entity class and id.
     *
     * @param inEntityClass
     *            the entity class
     * @param inEntityId
     *            the entity id
     * @return a Map of String,Object to use to test with transformList
     */
    private Map<String, Object> buildMap(final Class<?> inEntityClass, final Long inEntityId)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(HIBERNATE_CLASS_PROPERTY_NAME, inEntityClass);
        map.put(HIBERNATE_ID_PROPERTY_NAME, inEntityId);
        return map;
    }

    /**
     * Populate the input modelView with the entity id and return it.
     *
     * @param inModelView
     *            the ModelView to populate the ID of
     * @param inEntityId
     *            the entity id to set
     * @return the input ModelView
     */
    private ModelView buildModelView(final ModelView inModelView, final Long inEntityId)
    {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(HIBERNATE_ID_PROPERTY_NAME, inEntityId);
        inModelView.loadProperties(properties);
        return inModelView;
    }
}
