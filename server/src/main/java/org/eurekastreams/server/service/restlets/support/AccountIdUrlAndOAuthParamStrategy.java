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
package org.eurekastreams.server.service.restlets.support;

import org.restlet.data.Request;

/**
 * Implementation of the AccountIdStrategy for retrieving the accountid
 * from the oauth header or failing over to pulling it from the url.
 * 
 * This strategy is intended for development purposes.
 *
 */
public class AccountIdUrlAndOAuthParamStrategy implements AccountIdStrategy
{
    /**
     * Local instance of the accountid OAuth strategy.
     */
    private final AccountIdStrategy accountIdOAuthParamStrategy;
    
    /**
     * Constructor.
     * @param inAccountIdOAuthParamStrategy - instance of AccountIdStrategy for retrieving 
     * the accountid from the OAuth headers.
     */
    public AccountIdUrlAndOAuthParamStrategy(final AccountIdStrategy inAccountIdOAuthParamStrategy)
    {
        accountIdOAuthParamStrategy = inAccountIdOAuthParamStrategy;
    }
    
    /**
     * {@inheritDoc}
     * Retrieve the accountId from the OAuth header or if not available in OAuth header, 
     * retrieve from the url.
     * Returns null if the accountid is not found.
     */
    @Override
    public String getAccountId(final Request inRequest)
    {
        String accountId;
        if(accountIdOAuthParamStrategy.getAccountId(inRequest) != null)
        {
            accountId = accountIdOAuthParamStrategy.getAccountId(inRequest);
        }
        else
        {
            accountId = (String) inRequest.getAttributes().get("accountidFromUrl");
        }
        return accountId;
    }
}
