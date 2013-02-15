/*
 * Copyright (c) 2009-2013 Lockheed Martin Corporation
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
package org.eurekastreams.server.search.modelview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import org.junit.Before;

import java.util.Date;
import java.util.HashMap;

import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.junit.Test;

/**
 * Test fixture for DomainGroupModelView.
 */
public class DomainGroupModelViewTest
{
    /**
     * The number of people following this group.
     */
    private final int followersCount = 382;

    /**
     * The name of this group.
     */
    private final String name = "slkdflksj";

    /**
     * The short name of this group.
     */
    private final String shortName = "skhg";

    /**
     * The description of this group.
     */
    private final String description = "Thy micturations are to me";

    /**
     * The date this group was added to the system.
     */
    private final Date dateAdded = new Date();

    /**
     * The number of updates this group has.
     */
    private final int updatesCount = 382;

    /**
     * The group's avatar id.
     */
    private final String avatarId = "lsdkfjsldfjhgh";

    /**
     * The search score.
     */
    private final float searchScore = 0.2382F;

    /**
     * The id of the domain group.
     */
    private final long domainGroupId = 28382L;

    /**
     * The person that created the group.
     */
    private final Person createdby = new Person("testid", "test", "my", "last", "preferred");

    /**
     * IsPublic.
     */
    private final boolean isPublic = true;

    /**
     * Stream id.
     */
    private final long streamId = 1L;

    /**
     * CompositeStream id.
     */
    private final long compositeStreamId = 2L;

    /**
     * Flag to determine if group's activities can be commented on.
     */
    private final boolean commentable = false;

