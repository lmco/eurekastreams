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
package org.eurekastreams.web.client.ui.common.form.elements.userassociation;

import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacade;
import org.eurekastreams.web.client.ui.Bindable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;

/**
 * User association form element.
 */
public class UserAssociationFormElementView implements Bindable
{
    /**
     * Group description.
     */
    private static final String GROUP_DESC = "Enter the name of the LDAP group.";

    /**
     * Attribute description.
     */
    private static final String ATTR_DESC = "Specify an LDAP attribute (in the format: attribute=value)";
    /**
     * The access groups panel.
     */
    FlowPanel accessGroupsPanel;

    /**
     * Textbox containing the LDAP group.
     */
    TextBox membershipCriteria;

    /**
     * Radio button to search LDAP by group name.
     */
    RadioButton group;

    /**
     * Verifying label.
     */
    Label verifying;
    
    /**
     * Verify button.
     */
    Anchor verifyButton;

    /**
     * Required label.
     */
    Label requiredLabel;

    /**
     * Results label.
     */
    Label results;

    /**
     * Description.
     */
    Label description;

    /**
     * Radio button to search LDAP by attribute.
     */
    RadioButton attr;

    /**
     * The JSNI Facade.
     */
    WidgetJSNIFacade jSNIFacade;

    /**
     * The model.
     */
    private UserAssociationFormElementModel model;

    /**
     * Constructor.
     * 
     * @param inModel
     *            the model.
     * @param inJSNIFacade
     *            The jsniFacade to make untestable common calls to.
     */
    public UserAssociationFormElementView(final UserAssociationFormElementModel inModel,
            final WidgetJSNIFacade inJSNIFacade)
    {
        model = inModel;
        jSNIFacade = inJSNIFacade;
    }

    /**
     * Add verify command.
     * 
     * @param verifyCommand
     *            the command.
     */
    public void addVerifyCommand(final Command verifyCommand)
    {
        verifyButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                verifyCommand.execute();
            }
        });

        membershipCriteria.addKeyPressHandler(new KeyPressHandler()
        {

            public void onKeyPress(final KeyPressEvent event)
            {
                if (event.getCharCode() == KeyCodes.KEY_ENTER)
                {
                    verifyCommand.execute();
                }
            }
        });
    }

    /**
     * Adds a click handler to the group radio button.
     * 
     * @param handler
     *            the click handler.
     */
    public void addGroupClickHandler(final ClickHandler handler)
    {
        group.addClickHandler(handler);
    }

    /**
     * Adds a click handler to the attribute radio button.
     * 
     * @param handler
     *            the click handler.
     */
    public void addAttrClickHandler(final ClickHandler handler)
    {
        attr.addClickHandler(handler);
    }

    /**
     * Updates the UI while verification of a membership criteria is being processed.
     */
    public void onVerifyClicked()
    {
        verifying.setVisible(true);
        verifyButton.setVisible(false);
        requiredLabel.setVisible(false);
        results.setVisible(false);
    }

    /**
     * Updates the UI when verification fails.
     */
    public void onVerifyFailure()
    {
        results.setVisible(true);
        results.setText("There was an error processing your request. Please check your query and search again.");
        verifying.setVisible(false);
        verifyButton.setVisible(true);
        requiredLabel.setVisible(true);
    }

    /**
     * Updated the UI when attribute search is selected.
     */
    public void onAttributeSearchSelected()
    {
        description.setText(ATTR_DESC);
    }

    /**
     * Updated the UI when group search is selected.
     */
    public void onGroupSearchSelected()
    {
        description.setText(GROUP_DESC);
    }

    /**
     * Add membership criteria.
     * 
     * @param criteria
     *            the membership criteria.
     */
    public void addMembershipCriteria(final MembershipCriteria criteria)
    {
        final MembershipCriteriaItemComposite membershipCriteriaComposite = getItem(criteria);
        accessGroupsPanel.add(membershipCriteriaComposite);

        membershipCriteriaComposite.addDeleteClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (jSNIFacade.confirm("Are you sure you want to delete '" + criteria.getCriteria() + "'"))
                {
                    accessGroupsPanel.remove(membershipCriteriaComposite);
                    model.removeMembershipCriteria(criteria);
                }
            }
        });
    }

    /**
     * Used for testing.
     * 
     * @param inCriteria
     *            the membership criteria.
     * @return the item.
     */
    protected MembershipCriteriaItemComposite getItem(final MembershipCriteria inCriteria)
    {
        return new MembershipCriteriaItemComposite(inCriteria.getCriteria());
    }

    /**
     * Get the membership criteria.
     * 
     * @return the criteria.
     */
    public String getMembershipCriteria()
    {
        return membershipCriteria.getText();
    }

    /**
     * Updates for successfuly verification.
     * 
     * @param numberOfResults
     *            the number of results.
     */
    public void onVerifySuccess(final int numberOfResults)
    {
        verifying.setVisible(false);
        verifyButton.setVisible(true);
        requiredLabel.setVisible(true);
        results.setVisible(false);
        membershipCriteria.setText("");
    }

    /**
     * Updates for case where criteria returned no users.
     */
    public void onVerifyNoUsers()
    {
        results.setVisible(true);
        results.addStyleName("form-error-box");
        results.setText("No matching groups or users were found. Please check your query and search again.");
        verifying.setVisible(false);
        verifyButton.setVisible(true);
        requiredLabel.setVisible(true);
    }

    /**
     * @return true is the group checkbox is selected.
     */
    public boolean isGroupSelected()
    {
        return group.getValue();
    }

}
