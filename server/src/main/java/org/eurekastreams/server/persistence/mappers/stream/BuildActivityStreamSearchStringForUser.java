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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.GetPrivateCoordinatedAndFollowedGroupIdsForUser;

/**
 * Build a Lucene search string that security scopes the activity stream for a
 * specific user. This will include clauses for all public activities,
 * activities posted under organizations that the user is a coordinator for, and
 * to groups that the user is a coordinator or follower of.
 */
public class BuildActivityStreamSearchStringForUser extends
        BaseArgDomainMapper<Long, String>
{
    /**
     * Logger.
     */
    private Log log = LogFactory
            .getLog(BuildActivityStreamSearchStringForUser.class);

    /**
     * Mapper to get a list of all group ids that aren't public that a user can
     * see activity for.
     */
    private GetPrivateCoordinatedAndFollowedGroupIdsForUser getGroupIdsMapper;

    /**
     * Constructor.
     * 
     * @param inGetGroupIdsMapper
     *            Mapper to get a list of all group ids that aren't public that
     *            a user can see activity for.
     */
    public BuildActivityStreamSearchStringForUser(
            final GetPrivateCoordinatedAndFollowedGroupIdsForUser inGetGroupIdsMapper)
    {
        getGroupIdsMapper = inGetGroupIdsMapper;
    }

    /**
     * Return a Lucene query string that will return IDs for all activities that
     * a user has access to.
     * 
     * @param inUserPersonId
     *            the user id making the request
     * @return a Lucene query that will return the IDs for all actvities that a
     *         user has access to
     */
    public String execute(final Long inUserPersonId)
    {
        log
                .info("Building the cached security-scoped activity search string for user with person id: "
                        + inUserPersonId);

        // get all the group ids coordinated
        Set<Long> groupIds = getGroupIdsMapper.execute(inUserPersonId);

        // build the query
        StringBuilder sb = new StringBuilder();
        sb.append("isPublic:t");

        if (groupIds.size() > 0)
        {
            sb.append(" recipient:(");

            for (Long groupId : groupIds)
            {
                sb.append(" g" + groupId);
            }

            sb.append(")");
        }

        String result = sb.toString();

        log
                .info("User's cached security-scoped activity search string for user: "
                        + inUserPersonId + " = " + result);

        return result;
    }

}
