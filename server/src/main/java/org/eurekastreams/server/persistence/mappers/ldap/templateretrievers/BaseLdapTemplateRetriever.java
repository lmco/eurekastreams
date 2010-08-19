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
package org.eurekastreams.server.persistence.mappers.ldap.templateretrievers;

import java.util.HashMap;

import org.eurekastreams.server.persistence.mappers.requests.LdapLookupRequest;
import org.springframework.ldap.core.LdapTemplate;

/**
 * Base class for LdapTemplateRetrievers.
 * 
 */
public abstract class BaseLdapTemplateRetriever implements LdapTemplateRetriever
{
    /**
     * Map of ldap templates.
     */
    private HashMap<String, LdapTemplate> ldapTemplates;

    /**
     * The default LdapTemplate to use.
     */
    private LdapTemplate defaultLdapTemplate;

    /**
     * Constructor.
     * 
     * @param inLdapTemplates
     *            Map of ldap templates.
     * @param inDefaultLdapTemplate
     *            The default LdapTemplate to use.
     */
    public BaseLdapTemplateRetriever(final HashMap<String, LdapTemplate> inLdapTemplates,
            final LdapTemplate inDefaultLdapTemplate)
    {
        ldapTemplates = inLdapTemplates;
        defaultLdapTemplate = inDefaultLdapTemplate;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public LdapTemplate getLdapTemplate(final LdapLookupRequest inLdapLookupRequest)
    {
        return retrieveLdapTemplate(inLdapLookupRequest);
    }

    /**
     * Get the LdapTemplate.
     * 
     * @param inLdapLookupRequest
     *            {@link LdapLookupRequest}..
     * @return the LdapTemplate.
     */
    protected abstract LdapTemplate retrieveLdapTemplate(final LdapLookupRequest inLdapLookupRequest);

    /**
     * @return the ldapTemplate
     */
    protected HashMap<String, LdapTemplate> getLdapTemplates()
    {
        return ldapTemplates;
    }

    /**
     * @return the defaultLdapTemplate
     */
    protected LdapTemplate getDefaultLdapTemplate()
    {
        return (defaultLdapTemplate == null) ? ((LdapTemplate) ldapTemplates.values().toArray()[0])
                : defaultLdapTemplate;
    }

}
