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
package org.eurekastreams.server.service.opensocial.spi;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.testing.FakeGadgetToken;
import org.apache.shindig.social.core.model.MessageCollectionImpl;
import org.apache.shindig.social.core.model.MessageImpl;
import org.apache.shindig.social.opensocial.model.Message;
import org.apache.shindig.social.opensocial.model.MessageCollection;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.UserId.Type;
import org.junit.Before;
import org.junit.Test;

/**
 * This class provides the tests for the MessageService Implementation
 * of the Shindig OpenSocial spec.
 * 
 * Currently this class is only stubbed out.
 */
public class MessageServiceTest
{
    /**
     * This is a fake security token taken from Shindig for testing.
     */
    private static final SecurityToken FAKETOKEN = new FakeGadgetToken();

    /**
     * String to use for test application ids.
     */
    private static final String TEST_APP_ID = "testapp";

    /**
     * Test Message Collection Id.
     */
    private static final String TEST_MESSAGE_COLL_ID = "123456";
    
    /**
     * Test message to use in unit tests.
     */
    private final Message testMessage = new MessageImpl();
    
    /**
     * Test message collection to use in unit tests.
     */
    private final MessageCollection testMsgCollection = new MessageCollectionImpl();
    
    /**
     * Test collection options to use in unit tests.
     */
    private final CollectionOptions testCollectionOptions = new CollectionOptions();
    
    /**
     * A test UserId object to be used during the tests.
     */
    private final UserId testId = new UserId(Type.userId, "123456");
 
    /**
     * System under test.
     */
    private MessageServiceImpl sut;

    /**
     * Setup the tests.
     */
    @Before
    public void setUp()
    {
        sut = new MessageServiceImpl();
    }
    
    /**
     * Basic test for Creating a Message.
     * @throws Exception to catch errors
     */
    @Test
    public void testCreateMessage() throws Exception
    {
        sut.createMessage(testId, TEST_APP_ID, TEST_MESSAGE_COLL_ID, testMessage, FAKETOKEN);
        assertTrue(true);
    }
    
    /**
     * Basic test for Creating a Message Collection.
     * @throws Exception to catch errors
     */
    @Test
    public void testCreateMessageCollection() throws Exception
    {
        sut.createMessageCollection(testId, testMsgCollection, FAKETOKEN);
        assertTrue(true);
    }
    
    /**
     * Basic test for Deleting Messages.
     * @throws Exception to catch errors
     */
    @Test
    public void testDeleteMessages() throws Exception
    {
        sut.deleteMessages(testId, TEST_MESSAGE_COLL_ID, new ArrayList<String>(), FAKETOKEN);
        assertTrue(true);
    }
    
    /**
     * Basic test for Deleting a Message Collection.
     * @throws Exception to catch errors
     */
    @Test
    public void testDeleteMessageCollection() throws Exception
    {
        sut.deleteMessageCollection(testId, TEST_MESSAGE_COLL_ID, FAKETOKEN);
        assertTrue(true);
    }
    
    /**
     * Basic test for Getting Message Collections.
     * @throws Exception to catch errors
     */
    @Test
    public void testGetMessageCollections() throws Exception
    {
        sut.getMessageCollections(testId, new HashSet<String>(), 
                testCollectionOptions, FAKETOKEN);
        assertTrue(true);
    }
    
    /**
     * Basic test for Getting Messages.
     * @throws Exception to catch errors
     */
    @Test
    public void testGetMessages() throws Exception
    {
        sut.getMessages(testId, TEST_MESSAGE_COLL_ID, new HashSet<String>(), 
                new ArrayList<String>(), testCollectionOptions, FAKETOKEN);
        assertTrue(true);
    }
    
    /**
     * Basic test for Modifying a Message.
     * @throws Exception to catch errors
     */
    @Test
    public void testModifyMessage() throws Exception
    {
        sut.modifyMessage(testId, TEST_MESSAGE_COLL_ID, testMessage.getId(), 
                testMessage, FAKETOKEN);
        assertTrue(true);
    }
    
    /**
     * Basic test for Modifying a Message Collection.
     * @throws Exception to catch errors
     */
    @Test
    public void testModifyMessageCollection() throws Exception
    {
        sut.modifyMessageCollection(testId, testMsgCollection, FAKETOKEN);
        assertTrue(true);
    }
}
