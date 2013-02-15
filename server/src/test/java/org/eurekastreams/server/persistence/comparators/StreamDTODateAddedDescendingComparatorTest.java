/*
 * Copyright (c) 2011-2013 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.comparators;

import java.util.Date;

import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.junit.Assert;
import org.junit.Test;

import com.ibm.icu.util.Calendar;

/**
 * Test fixture for StreamDTODateAddedDescendingComparator.
 */
public class StreamDTODateAddedDescendingComparatorTest
{
    /**
     * System under test.
     */
    private StreamDTODateAddedDescendingComparator sut = new StreamDTODateAddedDescendingComparator();

    /**
     * Test compare when PersonModelView and DomainGroupModelView are passed in with the same follower count.
     */
    @Test
    public void testCompareWhenEqualGroupAndPerson()
    {
        Date sharedDate = new Date();
        PersonModelView pmv = new PersonModelView(1L, "persona", "Person", "A", "hey now", "", 50, sharedDate, 1L);
        DomainGroupModelView gmv = new DomainGroupModelView(1L, "groupb", "Group B", 50L, sharedDate, 2L);

        Assert.assertTrue(sut.compare(pmv, gmv) > 0);
        Assert.assertTrue(sut.compare(gmv, pmv) < 0);
    }

    /**
     * Test compare when two PersonModelViews are passed in with equal followers count.
     */
    @Test
    public void testCompareWhenTwoPeopleEqual()
    {
        Date sharedDate = new Date();
        PersonModelView pmv1 = new PersonModelView(1L, "persona", "Person", "A", "hey now", "", 50, sharedDate, 3L);
        PersonModelView pmv2 = new PersonModelView(2L, "personb", "Person", "B", "hey now", "", 50, sharedDate, 4L);

        Assert.assertEquals(0, sut.compare(pmv1, pmv2));
    }

    /**
     * Test compare when two DomainGroupModelViews are passed in with equal followers count.
     */
    @Test
    public void testCompareWhenTwoGroupsEqual()
    {
        Date sharedDate = new Date();
        DomainGroupModelView gmv1 = new DomainGroupModelView(1L, "groupa", "Group A", 50L, sharedDate, 5L);
        DomainGroupModelView gmv2 = new DomainGroupModelView(2L, "groupb", "Group B", 50L, sharedDate, 6L);

        Assert.assertEquals(0, sut.compare(gmv1, gmv2));
    }

    /**
     * Test compare when PersonModelView and DomainGroupModelView are passed in with different follower counts.
     */
    @Test
    public void testCompareWhenGroupAndPersonHaveDifferentDates()
    {
        Calendar cal = Calendar.getInstance();
        Date newer = cal.getTime();

        cal.add(Calendar.HOUR, -1);
        Date earlier = cal.getTime();

        PersonModelView pmv = new PersonModelView(1L, "persona", "Person", "A", "hey now", "", 40, earlier, 7L);
        DomainGroupModelView gmv = new DomainGroupModelView(1L, "groupb", "Group B", 50L, newer, 8L);

        Assert.assertTrue(sut.compare(pmv, gmv) > 0);
        Assert.assertTrue(sut.compare(gmv, pmv) < 0);
    }
}
