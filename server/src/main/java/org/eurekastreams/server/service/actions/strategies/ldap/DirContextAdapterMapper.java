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
package org.eurekastreams.server.service.actions.strategies.ldap;

import org.springframework.ldap.core.ContextMapper;

/**
 * This is a ContextMapper that is just a pass-through,
 * so search returns a list of DirContextAdapter objects.
 * This is used by the MembershipCriteriaToPersonMapper when searching
 * for subgroups.
 *
 */
public class DirContextAdapterMapper implements ContextMapper
{

    /**
     * Pass-through mapper returns the object passed in.
     * @param arg The object to map.
     * @return The same object that was passed in as arg.
     */
    public Object mapFromContext(final Object arg)
    {
        return arg;
    }

}
