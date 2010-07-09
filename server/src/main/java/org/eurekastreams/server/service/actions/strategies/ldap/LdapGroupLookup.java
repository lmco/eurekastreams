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

import java.util.HashMap;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.service.actions.strategies.ldap.filters.FilterCreator;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AbstractFilter;

/**
 * Checks to see if a group exists in LDAP.
 */
public class LdapGroupLookup
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Used to interface with LDAP.
     */
    private HashMap<String, LdapTemplate> ldapTemplate;

    /**
     * LDAP filter creator.
     */
    private FilterCreator ldapFilter;

    /**
     * The default LdapTemplate to use.
     */
    private LdapTemplate defaultLdapTemplate;

    /**
     * Sets the LDAP template.
     *
     * @param inLdapTemplate
     *            the template.
     * @param inLdapFilter
     *            filter creator.
     */
    public LdapGroupLookup(final HashMap<String, LdapTemplate> inLdapTemplate, final FilterCreator inLdapFilter)
    {
        ldapTemplate = inLdapTemplate;
        ldapFilter = inLdapFilter;
    }

    /**
     * Finds a group.
     *
     * @param inSearchString
     *            the string to search for.
     *
     * @return true if group was found, false otherwise.
     */
    @SuppressWarnings("unchecked")
    public boolean groupExists(final String inSearchString)
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

        }
        else
        {
            ldap = (defaultLdapTemplate == null)
                    ? ((LdapTemplate) ldapTemplate.values().toArray()[0])
                    : defaultLdapTemplate;

            if (log.isDebugEnabled())
            {
                    log.debug("No domain specified, searching only on "
                    + ((LdapContextSource) ldap.getContextSource()).getUrls()[0]
                    + " for : " + searchString);
            }
        }

        SearchControls searchControls = new SearchControls();

        searchControls.setCountLimit(5);
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        AbstractFilter filter = ldapFilter.getFilter("sAMAccountName=" + searchString);
        
        try
        {
            if (log.isTraceEnabled())
            {
                log.trace("Beginning group ldap search.  Filter: " + filter.encode());
            }

                List results = ldap.search("", filter.encode(), searchControls, new AttributesMapper()
                {
                    @Override
                    public Object mapFromAttributes(final Attributes attributes) throws NamingException
                    {
                        //do nothing - just checking existence
                        return null;
                    }
                });
                
            if (log.isTraceEnabled())
            {
                log.trace("found " + results.size() + " group; ldap search complete");
            }
            
            if (results.size() > 0)
            {
                return true;
            }
        }
        catch (Exception e)
        {
            log.error("Exception caught while searching LDAP for group.", e);
        }
        return false;
        
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
