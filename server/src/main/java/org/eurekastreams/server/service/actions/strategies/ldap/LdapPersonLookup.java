/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.service.actions.strategies.PersonLookupStrategy;
import org.eurekastreams.server.service.actions.strategies.ldap.filters.FilterCreator;
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AbstractFilter;

/**
 * Finds a list of people from LDAP.
 */
public class LdapPersonLookup implements PersonLookupStrategy
{

    /**
     * Max LDAP results per page.
     */
    private static final int RESULTS_PER_PAGE = 100;

    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(LdapPersonLookup.class);

    /**
     * Used to interface with LDAP.
     */
    private HashMap<String, LdapTemplate> ldapTemplate;

    /**
     * LDAP filter creator.
     */
    private FilterCreator ldapFilter;

    /**
     * The person mapper.
     */
    private CollectingNameClassPairCallbackHandler handler;

    /**
     * Object to append people to.
     */
    private PeopleAppender appender;

    /**
     * The default LdapTemplate to use.
     */
    private LdapTemplate defaultLdapTemplate;

    /**
     * Strategy for performing the search against ldap.
     */
    private LdapSearchStrategy ldapSearchStrategy;

    /**
     * Sets the LDAP template.
     *
     * @param inLdapTemplate
     *            the template.
     * @param inHandler
     *            maps an ldap records to a person.
     * @param inAppender
     *            appends the person to a list.
     * @param inLdapFilter
     *            filter creator.
     * @param inLdapSearchStrategy
     *            search strategy to use for querying ldap.
     */
    public LdapPersonLookup(final HashMap<String, LdapTemplate> inLdapTemplate,
            final CollectingNameClassPairCallbackHandler inHandler, final PeopleAppender inAppender,
            final FilterCreator inLdapFilter, final LdapSearchStrategy inLdapSearchStrategy)
    {
        ldapTemplate = inLdapTemplate;
        appender = inAppender;
        ldapFilter = inLdapFilter;
        handler = inHandler;
        ldapSearchStrategy = inLdapSearchStrategy;
    }

    /**
     * Finds a list of people.
     *
     * @param inSearchString
     *            the string to search for.
     * @param searchUpperBound
     *            max results to return in multiples of 100.
     *
     * @return a list of people found.
     */
    public List<Person> findPeople(final String inSearchString, final int searchUpperBound)
    {
        String searchString = inSearchString;
        String domain = "";

        LdapTemplate ldap = null;

        if (searchString.contains("\\"))
        {
            String[] domainQueryArr = searchString.split("\\\\");

            domain = domainQueryArr[0];
            searchString = domainQueryArr[1];

            ldap = ldapTemplate.get(domain);

            log.debug("Domain specified, searching only on "
                    + ((LdapContextSource) ldap.getContextSource()).getUrls()[0]
                    + " for : " + searchString);
        }
        else
        {
            ldap = (defaultLdapTemplate == null)
                    ? ((LdapTemplate) ldapTemplate.values().toArray()[0])
                    : defaultLdapTemplate;

            log.debug("No domain specified, searching only on "
                    + ((LdapContextSource) ldap.getContextSource()).getUrls()[0]
                    + " for : " + searchString);
        }

        SearchControls searchControls = new SearchControls();

        searchControls.setCountLimit(searchUpperBound);
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        AbstractFilter filter = ldapFilter.getFilter(searchString);

        HashMap<String, Person> personMap = new HashMap<String, Person>();

        appender.setPeople(personMap);
        appender.setDefaultLdapTemplate(ldap);

        try
        {
            ldapSearchStrategy.searchLdap(ldap, filter.encode(), searchControls, handler);
        }
        catch (Exception e)
        {
            log.error("Exception caught while searching LDAP.", e);
        }

        return new ArrayList<Person>(personMap.values());
    }

    /**
     * @return the defaultLdapTemplate
     */
    public LdapTemplate getDefaultLdapTemplate()
    {
        return defaultLdapTemplate;
    }

    /**
     * @param inDefaultLdapTemplate the defaultLdapTemplate to set
     */
    public void setDefaultLdapTemplate(final LdapTemplate inDefaultLdapTemplate)
    {
        this.defaultLdapTemplate = inDefaultLdapTemplate;
    }
}
