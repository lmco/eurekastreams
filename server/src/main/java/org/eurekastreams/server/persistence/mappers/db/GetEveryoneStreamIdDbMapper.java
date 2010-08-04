package org.eurekastreams.server.persistence.mappers.db;

import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

public class GetEveryoneStreamIdDbMapper extends BaseDomainMapper implements DomainMapper<Object, Long>
{

    @Override
    public Long execute(final Object inRequest)
    {
        String everyoneQueryString = "SELECT id from StreamView where type = :type";
        Query everyoneQuery = getEntityManager().createQuery(everyoneQueryString).setParameter("type",
        StreamView.Type.EVERYONE);
        return (Long) everyoneQuery.getSingleResult();
    }

}
