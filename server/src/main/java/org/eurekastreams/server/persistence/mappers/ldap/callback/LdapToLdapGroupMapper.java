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
package org.eurekastreams.server.persistence.mappers.ldap.callback;

import org.eurekastreams.server.persistence.mappers.ldap.LdapGroup;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;

/**
 * {@link ContextMapper} for converting search results to {@link LdapGroup} objects.
 * 
 */
public class LdapToLdapGroupMapper implements ContextMapper
{
    /**
     * {@inheritDoc}
     */
    @Override
    public Object mapFromContext(final Object inCtx)
    {
        DirContextAdapter dir = (DirContextAdapter) inCtx;

        return new LdapGroup(new DistinguishedName(dir.getDn()));
    }

}
