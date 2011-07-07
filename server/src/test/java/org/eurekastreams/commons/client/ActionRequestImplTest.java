/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.commons.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for ActionRequestImpl.
 * 
 */
@SuppressWarnings("deprecation")
public class ActionRequestImplTest
{
    /**
     * Subject under test.
     */
    private ActionRequestImpl<String> sut = null;

    /**
     * Testing getters & setters.
     */
    @Test
    public void testSetters()
    {
        String param = "param";

        sut = new ActionRequestImpl<String>();

        sut.setActionKey("foo");
        assertEquals("key doesn't match", "foo", sut.getActionKey());

        int id = 2;
        sut.setId(Integer.valueOf(id));
        assertEquals("id doesn't match", id, sut.getId());

        sut.setParam(param);
        assertEquals("params don't match", param, sut.getParam());

        sut.setResponse("bar");
        assertEquals("response doesn't match", "bar", sut.getResponse());

        final String sessionId = "thisIsMySession";
        sut.setSessionId(sessionId);
        assertEquals("Session id doesn't match", sessionId, sut.getSessionId());
    }

    /**
     * Tests the hashcode generation.
     */
    @Test
    public void testHashcode()
    {
        String paramsA1 = "param";
        String paramsA2 = "param";
        Long paramsB = 5L;

        ActionRequestImpl<String> rqst1 = new ActionRequestImpl<String>("action1", paramsA1);
        ActionRequestImpl<String> rqst2 = new ActionRequestImpl<String>("action1", paramsA2);
        ActionRequestImpl<String> rqst3 = new ActionRequestImpl<String>("action2", paramsB);

        assertEquals("Equal actions should have same hashcode", rqst1.hashCode(), rqst2.hashCode());

        assertTrue("Unrelated actions should have different hashcodes.", rqst1.hashCode() != rqst3.hashCode());
    }

    /**
     * Testing equals.
     */
    @Test
    public void testEquals()
    {

        String message;

        String param = "param";
        String key = "foo";
        sut = new ActionRequestImpl<String>(key, param);
        ActionRequestImpl<String> other = new ActionRequestImpl<String>();

        message = "other classes shouldn't match";
        assertFalse(message, sut.equals(null));
        assertFalse(message, sut.equals("another class"));

        message = "should not match with unequal key or parameters";
        assertFalse(message, sut.equals(other));
        other.setActionKey(key);
        assertFalse(message, sut.equals(other));

        message = "should match with equal key and parameters";
        other.setActionKey(key);
        other.setParam(param);
        assertTrue(message, sut.equals(other));
    }

    /**
     * Tests several combinations of null parameter lists.
     */
    @Test
    public void testEqualsNullParameter()
    {
        final String key = "KEY";

        // use separate arrays to insure equals compares the contents of the arrays, not the actual array itself
        String params1 = "param1param2";
        String params2 = "param1param2";
        String params3 = "different";

        ActionRequestImpl<String> null1 = new ActionRequestImpl<String>(key, null);
        ActionRequestImpl<String> null2 = new ActionRequestImpl<String>(key, null);
        ActionRequestImpl<String> nonNull1 = new ActionRequestImpl<String>(key, params1);
        ActionRequestImpl<String> nonNull2 = new ActionRequestImpl<String>(key, params2);
        ActionRequestImpl<String> different = new ActionRequestImpl<String>(key, params3);

        // null <-> null should be equal
        assertTrue("Null-null compare should be equal.", null1.equals(null2));

        // null <-> non-null should be unequal
        assertFalse("Null-non-null compare should be unequal.", null1.equals(nonNull2));

        // non-null <-> null should be unequal
        assertFalse("Non-null-null compare should be unequal.", nonNull1.equals(null2));

        // non-null <-> non-null should be equal
        assertTrue("Non-null-non-null compare should be equal.", nonNull1.equals(nonNull2));

        // different <-> non-null should be unequal
        assertFalse("Non-null-non-null compare should be equal.", different.equals(nonNull1));

        // non-null <-> different should be unequal
        assertFalse("Non-null-non-null compare should be equal.", nonNull1.equals(different));
    }
}
