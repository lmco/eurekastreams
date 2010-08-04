package org.eurekastreams.server.persistence.mappers.cache;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.RefreshDataSourceMapper;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

public class RefreshFollowedByActivitiesCacheMapper extends CachedDomainMapper implements
RefreshDataSourceMapper<Long, List<Long>>
{

    @Override
    public void refresh(final Long request, final List<Long> data)
    {
        getCache().setList(CacheKeys.ACTIVITIES_BY_FOLLOWING + request, data);
    }

}
