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
package org.eurekastreams.server.action.execution.gallery;

import junit.framework.Assert;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.AnonymousClassInterceptor;
import org.eurekastreams.server.action.request.gallery.GetGallerySearchResultsRequest;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.GalleryItem;
import org.eurekastreams.server.domain.GalleryItemType;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.persistence.mappers.LuceneSearchMapper;
import org.eurekastreams.server.persistence.mappers.requests.LuceneSearchRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetGallerySearchResultsExecution} class.
 *
 */
@SuppressWarnings("unchecked")
public class GetGallerySearchResultsExecutionTest
{
    /**
     * System under test.
     */
    private GetGallerySearchResultsExecution sut;

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
     * Mock mapper.
     */
    private LuceneSearchMapper<GalleryItem> mapper = context.mock(LuceneSearchMapper.class);

    /**
     * Mocked principal object.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Setup the test suite.
     */
    @Before
    public void setup()
    {
        sut = new GetGallerySearchResultsExecution(mapper);
    }


    /**
     * Perform the action for themes.
     *
     * @throws Exception
     *             happens due to limitation in AnonymousClassInterceptor. Can't intercept and return a value.
     */
    @Test(expected = NullPointerException.class)
    public final void performActionTheme() throws Exception
    {
        final GetGallerySearchResultsRequest request = new GetGallerySearchResultsRequest();

        final ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);

        request.setMaxResultsPerPage(1);
        request.setSearchText("search test");
        request.setSort("created");
        request.setStartingIndex(0);
        request.setType(GalleryItemType.THEME);

        final AnonymousClassInterceptor<LuceneSearchRequest> requestInt =
            new AnonymousClassInterceptor<LuceneSearchRequest>();

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(with(any(LuceneSearchRequest.class)));
                will(requestInt);
            }
        });

        sut.execute(currentContext);

        Assert.assertEquals(request.getStartingIndex(), requestInt.getObject().getFirstResult());
        Assert.assertEquals(request.getMaxResultsPerPage(), requestInt.getObject().getMaxResults());
        Assert.assertEquals(request.getSearchText(), requestInt.getObject().getSearchString());
        Assert.assertEquals(Theme.class, requestInt.getObject().getObjectType());
        Assert.assertNotNull(requestInt.getObject().getSortFields());
        Assert.assertNotNull(requestInt.getObject().getFields());

        context.assertIsSatisfied();
    }

    /**
     * Perform the action for gadgets.
     *
     * @throws Exception
     *             happens due to limitation in AnonymousClassInterceptor. Can't intercept and return a value.
     */
    @Test(expected = NullPointerException.class)
    public final void performActionGadget() throws Exception
    {
        final GetGallerySearchResultsRequest request = new GetGallerySearchResultsRequest();

        final ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);

        request.setMaxResultsPerPage(1);
        request.setSearchText("search test");
        request.setSort("created");
        request.setStartingIndex(0);
        request.setType(GalleryItemType.GADGET);

        final AnonymousClassInterceptor<LuceneSearchRequest> requestInt =
            new AnonymousClassInterceptor<LuceneSearchRequest>();

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(with(any(LuceneSearchRequest.class)));
                will(requestInt);
            }
        });

        sut.execute(currentContext);

        Assert.assertEquals(request.getStartingIndex(), requestInt.getObject().getFirstResult());
        Assert.assertEquals(request.getMaxResultsPerPage(), requestInt.getObject().getMaxResults());
        Assert.assertEquals(request.getSearchText(), requestInt.getObject().getSearchString());
        Assert.assertEquals(GadgetDefinition.class, requestInt.getObject().getObjectType());
        Assert.assertNotNull(requestInt.getObject().getSortFields());
        Assert.assertNotNull(requestInt.getObject().getFields());

        context.assertIsSatisfied();
    }
}
