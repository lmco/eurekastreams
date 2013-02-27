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

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.persistence.mappers.ldap.LdapGroup;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.springframework.ldap.core.DirContextAdapter;

/**
 * Test for LdapToLdapGroupMapper.
 * 
 */
public class LdapToLdapGroupMapperTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * {@link DirContextAdapter}.
     */
    DirContextAdapter ctx = context.mock(DirContextAdapter.class);

    /**
     * System under test.
     */
    LdapToLdapGroupMapper sut = new LdapToLdapGroupMapper();

    /**
     * Test.
     */
    @Test
    public void test()
    {
        final String dn = new String();

        context.checking(new Expectations()
        {
            {
                allowing(ctx).getNameInNamespace();
                will(returnValue(dn));
            }
        });

        LdapGroup result = (LdapGroup) sut.mapFromContext(ctx);

        assertEquals(dn, result.getDistinguishedName().toCompactString());
        context.assertIsSatisfied();
    }

}
