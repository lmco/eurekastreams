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

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.eurekastreams.server.persistence.mappers.ldap.callback.LdapSingleAttributeMapper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests LdapSingleAttributeMapper.
 */
public class LdapSingleAttributeMapperTest
{
    /** Test data. */
    private static final String ATTR_NAME = "anAttribute";

    /** Test data. */
    private static final String ATTR_VALUE = "some data";

    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: LDAP attribute collection. */
    private Attributes attributes = context.mock(Attributes.class);

    /** Fixture: Single LDAP attribute. */
    private Attribute attribute = context.mock(Attribute.class, ATTR_NAME);

    /** SUT. */
    private LdapSingleAttributeMapper sut;

    /**
     * Setup before each test.
     *
     * @throws NamingException
     *             Shouldn't.
     */
    @Before
    public void setUp() throws NamingException
    {
        context.checking(new Expectations()
        {
            {
                allowing(attribute).get().toString();
                will(returnValue(ATTR_VALUE));

                allowing(attributes).get(ATTR_NAME);
                will(returnValue(attribute));

                allowing(attributes).get(with(any(String.class)));
                will(returnValue(null));
            }
        });
    }

    /**
     * Tests mapping.
     *
     * @throws NamingException
     *             Shouldn't.
     */
    @Test
    public void testMapFromAttributesSuccess() throws NamingException
    {
        sut = new LdapSingleAttributeMapper(ATTR_NAME);

        Object result = sut.mapFromAttributes(attributes);
        context.assertIsSatisfied();
        assertEquals(ATTR_VALUE, result);
    }

    /**
     * Tests mapping.
     *
     * @throws NamingException
     *             Shouldn't.
     */
    @Test
    public void testMapFromAttributesNotFound() throws NamingException
    {
        sut = new LdapSingleAttributeMapper("wrong name");

        Object result = sut.mapFromAttributes(attributes);
        context.assertIsSatisfied();
        assertNull(result);
    }

    /**
     * Tests mapping.
     *
     * @throws NamingException
     *             Shouldn't.
     */
    @Test
    public void testMapFromAttributesNotFoundSpecifiedDefault() throws NamingException
    {
        final String defaultValue = "my default";

        sut = new LdapSingleAttributeMapper("wrong name", defaultValue);

        Object result = sut.mapFromAttributes(attributes);
        context.assertIsSatisfied();
        assertEquals(defaultValue, result);
    }

}
