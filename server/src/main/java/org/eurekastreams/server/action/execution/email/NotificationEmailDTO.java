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
package org.eurekastreams.server.action.execution.email;

import java.io.Serializable;
import java.util.Collection;

/**
 * Holds information to build an email.
 */
public class NotificationEmailDTO implements Serializable
{
    /** Subject line. */
    private String subject;

    /** Content of the text version of the body. */
    private String textBody;

    /** Content of the HTML version of the body. */
    private String htmlBody;

    /** Address of a single TO recipient. */
    private String toRecipient;

    /** Addresses of BCC recipients. */
    private Collection<String> bccRecipients;

    /**
     * @return the subject
     */
    public String getSubject()
    {
        return subject;
    }

    /**
     * @param inSubject
     *            the subject to set
     */
    public void setSubject(final String inSubject)
    {
        subject = inSubject;
    }

    /**
     * @return the textBody
     */
    public String getTextBody()
    {
        return textBody;
    }

    /**
     * @param inTextBody
     *            the textBody to set
     */
    public void setTextBody(final String inTextBody)
    {
        textBody = inTextBody;
    }

    /**
     * @return the htmlBody
     */
    public String getHtmlBody()
    {
        return htmlBody;
    }

    /**
     * @param inHtmlBody
     *            the htmlBody to set
     */
    public void setHtmlBody(final String inHtmlBody)
    {
        htmlBody = inHtmlBody;
    }

    /**
     * @return the toRecipient
     */
    public String getToRecipient()
    {
        return toRecipient;
    }

    /**
     * @param inToRecipient
     *            the toRecipient to set
     */
    public void setToRecipient(final String inToRecipient)
    {
        toRecipient = inToRecipient;
    }

    /**
     * @return the bccRecipients
     */
    public Collection<String> getBccRecipients()
    {
        return bccRecipients;
    }

    /**
     * @param inBccRecipients
     *            the bccRecipients to set
     */
    public void setBccRecipients(final Collection<String> inBccRecipients)
    {
        bccRecipients = inBccRecipients;
    }
}
