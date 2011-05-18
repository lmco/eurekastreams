/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.notification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.text.StrLookup;
import org.eurekastreams.server.action.execution.notification.TemplateEmailBuilder.HtmlEncodingLookup;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.EmailerFactory;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the general email builder.
 */
@SuppressWarnings("unchecked")
public class TemplateEmailBuilderTest
{
    /** Test data. */
    private static final long RECIPIENT1_ID = 1111L;

    /** Test data. */
    private static final long RECIPIENT2_ID = 1112L;

    /** Test data. */
    private static final String RECIPIENT1_EMAIL = "spammed1@example.com";

    /** Test data. */
    private static final String RECIPIENT2_EMAIL = "spammed2@example.com";

    /** Test data. */
    private static final long ACTOR_ID = 2222L;

    /** Test data. */
    private static final String ACTOR_NAME = "Somebody Active";

    /** Test data. */
    private static final long ACTIVITY_ID = 4444L;

    /** Test data. */
    private static final long DESTINATION_ID = 5555L;

    /** Test data. */
    private static final String GROUP_NAME = "Some Group";

    /** Test data. */
    private static final NotificationType NOTIFICATION_TYPE = NotificationType.COMMENT_TO_COMMENTED_POST;

    /** Context for building mock objects. */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** SUT. */
    private TemplateEmailBuilder sut;

    /** Fixture: For sending email. */
    private EmailerFactory emailer = context.mock(EmailerFactory.class);

    /** Fixture: For getting person info. */
    private DomainMapper<List<Long>, List<PersonModelView>> peopleMapper = context.mock(DomainMapper.class,
            "PeopleMapper");

    /** Fixture: For getting system settings. */
    private DomainMapper<MapperRequest<SystemSettings>, SystemSettings> systemSettingsMapper = context.mock(
            DomainMapper.class, "SystemSettings");

    /** Fixture: message. */
    private MimeMessage message = context.mock(MimeMessage.class);

    /** Fixture: person recipient of email. */
    private PersonModelView recipientPerson1 = new PersonModelView()
    {
        {
            setEmail(RECIPIENT1_EMAIL);
        }
    };
    /** Fixture: person recipient of email. */
    private PersonModelView recipientPerson2 = new PersonModelView()
    {
        {
            setEmail(RECIPIENT2_EMAIL);
        }
    };

    /** Fixture: system settings. */
    private SystemSettings systemSettings = new SystemSettings();

    /** Fixture: notification. */
    private NotificationDTO notification;

    /**
     * Constructor; one-time setup.
     */
    public TemplateEmailBuilderTest()
    {
        systemSettings.setSiteLabel("SiteLabel");
        systemSettings.setSupportEmailAddress("SupportEmailAddress");
        systemSettings.setSupportPhoneNumber("SupportPhoneNumber");
        systemSettings.setSupportStreamGroupDisplayName("SupportStreamGroupDisplayName");
        systemSettings.setSupportStreamGroupShortName("SupportStreamGroupShortName");
    }

    /**
     * Common setup before each test.
     * 
     * @throws Exception
     *             Possibly.
     */
    @Before
    public void setUp() throws Exception
    {
        notification = new NotificationDTO(Collections.singletonList(RECIPIENT1_ID), NOTIFICATION_TYPE, 0L);

        context.checking(new Expectations()
        {
            {
                allowing(systemSettingsMapper).execute(with(any(MapperRequest.class)));
                will(returnValue(systemSettings));
            }
        });
    }

    /**
     * Expectations to ignore recipients (for tests focused on other aspects).
     * 
     * @throws MessagingException
     *             Shouldn't.
     */
    private void setupIgnoreRecipientsExpectations() throws MessagingException
    {
        final List<PersonModelView> peopleList = new ArrayList<PersonModelView>();
        context.checking(new Expectations()
        {
            {
                allowing(peopleMapper);
                will(returnValue(peopleList));

                ignoring(emailer).setTo(with(any(MimeMessage.class)), with(any(String.class)));
                ignoring(emailer).setCc(with(any(MimeMessage.class)), with(any(String.class)));
                ignoring(emailer).setBcc(with(any(MimeMessage.class)), with(any(String.class)));
            }
        });
    }

