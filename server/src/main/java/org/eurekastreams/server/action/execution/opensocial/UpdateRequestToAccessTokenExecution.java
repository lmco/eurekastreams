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
package org.eurekastreams.server.action.execution.opensocial;

import java.util.Date;
import java.util.UUID;

import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.persistence.OAuthEntryMapper;

/**
 * This class converts a Request Token to an Access token.
 * 
 */
public class UpdateRequestToAccessTokenExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Instance of OAuth entry mapper injected by spring.
     */
    private final OAuthEntryMapper entryMapper;

    /**
     * Instance of {@link OAuthEntryConversionStrategy} for this class.
     */
    private final OAuthEntryConversionStrategy conversionStrat;

    /**
     * Constructor.
     * 
     * @param inEntryMapper
     *            - instance of an {@link OAuthEntryMapper}.
     * @param inConversionStrat
     *            - instance of an {@link OAuthEntryConversionStrategy}.
     */
    public UpdateRequestToAccessTokenExecution(final OAuthEntryMapper inEntryMapper,
            final OAuthEntryConversionStrategy inConversionStrat)
    {
        entryMapper = inEntryMapper;
        conversionStrat = inConversionStrat;
    }

    /**
     * {@inheritDoc}. Convert the supplied Request Token into an Access Token.
     */
    @Override
    public OAuthEntry execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        OAuthEntry requestEntry = (OAuthEntry) inActionContext.getParams();
        OAuthEntry accessEntry = new OAuthEntry(requestEntry);

        accessEntry.token = UUID.randomUUID().toString();
        accessEntry.tokenSecret = UUID.randomUUID().toString();

        accessEntry.type = OAuthEntry.Type.ACCESS;
        accessEntry.issueTime = new Date();

        entryMapper.delete(requestEntry.token);
        entryMapper.insert(conversionStrat.convertToEntryDTO(accessEntry));

        return accessEntry;
    }

}
