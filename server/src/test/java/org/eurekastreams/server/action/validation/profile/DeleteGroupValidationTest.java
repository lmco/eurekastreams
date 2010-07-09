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
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for DeleteGroupValidation class.
 * 
 */
@SuppressWarnings("unchecked")
public class DeleteGroupValidationTest
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
     * {@link FindByIdMapper}.
     */
    private FindByIdMapper<DomainGroup> findById = context.mock(FindByIdMapper.class);

    /**
     * {@link DomainGroup}.
     */
    private DomainGroup group = context.mock(DomainGroup.class);

    /**
     * System under test.
     */
    private DeleteGroupValidation sut = new DeleteGroupValidation(findById);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * Id used in test.
     */
    private Long id = 1L;

    /**
     * Good input.
     */
    @Test
    public void testGoodValidation()
    {

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(id));

                allowing(findById).execute(with(any(FindByIdRequest.class)));
                will(returnValue(group));
            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Bad input.
     */
    @Test(expected = ValidationException.class)
    public void testBadParamvalidation()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(id));

                allowing(findById).execute(with(any(FindByIdRequest.class)));
                will(returnValue(null));
            }
        });

        sut.validate(actionContext);
        context.assertIsSatisfied();
    }

}
