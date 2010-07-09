/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.domain;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

/**
 * Test class for PersistentLogin domain entity.
 *
 */
public class PersistentLoginTest
{

    /**
     *
     */
    private final long expireDate = 123456L;

    /**
     * Constructor test for accountId parameter.
     */
    @Test
    public final void testConstructorAccountId()
    {
        PersistentLogin sut = new PersistentLogin("mrburns", "imsurethiswillwork", expireDate);
        assertEquals("AccountId passed into constructor not returned by getAccountId()", "mrburns", sut.getAccountId());
    }

    /**
     * Constructor test for tokenValue parameter.
     */
    @Test
    public final void testContructorTokenValue()
    {
        PersistentLogin sut = new PersistentLogin("mrburns", "imsurethiswillwork", expireDate);
        assertEquals("TokenValue passed into constructor not returned by getTokenValue()", "imsurethiswillwork", sut
                .getTokenValue());

    }

    /**
     * Constructor test for tokenExpirationDate parameter.
     */
    @Test
    public final void testContructorTokenExpirationDate()
    {
        PersistentLogin sut = new PersistentLogin("mrburns", "imsurethiswillwork", expireDate);
        assertEquals("TokenExpirationDate passed into constructor not returned by getTokenExpirationDate()",
                expireDate, sut.getTokenExpirationDate());
    }

    /**
     * Setter test for tokenValue.
     */
    @Test
    public final void testSetTokenValue()
    {
        PersistentLogin sut = new PersistentLogin("mrburns", "imsurethiswillwork", expireDate);
        sut.setTokenValue("snuts");
        assertEquals("TokenValue not equal to value set by setTokenValue()", "snuts", sut.getTokenValue());

    }

    /**
     * Setter test for tokenExpirationDate.
     */
    @Test
    public final void testSetTokenExpirationDate()
    {
        final long newExpireDate = 789123L;
        PersistentLogin sut = new PersistentLogin("mrburns", "imsurethiswillwork", expireDate);
        sut.setTokenExpirationDate(newExpireDate);
        assertEquals("TokenExpirationDate not equal to value set by setTokenExpirationDate()", newExpireDate, sut
                .getTokenExpirationDate());
    }

}
