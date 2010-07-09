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

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

/**
 * Recommendation test.
 *
 */
public class RecommendationTest
{
    /**
     * Subject under test.
     */
    private Recommendation sut;

    /**
     * An OpenSocial id for the author. Arbitrary.
     */
    private static final String AUTHOR_OSID = UUID.randomUUID().toString();

    /**
     * An OpenSocial id for the subject. Arbitrary.
     */
    private static final String SUBJECT_OSID = UUID.randomUUID().toString();

    /**
     * The text of a recommendation.
     */
    private static final String RECO_TEXT = "The time has come, the walrus said, to speak of many things.";

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new Recommendation(SUBJECT_OSID, AUTHOR_OSID, RECO_TEXT);
    }

    /**
     * Test the getters.
     */
    @Test
    public void testGettersAndSetters()
    {
        Date date = new Date();

        sut.setAuthorOpenSocialId(AUTHOR_OSID);
        sut.setSubjectOpenSocialId(SUBJECT_OSID);
        sut.setText(RECO_TEXT);
        sut.setDate(date);

        assertEquals(AUTHOR_OSID, sut.getAuthorOpenSocialId());
        assertEquals(SUBJECT_OSID, sut.getSubjectOpenSocialId());
        assertEquals(RECO_TEXT, sut.getText());
        assertEquals(date, sut.getDate());
    }
}
