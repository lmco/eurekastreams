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
package org.eurekastreams.server.action.validation.profile;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.GetRelatedEntityCountRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for DeleteOrganizationValidation.
 * 
 */
@SuppressWarnings("unchecked")
public class DeleteOrganizationValidationTest
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
     * Mapper to find counts for related objects.
     */
    private DomainMapper<GetRelatedEntityCountRequest, Long> relatedEntityCountMapper = context
            .mock(DomainMapper.class);

    /**
     * System under test.
     */
    private DeleteOrganizationValidation sut = new DeleteOrganizationValidation(relatedEntityCountMapper);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * Test.
     */
    @Test
    public void testSuccess()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(1L));

                allowing(relatedEntityCountMapper).execute(with(any(GetRelatedEntityCountRequest.class)));
                will(returnValue(0L));
            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testFail()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(1L));

                allowing(relatedEntityCountMapper).execute(with(any(GetRelatedEntityCountRequest.class)));
                will(returnValue(1L));
            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();
    }
}
