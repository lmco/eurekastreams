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
package org.eurekastreams.web.client.ui.pages.profile.widgets;

import java.util.ArrayList;
import java.util.HashMap;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.action.request.profile.GetBreadcrumbsListRequest;
import org.eurekastreams.server.domain.BreadcrumbDTO;
import org.eurekastreams.server.domain.DomainGroupEntity;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

/**
 * Shows the bread crumbs for navigation.
 */
public class BreadcrumbPanel extends FlowPanel
{
    /**
     * The action processor.
     */
    private ActionProcessor processor;

    /**
     * Constructor.
     * 
     * @param inProcessor
     *            the processor to set.
     */
    public BreadcrumbPanel(final ActionProcessor inProcessor)
    {
        processor = inProcessor;
        this.addStyleName("breadcrumb");
    }

    /**
     * Specify the Person we're looking at. Set the breadcrumb trail to this person and the parent organization(s).
     * TODO: Change DomainEntity to Entity
     * 
     * @param inPerson
     *            the person whose info we are displaying.
     */
    public void setPerson(final Person inPerson)
    {
        buildBreadcrumbs(inPerson.getParentOrganization(), inPerson.getPreferredName() + " " + inPerson.getLastName(),
                true, false);
    }

    /**
     * Specify the Group we're looking at. Set the breadcrumb trail to this group and the parent organization(s).
     * 
     * @param inGroup
     *            the inGroup whose info we are displaying.
     * @param showItemLink
     *            flag to show this item in the trail as a link that wipes out all params when clicked.
     */
    public void setGroup(final DomainGroupEntity inGroup, final boolean showItemLink)
    {
        buildBreadcrumbs(inGroup.getParentOrganization(), inGroup.getName(), true, showItemLink);
    }

    /**
     * Set up the breadcrumbs based on an organization. Note that we should be able to collapse this and setPerson()
     * once we have the Resource hierarchy.
     * 
     * @param inOrganization
     *            the new organization
     */
    public void setOrganization(final OrganizationModelView inOrganization)
    {
        buildBreadcrumbs(inOrganization, inOrganization.getName(), false, false);
    }

    /**
     * Makes the action call to build the breadcrumb list.
     * 
     * @param org
     *            the organization starting point.
     * @param thisItem
     *            the display name of the end node in the breadcrumb trail.
     * @param showParent
     *            flag to show this item's direct parent in the trail; only orgs do not need to show this.
     * @param showItemLink
     *            flag to show this item in the trail as a link that wipes out all params when clicked.
     */
    private void buildBreadcrumbs(final OrganizationModelView org, final String thisItem, final boolean showParent,
            final boolean showItemLink)
    {
        GetBreadcrumbsListRequest request = new GetBreadcrumbsListRequest(org.getEntityId());

        // No need to get the organization hierarchy if the supplied org is the root org
        // and breadcrumbs are being displayed on an Org profile page.
        if (!showParent && (org.getParentOrganizationId() == org.getEntityId()))
        {
            displayBreadcrumbs(new ArrayList<BreadcrumbDTO>(), thisItem, showParent, showItemLink);
        }
        else
        {
            processor.makeRequest(new ActionRequestImpl<ArrayList<BreadcrumbDTO>>("getBreadcrumbsList", request),
                    new AsyncCallback<ArrayList<BreadcrumbDTO>>()
                    {
                        public void onFailure(final Throwable caught)
                        {
                        }

                        public void onSuccess(final ArrayList<BreadcrumbDTO> breadcrumbs)
                        {
                            // Don't show the parent if your parent is the root org.
                            if (showParent)
                            {
                                breadcrumbs
                                        .add(new BreadcrumbDTO(org.getName(), Page.ORGANIZATIONS, org.getShortName()));
                            }
                            displayBreadcrumbs(breadcrumbs, thisItem, showParent, showItemLink);
                        }
                    });
        }
    }

    /**
     * Makes the action call to build the breadcrumb list.
     * 
     * @param org
     *            the organization starting point.
     * @param thisItem
     *            the display name of the end node in the breadcrumb trail.
     * @param showParent
     *            flag to show this item's direct parent in the trail; only orgs do not need to show this.
     * @param showItemLink
     *            flag to show this item in the trail as a link that wipes out all params when clicked.
     */
    private void buildBreadcrumbs(final Organization org, final String thisItem, final boolean showParent,
            final boolean showItemLink)
    {
        GetBreadcrumbsListRequest request = new GetBreadcrumbsListRequest(org.getId());

        // No need to get the organization hierarchy if the supplied org is the root org
        // and breadcrumbs are being displayed on an Org profile page.
        if (!showParent && (org.getParentOrgId() == org.getId()))
        {
            displayBreadcrumbs(new ArrayList<BreadcrumbDTO>(), thisItem, showParent, showItemLink);
        }
        else
        {
            processor.makeRequest(new ActionRequestImpl<ArrayList<BreadcrumbDTO>>("getBreadcrumbsList", request),
                    new AsyncCallback<ArrayList<BreadcrumbDTO>>()
                    {
                        public void onFailure(final Throwable caught)
                        {
                        }

                        public void onSuccess(final ArrayList<BreadcrumbDTO> breadcrumbs)
                        {
                            // Don't show the parent if your parent is the root org.
                            if (showParent)
                            {
                                breadcrumbs
                                        .add(new BreadcrumbDTO(org.getName(), Page.ORGANIZATIONS, org.getShortName()));
                            }
                            displayBreadcrumbs(breadcrumbs, thisItem, showParent, showItemLink);
                        }
                    });
        }
    }

    /**
     * Put the breadcrumbs on the display.
     * 
     * @param breadcrumbs
     *            the crumbs.
     * @param thisItem
     *            the display name of the end node in the breadcrumb trail.
     * @param showParent
     *            flag to show this item's direct parent in the trail; only orgs do not need to show this.
     * @param showItemLink
     *            flag to show this item in the trail as a link that wipes out all params when clicked.
     */
    private void displayBreadcrumbs(final ArrayList<BreadcrumbDTO> breadcrumbs, final String thisItem,
            final boolean showParent, final boolean showItemLink)
    {
        Label separator;

        this.clear();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("tab", "Stream");

        for (BreadcrumbDTO crumb : breadcrumbs)
        {
            Hyperlink crumbLink = new Hyperlink(crumb.getText(), Session.getInstance().generateUrl(
                    new CreateUrlRequest(crumb.getPage(), crumb.getView(), params)));
            crumbLink.addStyleName("breadcrumb-link");
            this.add(crumbLink);

            separator = new Label(">");
            separator.addStyleName("breadcrumb-separator");
            this.add(separator);
        }

        if (showItemLink)
        {
            Hyperlink crumbLink = new Hyperlink(thisItem, Session.getInstance().generateUrl(
                    new CreateUrlRequest(new HashMap<String, String>(), true)));
            crumbLink.addStyleName("breadcrumb-link");
            crumbLink.addStyleName("breadcrumb-label");
            this.add(crumbLink);
        }
        else
        {
            Label label = new Label();
            label.setText(thisItem);
            label.addStyleName("breadcrumb-label");
            this.add(label);
        }
    }
}
