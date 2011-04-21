/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
import org.eurekastreams.web.client.events.OrgSelectedEvent;
import org.eurekastreams.web.client.model.OrganizationModelViewModel;
import org.eurekastreams.web.client.model.OrganizationTreeModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarDisplayPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.common.dialog.BaseDialogContent;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * Org lookup modal.
 */
public class OrgLookupContent extends BaseDialogContent
{
    /**
     * The lookup form.
     */
    private final FormPanel lookupForm = new FormPanel();

    /**
     * The search box.
     */
    private final TextBox searchBox = new TextBox();

    /**
     * The search button.
     */
    private final Label searchButton = new Label();

    /**
     * The save button.
     */
    private Hyperlink save;

    /**
     * The cancel button.
     */
    private Hyperlink cancel;

    /**
     * The avatar.
     */
    private AvatarWidget logoImage;

    /**
     * The selected org title.
     */
    private final Label orgTitle = new Label();

    /** The selected org overview. */
    private final Label orgOverview = new Label();

    /** Org description panel. */
    FlowPanel orgDescriptionPanel;

    /** Command to invoke to save data. */
    private final Command saveCommand;

    /** The MVVM View Model for this control. */
    private final OrgLookupViewModel viewModel;

    /** Org tree widget. */
    private final Tree orgTree = new Tree();

    /** Scrolling panel for the org tree. */
    private ScrollPanel orgTreePanel;

    /** Index to determine the widget for a given org. */
    private final HashMap<OrganizationTreeDTO, TreeItem> orgToNodeIndex = new HashMap<OrganizationTreeDTO, TreeItem>();

    /**
     * Constructor.
     *
     * @param inSaveCommand
     *            called if the Save button gets clicked
     */
    public OrgLookupContent(final Command inSaveCommand)
    {
        saveCommand = inSaveCommand;
        setupWidgets();
        setupEvents();

        viewModel = new OrgLookupViewModel(this, OrganizationTreeModel.getInstance(),
                OrganizationModelViewModel.getInstance(), Session.getInstance().getEventBus());
        viewModel.init();
    }

    /**
     * Builds the UI.
     */
    private void setupWidgets()
    {
        final FlowPanel lookupPanelContainer = new FlowPanel();
        lookupPanelContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().orgLookupContainer());

        final FlowPanel lookupPanel = new FlowPanel();

        Label lookupDesc = new Label("Find an organization by typing the name in the box, "
                + "or by browsing the organization structure.");
        lookupDesc.addStyleName(StaticResourceBundle.INSTANCE.coreCss().lookupDescription());

        lookupPanelContainer.add(lookupDesc);

        lookupPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().lookup());

        FlowPanel searchContainer = new FlowPanel();
        searchContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchList());

        searchButton.setTitle("search organization");
        searchButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchListButton());

        searchContainer.add(searchBox);
        searchContainer.add(searchButton);

        lookupPanel.add(searchContainer);

        final FlowPanel buttonArea = new FlowPanel();
        buttonArea.addStyleName(StaticResourceBundle.INSTANCE.coreCss().buttonArea());

        save = new Hyperlink("Select", History.getToken());
        save.addStyleName(StaticResourceBundle.INSTANCE.coreCss().lookupSelectButtonInactive());
        buttonArea.add(save);

        cancel = new Hyperlink("Cancel", History.getToken());
        cancel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().lookupCancelButton());
        buttonArea.add(cancel);

        orgTree.addStyleName(StaticResourceBundle.INSTANCE.coreCss().orgTree());
        orgTreePanel = new ScrollPanel(orgTree);
        orgTreePanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().orgTreeContainer());
        lookupPanel.add(orgTreePanel);

        lookupPanelContainer.add(lookupPanel);

        orgDescriptionPanel = new FlowPanel();
        orgDescriptionPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().orgDescription());

        logoImage = new AvatarWidget(Size.Small);
        orgDescriptionPanel.add(new AvatarDisplayPanel(logoImage));

        FlowPanel descriptionTextContainer = new FlowPanel();

        descriptionTextContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().orgDescriptionText());

        orgTitle.addStyleName(StaticResourceBundle.INSTANCE.coreCss().orgName());
        descriptionTextContainer.add(orgTitle);

        orgOverview.addStyleName(StaticResourceBundle.INSTANCE.coreCss().orgOverview());
        descriptionTextContainer.add(orgOverview);

        orgDescriptionPanel.add(descriptionTextContainer);
        orgDescriptionPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().displayNone());

        lookupPanelContainer.add(orgDescriptionPanel);

        lookupPanelContainer.add(buttonArea);

        lookupForm.add(lookupPanelContainer);
    }

    /**
     * Wires up events.
     */
    private void setupEvents()
    {
        cancel.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inArg0)
            {
                close();
            }
        });

        orgTree.addSelectionHandler(new SelectionHandler<TreeItem>()
        {
            public void onSelection(final SelectionEvent<TreeItem> ev)
            {
                EventBus.getInstance().notifyObservers(
                        new OrgSelectedEvent((OrganizationTreeDTO) ev.getSelectedItem().getUserObject()));
            }
        });
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
        return StaticResourceBundle.INSTANCE.coreCss().orgLookupDialog();
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
     * Get the selected org shortname.
     *
     * @return the org.
     */
    public OrganizationTreeDTO getOrg()
    {
        return viewModel.getSelectedOrg();
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

    /**
     * Create the tree item for an org recursively.
     *
     * @param org
     *            The org.
     * @return The tree item.
     */
    private TreeItem createOrgTreeItem(final OrganizationTreeDTO org)
    {
        TreeItem item = new TreeItem();
        item.setText(org.getDisplayName());
        item.setUserObject(org);
        orgToNodeIndex.put(org, item);
        for (OrganizationTreeDTO orgChild : org.getChildren())
        {
            item.addItem(createOrgTreeItem(orgChild));
        }
        return item;
    }

    /* ---- package-scope methods for use by the MVVM View Model only ---- */

    /**
     * Populates the control with the org tree.
     *
     * @param rootOrg
     *            Root org.
     */
    void populateOrgTree(final OrganizationTreeDTO rootOrg)
    {
        orgTree.clear();
        orgToNodeIndex.clear();
        orgTree.addItem(createOrgTreeItem(rootOrg));
    }

    /**
     * Selects a given org in the tree and scrolls the display to it.
     *
     * @param org
     *            Org whose widget to scroll to.
     */
    void selectAndScrollToOrg(final OrganizationTreeDTO org)
    {
        TreeItem item = orgToNodeIndex.get(org);
        orgTree.setSelectedItem(item, true);
        orgTree.ensureSelectedItemVisible();
        int pixels = item.getAbsoluteTop() - orgTree.getAbsoluteTop();
        scrollTopNative(orgTreePanel.getElement(), pixels);
    }

    /**
     * @return the searchBox
     */
    TextBox getSearchBox()
    {
        return searchBox;
    }

    /**
     * @return the searchButton
     */
    Label getSearchButton()
    {
        return searchButton;
    }

    /**
     * @return the save
     */
    Hyperlink getSave()
    {
        return save;
    }

    /**
     * @return the logoImage
     */
    AvatarWidget getLogoImage()
    {
        return logoImage;
    }

    /**
     * @return the orgTitle
     */
    Label getOrgTitle()
    {
        return orgTitle;
    }

    /**
     * @return the orgOverview
     */
    Label getOrgOverview()
    {
        return orgOverview;
    }

    /**
     * @return the orgDescriptionPanel
     */
    FlowPanel getOrgDescriptionPanel()
    {
        return orgDescriptionPanel;
    }

    /**
     * @return the saveCommand
     */
    Command getSaveCommand()
    {
        return saveCommand;
    }
}
