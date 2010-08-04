package org.eurekastreams.server.persistence.mappers.cache;

import org.eurekastreams.server.persistence.mappers.RefreshDataSourceMapper;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

public class RefreshEveryoneStreamIdCacheMapper extends CachedDomainMapper implements
RefreshDataSourceMapper<Object, Long>
{

    @Override
    public void refresh(final Object request, final Long data)
    {
        getCache().set(CacheKeys.CORE_STREAMVIEW_ID_EVERYONE, data);
    }

}
