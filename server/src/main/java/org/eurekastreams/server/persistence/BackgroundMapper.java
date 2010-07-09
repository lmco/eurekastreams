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
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.Background;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.Person;

/**
 * This class provides the mapper functionality for Background entities.
 */
public class BackgroundMapper extends DomainEntityMapper<Background>
{
    /**
     * The person mapper.
     */
    private PersonMapper personMapper;

    /**
     * Constructor.
     *
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     *
     * @param inPersonMapper
     *            the person mapper.
     */
    public BackgroundMapper(final QueryOptimizer inQueryOptimizer, final PersonMapper inPersonMapper)
    {
        super(inQueryOptimizer);
        personMapper = inPersonMapper;
    }

    /**
     * logger.
     */
    private static Log logger = LogFactory.getLog(BackgroundMapper.class);

    /**
     * Get the domain entity name for the generic query operations.
     *
     * @return the domain entity name for the gadget query operations.
     */
    @Override
    protected String getDomainEntityName()
    {
        return "Background";
    }

    /**
     * Find a person's Background.
     *
     * @param inOpenSocialId
     *            ID of the person
     *
     * @return list of Background for the person
     */
    @SuppressWarnings("unchecked")
    public Background findPersonBackground(final String inOpenSocialId)
    {
        logger.debug("finding background for person with opensocial id: " + inOpenSocialId);

        String queryString = "from Background b where b.person.openSocialId = :openSocialId";
        Query query = getEntityManager().createQuery(queryString);
        query.setParameter("openSocialId", inOpenSocialId);

        List results = query.getResultList();

        return (results.size() == 0) ? null : (Background) results.get(0);
    }

    /**
     * Delete a person's background.
     *
     * @param inBackground
     *            The Background to delete.
     */
    public void delete(final Background inBackground)
    {
        getEntityManager().remove(inBackground);
    }

    /**
     * delete bg item, used so there's not orphaned items when you update a bg.
     *
     * @param item
     *            item to be deleted
     */
    public void deleteItem(final BackgroundItem item)
    {
        getEntityManager().remove(item);
    }

    /**
     * Find the background. If none exists, create one for this user.
     *
     * @param inOpenSocialId
     *            the person whose background we are getting.
     * @return The background item.
     */
    @SuppressWarnings("unchecked")
    public Background findOrCreatePersonBackground(final String inOpenSocialId)
    {

        Background background = findPersonBackground(inOpenSocialId);

        if (null == background)
        {
            logger.debug("background not found, so creating one");

            Query q = getEntityManager().createQuery("from Person where openSocialId = :openSocialId").setParameter(
                    "openSocialId", inOpenSocialId);

            // Can't find the background. Get the person so we can make one.
            List personList = q.getResultList();
            if (personList.size() == 0)
            {
                // We can't even find this openSocialId. Give up.
                return null;
            }

            //
            background = new Background((Person) personList.get(0));

            background.setBackgroundItems(new ArrayList<BackgroundItem>(), BackgroundItemType.AFFILIATION);
            background.setBackgroundItems(new ArrayList<BackgroundItem>(), BackgroundItemType.HONOR);
            background.setBackgroundItems(new ArrayList<BackgroundItem>(), BackgroundItemType.INTEREST);
            background.setBackgroundItems(new ArrayList<BackgroundItem>(), BackgroundItemType.SKILL);

            getEntityManager().persist(background);
        }

        return background;
    }

    /**
     * Used for auto-complete, returns top "x" results where name is similar to the search string passed in and
     * background type is same.
     *
     * @param backgroundType
     *            The type of BackgroundItem to search.
     * @param searchString
     *            The target string to search on.
     * @param maxResults
     *            The max number of results to return
     * @return Top "x" results where name is similar to the search string passed in and background type is same.
     */
    @SuppressWarnings("unchecked")
    public List<String> findBackgroundItemNamesByType(final BackgroundItemType backgroundType,
            final String searchString, final int maxResults)
    {
        String searchParam = searchString + "%";

        Query q = getEntityManager().createQuery(
                "SELECT distinct(b.name) FROM BackgroundItem b "
                        + "WHERE b.backgroundType=:backgroundType AND lower(b.name) LIKE :searchParam").setParameter(
                "backgroundType", backgroundType).setParameter("searchParam", searchParam.toLowerCase()).setMaxResults(
                maxResults);

        List<String> results = q.getResultList();
        return results;

    }

    /**
     * Flushes and updates the index of the person.
     *
     * @param uuid
     *            the uuid of the person to update.
     */
    public void flush(final String uuid)
    {
        flush();
        getFullTextSession().index(personMapper.findByOpenSocialId(uuid));
    }
}
