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

import java.io.Serializable;

import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.PropertyMapper;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.form.elements.FormElement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * User association form element.
 */
public class UserAssociationFormElement extends FlowPanel implements FormElement, Bindable
{
    /**
     * The controller.
     */
    private UserAssociationFormElementController controller;

    // Package level widgets.
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
     * The model.
     */
    private UserAssociationFormElementModel model;

    /**
     * Constructor.
     *
     * @param inSettings
     *            the system settings.
     *
     */
    public UserAssociationFormElement(final SystemSettings inSettings)
    {
        this.addStyleName("form-user-association");

        Label label = new Label("Access List");
        label.addStyleName("form-label");

        Label topDesc = new Label("Specify the users that you want to have access to Eureka.");
        topDesc.addStyleName("form-instructions top-label");

        requiredLabel = new Label();
        requiredLabel.addStyleName("required-form-label");
        requiredLabel.setText("(required)");

        FlowPanel radioButtons = new FlowPanel();
        radioButtons.addStyleName("radio-button-container");

        Label searchLDAPBy = new Label("Search by LDAP");
        searchLDAPBy.addStyleName("form-label search-ldap-label");

        group = new RadioButton("ldap", "Group Name");
        attr = new RadioButton("ldap", "Attribute");
        group.setValue(true);
        group.addStyleName("group-check-box");

        radioButtons.add(searchLDAPBy);
        radioButtons.add(group);
        radioButtons.add(attr);

        membershipCriteria = new TextBox();


        // Need to do this to fix an especially nasty IE CSS bug (input margin inheritance)
        final SimplePanel textWrapper = new SimplePanel();
        textWrapper.addStyleName("input-wrapper");
        textWrapper.add(membershipCriteria);

        verifyButton = new Anchor("");
        verifyButton.addStyleName("add-button-submit");
        verifyButton.addStyleName("form-button");

        verifying = new Label("");
        verifying.addStyleName("form-verifying-spinny");
        verifying.setVisible(false);

        results = new Label();

        description = new Label();
        description.addStyleName("form-instructions");

        this.add(results);
        this.add(label);
        this.add(topDesc);
        this.add(requiredLabel);
        this.add(radioButtons);
        this.add(textWrapper);
        this.add(verifyButton);
        this.add(verifying);
        this.add(description);

        accessGroupsPanel = new FlowPanel();
        accessGroupsPanel.addStyleName("access-groups");
        this.add(accessGroupsPanel);

        model = new UserAssociationFormElementModel(Session.getInstance(), inSettings);

        UserAssociationFormElementView view = new UserAssociationFormElementView(model, new WidgetJSNIFacadeImpl());

        PropertyMapper mapper = new PropertyMapper(GWT.create(UserAssociationFormElement.class), GWT
                .create(UserAssociationFormElementView.class));

        mapper.bind(this, view);

        controller = new UserAssociationFormElementController(Session.getInstance(), view, model);

        controller.init();
    }

    /**
     * @return the form element key.
     */
    public String getKey()
    {
        return "ldapGroups";
    }

    /**
     * @return the value of the form element.
     */
    public Serializable getValue()
    {
        return model.getMembershipCriteria();
    }

    /**
     * Handles an error.
     *
     * @param errMessage
     *            the error message.
     */
    public void onError(final String errMessage)
    {
        // TODO Auto-generated method stub

    }

    /**
     * Handles success.
     */
    public void onSuccess()
    {
        // TODO Auto-generated method stub

    }

}
