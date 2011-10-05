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
package org.eurekastreams.server.service.email;

import javax.mail.FetchProfile;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;

import org.eurekastreams.commons.exceptions.ValidationException;
import org.hamcrest.collection.IsArrayContaining;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests ImapEmailIngester.
 */
public class ImapEmailIngesterTest
{
    /** Test data. */
    private static final String INPUT_FOLDER_NAME = "InputFolder";

    /** Test data. */
    private static final String ERROR_FOLDER_NAME = "ErrorFolder";

    /** Test data. */
    private static final String SUCCESS_FOLDER_NAME = "SuccessFolder";

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    /** For getting a connection to the mail server. */
    private final ImapStoreFactory storeFactory = context.mock(ImapStoreFactory.class);

    /** For validating/authenticating messages. */
    private final MessageProcessor messageProcessor = context.mock(MessageProcessor.class);

    /** Fixture: store. */
    private final Store store = context.mock(Store.class);

    /** Fixture: input folder. */
    private final Folder inputFolder = context.mock(Folder.class, "inputFolder");

    /** Fixture: error folder. */
    private final Folder errorFolder = context.mock(Folder.class, "errorFolder");

    /** Fixture: success folder. */
    private final Folder successFolder = context.mock(Folder.class, "successFolder");

    /** Fixture: message. */
    private final Message message = context.mock(Message.class);

