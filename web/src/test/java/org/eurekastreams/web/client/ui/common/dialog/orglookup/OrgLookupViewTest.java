/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import java.util.HashMap;

import junit.framework.Assert;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.OrganizationTreeDTO;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.eurekastreams.web.client.ui.common.ULPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * View test.
 */
public class OrgLookupViewTest
{
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
     * System under test.
     */
    private OrgLookupView sut;

    /**
     * The model.
     */
    private OrgLookupModel model = context.mock(OrgLookupModel.class);

    /**
     * The composite.
     */
    private OrgLookupContent composite = context.mock(OrgLookupContent.class);

    /**
     * The save command.
     */
    private Command saveCommand = context.mock(Command.class);

    /**
     * Setup test fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new OrgLookupView(composite, model, saveCommand);
        sut.orgDescriptionPanel = context.mock(FlowPanel.class, "orgDescriptionPanel");
        sut.cancel = context.mock(Hyperlink.class, "cancel");
        sut.logoImage = context.mock(AvatarWidget.class, "logoImage");
        sut.orgList = context.mock(ULPanel.class, "orgList");
        sut.orgOverview = context.mock(Label.class, "orgOverview");
        sut.orgTitle = context.mock(Label.class, "orgTitle");
        sut.save = context.mock(Hyperlink.class, "save");
        sut.searchBox = context.mock(TextBox.class, "searchBox");
        sut.searchButton = context.mock(Label.class, "searchButton");
    }

    /**
     * Wires up the cancel button.
     */
    @Test
    public final void wireUpCancelButtonTest()
    {
        final AnonymousClassInterceptor<ClickHandler> cancelHandler = new AnonymousClassInterceptor<ClickHandler>();

        context.checking(new Expectations()
        {
            {
                oneOf(sut.cancel).addClickHandler(with(any(ClickHandler.class)));
                will(cancelHandler);

                oneOf(composite).close();
            }
        });

        sut.wireUpCancelButton();

        cancelHandler.getObject().onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Wires up the save button.
     */
    @Test
    public final void wireUpSaveButtonTest()
    {
        final AnonymousClassInterceptor<ClickHandler> saveHandler = new AnonymousClassInterceptor<ClickHandler>();

        context.checking(new Expectations()
        {
            {
                oneOf(sut.save).addClickHandler(with(any(ClickHandler.class)));
                will(saveHandler);

                oneOf(composite).close();

                oneOf(saveCommand).execute();
            }
        });

        sut.wireUpSaveButton();

        saveHandler.getObject().onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Test onOrgChildrenFound.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void onOrgChildrenFoundTest()
    {
        final OrganizationTreeDTO results = new OrganizationTreeDTO();

        context.checking(new Expectations()
        {
            {
                oneOf(composite).getOrganizationTreeItem(with(equal(results)),
                        with(any(OrganizationTreeItemComposite.class)), with(equal(sut.orgList)),
                        with(any(HashMap.class)));
            }
        });

        sut.onOrgChildrenFound(results);

        context.assertIsSatisfied();
    }

    /**
     * Test onOrgInformationFound.
     */
    @Test
    public final void onOrgInformationFoundTest()
    {
        final OrganizationModelView modelView = new OrganizationModelView();
        modelView.setName("Org Name");
        modelView.setAvatarId("avatarId");
        modelView.setEntityId(1L);

        context.checking(new Expectations()
        {
            {
                oneOf(sut.orgTitle).setText(modelView.getName());
                oneOf(sut.logoImage).setAvatar(modelView.getEntityId(), modelView.getAvatarId(),
                        EntityType.ORGANIZATION);

                oneOf(sut.orgDescriptionPanel).removeStyleName("display-none");
                oneOf(sut.save).removeStyleName("lookup-select-button-inactive");
                oneOf(sut.save).addStyleName("lookup-select-button-active");

                oneOf(sut.orgOverview).setText("");
            }
        });

        sut.onOrgInformationFound(modelView);

        context.assertIsSatisfied();
    }

    /**
     * Tests getSearchText.
     */
    @Test
    public final void getSearchTextTest()
    {
        final String searchTxt = "search text";

        context.checking(new Expectations()
        {
            {
                oneOf(sut.searchBox).getText();
                will(returnValue(searchTxt));
            }
        });

        Assert.assertEquals(searchTxt, sut.getSearchText());

        context.assertIsSatisfied();
    }

    /**
     * Tests onOrgSearch.
     */
    @Test(expected = NullPointerException.class)
    public final void onOrgSearchTest()
    {
        final OrganizationTreeDTO found = context.mock(OrganizationTreeDTO.class);

        sut.onOrgSearch(found);
    }

}
