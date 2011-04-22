/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.OrganizationTreeDTO;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.OrgSelectedEvent;
import org.eurekastreams.web.client.events.data.GotOrganizationModelViewResponseEvent;
import org.eurekastreams.web.client.events.data.GotOrganizationTreeResponseEvent;
import org.eurekastreams.web.client.model.Fetchable;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

/**
 * Manages state and logic for the Organization Lookup control (adapted from MVVM pattern).
 */
public class OrgLookupViewModel
{
    /** View to manage. */
    private final OrgLookupContent view;

    /** Model to get the org tree. */
    private final Fetchable<Serializable> orgTreeModel;

    /** Model to get individual orgs. */
    private final Fetchable<Long> orgModel;

    /** The event bus. */
    private final EventBus eventBus;

    /** The selected org. */
    private OrganizationTreeDTO selectedOrg;

    /** Orgs in search order. */
    private final List<OrganizationTreeDTO> orgList = new ArrayList<OrganizationTreeDTO>();

    /**
     * Constructor.
     *
     * @param inView
     *            View to manage.
     * @param inOrgTreeModel
     *            Model to get the org tree.
     * @param inOrgModel
     *            Model to get individual orgs.
     * @param inEventBus
     *            The event bus.
     */
    public OrgLookupViewModel(final OrgLookupContent inView, final Fetchable<Serializable> inOrgTreeModel,
            final Fetchable<Long> inOrgModel, final EventBus inEventBus)
    {
        view = inView;
        orgTreeModel = inOrgTreeModel;
        orgModel = inOrgModel;
        eventBus = inEventBus;
    }

    /**
     * Sets up all event wiring and request initial data.
     */
    public void init()
    {
        // user clicked search button
        view.getSearchButton().addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inArg0)
            {
                searchForOrg();
            }
        });

        // user typed in search box
        view.getSearchBox().addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent ev)
            {
                if (ev.getNativeKeyCode() == KeyCodes.KEY_ENTER && !ev.isAnyModifierKeyDown())
                {
                    searchForOrg();
                }
            }
        });

        view.getSave().addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inArg0)
            {
                if (selectedOrg != null)
                {
                    view.getSaveCommand().execute();
                    view.close();
                }
            }
        });

        // org tree retrieved
        eventBus.addObserver(GotOrganizationTreeResponseEvent.class, new Observer<GotOrganizationTreeResponseEvent>()
        {
            public void update(final GotOrganizationTreeResponseEvent ev)
            {
                OrganizationTreeDTO rootOrg = ev.getResponse();
                orgList.clear();
                addOrgToSearchList(rootOrg);
                // treeIndex.clear();
                view.populateOrgTree(rootOrg);
            }
        });

        // user selected item in tree
        eventBus.addObserver(OrgSelectedEvent.class, new Observer<OrgSelectedEvent>()
        {
            public void update(final OrgSelectedEvent ev)
            {
                selectedOrg = ev.getOrg();
                orgModel.fetch(selectedOrg.getOrgId(), true);
            }
        });

        // info about selected org received
        eventBus.addObserver(GotOrganizationModelViewResponseEvent.class,
                new Observer<GotOrganizationModelViewResponseEvent>()
                {
                    public void update(final GotOrganizationModelViewResponseEvent ev)
                    {
                        OrganizationModelView org = ev.getResponse();
                        view.getOrgDescriptionPanel().removeStyleName(
                                StaticResourceBundle.INSTANCE.coreCss().displayNone());
                        view.getSave().removeStyleName(
                                StaticResourceBundle.INSTANCE.coreCss().lookupSelectButtonInactive());
                        view.getSave().addStyleName(StaticResourceBundle.INSTANCE.coreCss().lookupSelectButtonActive());
                        view.getOrgTitle().setText(org.getName());
                        view.getOrgOverview().setText(org.getOverview() != null ? org.getOverview() : "");
                        view.getLogoImage().setAvatar(org.getEntityId(), org.getAvatarId(), EntityType.ORGANIZATION);
                    }
                });

        // request org tree
        orgTreeModel.fetch(null, true);
    }

    /**
     * Performs the search for an organization by partial name.
     */
    private void searchForOrg()
    {
        String searchText = view.getSearchBox().getText();
        if (!searchText.isEmpty())
        {
            searchText = searchText.toLowerCase();
            for (OrganizationTreeDTO org : orgList)
            {
                if (org.getDisplayName().toLowerCase().startsWith(searchText))
                {
                    view.selectAndScrollToOrg(org);
                    return;
                }
            }
        }
    }

    /**
     * Add organization and children to search list.
     *
     * @param org
     *            the org.
     */
    private void addOrgToSearchList(final OrganizationTreeDTO org)
    {
        orgList.add(org);
        for (OrganizationTreeDTO child : org.getChildren())
        {
            addOrgToSearchList(child);
        }
    }

    /**
     * @return The selected org.
     */
    public OrganizationTreeDTO getSelectedOrg()
    {
        return selectedOrg;
    }
}
