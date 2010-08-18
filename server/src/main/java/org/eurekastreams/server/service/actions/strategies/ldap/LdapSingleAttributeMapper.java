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

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;

/**
 * Simple LDAP mapper to return a single attribute from an LDAP lookup.
 */
public class LdapSingleAttributeMapper implements AttributesMapper
{
    /** The name of the LDAP attribute to extract. */
    private String attributeName;

    /** Value to use if not found. */
    private String defaultValue = null;

    /**
     * Constructor.
     *
     * @param inAttributeName
     *            The name of the LDAP attribute to extract.
     */
    public LdapSingleAttributeMapper(final String inAttributeName)
    {
        attributeName = inAttributeName;
    }

    /**
     * Constructor.
     *
     * @param inAttributeName
     *            The name of the LDAP attribute to extract.
     * @param inDefaultValue
     *            Value to use if not found.
     */
    public LdapSingleAttributeMapper(final String inAttributeName, final String inDefaultValue)
    {
        attributeName = inAttributeName;
        defaultValue = inDefaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object mapFromAttributes(final Attributes attrs) throws NamingException
    {
        Attribute attr = attrs.get(attributeName);
        return attr != null ? attr.get().toString() : defaultValue;
    }
}
