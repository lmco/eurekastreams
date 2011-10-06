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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.service.email.TokenContentFormatter;
import org.eurekastreams.server.service.email.TokenEncoder;

/**
 * Gets a token for the current user for posting to an activity.
 */
public class GetTokenForActivityExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /** Creates the token. */
    private final TokenEncoder tokenEncoder;

    /** Builds the token content. */
    private final TokenContentFormatter tokenContentFormatter;

    /** Gets the user's key. */
    private final DomainMapper<Long, byte[]> cryptoKeyDao;

    /**
     * Constructor.
     *
     * @param inTokenEncoder
     *            Creates the token.
     * @param inTokenContentFormatter
     *            Builds the token content.
     * @param inCryptoKeyDao
     *            Gets the user's key.
     */
    public GetTokenForActivityExecution(final TokenEncoder inTokenEncoder,
            final TokenContentFormatter inTokenContentFormatter, final DomainMapper<Long, byte[]> inCryptoKeyDao)
    {
        tokenEncoder = inTokenEncoder;
        tokenContentFormatter = inTokenContentFormatter;
        cryptoKeyDao = inCryptoKeyDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        Long personId = inActionContext.getPrincipal().getId();
        Long activityId = (Long) inActionContext.getParams();

        String tokenData = tokenContentFormatter.buildForActivity(activityId, personId);

        // get current user's crypto key
        byte[] key = cryptoKeyDao.execute(personId);
        return tokenEncoder.encode(tokenData, key);
    }
}
