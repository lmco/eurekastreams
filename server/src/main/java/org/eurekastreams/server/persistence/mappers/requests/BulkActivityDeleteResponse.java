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
package org.eurekastreams.server.persistence.mappers.requests;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Response object for bulk activity deletion.
 * 
 */
public class BulkActivityDeleteResponse
{
    /**
     * Activity ids that were deleted.
     */
    private List<Long> activityIds;

    /**
     * Comment ids that were deleted.
     */
    private List<Long> commentIds;

    /**
     * Map of person ids that had starred activities deleted. with the activity ids.
     */
    private Map<Long, Set<Long>> peopleWithStarredActivities;

    /**
     * Constructor.
     */
    public BulkActivityDeleteResponse()
    {
        activityIds = new ArrayList<Long>();
        commentIds = new ArrayList<Long>();
        peopleWithStarredActivities = new Hashtable<Long, Set<Long>>();
    }

    /**
     * Constructor.
     * 
     * @param inActivityIds
     *            Activity ids that were deleted.
     * @param inCommentIds
     *            Comment ids that were deleted.
     * @param inPeopleWithStarredActivities
     *            Map of person ids that had starred activities deleted. with the activity ids.
     */
    public BulkActivityDeleteResponse(final List<Long> inActivityIds, final List<Long> inCommentIds,
            final Map<Long, Set<Long>> inPeopleWithStarredActivities)
    {
        activityIds = inActivityIds;
        commentIds = inCommentIds;
        peopleWithStarredActivities = inPeopleWithStarredActivities;
    }

    /**
     * @return the commentIds
     */
    public List<Long> getCommentIds()
    {
        return commentIds;
    }

    /**
     * @param inCommentIds
     *            the commentIds to set
     */
    public void setCommentIds(final List<Long> inCommentIds)
    {
        commentIds = inCommentIds;
    }

    /**
     * @return the peopleWithStarredActivities
     */
    public Map<Long, Set<Long>> getPeopleWithStarredActivities()
    {
        return peopleWithStarredActivities;
    }

    /**
     * @param inPeopleWithStarredActivities
     *            the peopleWithStarredActivities to set
     */
    public void setPeopleWithStarredActivities(final Map<Long, Set<Long>> inPeopleWithStarredActivities)
    {
        peopleWithStarredActivities = inPeopleWithStarredActivities;
    }

    /**
     * @return the activityIds
     */
    public List<Long> getActivityIds()
    {
        return activityIds;
    }

    /**
     * @param inActivityIds
     *            the activityIds to set
     */
    public void setActivityIds(final List<Long> inActivityIds)
    {
        activityIds = inActivityIds;
    }

}
