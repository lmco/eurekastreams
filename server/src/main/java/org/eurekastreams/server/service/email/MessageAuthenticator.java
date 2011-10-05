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

import java.util.Map;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Authenticates/validates a message using the From and To addresses and the token in the To address.
 */
public class MessageAuthenticator
{
    /** For decoding the token. */
    private final TokenEncoder tokenEncoder;

    /** Text address must begin with to be the desired To address. */
    private final String toEmailRequiredStart;

    /** Text address must end with to be the desired To address. */
    private final String toEmailRequiredEnd;

    /** DAO to get a user's crypto key given their email address. */
    private final DomainMapper<String, byte[]> userKeyByEmailDao;

    /** Transaction manager (to allow calling mappers). */
    private final PlatformTransactionManager transactionMgr;

    /**
     * Constructor.
     *
     * @param inRequiredToAddress
     *            Address messages must be sent to.
     * @param inTokenEncoder
     *            For decoding the token.
     * @param inUserKeyByEmailDao
     *            DAO to get a user's crypto key given their email address.
     * @param inTransactionMgr
     *            Transaction manager (to allow calling mappers).
     */
    public MessageAuthenticator(final String inRequiredToAddress, final TokenEncoder inTokenEncoder,
            final DomainMapper<String, byte[]> inUserKeyByEmailDao, final PlatformTransactionManager inTransactionMgr)
    {
        tokenEncoder = inTokenEncoder;
        userKeyByEmailDao = inUserKeyByEmailDao;
        transactionMgr = inTransactionMgr;

        int pos = inRequiredToAddress.indexOf('@');
        toEmailRequiredStart = inRequiredToAddress.substring(0, pos) + "+";
        toEmailRequiredEnd = inRequiredToAddress.substring(pos);
    }

    /**
     * Authenticates the message.
     * 
     * @param message
     *            Message to check.
     * @return Message meta data.
     * @throws MessagingException
     *             On appropriate errors.
     */
    public MessageAuthenticationResult authenticate(final Message message) throws MessagingException
    {
        // insure the message has a From address
        Address[] addresses = message.getFrom();
        if (addresses == null || addresses.length != 1 || addresses[0] == null)
        {
            throw new ValidationException("Message must contain a single From address.");
        }
        String fromAddress = ((InternetAddress) addresses[0]).getAddress();

        // insure the message has a To address which 1) matches the expected system address, and 2) has an address tag
        addresses = message.getRecipients(RecipientType.TO);
        String token = null;
        if (addresses != null)
        {
            for (int i = 0; i < addresses.length; i++)
            {
                String addr = ((InternetAddress) addresses[i]).getAddress();
                if (addr.startsWith(toEmailRequiredStart) && addr.endsWith(toEmailRequiredEnd))
                {
                    String middle = addr.substring(toEmailRequiredStart.length(),
                            addr.length() - toEmailRequiredEnd.length());
                    if (tokenEncoder.couldBeToken(middle))
                    {
                        token = middle;
                        break;
                    }
                }
            }
        }
        if (token == null)
        {
            throw new ValidationException("Cannot find To address for the system with an address tag.");
        }

        // get the user's key
        DefaultTransactionDefinition transDef = new DefaultTransactionDefinition();
        transDef.setName("TokenAddressMessageAuthenticator");
        transDef.setReadOnly(false);
        TransactionStatus transStatus = transactionMgr.getTransaction(transDef);
        byte[] key;
        try
        {
            key = userKeyByEmailDao.execute(fromAddress);
        }
        finally
        {
            transactionMgr.commit(transStatus);
        }

        // unpack the token
        Map<String, Long> tokenData = tokenEncoder.decode(token, key);
        if (tokenData == null)
        {
            throw new ValidationException("Cannot decode token for user.");
        }

        return new MessageAuthenticationResult(tokenData, fromAddress);
    }
}
