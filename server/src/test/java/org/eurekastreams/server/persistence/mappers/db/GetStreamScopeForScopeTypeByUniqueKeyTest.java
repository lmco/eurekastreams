/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.db;

import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetStreamScopeForScopeTypeByUniqueKey.
 */
public class GetStreamScopeForScopeTypeByUniqueKeyTest extends MapperTest
{
    /**
     * System under test - throws exception when result not found.
     */
    private GetStreamScopeForScopeTypeByUniqueKey sutThatThrowsExceptionWhenNotFound;

    /**
     * System under test - returns zero when result not found.
     */
    private GetStreamScopeForScopeTypeByUniqueKey sutThatReturnsZeroWhenNotFound;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sutThatThrowsExceptionWhenNotFound = new GetStreamScopeForScopeTypeByUniqueKey(ScopeType.PERSON, true);
        sutThatThrowsExceptionWhenNotFound.setEntityManager(getEntityManager());

        sutThatReturnsZeroWhenNotFound = new GetStreamScopeForScopeTypeByUniqueKey(ScopeType.PERSON, false);
        sutThatReturnsZeroWhenNotFound.setEntityManager(getEntityManager());
    }

    /**
     * Test execute when the scope exists.
     */
    @Test
    public void testWhenExists()
    {
        final Long expectedId = 3L;
        final Long destinationEntityId = 4507L;
        StreamScope result = sutThatThrowsExceptionWhenNotFound.execute("csagan");
        org.junit.Assert.assertEquals(new Long(expectedId), new Long(result.getId()));
        org.junit.Assert.assertEquals(destinationEntityId, result.getDestinationEntityId());
    }

    /**
     * Test execute when the scope does not exists and we're supposed to throw exception.
     */
    @Test(expected = RuntimeException.class)
    public void testEmptyWhenDoesNotExistAndThrowsException()
    {
        // this is a valid group ScopeType, so exercise that ScopeType is obeyed.
        sutThatThrowsExceptionWhenNotFound.execute("group1");
    }

    /**
     * Test execute when the scope does not exists, and we're supposed to return zero.
     */
    @Test
    public void testEmptyWhenDoesNotExistAndReturnsZero()
    {
        // this is a valid group ScopeType, so exercise that ScopeType is obeyed.
        org.junit.Assert.assertNull(sutThatReturnsZeroWhenNotFound.execute("group1"));
    }

    /**
     * Test execute when empty request, and we're supposed to throw exception.
     */
    @Test(expected = RuntimeException.class)
    public void testEmptyParamsWhenDoesNotExistAndThrowsException()
    {
        // this is a valid group ScopeType, so exercise that ScopeType is obeyed.
        sutThatThrowsExceptionWhenNotFound.execute(null);
    }

    /**
     * Test execute when empty request, and we're supposed to return zero.
     */
    @Test
    public void testEmptyParamsWhenDoesNotExistAndReturnsZero()
    {
        // this is a valid group ScopeType, so exercise that ScopeType is obeyed.
        org.junit.Assert.assertNull(sutThatReturnsZeroWhenNotFound.execute(null));
    }

}
