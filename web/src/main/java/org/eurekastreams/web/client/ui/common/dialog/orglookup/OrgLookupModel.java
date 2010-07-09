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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.domain.OrganizationTreeDTO;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.OrgInformationFoundEvent;
import org.eurekastreams.web.client.events.OrgLookupFindChildrenEvent;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Org lookup modal model.
 */
public class OrgLookupModel
{
    /**
     * Action processor.
     */
    private ActionProcessor processor;

    /**
     * Event bus.
     */
    private EventBus eventBus;

    /**
     * Index (used to search).
     */
    private List<OrganizationTreeDTO> orgIndex = new ArrayList<OrganizationTreeDTO>();

    /**
     * The selected org.
     */
    private OrganizationTreeDTO selectedOrg;

    /**
     * Constructor.
     * 
     * @param inProcessor
     *            action processor.
     * @param inEventBus
     *            event bus.
     */
    public OrgLookupModel(final ActionProcessor inProcessor, final EventBus inEventBus)
    {
        processor = inProcessor;
        eventBus = inEventBus;
    }

    /**
     * Get the children of an org.
     * 
     * @param orgShortName
     *            the org shortname.
     */
    public void getChildren(final String orgShortName)
    {
        processor.makeRequest(new ActionRequestImpl<OrganizationTreeDTO>("getOrganizationTree", null),
                new AsyncCallback<OrganizationTreeDTO>()
                {

                    public void onFailure(final Throwable arg0)
                    {
                        // Intentially left blank.

                    }

                    public void onSuccess(final OrganizationTreeDTO results)
                    {
                        eventBus.notifyObservers(new OrgLookupFindChildrenEvent(results));
                        indexOrg(results);
                    }
                });
    }

    /**
     * Find an org.
     * 
     * @param prefix
     *            search string.
     * @return the first match org.
     */
    public OrganizationTreeDTO findOrg(final String prefix)
    {
        OrganizationTreeDTO result = null;

        for (OrganizationTreeDTO org : orgIndex)
        {
            if (org.getDisplayName().toLowerCase().startsWith(prefix.toLowerCase()))
            {
                result = org;
                break;
            }
        }

        return result;
    }

    /**
     * Index org. Package scope for testing.
     * 
     * @param org
     *            the org.
     */
    void indexOrg(final OrganizationTreeDTO org)
    {
        orgIndex.add(org);

        for (OrganizationTreeDTO child : org.getChildren())
        {
            indexOrg(child);
        }
    }

    /**
     * Set the selected org.
     * 
     * @param inSelectedOrg
     *            the org.
     */
    public void setSelectedOrganization(final OrganizationTreeDTO inSelectedOrg)
    {
        selectedOrg = inSelectedOrg;

        processor.makeRequest(new ActionRequestImpl<OrganizationModelView>("getOrganizationById", selectedOrg
                .getOrgId()), new AsyncCallback<OrganizationModelView>()
        {

            public void onFailure(final Throwable arg0)
            {
                // Intentially left blank.

            }

            public void onSuccess(final OrganizationModelView result)
            {
                eventBus.notifyObservers(new OrgInformationFoundEvent(result));
            }
        });
    }

    /**
     * Get the selected org.
     * 
     * @return the selected org.
     */
    public OrganizationTreeDTO getSelectedOrg()
    {
        return selectedOrg;
    }
}
