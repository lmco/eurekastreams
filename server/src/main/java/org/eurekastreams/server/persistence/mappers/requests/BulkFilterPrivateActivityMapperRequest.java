package org.eurekastreams.server.persistence.mappers.requests;

import java.util.List;
import java.util.Set;

public class BulkFilterPrivateActivityMapperRequest
{
    private List<Long> activities;
    private Set<Long> groupAccess;

    public BulkFilterPrivateActivityMapperRequest(final List<Long> inActivities, final Set<Long> inGroupAccess)
    {
        activities = inActivities;
        groupAccess = inGroupAccess;
    }
    
    public List<Long> getActivities() {
        return activities;
    }
    
    public Set<Long> getGroupAccess() {
        return groupAccess;
    }
}
