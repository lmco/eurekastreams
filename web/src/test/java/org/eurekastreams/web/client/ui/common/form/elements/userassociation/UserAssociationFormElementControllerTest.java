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
import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MembershipCriteriaAddedEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaVerificationFailureEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaVerificationNoUsersEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaVerificationSuccessEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.ui.Session;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.Command;

/**
 * Tests the controller.
 */
public class UserAssociationFormElementControllerTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
            GWTMockUtilities.disarm();
        }
    };

    /**
     * System under test.
     */
    private UserAssociationFormElementController sut;

    /**
     * Mock session.
     */
    private Session session = context.mock(Session.class);

    /**
     * Mock view.
     */
    private UserAssociationFormElementView view = context.mock(UserAssociationFormElementView.class);

    /**
     * Mock model.
     */
    private UserAssociationFormElementModel model = context.mock(UserAssociationFormElementModel.class);

    /**
     * Mock event bus.
     */
    private EventBus eventBus = context.mock(EventBus.class);

    /**
     * Group click intercepter.
     */
    private AnonymousClassInterceptor<ClickHandler> groupClickHandlerInt = 
        new AnonymousClassInterceptor<ClickHandler>();

    /**
     * Attribue click intercepter.
     */
    private AnonymousClassInterceptor<ClickHandler> attrClickHandlerInt = new AnonymousClassInterceptor<ClickHandler>();

    /**
     * Verify command intercepter.
     */
    private AnonymousClassInterceptor<Command> verifyCommandInt = new AnonymousClassInterceptor<Command>();

    /**
     * Membership criteria added event intercepter.
     */
    private AnonymousClassInterceptor<Observer<MembershipCriteriaAddedEvent>> membershipCriteriaAddedEventInt = 
        new AnonymousClassInterceptor<Observer<MembershipCriteriaAddedEvent>>();

    /**
     * Membership criteria verification failure intercepter.
     */
    private AnonymousClassInterceptor<Observer<MembershipCriteriaVerificationFailureEvent>> 
        membershipCriteriaVerificationFailureEventInt = 
        new AnonymousClassInterceptor<Observer<MembershipCriteriaVerificationFailureEvent>>();

    /**
     * Membership criteria verification success intercepter.
     */
    private AnonymousClassInterceptor<Observer<MembershipCriteriaVerificationSuccessEvent>> 
        membershipCriteriaVerificationSuccessEventInt = 
        new AnonymousClassInterceptor<Observer<MembershipCriteriaVerificationSuccessEvent>>();

    /**
     * Membership criteria verification no user intercepter.
     */
    private AnonymousClassInterceptor<Observer<MembershipCriteriaVerificationNoUsersEvent>> 
        membershipCriteriaVerificationNoUsersEventInt = 
        new AnonymousClassInterceptor<Observer<MembershipCriteriaVerificationNoUsersEvent>>();

    /**
     * Setup test fixtures.
     */
    @Before
    public final void setUp()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(session).getEventBus();
                will(returnValue(eventBus));

                oneOf(view).onGroupSearchSelected();

                oneOf(view).addGroupClickHandler(with(any(ClickHandler.class)));
                will(groupClickHandlerInt);

                oneOf(view).addAttrClickHandler(with(any(ClickHandler.class)));
                will(attrClickHandlerInt);

                oneOf(view).addVerifyCommand(with(any(Command.class)));
                will(verifyCommandInt);

                oneOf(eventBus).addObserver(with(equal(MembershipCriteriaAddedEvent.class)), with(any(Observer.class)));
                will(membershipCriteriaAddedEventInt);

                oneOf(eventBus).addObserver(with(equal(MembershipCriteriaVerificationFailureEvent.class)),
                        with(any(Observer.class)));
                will(membershipCriteriaVerificationFailureEventInt);

                oneOf(eventBus).addObserver(with(equal(MembershipCriteriaVerificationNoUsersEvent.class)),
                        with(any(Observer.class)));
                will(membershipCriteriaVerificationNoUsersEventInt);

                oneOf(eventBus).addObserver(with(equal(MembershipCriteriaVerificationSuccessEvent.class)),
                        with(any(Observer.class)));
                will(membershipCriteriaVerificationSuccessEventInt);

                oneOf(model).init();
            }
        });

        sut = new UserAssociationFormElementController(session, view, model);
        sut.init();
    }

    /**
     * Tests the membership criteria added event.
     */
    @Test
    public final void membershipCriteriaAddedEventTest()
    {
        final MembershipCriteriaAddedEvent event = new MembershipCriteriaAddedEvent(new MembershipCriteria(), false);

        context.checking(new Expectations()
        {
            {
                oneOf(view).addMembershipCriteria(event.getMembershipCriteria());
            }
        });

        membershipCriteriaAddedEventInt.getObject().update(event);

        context.assertIsSatisfied();
    }

    /**
     * Tests the membership criteria verification failure event.
     */
    @Test
    public final void membershipCriteriaVerificationFailureEventTest()
    {
        final MembershipCriteriaVerificationFailureEvent event = new MembershipCriteriaVerificationFailureEvent();

        context.checking(new Expectations()
        {
            {
                oneOf(view).onVerifyFailure();
            }
        });

        membershipCriteriaVerificationFailureEventInt.getObject().update(event);

        context.assertIsSatisfied();
    }

    /**
     * Tests the membership criteria verification success event.
     */
    @Test
    public final void membershipCriteriaVerificationSuccessEventTest()
    {
        final MembershipCriteriaVerificationSuccessEvent event = new MembershipCriteriaVerificationSuccessEvent(1);

        context.checking(new Expectations()
        {
            {
                oneOf(view).onVerifySuccess(event.getNumberOfResults());
            }
        });

        membershipCriteriaVerificationSuccessEventInt.getObject().update(event);

        context.assertIsSatisfied();
    }

    /**
     * Tests the group radio button click handler.
     */
    @Test
    public final void groupClickHandlerTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(view).onGroupSearchSelected();
            }
        });

        groupClickHandlerInt.getObject().onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Tests the attribute radio button click handler.
     */
    @Test
    public final void attrClickHandlerTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(view).onAttributeSearchSelected();
            }
        });

        attrClickHandlerInt.getObject().onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Tests the verify command.
     */
    @Test
    public final void verifyCommandTest()
    {
        final String memberCriteria = "some.group";
        final Boolean groupSelect = Boolean.TRUE;

        context.checking(new Expectations()
        {
            {
                oneOf(view).onVerifyClicked();

                oneOf(view).getMembershipCriteria();
                will(returnValue(memberCriteria));

                oneOf(view).isGroupSelected();
                will(returnValue(groupSelect));

                oneOf(model).addMembershipCriteria(memberCriteria, groupSelect);
            }
        });

        verifyCommandInt.getObject().execute();

        context.assertIsSatisfied();
    }

    /**
     * Init test.
     */
    @Test
    public final void initTest()
    {
        context.assertIsSatisfied();
    }
}
