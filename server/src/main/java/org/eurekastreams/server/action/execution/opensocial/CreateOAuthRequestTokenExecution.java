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
import org.eurekastreams.server.action.request.opensocial.CreateOAuthRequestTokenRequest;
import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;

/**
 * This action exection creates a request token in the database for a new OAuth request.
 * 
 */
public class CreateOAuthRequestTokenExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Instance of OAuth insert mapper injected by spring.
     */
    private final InsertMapper<OAuthDomainEntry> insertMapper;

    /**
     * Strategy for converting {@link OAuthEntry} objects to {@link OAuthDomainEntry} objects.
     */
    private final OAuthEntryConversionStrategy oauthConversionStrat;

    /**
     * Instance of the OAuthDomain for this execution example: samplecontainer.com.
     */
    private final String oauthDomain;

    /**
     * Instance of the OAuthContainer for this execution example: default.
     */
    private final String oauthContainer;

    /**
     * Consutructor.
     * 
     * @param inOAuthDomain
     *            - String name of the OAuth domain for this execution.
     * @param inOAuthContainer
     *            - String name of the OAuth container for this execution.
     * @param inInsertMapper
     *            - {@link InsertMapper} for this execution.
     * @param inOAuthConversionStrat
     *            - strategy for converting oauthentries to oauthdomainentries.
     */
    public CreateOAuthRequestTokenExecution(final String inOAuthDomain, final String inOAuthContainer,
            final InsertMapper<OAuthDomainEntry> inInsertMapper,
            final OAuthEntryConversionStrategy inOAuthConversionStrat)
    {
        oauthDomain = inOAuthDomain;
        oauthContainer = inOAuthContainer;
        insertMapper = inInsertMapper;
        oauthConversionStrat = inOAuthConversionStrat;
    }

    /**
     * {@inheritDoc}.
     * 
     * Store a new OAuth request token in the database for a new request.
     * 
     */
    @Override
    public OAuthEntry execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        CreateOAuthRequestTokenRequest currentRequest = (CreateOAuthRequestTokenRequest) inActionContext.getParams();
        OAuthEntry entry = new OAuthEntry();
        entry.setAppId(currentRequest.getConsumerKey());
        entry.setConsumerKey(currentRequest.getConsumerKey());
        entry.setDomain(oauthDomain);
        entry.setContainer(oauthContainer);

        entry.setToken(UUID.randomUUID().toString());
        entry.setTokenSecret(UUID.randomUUID().toString());

        entry.setType(OAuthEntry.Type.REQUEST);
        entry.setIssueTime(new Date());
        entry.setOauthVersion(currentRequest.getOauthVersion());
        if (currentRequest.getSignedCallbackUrl() != null)
        {
            entry.setCallbackUrlSigned(true);
            entry.setCallbackUrl(currentRequest.getSignedCallbackUrl());
        }

        OAuthDomainEntry dto = oauthConversionStrat.convertToEntryDTO(entry);
        insertMapper.execute(new PersistenceRequest<OAuthDomainEntry>(dto));
        return entry;
    }

}
