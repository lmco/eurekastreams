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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MembershipCriteriaAddedEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaRemovedEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaVerificationFailureEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaVerificationNoUsersEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaVerificationSuccessEvent;
import org.eurekastreams.web.client.ui.Session;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionRequest;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.SystemSettings;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Tests the model.
 */
public class UserAssociationFormElementModelTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private UserAssociationFormElementModel sut;

    /**
     * Session mock.
     */
    private Session session = context.mock(Session.class);

    /**
     * Settings mock.
     */
    private SystemSettings settings = context.mock(SystemSettings.class);

    /**
     * Action processor.
     */
    private ActionProcessor processor = context.mock(ActionProcessor.class);

    /**
     * Event bus.
     */
    private EventBus eventBus = context.mock(EventBus.class);

    /**
     * Setup the test fixtures.
     */
    @Before
    public final void setUp()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(session).getActionProcessor();
                will(returnValue(processor));

                oneOf(session).getEventBus();
                will(returnValue(eventBus));
            }
        });

        sut = new UserAssociationFormElementModel(session, settings);
    }

    /**
     * Initialization test.
     */
    @Test
    public final void initTest()
    {
        final List<MembershipCriteria> associations = new ArrayList<MembershipCriteria>();
        associations.add(new MembershipCriteria());
        associations.add(new MembershipCriteria());

        context.checking(new Expectations()
        {
            {
                oneOf(settings).getMembershipCriteria();
                will(returnValue(associations));

                exactly(associations.size()).of(eventBus)
                        .notifyObservers(with(any(MembershipCriteriaAddedEvent.class)));
            }
        });

        sut.init();

        context.assertIsSatisfied();
    }

    /**
     * Add membership criteria failure test for attributes.
     */
    @Test
    public final void addMembershipCriteriaAttribFailureTest()
    {
        final String membershipCritera = "attr=val";
        final Boolean isGroup = Boolean.FALSE;

        final AnonymousClassInterceptor<AsyncCallback<List<Person>>> cbInt = 
            new AnonymousClassInterceptor<AsyncCallback<List<Person>>>();

        context.checking(new Expectations()
        {
            {
                oneOf(processor).makeRequest(with(any(ActionRequest.class)), with(any(AsyncCallback.class)));
                will(cbInt);

                oneOf(eventBus).notifyObservers(with(any(MembershipCriteriaVerificationFailureEvent.class)));
            }
        });

        sut.addMembershipCriteria(membershipCritera, isGroup);

        cbInt.getObject().onFailure(null);

        context.assertIsSatisfied();
    }

    /**
     * Add membership criteria failure test for groups.
     */
    @Test
    public final void addMembershipCriteriaGroupFailureTest()
    {
        final String membershipCritera = "some.group";
        final Boolean isGroup = Boolean.TRUE;

        final AnonymousClassInterceptor<AsyncCallback<List<Person>>> cbInt = 
            new AnonymousClassInterceptor<AsyncCallback<List<Person>>>();

        context.checking(new Expectations()
        {
            {
                oneOf(processor).makeRequest(with(any(ActionRequest.class)), with(any(AsyncCallback.class)));
                will(cbInt);

                oneOf(eventBus).notifyObservers(with(any(MembershipCriteriaVerificationFailureEvent.class)));
            }
        });

        sut.addMembershipCriteria(membershipCritera, isGroup);

        cbInt.getObject().onFailure(null);

        context.assertIsSatisfied();
    }

    /**
     * Add membership criteria success test with no results for a group.
     */
    @Test
    public final void addMembershipCriteriaGroupSuccessZeroResultsTest()
    {
        final String membershipCritera = "some.group";
        final Boolean isGroup = Boolean.TRUE;

        final AnonymousClassInterceptor<AsyncCallback<Boolean>> cbInt = 
            new AnonymousClassInterceptor<AsyncCallback<Boolean>>();

        context.checking(new Expectations()
        {
            {
                oneOf(processor).makeRequest(with(any(ActionRequest.class)), with(any(AsyncCallback.class)));
                will(cbInt);

                oneOf(eventBus).notifyObservers(with(any(MembershipCriteriaVerificationSuccessEvent.class)));
                oneOf(eventBus).notifyObservers(with(any(MembershipCriteriaVerificationNoUsersEvent.class)));
            }
        });

        sut.addMembershipCriteria(membershipCritera, isGroup);

        cbInt.getObject().onSuccess(false);

        context.assertIsSatisfied();
    }

    /**
     * Add membership criteria success test with no results for a attributes.
     */
    @Test
    public final void addMembershipCriteriaAttribSuccessZeroResultsTest()
    {
        final String membershipCritera = "attr=val";
        final Boolean isGroup = Boolean.FALSE;

        final AnonymousClassInterceptor<AsyncCallback<List<Person>>> cbInt = 
            new AnonymousClassInterceptor<AsyncCallback<List<Person>>>();

        context.checking(new Expectations()
        {
            {
                oneOf(processor).makeRequest(with(any(ActionRequest.class)), with(any(AsyncCallback.class)));
                will(cbInt);

                oneOf(eventBus).notifyObservers(with(any(MembershipCriteriaVerificationSuccessEvent.class)));
                oneOf(eventBus).notifyObservers(with(any(MembershipCriteriaVerificationNoUsersEvent.class)));
            }
        });

        sut.addMembershipCriteria(membershipCritera, isGroup);

        cbInt.getObject().onSuccess(new ArrayList<Person>());

        context.assertIsSatisfied();
    }

    /**
     * Add membership criteria test for a group.
     */
    @Test
    public final void addMembershipCriteriaGroupSuccessTest()
    {
        final String membershipCritera = "some.group";
        final Boolean isGroup = Boolean.TRUE;

        final AnonymousClassInterceptor<AsyncCallback<Boolean>> cbInt = 
            new AnonymousClassInterceptor<AsyncCallback<Boolean>>();

        context.checking(new Expectations()
        {
            {
                oneOf(processor).makeRequest(with(any(ActionRequest.class)), with(any(AsyncCallback.class)));
                will(cbInt);

                oneOf(eventBus).notifyObservers(with(any(MembershipCriteriaVerificationSuccessEvent.class)));

                oneOf(eventBus).notifyObservers(with(any(MembershipCriteriaAddedEvent.class)));
            }
        });

        sut.addMembershipCriteria(membershipCritera, isGroup);

        cbInt.getObject().onSuccess(true);

        context.assertIsSatisfied();
    }

    /**
     * Add membership criteria test for attributes.
     */
    @Test
    public final void addMembershipCriteriaAttribSuccessTest()
    {
        final String membershipCritera = "attr=val";
        final Boolean isGroup = Boolean.FALSE;

        final AnonymousClassInterceptor<AsyncCallback<List<Person>>> cbInt = 
            new AnonymousClassInterceptor<AsyncCallback<List<Person>>>();

        List<Person> results = new ArrayList<Person>();
        results.add(new Person());

        context.checking(new Expectations()
        {
            {
                oneOf(processor).makeRequest(with(any(ActionRequest.class)), with(any(AsyncCallback.class)));
                will(cbInt);

                oneOf(eventBus).notifyObservers(with(any(MembershipCriteriaVerificationSuccessEvent.class)));

                oneOf(eventBus).notifyObservers(with(any(MembershipCriteriaAddedEvent.class)));
            }
        });

        sut.addMembershipCriteria(membershipCritera, isGroup);

        cbInt.getObject().onSuccess(results);

        context.assertIsSatisfied();
    }

    /**
     * Tests the membership criteria property.
     */
    @Test
    @SuppressWarnings("unchecked")
    public final void membershipCriteriaPropertyTest()
    {
        final List<MembershipCriteria> associations = new ArrayList<MembershipCriteria>();
        associations.add(new MembershipCriteria());
        associations.add(new MembershipCriteria());

        context.checking(new Expectations()
        {
            {
                oneOf(settings).getMembershipCriteria();
                will(returnValue(associations));

                exactly(associations.size()).of(eventBus)
                        .notifyObservers(with(any(MembershipCriteriaAddedEvent.class)));
                
                oneOf(eventBus).notifyObservers(with(any(MembershipCriteriaRemovedEvent.class)));
            }
        });

        sut.init();

        Assert.assertEquals(2, ((List<String>) sut.getMembershipCriteria()).size());

        sut.removeMembershipCriteria(((List<MembershipCriteria>) sut.getMembershipCriteria()).get(0));

        Assert.assertEquals(1, ((List<MembershipCriteria>) sut.getMembershipCriteria()).size());

        context.assertIsSatisfied();
    }
}
