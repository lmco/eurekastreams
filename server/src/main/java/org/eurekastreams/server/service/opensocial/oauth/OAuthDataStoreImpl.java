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
package org.eurekastreams.server.service.opensocial.oauth;

import java.util.Date;
import java.util.UUID;

import net.oauth.OAuthConsumer;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthServiceProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.AuthenticationMode;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.crypto.Crypto;
import org.apache.shindig.social.core.oauth.OAuthSecurityToken;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry.Type;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.eurekastreams.server.domain.OAuthDomainEntry;
import org.eurekastreams.server.persistence.OAuthConsumerMapper;
import org.eurekastreams.server.persistence.OAuthEntryMapper;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * {@link OAuthDataStore} implementation that is used during both 2 and 3-legged OAuth 
 * authorizations from Shindig.  These methods are called 
 */
public class OAuthDataStoreImpl implements OAuthDataStore
{
    /**
     * Local instance of logger.
     */
    private final Log logger = LogFactory.getLog(OAuthDataStoreImpl.class);
    
    /**
     * Instance of OAuth entry mapper injected by spring.
     */
    private OAuthEntryMapper entryMapper;

    /**
     * Instance of OAuth consumer mapper injected by spring.
     */
    private OAuthConsumerMapper consumerMapper;

    /**
     * Instance of the transaction manager injected by spring for mapper calls.
     */
    private PlatformTransactionManager transMgr = null;

    /**
     * The local service provider.
     */
    private final OAuthServiceProvider serviceProvider;
    
    /**
     * Number of digits to create for the random callback token.
     */
    private static final int CALLBACK_TOKEN_LENGTH = 6;

    /**
     * Maximum attempts to attempt to authorize a request token. 
     */
    private static final int CALLBACK_TOKEN_ATTEMPTS = 5;

    /**
     * Default constructor. Empty but necessary for spring.
     */
    public OAuthDataStoreImpl()
    {
    	serviceProvider = null;
    }

    /**
     * @param inEntryMapper
     *            the entryMapper to set
     */
    public void setEntryMapper(final OAuthEntryMapper inEntryMapper)
    {
        entryMapper = inEntryMapper;
    }

    /**
     * @param inConsumerMapper
     *            the consumerMapper to set
     */
    public void setConsumerMapper(final OAuthConsumerMapper inConsumerMapper)
    {
        consumerMapper = inConsumerMapper;
    }

    /**
     * @param inTransMgr
     *            the transMgr to set
     */
    public void setTransMgr(final PlatformTransactionManager inTransMgr)
    {
        transMgr = inTransMgr;
    }

    /**
     * Constructor.
     * @param inRequestTokenUrl - Url for retrieving the request token
     * @param inAuthorizeUrl - Url for authorizing the user.
     * @param inAccessTokenUrl - Url for retrieving the access token.
     * @param inEntryMapper
     *          the oauth entry mapper.
     * @param inConsumerMapper
     *          the oauth consumer mapper.
     * @param inTransMgr
     *          the transaction manager.
     */
    @Inject
    public OAuthDataStoreImpl(@Named("eureka.oauth.requesttokenurl") final String inRequestTokenUrl, 
    		@Named("eureka.oauth.authorizeurl") final String inAuthorizeUrl, 
    		@Named("eureka.oauth.accesstokenurl") final String inAccessTokenUrl, 
    		final OAuthEntryMapper inEntryMapper, final OAuthConsumerMapper inConsumerMapper,
            final PlatformTransactionManager inTransMgr)
    {
    	serviceProvider = new OAuthServiceProvider(inRequestTokenUrl, inAuthorizeUrl, inAccessTokenUrl);
    	entryMapper = inEntryMapper;
    	consumerMapper = inConsumerMapper;
    	transMgr = inTransMgr;
    }

