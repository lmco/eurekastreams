/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.Query;

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Followable;
import org.eurekastreams.server.domain.GroupFollower;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.cache.RemoveDomainGroupFromCacheMapper;
import org.eurekastreams.server.persistence.strategies.DescendantOrganizationStrategy;

/**
 * This class provides the mapper functionality for DomainGroup entities.
 */
@Deprecated
public class DomainGroupMapper extends DomainEntityMapper<DomainGroup> implements FollowMapper, CompositeEntityMapper
{
    /**
     * Constructor.
     *
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     * @param inRemoveGroupFromCacheMapper
     *            mapper to remove group from cache
     */
    public DomainGroupMapper(final QueryOptimizer inQueryOptimizer,
            final RemoveDomainGroupFromCacheMapper inRemoveGroupFromCacheMapper)
    {
        super(inQueryOptimizer);
        removeGroupFromCacheMapper = inRemoveGroupFromCacheMapper;
    }

    /**
     * Mapper to remove group from cache.
     */
    private RemoveDomainGroupFromCacheMapper removeGroupFromCacheMapper;

    /**
     * The descendant organization strategy.
     */
    private DescendantOrganizationStrategy descendantOrgStrategy;

    /**
     * Set the DescendantOrgStrategy.
     *
     * @param inDescendantOrgStrategy
     *            the descendantOrgStrategy to set
     */
    public void setDescendantOrgStrategy(final DescendantOrganizationStrategy inDescendantOrgStrategy)
    {
        descendantOrgStrategy = inDescendantOrgStrategy;
    }

    /**
     * Get the descendant organization strategy.
     *
     * @return the descendantOrgStrategy the descendant org strategy
     */
    protected DescendantOrganizationStrategy getDescendantOrgStrategy()
    {
        if (descendantOrgStrategy == null)
        {
            throw new NullPointerException("descendantOrgStrategy is null - make sure to set it on JpaPersonMapper.");
        }
        return descendantOrgStrategy;
    }

    /**
     * Look up a group by its short name.
     *
     * @param groupShortName
     *            the short name of a group to look for
     * @return the DomainGroup corresponding to the provided short name
     */
    @SuppressWarnings("unchecked")
    public DomainGroup findByShortName(final String groupShortName)
    {
        Query q = getEntityManager().createQuery("from DomainGroup where shortname = :inName").setParameter("inName",
                groupShortName.toLowerCase()).setFlushMode(FlushModeType.COMMIT);

        List results = q.getResultList();

        return (results.size() == 0) ? null : (DomainGroup) results.get(0);
    }

    /**
     * @return the entity's type
     */
    @Override
    protected String getDomainEntityName()
    {
        return "DomainGroup";
    }

    /**
     * Creates a follower/following relationship between two entities.
     *
     * @param followerId
     *            The id of the follower Person
     * @param followingId
     *            The entity id being Followed.
     */
    public void addFollower(final long followerId, final long followingId)
    {
        Query q = getEntityManager().createQuery(
                "FROM GroupFollower where followerId=:followerId and followingId=:followingId").setParameter(
                "followerId", followerId).setParameter("followingId", followingId);

        if (q.getResultList().size() > 0)
        {
            // already following
            return;
        }

        // Shift down the groupstreamindex of the existing followed groups
        getEntityManager().createQuery(
                "update GroupFollower set groupstreamindex = groupstreamindex + 1 where followerId=:followerId")
                .setParameter("followerId", followerId).executeUpdate();

        // add follower, groupstreamindex defaults to 0
        getEntityManager().persist(new GroupFollower(followerId, followingId));

        // now update the counts for persons.
        getEntityManager().createQuery(
                "update versioned Person set groupsCount = followingGroup.size where id=:followerId").setParameter(
                "followerId", followerId).executeUpdate();

        getEntityManager().createQuery(
                "update versioned DomainGroup set followersCount = followers.size where id=:followingId").setParameter(
                "followingId", followingId).executeUpdate();

        getEntityManager().flush();
        getEntityManager().clear();

        DomainGroup followingEntity = findById(followingId);

        // reindex the following in the search index
        getFullTextSession().index(followingEntity);

        // remove the group from cache
        removeGroupFromCacheMapper.execute(followingEntity);
    }

    /**
     * Returns a set of People following the specified DomainGroup.
     *
     * @param shortName
     *            The shortName of the DomainGroup for whom to get followers.
     * @param start
     *            paging start.
     * @param end
     *            paging end.
     * @return paged set of followers.
     */
    public PagedSet<Person> getFollowers(final String shortName, final int start, final int end)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("shortName", shortName.toLowerCase());
        String query = "select g.followers from DomainGroup g where g.shortName = :shortName";

        // although the line above is just concatenating a query string
        // and this implies vulnerability to SQL injection attacks,
        // the call to this.getPagedResults() actually parameterizes the SQL
        // so there is not actually risk of SQL injection here.
        PagedSet<Person> results = getTypedPagedResults(start, end, query, parameters);

