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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.commons.test.EasyMatcher;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

/**
 * Tests MessageProcessor.
 */
@SuppressWarnings("unchecked")
public class MessageProcessorTest
{
    /** Test data. */
    private static final Long PERSON_ID = 100L;

    /** Test data. */
    private static final String PERSON_ACCOUNT_ID = "jdoe";

    /** Test data. */
    private static final String PERSON_OS_ID = "1234567890ABCDEF";

    /** Test data. */
    private static final String ACTION_NAME = "TheAction";

    /** Test data. */
    private static final String AT_DOMAIN = "@eurekastreams.org";

    /** Test data. */
    private static final String SENDER_ADDRESS = "john.doe@eurekastreams.org";

    /** Test data. */
    private static final String OTHER_ADDRESS = "someone.else@eurekastreams.org";

    /** Test data. */
    private static final String SYSTEM_ADDRESS = "system@eurekastreams.org";

    /** Test data. */
    private static final String TOKEN = "ThisIsAToken";

    /** Test data. */
    private static final String TOKEN_CONTENT = "ThisIsDataInAToken";

    /** Test data. */
    private static final byte[] KEY = "ThisIsAKey".getBytes();

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** For getting the user's content from the message. */
    private final MessageContentExtractor messageContentExtractor = context.mock(MessageContentExtractor.class,
            "messageContentExtractor");

    /** Determines which action to execute. */
    private final ActionSelector actionSelector = context.mock(ActionSelector.class, "actionSelector");

    /** Instance of {@link ActionController} used to run actions. */
    private final ActionController serviceActionController = context.mock(ActionController.class,
            "serviceActionController");

    /** The context from which this service can load action beans. */
    private final BeanFactory beanFactory = context.mock(BeanFactory.class, "beanFactory");

    /** For decoding the token. */
    private final TokenEncoder tokenEncoder = context.mock(TokenEncoder.class, "tokenEncoder");

    /** Parses the token content. */
    private final TokenContentFormatter tokenContentFormatter = context.mock(TokenContentFormatter.class,
            "tokenContentFormatter");

    /** Transaction manager (to allow calling mappers). */
    private final PlatformTransactionManager transactionMgr = context.mock(PlatformTransactionManager.class,
            "transactionMgr");

    /** DAO to get a user's person ID given their email address. */
    private final DomainMapper<String, Long> personIdByEmailDao = context.mock(DomainMapper.class,
            "personIdByEmailDao");

    /** DAO to get a user's crypto key given their person ID. */
    private final DomainMapper<Long, byte[]> userKeyByIdDao = context.mock(DomainMapper.class, "userKeyByIdDao");

    /** DAO to get a person by ID. */
    private final DomainMapper<Long, PersonModelView> personDao = context.mock(DomainMapper.class, "personDao");

    /** Responds to messages that failed execution with result status. */
    private final MessageReplier messageReplier = context.mock(MessageReplier.class, "messageReplier");

    /** Fixture: message. */
    private final Message message = context.mock(Message.class);

    /** Fixture: taskHandlerServiceAction. */
    private final TaskHandlerServiceAction taskHandlerServiceAction = context.mock(TaskHandlerServiceAction.class,
            "taskHandlerServiceAction");

    /** Fixture: serviceAction. */
    private final ServiceAction serviceAction = context.mock(ServiceAction.class, "serviceAction");

    /** Fixture. */
    PersonModelView person = context.mock(PersonModelView.class, "person");

