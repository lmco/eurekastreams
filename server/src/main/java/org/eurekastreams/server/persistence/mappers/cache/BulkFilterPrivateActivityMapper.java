package org.eurekastreams.server.persistence.mappers.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.chained.PartialMapperResponse;
import org.eurekastreams.server.persistence.mappers.requests.BulkFilterPrivateActivityMapperRequest;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

public class BulkFilterPrivateActivityMapper extends CachedDomainMapper
        implements
        DomainMapper<BulkFilterPrivateActivityMapperRequest, PartialMapperResponse<BulkFilterPrivateActivityMapperRequest, List<Long>>>
{
    /**
     * Log.
     */
    private static Log log = LogFactory.make();

    public PartialMapperResponse<BulkFilterPrivateActivityMapperRequest, List<Long>> execute(
            BulkFilterPrivateActivityMapperRequest request)
    {
        List<Long> activityIds = request.getActivities();

        List<String> stringKeys = new ArrayList<String>();
        for (long key : activityIds)
        {
            stringKeys.add(CacheKeys.ACTIVITY_BY_ID + key);
        }

        // Finds activities in the cache.
        Map<String, ActivityDTO> activities = (Map<String, ActivityDTO>) (Map<String, ? >) getCache().multiGet(
                stringKeys); // Determines if any of the activities were missing from the cache
        List<Long> uncachedActivityKeys = new ArrayList<Long>();

        List<Long> publicIds = new ArrayList<Long>();

        for (int i = 0; i < activityIds.size(); i++)
        {
            if (!activities.containsKey(CacheKeys.ACTIVITY_BY_ID + activityIds.get(i)))
            {
                uncachedActivityKeys.add(activityIds.get(i));
            }
            else
            {
                ActivityDTO activity = activities.get(CacheKeys.ACTIVITY_BY_ID + activityIds.get(i));
                
                boolean isDestiationStreamPublic = activity.getIsDestinationStreamPublic();
                final Long destinationStreamId = activity.getDestinationStream().getDestinationEntityId();

                if (isDestiationStreamPublic)
                {
                    publicIds.add(activityIds.get(i));
                    continue;
                }

                // see if the user has access to view the private group
                if (request.getGroupAccess().contains(destinationStreamId))
                {
                    publicIds.add(activityIds.get(i));
                }
            }
        }

        log.debug("Returning Activities Total: " + publicIds.size());

        if (uncachedActivityKeys.size() > 0)
        {
            return new PartialMapperResponse<BulkFilterPrivateActivityMapperRequest, List<Long>>(publicIds,
                    new BulkFilterPrivateActivityMapperRequest(uncachedActivityKeys, request.getGroupAccess()));
        }
        else
        {
            return new PartialMapperResponse<BulkFilterPrivateActivityMapperRequest, List<Long>>(publicIds);
        }
    }
}
