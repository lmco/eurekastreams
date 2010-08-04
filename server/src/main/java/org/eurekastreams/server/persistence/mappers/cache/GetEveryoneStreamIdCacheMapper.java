package org.eurekastreams.server.persistence.mappers.cache;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

public class GetEveryoneStreamIdCacheMapper extends CachedDomainMapper implements DomainMapper<Object, Long>
{

    @Override
    public Long execute(final Object inRequest)
    {
        return (Long) getCache().get(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE);
    }

}
