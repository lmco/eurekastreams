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

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.server.domain.OrganizationTreeDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.PropertyMapper;
import org.eurekastreams.web.client.ui.common.ULPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarDisplayPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Background;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Org lookup modal.
 */
public class OrgLookupContent implements DialogContent, Bindable
{
    /**
     * The close command.
     */
    private WidgetCommand closeCommand = null;

    /**
     * The lookup form.
     */
    FormPanel lookupForm;

    /**
     * The view.
     */
    private OrgLookupView view;

    /**
     * The model.
     */
    private OrgLookupModel model;

    /**
     * The search box.
     */
    TextBox searchBox;

    /**
     * The search button.
     */
    Label searchButton;

    /**
     * The save button.
     */
    Hyperlink save;

    /**
     * The cancel button.
     */
    Hyperlink cancel;

    /**
     * The avatar.
     */
    AvatarWidget logoImage;

    /**
     * The list of orgs.
     */
    ULPanel orgList;

    /**
     * The selected org title.
     */
    Label orgTitle;

    /**
     * The selected org overview.
     */
    Label orgOverview;

    /**
     * Org container.
     */
    private FlowPanel orgTreeContainer;
    
    /**
     * Org description panel.
     */
    FlowPanel orgDescriptionPanel;

    /**
     * Constructor.
     *
     * @param saveCommand
     *            called if the Save button gets clicked
     * @param inProcessor
     *            for requesting information from the server
     */
    public OrgLookupContent(final Command saveCommand, final ActionProcessor inProcessor)
    {
        lookupForm = new FormPanel();

        final FlowPanel lookupPanelContainer = new FlowPanel();
        lookupPanelContainer.addStyleName("org-lookup-container");

        final FlowPanel lookupPanel = new FlowPanel();

        Label lookupDesc =
                new Label(
                        "Find an organization by typing the name in the box, "
                        + "or by browsing the organization structure.");
        lookupDesc.addStyleName("lookup-description");

        lookupPanelContainer.add(lookupDesc);

        lookupPanel.addStyleName("lookup");

        searchBox = new TextBox();
        searchButton = new Label();

        FlowPanel searchContainer = new FlowPanel();
        searchContainer.addStyleName("search-list");

        searchButton.setTitle("search organization");
        searchButton.addStyleName("search-list-button");

        searchContainer.add(searchBox);
        searchContainer.add(searchButton);

        lookupPanel.add(searchContainer);

        final FlowPanel buttonArea = new FlowPanel();
        buttonArea.addStyleName("button-area");

        save = new Hyperlink("Select", History.getToken());
        save.addStyleName("lookup-select-button-inactive");
        buttonArea.add(save);

        cancel = new Hyperlink("Cancel", History.getToken());
        cancel.addStyleName("lookup-cancel-button");
        buttonArea.add(cancel);

        orgTreeContainer = new FlowPanel();
        orgTreeContainer.addStyleName("org-tree-container");
        orgList = new ULPanel();

        orgTreeContainer.add(orgList);
        lookupPanel.add(orgTreeContainer);

        lookupPanelContainer.add(lookupPanel);

        orgDescriptionPanel = new FlowPanel();
        orgDescriptionPanel.addStyleName("org-description");

        logoImage = new AvatarWidget(Size.Small, Background.Gray);
        orgDescriptionPanel.add(new AvatarDisplayPanel(logoImage));

        FlowPanel descriptionTextContainer = new FlowPanel();

        descriptionTextContainer.addStyleName("org-description-text");

        orgTitle = new Label();
        orgTitle.addStyleName("org-name");
        descriptionTextContainer.add(orgTitle);

        orgOverview = new Label();
        orgOverview.addStyleName("org-overview");
        descriptionTextContainer.add(orgOverview);

        orgDescriptionPanel.add(descriptionTextContainer);
        orgDescriptionPanel.addStyleName("display-none");

        lookupPanelContainer.add(orgDescriptionPanel);

        lookupPanelContainer.add(buttonArea);

        lookupForm.add(lookupPanelContainer);

        // Create and initialize the controller.

        model = new OrgLookupModel(inProcessor, EventBus.getInstance());
        view = new OrgLookupView(this, model, saveCommand);
        OrgLookupController controller = new OrgLookupController(model, view, EventBus.getInstance());

        PropertyMapper mapper = new PropertyMapper(GWT.create(OrgLookupContent.class), GWT.create(OrgLookupView.class));

        mapper.bind(this, view);

        controller.init();
    }

    /**
     * Close the modal.
     */
    public void close()
    {
        closeCommand.execute();
    }

    /**
     * Get the modal body.
     *
     * @return the body.
     */
    public Widget getBody()
    {
        return lookupForm;
    }

    /**
     * Get the modal CSS name.
     *
     * @return the CSS class.
     */
    public String getCssName()
    {
        return "org-lookup-dialog";
    }

    /**
     * Get the modal title.
     *
     * @return the modal title.
     */
    public String getTitle()
    {
        return "Select Organization";
    }

    /**
     * Injects the close command.
     *
     * @param command
     *            the close command.
     */
    public void setCloseCommand(final WidgetCommand command)
    {
        closeCommand = command;
    }

    /**
     * Called on show.
     */
    public void show()
    {
        // Intentionally left blank.
    }
    
    /**
     * Get the selected org shortname.
     *
     * @return the org.
     */
    public OrganizationTreeDTO getOrg()
    {
        return model.getSelectedOrg();
    }

    /**
     * Get an org tree item.
     *
     * @param results
     *            the org tree.
     * @param parent
     *            the parent.
     * @param inOrgList
     *            the target list.
     * @param treeIndex
     *            the index.
     * @return the tree item.
     */
    public OrganizationTreeItemComposite getOrganizationTreeItem(final OrganizationTreeDTO results,
            final OrganizationTreeItemComposite parent, final ULPanel inOrgList,
            final HashMap<OrganizationTreeDTO, OrganizationTreeItemComposite> treeIndex)
    {
        return new OrganizationTreeItemComposite(results, parent, inOrgList, treeIndex, EventBus.getInstance());
    }

    /**
     * Scroll the tree container element.
     *
     * @param i
     *            number of pixels to scroll.
     */
    public void scrollTop(final int i)
    {
        scrollTopNative(orgTreeContainer.getElement(), i);

    }

    /**
     * Native scroll implementation.
     *
     * @param element
     *            the element to scroll.
     * @param i
     *            the number of pixels to scroll.
     */
    private native void scrollTopNative(final Element element, final int i)/*-{
                    element.scrollTop = i;
                }-*/;
}
