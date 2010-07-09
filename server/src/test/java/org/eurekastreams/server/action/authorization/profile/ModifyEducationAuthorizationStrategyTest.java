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
package org.eurekastreams.server.action.authorization.profile;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.transformer.RequestTransformer;
import org.eurekastreams.server.domain.Enrollment;
import org.eurekastreams.server.persistence.EnrollmentMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Authorizor test.
 *
 */
public class ModifyEducationAuthorizationStrategyTest
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
     * Mapper mock.
     */
    private EnrollmentMapper mapper = context.mock(EnrollmentMapper.class);

    /**
     * Transformer mock.
     */
    private RequestTransformer optimusPrimeTransformer = context.mock(RequestTransformer.class);
    /**
     * System under test.
     */
    private ModifyEducationAuthorizationStrategy sut = new ModifyEducationAuthorizationStrategy(mapper,
            optimusPrimeTransformer);

    /**
     * Mocked instance of the principal object.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Principal mock.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Test.
     */
    @Test
    public void authorize()
    {
        final List<Enrollment> enrollments = new ArrayList<Enrollment>();
        final Enrollment enrollment1 = context.mock(Enrollment.class, "e1");
        final Enrollment enrollment2 = context.mock(Enrollment.class, "e2");
        enrollments.add(enrollment1);
        enrollments.add(enrollment2);

        context.checking(new Expectations()
        {
            {
                allowing(optimusPrimeTransformer).transform(actionContext);
                will(returnValue(1L));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getOpenSocialId();
                will(returnValue("opensocial"));

                oneOf(mapper).findPersonEnrollmentsByOpenSocialId("opensocial");
                will(returnValue(enrollments));

                allowing(enrollment1).getId();
                will(returnValue(0L));

                allowing(enrollment2).getId();
                will(returnValue(1L));

            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = AuthorizationException.class)
    public void dontAuthorize()
    {
        final List<Enrollment> enrollments = new ArrayList<Enrollment>();
        final Enrollment enrollment1 = context.mock(Enrollment.class, "e1");
        final Enrollment enrollment2 = context.mock(Enrollment.class, "e2");
        enrollments.add(enrollment1);
        enrollments.add(enrollment2);

        context.checking(new Expectations()
        {
            {
                allowing(optimusPrimeTransformer).transform(actionContext);
                will(returnValue(1L));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getOpenSocialId();
                will(returnValue("opensocial"));

                oneOf(mapper).findPersonEnrollmentsByOpenSocialId("opensocial");
                will(returnValue(enrollments));

                allowing(enrollment1).getId();
                will(returnValue(0L));

                allowing(enrollment2).getId();
                will(returnValue(2L));

            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }
}
