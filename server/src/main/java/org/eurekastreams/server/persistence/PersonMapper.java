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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.Followable;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.Person;

/**
 * This class provides the mapper functionality for Person entities.
 */
@Deprecated
public class PersonMapper extends DomainEntityMapper<Person> implements FollowMapper
{
    /**
     * Local log instance.
     */
    private static Log logger = LogFactory.getLog(PersonMapper.class);

    /**
     * Constructor.
     * 
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public PersonMapper(final QueryOptimizer inQueryOptimizer)
    {
        super(inQueryOptimizer);
    }

    /**
     * Retrieve the name of the DomainEntity. This is to allow for the super class to identify the table within
     * hibernate.
     * 
     * @return The name of the domain entity.
     */
    @Override
    protected String getDomainEntityName()
    {
        return "Person";
    }

    /**
     * Find a person by accountId.
     * 
     * @param accountId
     *            the accountId of the user to search for - lower-cased for uniqueness
     * @return the Person with the user account
     */
    @SuppressWarnings("unchecked")
    public Person findByAccountId(final String accountId)
    {
        Query q = getEntityManager().createQuery("from Person where accountId = :accountId").setParameter("accountId",
                accountId.toLowerCase());
        List results = q.getResultList();

        return (results.size() == 0) ? null : (Person) results.get(0);
    }

    /**
     * Find a person by opensocial ID.
     * 
     * @param openSocialId
     *            the openSocialId of the user to search for - lower-cased for uniqueness
     * @return the Person with the open social id.
     */
    @SuppressWarnings("unchecked")
    public Person findByOpenSocialId(final String openSocialId)
    {
        Query q = getEntityManager().createQuery("from Person where openSocialId = :openSocialId").setParameter(
                "openSocialId", openSocialId.toLowerCase());

        List results = q.getResultList();

        return (results.size() == 0) ? null : (Person) results.get(0);
    }

    /**
     * Creates a follower/following relationship between two Person objects.
     * 
     * @param followerId
     *            The id of the follower Person
     * @param followingId
     *            The id of the person being Followed.
     */
    public void addFollower(final long followerId, final long followingId)
    {
        Query q = getEntityManager().createQuery(
                "FROM Follower where followerId=:followerId and followingId=:followingId").setParameter("followerId",
                followerId).setParameter("followingId", followingId);

        if (q.getResultList().size() > 0)
        {
            // already following
            return;
        }

        // add follower
        getEntityManager().persist(new Follower(followerId, followingId));

        // now update the counts for persons subtracting 1 for themselves.
        getEntityManager().createQuery(
                "update versioned Person set followingCount = following.size - 1 where id=:followerId").setParameter(
                "followerId", followerId).executeUpdate();

        getEntityManager().createQuery(
                "update versioned Person set followersCount = followers.size - 1 where id=:followingId").setParameter(
                "followingId", followingId).executeUpdate();

        getEntityManager().flush();
        getEntityManager().clear();

        // reindex the following in the search index

        // Note: Finding the entity by id is massively faster than doing a refresh on
        // the entity. This way the recently fetched entity will have the updated counts
        // to send to index.
        Person followingEntity = findById(followingId);

        getFullTextSession().index(followingEntity);
    }

    /**
     * Removes a follower/following relationship between two Person objects.
     * 
     * @param followerId
     *            The id of the follower Person
     * @param followingId
     *            The person id being Followed.
     */
    public void removeFollower(final long followerId, final long followingId)
    {
        int rowsDeleted = getEntityManager().createQuery(
                "DELETE FROM Follower where followerId=:followerId and followingId=:followingId").setParameter(
                "followerId", followerId).setParameter("followingId", followingId).executeUpdate();

        if (rowsDeleted == 0)
        {
            // not following, short circuit.
            return;
        }
        // now update the counts for persons subtracting 1 for themselves.
        getEntityManager().createQuery(
                "update versioned Person set followingCount = following.size - 1 " + "where id=:followerId")
                .setParameter("followerId", followerId).executeUpdate();

        getEntityManager().createQuery(
                "update versioned Person set followersCount = followers.size - 1 where id=:followingId").setParameter(
                "followingId", followingId).executeUpdate();

        getEntityManager().flush();
        getEntityManager().clear();

        Person followingEntity = findById(followingId);

        // reindex the following in the search index
        getFullTextSession().index(followingEntity);
    }

    /**
     * Returns a set of People following the specified Person, minus themselves.
     * 
     * @param accountId
     *            The Person for whom to get followers.
     * @param start
     *            paging start.
     * @param end
     *            paging end.
     * @return paged set of followers.
     */
    public PagedSet<Person> getFollowers(final String accountId, final int start, final int end)
    {
        return getConnectionsMinusSelf(accountId, start, end, "followers");
    }

