/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.eurekastreams.server.persistence.OAuthConsumerMapper;

/**
 * This class provides the strategy for converting {@link OAuthEntry} objects to {@link OAuthDomainEntry} objects
 * and vice versa.
 *
 */
public class OAuthEntryConversionStrategy
{
    /**
     * Instance of the {@link OAuthConsumerMapper}.
     */
    private final OAuthConsumerMapper mapper;
    
    /**
     * Constructor.
     * @param inMapper - instance of the {@link OAuthConsumerMapper} to retrieve consumer information.
     */
    public OAuthEntryConversionStrategy(final OAuthConsumerMapper inMapper)
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
        dto.setAppId(entry.appId);
        dto.setAuthorized(entry.authorized);
        dto.setCallbackToken(entry.callbackToken);
        dto.setCallbackTokenAttempts(entry.callbackTokenAttempts);
        dto.setCallbackUrl(entry.callbackUrl);
        dto.setCallbackUrlSigned(entry.callbackUrlSigned);
        dto.setConsumer(mapper.findConsumerByConsumerKey(entry.consumerKey));
        dto.setContainer(entry.container);
        dto.setDomain(entry.domain);
        dto.setIssueTime(entry.issueTime);
        dto.setOauthVersion(entry.oauthVersion);
        dto.setToken(entry.token);
        dto.setTokenSecret(entry.tokenSecret);
        dto.setType(entry.type.toString());
        dto.setUserId(entry.userId);
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
        entry.appId = dto.getAppId();
        entry.authorized = dto.isAuthorized();
        entry.callbackToken = dto.getCallbackToken();
        entry.callbackTokenAttempts = dto.getCallbackTokenAttempts();
        entry.callbackUrl = dto.getCallbackUrl();
        entry.callbackUrlSigned = dto.isCallbackUrlSigned();
        entry.consumerKey = dto.getConsumer().getConsumerKey();
        entry.container = dto.getContainer();
        entry.domain = dto.getDomain();
        entry.issueTime = dto.getIssueTime();
        entry.oauthVersion = dto.getOauthVersion();
        entry.token = dto.getToken();
        entry.tokenSecret = dto.getTokenSecret();
        entry.type = Type.valueOf(dto.getType());
        entry.userId = dto.getUserId();
        return entry;
    }
}
