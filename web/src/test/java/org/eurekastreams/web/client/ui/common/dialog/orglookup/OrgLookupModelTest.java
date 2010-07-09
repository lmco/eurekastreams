/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.dialog.orglookup;

import java.util.ArrayList;

import junit.framework.Assert;

import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.OrgInformationFoundEvent;
import org.eurekastreams.web.client.events.OrgLookupFindChildrenEvent;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionRequest;
import org.eurekastreams.server.domain.OrganizationTreeDTO;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Model tests.
 */
public class OrgLookupModelTest
{
    /**
     * Context for building mock objects.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private OrgLookupModel sut = null;

    /**
     * The action processor.
     */
    private ActionProcessor processor = context.mock(ActionProcessor.class);

    /**
     * The event bus.
     */
    private EventBus eventBus = context.mock(EventBus.class);

    /**
     * Setup test fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new OrgLookupModel(processor, eventBus);
    }

    /**
     * Get children failure.
     */
    @Test
    public final void getChildrenFailureTest()
    {
        final String orgShortName = "";

        final AnonymousClassInterceptor<AsyncCallback<OrganizationTreeDTO>> cbInt = 
            new AnonymousClassInterceptor<AsyncCallback<OrganizationTreeDTO>>();

        context.checking(new Expectations()
        {
            {
                oneOf(processor).makeRequest(with(any(ActionRequest.class)), with(any(AsyncCallback.class)));
                will(cbInt);

            }
        });

        sut.getChildren(orgShortName);

        // Does nothing.
        cbInt.getObject().onFailure(null);

        context.assertIsSatisfied();
    }

    /**
     * Get children success.
     */
    @Test
    public final void getChildrenSuccessTest()
    {
        final String orgShortName = "";

        final AnonymousClassInterceptor<AsyncCallback<OrganizationTreeDTO>> cbInt = 
            new AnonymousClassInterceptor<AsyncCallback<OrganizationTreeDTO>>();

        final OrganizationTreeDTO result = new OrganizationTreeDTO();
        result.setChildren(new ArrayList<OrganizationTreeDTO>());

        context.checking(new Expectations()
        {
            {
                oneOf(processor).makeRequest(with(any(ActionRequest.class)), with(any(AsyncCallback.class)));
                will(cbInt);

                oneOf(eventBus).notifyObservers(with(any(OrgLookupFindChildrenEvent.class)));

            }
        });

        sut.getChildren(orgShortName);

        // Does nothing.
        cbInt.getObject().onSuccess(result);

        context.assertIsSatisfied();
    }

    /**
     * Tests finding an org.
     */
    @Test
    public final void findOrgTest()
    {
        final OrganizationTreeDTO org = new OrganizationTreeDTO();
        org.setDisplayName("Org Name");
        org.setChildren(new ArrayList<OrganizationTreeDTO>());

        sut.indexOrg(org);

        Assert.assertEquals(org, sut.findOrg(org.getDisplayName()));
    }

    /**
     * Set the selected org.
     */
    @Test
    public final void setSelectedOrganizationSuccessTest()
    {
        final AnonymousClassInterceptor<AsyncCallback<OrganizationModelView>> cbInt = 
            new AnonymousClassInterceptor<AsyncCallback<OrganizationModelView>>();

        final OrganizationTreeDTO org = new OrganizationTreeDTO();
        org.setChildren(new ArrayList<OrganizationTreeDTO>());

        context.checking(new Expectations()
        {
            {
                oneOf(processor).makeRequest(with(any(ActionRequest.class)), with(any(AsyncCallback.class)));
                will(cbInt);

                oneOf(eventBus).notifyObservers(with(any(OrgInformationFoundEvent.class)));

            }
        });

        sut.setSelectedOrganization(org);

        cbInt.getObject().onSuccess(null);

        Assert.assertEquals(org, sut.getSelectedOrg());

        context.assertIsSatisfied();
    }

    /**
     * Set the selected org.
     */
    @Test
    public final void setSelectedOrganizationFailureTest()
    {
        final AnonymousClassInterceptor<AsyncCallback<OrganizationModelView>> cbInt = 
            new AnonymousClassInterceptor<AsyncCallback<OrganizationModelView>>();

        final OrganizationTreeDTO org = new OrganizationTreeDTO();
        org.setChildren(new ArrayList<OrganizationTreeDTO>());

        context.checking(new Expectations()
        {
            {
                oneOf(processor).makeRequest(with(any(ActionRequest.class)), with(any(AsyncCallback.class)));
                will(cbInt);

                never(eventBus).notifyObservers(with(any(OrgInformationFoundEvent.class)));

            }
        });

        sut.setSelectedOrganization(org);

        cbInt.getObject().onFailure(null);

        context.assertIsSatisfied();
    }
}
