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

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.restlet.data.Form;
import org.restlet.data.Request;

/**
 * Use input request to get user principal.
 * 
 */
public class OAuthRequestToPrincipalTransformer implements Transformer<Request, Principal>
{

    /**
     * Principal populator.
     */
    PrincipalPopulator principalPopulator;

    /**
     * Constructor.
     * 
     * @param inPrincipalPopulator
     *            PrincipalPopulator to create principal.
     */
    public OAuthRequestToPrincipalTransformer(final PrincipalPopulator inPrincipalPopulator)
    {
        principalPopulator = inPrincipalPopulator;
    }

    /**
     * Use input request to get user principal.
     * 
     * @param inTransformType
     *            Request.
     * @return User principal.
     */
    @Override
    public Principal transform(final Request inTransformType)
    {
        String accountid = null;
        Principal result = null;

        if (inTransformType.getAttributes().containsKey("org.restlet.http.headers"))
        {
            Form httpHeaders = (Form) inTransformType.getAttributes().get("org.restlet.http.headers");

            if (httpHeaders.getFirstValue("accountid") != null)
            {
                accountid = httpHeaders.getFirstValue("accountid");
            }
        }

        if (accountid != null)
        {
            result = principalPopulator.getPrincipal(accountid, "");
        }

        return result;
    }
}
