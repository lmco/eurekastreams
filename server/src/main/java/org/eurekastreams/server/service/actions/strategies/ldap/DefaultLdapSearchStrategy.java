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
package org.eurekastreams.server.service.actions.strategies.ldap;

import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler;
import org.springframework.ldap.core.LdapTemplate;

/**
 * This is a default implementation of the {@link LdapSearchStrategy} which performs an ldap search using the supplied
 * {@link LdapTemplate}, filter, {@link SearchControls} and {@link CollectingNameClassPairCallbackHandler}. If the
 * target Ldap environment supports paging, the PagedLdapSearchStrategy provides better performance.
 *
 */
public class DefaultLdapSearchStrategy implements LdapSearchStrategy
{
    /**
     * Local logger instance.
     */
    private final Log logger = LogFactory.make();

    /**
     * {@inheritDoc}.
     *
     * This method performs the default ldap search with the provided filter criteria.
     */
    @Override
    public void searchLdap(final LdapTemplate inLdapTemplate, final String inEncodedFilter,
            final SearchControls inSearchControls, final CollectingNameClassPairCallbackHandler inHandler)
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("Beginning direct search of ldap with filter: " + inEncodedFilter);
        }

        inLdapTemplate.search("", inEncodedFilter, inSearchControls, inHandler);

        if (logger.isTraceEnabled())
        {
            logger.trace("Completed direct search of ldap with " + inHandler.getList().size() + " results.");
        }
    }
}
