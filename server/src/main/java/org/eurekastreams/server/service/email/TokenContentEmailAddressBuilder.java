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

import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Convenience class for building the recipient email address a user uses to interact with the system via email.
 *
 * Must be used from within a transaction to allow access to the database.
 */
public class TokenContentEmailAddressBuilder
{
    /** Front portion of the email address monitored by the system for ingesting email. */
    private final String toEmailStart;

    /** Rear portion of the email address monitored by the system for ingesting email. */
    private final String toEmailEnd;

    /** Creates the token. */
    private final TokenEncoder tokenEncoder;

    /** Gets the user's key. */
    private final DomainMapper<Long, byte[]> cryptoKeyDao;

    /**
     * Constructor.
     *
     * @param inTokenEncoder
     *            Creates the token.
     * @param inCryptoKeyDao
     *            Gets the user's key.
     * @param inToAddress
     *            Destination email address (monitored by the system for ingesting email).
     */
    public TokenContentEmailAddressBuilder(final TokenEncoder inTokenEncoder,
            final DomainMapper<Long, byte[]> inCryptoKeyDao, final String inToAddress)
    {
        tokenEncoder = inTokenEncoder;
        cryptoKeyDao = inCryptoKeyDao;

        int pos = inToAddress.indexOf('@');
        toEmailStart = inToAddress.substring(0, pos) + "+";
        toEmailEnd = inToAddress.substring(pos);
    }

    /**
     * Builds the email address with an embedded token.
     *
     * @param tokenContent
     *            Pre-formatted token content.
     * @param userId
     *            User whose key will sign the token.
     * @return Email address.
     */
    public String build(final String tokenContent, final long userId)
    {
        byte[] key = cryptoKeyDao.execute(userId);
        String token = tokenEncoder.encode(tokenContent, key);
        return toEmailStart + token + toEmailEnd;
    }
}
