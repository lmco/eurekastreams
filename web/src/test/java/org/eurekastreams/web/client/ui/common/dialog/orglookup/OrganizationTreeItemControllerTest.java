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

import org.eurekastreams.server.domain.OrganizationTreeDTO;
import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.OrgSelectedEvent;
import org.eurekastreams.web.client.ui.common.ULPanel;
import org.eurekastreams.web.client.ui.pages.master.CoreCss;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.Label;

/**
 * Organization tree item controller.
 */
public class OrganizationTreeItemControllerTest
{
    /**
     * System under test.
     */
    private OrganizationTreeItemController sut;

    /**
     * Context for building mock objects.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
            GWTMockUtilities.disarm();
        }
    };

    /**
     * Mock event bus.
     */
    private EventBus eventBus = context.mock(EventBus.class);

    /**
     * Css Resource.
     */
    private CoreCss css = context.mock(CoreCss.class);

    /**
     * Setup the test fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new OrganizationTreeItemController(eventBus, css);
    }

    /**
     * Wire up the expand button test.
     */
    @Test
    public final void wireUpExpandButtonTest()
    {
        final Label expand = context.mock(Label.class, "expand");
        final ULPanel children = context.mock(ULPanel.class, "children");

        final AnonymousClassInterceptor<ClickHandler> expandClickInt = new AnonymousClassInterceptor<ClickHandler>();

        context.checking(new Expectations()
        {
            {
                allowing(css).expanded();
                will(returnValue("expanded"));

                oneOf(expand).addClickHandler(with(any(ClickHandler.class)));
                will(expandClickInt);

                oneOf(children).isVisible();
                will(returnValue(true));

                oneOf(children).setVisible(false);

                oneOf(expand).removeStyleName("expanded");
            }
        });

        sut.wireUpExpandButton(expand, children);
        expandClickInt.getObject().onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Wire up the select button test.
     */
    @Test
    public final void wireUpSelectButtonTest()
    {
        final Label displayName = context.mock(Label.class, "displayName");
        final OrganizationTreeItemComposite treeItem = context.mock(OrganizationTreeItemComposite.class, "treeItem");
        final OrganizationTreeDTO orgTree = context.mock(OrganizationTreeDTO.class, "orgTree");
        final OrganizationTreeItemComposite parent = context.mock(OrganizationTreeItemComposite.class, "parent");

        final AnonymousClassInterceptor<ClickHandler> displayClickInt = new AnonymousClassInterceptor<ClickHandler>();

        context.checking(new Expectations()
        {
            {
                oneOf(displayName).addClickHandler(with(any(ClickHandler.class)));
                will(displayClickInt);

                oneOf(css).selected();
                will(returnValue("selected"));

                oneOf(treeItem).addStyleName("selected");

                oneOf(parent).showChildren(true);

                oneOf(parent).showAncestors();

                oneOf(eventBus).notifyObservers(with(any(OrgSelectedEvent.class)));
            }
        });

        sut.wireUpSelectButton(displayName, treeItem, orgTree, parent);

        displayClickInt.getObject().onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Show children test.
     */
    @Test
    public final void showChildrenTest()
    {
        final Label expand = context.mock(Label.class, "expand");
        final ULPanel children = context.mock(ULPanel.class, "children");

        context.checking(new Expectations()
        {
            {
                allowing(css).expanded();
                will(returnValue("expanded"));
                oneOf(children).setVisible(true);

                oneOf(expand).addStyleName("expanded");
            }
        });

        sut.showChildren(expand, children, true);
        context.assertIsSatisfied();
    }

    /**
     * Hide children test.
     */
    @Test
    public final void hideChildrenTest()
    {
        final Label expand = context.mock(Label.class, "expand");
        final ULPanel children = context.mock(ULPanel.class, "children");

        context.checking(new Expectations()
        {
            {

                allowing(css).expanded();
                will(returnValue("expanded"));
                oneOf(children).setVisible(false);

                oneOf(expand).removeStyleName("expanded");
            }
        });

        sut.showChildren(expand, children, false);
        context.assertIsSatisfied();
    }

    /**
     * Select test without anything currently selected.
     */
    @Test
    public final void selectNoneSelectedTest()
    {
        final OrganizationTreeItemComposite treeItem = context.mock(OrganizationTreeItemComposite.class, "treeItem");
        final OrganizationTreeDTO orgTree = context.mock(OrganizationTreeDTO.class, "orgTree");
        final OrganizationTreeItemComposite parent = context.mock(OrganizationTreeItemComposite.class, "parent");

        OrganizationTreeItemController.selectedTreeItem = null;

        context.checking(new Expectations()
        {
            {

                oneOf(css).selected();
                will(returnValue("selected"));
                oneOf(treeItem).addStyleName("selected");
                oneOf(parent).showChildren(true);
                oneOf(parent).showAncestors();
                oneOf(eventBus).notifyObservers(with(any(OrgSelectedEvent.class)));
            }
        });

        sut.select(treeItem, orgTree, parent);
        context.assertIsSatisfied();
    }

    /**
     * Select test with something currently selected.
     */
    @Test
    public final void selectSelectedTest()
    {
        final OrganizationTreeItemComposite treeItem = context.mock(OrganizationTreeItemComposite.class, "treeItem");
        final OrganizationTreeDTO orgTree = context.mock(OrganizationTreeDTO.class, "orgTree");
        final OrganizationTreeItemComposite parent = context.mock(OrganizationTreeItemComposite.class, "parent");

        final OrganizationTreeItemComposite selectedTreeItem = context.mock(OrganizationTreeItemComposite.class,
                "selectedTreeItem");

        OrganizationTreeItemController.selectedTreeItem = selectedTreeItem;

        context.checking(new Expectations()
        {
            {
                allowing(css).selected();
                will(returnValue("selected"));
                oneOf(selectedTreeItem).removeStyleName("selected");
                oneOf(treeItem).addStyleName("selected");
                oneOf(parent).showChildren(true);
                oneOf(parent).showAncestors();
                oneOf(eventBus).notifyObservers(with(any(OrgSelectedEvent.class)));
            }
        });

        sut.select(treeItem, orgTree, parent);
        context.assertIsSatisfied();
    }

    /**
     * Show ancestors test.
     */
    @Test
    public final void showAncestorsTest()
    {
        final OrganizationTreeItemComposite parent = context.mock(OrganizationTreeItemComposite.class, "parent");

        context.checking(new Expectations()
        {
            {
                oneOf(parent).showChildren(true);
                oneOf(parent).showAncestors();
            }
        });

        sut.showAncestors(parent);
        context.assertIsSatisfied();
    }
}
