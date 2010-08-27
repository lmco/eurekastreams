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

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.ldap.callback.CallbackHandlerFactory;
import org.eurekastreams.server.persistence.mappers.ldap.filters.FilterCreator;
import org.eurekastreams.server.persistence.mappers.ldap.templateretrievers.LdapTemplateRetriever;
import org.eurekastreams.server.persistence.mappers.requests.LdapLookupRequest;
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AbstractFilter;

/**
 * Mapper for doing ldap lookups.
 * 
 * @param <Type>
 *            Type to be returned from lookup.
 */
public class LdapLookup<Type> implements DomainMapper<LdapLookupRequest, List<Type>>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * {@link LdapTemplateRetriever}.
     */
    private LdapTemplateRetriever ldapTemplateRetriever;

    /**
     * LDAP filter creator for finding group by name.
     */
    private FilterCreator filterCreator;

    /**
     * The {@link CallbackHandlerFactory}.
     */
    private CallbackHandlerFactory handlerFactory;

    /**
     * Strategy for performing the search against ldap.
     */
    private LdapSearchStrategy ldapSearchStrategy;

    /**
     * Constructor.
     * 
     * @param inLdapTemplateRetriever
     *            {@link LdapTemplateRetriever}.
     * @param inFilterCreator
     *            LDAP filter creator.
     * @param inHandlerFactory
     *            The {@link CallbackHandlerFactory}.
     * @param inLdapSearchStrategy
     *            Strategy for performing the search against ldap.
     */
    public LdapLookup(final LdapTemplateRetriever inLdapTemplateRetriever, final FilterCreator inFilterCreator,
            final CallbackHandlerFactory inHandlerFactory, final LdapSearchStrategy inLdapSearchStrategy)
    {
        ldapTemplateRetriever = inLdapTemplateRetriever;
        filterCreator = inFilterCreator;
        handlerFactory = inHandlerFactory;
        ldapSearchStrategy = inLdapSearchStrategy;
    }

    /**
     * Execute an ldap query based on {@link LdapLookupRequest} parameters and this DAO's configuration.
     * LdapLookupRequest is used for search upper bound, the {@link LdapTemplate}, and the search string. The rest of
     * ldap query functionality is determined by DAO configuration.
     * 
     * @param inRequest
     *            {@link LdapLookupRequest}.
     * @return List of objects found as as result of ldap query.
     * 
     */
    @Override
    public List<Type> execute(final LdapLookupRequest inRequest)
    {
        // get ldap template.
        LdapTemplate template = ldapTemplateRetriever.getLdapTemplate(inRequest);

        // set up search controls.
        SearchControls searchControls = new SearchControls();
        searchControls.setCountLimit(inRequest.getSearchUpperBound());
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        // add passed in attribute criteria to filter.
        AbstractFilter abstractFilter = filterCreator.getFilter(inRequest.getQueryString());

        // get the configured CollectingNameClassPairCallbackHandler to use for query.
        CollectingNameClassPairCallbackHandler collectingHandler = handlerFactory.getCallbackHandler();

        // execute query.
        ldapSearchStrategy.searchLdap(template, abstractFilter.encode(), searchControls, collectingHandler);

        // get results gathered by CollectingNameClassPairCallbackHandler.
        List<Type> rawResults = collectingHandler.getList();

        // Results contain nulls if the context/attribute mappers were unable to create objects, so pull them out.
        List<Type> results = new ArrayList<Type>();
        for (Type t : rawResults)
        {
            if (t != null)
            {
                results.add(t);
            }
        }

        return results;
    }

}