    /**
     * Flag to determine if group's stream can be posted on.
     */
    private final boolean streamPostable = false;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        createdby.setDisplayNameSuffix(" (FOO)");
    }

    /**
     * Test helper method to assert all properties.
     * 
     * @param sut
     *            the SUT
     */
    private void assertAll(final DomainGroupModelView sut)
    {
        assertEquals(shortName, sut.getShortName());
        assertEquals(followersCount, sut.getFollowersCount());
        assertEquals(name, sut.getName());
        assertEquals(shortName, sut.getShortName());
        assertEquals(description, sut.getDescription());
        assertEquals(dateAdded, sut.getDateAdded());
        assertEquals(updatesCount, sut.getUpdatesCount());
        assertEquals(avatarId, sut.getAvatarId());
        assertEquals(isPublic, sut.isPublic());
        assertEquals(createdby.getAccountId(), sut.getPersonCreatedByAccountId());
        assertEquals(createdby.getDisplayName(), sut.getPersonCreatedByDisplayName());
        assertEquals("preferred last (FOO)", sut.getPersonCreatedByDisplayName());
        assertEquals(streamId, sut.getStreamId());
        assertEquals(commentable, sut.isCommentable());
        assertEquals(streamPostable, sut.isStreamPostable());

        assertEquals(sut.getEntityId(), sut.getId());
        assertEquals(shortName, sut.getUniqueId());
    }

    /**
     * Test helper method to assert the default values.
     * 
     * @param sut
     *            the SUT
     */
    private void assertDefaultValues(final DomainGroupModelView sut)
    {
        assertEquals(ModelView.UNINITIALIZED_INTEGER_VALUE, sut.getFollowersCount());
        assertEquals(ModelView.UNINITIALIZED_STRING_VALUE, sut.getName());
        assertEquals(ModelView.UNINITIALIZED_STRING_VALUE, sut.getShortName());
        assertEquals(ModelView.UNINITIALIZED_STRING_VALUE, sut.getDescription());
        assertEquals(ModelView.UNINITIALIZED_DATE_VALUE, sut.getDateAdded());
        assertEquals(ModelView.UNINITIALIZED_INTEGER_VALUE, sut.getUpdatesCount());
        assertEquals(ModelView.UNINITIALIZED_STRING_VALUE, sut.getAvatarId());
        assertEquals(false, sut.isPublic());
        assertEquals(ModelView.UNINITIALIZED_LONG_VALUE, sut.getStreamId());
        assertEquals(true, sut.isCommentable());
        assertEquals(true, sut.isStreamPostable());
        assertNull(sut.getStickyActivity());
        assertNull(sut.getStickyActivityId());
    }

    /**
     * Test the setters.
     */
    @Test
    public void testSetters()
    {
        DomainGroupModelView sut = new DomainGroupModelView();
        sut.setEntityId(domainGroupId);
        sut.setFollowersCount(followersCount);
        sut.setName(name);
        sut.setShortName(shortName);
        sut.setDescription(description);
        sut.setDateAdded(dateAdded);
        sut.setUpdatesCount(updatesCount);
        sut.setAvatarId(avatarId);
        sut.setIsPublic(isPublic);
        sut.setPersonCreatedByAccountId(createdby.getAccountId());
        sut.setPersonCreatedByDisplayName(createdby.getDisplayName());
        sut.setStreamId(streamId);
        sut.setCommentable(commentable);
        sut.setStreamPostable(streamPostable);

        assertAll(sut);
    }

    /**
     * Tests sticky activity getters/setters.
     */
    @Test
    public void testStickyActivity()
    {
        DomainGroupModelView sut = new DomainGroupModelView();
        assertDefaultValues(sut);
        sut.setStickyActivityId(8L);
        assertEquals((Long) 8L, sut.getStickyActivityId());
        assertNull(sut.getStickyActivity());

        ActivityDTO activity = new ActivityDTO();
        activity.setId(7L);
        sut.setStickyActivity(activity);
        assertEquals((Long) 8L, sut.getStickyActivityId());
        assertSame(activity, sut.getStickyActivity());
    }

    /**
     * Test the default property values.
     */
    @Test
    public void testDefaultValues()
    {
        DomainGroupModelView sut = new DomainGroupModelView();
        assertDefaultValues(sut);
    }

    /**
     * Test toString().
     */
    @Test
    public void testToString()
    {
        DomainGroupModelView sut = new DomainGroupModelView();

        // test uninitialized toString()
        assertEquals("DomainGroup", sut.toString());

        // load the ID
        HashMap<String, Object> p = new HashMap<String, Object>();
        p.put("__HSearch_id", domainGroupId);
        sut.loadProperties(p);

        // test initialized sut
        assertEquals("DomainGroup#" + domainGroupId, sut.toString());
    }

    /**
     * Test loading all properties.
     */
    @Test
    public void testLoadProperties()
    {
        HashMap<String, Object> p = new HashMap<String, Object>();
        p.put("__HSearch_id", domainGroupId);
        p.put("__HSearch_Score", searchScore);
        p.put("followersCount", followersCount);
        p.put("name", name);
        p.put("shortName", shortName);
        p.put("description", description);
        p.put("dateAdded", dateAdded);
        p.put("updatesCount", updatesCount);
        p.put("avatarId", avatarId);
        p.put("isPublic", isPublic);
        p.put("personCreatedByAccountId", createdby.getAccountId());
        p.put("personCreatedByDisplayName", createdby.getDisplayName());
        p.put("streamId", streamId);
        p.put("compositeStreamId", compositeStreamId);
        p.put("commentable", commentable);
        p.put("streamPostable", streamPostable);
        p.put("stickyActivityId", 9L);

        DomainGroupModelView sut = new DomainGroupModelView();
        sut.loadProperties(p);
        assertAll(sut);
        assertEquals(domainGroupId, sut.getEntityId());
        assertEquals(searchScore, sut.getSearchIndexScore(), 0);
        assertEquals((Long) 9L, sut.getStickyActivityId());
        assertNull(sut.getStickyActivity());
    }

    /**
     * Test loading an empty property map keeps default values.
     */
    @Test
    public void testLoadEmptyProperties()
    {
        HashMap<String, Object> p = new HashMap<String, Object>();
        DomainGroupModelView sut = new DomainGroupModelView();
        sut.loadProperties(p);
        assertDefaultValues(sut);
    }
}
