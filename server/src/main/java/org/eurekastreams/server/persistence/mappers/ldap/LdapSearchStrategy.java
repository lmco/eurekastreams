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

import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler;
import org.springframework.ldap.core.LdapTemplate;

/**
 * This interface provides the signature for a strategy to search Ldap.
 *
 */
public interface LdapSearchStrategy
{
    /**
     * This method is responsible for searching ldap with the provided parameters. The list contained within the
     * {@link CollectingNameClassPairCallbackHandler} will be populated with the search results from the ldap query.
     *
     * @param inLdapTemplate
     *            - provides the ldap search functionality from within spring.
     * @param inEncodedFilter
     *            - encoded string based filter used to search ldap.
     * @param inSearchControls
     *            - controls on how the search is performed. Most commonly contains the size of the result set and the
     *            search scope.
     * @param inHandler
     *            - strategy for collecting results and converting them to a domain object.
     */
    void searchLdap(LdapTemplate inLdapTemplate, String inEncodedFilter, SearchControls inSearchControls,
            CollectingNameClassPairCallbackHandler inHandler);
}
