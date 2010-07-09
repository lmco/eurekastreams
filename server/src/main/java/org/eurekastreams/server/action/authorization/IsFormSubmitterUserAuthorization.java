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
package org.eurekastreams.server.action.authorization;

import java.io.Serializable;
import java.util.Map;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;

/**
 * This authorization strategy checks that the user making the request is the same as the field in the map passed in
 * through the parameters. The key into the map is supplied in the constructor.
 *
 */
public class IsFormSubmitterUserAuthorization implements AuthorizationStrategy<PrincipalActionContext>
{

    /**
     * Key into the Map that contains the accountid, supplied by the constructor.
     */
    private final String accountIdFieldKey;

    /**
     * Constructor.
     *
     * @param inAccountIdFieldKey
     *            - string key of the field that contains the account id information from the form to check with the
     *            principal of the request.
     */
    public IsFormSubmitterUserAuthorization(final String inAccountIdFieldKey)
    {
        accountIdFieldKey = inAccountIdFieldKey;
    }

    /**
     * {@inheritDoc}.
     *
     * This method checks that the accountid of the person making the request matches the accountid passed in by the
     * form specified by the configured key.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void authorize(final PrincipalActionContext inActionContext) throws AuthorizationException
    {
        Map<String, Serializable> inFields = (Map<String, Serializable>) inActionContext.getParams();
        //TODO:This should go in a validation strategy.
        if (!inFields.containsKey(accountIdFieldKey))
        {
            throw new AuthorizationException("Account key does not exist, failed to authorize.");
        }

        if (inActionContext.getPrincipal().getAccountId()
                .compareToIgnoreCase((String) inFields.get(accountIdFieldKey)) != 0)
        {
            throw new AuthorizationException("Insufficient permissions to update personal profile.");
        }
    }

}
