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

import org.apache.commons.lang.StringUtils;
import org.eurekastreams.server.persistence.mappers.requests.LdapLookupRequest;
import org.springframework.ldap.core.LdapTemplate;

/**
 * Return LdapTemplate based on presented LdapGroup relative dn. This implementation only uses the template key from
 * {@link LdapLookupRequest} and ignores query string.
 * 
 */
public class LdapGroupDnLdapTemplateRetriever extends BaseLdapTemplateRetriever
{
    /**
     * Constructor.
     * 
     * @param inLdapTemplates
     *            Map of ldap templates.
     * @param inDefaultLdapTemplate
     *            The default LdapTemplate to use.
     */
    public LdapGroupDnLdapTemplateRetriever(final HashMap<String, LdapTemplate> inLdapTemplates,
            final LdapTemplate inDefaultLdapTemplate)
    {
        super(inLdapTemplates, inDefaultLdapTemplate);
    }

    /**
     * Uses specified template key to return an LdapTemplate used to make query.
     * 
     * @param inLdapLookupRequest
     *            {@link LdapLookupRequest}.
     * @return LdapTemplate based on provided template key, or default template if none found.
     */
    @Override
    protected LdapTemplate retrieveLdapTemplate(final LdapLookupRequest inLdapLookupRequest)
    {
        String testString = inLdapLookupRequest.getTemplateKey();

        if (testString != null && !testString.isEmpty())
        {
            for (String key : getLdapTemplates().keySet())
            {
                if (StringUtils.containsIgnoreCase(testString, "dc=" + key))
                {
                    return getLdapTemplates().get(key);
                }
            }
        }

        return getDefaultLdapTemplate();
    }

}
