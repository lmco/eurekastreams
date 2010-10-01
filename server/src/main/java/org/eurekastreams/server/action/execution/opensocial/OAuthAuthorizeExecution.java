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

import java.io.IOException;

import net.oauth.OAuth;

import org.apache.commons.logging.Log;
import org.apache.shindig.common.crypto.Crypto;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Authorizes or rejects an OAuth request.
 */
public class OAuthAuthorizeExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * The mapper used for retrieving OAuthEntries from the db.
     */
    private final DomainMapper<String, OAuthDomainEntry> entryMapper;

    /**
     * Number of digits to create for the random callback token.
     */
    private static final int CALLBACK_TOKEN_LENGTH = 6;

    /**
     * Constructor.
     * 
     * @param inEntryMapper
     *            - mapper for the OauthEntry.
     */
    public OAuthAuthorizeExecution(final DomainMapper<String, OAuthDomainEntry> inEntryMapper)
    {
        entryMapper = inEntryMapper;
    }

    /**
     * {@inheritDoc}
     * 
     * This execute method retrieves the ActivityDTO objects for the parameters passed in.
     */
    @Override
    public String execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        final String accountId = inActionContext.getPrincipal().getAccountId();
        final String token = (String) inActionContext.getParams();

        log.trace("Authorizing OAuth token for user: " + accountId);
        OAuthEntry tokenEntry = new OAuthEntry();
        tokenEntry.setToken(token);

        try
        {
            OAuthDomainEntry dto = entryMapper.execute(token);
            dto.setAuthorized(true);
            if (dto.isCallbackUrlSigned())
            {
                dto.setCallbackToken(Crypto.getRandomDigits(CALLBACK_TOKEN_LENGTH));
            }
        }
        catch (Exception ex)
        {
            log.error("An error occurred authorizing the OAuth token.", ex);
            throw new ExecutionException(ex);
        }

        OAuthDomainEntry currentEntry = entryMapper.execute(tokenEntry.getToken());
        log.trace("Authorization for user: " + accountId + " complete.");
        String callbackUrl = "";
        if (currentEntry.getCallbackUrl() != null && currentEntry.getCallbackUrl().length() > 0)
        {
            try
            {
                callbackUrl = OAuth.addParameters(currentEntry.getCallbackUrl(), "oauth_verifier", currentEntry
                        .getCallbackToken());
                callbackUrl = OAuth.addParameters(callbackUrl, OAuth.OAUTH_TOKEN, currentEntry.getToken());
            }
            catch (IOException ioe)
            {
                log.error("An IO exception has occured.", ioe);
                throw new ExecutionException(ioe);
            }
        }
        log.debug("return the callback url with the oauth_token added " + callbackUrl);
        return callbackUrl;
    }

}
