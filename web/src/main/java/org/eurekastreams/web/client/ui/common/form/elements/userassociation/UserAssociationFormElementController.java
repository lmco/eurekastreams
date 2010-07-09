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

import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MembershipCriteriaAddedEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaVerificationFailureEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaVerificationNoUsersEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaVerificationSuccessEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;

/**
 * User association form element.
 */
public class UserAssociationFormElementController
{
    /**
     * The view.
     */
    private UserAssociationFormElementView view;

    /**
     * The model.
     */
    private UserAssociationFormElementModel model;

    /**
     * The event bus.
     */
    private EventBus eventBus;

    /**
     * Constructor.
     * 
     * @param inSession
     *            the sessoin
     * @param inView
     *            the view.
     * @param inModel
     *            the model.
     */
    public UserAssociationFormElementController(final Session inSession, final UserAssociationFormElementView inView,
            final UserAssociationFormElementModel inModel)
    {
        view = inView;
        model = inModel;
        eventBus = inSession.getEventBus();
    }

    /**
     * Initialize the controller.
     */
    public void init()
    {
        view.onGroupSearchSelected();

        view.addGroupClickHandler(new ClickHandler()
        {

            public void onClick(final ClickEvent event)
            {
                view.onGroupSearchSelected();
            }
        });

        view.addAttrClickHandler(new ClickHandler()
        {

            public void onClick(final ClickEvent event)
            {
                view.onAttributeSearchSelected();
            }
        });

        view.addVerifyCommand(new Command()
        {
            public void execute()
            {
                view.onVerifyClicked();

                model.addMembershipCriteria(view.getMembershipCriteria(), view.isGroupSelected());
            }
        });

        eventBus.addObserver(MembershipCriteriaAddedEvent.class, new Observer<MembershipCriteriaAddedEvent>()
        {

            public void update(final MembershipCriteriaAddedEvent event)
            {
                view.addMembershipCriteria(event.getMembershipCriteria());
            }
        });

        eventBus.addObserver(MembershipCriteriaVerificationFailureEvent.class,
                new Observer<MembershipCriteriaVerificationFailureEvent>()
                {

                    public void update(final MembershipCriteriaVerificationFailureEvent event)
                    {
                        view.onVerifyFailure();

                    }
                });
        
        eventBus.addObserver(MembershipCriteriaVerificationNoUsersEvent.class,
                new Observer<MembershipCriteriaVerificationNoUsersEvent>()
                {

                    public void update(final MembershipCriteriaVerificationNoUsersEvent event)
                    {
                        view.onVerifyNoUsers();

                    }
                });

        eventBus.addObserver(MembershipCriteriaVerificationSuccessEvent.class,
                new Observer<MembershipCriteriaVerificationSuccessEvent>()
                {

                    public void update(final MembershipCriteriaVerificationSuccessEvent event)
                    {
                        view.onVerifySuccess(event.getNumberOfResults());
                    }
                });

        model.init();
    }
}
