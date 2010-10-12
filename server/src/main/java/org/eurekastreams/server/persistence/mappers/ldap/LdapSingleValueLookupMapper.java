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

import java.util.List;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.LdapLookupRequest;

/**
 * Uses an LDAP query to look up a single entry in LDAP and return a single attribute (e.g. given a user's account id,
 * return the UPN). This is essentially just an adapter to convert the request and response types so it can be used with
 * other String-to-String mappers.
 */
public class LdapSingleValueLookupMapper implements DomainMapper<String, String>
{
    /** Mapper to query LDAP. */
    private DomainMapper<LdapLookupRequest, List<String>> ldapQueryMapper;

    /**
     * Constructor.
     *
     * @param inLdapQueryMapper
     *            Mapper to query LDAP.
     */
    public LdapSingleValueLookupMapper(final DomainMapper<LdapLookupRequest, List<String>> inLdapQueryMapper)
    {
        ldapQueryMapper = inLdapQueryMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String execute(final String inRequest)
    {
        // use LDAP mapper to fetch desired attribute for user
        List<String> results = ldapQueryMapper.execute(new LdapLookupRequest(inRequest));

        return results.isEmpty() ? null : results.get(0);
    }
}