    /** SUT. */
    private MessageProcessor sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new MessageProcessor(messageContentExtractor, actionSelector, serviceActionController, beanFactory,
                tokenEncoder, tokenContentFormatter, transactionMgr, personIdByEmailDao, userKeyByIdDao, personDao,
                messageReplier, SYSTEM_ADDRESS);
        context.checking(new Expectations()
        {
            {
                allowing(person).getId();
                will(returnValue(PERSON_ID));
                allowing(person).getAccountId();
                will(returnValue(PERSON_ACCOUNT_ID));
                allowing(person).getOpenSocialId();
                will(returnValue(PERSON_OS_ID));
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
    public void testGetFromOk() throws MessagingException
    {
        context.checking(new Expectations()
        {
            {
                allowing(message).getFrom();
                will(returnValue(new Address[] { new InternetAddress(SENDER_ADDRESS) }));
            }
        });
        assertEquals(SENDER_ADDRESS, sut.getFromAddress(message));
        context.assertIsSatisfied();
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test(expected = Exception.class)
    public void testGetFromNone1() throws MessagingException
    {
        context.checking(new Expectations()
        {
            {
                allowing(message).getFrom();
                will(returnValue(new Address[0]));
            }
        });
        sut.getFromAddress(message);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test(expected = Exception.class)
    public void testGetFromNone2() throws MessagingException
    {
        context.checking(new Expectations()
        {
            {
                allowing(message).getFrom();
                will(returnValue(null));
            }
        });
        sut.getFromAddress(message);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test(expected = Exception.class)
    public void testGetFromNone3() throws MessagingException
    {
        context.checking(new Expectations()
        {
            {
                allowing(message).getFrom();
                will(returnValue(new Address[] { null }));
            }
        });
        sut.getFromAddress(message);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test(expected = Exception.class)
    public void testGetFromTooMany() throws MessagingException
    {
        context.checking(new Expectations()
        {
            {
                allowing(message).getFrom();
                will(returnValue(new Address[] { new InternetAddress(SENDER_ADDRESS),
                        new InternetAddress(OTHER_ADDRESS) }));
            }
        });
        sut.getFromAddress(message);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test(expected = Exception.class)
    public void testGetTokenNone() throws MessagingException
    {
        context.checking(new Expectations()
        {
            {
                allowing(message).getRecipients(RecipientType.TO);
                will(returnValue(null));
            }
        });
        sut.getToken(message);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test(expected = Exception.class)
    public void testGetTokenNotFound() throws MessagingException
    {
        context.checking(new Expectations()
        {
            {
                allowing(message).getRecipients(RecipientType.TO);
                will(returnValue(new Address[] { new InternetAddress(OTHER_ADDRESS),
                        new InternetAddress(SYSTEM_ADDRESS), new InternetAddress("system+ABC" + AT_DOMAIN) }));
                allowing(tokenEncoder).couldBeToken("ABC");
                will(returnValue(false));
            }
        });
        sut.getToken(message);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     *
     * @throws MessagingException
     *             Won't.
     */
    @Test
    public void testGetTokenOk() throws MessagingException
    {
        context.checking(new Expectations()
        {
            {
                allowing(message).getRecipients(RecipientType.TO);
                will(returnValue(new Address[] { new InternetAddress(OTHER_ADDRESS),
                        new InternetAddress(SYSTEM_ADDRESS), new InternetAddress("system+ABC" + AT_DOMAIN),
                        new InternetAddress("system+XYZ" + AT_DOMAIN), new InternetAddress(SENDER_ADDRESS) }));
                allowing(tokenEncoder).couldBeToken("ABC");
                will(returnValue(false));
                allowing(tokenEncoder).couldBeToken("XYZ");
                will(returnValue(true));
            }
        });
        assertEquals("XYZ", sut.getToken(message));
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testExecuteActionSuccessBasic()
    {
        final UserActionRequest sel = new UserActionRequest(ACTION_NAME, null, "");
        context.checking(new Expectations()
        {
            {
                allowing(beanFactory).getBean(ACTION_NAME);
                will(returnValue(serviceAction));
                oneOf(serviceActionController).execute(with(new EasyMatcher<ServiceActionContext>()
                {
                    @Override
                    protected boolean isMatch(final ServiceActionContext inTestObject)
                    {
                        return ACTION_NAME.equals(inTestObject.getActionId()) && "".equals(inTestObject.getParams());
                    }
                }), with(same(serviceAction)));
            }
        });
        sut.executeAction(message, sel, person);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = ExecutionException.class)
    public void testExecuteActionErrorBasic()
    {
        final UserActionRequest sel = new UserActionRequest(ACTION_NAME, null, "");
        context.checking(new Expectations()
        {
            {
                allowing(beanFactory).getBean(ACTION_NAME);
                will(returnValue(serviceAction));
                oneOf(serviceActionController).execute(with(new EasyMatcher<ServiceActionContext>()
                {
                    @Override
                    protected boolean isMatch(final ServiceActionContext inTestObject)
                    {
                        return ACTION_NAME.equals(inTestObject.getActionId()) && "".equals(inTestObject.getParams());
                    }
                }), with(same(serviceAction)));
                will(throwException(new ExecutionException()));
                oneOf(messageReplier).reply(message, person, sel);
            }
        });
        sut.executeAction(message, sel, person);
        context.assertIsSatisfied();
    }


    /**
     * Test.
     */
    @Test
    public void testExecuteActionSuccessTaskHandler()
    {
        final UserActionRequest sel = new UserActionRequest(ACTION_NAME, null, "");
        context.checking(new Expectations()
        {
            {
                allowing(beanFactory).getBean(ACTION_NAME);
                will(returnValue(taskHandlerServiceAction));
                oneOf(serviceActionController).execute(with(new EasyMatcher<ServiceActionContext>()
                {
                    @Override
                    protected boolean isMatch(final ServiceActionContext inTestObject)
                    {
                        return ACTION_NAME.equals(inTestObject.getActionId()) && "".equals(inTestObject.getParams());
                    }
                }), with(same(taskHandlerServiceAction)));
            }
        });
        sut.executeAction(message, sel, person);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = ExecutionException.class)
    public void testExecuteActionErrorTaskHandler()
    {
        final UserActionRequest sel = new UserActionRequest(ACTION_NAME, null, "");
        context.checking(new Expectations()
        {
            {
                allowing(beanFactory).getBean(ACTION_NAME);
                will(returnValue(taskHandlerServiceAction));
                oneOf(serviceActionController).execute(with(new EasyMatcher<ServiceActionContext>()
                {
                    @Override
                    protected boolean isMatch(final ServiceActionContext inTestObject)
                    {
                        return ACTION_NAME.equals(inTestObject.getActionId()) && "".equals(inTestObject.getParams());
                    }
                }), with(same(taskHandlerServiceAction)));
                will(throwException(new ExecutionException()));
                oneOf(messageReplier).reply(message, person, sel);
            }
        });
        sut.executeAction(message, sel, person);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = ExecutionException.class)
    public void testExecuteActionBadBean()
    {
        final UserActionRequest sel = new UserActionRequest(ACTION_NAME, null, "");
        context.checking(new Expectations()
        {
            {
                allowing(beanFactory).getBean(ACTION_NAME);
                will(returnValue(""));
                oneOf(messageReplier).reply(message, person, sel);
            }
        });
        sut.executeAction(message, sel, person);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testGetTokenData()
    {
        final Map data = context.mock(Map.class);
        context.checking(new Expectations()
        {
            {
                oneOf(tokenEncoder).decode(TOKEN, KEY);
                will(returnValue(TOKEN_CONTENT));
                oneOf(tokenContentFormatter).parse(TOKEN_CONTENT);
                will(returnValue(data));
            }
        });

        assertSame(data, sut.getTokenData(TOKEN, KEY));
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = Exception.class)
    public void testGetTokenDataErrorDecrypt()
    {
        final Map data = context.mock(Map.class);
        context.checking(new Expectations()
        {
            {
                oneOf(tokenEncoder).decode(TOKEN, KEY);
                will(returnValue(null));
            }
        });

        sut.getTokenData(TOKEN, KEY);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = Exception.class)
    public void testGetTokenDataErrorParse()
    {
        final Map data = context.mock(Map.class);
        context.checking(new Expectations()
        {
            {
                oneOf(tokenEncoder).decode(TOKEN, KEY);
                will(returnValue(TOKEN_CONTENT));
                oneOf(tokenContentFormatter).parse(TOKEN_CONTENT);
                will(returnValue(null));
            }
        });

        sut.getTokenData(TOKEN, KEY);
        context.assertIsSatisfied();
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
    public void testExecute() throws MessagingException, IOException
    {
        final Map data = context.mock(Map.class);
        final TransactionStatus transaction = context.mock(TransactionStatus.class);
        final States state = context.states("trans").startsAs("none");
        final String content = "This is the content";
        final Serializable params = context.mock(Serializable.class, "params");
        final UserActionRequest selection = new UserActionRequest(ACTION_NAME, null, params);

        context.checking(new Expectations()
        {
            {
                allowing(message).getFrom();
                will(returnValue(new Address[] { new InternetAddress(SENDER_ADDRESS) }));

                allowing(message).getRecipients(RecipientType.TO);
                will(returnValue(new Address[] { new InternetAddress("system+" + TOKEN + AT_DOMAIN) }));
                allowing(tokenEncoder).couldBeToken(TOKEN);
                will(returnValue(true));

                oneOf(transactionMgr).getTransaction(with(any(TransactionDefinition.class)));
                when(state.is("none"));
                will(returnValue(transaction));
                then(state.is("in"));

                allowing(personIdByEmailDao).execute(SENDER_ADDRESS);
                when(state.is("in"));
                will(returnValue(PERSON_ID));

                allowing(userKeyByIdDao).execute(PERSON_ID);
                when(state.is("in"));
                will(returnValue(KEY));

                allowing(personDao).execute(PERSON_ID);
                when(state.is("in"));
                will(returnValue(person));

                oneOf(transactionMgr).commit(transaction);
                when(state.is("in"));
                then(state.is("none"));

                oneOf(tokenEncoder).decode(TOKEN, KEY);
                will(returnValue(TOKEN_CONTENT));
                oneOf(tokenContentFormatter).parse(TOKEN_CONTENT);
                will(returnValue(data));

                allowing(messageContentExtractor).extract(message);
                will(returnValue(content));

                oneOf(actionSelector).select(data, content, person);
                will(returnValue(selection));

                allowing(beanFactory).getBean(ACTION_NAME);
                will(returnValue(serviceAction));
                oneOf(serviceActionController).execute(with(new EasyMatcher<ServiceActionContext>()
                {
                    @Override
                    protected boolean isMatch(final ServiceActionContext inTestObject)
                    {
                        return ACTION_NAME.equals(inTestObject.getActionId()) && params == inTestObject.getParams();
                    }
                }), with(same(serviceAction)));
            }
        });

        sut.execute(message);
        context.assertIsSatisfied();
    }
}
