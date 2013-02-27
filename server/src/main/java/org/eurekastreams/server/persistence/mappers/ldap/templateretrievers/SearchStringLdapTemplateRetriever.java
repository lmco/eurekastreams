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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.requests.LdapLookupRequest;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * LdapTemplateRetriever used for parsing search string.
 * 
 */
public class SearchStringLdapTemplateRetriever extends BaseLdapTemplateRetriever
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Constructor.
     * 
     * @param inLdapTemplates
     *            Map of ldap templates.
     * @param inDefaultLdapTemplate
     *            The default LdapTemplate to use.
     */
    public SearchStringLdapTemplateRetriever(final HashMap<String, LdapTemplate> inLdapTemplates,
            final LdapTemplate inDefaultLdapTemplate)
    {
        super(inLdapTemplates, inDefaultLdapTemplate);
    }

    /**
     * Uses specified search string and/or template key to return an LdapTemplate used to make query. If search string
     * contains domain, that is used to try to find LdapTemplate, if not, template key is used to attempt to find it, if
     * neither is present or results in a LdapTemplate being found, default template is used.
     * 
     * @param inLdapLookupRequest
     *            {@link LdapLookupRequest}.
     * @return {@link LdapTemplate} based on {@link LdapLookupRequest}, or default if none found.
     */
    @Override
    protected LdapTemplate retrieveLdapTemplate(final LdapLookupRequest inLdapLookupRequest)
    {
        String templateKey = inLdapLookupRequest.getTemplateKey();
        String searchString = inLdapLookupRequest.getQueryString();

        LdapTemplate result = null;

        if (searchString != null && searchString.contains("\\"))
        {
            String[] domainQueryArr = searchString.split("\\\\");

            // Example: "domain\attribute=foo" will give domain of "domain" and searchString of "attribute=foo".
            String domain = domainQueryArr[0].toLowerCase();
            searchString = domainQueryArr[1];

            // no matter what domain we get, modify search string to remove domain.
            inLdapLookupRequest.setQueryString(searchString);

            // if domain is not null/empty, try to use it for ldap template.
            if (domain != null && !domain.isEmpty())
            {
                result = getLdapTemplates().get(domain);
            }

            // no dice on domain from search string, try template key.
            if (result == null && templateKey != null)
            {
                result = getLdapTemplates().get(templateKey);
            }

            // if not found, give back default, but log as error.
            if (result == null)
            {
                result = getDefaultLdapTemplate();
                log.error("Domain specified (" + domain + "), but not found in list of templates. "
                        + "Attempting search on default template");
            }

            if (log.isDebugEnabled())
            {
                log.debug("Domain specified, searching only on "
                        + ((LdapContextSource) result.getContextSource()).getUrls()[0] 
                        + " with base: " + ((LdapContextSource) result.getContextSource()).getBaseLdapPathAsString() 
                        + " for : " + searchString);
            }
        }
        else
        {
            // no domain specified in search string, try template key.
            if (templateKey != null)
            {
                result = getLdapTemplates().get(templateKey);
            }

            // no dice with template key, use default.
            if (result == null)
            {
                result = getDefaultLdapTemplate();
            }

            if (log.isDebugEnabled())
            {
                log.debug("No domain specified, searching only on "
                        + ((LdapContextSource) result.getContextSource()).getUrls()[0] + " for : " + searchString);
            }
        }
        return result;
    }
}
