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

import java.io.Serializable;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * This class is responsible for retrieving an OAuthToken based on a supplied string key.
 * 
 */
public class GetOAuthEntryByTokenExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Instance of OAuth entry mapper injected by spring.
     */
    private final DomainMapper<String, OAuthDomainEntry> entryMapper;

    /**
     * Instance of {@link OAuthEntryConversionStrategy} for this class.
     */
    private final OAuthEntryConversionStrategy conversionStrat;

    /**
     * Constructor.
     * 
     * @param inMapper
     *            - instance of {@link DomainMapper}.
     * @param inConversionStrat
     *            - instance of {@link OAuthEntryConversionStrategy}.
     */
    public GetOAuthEntryByTokenExecution(final DomainMapper<String, OAuthDomainEntry> inMapper,
            final OAuthEntryConversionStrategy inConversionStrat)
    {
        entryMapper = inMapper;
        conversionStrat = inConversionStrat;
    }

    /**
     * {@inheritDoc}. Retrieve the OAuthToken based on the supplied token.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        String token = (String) inActionContext.getParams();
        OAuthDomainEntry dto = entryMapper.execute(token);
        return (dto == null) ? null : conversionStrat.convertToEntry(dto);
    }

}
