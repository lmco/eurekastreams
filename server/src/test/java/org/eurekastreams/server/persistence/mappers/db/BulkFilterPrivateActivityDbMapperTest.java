package org.eurekastreams.server.persistence.mappers.db;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.BulkFilterPrivateActivityMapperRequest;
import org.junit.Before;
import org.junit.Test;

public class BulkFilterPrivateActivityDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private BulkFilterPrivateActivityDbMapper sut = null;

    /**
     * Setup test fixtures.
     */
    @Before
    public void before()
    {
        sut = new BulkFilterPrivateActivityDbMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        List<Long> activites = new ArrayList<Long>();
        activites.add(1L);
        Set<Long> groups = new HashSet<Long>();
        groups.add(1L);

        sut.execute(new BulkFilterPrivateActivityMapperRequest(activites, groups));
    }

}