    /**
     * Core functionality shared by the data use tests.
     * 
     * @param template
     *            The base template to use.
     * @param expectedText
     *            The expected resulting text.
     * @throws Exception
     *             Shouldn't.
     */
    private void coreDataUseTest(final String template, final String expectedText) throws Exception
    {
        setupIgnoreRecipientsExpectations();
        context.checking(new Expectations()
        {
            {
                oneOf(emailer).setSubject(with(same(message)), with(equal("S:" + expectedText)));
                oneOf(emailer).setTextBody(with(same(message)), with(equal("T:" + expectedText)));
                oneOf(emailer).setHtmlBody(with(same(message)), with(equal("H:" + expectedText)));
            }
        });

        sut = new TemplateEmailBuilder(emailer, peopleMapper, systemSettingsMapper, null, "S:" + template, "T:"
                + template, "H:" + template);
        sut.build(notification, message);
        context.assertIsSatisfied();
    }

    /**
     * Tests using actor data.
     * 
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testBuildUsingActor() throws Exception
    {
        notification.setActorId(ACTOR_ID);
        notification.setActorAccountId("ActorAccount");
        notification.setActorName(ACTOR_NAME);

        coreDataUseTest("$(actor.id)/$(actor.accountid)/$(actor.name)", ACTOR_ID + "/ActorAccount/" + ACTOR_NAME);
    }

    /**
     * Tests using activity data.
     * 
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testBuildUsingActivity() throws Exception
    {
        notification.setActivity(ACTIVITY_ID, BaseObjectType.BOOKMARK);

        coreDataUseTest("$(activity.id)/$(activity.type)", ACTIVITY_ID + "/link");
    }

    /**
     * Tests using activity data.
     * 
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testBuildUsingActivity2() throws Exception
    {
        notification.setActivity(ACTIVITY_ID, BaseObjectType.VIDEO);

        coreDataUseTest("$(activity.id)/$(activity.type)", ACTIVITY_ID + "/video");
    }

    /**
     * Tests using destination data.
     * 
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testBuildUsingAuxiliary() throws Exception
    {
        notification.setAuxiliary(EntityType.GROUP, "mygroup", GROUP_NAME);

        coreDataUseTest("$(aux.type)/$(aux.uniqueid)/$(aux.name)/$(aux.page)", "GROUP/mygroup/" + GROUP_NAME
                + "/groups");
    }

    /**
     * Tests using extra properties.
     * 
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testBuildWithExtraProperties() throws Exception
    {
        notification.setActorId(ACTOR_ID);
        notification.setActorName(ACTOR_NAME);

        final String template = "$(actor.id)/$(actor.name)/$(key1)";
        final String expectedText = ACTOR_ID + "/" + ACTOR_NAME + "/value1";

        setupIgnoreRecipientsExpectations();
        context.checking(new Expectations()
        {
            {
                oneOf(emailer).setSubject(with(same(message)), with(equal(expectedText)));
                oneOf(emailer).setTextBody(with(same(message)), with(equal(expectedText)));
                oneOf(emailer).setHtmlBody(with(same(message)), with(equal(expectedText)));
            }
        });

        Map<String, String> extraProperties = new HashMap<String, String>();
        extraProperties.put("key1", "value1");
        extraProperties.put("actor.id", "**" + Long.toString(ACTOR_ID + 4) + "**");

        sut = new TemplateEmailBuilder(emailer, peopleMapper, systemSettingsMapper, extraProperties, template,
                template, template);
        sut.build(notification, message);
        context.assertIsSatisfied();
    }

    /**
     * Tests using invocation properties.
     * 
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testBuildWithInvocationProperties() throws Exception
    {
        notification.setActorId(ACTOR_ID);
        notification.setActorName(ACTOR_NAME);

        final String template = "$(actor.id)/$(actor.name)/$(key1)/$(key2)/$(key3)";
        final String expectedText = ACTOR_ID + "/" + ACTOR_NAME + "/value1/value2/valueC";

        setupIgnoreRecipientsExpectations();
        context.checking(new Expectations()
        {
            {
                oneOf(emailer).setSubject(with(same(message)), with(equal(expectedText)));
                oneOf(emailer).setTextBody(with(same(message)), with(equal(expectedText)));
                oneOf(emailer).setHtmlBody(with(same(message)), with(equal(expectedText)));
            }
        });

        Map<String, String> extraProperties = new HashMap<String, String>();
        extraProperties.put("key1", "value1");
        extraProperties.put("key2", "value2");
        extraProperties.put("actor.id", "**" + Long.toString(ACTOR_ID + 4) + "**");

        Map<String, String> invocationProperties = new HashMap<String, String>();
        invocationProperties.put("key2", "valueB");
        invocationProperties.put("key3", "valueC");

        sut = new TemplateEmailBuilder(emailer, peopleMapper, systemSettingsMapper, extraProperties, template,
                template, template);
        sut.build(notification, invocationProperties, message);
        context.assertIsSatisfied();
    }

    /**
     * Tests with one recipient.
     * 
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testBuildWithOneRecipient() throws Exception
    {
        notification.setRecipientIds(Collections.singletonList(RECIPIENT1_ID));

        context.checking(new Expectations()
        {
            {
                oneOf(peopleMapper).execute(with(equal(Collections.singletonList(RECIPIENT1_ID))));
                will(returnValue(Collections.singletonList(recipientPerson1)));
                oneOf(emailer).setTo(with(same(message)), with(equal(RECIPIENT1_EMAIL)));

                ignoring(emailer).setTextBody(with(any(MimeMessage.class)), with(any(String.class)));
                ignoring(emailer).setHtmlBody(with(any(MimeMessage.class)), with(any(String.class)));
                ignoring(emailer).setSubject(with(any(MimeMessage.class)), with(any(String.class)));
            }
        });

        sut = new TemplateEmailBuilder(emailer, peopleMapper, systemSettingsMapper, null, "", "", "");
        sut.build(notification, message);

        context.assertIsSatisfied();
    }

    /**
     * Tests with multiple recipients.
     * 
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testBuildWithMultipleRecipients() throws Exception
    {
        notification.setRecipientIds(Arrays.asList(RECIPIENT1_ID, RECIPIENT2_ID));

        context.checking(new Expectations()
        {
            {
                // intentionally flip the order in the mapper, since the mapper return order is unpredictable
                oneOf(peopleMapper).execute(
                        (List<Long>) with(Matchers.allOf(Matchers.hasItem(RECIPIENT1_ID), Matchers
                                .hasItem(RECIPIENT2_ID))));
                will(returnValue(Arrays.asList(recipientPerson2, recipientPerson1)));

                oneOf(emailer).setBcc(with(same(message)), with(equal(RECIPIENT2_EMAIL + "," + RECIPIENT1_EMAIL)));

                ignoring(emailer).setTextBody(with(any(MimeMessage.class)), with(any(String.class)));
                ignoring(emailer).setHtmlBody(with(any(MimeMessage.class)), with(any(String.class)));
                ignoring(emailer).setSubject(with(any(MimeMessage.class)), with(any(String.class)));
            }
        });

        sut = new TemplateEmailBuilder(emailer, peopleMapper, systemSettingsMapper, null, "", "", "");
        sut.build(notification, message);

        context.assertIsSatisfied();
    }

    /**
     * Tests the HTML encoding decorator.
     */
    @Test
    public void testHtmlEncodingDecorator()
    {
        final Map<String, String> vars = new HashMap<String, String>();
        vars.put("isNull", null);
        vars.put("needsHelp", "<this & that>");

        StrLookup decorated = new StrLookup()
        {
            /**
             * {@inheritDoc}
             */
            @Override
            public String lookup(final String inKey)
            {
                return vars.get(inKey);
            }
        };

        HtmlEncodingLookup decorator = new HtmlEncodingLookup(decorated);
        assertNull(decorator.lookup("isNull"));
        assertEquals("&lt;this &amp; that&gt;", decorator.lookup("needsHelp"));
    }

    /**
     * Tests using system settings.
     * 
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testBuildWithSystemSettings() throws Exception
    {
        final String template = "$(settings.sitelabel)/$(settings.support.email)/$(settings.support.phone)"
                + "/$(settings.support.name)" + "/$(settings.support.uniqueid)";
        final String expectedText = "SiteLabel/SupportEmailAddress/SupportPhoneNumber/SupportStreamGroupDisplayName"
                + "/SupportStreamGroupShortName";

        setupIgnoreRecipientsExpectations();
        context.checking(new Expectations()
        {
            {
                oneOf(emailer).setSubject(with(same(message)), with(equal("S:" + expectedText)));
                oneOf(emailer).setTextBody(with(same(message)), with(equal("T:" + expectedText)));
                oneOf(emailer).setHtmlBody(with(same(message)), with(equal("H:" + expectedText)));
            }
        });

        sut = new TemplateEmailBuilder(emailer, peopleMapper, systemSettingsMapper, null, "S:" + template, "T:"
                + template, "H:" + template);
        sut.build(notification, message);
        context.assertIsSatisfied();
    }
}
