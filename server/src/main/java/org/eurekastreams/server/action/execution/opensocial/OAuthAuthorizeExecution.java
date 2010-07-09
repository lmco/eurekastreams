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
import net.oauth.OAuthProblemException;

import org.apache.commons.logging.Log;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.eurekastreams.server.persistence.OAuthEntryMapper;

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
     * The oauth data store to communicate with.
     */
    private OAuthDataStore dataStore;
    
    /**
     * The mapper used for retrieving OAuthEntries from the db.
     */
    private OAuthEntryMapper oauthEntryMapper;

    /**
     * Setter.
     * 
     * @param inDataStore
     *            the dataStore to set
     */
    public void setDataStore(final OAuthDataStore inDataStore)
    {
        dataStore = inDataStore;
    }

    /**
     * Setter for the OAuthEntryMapper.
     * @param inMapper 
     *           the mapper to set.
     */
    public void setOAuthEntryMapper(final OAuthEntryMapper inMapper)
    {
        oauthEntryMapper = inMapper;
    }
    
    /**
     * Default constructor.
     */
    public OAuthAuthorizeExecution()
    {
        //left blank for GWT serialization requirements.
    }
    
    /**
     * Constructor.
     * @param inDataStore
     *          the datastore.
     * @param inOAuthEntryMapper - mapper for the OauthEntry.
     */
    public OAuthAuthorizeExecution(final OAuthDataStore inDataStore, final OAuthEntryMapper inOAuthEntryMapper)
    {
        dataStore = inDataStore;
        oauthEntryMapper = inOAuthEntryMapper;
    }

    /**
     * REMOVE THIS - ONLY HERE TO MAKE SPRING HAPPY WHILE I AM WAITING TO CHECK IN THE OUATH STUFF.
     * @param inDataStore
     *          the datastore.
     */
    public OAuthAuthorizeExecution(final OAuthDataStore inDataStore)
    {
        dataStore = inDataStore;
        oauthEntryMapper = null;
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
        tokenEntry.token = token;
        
        try
        {
            dataStore.authorizeToken(tokenEntry, accountId);
        }
        catch (OAuthProblemException oape)
        {
            log.error("An OAuth problem has occured.", oape);
            throw new ExecutionException(oape);
        }
        
        OAuthDomainEntry currentEntry = oauthEntryMapper.findEntry(tokenEntry.token);
        log.trace("Authorization for user: " + accountId + " complete.");
        String callbackUrl = "";
        if (currentEntry.getCallbackUrl() != null 
                && currentEntry.getCallbackUrl().length() > 0)
        {
            try
            {
            callbackUrl = 
                OAuth.addParameters(currentEntry.getCallbackUrl(), "oauth_verifier", currentEntry.getCallbackToken());
            callbackUrl = 
                OAuth.addParameters(callbackUrl, OAuth.OAUTH_TOKEN, currentEntry.getToken());
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
