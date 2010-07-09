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

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.OrganizationTreeDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.SaveSelectedOrgEvent;
import org.eurekastreams.web.client.ui.common.EditPanel;
import org.eurekastreams.web.client.ui.common.EditPanel.Mode;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.orglookup.OrgLookupContent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Org lookup form element.
 * 
 */
public class OrgLookupFormElement extends FlowPanel implements FormElement
{
    /**
     * The key to be used when the value is sent to the server.
     */
    private String key = "";

    /**
     * The org shortname.
     */
    private String orgName = "";

    /**
     * The org lookup content.
     */
    private OrgLookupContent orgLookupContent;

    /**
     * Selected org name.
     */
    private Label selectedOrgName;

    /** Title label. */
    private Label title;

    /**
     * Select label.
     */
    private Label select;

    /**
     * Org panel.
     */
    final FlowPanel orgPanel;

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
     * @param inOrg
     *            the organization.
     * @param inReadOnly
     *            if the element is read only.
     */
    public OrgLookupFormElement(final String inTitle, final String subTitle, final String inInstructions,
            final String inKey, final String inSelectedLabelText, final boolean inRequired,
            final ActionProcessor inProcessor, final Organization inOrg, final boolean inReadOnly)
    {
        this.addStyleName("org-lookup-form-element");

        if (inReadOnly)
        {
            this.addStyleName("org-lookup-form-element-readonly");
        }

        key = inKey;

        title = new Label(inTitle);
        title.addStyleName("form-label");

        Label subTitleLabel = new Label(subTitle);
        subTitleLabel.addStyleName("form-sub-title");

        this.add(title);
        this.add(subTitleLabel);
        
        if (inInstructions != null && !inInstructions.isEmpty())
        {
            Label instructions = new Label(inInstructions);
            instructions.addStyleName("form-instructions");
            this.add(instructions);
        }

        if (inRequired)
        {
            Label requiredLabel = new Label("(required)");
            requiredLabel.addStyleName("required-form-label");
            this.add(requiredLabel);
        }

        select = new Label("Lookup");
        select.addStyleName("form-button form-lookup-button");
        selectedOrgName = new Label();
        selectedOrgName.setVisible(false);
        selectedOrgName.addStyleName("selected-org-name");

        ClickHandler lookupHandler = new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (!inReadOnly)
                {
                    orgLookupContent = new OrgLookupContent(getSaveCommand(), inProcessor);
                    Dialog newDialog = new Dialog(orgLookupContent);
                    newDialog.setBgVisible(true);
                    newDialog.center();
                }
            }
        };

        select.addClickHandler(lookupHandler);

        orgPanel = new FlowPanel();
        
        if (!inReadOnly)
        {
            final EditPanel editControls = new EditPanel(orgPanel, Mode.DELETE);
            orgPanel.add(editControls);
            editControls.addDeleteClickHandler(lookupHandler);
        }
        
        orgPanel.add(selectedOrgName);


        this.add(orgPanel);
        this.add(select);

        if (null != inOrg)
        {
            OrganizationTreeDTO orgTree = new OrganizationTreeDTO();
            orgTree.setDisplayName(inOrg.getName());
            orgTree.setOrgId(inOrg.getId());
            orgTree.setShortName(inOrg.getShortName());

            setOrg(orgTree);
        }
    }

    /**
     * Get the save command.
     * 
     * @return the command.
     */
    private Command getSaveCommand()
    {
        return new Command()
        {
            public void execute()
            {
                setOrg(orgLookupContent.getOrg());
            }
        };
    }

    /**
     * Set the org.
     * 
     * @param org
     *            the org.
     */
    private void setOrg(final OrganizationTreeDTO org)
    {
        select.setVisible(false);
        selectedOrgName.setText(org.getDisplayName());
        selectedOrgName.setVisible(true);

        orgName = org.getShortName();

        EventBus.getInstance().notifyObservers(new SaveSelectedOrgEvent(org));
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
        return orgName;
    }

    /**
     * Called on error.
     * 
     * @param errMessage
     *            the error.
     */
    public void onError(final String errMessage)
    {
        title.addStyleName("form-error");
    }

    /**
     * Called on success.
     */
    public void onSuccess()
    {
        title.removeStyleName("form-error");
    }

    /**
     * @return the orgShortName
     */
    public String getOrgShortName()
    {
        return orgLookupContent.getOrg().getShortName();
    }
}
