/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

/**
 * This class is a default implementation of the Principal interface. If additional properties are needed, implement
 * Principal in a concrete class along with an associated {@link PrincipalPopulator} class.
 */
public class DefaultPrincipal implements Principal
{
    /**
     * serial id.
     */
    private static final long serialVersionUID = 4658324728068373663L;

    /**
     * Local instance of the accountId.
     */
    private final String accountId;

    /**
     * Local instance of the OpenSocial id.
     */
    private final String openSocialId;

    /**
     * Local instance of the Entity Id.
     */
    private final Long id;

    /**
     * Base constructor for the Principal object.
     *
     * @param inAccountId
     *            - string based account id to create the Principal object with.
     * @param inOpenSocialId
     *            - string based opensocial id to create the Principal object with.
     * @param inId
     *            - Long entity id to create the Principal object with.
     */
    public DefaultPrincipal(final String inAccountId, final String inOpenSocialId, final Long inId)
    {
        accountId = inAccountId;
        openSocialId = inOpenSocialId;
        id = inId;
    }
    /**
     * {@inheritDoc}.
     */
    public String getAccountId()
    {
        return accountId;
    }

    /**
     * {@inheritDoc}.
     */
    public String getOpenSocialId()
    {
        return openSocialId;
    }

    /**
     * {@inheritDoc}.
     */
    public Long getId()
    {
        return id;
    }
}
