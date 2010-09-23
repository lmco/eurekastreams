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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.hibernate.ModelViewResultTransformer;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.factories.ActivityDTOFactory;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.hibernate.Criteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * Gets a list of ActivityDTO objects for a given list of activity ids.
 */
public class BulkActivitiesDbMapper extends BaseArgDomainMapper<List<Long>, List<ActivityDTO>> implements
        DomainMapper<List<Long>, List<ActivityDTO>>
{
    /**
     * Logger.
     */
    private static Log log = LogFactory.make();

    /**
     * Mapper to get person info.
     */
    private GetPeopleByAccountIds peopleMapper;

    /**
     * Group Mapper.
     */
    private GetDomainGroupsByShortNames groupMapper;

    /**
     * DAO for comment id list.
     */
    private GetOrderedCommentIdsByActivityId commentIdListDAO;

    /**
     * DAO for comments by id.
     */
    private GetCommentsById commentsByIdDAO;

    /**
     * @param inPeopleMapper
     *            the people mapper.
     * @param inGroupMapper
     *            the group mapper.
     * @param inCommentIdListDAO
     *            comment ID list DAO.
     * @param inCommentsByIdDAO
     *            comments by ID DAO.
     */
    public BulkActivitiesDbMapper(final GetPeopleByAccountIds inPeopleMapper,
            final GetDomainGroupsByShortNames inGroupMapper, final GetOrderedCommentIdsByActivityId inCommentIdListDAO,
            final GetCommentsById inCommentsByIdDAO)
    {
        peopleMapper = inPeopleMapper;
        groupMapper = inGroupMapper;
        commentIdListDAO = inCommentIdListDAO;
        commentsByIdDAO = inCommentsByIdDAO;
    }

    /**
     * Looks in cache for the necessary activity DTOs and returns them if found. Otherwise, makes a database call, puts
     * them in cache, and returns them.
     *
     * @param activityIds
     *            the list of ids that should be found.
     * @return list of ActivityDTO objects.
     */
    @SuppressWarnings("unchecked")
    public List<ActivityDTO> execute(final List<Long> activityIds)
    {
        Criteria criteria = getHibernateSession().createCriteria(Activity.class);
        ProjectionList fields = Projections.projectionList();
        fields.add(getColumn("id"));
        fields.add(getColumn("verb"));
        fields.add(getColumn("baseObjectType"));
        fields.add(Projections.property("baseObject").as("baseObjectProperties"));
        fields.add(Projections.property("recipStreamScope.destinationEntityId").as("destinationStreamEntityId"));
        fields.add(Projections.property("recipStreamScope.scopeType").as("destinationStreamScopeType"));
        fields.add(Projections.property("recipStreamScope.uniqueKey").as("destinationStreamUniqueKey"));
        fields.add(Projections.property("recipientParentOrg.id").as("recipientParentOrgId"));
        fields.add(getColumn("isDestinationStreamPublic"));
        fields.add(getColumn("actorType"));
        fields.add(getColumn("originalActorType"));
        fields.add(Projections.property("actorId").as("actorUniqueIdentifier"));
        fields.add(Projections.property("originalActorId").as("originalActorUniqueIdentifier"));
        fields.add(getColumn("postedTime"));
        fields.add(getColumn("mood"));
        fields.add(getColumn("location"));
        fields.add(getColumn("annotation"));
        fields.add(getColumn("appId"));
        fields.add(getColumn("appSource"));
        fields.add(getColumn("appName"));
        criteria.createAlias("recipientStreamScope", "recipStreamScope");
        criteria.setProjection(fields);
        criteria.add(Restrictions.in("this.id", activityIds));

        final Map<Long, ActivityDTO> activityMap = new HashMap<Long, ActivityDTO>();

        ModelViewResultTransformer<ActivityDTO> resultTransformer = new ModelViewResultTransformer<ActivityDTO>(
                new ActivityDTOFactory());
        criteria.setResultTransformer(resultTransformer);
        List<ActivityDTO> results = criteria.list();
        for (ActivityDTO activity : results)
        {
            activityMap.put(activity.getId(), activity);

            // fills in data from cached view of stream
            List<Long> streamIds = new ArrayList<Long>();
            streamIds.add(activity.getDestinationStream().getId());

            // get the display name for the destination stream
            if (activity.getDestinationStream().getUniqueIdentifier() != null)
            {
                if (activity.getDestinationStream().getType() == EntityType.PERSON)
                {
                    PersonModelView person = peopleMapper.fetchUniqueResult(activity.getDestinationStream()
                            .getUniqueIdentifier());
                    activity.getDestinationStream().setDisplayName(person.getDisplayName());

                }
                else if (activity.getDestinationStream().getType() == EntityType.GROUP)
                {
                    DomainGroupModelView group = groupMapper.fetchUniqueResult(activity.getDestinationStream()
                            .getUniqueIdentifier());
                    activity.getDestinationStream().setDisplayName(group.getName());
                }
            }

            if (activity.getActor().getType() == EntityType.PERSON)
            {
                List<String> peopleIds = new ArrayList<String>();
                peopleIds.add(activity.getActor().getUniqueIdentifier());
                List<PersonModelView> people = peopleMapper.execute(peopleIds);
                if (people.size() > 0)
                {
                    activity.getActor().setId(people.get(0).getEntityId());
                    activity.getActor().setDisplayName(people.get(0).getDisplayName());
                    activity.getActor().setAvatarId(people.get(0).getAvatarId());
                }
            }
            else if (activity.getActor().getType() == EntityType.GROUP)
            {
                List<String> groupIds = new ArrayList<String>();
                groupIds.add(activity.getActor().getUniqueIdentifier());
                List<DomainGroupModelView> groups = groupMapper.execute(groupIds);
                if (groups.size() > 0)
                {
                    activity.getActor().setId(groups.get(0).getEntityId());
                    activity.getActor().setDisplayName(groups.get(0).getName());
                    activity.getActor().setAvatarId(groups.get(0).getAvatarId());
                }
            }
            // fills in data from cached view of original actor
            if (activity.getOriginalActor().getType() == EntityType.PERSON)
            {
                List<String> peopleIds = new ArrayList<String>();
                peopleIds.add(activity.getOriginalActor().getUniqueIdentifier());
                List<PersonModelView> people = peopleMapper.execute(peopleIds);
                if (people.size() > 0)
                {
                    activity.getOriginalActor().setId(people.get(0).getEntityId());
                    activity.getOriginalActor().setDisplayName(people.get(0).getDisplayName());
                    activity.getOriginalActor().setAvatarId(people.get(0).getAvatarId());
                }
            }

            loadCommentInfo(activity); // set the first/last comment and comment count.
        }

        final List<ActivityDTO> orderedResults = new LinkedList<ActivityDTO>();

        for (int i = 0; i < activityIds.size(); i++)
        {
            if (activityMap.containsKey(activityIds.get(i)))
            {
                orderedResults.add(activityMap.get(activityIds.get(i)));
            }
        }

        return orderedResults;
    }

    /**
     * Load the first/last comments of an activity if present, also sets the comment count.
     *
     * @param activity
     *            ActivityDTO to load comment info for.
     */
    private void loadCommentInfo(final ActivityDTO activity)
    {
        List<Long> commentIds = this.commentIdListDAO.execute(activity.getId());
        int numOfComments = commentIds.size();
        activity.setCommentCount(numOfComments);

        // short circuit if nothing to do.
        if (numOfComments == 0)
        {
            return;
        }

        // get the ids for the first and last comments.
        ArrayList<Long> firstLastCommentIds = new ArrayList<Long>();
        firstLastCommentIds.add(commentIds.get(0));
        if (numOfComments > 1)
        {
            firstLastCommentIds.add(commentIds.get(commentIds.size() - 1));
        }

        // get the commentDTOs.
        List<CommentDTO> firstLastCommentDTOs = commentsByIdDAO.execute(firstLastCommentIds);

        // make sure we got what we asked for.
        if (firstLastCommentDTOs.size() != firstLastCommentIds.size())
        {
            throw new RuntimeException("Error loading first/last comments for Activity: " + activity.getId());
        }

        // set the commentDTOs in the activity appropriately.
        activity.setFirstComment(firstLastCommentDTOs.get(0));
        if (firstLastCommentDTOs.size() > 1)
        {
            activity.setLastComment(firstLastCommentDTOs.get(1));
        }
    }
}
