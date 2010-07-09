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
package org.eurekastreams.server.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import org.eurekastreams.server.domain.FeedReader;
import org.eurekastreams.server.domain.FeedReaderUrlCount;
import org.eurekastreams.testing.FeedReaderDBUnitFixtureSetup;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

//TODO must be brought out into the feed reader project. 

/**
 * FeedReaderMapperTest class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:feedReaderContext-test.xml" })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class FeedReaderMapperTest
{

    /**
     * Instance ID.
     */
    private long testId = 0;

    /**
     * OS ID.
     */
    private String testOSId = "1234";

    /**
     * module ID.
     */
    private String testModuleId = "45";

    /**
     * url ID.
     */
    private String testUrl = "www.google.com";

    /**
     * test Date.
     */
    private Date testDate = new Date(Long.parseLong("100000"));

    /**
     * JpaBackgroundMapper - system under test.
     */
    @Autowired
    private FeedReaderMapper sut;

    /**
     * new Feedreader object to create.
     */
    private FeedReader newFeedReader = new FeedReader();

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        newFeedReader.setDateAdded(testDate);
        newFeedReader.setFeedTitle("new Title");
        newFeedReader.setId(testId);
        newFeedReader.setModuleId("newID");
        newFeedReader.setOpenSocialId(testOSId);
        newFeedReader.setUrl(testUrl);

    }

    /**
     * Test finding a person's background.
     */
    @Test
    public void findFeedByOpenSocialIdAndModuleId()
    {

        final String userId = "1234";
        final String appId = "45";
        FeedReader feedReader = sut.findFeedByOpenSocialIdAndModuleId(userId, appId);

        assertNotNull("No feeds found for user" + userId + " with appid of " + appId, feedReader);

        assertEquals(feedReader.getUrl(), "http://www.google.com");

    }

    /**
     * Test finding a person's background.
     */
    @Test
    public void findFeedById()
    {

        final String userId = "1234";
        List<FeedReader> feedReader = sut.findFeedsByOpenSocialId(userId);

        assertNotNull("No feeds found for user" + userId, feedReader);

        assertEquals(2, feedReader.size());

    }

    /**
     * Test to verify that data has been removed from the object and persisted to the db.
     * 
     * @throws Exception
     *             when an exception is encountered.
     */
    @Test
    public void testRemoveDataItem() throws Exception
    {
        sut.delete(1);
        assertEquals(null, sut.findFeedByOpenSocialIdAndModuleId(testOSId, testModuleId));
    }

    /**
     * Test to verify that data has been removed from the object and persisted to the db.
     * 
     * @throws Exception
     *             when an exception is encountered.
     */
    @Test
    public void testInsertDataItem() throws Exception
    {
        sut.insert(newFeedReader);
        assertEquals(newFeedReader, sut.findFeedByOpenSocialIdAndModuleId(newFeedReader.getOpenSocialId(),
                newFeedReader.getModuleId()));
    }

    /**
     * Test finding a person's background.
     */
    @Test
    public void findTopTenPublicFeeds()
    {
        List<FeedReaderUrlCount> feedReaders = sut.findTop10PublicFeeds();

        assertEquals(4, feedReaders.size());
        // Assert that the google2.com one is first.
        assertEquals("http://www.google2.com", feedReaders.get(0).getUrl());
        // assert that it has two counts.
        assertEquals((Long) 2L, (Long) feedReaders.get(0).getCount());
    }

    /**
     * Test finding a person's background.
     */
    @Test
    public void findTopTenFriendFeeds()
    {
        List<FeedReaderUrlCount> feedReaders = sut.findTop10FriendFeeds("1234, 12346");

        // should have one less feed returned since you are nto friends with everyone.
        assertEquals(3, feedReaders.size());
    }

    /**
     * Load the DBUnit XML for the all tests in this suite.
     * 
     * @throws Exception
     *             If error occurs during setup.
     */
    @BeforeClass
    public static void setUp() throws Exception
    {
        // Load up the DBUnit data set
        FeedReaderDBUnitFixtureSetup.loadDataSet("/feedReaderDataset.xml");
    }
}
