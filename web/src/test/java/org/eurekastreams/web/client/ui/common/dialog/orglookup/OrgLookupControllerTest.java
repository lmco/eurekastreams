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

import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.OrgInformationFoundEvent;
import org.eurekastreams.web.client.events.OrgLookupFindChildrenEvent;
import org.eurekastreams.web.client.events.OrgSelectedEvent;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.eurekastreams.server.domain.OrganizationTreeDTO;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

import com.google.gwt.user.client.Command;

/**
 * Controller test.
 */
public class OrgLookupControllerTest
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
    private OrgLookupController sut = null;

    /**
     * Mock model.
     */
    private OrgLookupModel model = context.mock(OrgLookupModel.class);

    /**
     * Mock view.
     */
    private OrgLookupView view = context.mock(OrgLookupView.class);

    /**
     * Mock event bus.
     */
    private EventBus eventBus = context.mock(EventBus.class);

    /**
     * Find children event int.
     */
    private AnonymousClassInterceptor<Observer<OrgLookupFindChildrenEvent>> findChildrenEventInt = 
        new AnonymousClassInterceptor<Observer<OrgLookupFindChildrenEvent>>();

    /**
     * Info found event int.
     */
    private AnonymousClassInterceptor<Observer<OrgInformationFoundEvent>> orgInfoFoundEventInt = 
        new AnonymousClassInterceptor<Observer<OrgInformationFoundEvent>>();

    /**
     * Org selected event int.
     */
    private AnonymousClassInterceptor<Observer<OrgSelectedEvent>> orgSelectedEventInt =
        new AnonymousClassInterceptor<Observer<OrgSelectedEvent>>();

    /**
     * Search click int.
     */
    private AnonymousClassInterceptor<Command> searchClickEventInt = new AnonymousClassInterceptor<Command>();

    /**
     * Setup test fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new OrgLookupController(model, view, eventBus);

        context.checking(new Expectations()
        {
            {
                oneOf(eventBus).addObserver(with(equal(OrgLookupFindChildrenEvent.class)), with(any(Observer.class)));
                will(findChildrenEventInt);

                oneOf(eventBus).addObserver(with(equal(OrgInformationFoundEvent.class)), with(any(Observer.class)));
                will(orgInfoFoundEventInt);

                oneOf(eventBus).addObserver(with(equal(OrgSelectedEvent.class)), with(any(Observer.class)));
                will(orgSelectedEventInt);

                oneOf(view).addSearchCommand(with(any(Command.class)));
                will(searchClickEventInt);

                oneOf(model).getChildren("");
                
                oneOf(view).wireUpCancelButton();
                
                oneOf(view).wireUpSaveButton();
            }
        });

        sut.init();
    }

    /**
     * Tests the children found event.
     */
    @Test
    public final void orgLookupFindChildrenEventTest()
    {
        final OrganizationTreeDTO dto = new OrganizationTreeDTO();
        final OrgLookupFindChildrenEvent event = new OrgLookupFindChildrenEvent(dto);

        context.checking(new Expectations()
        {
            {
                oneOf(view).onOrgChildrenFound(dto);
            }
        });

        findChildrenEventInt.getObject().update(event);

        context.assertIsSatisfied();
    }

    /**
     * Tests the selected info found event.
     */
    @Test
    public final void orgInformationFoundEventTest()
    {
        final OrganizationModelView modelView = new OrganizationModelView();
        final OrgInformationFoundEvent event = new OrgInformationFoundEvent(modelView);

        context.checking(new Expectations()
        {
            {
                oneOf(view).onOrgInformationFound(modelView);
            }
        });

        orgInfoFoundEventInt.getObject().update(event);

        context.assertIsSatisfied();
    }

    /**
     * Tests the org selected event.
     */
    @Test
    public final void orgSelectedEventTest()
    {
        final OrganizationTreeDTO dto = new OrganizationTreeDTO();
        final OrgSelectedEvent event = new OrgSelectedEvent(dto);

        context.checking(new Expectations()
        {
            {
                oneOf(model).setSelectedOrganization(dto);
            }
        });

        orgSelectedEventInt.getObject().update(event);

        context.assertIsSatisfied();
    }

    /**
     * Tests the search click event.
     */
    @Test
    public final void searchClickTest()
    {
        final String searchTxt = "search text";
        final OrganizationTreeDTO dto = new OrganizationTreeDTO();

        context.checking(new Expectations()
        {
            {
                oneOf(view).getSearchText();
                will(returnValue(searchTxt));

                oneOf(model).findOrg(searchTxt);
                will(returnValue(dto));

                oneOf(view).onOrgSearch(dto);
            }
        });

        searchClickEventInt.getObject().execute();

        context.assertIsSatisfied();
    }

    /**
     * Init test.
     */
    @Test
    public final void initTest()
    {
        context.assertIsSatisfied();
    }
}
