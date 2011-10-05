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

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

import com.sun.mail.imap.IMAPSSLStore;

/**
 * Returns a connected IMAP message store.
 */
public class ImapStoreFactory
{
    /** Properties for setting up the connection to the mail server. */
    private final Properties sessionProperties;

    /** Mail server name. */
    private final String mailServerName;

    /** Mail server port. */
    private final int mailServerPort;

    /** Username for connecting to the mail server. */
    private final String mailUsername;

    /** Password for connecting to the mail server. */
    private final String mailPassword;


    /**
     * Constructor.
     * 
     * @param inMailServerName
     *            Mail server name.
     * @param inMailServerPort
     *            Mail server port.
     * @param inMailUsername
     *            Username for connecting to the mail server.
     * @param inMailPassword
     *            Password for connecting to the mail server.
     * @param inSessionProperties
     *            Properties for setting up the connection to the mail server.
     */
    public ImapStoreFactory(final String inMailServerName, final int inMailServerPort, final String inMailUsername,
            final String inMailPassword, final Properties inSessionProperties)
    {
        mailServerName = inMailServerName;
        mailServerPort = inMailServerPort;
        mailUsername = inMailUsername;
        mailPassword = inMailPassword;
        sessionProperties = inSessionProperties;
    }

    /**
     * Connects to the message store (mail server).
     * 
     * @return Message store.
     * @throws MessagingException
     *             On connection failure.
     */
    public Store getStore() throws MessagingException
    {
        // build the URL
        URLName url = new URLName("imap", mailServerName, mailServerPort, "", mailUsername, mailPassword);

        Session session = Session.getInstance(sessionProperties, null);

        Store store = new IMAPSSLStore(session, url);
        store.connect();

        return store;
    }
}