    /**
     * Returns a set of People who are being followed by the specified person.
     * 
     * @param accountId
     *            The Person for whom to get following.
     * @param start
     *            paging start.
     * @param end
     *            paging end.
     * @return paged set of following.
     */
    public PagedSet<Followable> getFollowing(final String accountId, final int start, final int end)
    {
        return getConnectionsMinusSelf(accountId, start, end, "following");
    }

    /**
     *Gets connections for a Person excluding that person in the results.
     * 
     * @param <V>
     *            allows this method to return PagedSets of different types (Person, Followable).
     * @param accountId
     *            The Person for whom to get info.
     * @param start
     *            paging start.
     * @param end
     *            paging end.
     * 
     * @param which
     *            should be attribute in the model: "follower" or "following"
     * 
     * @return paged set of connections.
     */
    private <V> PagedSet<V> getConnectionsMinusSelf(final String accountId, final int start, final int end,
            final String which)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("accountId", accountId);
        String query = "select followedOrFollowingPerson FROM Person as user,"
                + " Person AS followedOrFollowingPerson WHERE followedOrFollowingPerson MEMBER OF user." + which
                + " AND user.accountId =  :accountId and followedOrFollowingPerson.accountId != :accountId";

        // although the line above is just concatenating a query string
        // and this implies vulnerability to SQL injection attacks,
        // the call to this.getPagedResults() actually parameterizes the SQL
        // so there is not actually risk of SQL injection here.
        return getTypedPagedResults(start, end, query, parameters);
    }

    /**
     * Returns true if follower/following relationship exists false otherwise.
     * 
     * @param followerAccountId
     *            The follower person's account Id.
     * @param followingAccountId
     *            The following person's accountId.
     * @return True if follower/following relationship exists false otherwise.
     */
    @SuppressWarnings("unchecked")
    public boolean isFollowing(final String followerAccountId, final String followingAccountId)
    {
        Query q = getEntityManager().createQuery(
                "FROM Person as follower" + " inner join follower.following as following"
                        + " where follower.accountId=:followerAccountId and"
                        + " following.accountId=:followingAccountId").setParameter("followerAccountId",
                followerAccountId).setParameter("followingAccountId", followingAccountId);

        List<Person> results = q.getResultList();

        return (results.size() != 0);
    }

    /**
     * Finds people with the last name starting with the prefix.
     * 
     * @param prefix
     *            the first letters in the last name to search by
     * 
     * @return a list of persons with that last name prefix
     */
    @SuppressWarnings("unchecked")
    public List<Person> findPeopleByPrefix(final String prefix)
    {
        String[] prefixes = prefix.split(", ");

        String lastName = prefixes[0];
        String firstName = "";

        if (prefixes.length > 1)
        {
            firstName = prefixes[1];
        }

        Query q = getEntityManager().createQuery(
                "From Person where lower(lastName) LIKE :lprefix and lower(displayName) LIKE :fprefix "
                        + "ORDER BY lastName, firstName, middleName").setParameter("lprefix",
                lastName.toLowerCase() + "%").setParameter("fprefix", firstName.toLowerCase() + "%");

        return q.getResultList();
    }

    /**
     * Finds people based on a list of OpenSocial Ids. This method supports opensocial calls for retrieving people.
     * 
     * @param openSocialIds
     *            The list of opensocial ids to return Person objects for.
     * @return a list of persons that match the list of ids supplied.
     */
    @SuppressWarnings("unchecked")
    public List<Person> findPeopleByOpenSocialIds(final List<String> openSocialIds)
    {
        String[] osIds = openSocialIds.toArray(new String[openSocialIds.size()]);
        String osIdSet = "";
        for (int index = 0; index < osIds.length; index++)
        {
            osIdSet += "'" + osIds[index] + "'";
            if (index < osIds.length - 1)
            {
                osIdSet += ", ";
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Finding people by openSocialIds: " + osIdSet);
        }

        Query q = getEntityManager().createQuery("From Person where openSocialId IN (:osIds)").setParameter("osIds",
                openSocialIds);

        List<Person> people = q.getResultList();

        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieved " + people.size() + " results from opensocialids: " + osIdSet);
        }

        return people;
    }

    /**
     * Finds people being followed based on a list of follower OpenSocial Ids. This method supports opensocial calls for
     * retrieving people. This does not exclude their own account.
     * 
     * @param openSocialIds
     *            The list of opensocial ids to return followed Person objects for.
     * @return a list of follower persons that match the list of ids supplied.
     */
    @SuppressWarnings("unchecked")
    public List<Person> findPeopleFollowedUsingFollowerOpenSocialIds(final List<String> openSocialIds)
    {
        List<Person> people = findPeopleByOpenSocialIds(openSocialIds);

        List<Person> outList = new ArrayList();
        for (Person follower : people)
        {
            outList.addAll(follower.getFollowing());
        }
        return outList;
    }
}
