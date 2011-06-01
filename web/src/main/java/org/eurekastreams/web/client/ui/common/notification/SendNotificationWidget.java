/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.notification;

import org.eurekastreams.server.action.request.notification.SendPrebuiltNotificationRequest;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.data.SendNotificationFailureEvent;
import org.eurekastreams.web.client.events.data.SendNotificationSuccessEvent;
import org.eurekastreams.web.client.model.SendNotificationModel;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextAreaFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextBoxFormElement;
import org.eurekastreams.web.client.ui.pages.master.CoreCss;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget to send a notification.
 */
public class SendNotificationWidget extends Composite
{
    /** Max length. */
    private static final int MAX_MESSAGE_LENGTH = 250;

    /** Max length. */
    private static final int MAX_URL_LENGTH = 2048;

    /** Binder for building UI. */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    /** High-priority checkbox. */
    @UiField
    CheckBox highPriorityUi;

    /** Message text box. */
    @UiField(provided = true)
    BasicTextAreaFormElement messageUi;

    /** URL text box. */
    @UiField(provided = true)
    BasicTextBoxFormElement urlUi;

    /** Global CSS. */
    @UiField(provided = true)
    CoreCss coreCss;

    /** Global resources. */
    @UiField(provided = true)
    StaticResourceBundle globalResources;

    /** Send notification button. */
    @UiField
    Image sendButton;

    /** Disabled placeholder for send notification button. */
    @UiField
    Image sendButtonDisabled;

    /** Wait spinner placeholder for send notification button. */
    @UiField
    Image waitSpinner;

    /** Current state of whether the form can be submitted. */
    private boolean allowSubmit = true;

    /**
     * Constructor.
     */
    public SendNotificationWidget()
    {
        coreCss = StaticResourceBundle.INSTANCE.coreCss();
        globalResources = StaticResourceBundle.INSTANCE;
        urlUi = new BasicTextBoxFormElement("http://", MAX_URL_LENGTH, false, "Notification Destination", null, "",
                null, false);
        messageUi = new BasicTextAreaFormElement(MAX_MESSAGE_LENGTH, null, null, "", null, true)
        {
            @Override
            protected void onTextChanges()
            {
                super.onTextChanges();
                checkIfSubmitAllowed();
            }
        };

        initWidget(binder.createAndBindUi(this));
        checkIfSubmitAllowed();

        final EventBus eventBus = EventBus.getInstance();
        eventBus.addObserver(SendNotificationSuccessEvent.class, new Observer<SendNotificationSuccessEvent>()
        {
            public void update(final SendNotificationSuccessEvent inArg1)
            {
                eventBus.notifyObservers(ShowNotificationEvent.getInstance("Notification successfully sent"));

                messageUi.clear();
                urlUi.setValue("");

                waitSpinner.setVisible(false);
                sendButtonDisabled.setVisible(true);
            }
        });
        eventBus.addObserver(SendNotificationFailureEvent.class, new Observer<SendNotificationFailureEvent>()
        {
            public void update(final SendNotificationFailureEvent inArg1)
            {
                eventBus.notifyObservers(ShowNotificationEvent.getInstance("Error sending notification"));

                waitSpinner.setVisible(false);
                sendButton.setVisible(true);
            }
        });
    }

    /**
     * Determines if the form can be submitted.
     */
    private void checkIfSubmitAllowed()
    {
        boolean newAllowSubmit = !messageUi.isOverLimit() && !messageUi.isEmpty();
        if (allowSubmit != newAllowSubmit)
        {
            allowSubmit = newAllowSubmit;
            sendButton.setVisible(allowSubmit);
            sendButtonDisabled.setVisible(!allowSubmit);
        }
    }

    /**
     * Sends notification when button clicked.
     *
     * @param ev
     *            Event.
     */
    @UiHandler("sendButton")
    void onSendButtonClick(final ClickEvent ev)
    {
        if (allowSubmit)
        {
            final String url = (String) urlUi.getValue();
            if (url != null && !url.isEmpty() && !url.startsWith("http://") && !url.startsWith("https://"))
            {
                EventBus.getInstance().notifyObservers(
                        ShowNotificationEvent.getInstance("Notification destination must be an http or https URL"));
                return;
            }

            SendPrebuiltNotificationRequest rqst = new SendPrebuiltNotificationRequest();
            rqst.setHighPriority(highPriorityUi.getValue());
            rqst.setMessage(messageUi.getValue());

            rqst.setUrl(url);

            sendButton.setVisible(false);
            waitSpinner.setVisible(true);

            SendNotificationModel.getInstance().insert(rqst);
        }
    }

    /**
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, SendNotificationWidget>
    {
    }
}
