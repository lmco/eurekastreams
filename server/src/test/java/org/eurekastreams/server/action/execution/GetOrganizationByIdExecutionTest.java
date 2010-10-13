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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetOrganizationByIdExecution class.
 *
 */
public class GetOrganizationByIdExecutionTest
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
    private GetOrganizationByIdExecution sut;

    /**
     * An org id to use for testing.
     */
    private static final Long ORG_ID = 46L;

    /**
     * {@link GetOrganizationsByIds} mock.
     */
    private DomainMapper<List<Long>, List<OrganizationModelView>> getOrgsByIdsMapperMock = context
            .mock(DomainMapper.class);

    /**
     * {@link ActionContext} mock.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new GetOrganizationByIdExecution(getOrgsByIdsMapperMock);
    }

    /**
     * Check that we get good results with an id that corresponds to a real org.
     */
    @Test
    public void testPerformWithValidId()
    {
        final OrganizationModelView orgModelView = new OrganizationModelView();
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(ORG_ID));

                oneOf(getOrgsByIdsMapperMock).execute(Collections.singletonList(ORG_ID));
                will(returnValue(Collections.singletonList(orgModelView)));
            }
        });

        OrganizationModelView actual = sut.execute(actionContext);
        assertSame(orgModelView, actual);
        context.assertIsSatisfied();
    }

    /**
     * Check that we get a null result if run with an id that does not correspond to a real org.
     */
    @Test
    public void testPerformWithNoSuchId()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(ORG_ID));

                oneOf(getOrgsByIdsMapperMock).execute(Collections.singletonList(ORG_ID));
                will(returnValue(new ArrayList<OrganizationModelView>()));
            }
        });

        assertNull(sut.execute(actionContext));

        context.assertIsSatisfied();
    }
}
