/*
 * Copyright (c) 2010 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.server.action.request.opensocial;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * This class represents the request parameters for retrieving Activities based
 * on information passed from the OpenSocial adapter.
 *
 */
public class GetUserActivitiesRequest implements Serializable
{
    /**
     * Serializable id for this class.
     */
    private static final long serialVersionUID = 2389571868085648257L;

    /**
     * Local instance of the List of activity ids for this request.
     */
    private List<Long> activityIds;

    /**
     * Local instance of the List of OpenSocial ids for the people to retrieve activities for.
     */
    private Set<String> openSocialIds;

    /**
     * Constructor.
     * @param inActivityIds - instance of the List of Activity Ids to retrieve.
     * @param inOpenSocialIds - instance of the Set of the OpenSocial ids to retrieve activities for.
     */
    public GetUserActivitiesRequest(final List<Long> inActivityIds, final Set<String> inOpenSocialIds)
    {
        activityIds = inActivityIds;
        openSocialIds = inOpenSocialIds;
    }

    /**
     * @return the activityIds
     */
    public List<Long> getActivityIds()
    {
        return activityIds;
    }

    /**
     * @param inActivityIds the activityIds to set
     */
    public void setActivityIds(final List<Long> inActivityIds)
    {
        activityIds = inActivityIds;
    }

    /**
     * @return the openSocialIds
     */
    public Set<String> getOpenSocialIds()
    {
        return openSocialIds;
    }

    /**
     * @param inOpenSocialIds the openSocialIds to set
     */
    public void setOpenSocialIds(final Set<String> inOpenSocialIds)
    {
        openSocialIds = inOpenSocialIds;
    }


}
