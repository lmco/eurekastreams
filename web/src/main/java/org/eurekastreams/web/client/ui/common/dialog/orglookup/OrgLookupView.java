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

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.OrganizationTreeDTO;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.common.ULPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Org lookup modal view.
 */
public class OrgLookupView implements Bindable
{
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
     * The selected org title.
     */
    Label orgTitle;

    /**
     * The selected org overview.
     */
    Label orgOverview;

    /**
     * The org children list.
     */
    ULPanel orgList;
    
    /**
     * The org description panel.
     */
    FlowPanel orgDescriptionPanel;

    /**
     * The save command.
     */
    private Command saveCommand;

    /**
     * The index matching the DTOs to the corresponding widget.
     */
    private HashMap<OrganizationTreeDTO, OrganizationTreeItemComposite> treeIndex =
        new HashMap<OrganizationTreeDTO, OrganizationTreeItemComposite>();

    /**
     * The composite.
     */
    private OrgLookupContent composite;

    /**
     * Add a search button click handler.
     *
     * @param command
     *            the click handler.
     */
    public void addSearchCommand(final Command command)
    {
        searchButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                command.execute();
            }
        });

        searchBox.addKeyPressHandler(new KeyPressHandler()
        {

            public void onKeyPress(final KeyPressEvent event)
            {
                if (event.getCharCode() == KeyCodes.KEY_ENTER)
                {
                    command.execute();
                }

            }
        });
    }

    /**
     * Constructor.
     *
     * @param inComposite
     *            parent composite.
     * @param inModel
     *            the model.
     * @param inSaveCommand
     *            the save command.
     */
    public OrgLookupView(final OrgLookupContent inComposite, final OrgLookupModel inModel, final Command inSaveCommand)
    {
        composite = inComposite;
        model = inModel;
        saveCommand = inSaveCommand;
    }

    /**
     * Wire up the save button.
     */
    public void wireUpSaveButton()
    {
        save.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                saveCommand.execute();
                composite.close();
            }
        });
    }

    /**
     * Wire up the cancel button.
     */
    public void wireUpCancelButton()
    {
        cancel.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                composite.close();
            }
        });
    }

    /**
     * Called when the org tree is found.
     *
     * @param results
     *            the resulting tree.
     */
    public void onOrgChildrenFound(final OrganizationTreeDTO results)
    {
        composite.getOrganizationTreeItem(results, null, orgList, treeIndex);
    }

    /**
     * Called when information about a selected org is found.
     *
     * @param org
     *            the org model.
     */
    public void onOrgInformationFound(final OrganizationModelView org)
    {
        orgDescriptionPanel.removeStyleName("display-none");
        save.removeStyleName("lookup-select-button-inactive");
        save.addStyleName("lookup-select-button-active");

        orgTitle.setText(org.getName());

        if (null != org.getOverview())
        {
            orgOverview.setText(org.getOverview());
        }
        else
        {
            orgOverview.setText("");
        }

        logoImage.setAvatar(org.getEntityId(), org.getAvatarId(), EntityType.ORGANIZATION);

    }

    /**
     * @return the search text.
     */
    public String getSearchText()
    {
        return searchBox.getText();
    }
    
    /**
     * Called when a search is executed.
     *
     * @param found
     *            the found DTO.
     */
    public void onOrgSearch(final OrganizationTreeDTO found)
    {
        treeIndex.get(found).select();

        composite.scrollTop(treeIndex.get(found).getAbsoluteTop() - orgList.getAbsoluteTop());
    }
}
