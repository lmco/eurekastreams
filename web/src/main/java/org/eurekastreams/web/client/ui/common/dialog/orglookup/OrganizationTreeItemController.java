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
import org.eurekastreams.web.client.events.OrgSelectedEvent;
import org.eurekastreams.web.client.ui.common.ULPanel;
import org.eurekastreams.server.domain.OrganizationTreeDTO;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;

/**
 * Org tree item.
 */
public class OrganizationTreeItemController
{
    /**
     * Selected item. Package scope for testing.
     */
    static OrganizationTreeItemComposite selectedTreeItem = null;

    /**
     * The event bus.
     */
    private EventBus eventBus;

    /**
     * Constructor.
     * 
     * @param inEventBus
     *            the event bus.
     */
    public OrganizationTreeItemController(final EventBus inEventBus)
    {
        eventBus = inEventBus;
    }

    /**
     * Wire up hte expand button.
     * 
     * @param expand
     *            the button.
     * @param children
     *            the children.
     */
    public void wireUpExpandButton(final Label expand, final ULPanel children)
    {
        expand.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                // Toggle
                showChildren(expand, children, !children.isVisible());
            }
        });
    }

    /**
     * Wire up the select button.
     * 
     * @param displayName
     *            the display name.
     * @param treeItem
     *            the tree item.
     * @param orgTree
     *            the tree.
     * @param parent
     *            the parent.
     */
    public void wireUpSelectButton(final Label displayName, final OrganizationTreeItemComposite treeItem,
            final OrganizationTreeDTO orgTree, final OrganizationTreeItemComposite parent)
    {
        displayName.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                select(treeItem, orgTree, parent);
            }
        });
    }

    /**
     * Show the direct children.
     * 
     * @param children
     *            the children.
     * @param expand
     *            the expand button.
     * 
     * @param show
     *            show/hide.
     */
    public void showChildren(final Label expand, final ULPanel children, final boolean show)
    {
        children.setVisible(show);

        if (show)
        {
            expand.addStyleName("expanded");
        }
        else
        {
            expand.removeStyleName("expanded");
        }

    }

    /**
     * Select this item.
     * 
     * @param treeItem
     *            the tree item.
     * @param orgTree
     *            the org tree.
     * @param parent
     *            the parent.
     */
    public void select(final OrganizationTreeItemComposite treeItem, final OrganizationTreeDTO orgTree,
            final OrganizationTreeItemComposite parent)
    {
        if (null != selectedTreeItem)
        {
            selectedTreeItem.removeStyleName("selected");
        }

        selectedTreeItem = treeItem;
        selectedTreeItem.addStyleName("selected");

        showAncestors(parent);

        eventBus.notifyObservers(new OrgSelectedEvent(orgTree));
    }

    /**
     * Show all the ancestors of this item.
     * 
     * @param parent
     *            the parent.
     */
    public void showAncestors(final OrganizationTreeItemComposite parent)
    {
        if (null != parent)
        {
            parent.showChildren(true);
            parent.showAncestors();
        }
    }

}
