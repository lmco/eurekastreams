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
package org.eurekastreams.server.action.validation;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.eurekastreams.commons.exceptions.ValidationException;

/**
 * Used to Validate a Email address.
 *
 */
public class EmailAddressValidator
{

    /**
     * @param emailAddress
     *            email to test.
     * @throws ValidationException
     *             if the email does not format to a proper email.
     */
    void validate(final String emailAddress) throws ValidationException
    {
        try
        {
            InternetAddress emailAddr = new InternetAddress(emailAddress);
            if (!fullAddress(emailAddress))
            {
                throw new ValidationException("Please enter a properly formatted email address.");
            }
        }
        catch (AddressException ex)
        {
            throw new ValidationException("Please provide a valid email address.");
        }
    }

    /**
     * @param inEmailAddress
     *            email to test.
     * @return checks to see if email has a domain.
     */
    private boolean fullAddress(final String inEmailAddress)
    {
        String[] addressTokens = inEmailAddress.split("@");
        return addressTokens.length == 2;
    }
}
