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
package org.eurekastreams.server.action.execution.profile;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.BannerableDTO;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.mappers.BannerableMapper;
import org.eurekastreams.server.persistence.mappers.requests.UpdateCachedBannerMapperRequest;
import org.eurekastreams.server.persistence.mappers.stream.BaseArgCachedDomainMapper;
import org.eurekastreams.server.service.actions.strategies.ImageWriter;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.userdetails.UserDetails;

/**
 * Test class for the DeleteBannerAction.
 */
public class DeleteBannerExecutionTest
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
     * ActionContext mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Principal mock.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * The mock mapper to be used by the action.
     */
    private BannerableMapper bannerableMapper = context.mock(BannerableMapper.class);

    /**
     * Mocked instance of the Cache Banner Mapper.
     */
    private BaseArgCachedDomainMapper<UpdateCachedBannerMapperRequest, Object> cachedBannerMapperMock = context
            .mock(BaseArgCachedDomainMapper.class);

    /**
     * Mocked organization who will get the new tab.
     */
    private Organization organization = context.mock(Organization.class);

    /**
     * Mocked ImageWriter.
     */
    private ImageWriter imageWriter = context.mock(ImageWriter.class);

    /**
     * Subject under test.
     */
    private DeleteBannerExecution sut = null;

    /**
     * The mock user information from the session.
     */
    private UserDetails user = context.mock(UserDetails.class);

    /**
     * Empty params.
     */
    final Long testParam = 5L;

    /**
     * DTO for the Bannerable Object.
     */
    BannerableDTO bannerDTO;

    /**
     * Setup the test.
     */
    @Before
    public final void setUp()
    {
        sut = new DeleteBannerExecution(bannerableMapper, cachedBannerMapperMock, imageWriter);
    }

    /**
     * Testing the delete action.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void performActionWithOutNullBannerId() throws Exception
    {

        bannerDTO = new BannerableDTO("notnull", 5L);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(testParam));

                allowing(organization).getId();
                will(returnValue(5L));

                ignoring(user);

                oneOf(bannerableMapper).getBannerableDTO(5L);
                will(returnValue(bannerDTO));

                oneOf(imageWriter).delete("nnotnull");

                oneOf(bannerableMapper).updateBannerId(5L, null);
                will(returnValue(true));

                oneOf(cachedBannerMapperMock).execute(with(any(UpdateCachedBannerMapperRequest.class)));
            }
        });

        sut.execute(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test case with a null banner id.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void performActionWithNullBannerId() throws Exception
    {

        bannerDTO = new BannerableDTO(null, 5L);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(testParam));

                ignoring(user);

                oneOf(bannerableMapper).getBannerableDTO(5L);
                will(returnValue(bannerDTO));

                oneOf(bannerableMapper).updateBannerId(5L, null);
                will(returnValue(true));

                oneOf(cachedBannerMapperMock).execute(with(any(UpdateCachedBannerMapperRequest.class)));

            }
        });

        sut.execute(actionContext);
        context.assertIsSatisfied();
    }
}
