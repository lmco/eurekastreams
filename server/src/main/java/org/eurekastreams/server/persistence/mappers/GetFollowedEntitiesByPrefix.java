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
package org.eurekastreams.server.persistence.mappers;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.requests.GetEntitiesByPrefixRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetFollowedPersonIds;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.DisplayEntityModelView;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * Mapper for returning followed entities by prefix.
 * 
 */
public class GetFollowedEntitiesByPrefix extends ReadMapper<GetEntitiesByPrefixRequest, List<DisplayEntityModelView>>
{
    /**
     * default max results from query.
     */
    private static final int DEFAULT_MAX_RESULTS = 20;

    /**
     * Mapper to get the id of the current person by accountId.
     */
    private GetPeopleByAccountIds getPeopleByAccountIdsMapper;

    /**
     * Mapper to get the people ids that a person is following.
     */
    private GetFollowedPersonIds getFollowedPersonIdsMapper;

    /**
     * Mapper to get the group ids that a person is following.
     */
    private DomainMapper<Long, List<Long>> getFollowedGroupIdsMapper;

    /**
     * Return list of DisplayEntityModelViews representing people/groups a user is following that match the prefix
     * requirements.
     * 
     * @param inRequest
     *            The request parameters.
     * @return list of DisplayEntityModelView representing people/groups a user is following that match the prefix
     *         requirements.
     */
    @Override
    public List<DisplayEntityModelView> execute(final GetEntitiesByPrefixRequest inRequest)
    {
        List<DisplayEntityModelView> results = new ArrayList<DisplayEntityModelView>();

        Long personId = getPeopleByAccountIdsMapper.fetchId(inRequest.getUserKey());

        results.addAll(getFollowedPeopleByPrefix(inRequest, getFollowedPersonIdsMapper.execute(personId)));
        results.addAll(getFollowedGroupsByPrefix(inRequest, getFollowedGroupIdsMapper.execute(personId)));

        // add "no results" DisplayEntityModelView if nothing was found.
        if (results.size() == 0)
        {
            DisplayEntityModelView femv = new DisplayEntityModelView();
            femv.setType(EntityType.NOTSET);
            femv.setDisplayName("No matches found");
            results.add(femv);
        }

        return results;
    }

    /**
     * Return list of DisplayEntityModelView representing people a user is following that match the prefix requirements.
     * 
     * @param inRequest
     *            The request parameters.
     * @param followedPersonIds
     *            Set of accountIds for followed people.
     * @return list of DisplayEntityModelView representing people a user is following that match the prefix
     *         requirements.
     */
    @SuppressWarnings("unchecked")
    private List<DisplayEntityModelView> getFollowedPeopleByPrefix(final GetEntitiesByPrefixRequest inRequest,
            final List<Long> followedPersonIds)
    {
        List<DisplayEntityModelView> results = new ArrayList<DisplayEntityModelView>();

        // return immediately, no need to execute query.
        if (followedPersonIds.size() == 0)
        {
            return results;
        }

        Criteria criteria = getHibernateSession().createCriteria(Person.class);

        // determine the fields we want.
        ProjectionList fields = Projections.projectionList();
        fields.add(getColumn("preferredName"));
        fields.add(getColumn("lastName"));
        fields.add(getColumn("accountId"));
        fields.add(getColumn("streamScope.id"));
        criteria.setProjection(fields);

        // set the max results to return
        // TODO find out what this should be.
        criteria.setMaxResults(DEFAULT_MAX_RESULTS);

        criteria.add(Restrictions.in("this.id", followedPersonIds));
        criteria.add(Restrictions.eq("this.streamPostable", Boolean.TRUE));

        Criterion lastName = Restrictions.ilike("this.lastName", inRequest.getPrefix(), MatchMode.START);
        Criterion firstName = Restrictions.ilike("this.firstName", inRequest.getPrefix(), MatchMode.START);
        Criterion preferredName = Restrictions.ilike("this.preferredName", inRequest.getPrefix(), MatchMode.START);

        criteria.add(Restrictions.or(lastName, Restrictions.or(firstName, preferredName)));

        // execute query
        List<Object[]> queryResults = criteria.list();

        for (Object[] queryResult : queryResults)
        {
            DisplayEntityModelView femv = new DisplayEntityModelView();
            femv.setDisplayName((String) queryResult[0] + " " + (String) queryResult[1]);
            femv.setUniqueKey((String) queryResult[2]);
            femv.setStreamScopeId((Long) queryResult[3]);
            femv.setType(EntityType.PERSON);
            results.add(femv);
        }
        return results;
    }

    /**
     * Return list of DisplayEntityModelView representing groups a user is following that match the prefix requirements.
     * 
     * @param inRequest
     *            The request parameters.
     * @param followedGroups
     *            Set of group ids for followed groups.
     * @return list of DisplayEntityModelView representing groups a user is following that match the prefix
     *         requirements.
     */
    @SuppressWarnings("unchecked")
    private List<DisplayEntityModelView> getFollowedGroupsByPrefix(final GetEntitiesByPrefixRequest inRequest,
            final List<Long> followedGroups)
    {
        List<DisplayEntityModelView> results = new ArrayList<DisplayEntityModelView>();

        // return immediately, no need to execute query.
        if (followedGroups.size() == 0)
        {
            return results;
        }

        Criteria criteria = getHibernateSession().createCriteria(DomainGroup.class);

        // determine the fields we want.
        ProjectionList fields = Projections.projectionList();
        fields.add(getColumn("name"));
        fields.add(getColumn("shortName"));
        fields.add(getColumn("streamScope.id"));
        criteria.setProjection(fields);

        // set the max results to return
        // TODO find out what this should be.
        criteria.setMaxResults(DEFAULT_MAX_RESULTS);

        criteria.add(Restrictions.in("this.id", followedGroups));
        criteria.add(Restrictions.ilike("this.name", inRequest.getPrefix(), MatchMode.START));
        criteria.add(Restrictions.eq("this.streamPostable", Boolean.TRUE));

        // execute query
        List<Object[]> queryResults = criteria.list();

        for (Object[] queryResult : queryResults)
        {
            DisplayEntityModelView femv = new DisplayEntityModelView();
            femv.setDisplayName((String) queryResult[0]);
            femv.setUniqueKey((String) queryResult[1]);
            femv.setStreamScopeId((Long) queryResult[2]);
            femv.setType(EntityType.GROUP);
            results.add(femv);
        }

        return results;
    }

    /**
     * @param inGetFollowedPersonIdsMapper
     *            the getFollowedPersonIdsMapper to set
     */
    public void setGetFollowedPersonIdsMapper(final GetFollowedPersonIds inGetFollowedPersonIdsMapper)
    {
        this.getFollowedPersonIdsMapper = inGetFollowedPersonIdsMapper;
    }

    /**
     * @param inGetFollowedGroupIdsMapper
     *            the getFollowedGroupIdsMapper to set
     */
    public void setGetFollowedGroupIdsMapper(final DomainMapper<Long, List<Long>> inGetFollowedGroupIdsMapper)
    {
        this.getFollowedGroupIdsMapper = inGetFollowedGroupIdsMapper;
    }

    /**
     * @param inGetPeopleByAccountIdsMapper
     *            the getPeopleByAccountIdsMapper to set
     */
    public void setGetPeopleByAccountIdsMapper(final GetPeopleByAccountIds inGetPeopleByAccountIdsMapper)
    {
        this.getPeopleByAccountIdsMapper = inGetPeopleByAccountIdsMapper;
    }

}
