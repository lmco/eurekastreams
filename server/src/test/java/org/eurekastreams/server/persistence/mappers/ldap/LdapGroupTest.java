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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.springframework.ldap.core.DistinguishedName;

/**
 * Test for LdapGroup.
 * 
 */
public class LdapGroupTest
{
    /**
     * Test.
     */
    @Test
    public void test()
    {
        DistinguishedName dn = new DistinguishedName();
        LdapGroup sut = new LdapGroup(dn);
        ArrayList<String> source = new ArrayList<String>(Arrays.asList("blah"));
        sut.setSourceList(source);

        assertEquals(dn, sut.getDistinguishedName());
        assertEquals(source, sut.getSourceList());
    }
}
