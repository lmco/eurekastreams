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
package org.eurekastreams.web.client.ui.common.dialog.tos;

import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.server.domain.TermsOfServiceDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * The terms of service dialog.
 */
public class TermsOfServiceDialogContent implements DialogContent
{
    /**
     * Terms of service.
     */
    TermsOfServiceDTO tos;

    /**
     * Body of the modal.
     */
    FlowPanel body = new FlowPanel();

    /**
     * The close command.
     */
    private WidgetCommand closeCommand = null;

    /**
     * Accept button.
     */
    private Hyperlink acceptTos;

    /**
     * Cancel button.
     */
    private Anchor cancelTos;

    /**
     * Confirm checkbox.
     */
    private CheckBox confirmCheckBox;

    /**
     * Constructor.
     * 
     * @param inTos
     *            the terms of service.
     * @param readOnly
     *            if the TOS modal is read only.
     */
    public TermsOfServiceDialogContent(final TermsOfServiceDTO inTos, final boolean readOnly)
    {
        tos = inTos;

        HTML tosText = new HTML(tos.getTermsOfService());
        tosText.addStyleName(StaticResourceBundle.INSTANCE.coreCss().tosBody());

        Label tosExplanation = new Label("Please confirm below you have read and understand the terms of service.");
        tosExplanation.addStyleName(StaticResourceBundle.INSTANCE.coreCss().explanation());
        Label tosHeader = new Label("Terms of Service");
        tosHeader.addStyleName(StaticResourceBundle.INSTANCE.coreCss().header());

        body.add(tosExplanation);
        body.add(tosHeader);
        body.add(tosText);

        acceptTos = new Hyperlink("Accept", History.getToken());
        acceptTos.addStyleName(StaticResourceBundle.INSTANCE.coreCss().agreeButton());
        acceptTos.addStyleName(StaticResourceBundle.INSTANCE.coreCss().agreeButtonDisabled());
        cancelTos = new Anchor("Cancel", "http://www.eurekastreams.org");
        cancelTos.addStyleName(StaticResourceBundle.INSTANCE.coreCss().disagreeButton());

        confirmCheckBox = new CheckBox("I have read and understand the terms of service.");
        confirmCheckBox.addStyleName(StaticResourceBundle.INSTANCE.coreCss().confirmCheckbox());

        if (!readOnly)
        {
            body.add(confirmCheckBox);
            body.add(acceptTos);
            body.add(cancelTos);
        }
        else
        {
            body.addStyleName(StaticResourceBundle.INSTANCE.coreCss().readOnlyTos());
        }

        final TermsOfServiceDialogModel model = new TermsOfServiceDialogModel(Session.getInstance(), EventBus
                .getInstance());
        new TermsOfServiceDialogController(this, model, EventBus.getInstance());
    }

    /**
     * Add a handler to the agree button.
     * 
     * @param handler
     *            the handler.
     */
    public void addAgreeClickHandler(final ClickHandler handler)
    {
        acceptTos.addClickHandler(handler);
    }

    /**
     * Add a value change handler to the checkbox.
     * 
     * @param handler
     *            the handler.
     */
    public void addConfirmCheckBoxValueHandler(final ValueChangeHandler<Boolean> handler)
    {
        confirmCheckBox.addValueChangeHandler(handler);
    }

    /**
     * Call the close command.
     */
    public void close()
    {
        closeCommand.execute();
    }

    /**
     * Get the body.
     * 
     * @return the body.s
     */
    public Widget getBody()
    {
        return body;
    }

    /**
     * Get the CSS class for the modal.
     * 
     * @return the CSS class.
     */
    public String getCssName()
    {
        return "terms-of-service-modal";
    }

    /**
     * Get the title.
     * 
     * @return the title.
     */
    public String getTitle()
    {
        return "Terms of Service";
    }

    /**
     * The command to call to close the dialog.
     * 
     * @param command
     *            the close command.
     */
    public void setCloseCommand(final WidgetCommand command)
    {
        closeCommand = command;
    }

    /**
     * Called when the modal is shown.
     */
    public void show()
    {
        // Intentionally left blank.
    }

    /**
     * Respond to setting the accept checkbox value.
     * 
     * @param agree
     *            the value.
     */
    public void setAcceptEnabled(final Boolean agree)
    {
        if (agree)
        {
            acceptTos.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().agreeButtonDisabled());
        }
        else
        {
            acceptTos.addStyleName(StaticResourceBundle.INSTANCE.coreCss().agreeButtonDisabled());
        }

    }
}
