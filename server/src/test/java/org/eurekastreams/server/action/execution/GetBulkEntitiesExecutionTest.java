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
package org.eurekastreams.server.action.execution;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for the bulk entities action.
 * 
 */
public class GetBulkEntitiesExecutionTest
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
     * {@link PrincipalActionContext} mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Mapper to get a list of PersonModelViews from a list of AccountIds.
     */
    private DomainMapper<List<String>, List<PersonModelView>> getPersonModelViewsByAccountIdsMapper = context.mock(
            DomainMapper.class, "getPersonModelViewsByAccountIdsMapper");
    /**
     * Group mapper.
     */
    private GetDomainGroupsByShortNames groupMapper = context.mock(GetDomainGroupsByShortNames.class);
    /**
     * System under test.
     */
    private GetBulkEntitiesExecution sut = new GetBulkEntitiesExecution(getPersonModelViewsByAccountIdsMapper,
            groupMapper);

    /**
     * Test.
     */
    @Test
    public void execute()
    {
        final ArrayList<StreamEntityDTO> entities = new ArrayList<StreamEntityDTO>();
        StreamEntityDTO person = new StreamEntityDTO();
        person.setType(EntityType.PERSON);
        person.setUniqueIdentifier("person1");
        entities.add(person);

        StreamEntityDTO group = new StreamEntityDTO();
        group.setType(EntityType.GROUP);
        group.setUniqueIdentifier("group1");
        entities.add(group);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(entities));

                oneOf(getPersonModelViewsByAccountIdsMapper).execute(with(any(List.class)));
                will(returnValue(new ArrayList<PersonModelView>()));

                oneOf(groupMapper).execute(with(any(List.class)));
                will(returnValue(new ArrayList<DomainGroupModelView>()));
            }
        });

        sut.execute(actionContext);
    }
}
