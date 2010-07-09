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
import org.eurekastreams.server.domain.Job;
import org.eurekastreams.server.persistence.JobMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Authorizor test.
 *
 */
public class ModifyEmploymentAuthorizationStrategyTest
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
    private JobMapper mapper = context.mock(JobMapper.class);

    /**
     * Transformer mock.
     */
    private RequestTransformer optimusPrimeTransformer = context.mock(RequestTransformer.class);

    /**
     * System under test.
     */
    private ModifyEmploymentAuthorizationStrategy sut = new ModifyEmploymentAuthorizationStrategy(mapper,
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
        final List<Job> jobs = new ArrayList<Job>();
        final Job job1 = context.mock(Job.class, "e1");
        final Job job2 = context.mock(Job.class, "e2");
        jobs.add(job1);
        jobs.add(job2);

        context.checking(new Expectations()
        {
            {
                allowing(optimusPrimeTransformer).transform(actionContext);
                will(returnValue(1L));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getOpenSocialId();
                will(returnValue("opensocial"));

                oneOf(mapper).findPersonJobsByOpenSocialId("opensocial");
                will(returnValue(jobs));

                allowing(job1).getId();
                will(returnValue(0L));

                allowing(job2).getId();
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
        final List<Job> jobs = new ArrayList<Job>();
        final Job job1 = context.mock(Job.class, "e1");
        final Job job2 = context.mock(Job.class, "e2");
        jobs.add(job1);
        jobs.add(job2);

        context.checking(new Expectations()
        {
            {
                allowing(optimusPrimeTransformer).transform(actionContext);
                will(returnValue(1L));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getOpenSocialId();
                will(returnValue("opensocial"));

                oneOf(mapper).findPersonJobsByOpenSocialId("opensocial");
                will(returnValue(jobs));

                allowing(job1).getId();
                will(returnValue(0L));

                allowing(job2).getId();
                will(returnValue(2L));

            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }
}
