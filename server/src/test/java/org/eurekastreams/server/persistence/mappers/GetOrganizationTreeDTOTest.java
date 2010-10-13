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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.OrganizationTreeDTO;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for GetOrganizationTreeDTO.
 */
public class GetOrganizationTreeDTOTest
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
     * test the execute() method brings back an org hierarchy tree we'd expect.
     */
    @Test
    public void testExecute()
    {
        final DomainMapper<Long, List<Long>> getAllOrganizationIdsMapper = context.mock(DomainMapper.class,
                "getAllOrganizationIdsMapper");
        final DomainMapper<List<Long>, List<OrganizationModelView>> organizationsByIdMapper = context.mock(
                DomainMapper.class, "organizationsByIdMapper");

        GetOrganizationTreeDTO sut = new GetOrganizationTreeDTO(getAllOrganizationIdsMapper, organizationsByIdMapper);

        final List<Long> allOrgIds = new ArrayList<Long>();
        allOrgIds.add(5L);
        allOrgIds.add(6L);
        allOrgIds.add(7L);

        OrganizationModelView org;
        final List<OrganizationModelView> orgs = new ArrayList<OrganizationModelView>();

        // 5
        // - 6
        // - 7
        // - - 8

        // 5
        org = new OrganizationModelView();
        org.setEntityId(5L);
        org.setParentOrganizationId(5L);
        orgs.add(org);

        // 6
        org = new OrganizationModelView();
        org.setEntityId(6L);
        org.setParentOrganizationId(5L);
        orgs.add(org);

        // 7
        org = new OrganizationModelView();
        org.setEntityId(7L);
        org.setParentOrganizationId(5L);
        orgs.add(org);

        // 8
        org = new OrganizationModelView();
        org.setEntityId(8L);
        org.setParentOrganizationId(7L);
        orgs.add(org);

        context.checking(new Expectations()
        {
            {
                oneOf(getAllOrganizationIdsMapper).execute(null);
                will(returnValue(allOrgIds));

                oneOf(organizationsByIdMapper).execute(allOrgIds);
                will(returnValue(orgs));
            }
        });

        OrganizationTreeDTO orgTree = sut.execute(null);

        assertEquals(5L, (long) orgTree.getOrgId());
        assertEquals(2, orgTree.getChildren().size());

        OrganizationTreeDTO org6 = null;
        OrganizationTreeDTO org7 = null;
        for (OrganizationTreeDTO otree : orgTree.getChildren())
        {
            if (otree.getOrgId() == 6L)
            {
                org6 = otree;
            }
            else if (otree.getOrgId() == 7L)
            {
                org7 = otree;
            }
        }
        assertEquals(0, org6.getChildren().size());
        assertEquals(1, org7.getChildren().size());
        assertEquals(new Long(8L), org7.getChildren().get(0).getOrgId());
    }
}
