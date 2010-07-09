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

import java.io.IOException;

import org.apache.commons.fileupload.FileItem;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.profile.SaveImageRequest;
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

/**
 * Test suite for the {@link UpdateBannerExecution} class.
 *
 */
public class UpdateBannerExecutionTest
{
    /**
     * System under test.
     */
    private UpdateBannerExecution sut;

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
     * The mock mapper to be used by the action.
     */
    private final BannerableMapper bannerableMapper = context.mock(BannerableMapper.class);;

    /**
     * Mocked instance of the Principal class for this test suite.
     */
    private final Principal principalMock = context.mock(Principal.class);

    /**
     * Mocked instance of the BaseArgCachedDomainMapper for this test suite.
     */
    private final BaseArgCachedDomainMapper<UpdateCachedBannerMapperRequest, Object> updateCacheMock =
        context.mock(BaseArgCachedDomainMapper.class);

    /**
     * Used to verify that the uploaded file is really an image.
     */
    private final ImageWriter imageWriter = context.mock(ImageWriter.class);

    /**
     * A id for testing.
     */
    private static final long ORG_ID = 23L;

    /**
     * A fake banner id.
     */
    private static final String BANNER_ID = "123bannerId";

    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new UpdateBannerExecution(bannerableMapper, updateCacheMock, imageWriter);
    }

    /**
     * Test the action.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void performAction() throws Exception
    {
        final FileItem file = null;
        context.checking(new Expectations()
        {
            {

                oneOf(bannerableMapper).updateBannerId(ORG_ID, BANNER_ID);
                will(returnValue(true));

                oneOf(bannerableMapper).flush();

                oneOf(updateCacheMock).execute(with(any(UpdateCachedBannerMapperRequest.class)));

                oneOf(imageWriter).write(file, "n" + BANNER_ID);
            }
        });
        SaveImageRequest currentRequest = new SaveImageRequest(file, ORG_ID, BANNER_ID);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        sut.execute(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Test the action fails appropriately..
     *
     * @throws Exception
     *             not expected
     */
    @Test(expected = ExecutionException.class)
    public void performActionFails() throws Exception
    {
        final FileItem file = null;
        context.checking(new Expectations()
        {
            {

                oneOf(bannerableMapper).updateBannerId(ORG_ID, BANNER_ID);
                will(returnValue(true));

                oneOf(bannerableMapper).flush();

                oneOf(updateCacheMock).execute(with(any(UpdateCachedBannerMapperRequest.class)));

                oneOf(imageWriter).write(file, "n" + BANNER_ID);
                will(throwException(new IOException()));
            }
        });
        SaveImageRequest currentRequest = new SaveImageRequest(file, ORG_ID, BANNER_ID);
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        sut.execute(currentContext);

        context.assertIsSatisfied();
    }
}
