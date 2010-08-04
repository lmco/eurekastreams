package org.eurekastreams.server.persistence.mappers.db;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.FollowedActivityIdsLoader;

public class GetFollowedByActivitiesDbMapper extends BaseArgDomainMapper<Long, List<Long>>
{
    private FollowedActivityIdsLoader followedLoader;

    private int maxRequest;

    public GetFollowedByActivitiesDbMapper(final FollowedActivityIdsLoader inFollowedLoader,
            final int inMaxRequest)
    {
        followedLoader = inFollowedLoader;
        maxRequest = inMaxRequest;
    }

    @Override
    public List<Long> execute(final Long inRequest)
    {
        // TODO Auto-generated method stub
        return followedLoader.getFollowedActivityIds(inRequest, maxRequest);
    }

}
