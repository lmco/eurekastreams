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

import java.io.Serializable;

import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.TermsOfServiceAcceptedEvent;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Model.
 */
public class TermsOfServiceDialogModel
{
    /**
     * The session.
     */
    private Session session;

    /**
     * Event bus.
     */
    private EventBus eventBus;

    /**
     * If the user has agreed.
     */
    private Boolean agree;

    /**
     * Constructor.
     * 
     * @param inSession
     *            the session.
     * @param inEventBus
     *            the event bus.
     */
    public TermsOfServiceDialogModel(final Session inSession, final EventBus inEventBus)
    {
        session = inSession;
        eventBus = inEventBus;
        agree = false;
    }

    /**
     * Accep the terms of service.
     */
    public void acceptTermsOfService()
    {
        session.getActionProcessor().makeRequest(new ActionRequestImpl<Person>("acceptTermsOfService", null),
                new AsyncCallback<Serializable>()
                {
                    public void onFailure(final Throwable caught)
                    {

                    }

                    public void onSuccess(final Serializable result)
                    {
                        eventBus.notifyObservers(new TermsOfServiceAcceptedEvent());
                    }
                });
    }

    /**
     * Set the value of agreement with the ToS.
     * 
     * @param inAgree
     *            the value.
     */
    public void setAgreeValue(final Boolean inAgree)
    {
        agree = inAgree;

    }

    /**
     * Get the value of agreement.
     * 
     * @return the value.
     */
    public Boolean getAgreeValue()
    {
        return agree;
    }
}
