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
package org.eurekastreams.commons.actions.context;

import java.io.Serializable;

import org.eurekastreams.commons.actions.context.service.ServiceActionContext;

/**
 * An action context providing both the user and client making the request.
 */
public class ClientPrincipalActionContextImpl extends ServiceActionContext implements ClientPrincipalActionContext
{
    /** Fingerprint. */
    private static final long serialVersionUID = 4888378459753358746L;

    /** Client ID. */
    private final String clientUniqueId;

    /**
     * Constructor.
     *
     * @param inParams
     *            Instance of the Params for the current action context.
     * @param inPrincipal
     *            Instance of the {@link Principal} for the current action context.
     * @param inClientUniqueId
     *            Client ID for the current action context.
     */
    public ClientPrincipalActionContextImpl(final Serializable inParams, final Principal inPrincipal,
            final String inClientUniqueId)
    {
        super(inParams, inPrincipal);
        clientUniqueId = inClientUniqueId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientUniqueId()
    {
        return clientUniqueId;
    }
}
