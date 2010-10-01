/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry.Type;
import org.eurekastreams.server.domain.OAuthConsumer;
import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * This class provides the strategy for converting {@link OAuthEntry} objects to {@link OAuthDomainEntry} objects
 * and vice versa.
 *
 */
public class OAuthEntryConversionStrategy
{
    /**
     * Instance of the {@link DomainMapper}.
     */
    private final DomainMapper<String, OAuthConsumer> mapper;
    
    /**
     * Constructor.
     * @param inMapper - instance of the {@link DomainMapper} to retrieve consumer information.
     */
    public OAuthEntryConversionStrategy(final DomainMapper<String, OAuthConsumer> inMapper)
    {
        mapper = inMapper;
    }
    
    /**
     * Maps an entry to an entry data transfer object.
     * @param entry
     *          the entry to convert.
     * @return the converted entry dto.
     */
    public OAuthDomainEntry convertToEntryDTO(final OAuthEntry entry)
    {
        OAuthDomainEntry dto = new OAuthDomainEntry();
        dto.setAppId(entry.getAppId());
        dto.setAuthorized(entry.isAuthorized());
        dto.setCallbackToken(entry.getCallbackToken());
        dto.setCallbackTokenAttempts(entry.getCallbackTokenAttempts());
        dto.setCallbackUrl(entry.getCallbackUrl());
        dto.setCallbackUrlSigned(entry.isCallbackUrlSigned());
        dto.setConsumer(mapper.execute(entry.getConsumerKey()));
        dto.setContainer(entry.getContainer());
        dto.setDomain(entry.getDomain());
        dto.setIssueTime(entry.getIssueTime());
        dto.setOauthVersion(entry.getOauthVersion());
        dto.setToken(entry.getToken());
        dto.setTokenSecret(entry.getTokenSecret());
        dto.setType(entry.getType().toString());
        dto.setUserId(entry.getUserId());
        return dto;
    }

    /**
     * Maps an entry dto to an entry.
     * @param dto
     *          the dto to convert.
     * @return the converted entry.
     */
    public OAuthEntry convertToEntry(final OAuthDomainEntry dto)
    {
        OAuthEntry entry = new OAuthEntry();
        entry.setAppId(dto.getAppId());
        entry.setAuthorized(dto.isAuthorized());
        entry.setCallbackToken(dto.getCallbackToken());
        entry.setCallbackTokenAttempts(dto.getCallbackTokenAttempts());
        entry.setCallbackUrl(dto.getCallbackUrl());
        entry.setCallbackUrlSigned(dto.isCallbackUrlSigned());
        entry.setConsumerKey(dto.getConsumer().getConsumerKey());
        entry.setContainer(dto.getContainer());
        entry.setDomain(dto.getDomain());
        entry.setIssueTime(dto.getIssueTime());
        entry.setOauthVersion(dto.getOauthVersion());
        entry.setToken(dto.getToken());
        entry.setTokenSecret(dto.getTokenSecret());
        entry.setType(Type.valueOf(dto.getType()));
        entry.setUserId(dto.getUserId());
        return entry;
    }
}
