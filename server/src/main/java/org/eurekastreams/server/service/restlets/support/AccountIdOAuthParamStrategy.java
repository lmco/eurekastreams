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

import org.restlet.data.Form;
import org.restlet.data.Request;

/**
 * Implementation fo the {@link AccountIdStrategy} that pulls the account id from the
 * OAuth request headers.
 *
 */
public class AccountIdOAuthParamStrategy implements AccountIdStrategy
{
    /**
     * {@inheritDoc}.
     * Retrieve the Account id information supplied by the OAuth request in the headers.
     * Returns null if the header is not found.
     */
    @SuppressWarnings("unchecked")
    @Override
    public String getAccountId(final Request inRequest)
    {
        String accountid = null;
        
        if(inRequest.getAttributes().containsKey("org.restlet.http.headers"))
        {
            Form httpHeaders = 
                (Form) inRequest.getAttributes().get("org.restlet.http.headers");
            
            if(httpHeaders.getFirstValue("accountid") != null)
            {
                accountid = (String) httpHeaders.getFirstValue("accountid");
            }
        }
        
        return accountid;
    }

}
