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
import java.util.Map;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.eurekastreams.commons.actions.context.DefaultPrincipal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Responsible for all processing of a single received email message.
 */
public class MessageProcessor
{
    /** For getting the user's content from the message. */
    private final MessageContentExtractor messageContentExtractor;

    /** Determines which action to execute. */
    private final ActionSelectorFactory actionSelector;

    /** Instance of {@link ActionController} used to run actions. */
    private final ActionController serviceActionController;

    /** The context from which this service can load action beans. */
    private final BeanFactory beanFactory;

    /** For decoding the token. */
    private final TokenEncoder tokenEncoder;

    /** Parses the token content. */
    private final TokenContentFormatter tokenContentFormatter;

    /** Transaction manager (to allow calling mappers). */
    private final PlatformTransactionManager transactionMgr;

    /** DAO to get a user's person ID given their email address. */
    private final DomainMapper<String, Long> personIdByEmailDao;

    /** DAO to get a user's crypto key given their person ID. */
    private final DomainMapper<Long, byte[]> userKeyByIdDao;

    /** DAO to get a person by ID. */
    private final DomainMapper<Long, PersonModelView> personDao;

    /** Responds to messages that failed execution with result status. */
    private final MessageReplier messageReplier;

    /** Address messages must be sent to. */
    private final String requiredToAddress;

    /** Text address must begin with to be the desired To address. */
    private final String toEmailRequiredStart;

    /** Text address must end with to be the desired To address. */
    private final String toEmailRequiredEnd;

    /**
     * Constructor.
     *
     * @param inMessageContentExtractor
     *            For getting the user's content from the message.
     * @param inActionSelector
     *            Determines which action to execute.
     * @param inServiceActionController
     *            Instance of {@link ActionController} used to run actions.
     * @param inBeanFactory
     *            The context from which this service can load action beans.
     * @param inTokenEncoder
     *            For decoding the token.
     * @param inTokenContentFormatter
     *            Parses the token content.
     * @param inTransactionMgr
     *            Transaction manager (to allow calling mappers).
     * @param inPersonIdByEmailDao
     *            DAO to get a user's person ID given their email address.
     * @param inUserKeyByIdDao
     *            DAO to get a user's crypto key given their person ID.
     * @param inPersonDao
     *            DAO to get a person by ID.
     * @param inMessageReplier
     *            Responds to messages that failed execution with result status.
     * @param inRequiredToAddress
     *            Address messages must be sent to.
     */
    public MessageProcessor(final MessageContentExtractor inMessageContentExtractor,
            final ActionSelectorFactory inActionSelector, final ActionController inServiceActionController,
            final BeanFactory inBeanFactory, final TokenEncoder inTokenEncoder,
            final TokenContentFormatter inTokenContentFormatter, final PlatformTransactionManager inTransactionMgr,
            final DomainMapper<String, Long> inPersonIdByEmailDao, final DomainMapper<Long, byte[]> inUserKeyByIdDao,
            final DomainMapper<Long, PersonModelView> inPersonDao, final MessageReplier inMessageReplier,
            final String inRequiredToAddress)
    {
        messageContentExtractor = inMessageContentExtractor;
        actionSelector = inActionSelector;
        serviceActionController = inServiceActionController;
        beanFactory = inBeanFactory;
        tokenEncoder = inTokenEncoder;
        tokenContentFormatter = inTokenContentFormatter;
        transactionMgr = inTransactionMgr;
        personIdByEmailDao = inPersonIdByEmailDao;
        userKeyByIdDao = inUserKeyByIdDao;
        personDao = inPersonDao;
        messageReplier = inMessageReplier;

        requiredToAddress = inRequiredToAddress;
        int pos = inRequiredToAddress.indexOf('@');
        toEmailRequiredStart = inRequiredToAddress.substring(0, pos) + "+";
        toEmailRequiredEnd = inRequiredToAddress.substring(pos);
    }

    /**
     * Processes the message.
     *
     * @param message
     *            Message to process.
     * @param inResponseMessages
     *            List to add response messages to.
     * @return If message was processed.
     * @throws MessagingException
     *             On error.
     * @throws IOException
     *             On error.
     */
    public boolean execute(final Message message, final List<Message> inResponseMessages) throws MessagingException,
            IOException
    {
        String token = getToken(message);
        if (token == null)
        {
            return false;
        }
        String fromAddress = getFromAddress(message);

        // get the sender and sender's key
        DefaultTransactionDefinition transDef = new DefaultTransactionDefinition();
        transDef.setName("TokenAddressMessageAuthenticator");
        transDef.setReadOnly(false);
        TransactionStatus transStatus = transactionMgr.getTransaction(transDef);
        byte[] key;
        Long personId;
        PersonModelView person;
        try
        {
            personId = personIdByEmailDao.execute(fromAddress);
            key = userKeyByIdDao.execute(personId);
            person = personDao.execute(personId);
        }
        finally
        {
            transactionMgr.commit(transStatus);
        }

        // decrypt and unpack the token
        Map<String, Long> tokenData = getTokenData(token, key);

        // get the message content
        String content = messageContentExtractor.extract(message);

        // choose action to execute
        UserActionRequest actionSelection = actionSelector.select(tokenData, content, person);

        // execute action
        executeAction(message, actionSelection, person, inResponseMessages);
        return true;
    }

