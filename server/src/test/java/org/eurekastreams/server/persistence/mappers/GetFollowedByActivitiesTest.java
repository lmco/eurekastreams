package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class GetFollowedByActivitiesTest extends CachedMapperTest
{
    /**
     * User id from dataset.xml.
     */
    private static final long USER_ID = 99L;

    @Autowired
    @Qualifier("getFollowedByActivities")
    private ChainedDomainMapper<Long, List<Long>> getFollowedByActivities;

    /**
     * Test getFollowedActivityIds method.
     */
    @Test
    public void testGetFollowedActivityIdsWithPersonGroupActivities()
    {
        List<Long> results = getFollowedByActivities.execute(USER_ID);

        //assert correct number of results.
        assertEquals(2, results.size());

        //assert list is sorted correctly.
        assertTrue(results.get(0) > results.get(1));
    }
}
