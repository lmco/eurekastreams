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

import java.io.IOException;
import java.util.List;

import javax.mail.FetchProfile;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;

import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.service.actions.strategies.EmailerFactory;
import org.hamcrest.collection.IsArrayContaining;
import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.action.CustomAction;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests ImapEmailIngester.
 */
@SuppressWarnings("unchecked")
public class ImapEmailIngesterTest
{
    /** Test data. */
    private static final String INPUT_FOLDER_NAME = "InputFolder";

    /** Test data. */
    private static final String ERROR_FOLDER_NAME = "ErrorFolder";

    /** Test data. */
    private static final String SUCCESS_FOLDER_NAME = "SuccessFolder";

    /** Test data. */
    private static final String DISCARD_FOLDER_NAME = "DiscardFolder";

    /** Used for mocking objects. */
    private final JUnit4Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    /** For getting a connection to the mail server. */
    private final ImapStoreFactory storeFactory = mockery.mock(ImapStoreFactory.class);

    /** For validating/authenticating messages. */
    private final MessageProcessor messageProcessor = mockery.mock(MessageProcessor.class);

    /** Fixture: store. */
    private final Store store = mockery.mock(Store.class);

    /** Fixture: input folder. */
    private final Folder inputFolder = mockery.mock(Folder.class, "inputFolder");

    /** Fixture: error folder. */
    private final Folder errorFolder = mockery.mock(Folder.class, "errorFolder");

    /** Fixture: success folder. */
    private final Folder successFolder = mockery.mock(Folder.class, "successFolder");

    /** Fixture: success folder. */
    private final Folder discardFolder = mockery.mock(Folder.class, "discardFolder");

    /** Fixture: message. */
    private final Message message = mockery.mock(Message.class, "message");

    /** Fixture: response message. */
    private final Message responseMessage = mockery.mock(Message.class, "responseMessage");

    /** For sending response emails. */
    private final EmailerFactory emailerFactory = mockery.mock(EmailerFactory.class, "emailerFactory");

