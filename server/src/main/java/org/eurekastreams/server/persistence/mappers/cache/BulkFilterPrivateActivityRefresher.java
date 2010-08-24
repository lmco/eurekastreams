package org.eurekastreams.server.persistence.mappers.cache;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.chained.RefreshStrategy;
import org.eurekastreams.server.persistence.mappers.requests.BulkFilterPrivateActivityMapperRequest;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

public class BulkFilterPrivateActivityRefresher extends CachedDomainMapper implements
        RefreshStrategy<BulkFilterPrivateActivityMapperRequest, List<Long>>
{
    public void refresh(BulkFilterPrivateActivityMapperRequest request, List<Long> response)
    {
    }
}
