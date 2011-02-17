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
package org.eurekastreams.web.client.ui.common.form.elements.userassociation;

import java.io.Serializable;
import java.util.ArrayList;

import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MembershipCriteriaAddedEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaRemovedEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaVerificationFailureEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaVerificationNoUsersEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacade;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.MembershipCriteriaVerificationModel;
import org.eurekastreams.web.client.model.requests.MembershipCriteriaVerificationRequest;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.form.elements.FormElement;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
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
    /** Group description. */
    private static final String GROUP_DESC = "Enter the name of the LDAP group.";

    /** Attribute description. */
    private static final String ATTR_DESC = "Specify an LDAP attribute (in the format: attribute=value)";

    /** Message for verification with no users. */
    private static final String VERIFY_NO_USERS_MESSAGE = //\n
        "No matching groups or users were found. Please check your query and search again.";

    /** Message for verification failures. */
    private static final String VERIFY_FAILURE_MESSAGE = //\n
    "There was an error processing your request. Please check your query and search again.";

    /** The access groups panel. */
    private FlowPanel accessGroupsPanel;

    /** Textbox containing the LDAP group. */
    private TextBox membershipCriteria;

    /** Radio button to search LDAP by group name. */
    private RadioButton group;

    /** Verifying label. */
    private Label verifying;

    /** Verify button. */
    private Anchor verifyButton;

    /** Required label. */
    private Label requiredLabel;

    /** Results label. */
    private Label results;

    /** Description. */
    private Label description;

    /** Radio button to search LDAP by attribute. */
    private RadioButton attr;

    /** The JSNI Facade. */
    private final WidgetJSNIFacade jSNIFacade = new WidgetJSNIFacadeImpl();

    /** Membership criteria items. */
    private final ArrayList<MembershipCriteria> items = new ArrayList<MembershipCriteria>();


    /**
     * Constructor.
     *
     * @param inSettings
     *            the system settings.
     */
    public UserAssociationFormElement(final SystemSettings inSettings)
    {
        setupWidgets();
        setupEvents();

        for (MembershipCriteria criterion : inSettings.getMembershipCriteria())
        {
            addMembershipCriteria(criterion);
        }
    }

    /**
     * Builds the UI.
     */
    private void setupWidgets()
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

        description = new Label(GROUP_DESC);
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
    }

    /**
     * Wires up events.
     */
    private void setupEvents()
    {
        EventBus eventBus = Session.getInstance().getEventBus();

        // update the UI when group / attribute search is selected.
        group.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                description.setText(GROUP_DESC);
            }
        });
        attr.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                description.setText(ATTR_DESC);
            }
        });

        // make verification request to the server when user clicks verify button / presses enter
        verifyButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inArg0)
            {
                initiateVerification();
            }
        });
        membershipCriteria.addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent ev)
            {
                if (ev.getNativeKeyCode() == KeyCodes.KEY_ENTER)
                {
                    initiateVerification();
                }
            }
        });

        // got verification result: criterion matched some users
        eventBus.addObserver(MembershipCriteriaAddedEvent.class, new Observer<MembershipCriteriaAddedEvent>()
        {
            public void update(final MembershipCriteriaAddedEvent ev)
            {
                membershipCriteria.setText("");
                results.setVisible(false);
                verifyingDone();

                addMembershipCriteria(ev.getMembershipCriteria());
            }
        });

        // got verification result: criterion matched no users
        eventBus.addObserver(MembershipCriteriaVerificationNoUsersEvent.class,
                new Observer<MembershipCriteriaVerificationNoUsersEvent>()
                {
                    public void update(final MembershipCriteriaVerificationNoUsersEvent event)
                    {
                        results.setVisible(true);
                        results.addStyleName("form-error-box");
                        results.setText(VERIFY_NO_USERS_MESSAGE);
                        verifyingDone();
                    }
                });

        // got verification result: query failed
        eventBus.addObserver(MembershipCriteriaVerificationFailureEvent.class,
                new Observer<MembershipCriteriaVerificationFailureEvent>()
                {
                    public void update(final MembershipCriteriaVerificationFailureEvent event)
                    {
                        results.setVisible(true);
                        results.setText(VERIFY_FAILURE_MESSAGE);
                        verifyingDone();
                    }
                });
    }

    /**
     * Do the verification.
     */
    private void initiateVerification()
    {
        verifying.setVisible(true);
        verifyButton.setVisible(false);
        requiredLabel.setVisible(false);
        results.setVisible(false);

        MembershipCriteriaVerificationModel.getInstance().fetch(
                new MembershipCriteriaVerificationRequest(membershipCriteria.getText(), group.getValue()), false);
    }

    /**
     * Actions taken when a verification finishes.
     */
    private void verifyingDone()
    {
        verifying.setVisible(false);
        verifyButton.setVisible(true);
        requiredLabel.setVisible(true);
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
        return items;
    }

    /**
     * Handles an error.
     *
     * @param errMessage
     *            the error message.
     */
    public void onError(final String errMessage)
    {
    }

    /**
     * Handles success.
     */
    public void onSuccess()
    {
    }

    /**
     * Add membership criteria.
     *
     * @param criterion
     *            the membership criteria.
     */
    public void addMembershipCriteria(final MembershipCriteria criterion)
    {
        items.add(criterion);

        final MembershipCriteriaItemComposite membershipCriteriaComposite = new MembershipCriteriaItemComposite(
                criterion.getCriteria());
        accessGroupsPanel.add(membershipCriteriaComposite);

        membershipCriteriaComposite.addDeleteClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (jSNIFacade.confirm("Are you sure you want to delete '" + criterion.getCriteria() + "'"))
                {
                    accessGroupsPanel.remove(membershipCriteriaComposite);
                    Session.getInstance().getEventBus().notifyObservers(new MembershipCriteriaRemovedEvent(criterion));
                    items.remove(criterion);
                }
            }
        });
    }
}
