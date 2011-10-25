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
import org.eurekastreams.server.domain.EntityIdentifier;
import org.eurekastreams.server.service.email.TokenContentEmailAddressBuilder;
import org.eurekastreams.server.service.email.TokenContentFormatter;

/**
 * Gets a token for the current user for posting to a stream.
 */
public class GetEmailAddressForStreamExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /** Builds the token content. */
    private final TokenContentFormatter tokenContentFormatter;

    /** Builds the recipient email address with a token. */
    private final TokenContentEmailAddressBuilder tokenAddressBuilder;

    /**
     * Constructor.
     *
     * @param inTokenContentFormatter
     *            Builds the token content.
     * @param inTokenAddressBuilder
     *            Builds the recipient email address with a token.
     */
    public GetEmailAddressForStreamExecution(final TokenContentFormatter inTokenContentFormatter,
            final TokenContentEmailAddressBuilder inTokenAddressBuilder)
    {
        tokenContentFormatter = inTokenContentFormatter;
        tokenAddressBuilder = inTokenAddressBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {

        EntityIdentifier params = (EntityIdentifier) inActionContext.getParams();

        String tokenData = tokenContentFormatter.buildForStream(params.getType(), params.getId());

        Long personId = inActionContext.getPrincipal().getId();
        String address = tokenAddressBuilder.build(tokenData, personId);
        return address;
    }
}
