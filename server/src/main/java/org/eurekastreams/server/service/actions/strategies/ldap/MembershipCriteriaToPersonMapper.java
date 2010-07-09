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

import java.util.HashMap;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.service.actions.strategies.ldap.filters.FilterCreator;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.BaseLdapPathAware;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * Maps an LDAP group to a list of people.
 */
public class MembershipCriteriaToPersonMapper implements ContextMapper, PeopleAppender, BaseLdapPathAware
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(MembershipCriteriaToPersonMapper.class);

    /**
     * Max results per person search.
     */
    private static final int DEFAULT_MAX_RESULTS = Integer.MAX_VALUE;

    /**
     * Base DN for LDAP queries.
     */
    private String baseLdapPath;

    /**
     * Collection of LDAP templates available for search.
     */
    private HashMap<String, LdapTemplate> ldapTemplate = null;

    /**
     * Default LdapTemplate to use for searches.
     */
    private LdapTemplate defaultLdapTemplate = null;

    /**
     * List of people.
     */
    private HashMap<String, Person> people = null;

    /**
     * {@link LdapPersonLookup}.
     */
    private final LdapPersonLookup personLookupStrategy;

    /**
     * Filter used to find the subgroups within the membership criteria results.
     */
    private final FilterCreator groupFilter;

    /**
     * Attribute name for member.
     */
    private String memberAttrib = "member";

    /**
     * Attribute name for objectClass.
     */
    private String objectClassAttrib = "objectClass";

    /**
     * Value for objectClass attribute that represents a person.
     */
    private String personObjectClassAttribValue = "person";

    /**
     * Value for objectClass attribute that represents a group.
     */
    private String groupObjectClassAttribValue = "group";

    /**
     * @param inPeople
     *            the people list.
     */
    public void setPeople(final HashMap<String, Person> inPeople)
    {
        people = inPeople;
    }

    /**
     * Constructor.
     *
     * @param inLdapTemplate
     *            the LDAP template for search.
     * @param inBaseLdapPath
     *            base LDAP path.
     * @param inDefaultLdapTemplate
     *            the ldapTemplate to use, if null it uses first value from inLdapTemplate collection.
     * @param inPersonLookupStrategy
     *            strategy for looking up people.
     * @param inGroupFilter
     *            FilterCreator object configured for finding matches to subgroups within the membership criteria search
     *            query results.
     */
    public MembershipCriteriaToPersonMapper(final HashMap<String, LdapTemplate> inLdapTemplate,
            final String inBaseLdapPath, final LdapTemplate inDefaultLdapTemplate,
            final LdapPersonLookup inPersonLookupStrategy, final FilterCreator inGroupFilter)
    {
        ldapTemplate = inLdapTemplate;
        baseLdapPath = inBaseLdapPath;
        defaultLdapTemplate = inDefaultLdapTemplate == null ? (LdapTemplate) ldapTemplate.values().toArray()[0]
                : inDefaultLdapTemplate;
        personLookupStrategy = inPersonLookupStrategy;
        groupFilter = inGroupFilter;
    }

    /**
     * Maps to a list of people.
     *
     * @param inDir
     *            the context.
     * @return null.
     */
    @SuppressWarnings("unchecked")
    public Object mapFromContext(final Object inDir)
    {
        DirContextAdapter dir = (DirContextAdapter) inDir;

        DistinguishedName dn = new DistinguishedName(dir.getDn());
        LdapTemplate groupLdap = getLdapTemplateByRelativeDN(dn.toCompactString());
        String groupLdapUrl = ((LdapContextSource) groupLdap.getContextSource()).getUrls()[0];

        dn.prepend(new DistinguishedName(baseLdapPath));
        String memberOfAttribQuery = "memberOf=" + dn.toCompactString();

        personLookupStrategy.setDefaultLdapTemplate(groupLdap);
        // Find all of the people that are members of the group.
        List<Person> results = personLookupStrategy.findPeople(memberOfAttribQuery, DEFAULT_MAX_RESULTS);

        if (log.isDebugEnabled())
        {
            log.debug("Searched " + groupLdapUrl + " for direct members of " + dn.toCompactString() + "and found: "
                    + results.size());
        }

        SearchControls searchControls = new SearchControls();
        searchControls.setCountLimit(DEFAULT_MAX_RESULTS);
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        // Find all of the sub-groups that are a member of the group.
        List groupList = groupLdap.search("", (groupFilter.getFilter(memberOfAttribQuery)).encode(), searchControls,
                new DirContextAdapterMapper());

        if (log.isDebugEnabled())
        {
            log.debug("Searched " + groupLdapUrl + " for subgroups in " + dn.toCompactString() + "and found: "
                    + groupList.size());
        }

        for (Object tempDir : groupList)
        {
            mapFromContext(tempDir);
        }

        for (Person p : results)
        {
            people.put(p.getAccountId(), p);
        }
        return null;
    }

    /**
     * Method to return correct ldapTemplate based on relative DN of group.
     *
     * @param name
     *            relative group DN
     * @return ldapTemplate associated with key in ldapTemplate collection if "dc='key' is found in the name parameter.
     *         If not found, defaultLdapTemplate is used.
     */
    private LdapTemplate getLdapTemplateByRelativeDN(final String name)
    {
        String testString = name.toLowerCase();

        for (String key : ldapTemplate.keySet())
        {
            if (testString.indexOf("dc=" + key.toLowerCase()) != -1)
            {
                return ldapTemplate.get(key);
            }
        }
        return defaultLdapTemplate;
    }

    /**
     * Sets the base DN for LDAP queries.
     *
     * @param inBaseLdapPath
     *            the base path.
     */
    public void setBaseLdapPath(final DistinguishedName inBaseLdapPath)
    {
        baseLdapPath = inBaseLdapPath.toCompactString();
    }

    /**
     * @return the objectClassAttr
     */
    public String getObjectClassAttrib()
    {
        return objectClassAttrib;
    }

    /**
     * @param inObjectClassAttr
     *            the objectClassAttr to set
     */
    public void setObjectClassAttrib(final String inObjectClassAttr)
    {
        this.objectClassAttrib = inObjectClassAttr;
    }

    /**
     * @return the personObjectClassAttrValue
     */
    public String getPersonObjectClassAttribValue()
    {
        return personObjectClassAttribValue;
    }

    /**
     * @param inPersonObjectClassAttrValue
     *            the personObjectClassAttrValue to set
     */
    public void setPersonObjectClassAttribValue(final String inPersonObjectClassAttrValue)
    {
        this.personObjectClassAttribValue = inPersonObjectClassAttrValue;
    }

    /**
     * @return the groupObjectClassAttrValue
     */
    public String getGroupObjectClassAttribValue()
    {
        return groupObjectClassAttribValue;
    }

    /**
     * @param inGroupObjectClassAttrValue
     *            the groupObjectClassAttrValue to set
     */
    public void setGroupObjectClassAttribValue(final String inGroupObjectClassAttrValue)
    {
        this.groupObjectClassAttribValue = inGroupObjectClassAttrValue;
    }

    /**
     * @return the memberAttrib
     */
    public String getMemberAttrib()
    {
        return memberAttrib;
    }

    /**
     * @param inMemberAttrib
     *            the memberAttrib to set
     */
    public void setMemberAttrib(final String inMemberAttrib)
    {
        this.memberAttrib = inMemberAttrib;
    }

    /**
     * @param inLdapTemplate
     *            the default LdapTemplate to use.
     */
    public void setDefaultLdapTemplate(final LdapTemplate inLdapTemplate)
    {
        defaultLdapTemplate = inLdapTemplate;
    }
}
