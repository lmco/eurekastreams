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
package org.eurekastreams.web.client.ui.common.notification.dialog;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.InAppNotificationDTO;
import org.eurekastreams.web.client.ui.common.notification.dialog.Source.Filter;
import org.junit.Test;

/**
 * Tests SourceListBuilder.
 */
public class SourceListBuilderTest
{
    /**
     * Tests source building.
     */
    @Test
    public void test()
    {
        String personId1 = "person1";
        String personId2 = "person2";
        String groupId1 = "group1";
        String groupId2 = "group2";

        String personName1 = "Zachary Iwouldsortlast";
        String personName2 = "John Doe";
        String groupName1 = "Eureka Streams";
        String groupName2 = "Apple Fans";

        String app1Id = "app1";
        String app2Id = "app2";
        String app3Id = "app3";
        String app1Name = "Test Application";
        String app2Name = "Another Application";
        String app3Name = "My Application";

        // -- prepare list of notifications --
        List<InAppNotificationDTO> notifs = new ArrayList<InAppNotificationDTO>();
        notifs.add(makeNotif(EntityType.PERSON, personId1, false, personName1));
        notifs.add(makeNotif(EntityType.PERSON, personId1, false, personName1));

        notifs.add(makeNotif(EntityType.PERSON, personId2, true, personName2));
        notifs.add(makeNotif(EntityType.PERSON, personId2, false, personName2));
        notifs.add(makeNotif(EntityType.PERSON, personId2, true, personName2));

        notifs.add(makeNotif(EntityType.GROUP, groupId1, true, groupName1));
        notifs.add(makeNotif(EntityType.GROUP, groupId1, true, groupName1));

        notifs.add(makeNotif(EntityType.GROUP, groupId2, false, groupName2));
        notifs.add(makeNotif(EntityType.GROUP, groupId2, true, groupName2));
        notifs.add(makeNotif(EntityType.GROUP, groupId2, false, groupName2));

        notifs.add(makeNotif(EntityType.NOTSET, null, false, null));
        notifs.add(makeNotif(EntityType.NOTSET, "", false, ""));
        notifs.add(makeNotif(null, null, false, null));
        notifs.add(makeNotif(null, "", false, ""));

        notifs.add(makeNotif(EntityType.NOTSET, null, true, null));
        notifs.add(makeNotif(EntityType.NOTSET, "", true, ""));
        notifs.add(makeNotif(null, null, true, null));
        notifs.add(makeNotif(null, "", true, ""));

        notifs.add(makeNotif(EntityType.APPLICATION, app1Id, false, app1Name));
        notifs.add(makeNotif(EntityType.APPLICATION, app1Id, false, app1Name));

        notifs.add(makeNotif(EntityType.APPLICATION, app2Id, true, app2Name));
        notifs.add(makeNotif(EntityType.APPLICATION, app2Id, true, app2Name));

        notifs.add(makeNotif(EntityType.APPLICATION, app3Id, false, app3Name));
        notifs.add(makeNotif(EntityType.APPLICATION, app3Id, true, app3Name));

        // "shuffle" the notifs
        int shuffler = 5;
        final int size = notifs.size();
        assertTrue("The test needs to use a shuffler value that doesn't divide evenly into the number of notifs.",
                size % shuffler != 0);
        List<InAppNotificationDTO> notifs2 = new ArrayList<InAppNotificationDTO>(size);
        for (int i = 0; i < size; i++)
        {
            notifs2.add(notifs.get(i*shuffler % size));
        }

        // run!
        SourceListBuilder sut = new SourceListBuilder(notifs2, personId1);

        // validate
        Source rootSource = sut.getRootSource();
        List<Source> sources = sut.getSourceList();

        assertEquals(10, sources.size());

        assertSource(sources.get(0), null, null, "All", 12, 24, notifs2);
        assertNull(sources.get(0).getParent());
        assertSame(rootSource, sources.get(0));

        assertSource(sources.get(1), null, null, "Streams", 5, 10, notifs2);
        assertSame(rootSource, sources.get(1).getParent());

        assertSource(sources.get(2), EntityType.PERSON, personId1, "My Stream", 0, 2, notifs2);
        assertNotNull(sources.get(2).getParent());

        assertSource(sources.get(3), EntityType.GROUP, groupId2, groupName2, 1, 3, notifs2);
        assertNotNull(sources.get(3).getParent());

        assertSource(sources.get(4), EntityType.GROUP, groupId1, groupName1, 2, 2, notifs2);
        assertNotNull(sources.get(4).getParent());

        assertSource(sources.get(5), EntityType.PERSON, personId2, personName2, 2, 3, notifs2);
        assertNotNull(sources.get(5).getParent());

        assertSource(sources.get(6), null, null, "Apps", 3, 6, notifs2);
        assertSame(rootSource, sources.get(6).getParent());

        assertSource(sources.get(7), EntityType.APPLICATION, app2Id, app2Name, 2, 2, notifs2);
        assertNotNull(sources.get(7).getParent());

        assertSource(sources.get(8), EntityType.APPLICATION, app3Id, app3Name, 1, 2, notifs2);
        assertNotNull(sources.get(8).getParent());

        assertSource(sources.get(9), EntityType.APPLICATION, app1Id, app1Name, 0, 2, notifs2);
        assertNotNull(sources.get(9).getParent());

        assertNotNull(sut.getSourceIndex());
        assertFalse(sut.getSourceIndex().isEmpty());
    }

    /**
     * Validates a source is as expected.
     *
     * @param source
     *            Source to inspect.
     * @param entityType
     *            Expected entity type.
     * @param uniqueId
     *            Expected unique id.
     * @param displayName
     *            Expected display name.
     * @param unreadCount
     *            Expected unread count.
     * @param filterCount
     *            Expected number of notifs that match the filter.
     * @param notifs
     *            List of notifs.
     */
    private void assertSource(final Source source, final EntityType entityType, final String uniqueId,
            final String displayName, final int unreadCount, final int filterCount,
            final List<InAppNotificationDTO> notifs)
    {
        assertEquals(entityType, source.getEntityType());
        assertEquals(uniqueId, source.getUniqueId());
        assertEquals(displayName, source.getDisplayName());
        assertEquals(unreadCount, source.getUnreadCount());
        Filter filter = source.getFilter();
        assertNotNull(filter);
        int matchCount = 0;
        for (InAppNotificationDTO notif : notifs)
        {
            if (filter.shouldDisplay(notif))
            {
                matchCount++;
            }
        }
        assertEquals(filterCount, matchCount);
    }

    /**
     * Create a notification for test data.
     *
     * @param entityType
     *            Type of source entity.
     * @param uniqueId
     *            ID of source entity.
     * @param unread
     *            If unread.
     * @param name
     *            Name of source entity.
     * @return Notification.
     */
    private InAppNotificationDTO makeNotif(final EntityType entityType, final String uniqueId, final boolean unread,
            final String name)
    {
        return new InAppNotificationDTO(0, null, null, null, null, false, !unread, entityType, uniqueId, name, null,
                null);
    }
}
