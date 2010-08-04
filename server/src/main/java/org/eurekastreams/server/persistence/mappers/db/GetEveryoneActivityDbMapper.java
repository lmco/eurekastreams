package org.eurekastreams.server.persistence.mappers.db;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

public class GetEveryoneActivityDbMapper extends BaseDomainMapper implements DomainMapper<Long, List<Long>>
{
    /**
     * Maximum number of items for the activity id lists.
     */
    private static final int MAX_RESULTS = 10000;

    @Override
    public List<Long> execute(final Long inRequest)
    {
        String idsQueryString = "select id FROM Activity ORDER BY PostedTime desc";
        Query idsQuery = getEntityManager().createQuery(idsQueryString);
        idsQuery.setMaxResults(MAX_RESULTS);
        return idsQuery.getResultList();
    }

}
