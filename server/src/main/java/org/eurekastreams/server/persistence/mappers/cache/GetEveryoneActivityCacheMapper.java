package org.eurekastreams.server.persistence.mappers.cache;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

public class GetEveryoneActivityCacheMapper extends CachedDomainMapper implements DomainMapper<Long, List<Long>>
{

    @Override
    public List<Long> execute(final Long inRequest)
    {
        return getCache().getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + inRequest);
    }

}
