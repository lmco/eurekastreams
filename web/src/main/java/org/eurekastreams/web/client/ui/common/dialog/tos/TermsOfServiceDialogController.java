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

import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.TermsOfServiceAcceptedEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

/**
 * The controller.
 */
public class TermsOfServiceDialogController
{
    /**
     * Constructor.
     * 
     * @param view
     *            the view.
     * @param model
     *            the model.
     * @param eventBus
     *            the event bus.
     */
    public TermsOfServiceDialogController(final TermsOfServiceDialogContent view,
            final TermsOfServiceDialogModel model, final EventBus eventBus)
    {
        eventBus.addObserver(TermsOfServiceAcceptedEvent.class, new Observer<TermsOfServiceAcceptedEvent>()
        {
            public void update(final TermsOfServiceAcceptedEvent event)
            {
                view.close();
            }
        });

        view.addAgreeClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (model.getAgreeValue())
                {
                    model.acceptTermsOfService();
                }
            }
        });

        view.addConfirmCheckBoxValueHandler(new ValueChangeHandler<Boolean>()
        {
            public void onValueChange(final ValueChangeEvent<Boolean> event)
            {
                model.setAgreeValue(event.getValue());
                view.setAcceptEnabled(event.getValue());
            }
        });

    }
}