    /**
     * Creates a request token for a new OAuth request.
     * @param consumerKey
     *          the consumer key for this request.
     * @param oauthVersion
     *          the version of the oauth protocol used in this request.
     * @param signedCallbackUrl
     *          the callback url (needed for OAuth 1.0 A).
     * @return the entry containing the new token.
     * @throws OAuthProblemException
     *          thrown when token could not be persisted. 
     */
    public OAuthEntry generateRequestToken(final String consumerKey, final String oauthVersion,
            final String signedCallbackUrl) throws OAuthProblemException
    {
        DefaultTransactionDefinition transDef = new DefaultTransactionDefinition();
        transDef.setName("RequestTokenTransaction");
        transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transStatus = transMgr.getTransaction(transDef);

        try
        {
            OAuthEntry entry = new OAuthEntry();
            entry.appId = consumerKey;
            entry.consumerKey = consumerKey;
            entry.domain = "samplecontainer.com";
            entry.container = "default";

            entry.token = UUID.randomUUID().toString();
            entry.tokenSecret = UUID.randomUUID().toString();

            entry.type = OAuthEntry.Type.REQUEST;
            entry.issueTime = new Date();
            entry.oauthVersion = oauthVersion;
            if (signedCallbackUrl != null)
            {
                entry.callbackUrlSigned = true;
                entry.callbackUrl = signedCallbackUrl;
            }

            OAuthDomainEntry dto = convertToEntryDTO(entry);
            entryMapper.insert(dto);
            transMgr.commit(transStatus);
            return entry;
        }
        catch (Exception ex)
        {
            logger.error("Error occurred persisting request token information", ex);
            transMgr.rollback(transStatus);
            throw new OAuthProblemException("Unable to persist request token information");
        }
    }

    /**
     * Authorize the access token.
     * @param entry
     *          the entry to authorize.
     * @param userId
     *          the user making the request.
     * @throws OAuthProblemException
     *          thrown when the token could not be persisted.
     */
    public void authorizeToken(final OAuthEntry entry, final String userId) throws OAuthProblemException
    {
        DefaultTransactionDefinition transDef = new DefaultTransactionDefinition();
        transDef.setName("AuthorizeTokenTransaction");
        transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transStatus = transMgr.getTransaction(transDef);

        try
        {
            OAuthDomainEntry dto = entryMapper.findEntry(entry.token);
            dto.setAuthorized(true);
            if (dto.isCallbackUrlSigned())
            {
                dto.setCallbackToken(Crypto.getRandomDigits(CALLBACK_TOKEN_LENGTH));
            }
            transMgr.commit(transStatus);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred persisting authorization token information", ex);
            transMgr.rollback(transStatus);
            throw new OAuthProblemException("Unable to persist token authorization information");
        }
    }

    /**
     * Authorize the access token with an OAuthDomainEntry passed in.
     * @param inEntry - OAuthDomainEntry representating the OAuth token.
     * @param userId - Userid requesting the authorization.
     * @throws OAuthProblemException - if errors occur.
     */
    public void authorizeToken(final OAuthDomainEntry inEntry, final String userId) throws OAuthProblemException
    {
        authorizeToken(convertToEntry(inEntry), userId);
    }
    
    /**
     * Exchange a request token for an access token.
     * @param entry
     *          the entry to authorize.
     * @return the entry with the access token.
     * @throws OAuthProblemException
     *          thrown when the token could not be persisted.
     */
    public OAuthEntry convertToAccessToken(final OAuthEntry entry) throws OAuthProblemException
    {
        DefaultTransactionDefinition transDef = new DefaultTransactionDefinition();
        transDef.setName("AccessTokenTransaction");
        transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transStatus = transMgr.getTransaction(transDef);

        try
        {
            OAuthEntry accessEntry = new OAuthEntry(entry);

            accessEntry.token = UUID.randomUUID().toString();
            accessEntry.tokenSecret = UUID.randomUUID().toString();

            accessEntry.type = OAuthEntry.Type.ACCESS;
            accessEntry.issueTime = new Date();

            entryMapper.delete(entry.token);
            entryMapper.insert(convertToEntryDTO(accessEntry));
            transMgr.commit(transStatus);

            return accessEntry;
        }
        catch (Exception ex)
        {
            logger.error("Error occurred persisting access token information", ex);
            transMgr.rollback(transStatus);
            throw new OAuthProblemException("Unable to persist access token information");
        }
    }

