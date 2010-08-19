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
package org.eurekastreams.server.domain.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Organization;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Activity test class.
 *
 */
public class ActivityTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private Activity sut;

    /**
     * Activity OpenSocial id.
     */
    private String openSocialId = "openSocialId";

    /**
     * Activity time sent.
     */
    private Date postedTime = new Date();

    /**
     * Activity time updated.
     */
    private Date updated = new Date();

    /**
     * Actor id.
     */
    private String actorId = "actorId";

    /**
     * Actor type.
     */
    private EntityType actorType = EntityType.PERSON;

    /**
     * Original Actor id (used for sharing).
     */
    private String originalActorId = "origActorId";

    /**
     * Original Actor type.
     */
    private EntityType originalActorType = EntityType.PERSON;

    /**
     * Recipient's parent org.
     */
    private Organization recipientParentOrg = context.mock(Organization.class);

    /**
     * Recipient's stream scope.
     */
    private StreamScope recipientStreamScope = context.mock(StreamScope.class);

    /**
     * Activity verb.
     */
    private ActivityVerb verb = ActivityVerb.POST;

    /**
     * HashMap representing base object of activity.
     */
    private HashMap<String, String> baseObject = new HashMap<String, String>();

    /**
     * Base object type for activity.
     */
    private BaseObjectType baseObjectType = BaseObjectType.NOTE;

    /**
     * Location.
     */
    private String location = "location";

    /**
     * Annotation.
     */
    private String annotation = "annotation";

    /**
     * Mood.
     */
    private String mood = "mood";

    /**
     * Original Activity id.
     */
    private long originalActivityId = 1L;

    /**
     * HashTags.
     */
    private Set<HashTag> hashtags = new HashSet<HashTag>();

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new Activity();
        sut.setOpenSocialId(openSocialId);
        sut.setUpdated(updated);
        sut.setPostedTime(postedTime);
        sut.setActorId(actorId);
        sut.setActorType(actorType);
        sut.setOriginalActorId(originalActorId);
        sut.setOriginalActorType(originalActorType);
        sut.setRecipientParentOrg(recipientParentOrg);
        sut.setRecipientStreamScope(recipientStreamScope);
        sut.setVerb(verb);
        sut.setBaseObjectType(baseObjectType);
        sut.setBaseObject(baseObject);
        sut.setLocation(location);
        sut.setAnnotation(annotation);
        sut.setMood(mood);
        sut.setOriginalActivityId(originalActivityId);
        sut.setIsDestinationStreamPublic(true);
        sut.setFlagged(true);
        sut.setHashTags(hashtags);
    }

    /**
     * Test the getters for this class.
     */
    @Test
    public void testGets()
    {
        assertEquals("OpenSocialId not as expected", openSocialId, sut.getOpenSocialId());
        assertEquals("updated not as expected", updated, sut.getUpdated());
        assertEquals("postedTime not as expected", postedTime, sut.getPostedTime());
        assertEquals("actorId not as expected", actorId, sut.getActorId());
        assertEquals("actorType not as expected", actorType, sut.getActorType());
        assertEquals("original actorId not as expected", originalActorId, sut.getOriginalActorId());
        assertEquals("original actorType not as expected", originalActorType, sut.getOriginalActorType());
        assertEquals("recipient parent org not as expected", recipientParentOrg, sut.getRecipientParentOrg());
        assertEquals("recipient StreamScope not as expected", recipientStreamScope, sut.getRecipientStreamScope());
        assertEquals("verb not as expected", verb, sut.getVerb());
        assertEquals("base object type not as expected", baseObjectType, sut.getBaseObjectType());
        assertEquals("base object not as expected", baseObject, sut.getBaseObject());
        assertEquals("Location not as expected", location, sut.getLocation());
        assertEquals("Annotation object not as expected", annotation, sut.getAnnotation());
        assertEquals("Mood not as expected", mood, sut.getMood());
        assertEquals("Original Activity Id not as expected", originalActivityId, sut.getOriginalActivityId());
        assertEquals("Original isDestinationStreamPublic not as expected", true, sut.getIsDestinationStreamPublic());
        assertTrue("Flagged not as expected", sut.isFlagged());
        assertSame(hashtags, sut.getHashTags());
    }

    /**
     * Test addHashtag().
     */
    @Test
    public void testAddHashTagAfterGet()
    {
        HashTag hashTag = new HashTag();

        Activity activity = new Activity();
        assertNotNull(activity.getHashTags());
        assertEquals(0, activity.getHashTags().size());
        activity.addHashTag(hashTag);

        assertEquals(1, activity.getHashTags().size());
        for (HashTag ht : activity.getHashTags())
        {
            assertSame(hashTag, ht);
        }
    }

    /**
     * Test addHashtag().
     */
    @Test
    public void testAddHashTagWithoutGet()
    {
        HashTag hashTag1 = new HashTag();
        HashTag hashTag2 = new HashTag();

        hashTag1.setContent("foo");
        hashTag2.setContent("bar");

        Activity activity = new Activity();
        activity.addHashTag(hashTag1);
        activity.addHashTag(hashTag2);

        assertEquals(2, activity.getHashTags().size());
        boolean hashtag1Found = false;
        boolean hashtag2Found = false;
        for (HashTag ht : activity.getHashTags())
        {
            if (ht.getContent().equals("foo"))
            {
                hashtag1Found = true;
            }
            if (ht.getContent().equals("bar"))
            {
                hashtag2Found = true;
            }
        }
        assertTrue(hashtag1Found);
        assertTrue(hashtag2Found);
    }
}
