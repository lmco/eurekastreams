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

import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.server.domain.OrganizationTreeDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.model.OrganizationModelViewModel;
import org.eurekastreams.web.client.model.OrganizationTreeModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.ULPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarDisplayPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Background;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
public class OrgLookupContent implements DialogContent
{
    /**
     * The close command.
     */
    private WidgetCommand closeCommand = null;

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
     * The list of orgs.
     */
    private final ULPanel orgList = new ULPanel();

    /**
     * The selected org title.
     */
    private final Label orgTitle = new Label();

    /**
     * The selected org overview.
     */
    private final Label orgOverview = new Label();

    /**
     * Org container.
     */
    private final FlowPanel orgTreeContainer = new FlowPanel();
    
    /**
     * Org description panel.
     */
    FlowPanel orgDescriptionPanel;

    /** Command to invoke to save data. */
    private final Command saveCommand;

    /** The MVVM View Model for this control. */
    private final OrgLookupViewModel viewModel;

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

        orgTreeContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().orgTreeContainer());

        orgTreeContainer.add(orgList);
        lookupPanel.add(orgTreeContainer);

        lookupPanelContainer.add(lookupPanel);

        orgDescriptionPanel = new FlowPanel();
        orgDescriptionPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().orgDescription());

        logoImage = new AvatarWidget(Size.Small, Background.Gray);
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

    /* ---- package-scope methods for use by the MVVM View Model only ---- */

    /**
     * Populates the control with the org tree.
     *
     * @param rootOrg
     *            Root org.
     * @param treeIndex
     *            Index of org to widget to fill.
     */
    void populateOrgTree(final OrganizationTreeDTO rootOrg,
            final HashMap<OrganizationTreeDTO, OrganizationTreeItemComposite> treeIndex)
    {
        new OrganizationTreeItemComposite(rootOrg, null, orgList, treeIndex, EventBus.getInstance());
    }

    /**
     * Scrolls the display to the given org in the tree.
     *
     * @param orgWidget
     *            Widget representing the org.
     */
    void scrollToOrgWidget(final Widget orgWidget)
    {
        int pixels = orgWidget.getAbsoluteTop() - orgList.getAbsoluteTop();
        scrollTopNative(orgTreeContainer.getElement(), pixels);
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
