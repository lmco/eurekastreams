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

import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.OrgInformationFoundEvent;
import org.eurekastreams.web.client.events.OrgLookupFindChildrenEvent;
import org.eurekastreams.web.client.events.OrgSelectedEvent;
import org.eurekastreams.server.domain.OrganizationTreeDTO;

import com.google.gwt.user.client.Command;

/**
 * The org lookup modal controller.
 */
public class OrgLookupController
{
    /**
     * The model.
     */
    private OrgLookupModel model;

    /**
     * The view.
     */
    private OrgLookupView view;

    /**
     * The event bus.
     */
    private EventBus eventBus;

    /**
     * Constructor.
     * 
     * @param inModel
     *            the model.
     * @param inView
     *            the view.
     * @param inEventBus
     *            the event bus.
     */
    public OrgLookupController(final OrgLookupModel inModel, final OrgLookupView inView, final EventBus inEventBus)
    {
        model = inModel;
        view = inView;
        eventBus = inEventBus;

    }

    /**
     * Wire up the events.
     */
    public void init()
    {

        eventBus.addObserver(OrgLookupFindChildrenEvent.class, new Observer<OrgLookupFindChildrenEvent>()
        {
            public void update(final OrgLookupFindChildrenEvent event)
            {
                view.onOrgChildrenFound(event.getResults());
            }
        });

        eventBus.addObserver(OrgInformationFoundEvent.class, new Observer<OrgInformationFoundEvent>()
        {
            public void update(final OrgInformationFoundEvent event)
            {
                view.onOrgInformationFound(event.getOrg());
            }
        });

        eventBus.addObserver(OrgSelectedEvent.class, new Observer<OrgSelectedEvent>()
        {
            public void update(final OrgSelectedEvent event)
            {
                model.setSelectedOrganization(event.getOrg());
            }
        });

        view.addSearchCommand(new Command()
        {
            public void execute()
            {
                OrganizationTreeDTO found = model.findOrg(view.getSearchText());

                if (null != found)
                {
                    view.onOrgSearch(found);
                }
            }
        });
        
        view.wireUpCancelButton();
        view.wireUpSaveButton();

        model.getChildren("");
    }

}