    /** SUT. */
    private ImapEmailIngester sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new ImapEmailIngester(storeFactory, messageProcessor, INPUT_FOLDER_NAME, ERROR_FOLDER_NAME,
                SUCCESS_FOLDER_NAME);
    }

    /**
     * Sets up expectations for a successful connection and retrieval of messages.
     *
     * @param msgs
     *            Messages to be fetched from inbox.
     * @throws MessagingException
     *             Won't.
     */
    private void expectSuccessfulFrame(final Message[] msgs) throws MessagingException
    {
        context.checking(new Expectations()
        {
            {
                oneOf(storeFactory).getStore();
                will(returnValue(store));

                oneOf(store).getFolder(INPUT_FOLDER_NAME);
                will(returnValue(inputFolder));
                allowing(inputFolder).exists();
                will(returnValue(true));
                allowing(store).getFolder(SUCCESS_FOLDER_NAME);
                will(returnValue(successFolder));
                allowing(successFolder).exists();
                will(returnValue(true));
                allowing(store).getFolder(ERROR_FOLDER_NAME);
                will(returnValue(errorFolder));
                allowing(errorFolder).exists();
                will(returnValue(true));

                oneOf(inputFolder).open(Folder.READ_WRITE);
                oneOf(inputFolder).getMessages();
                will(returnValue(msgs));
                oneOf(inputFolder).fetch(with(same(msgs)), with(any(FetchProfile.class)));

                oneOf(inputFolder).close(true);
                oneOf(store).close();
            }
        });
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testExecuteSuccess() throws MessagingException
    {
        expectSuccessfulFrame(new Message[] { message });
        context.checking(new Expectations()
        {
            {
                oneOf(messageProcessor).execute(message);

                oneOf(inputFolder).copyMessages(with(IsArrayContaining.hasItemInArray(message)),
                        with(same(successFolder)));
                oneOf(message).setFlag(Flag.DELETED, true);
            }
        });

        sut.execute();
        context.assertIsSatisfied();
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testExecuteSuccessNoCopy() throws MessagingException
    {
        sut = new ImapEmailIngester(storeFactory, messageProcessor, INPUT_FOLDER_NAME, ERROR_FOLDER_NAME, null);

        expectSuccessfulFrame(new Message[] { message });
        context.checking(new Expectations()
        {
            {
                oneOf(messageProcessor).execute(message);

                oneOf(message).setFlag(Flag.DELETED, true);
            }
        });

        sut.execute();
        context.assertIsSatisfied();
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testExecuteFailMsg() throws MessagingException
    {
        expectSuccessfulFrame(new Message[] { message });

        context.checking(new Expectations()
        {
            {
                oneOf(messageProcessor).execute(message);
                will(throwException(new ValidationException()));

                oneOf(inputFolder).copyMessages(with(IsArrayContaining.hasItemInArray(message)),
                        with(same(errorFolder)));
                oneOf(message).setFlag(Flag.DELETED, true);
            }
        });

        sut.execute();
        context.assertIsSatisfied();
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testExecuteFailMsgNoCopy() throws MessagingException
    {
        sut = new ImapEmailIngester(storeFactory, messageProcessor, INPUT_FOLDER_NAME, "", SUCCESS_FOLDER_NAME);
        expectSuccessfulFrame(new Message[] { message });

        context.checking(new Expectations()
        {
            {
                oneOf(messageProcessor).execute(message);
                will(throwException(new MessagingException()));

                oneOf(message).setFlag(Flag.DELETED, true);
            }
        });

        sut.execute();
        context.assertIsSatisfied();
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testExecuteNoMessages() throws MessagingException
    {
        expectSuccessfulFrame(new Message[] {});
        sut.execute();
        context.assertIsSatisfied();
    }

    /**
     * Tests error condition.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testExecuteCannotOpenStore() throws MessagingException
    {
        context.checking(new Expectations()
        {
            {
                oneOf(storeFactory).getStore();
                will(throwException(new MessagingException()));
            }
        });

        sut.execute();
        context.assertIsSatisfied();
    }

    /**
     * Tests error condition.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testExecuteBadInputFolder() throws MessagingException
    {
        context.checking(new Expectations()
        {
            {
                oneOf(storeFactory).getStore();
                will(returnValue(store));

                oneOf(store).getFolder(INPUT_FOLDER_NAME);
                will(returnValue(inputFolder));
                oneOf(inputFolder).exists();
                will(returnValue(false));

                oneOf(store).close();
            }
        });

        sut.execute();
        context.assertIsSatisfied();
    }

    /**
     * Tests error condition.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testExecuteBadErrorFolder() throws MessagingException
    {
        context.checking(new Expectations()
        {
            {
                oneOf(storeFactory).getStore();
                will(returnValue(store));

                oneOf(store).getFolder(INPUT_FOLDER_NAME);
                will(returnValue(inputFolder));
                allowing(inputFolder).exists();
                will(returnValue(true));
                oneOf(store).getFolder(SUCCESS_FOLDER_NAME);
                will(returnValue(successFolder));
                allowing(successFolder).exists();
                will(returnValue(true));
                oneOf(store).getFolder(ERROR_FOLDER_NAME);
                will(returnValue(errorFolder));
                allowing(errorFolder).exists();
                will(returnValue(false));

                oneOf(store).close();
            }
        });

        sut.execute();
        context.assertIsSatisfied();
    }

    /**
     * Tests error condition.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testExecuteBadSuccessFolder() throws MessagingException
    {
        context.checking(new Expectations()
        {
            {
                oneOf(storeFactory).getStore();
                will(returnValue(store));

                oneOf(store).getFolder(INPUT_FOLDER_NAME);
                will(returnValue(inputFolder));
                allowing(inputFolder).exists();
                will(returnValue(true));
                oneOf(store).getFolder(SUCCESS_FOLDER_NAME);
                will(returnValue(successFolder));
                allowing(successFolder).exists();
                will(returnValue(false));

                oneOf(store).close();
            }
        });

        sut.execute();
        context.assertIsSatisfied();
    }

    /**
     * Tests error condition.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testExecuteException1() throws MessagingException
    {
        context.checking(new Expectations()
        {
            {
                oneOf(storeFactory).getStore();
                will(returnValue(store));

                oneOf(store).getFolder(INPUT_FOLDER_NAME);
                will(throwException(new MessagingException()));

                oneOf(store).close();
            }
        });

        sut.execute();
        context.assertIsSatisfied();
    }

    /**
     * Tests error condition.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testExecuteException2() throws MessagingException
    {
        context.checking(new Expectations()
        {
            {
                oneOf(storeFactory).getStore();
                will(returnValue(store));

                oneOf(store).getFolder(INPUT_FOLDER_NAME);
                will(throwException(new IllegalArgumentException()));

                oneOf(store).close();
            }
        });

        sut.execute();
        context.assertIsSatisfied();
    }

    /**
     * Tests error condition.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testExecuteCloseException() throws MessagingException
    {
        context.checking(new Expectations()
        {
            {
                oneOf(storeFactory).getStore();
                will(returnValue(store));

                oneOf(store).getFolder(INPUT_FOLDER_NAME);
                will(throwException(new IllegalArgumentException()));

                oneOf(store).close();
                will(throwException(new MessagingException()));
            }
        });
        sut.execute();
        context.assertIsSatisfied();
    }
}
