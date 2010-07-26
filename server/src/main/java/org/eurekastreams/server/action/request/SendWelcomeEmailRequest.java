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
package org.eurekastreams.server.action.request;

import java.io.Serializable;

/**
 * Request that contains email address and account id.
 */
public class SendWelcomeEmailRequest implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 1569185871258246062L;

    /**
     * Email address of person to notify.
     */
    private String emailAddress;

    /**
     * Account name of person to notify.
     */
    private String accountId;

    /**
     * Empty constructor.
     */
    public SendWelcomeEmailRequest()
    {
    }

    /**
     * Constructor.
     * 
     * @param inEmailAddress
     *            the email address.
     * @param inAccountId
     *            the account id.
     */
    public SendWelcomeEmailRequest(final String inEmailAddress, final String inAccountId)
    {
        setEmailAddress(inEmailAddress);
        setAccountId(inAccountId);
    }

    /**
     * @param inEmailAddress
     *            the emailAddress to set
     */
    public void setEmailAddress(final String inEmailAddress)
    {
        emailAddress = inEmailAddress;
    }

    /**
     * @return the emailAddress
     */
    public String getEmailAddress()
    {
        return emailAddress;
    }

    /**
     * @param inAccountId
     *            the accountId to set
     */
    public void setAccountId(final String inAccountId)
    {
        accountId = inAccountId;
    }

    /**
     * @return the accountId
     */
    public String getAccountId()
    {
        return accountId;
    }

}
