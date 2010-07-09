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

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.FeedReader;
import org.eurekastreams.server.domain.FeedReaderUrlCount;

//TODO must be brought out into the feed reader project. 

/**
 * This class provides the mapper functionality for FeedReader entities.
 */
public class FeedReaderMapper
{
    /**
     * Local log instance.
     */
    private static Log logger = LogFactory.getLog(FeedReaderMapper.class);

    /**
     * EntityManager to use for all ORM operations.
     */
    private EntityManager entityManager;

    /**
     * Getter for entityManager.
     * 
     * @return The entityManager.
     */
    protected EntityManager getEntityManager()
    {
        return entityManager;
    }

    /**
     * Set the entity manager - used for low-level ORM hits like flush & clear.
     * 
     * @param inEntityManager
     *            the EntityManager to inject
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager inEntityManager)
    {
        this.entityManager = inEntityManager;
    }

    /**
     * Insert the domain entity.
     * 
     * @param domainEntity
     *            The domainEntity to operate on.
     */
    public void insert(final FeedReader domainEntity)
    {
        entityManager.persist(domainEntity);

    }

    /**
     * Update all entities that have changed since they were loaded within the same context.
     */
    public void flush()
    {
        entityManager.flush();
    }

    /**
     * Refresh the domain entity.
     * 
     * @param domainEntity
     *            the domain entity.
     */
    public void refresh(final FeedReader domainEntity)
    {
        entityManager.refresh(domainEntity);
    }

    /**
     * Find the domain entity by id.
     * 
     * @param inModuleId
     *            module id of the feed read to lookup.
     * @param inOpenSocialId
     *            User Id (OS ID) of the feed reader to lookup.
     * 
     * @return the entity with the input
     */
    @SuppressWarnings("unchecked")
    public FeedReader findFeedByOpenSocialIdAndModuleId(final String inOpenSocialId, final String inModuleId)
    {
        Query q = entityManager.createQuery(
                "from FeedReader where moduleId = :inModuleId  and openSocialId= :inOpenSocialId").setParameter(
                "inModuleId", inModuleId).setParameter("inOpenSocialId", inOpenSocialId);

        List<FeedReader> results = q.getResultList();
        if (results.size() < 1)
        {
            return null;
        }
        else
        {
            return (FeedReader) results.get(0);
        }
    }

    /**
     * Find the domain entity by id.
     * 
     * @param inOpenSocialId
     *            User Id (OS ID) of the feed reader to lookup.
     * 
     * @return the entity with the input
     */
    @SuppressWarnings("unchecked")
    public List<FeedReader> findFeedsByOpenSocialId(final String inOpenSocialId)
    {
        Query q = entityManager.createQuery("from FeedReader where openSocialId= :inOpenSocialId").setParameter(
                "inOpenSocialId", inOpenSocialId);

        List<FeedReader> results = q.getResultList();

        return results;
    }

    /**
     * Find the domain entity by id.
     * 
     * @return the entity with the input
     */
    @SuppressWarnings("unchecked")
    public List<FeedReaderUrlCount> findTop10PublicFeeds()
    {
        Query q = entityManager.createQuery("SELECT url, count(*), feedTitle from FeedReader"
                + " group by url, feedTitle ORDER BY count(*) desc");

        // TODO find a better place to set this. 5+5 = magic number rule work around.
        q.setMaxResults(5 + 5);
        List<FeedReaderUrlCount> feedList = new LinkedList<FeedReaderUrlCount>();
        List<Object[]> results = (List<Object[]>) q.getResultList();

        for (Object[] result : results)
        {
            FeedReaderUrlCount resultItem = new FeedReaderUrlCount();
            resultItem.setUrl((String) result[0]);
            resultItem.setCount((Long) result[1]);
            resultItem.setFeedTitle((String) result[2]);
            feedList.add(resultItem);
        }

        return feedList;
    }

    /**
     * Find the domain entity by id.
     * 
     * @param openSocialIdString
     *            List of OS Ids for the people to get Top feeds for.
     * @return the entity with the input
     */
    @SuppressWarnings("unchecked")
    public List<FeedReaderUrlCount> findTop10FriendFeeds(final String openSocialIdString)
    {
        List<FeedReaderUrlCount> feedList = new LinkedList<FeedReaderUrlCount>();

        if (!openSocialIdString.isEmpty() && openSocialIdString != null)
        {
            Query q = entityManager
                    .createQuery("SELECT url, count(*), feedTitle from FeedReader where openSocialId IN ("
                            + openSocialIdString + ") group by url, feedTitle ORDER BY count(*) desc");
            // TODO get rid of this and figure out an appropriate place to put this.
            String theMagicNumberRuleSucks = "10";
            q.setMaxResults(Integer.parseInt(theMagicNumberRuleSucks));
            List<Object[]> results = (List<Object[]>) q.getResultList();

            for (Object[] result : results)
            {
                FeedReaderUrlCount resultItem = new FeedReaderUrlCount();
                resultItem.setUrl((String) result[0]);
                resultItem.setCount((Long) result[1]);
                resultItem.setFeedTitle((String) result[2]);
                feedList.add(resultItem);
            }
        }

        return feedList;
    }

    /**
     * Delete a feed.
     * 
     * @param id
     *            the id of the recommendation to delete.
     */
    public void delete(final long id)
    {
        entityManager.createQuery("DELETE FROM FeedReader where id=:id").setParameter("id", id).executeUpdate();
    }
}
