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
package org.eurekastreams.server.service.actions.strategies;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;


/**
 * Test fixture for EmailerFactory.
 */
public class EmailerFactoryTest
{
    /** Test data. */
    private static final String DEFAULT_FROM = "default.sender@email.com";

    /** Context for building mock objects. */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** SUT. */
    private EmailerFactory sut;

    /**
     * Common setup for all tests.
     *
     * @throws Exception
     *             Possibly.
     */
    @Before
    public void setUp() throws Exception
    {
        final String mailSmtpHost = "host.email.com";
        final String mailSmtpPort = "25";
        final String mailTransportProtocol = "smtp";

        Map<String, String> settings = new HashMap<String, String>();
        settings.put("mail.smtp.host", mailSmtpHost);
        settings.put("mail.smtp.port", mailSmtpPort);
        sut = new EmailerFactory(mailTransportProtocol, settings, DEFAULT_FROM);
    }

    /**
     * Test creating a message.
     *
     * @throws MessagingException
     *             Only on test failure.
     * @throws IOException
     *             Only on test failure.
     */
    @Test
    public void testCreateMessage() throws MessagingException, IOException
    {
        MimeMessage msg = sut.createMessage();
        Assert.notNull(msg, "Message should not be null.");
        Assert.isInstanceOf(Multipart.class, msg.getContent(), "Content should be multipart.");
        Assert.notNull(msg.getSentDate(), "Date should be specified.");

        Address[] list = msg.getFrom();
        assertEquals(1, list.length);
        assertEquals(DEFAULT_FROM, list[0].toString());
    }

    /**
     * Test setting recipients.
     *
     * @throws MessagingException
     *             Only on test failure.
     */
    @Test
    public void testSetTo() throws MessagingException
    {
        final String toString = "recipient@email.com";

        MimeMessage msg = sut.createMessage();

        sut.setTo(msg, toString);

        Address[] list = msg.getRecipients(RecipientType.TO);
        assertEquals(1, list.length);
        assertEquals(toString, list[0].toString());
    }



    /**
     * Test setting recipients.
     *
     * @throws MessagingException
     *             Only on test failure.
     */
    @Test
    public void testSetCc() throws MessagingException
    {
        final String toString = "recipient@email.com";

        MimeMessage msg = sut.createMessage();

        sut.setCc(msg, toString);

        Address[] list = msg.getRecipients(RecipientType.CC);
        assertEquals(1, list.length);
        assertEquals(toString, list[0].toString());
    }


    /**
     * Test setting recipients.
     *
     * @throws MessagingException
     *             Only on test failure.
     */
    @Test
    public void testSetBcc() throws MessagingException
    {
        final String toString = "recipient@email.com";

        MimeMessage msg = sut.createMessage();

        sut.setBcc(msg, toString);

        Address[] list = msg.getRecipients(RecipientType.BCC);
        assertEquals(1, list.length);
        assertEquals(toString, list[0].toString());
    }

    /**
     * Test setting sender.
     *
     * @throws MessagingException
     *             Only on test failure.
     */
    @Test
    public void testSetFrom() throws MessagingException
    {
        final String fromString = "sender@email.com";

        MimeMessage msg = sut.createMessage();

        sut.setFrom(msg, fromString);

        Address[] list = msg.getFrom();
        assertEquals(1, list.length);
        assertEquals(fromString, list[0].toString());
    }

    /**
     * Test setting reply-to.
     * 
     * @throws MessagingException
     *             Only on test failure.
     */
    @Test
    public void testSetReplyTo() throws MessagingException
    {
        final String addressString = "somebody@email.com";

        MimeMessage msg = sut.createMessage();

        sut.setReplyTo(msg, addressString);

        Address[] list = msg.getReplyTo();
        assertEquals(1, list.length);
        assertEquals(addressString, list[0].toString());
    }

    /**
     * Test setting subject.
     * 
     * @throws MessagingException
     *             Only on test failure.
     */
    @Test
    public void testSetSubject() throws MessagingException
    {
        final String subject = "this is an email subject.";

        MimeMessage msg = sut.createMessage();

        sut.setSubject(msg, subject);

        assertEquals(subject, msg.getSubject());
    }

    /**
     * Test setting text body.
     *
     * @throws MessagingException
     *             Only on test failure.
     * @throws IOException
     *             Only on test failure.
     */
    @Test
    public void testSetTextBody() throws MessagingException, IOException
    {
        final String textBody = "this is the text body";

        MimeMessage msg = sut.createMessage();

        Multipart content = (Multipart) msg.getContent();
        assertEquals(0, content.getCount());

        sut.setTextBody(msg, textBody);

        assertEquals(1, content.getCount());
        BodyPart part = content.getBodyPart(0);
        assertEquals(textBody, part.getContent().toString());
        assertEquals("text/plain", part.getContentType());
    }

    /**
     * Test setting HTML body.
     *
     * @throws MessagingException
     *             Only on test failure.
     * @throws IOException
     *             Only on test failure.
     */
    @Test
    public void testSetHtmlBody() throws MessagingException, IOException
    {
        final String htmlBody = "this is the html body";

        MimeMessage msg = sut.createMessage();

        Multipart content = (Multipart) msg.getContent();
        assertEquals(0, content.getCount());

        sut.setHtmlBody(msg, htmlBody);

        assertEquals(1, content.getCount());
        BodyPart part = content.getBodyPart(0);
        assertEquals(htmlBody, part.getContent().toString());
        assertEquals("text/html; charset=ISO-8859-1", part.getContentType());
    }

    /**
     * Test exception getting the internal content.
     *
     * @throws MessagingException
     *             Expected result.
     * @throws IOException
     *             Should be impossible.
     */
    @Test(expected = MessagingException.class)
    public void testGetMultipartException() throws MessagingException, IOException
    {
        final MimeMessage msg = context.mock(MimeMessage.class);

        context.checking(new Expectations()
        {
            {
                allowing(msg).getContent();
                will(throwException(new IOException()));
            }
        });

        sut.getMultipart(msg);
        context.assertIsSatisfied();
    }

    /**
     * Test building a recipient list.
     *
     * @throws MessagingException
     *             Only on test failure.
     */
    @Test
    public void testSetToList() throws MessagingException
    {
        final String to1 = "recipient1@email.com";
        final String to2 = "recipient2@email.com";
        PersonModelView person1 = new PersonModelView()
        {
            {
                setEmail(to1);
            }
        };
        PersonModelView person2 = new PersonModelView()
        {
            {
                setEmail(to2);
            }
        };

        String result = EmailerFactory.buildEmailList(Arrays.asList(person1, person2));
        assertEquals(to1 + "," + to2, result);
    }
}
