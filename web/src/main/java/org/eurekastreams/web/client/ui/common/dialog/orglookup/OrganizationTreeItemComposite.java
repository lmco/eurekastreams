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

import java.util.HashMap;

import org.eurekastreams.server.domain.OrganizationTreeDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.ui.common.ULPanel;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Org tree item.
 */
public class OrganizationTreeItemComposite extends FlowPanel
{
    /**
     * The item.
     */
    private OrganizationTreeItemComposite treeItem;

    /**
     * Org tree.
     */
    private OrganizationTreeDTO orgTree;

    /**
     * Parent.
     */
    private OrganizationTreeItemComposite parent;

    /**
     * Children list.
     */
    private ULPanel children;

    /**
     * Expand button.
     */
    private Label expand;

    /**
     * The event bus.
     */
    private EventBus eventBus;

    /**
     * The controller.
     */
    private OrganizationTreeItemController controller;

    /**
     * Constructor.
     * 
     * @param inOrgTree
     *            the org tree.
     * @param inParent
     *            the parent.
     * @param container
     *            the container.
     * @param treeIndex
     *            the index.
     * @param inEventBus
     *            the event bus.
     */
    public OrganizationTreeItemComposite(final OrganizationTreeDTO inOrgTree,
            final OrganizationTreeItemComposite inParent, final ComplexPanel container,
            final HashMap<OrganizationTreeDTO, OrganizationTreeItemComposite> treeIndex, final EventBus inEventBus)
    {
        treeItem = this;
        orgTree = inOrgTree;
        parent = inParent;
        eventBus = inEventBus;

        controller = new OrganizationTreeItemController(eventBus);

        treeIndex.put(orgTree, this);

        container.add(this);
        container.addStyleName(StaticResourceBundle.INSTANCE.coreCss().orgTree());

        expand = new Label(" ");
        expand.addStyleName(StaticResourceBundle.INSTANCE.coreCss().expandable());
        this.add(expand);

        Label displayName = new Label(orgTree.getDisplayName());
        displayName.addStyleName(StaticResourceBundle.INSTANCE.coreCss().orgName());
        this.add(displayName);

        children = new ULPanel();
        children.setVisible(false);

        if (orgTree.getChildren().size() > 0)
        {
            this.add(children);

            controller.wireUpExpandButton(expand, children);

            for (OrganizationTreeDTO org : orgTree.getChildren())
            {
                new OrganizationTreeItemComposite(org, this, children, treeIndex, eventBus);
            }
        }
        else
        {
            expand.setVisible(false);
        }

        controller.wireUpSelectButton(displayName, treeItem, orgTree, parent);
    }

    /**
     * Show the children.
     * 
     * @param show
     *            the they should be shown.
     */
    public void showChildren(final boolean show)
    {
        controller.showChildren(expand, children, show);
    }

    /**
     * Show all ancestors.
     */
    public void showAncestors()
    {
        controller.showAncestors(parent);
    }

    /**
     * Select this item.
     */
    public void select()
    {
        controller.select(treeItem, orgTree, parent);
    }
}