    /**
     * Decrypts and unpacks the token.
     *
     * @param token
     *            Raw token.
     * @param key
     *            User's key.
     * @return Data contained in token.
     */
    Map<String, Long> getTokenData(final String token, final byte[] key)
    {
        String tokenConent = tokenEncoder.decode(token, key);
        if (tokenConent == null)
        {
            throw new ValidationException("Cannot decrypt token for user.");
        }
        Map<String, Long> tokenData = tokenContentFormatter.parse(tokenConent);
        if (tokenData == null)
        {
            throw new ValidationException("Cannot parse token.");
        }
        return tokenData;
    }

    /**
     * Execute the selected action.
     *
     * @param message
     *            The original message.
     * @param actionSelection
     *            The selected action.
     * @param person
     *            The user.
     * @param inResponseMessages
     *            List to add response messages to.
     */
    void executeAction(final Message message, final UserActionRequest actionSelection, final PersonModelView person,
            final List<Message> inResponseMessages)
    {
        try
        {
            Object springBean = beanFactory.getBean(actionSelection.getActionKey());
            PrincipalActionContext actionContext = new ServiceActionContext(actionSelection.getParams(),
                    new DefaultPrincipal(person.getAccountId(), person.getOpenSocialId(), person.getId()));
            actionContext.setActionId(actionSelection.getActionKey());
            if (springBean instanceof ServiceAction)
            {
                ServiceAction action = (ServiceAction) springBean;
                serviceActionController.execute(actionContext, action);
            }
            else if (springBean instanceof TaskHandlerServiceAction)
            {
                TaskHandlerServiceAction action = (TaskHandlerServiceAction) springBean;
                serviceActionController.execute(actionContext, action);
            }
            else
            {
                throw new ExecutionException("Bean '" + actionSelection.getActionKey()
                        + "' is not an executable action");
            }
        }
        catch (RuntimeException ex)
        {
            // notify user on failure
            // Note: A response is only sent for errors processing the action (which could be due to missing content
            // from the message). This is because any errors encountered prior represent a bad sender or ill-formed
            // message or token and thus represent a suspicious message. In that case we don't want to send a reply, for
            // security.
            messageReplier.reply(message, person, actionSelection, ex, inResponseMessages);
            throw ex;
        }
    }

    /**
     * Extracts the FROM address from the message.
     *
     * @param message
     *            The message.
     * @return The FROM address.
     * @throws MessagingException
     *             On error.
     */
    String getFromAddress(final Message message) throws MessagingException
    {
        // insure the message has a From address
        Address[] addresses = message.getFrom();
        if (addresses == null || addresses.length != 1 || addresses[0] == null)
        {
            throw new ValidationException("Message must contain a single From address.");
        }
        return ((InternetAddress) addresses[0]).getAddress();
    }

    /**
     * Extracts the token from the message.
     *
     * @param message
     *            The message.
     * @return The token.
     * @throws MessagingException
     *             On error.
     */
    String getToken(final Message message) throws MessagingException
    {
        // insure the message has a To address which 1) matches the expected system address, and 2) has an address tag
        Address[] addresses = message.getRecipients(RecipientType.TO);
        if (addresses != null)
        {
            boolean noReplyFound = false;
            for (int i = 0; i < addresses.length; i++)
            {
                String addr = ((InternetAddress) addresses[i]).getAddress();

                // check for token-less system address: no-reply
                if (requiredToAddress.equals(addr))
                {
                    noReplyFound = true;
                }
                // check for token
                else if (addr.startsWith(toEmailRequiredStart) && addr.endsWith(toEmailRequiredEnd))
                {
                    String middle = addr.substring(toEmailRequiredStart.length(),
                            addr.length() - toEmailRequiredEnd.length());
                    if (tokenEncoder.couldBeToken(middle))
                    {
                        return middle;
                    }
                }
            }
            if (noReplyFound)
            {
                return null;
            }
        }
        throw new ValidationException("Cannot find To address for the system with an address tag.");
    }
}
