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
package org.eurekastreams.server.persistence.comparators;

import java.util.Date;

import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test fixture for StreamDTOFollowerCountDescendingComparator.
 */
public class StreamDTOFollowerCountDescendingComparatorTest
{
    /**
     * System under test.
     */
    private StreamDTOFollowerCountDescendingComparator sut = new StreamDTOFollowerCountDescendingComparator();

    /**
     * Test compare when PersonModelView and DomainGroupModelView are passed in with the same follower count.
     */
    @Test
    public void testCompareWhenEqualGroupAndPerson()
    {
        PersonModelView pmv = new PersonModelView(1L, "persona", "Person", "A", 50L, new Date());
        DomainGroupModelView gmv = new DomainGroupModelView(1L, "groupb", "Group B", 50L, new Date());

        Assert.assertTrue(sut.compare(pmv, gmv) > 0);
        Assert.assertTrue(sut.compare(gmv, pmv) < 0);
    }

    /**
     * Test compare when two PersonModelViews are passed in with equal followers count.
     */
    @Test
    public void testCompareWhenTwoPeopleEqual()
    {
        PersonModelView pmv1 = new PersonModelView(1L, "persona", "Person", "A", 50L, new Date());
        PersonModelView pmv2 = new PersonModelView(2L, "personb", "Person", "B", 50L, new Date());

        Assert.assertEquals(0, sut.compare(pmv1, pmv2));
    }

    /**
     * Test compare when two DomainGroupModelViews are passed in with equal followers count.
     */
    @Test
    public void testCompareWhenTwoGroupsEqual()
    {
        DomainGroupModelView gmv1 = new DomainGroupModelView(1L, "groupa", "Group A", 50L, new Date());
        DomainGroupModelView gmv2 = new DomainGroupModelView(2L, "groupb", "Group B", 50L, new Date());

        Assert.assertEquals(0, sut.compare(gmv1, gmv2));
    }

    /**
     * Test compare when PersonModelView and DomainGroupModelView are passed in with different follower counts.
     */
    @Test
    public void testCompareWhenGroupAndPersonHaveDifferentFollowerCounts()
    {
        PersonModelView pmv = new PersonModelView(1L, "persona", "Person", "A", 40L, new Date());
        DomainGroupModelView gmv = new DomainGroupModelView(1L, "groupb", "Group B", 50L, new Date());

        Assert.assertTrue(sut.compare(pmv, gmv) > 0);
        Assert.assertTrue(sut.compare(gmv, pmv) < 0);
    }
}
