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
/**
 *
 */
package org.eurekastreams.web.client.ui.common.form.elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.SaveSelectedOrgEvent;
import org.eurekastreams.web.client.ui.common.EditPanel;
import org.eurekastreams.web.client.ui.common.EditPanel.Mode;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.orglookup.OrgLookupContent;
import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.OrganizationTreeDTO;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Org lookup form element.
 * 
 */
public class MultiOrgLookupFormElement extends FlowPanel implements FormElement
{
    /**
     * The key to be used when the value is sent to the server.
     */
    private String key = "";

    /**
     * The org lookup content.
     */
    private OrgLookupContent orgLookupContent;

    /**
     * Selected orgs list.
     */
    private FlowPanel selectedOrgs = new FlowPanel();

    /**
     * The list of orgs by shortname.
     */
    private ArrayList<String> orgs = new ArrayList<String>();

    /**
     * Constructor.
     * 
     * @param inTitle
     *            the title.
     * @param subTitle
     *            a sub title.
     * @param inInstructions
     *            the instructions.
     * @param inKey
     *            the key.
     * @param inSelectedLabelText
     *            the text of the label that will be put next to the org once it is selected
     * @param inRequired
     *            if it is required.
     * @param inProcessor
     *            the action processor.
     * @param relatedOrganizations
     *            related orgs.
     */
    public MultiOrgLookupFormElement(final String inTitle, final String subTitle, final String inInstructions,
            final String inKey, final String inSelectedLabelText, final boolean inRequired,
            final ActionProcessor inProcessor, final List<Organization> relatedOrganizations)
    {
        this.addStyleName("org-lookup-form-element");

        key = inKey;

        Label title = new Label(inTitle);
        title.addStyleName("form-label");


        Label subTitleLabel = new Label(subTitle);
        subTitleLabel.addStyleName("form-sub-title");
        Label instructions = new Label(inInstructions);
        instructions.addStyleName("form-instructions");

        this.add(title);
        this.add(subTitleLabel);
        this.add(instructions);
        this.add(selectedOrgs);

        Label select = new Label("Add Organization");
        select.addStyleName("form-button form-lookup-button");
        final Label selectedOrgName = new Label();
        selectedOrgName.addStyleName("selected-org-name");
        final Label selectedLabel = new Label(inSelectedLabelText);
        selectedLabel.addStyleName("org-select-label");
        selectedLabel.setVisible(false);

        select.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                orgLookupContent = new OrgLookupContent(getSaveCommand(selectedOrgName, selectedLabel), inProcessor);
                Dialog newDialog = new Dialog(orgLookupContent);
                newDialog.setBgVisible(true);
                newDialog.center();
            }

        });

        this.add(select);

        if (inRequired)
        {
            Label requiredLabel = new Label("(required)");
            requiredLabel.addStyleName("required-form-label");
            this.add(requiredLabel);
        }

        this.add(selectedOrgName);
        this.add(selectedLabel);

        for (Organization org : relatedOrganizations)
        {
            final OrganizationTreeDTO treeItem = new OrganizationTreeDTO();

            treeItem.setDisplayName(org.getName());
            treeItem.setOrgId(org.getId());
            treeItem.setShortName(org.getShortName());

            addRelatedOrg(treeItem);
        }
    }

    /**
     * Get the save command.
     * 
     * @param selectedOrgName
     *            the selected org name label.
     * @param selectedLabel
     *            the label that goes with the selected org
     * 
     * @return the command.
     */
    private Command getSaveCommand(final Label selectedOrgName, final Label selectedLabel)
    {
        return new Command()
        {
            public void execute()
            {
                addRelatedOrg(orgLookupContent.getOrg());
            }
        };
    }

    /**
     * Add a related org.
     * 
     * @param org
     *            the org.
     */
    private void addRelatedOrg(final OrganizationTreeDTO org)
    {
        if (!orgs.contains(org.getShortName()))
        {
            orgs.add(org.getShortName());

            final FlowPanel orgPanel = new FlowPanel();
            orgPanel.addStyleName("multi-org-form-element-item");

            EditPanel editControls = new EditPanel(orgPanel, Mode.DELETE);
            orgPanel.add(editControls);
            
            orgPanel.add(new Label(org.getDisplayName()));
            
            editControls.addDeleteClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    orgPanel.removeFromParent();
                    orgs.remove(org.getShortName());
                }
            });

            selectedOrgs.add(orgPanel);
            EventBus.getInstance().notifyObservers(new SaveSelectedOrgEvent(org));
        }

    }

    /**
     * Get the key.
     * 
     * @return the key.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Get the value.
     * 
     * @return the value.
     */
    public Serializable getValue()
    {
        return orgs;
    }

    /**
     * Called on error.
     * 
     * @param errMessage
     *            the error.
     */
    public void onError(final String errMessage)
    {
        // Intentionally left blank.

    }

    /**
     * Called on success.
     */
    public void onSuccess()
    {
        // Intentionally left blank.
    }
}
