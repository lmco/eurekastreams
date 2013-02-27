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
package org.eurekastreams.server.persistence.mappers.ldap;

import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * This strategy allows for searching ldap with a {@link PagedResultsDirContextProcessor}. This strategy will only work
 * in Ldap environments configured to allow paging. If paging is not supported in the target ldap environment, configure
 * the DefaultLdapSearchStrategy which does not use paging.
 * 
 * This strategy provides a mechanism for setting the page size or using a default of 100. This could be a performance
 * optimization for the target environment if a page size is too large or small.
 * 
 */
public class PagedLdapSearchStrategy implements LdapSearchStrategy
{
    /**
     * Logging instance.
     */
    private final Log logger = LogFactory.make();

    /**
     * Max LDAP results per page.
     */
    private static final int DEFAULT_RESULTS_PER_PAGE = 100;

    /**
     * The number of results per page.
     */
    private final int resultsPerPage;

    /**
     * Default constructor which sets the results per page to the default of 100.
     */
    public PagedLdapSearchStrategy()
    {
        resultsPerPage = DEFAULT_RESULTS_PER_PAGE;
    }

    /**
     * Constructor.
     * 
     * @param inPageSize
     *            - page size to use when querying ldap.
     */
    public PagedLdapSearchStrategy(final int inPageSize)
    {
        resultsPerPage = inPageSize;
    }

    /**
     * {@inheritDoc}
     * 
     * This method provides the implementation for searching ldap with a {@link PagedResultsDirContextProcessor}.
     */
    @Override
    public void searchLdap(final LdapTemplate inLdapTemplate, final String inEncodedFilter,
            final SearchControls inSearchControls, final CollectingNameClassPairCallbackHandler inHandler)
    {
        PagedResultsDirContextProcessor pager = new PagedResultsDirContextProcessor(resultsPerPage);

        if (logger.isTraceEnabled())
        {
            logger.trace("Beginning paged ldap search with " + resultsPerPage + " results per page.  Filter: "
                    + inEncodedFilter + " using dc: "
            		+ ((LdapContextSource) inLdapTemplate.getContextSource()).getUrls()[0] + " using baseLdapPath: "
            		+ ((LdapContextSource) inLdapTemplate.getContextSource()).getBaseLdapPathAsString());
        }

        do
        {
            // Although the SearchControls object contains a limit on the max results for the
            // search, the paging processor works over the entire result set so the loop is
            // cut short manually.
            inLdapTemplate.search("", inEncodedFilter, inSearchControls, inHandler, pager);

            pager = new PagedResultsDirContextProcessor(resultsPerPage, pager.getCookie());
        }
        while (pager.getCookie() != null && pager.getCookie().getCookie() != null
                && inHandler.getList().size() < inSearchControls.getCountLimit());

        if (logger.isTraceEnabled())
        {
            logger.trace("Paged ldap search complete with " + inHandler.getList().size() + " results retrieved");
        }
    }
}
