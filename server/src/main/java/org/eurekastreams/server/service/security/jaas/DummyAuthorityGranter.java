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
package org.eurekastreams.server.service.security.jaas;

import java.security.Principal;
import java.util.Set;

import org.springframework.security.providers.jaas.AuthorityGranter;

/**
 * A "do-nothing implementation of Spring's AuthorityGranter interface.
 * Jaas authentication requires a non null instance of this interface, but we
 * are using a custom UserDetailsService to get load authorities, so we just
 * need this to be a placeholder.
 * 
 */
public class DummyAuthorityGranter implements AuthorityGranter
{

    /**
     * Returns null.
     * @param principal Not used method only returns null.
     * @return Returns null.
     */
    @SuppressWarnings("unchecked")
    public Set grant(final Principal principal)
    {        
        return null;
    }

}