    /** SUT. */
    private ImapEmailIngester sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new ImapEmailIngester(storeFactory, messageProcessor, emailerFactory, INPUT_FOLDER_NAME,
                ERROR_FOLDER_NAME, SUCCESS_FOLDER_NAME, DISCARD_FOLDER_NAME)
        {
            // do this until we're done with the debug code
            @Override
            protected void dumpMessage(final Message inMessage, final int inIndex)
            {
            }
        };
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
        mockery.checking(new Expectations()
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
                allowing(store).getFolder(DISCARD_FOLDER_NAME);
                will(returnValue(discardFolder));
                allowing(discardFolder).exists();
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
     * @throws IOException
     *             Won't.
     */
    @Test
    public void testExecuteSuccess() throws MessagingException, IOException
    {
        expectSuccessfulFrame(new Message[] { message });
        mockery.checking(new Expectations()
        {
            {
                oneOf(messageProcessor).execute(with(same(message)), with(any(List.class)));
                will(returnValue(true));

                oneOf(inputFolder).copyMessages(with(IsArrayContaining.hasItemInArray(message)),
                        with(same(successFolder)));
                oneOf(message).setFlag(Flag.DELETED, true);
            }
        });

        sut.execute();
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Won't.
     * @throws IOException
     *             Won't.
     */
    @Test
    public void testExecuteSuccessNoCopy() throws MessagingException, IOException
    {
        sut = new ImapEmailIngester(storeFactory, messageProcessor, emailerFactory, INPUT_FOLDER_NAME,
                ERROR_FOLDER_NAME, null, null)
        {
            @Override
            protected void dumpMessage(final Message inMessage, final int inIndex)
            {
            }
        };

        expectSuccessfulFrame(new Message[] { message });
        mockery.checking(new Expectations()
        {
            {
                oneOf(messageProcessor).execute(with(same(message)), with(any(List.class)));
                will(returnValue(true));

                oneOf(message).setFlag(Flag.DELETED, true);
            }
        });

        sut.execute();
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Won't.
     * @throws IOException
     *             Won't.
     */
    @Test
    public void testExecuteFailMsg() throws MessagingException, IOException
    {
        expectSuccessfulFrame(new Message[] { message });

        mockery.checking(new Expectations()
        {
            {
                oneOf(messageProcessor).execute(with(same(message)), with(any(List.class)));
                will(new CustomAction("simulate error")
                {
                    @Override
                    public Object invoke(final Invocation inInvocation) throws Throwable
                    {
                        ((List<Message>) inInvocation.getParameter(1)).add(responseMessage);
                        throw new ValidationException();
                    }
                });

                oneOf(inputFolder).copyMessages(with(IsArrayContaining.hasItemInArray(message)),
                        with(same(errorFolder)));
                oneOf(message).setFlag(Flag.DELETED, true);
                oneOf(emailerFactory).sendMail(with(same(responseMessage)));
            }
        });

        sut.execute();
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Won't.
     * @throws IOException
     *             Won't.
     */
    @Test
    public void testExecuteFailMsgNoCopy() throws MessagingException, IOException
    {
        sut = new ImapEmailIngester(storeFactory, messageProcessor, emailerFactory, INPUT_FOLDER_NAME, "",
                SUCCESS_FOLDER_NAME, " ")
        {
            @Override
            protected void dumpMessage(final Message inMessage, final int inIndex)
            {
            }
        };
        expectSuccessfulFrame(new Message[] { message });

        mockery.checking(new Expectations()
        {
            {
                oneOf(messageProcessor).execute(with(same(message)), with(any(List.class)));
                will(throwException(new MessagingException()));

                oneOf(message).setFlag(Flag.DELETED, true);
            }
        });

        sut.execute();
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Won't.
     * @throws IOException
     *             Won't.
     */
    @Test
    public void testExecuteDiscard() throws MessagingException, IOException
    {
        expectSuccessfulFrame(new Message[] { message });
        mockery.checking(new Expectations()
        {
            {
                oneOf(messageProcessor).execute(with(same(message)), with(any(List.class)));
                will(returnValue(false));

                oneOf(inputFolder).copyMessages(with(IsArrayContaining.hasItemInArray(message)),
                        with(same(discardFolder)));
                oneOf(message).setFlag(Flag.DELETED, true);
            }
        });

        sut.execute();
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Won't.
     * @throws IOException
     *             Won't.
     */

    @Test
    public void testExecuteDiscardNoCopy() throws MessagingException, IOException
    {
        sut = new ImapEmailIngester(storeFactory, messageProcessor, emailerFactory, INPUT_FOLDER_NAME, null, null,
                null)
        {
            @Override
            protected void dumpMessage(final Message inMessage, final int inIndex)
            {
            }
        };

        expectSuccessfulFrame(new Message[] { message });
        mockery.checking(new Expectations()
        {
            {
                oneOf(messageProcessor).execute(with(same(message)), with(any(List.class)));
                will(returnValue(false));

                oneOf(message).setFlag(Flag.DELETED, true);
            }
        });

        sut.execute();
        mockery.assertIsSatisfied();
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
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
        {
            {
                oneOf(storeFactory).getStore();
                will(throwException(new MessagingException()));
            }
        });

        sut.execute();
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
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
        mockery.assertIsSatisfied();
    }

    /**
     * Tests error condition.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testExecuteBadDiscardFolder() throws MessagingException
    {
        mockery.checking(new Expectations()
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
                allowing(store).getFolder(DISCARD_FOLDER_NAME);
                will(returnValue(discardFolder));
                allowing(discardFolder).exists();
                will(returnValue(false));

                oneOf(store).close();
            }
        });

        sut.execute();
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
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
                allowing(store).getFolder(DISCARD_FOLDER_NAME);
                will(returnValue(discardFolder));
                allowing(discardFolder).exists();
                will(returnValue(true));

                oneOf(store).close();
            }
        });

        sut.execute();
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
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
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
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
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
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
        mockery.assertIsSatisfied();
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
        mockery.checking(new Expectations()
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
        mockery.assertIsSatisfied();
    }
}
