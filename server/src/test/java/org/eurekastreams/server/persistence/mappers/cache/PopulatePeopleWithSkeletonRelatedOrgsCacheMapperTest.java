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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.domain.EntityTestHelper;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for PopulatePeopleWithSkeletonRelatedOrgsCacheMapper.
 */
public class PopulatePeopleWithSkeletonRelatedOrgsCacheMapperTest
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
     * Mapper to get organization modelviews by ids.
     */
    private DomainMapper<List<Long>, List<OrganizationModelView>> getOrgsByIdsMapper = context.mock(DomainMapper.class);

    /**
     * Mapper to get person modelviews by ids.
     */
    private GetPeopleByIds getPeopleByIdsMapper = context.mock(GetPeopleByIds.class);

    /**
     * Test the execute method.
     */
    @Test
    public void testExecute()
    {
        final Person person = new Person();
        final Long personId = 8273L;
        final String org1Name = "Org 1";
        final String org2Name = "Org 2";

        EntityTestHelper.setPersonId(person, personId);

        final OrganizationModelView orgMv1 = new OrganizationModelView();
        orgMv1.setName(org1Name);

        final OrganizationModelView orgMv2 = new OrganizationModelView();
        orgMv2.setName(org2Name);

        final List<OrganizationModelView> relatedOrgMvs = new ArrayList<OrganizationModelView>();
        relatedOrgMvs.add(orgMv1);
        relatedOrgMvs.add(orgMv2);

        final List<Long> relatedOrgIds = new ArrayList<Long>();

        PersonModelView pmv = new PersonModelView();
        pmv.setEntityId(personId);
        pmv.setRelatedOrganizationIds(relatedOrgIds);

        final List<PersonModelView> pmvs = new ArrayList<PersonModelView>();
        pmvs.add(pmv);

        context.checking(new Expectations()
        {
            {
                oneOf(getPeopleByIdsMapper).execute(with(equal(Collections.singletonList(personId))));
                will(returnValue(pmvs));

                oneOf(getOrgsByIdsMapper).execute(relatedOrgIds);
                will(returnValue(relatedOrgMvs));

            }
        });

        // Perform SUT
        PopulatePeopleWithSkeletonRelatedOrgsCacheMapper sut = new PopulatePeopleWithSkeletonRelatedOrgsCacheMapper(
                getOrgsByIdsMapper, getPeopleByIdsMapper);

        sut.execute(person);

        // ASSERT
        assertEquals(2, person.getRelatedOrganizations().size());
        assertTrue((person.getRelatedOrganizations().get(0).getName().equals(org1Name)
                && person.getRelatedOrganizations().get(1).getName().equals(org2Name) || (person
                .getRelatedOrganizations().get(1).getName().equals(org1Name) && person.getRelatedOrganizations().get(0)
                .getName().equals(org2Name))));

        context.assertIsSatisfied();
    }
}
