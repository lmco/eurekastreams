/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.security.ldap;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.ldap.LdapAuthoritiesPopulator;

/**
 * A "do-nothing implementation of Spring's LdapAuthoritiesPopulator interface.
 * LDAP authentication requires a non null instance of this interface, but we
 * are using a custom UserDetailsService to get load authorities, so we just
 * need this to be a placeholder.
 * 
 */
public class DummyLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator
{
    /**
     * A "do-nothing implementation of Spring's LdapAuthoritiesPopulator.
     * interface
     * @param arg0 Doesn't matter, not used.
     * @param arg1 Doesn't matter, not used.
     * @return Empty GrantedAuthority array.
     */
    public GrantedAuthority[] getGrantedAuthorities(final DirContextOperations arg0,
            final String arg1)
    {
        return new GrantedAuthority[0];
    }

}
