/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence;

import org.eurekastreams.server.domain.Followable;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.Person;

/**
 * Brings together methods that are used for following a Person, Group, or Organization. 
 */
public interface FollowMapper
{
    /**
     * Returns a set of People who are following the specified Person.
     * 
     * @param accountId
     *            The Person for whom to get followers.
     * @param start
     *            paging start.
     * @param end
     *            paging end.
     * @return paged set of followers.
     */
    PagedSet<Person> getFollowers(final String accountId, final int start, final int end);

    /**
     * Returns a set of Followables who are being followed by the specified Person.
     * 
     * @param accountId
     *            The Person for whom to get followers.
     * @param start
     *            paging start.
     * @param end
     *            paging end.
     * @return paged set of followers.
     */
    PagedSet<Followable> getFollowing(final String accountId, final int start, final int end);

    /**
     * Creates a follower/following relationship between two entities.
     * 
     * @param followerId
     *            The follower entity id
     * @param followingId
     *            The id of the entity being Followed.
     */
    void addFollower(long followerId, long followingId);

    /**
     * Removes a follower/following relationship between two entities.
     * 
     * @param followerId
     *            The follower entity id
     * @param followingId
     *            The id of the entity being Followed.
     */
    void removeFollower(long followerId, long followingId);

    /**
     * Returns true if follower/following relationship exists false otherwise.
     * 
     * @param followerAccountId
     *            The follower person's account Id.
     * @param followingAccountId
     *            The following person's accountId.
     * @return True if follower/following relationship exists false otherwise.
     */
    boolean isFollowing(String followerAccountId, String followingAccountId);
}
