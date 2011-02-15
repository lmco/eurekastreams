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
import java.util.List;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.action.request.GroupLookupRequest;
import org.eurekastreams.server.action.request.PersonLookupRequest;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MembershipCriteriaAddedEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaRemovedEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaVerificationFailureEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaVerificationNoUsersEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaVerificationSuccessEvent;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * User association form element.
 */
public class UserAssociationFormElementModel
{
    /**
     * The maximum number of results to return from a lookup attempt.
     */
    public static final int MAX_RESULTS = 100;

    /**
     * Action processor use.
     */
    private final ActionProcessor processor;

    /**
     * Membership criteria items.
     */
    private final List<MembershipCriteria> items = new ArrayList<MembershipCriteria>();

    /**
     * The system settings.
     */
    private final SystemSettings settings;

    /**
     * The event bus.
     */
    private final EventBus eventBus;

    /**
     * Constructor.
     *
     * @param inSession
     *            the session.
     * @param inSettings
     *            the the settings.
     *
     */
    public UserAssociationFormElementModel(final Session inSession, final SystemSettings inSettings)
    {
        processor = inSession.getActionProcessor();
        settings = inSettings;
        eventBus = inSession.getEventBus();
    }

    /**
     * Initialize the model with settings.
     */
    public void init()
    {
        for (MembershipCriteria criteria : settings.getMembershipCriteria())
        {
            items.add(criteria);
            eventBus.notifyObservers(new MembershipCriteriaAddedEvent(criteria, false));
        }
    }

    /**
     * Add membership criteria.
     *
     * @param membershipCritera
     *            the criteria.
     * @param isGroup
     *            if the criteria represents a group.
     */
    public void addMembershipCriteria(final String membershipCritera, final boolean isGroup)
    {
        if (!isGroup)
        {
            String action = "personLookupOrg";

            PersonLookupRequest request = new PersonLookupRequest(membershipCritera, new Integer(MAX_RESULTS));
            processor.makeRequest(new ActionRequestImpl<PersonModelView>(action, request),
                    new AsyncCallback<List<PersonModelView>>()
                    {
                        public void onFailure(final Throwable caught)
                        {
                            eventBus.notifyObservers(new MembershipCriteriaVerificationFailureEvent());
                        }

                        public void onSuccess(final List<PersonModelView> people)
                        {
                            eventBus.notifyObservers(new MembershipCriteriaVerificationSuccessEvent(people.size()));

                            if (people.size() > 0)
                            {
                                MembershipCriteria criteria = new MembershipCriteria();
                                criteria.setCriteria(membershipCritera);

                                items.add(criteria);
                                eventBus.notifyObservers(new MembershipCriteriaAddedEvent(criteria, true));
                            }
                            else
                            {
                                eventBus.notifyObservers(new MembershipCriteriaVerificationNoUsersEvent());
                            }
                        }
                    });
        }
        else
        {
            String action = "groupLookup";

            GroupLookupRequest request = new GroupLookupRequest(membershipCritera);
            processor.makeRequest(new ActionRequestImpl<Person>(action, request), new AsyncCallback<Boolean>()
            {
                public void onFailure(final Throwable caught)
                {
                    eventBus.notifyObservers(new MembershipCriteriaVerificationFailureEvent());
                }

                public void onSuccess(final Boolean wasFound)
                {
                    eventBus.notifyObservers(new MembershipCriteriaVerificationSuccessEvent(1));

                    if (wasFound)
                    {
                        MembershipCriteria criteria = new MembershipCriteria();
                        criteria.setCriteria(membershipCritera);

                        items.add(criteria);
                        eventBus.notifyObservers(new MembershipCriteriaAddedEvent(criteria, true));
                    }
                    else
                    {
                        eventBus.notifyObservers(new MembershipCriteriaVerificationNoUsersEvent());
                    }
                }
            });
        }
    }

    /**
     * Get the membership criteria.
     *
     * @return the criteria.
     */
    public Serializable getMembershipCriteria()
    {
        return (Serializable) items;
    }

    /**
     * Remove membership criteria.
     *
     * @param inCriteria
     *            the criteria.
     */
    public void removeMembershipCriteria(final MembershipCriteria inCriteria)
    {
        items.remove(inCriteria);
        eventBus.notifyObservers(new MembershipCriteriaRemovedEvent(inCriteria));
    }
}
