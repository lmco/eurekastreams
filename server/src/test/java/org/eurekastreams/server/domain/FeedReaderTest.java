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

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

//TODO must be brought out into the feed reader project.  

/**
 * Test class for FeedReader Entity.
 */
public class FeedReaderTest
{
    /**
     * Subject under test.
     */
    private FeedReader sut;

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new FeedReader("1", "2");
    }

    /**
     * Test the getters and setters.
     */
    @Test
    public void testGettersAndSetters()
    {
        String numberString = "100000000"; // this is me cheating the magic number
        sut.setDateAdded(new Date(Long.parseLong(numberString)));
        assertEquals((Date) new Date(Long.parseLong(numberString)), sut.getDateAdded());

        sut.setId(4);
        assertEquals((long) 4, sut.getId());

        sut.setModuleId("2");
        assertEquals("2", sut.getModuleId());

        sut.setOpenSocialId("ID");
        assertEquals("ID", sut.getOpenSocialId());

        sut.setUrl("url");
        assertEquals("url", sut.getUrl());
    }
}