        return results;
    }

    /**
     * Returns a set of DomainGroups that are being followed by the specified person.
     *
     * @param accountId
     *            The id of the DomainGroup for whom to get following.
     * @param start
     *            paging start.
     * @param end
     *            paging end.
     * @return paged set of following.
     */
    public PagedSet<Followable> getFollowing(final String accountId, final int start, final int end)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("accountId", accountId);
        String query = "select p.followingGroup from Person p where p.accountId = :accountId";

        // although the line above is just concatenating a query string
        // and this implies vulnerability to SQL injection attacks,
        // the call to this.getPagedResults() actually parameterizes the SQL
        // so there is not actually risk of SQL injection here.
        PagedSet<Followable> results = getTypedPagedResults(start, end, query, parameters);

        return results;
    }

    /**
     * Returns true if follower/following relationship exists false otherwise.
     *
     * @param followerAccountId
     *            The follower person's account Id.
     * @param shortName
     *            The short name of the group being followed
     * @return True if follower/following relationship exists false otherwise.
     */
    @SuppressWarnings("unchecked")
    public boolean isFollowing(final String followerAccountId, final String shortName)
    {
        Query q = getEntityManager().createQuery(
                "FROM Person as follower" + " inner join follower.followingGroup as following"
                        + " where follower.accountId=:followerAccountId and"
                        + " following.shortName=:followingShortName").setParameter("followerAccountId",
                followerAccountId).setParameter("followingShortName", shortName.toLowerCase());

        List<Person> results = q.getResultList();

        return (results.size() != 0);
    }

    /**
     * Removes a follower/following relationship between a Person and a DomainGroup.
     *
     * @param followerId
     *            The if of the follower Person
     * @param followingId
     *            The group id being Followed.
     */
    public void removeFollower(final long followerId, final long followingId)
    {
        // Shift up the groupstreamindex of the remaining followed groups
        getEntityManager().createQuery(// \n
                "update GroupFollower gf1 set gf1.groupStreamIndex = gf1.groupStreamIndex - 1 "// \n
                        + "where gf1.groupStreamIndex > " // \n
                        + "(select gf2.groupStreamIndex from GroupFollower gf2 " // \n
                        + "where gf2.pk.followerId=gf1.pk.followerId and gf2.pk.followingId=:followingId)" // \n
                        + "AND gf1.pk.followerId=:followerId") // \n
                .setParameter("followerId", followerId).setParameter("followingId", followingId).executeUpdate();

        int rowsDeleted = getEntityManager().createQuery(
                "DELETE FROM GroupFollower where followerId=:followerId and followingId=:followingId").setParameter(
                "followerId", followerId).setParameter("followingId", followingId).executeUpdate();

        if (rowsDeleted == 0)
        {
            // not following, short circuit.
            return;
        }

        // now update the counts for persons.
        getEntityManager().createQuery(
                "update versioned Person set groupsCount = followingGroup.size where id=:followerId").setParameter(
                "followerId", followerId).executeUpdate();

        getEntityManager().createQuery(
                "update versioned DomainGroup set followersCount = followers.size where id=:followingId").setParameter(
                "followingId", followingId).executeUpdate();

        getEntityManager().flush();
        getEntityManager().clear();

        DomainGroup followingEntity = findById(followingId);

        // reindex the following in the search index
        getFullTextSession().index(followingEntity);

        // remove the group from cache
        removeGroupFromCacheMapper.execute(followingEntity);
    }

    /**
     * Get a String representation the Person.id of all of the Person.ids for coordinators and followers of the input
     * group.
     *
     * @param domainGroup
     *            the DomainGroup to find coordinators and followers for
     * @return an array of all of the Person.ids for coordinators and followers of the input group
     */
    @SuppressWarnings("unchecked")
    public Long[] getFollowerAndCoordinatorPersonIds(final DomainGroup domainGroup)
    {
        // use a set to eliminate duplicates
        HashSet<Long> peopleIds = new HashSet<Long>();
        Query q = getEntityManager().createQuery("SELECT pk.followerId FROM GroupFollower WHERE followingId=:groupId")
                .setParameter("groupId", domainGroup.getId());
        peopleIds.addAll(q.getResultList());

        q = getEntityManager().createQuery(
                "SELECT p.id FROM Person p, DomainGroup g WHERE p MEMBER OF g.coordinators AND g.id=:groupId")
                .setParameter("groupId", domainGroup.getId());
        peopleIds.addAll(q.getResultList());

        return peopleIds.toArray(new Long[peopleIds.size()]);
    }

    /**
     * Delete this group.
     *
     * @param id
     *            the id of the group to delete.
     */
    public void deleteById(final long id)
    {
        DomainGroup group = findById(id);
        getEntityManager().remove(group);

        getEntityManager().flush();
    }
}
