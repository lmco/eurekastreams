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
package org.eurekastreams.server.action.authorization.opensocial;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.persistence.mappers.opensocial.GetGadgetsByGadgetDefAndConsumerKey;
import org.eurekastreams.server.persistence.mappers.requests.opensocial.GetGadgetsByGadgetDefAndConsumerKeyRequest;

/**
 * This class performs a business logic check to ensure that the request coming through is legit. 
 * Simple sanity check to
 * ensure that the request is being made for a user that has the app associated with the consumer key installed.
 * 
 * This supports the "reverse call home" scenario in two legged oauth where the server hosting a gadget may wish to
 * request opensocial information about the user that has installed their application.
 * 
 */
public class OAuthRequestAuthorization implements AuthorizationStrategy<PrincipalActionContext>
{
    /**
     * Local instance of the {@link GetGadgetsByGadgetDefAndConsumerKey} mapper that determines if the user passed in
     * has at least one instance of the application associated with the supplied consumer key installed on their start
     * page.
     */
    private final GetGadgetsByGadgetDefAndConsumerKey verifyMapper;

    /**
     * Constructor.
     * 
     * @param inVerifyMapper
     *            instance of the {@link GetGadgetsByGadgetDefAndConsumerKey} mapper.
     */
    public OAuthRequestAuthorization(final GetGadgetsByGadgetDefAndConsumerKey inVerifyMapper)
    {
        verifyMapper = inVerifyMapper;
    }

    /**
     * {@inheritDoc}. Determine if the user being authorized has an instance of the application associated to the
     * consumer key installed on their start page.
     */
    @Override
    public void authorize(final PrincipalActionContext inActionContext) throws AuthorizationException
    {
        String consumerKey = (String) inActionContext.getParams();
        Long gadgetCount = verifyMapper.execute(new GetGadgetsByGadgetDefAndConsumerKeyRequest(consumerKey,
                inActionContext.getPrincipal().getId()));
        if (gadgetCount <= 0)
        {
            throw new AuthorizationException("The user supplied with this OAuth request does not have the app "
                    + "associated with this consumer key installed on their start page.");
        }
    }

}
