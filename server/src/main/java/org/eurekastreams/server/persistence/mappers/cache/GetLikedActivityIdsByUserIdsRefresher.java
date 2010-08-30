package org.eurekastreams.server.persistence.mappers.cache;

import java.util.Collection;

import org.eurekastreams.server.persistence.mappers.chained.RefreshStrategy;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

public class GetLikedActivityIdsByUserIdsRefresher extends CachedDomainMapper implements
        RefreshStrategy<Collection<Long>, Collection<Collection<Long>>>
{
    public void refresh(Collection<Long> request, Collection<Collection<Long>> response)
    {
        // TODO Auto-generated method stub
    }

}