    /**
     * Return the consumer for a given key.
     * @param consumerKey
     *          the key.
     * @return the found consumer.
     * @throws OAuthProblemException
     *          thrown when there was a problem retrieving the consumer information.
     */
    public OAuthConsumer getConsumer(final String consumerKey) throws OAuthProblemException
    {
        //TODO - finish
        // Serializable[] params = {consumerKey};
        // org.eurekastreams.server.domain.OAuthConsumer currentConsumer = null;
        // OAuthConsumer oauthConsumer = null;
        // try
        // {
        // //Retrieve the model OAuthConsumer and convert to net.oauth.OAuthConsumer.
        // currentConsumer =
        // oauthConsumerAction.performAction(new NoCurrentUserDetails(), params);
        // oauthConsumer = new OAuthConsumer(
        // currentConsumer.getCallbackUrl(),
        // currentConsumer.getConsumerKey(),
        // currentConsumer.getConsumerSecret(),
        // new OAuthServiceProvider (
        // oauthSP.getRequestUrl(),
        // oauthSP.getAuthorizeUrl(),
        // oauthSP.getAccessUrl()));
        // //Set consumer properties here. The sample uses gadget properties of the ModulePrefs.
        // }
        // catch (Exception ex)
        // {
        //            
        // throw new OAuthProblemException(
        // "Error occurred retrieving OAuthConsumer with the provided key.");
        // }
        // return oauthConsumer;

        // return null;
        
        OAuthConsumer consumer = null;
        
        try
        {
            org.eurekastreams.server.domain.OAuthConsumer mappedConsumer = 
                consumerMapper.findConsumerByConsumerKey(consumerKey);
            consumer = new OAuthConsumer(
                    mappedConsumer.getCallbackURL(), 
                    consumerKey, 
                    mappedConsumer.getConsumerSecret(), serviceProvider);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred retrieving consumer with provided key.", ex);
            throw new OAuthProblemException("Unable to retrieve consumer with provided information.");
        }
        return consumer;
    }

    /**
     * Get an entry for a specific oauth token.
     * @param oauthToken
     *          the token.
     * @return the associated entry.
     */
    public OAuthEntry getEntry(final String oauthToken)
    {
        OAuthDomainEntry dto = entryMapper.findEntry(oauthToken);
        if (dto == null)
        {
            return null;
        }
        return this.convertToEntry(dto);
    }

    /**
     * Marks a token as disabled.
     * @param entry
     *          the OAuthEntry to disable.
     */
    public void disableToken(final OAuthEntry entry)
    {
        DefaultTransactionDefinition transDef = new DefaultTransactionDefinition();
        transDef.setName("DisableTokenTransaction");
        transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transStatus = transMgr.getTransaction(transDef);

        try
        {
            OAuthDomainEntry dto = entryMapper.findEntry(entry.token);
            dto.setCallbackTokenAttempts(dto.getCallbackTokenAttempts() + 1);
            if (!dto.isCallbackUrlSigned() || dto.getCallbackTokenAttempts() >= CALLBACK_TOKEN_ATTEMPTS)
            {
                dto.setType(OAuthEntry.Type.DISABLED.toString());
            }

            transMgr.commit(transStatus);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred disabling token.", ex);
            transMgr.rollback(transStatus);
        }
    }

    /**
     * Deletes a token.
     * @param entry
     *          the OAuthEntry to remove.
     */
    public void removeToken(final OAuthEntry entry)
    {
        DefaultTransactionDefinition transDef = new DefaultTransactionDefinition();
        transDef.setName("RemoveTokenTransaction");
        transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transStatus = transMgr.getTransaction(transDef);

        try
        {
            entryMapper.delete(entry.token);
            transMgr.commit(transStatus);
        }
        catch (Exception ex)
        {
            logger.error("Error occurred removing token.", ex);
            transMgr.rollback(transStatus);
        }
    }

    /**
     * Return the proper security token for a 2 legged oauth request that has been validated for the given consumerKey.
     * App specific checks like making sure the requested user has the app installed should take place in this method.
     * @param consumerKey
     *          the consumer making the oauth request.
     * @param userId
     *          the userId for this request.
     * @return the found securitytoken.
     * @throws OAuthProblemException
     *          thrown when the consumer key was not found.
     */
    public SecurityToken getSecurityTokenForConsumerRequest(final String consumerKey, final String userId)
            throws OAuthProblemException
    {
        // TODO finish?
        String domain = "samplecontainer.com";
        String container = "default";

        return new OAuthSecurityToken(userId, null, consumerKey, domain, container,
                AuthenticationMode.OAUTH_CONSUMER_REQUEST.name());
    }

    /**
     * Maps an entry to an entry data transfer object.
     * @param entry
     *          the entry to convert.
     * @return the converted entry dto.
     */
    private OAuthDomainEntry convertToEntryDTO(final OAuthEntry entry)
    {
        OAuthDomainEntry dto = new OAuthDomainEntry();
        dto.setAppId(entry.appId);
        dto.setAuthorized(entry.authorized);
        dto.setCallbackToken(entry.callbackToken);
        dto.setCallbackTokenAttempts(entry.callbackTokenAttempts);
        dto.setCallbackUrl(entry.callbackUrl);
        dto.setCallbackUrlSigned(entry.callbackUrlSigned);
        dto.setConsumer(consumerMapper.findConsumerByConsumerKey(entry.consumerKey));
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
    private OAuthEntry convertToEntry(final OAuthDomainEntry dto)
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
