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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.hibernate.ModelViewResultTransformer;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.strategies.ActivityDeletePropertyStrategy;
import org.eurekastreams.server.persistence.strategies.CommentDeletePropertyStrategy;
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
public class BulkActivitiesMapper extends CachedDomainMapper
{
    /**
     * Logger.
     */
    private static Log log = LogFactory.make();

    /**
     * Mapper to get starred activities.
     */
    private GetStarredActivityIds starredActivitiesMapper;

    /**
     * Mapper to get stream info.
     */
    private GetStreamsByIds streamMapper;

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
     * Strategy used to set deletable property comments included in the activityDTO.
     */
    private CommentDeletePropertyStrategy commentDeletePropertySetter;

    /**
     * Strategy used to set deletable property of an activityDTO.
     */
    private ActivityDeletePropertyStrategy activityDeletePropertySetter;

    // TODO: Mappers should be constructor args vs properties as they are not optional.
    /**
     * @return the starredActivitiesMapper
     */
    public GetStarredActivityIds getStarredActivitiesMapper()
    {
        return starredActivitiesMapper;
    }

    /**
     * @param inStarredActivitiesMapper
     *            the starredActivitiesMapper to set
     */
    public void setStarredActivitiesMapper(final GetStarredActivityIds inStarredActivitiesMapper)
    {
        starredActivitiesMapper = inStarredActivitiesMapper;
    }

    /**
     * @param inStreamMapper
     *            the streamMapper to set
     */
    public void setStreamMapper(final GetStreamsByIds inStreamMapper)
    {
        streamMapper = inStreamMapper;
    }

    /**
     * @return the streamMapper
     */
    public GetStreamsByIds getStreamMapper()
    {
        return streamMapper;
    }

    /**
     * @param inPeopleMapper
     *            the peopleMapper to set
     */
    public void setPeopleMapper(final GetPeopleByAccountIds inPeopleMapper)
    {
        peopleMapper = inPeopleMapper;
    }

    /**
     * @param inGroupMapper
     *            the groupMapper to set
     */
    public void setGroupMapper(final GetDomainGroupsByShortNames inGroupMapper)
    {
        groupMapper = inGroupMapper;
    }

    /**
     * @return the peopleMapper
     */
    public GetPeopleByAccountIds getPeopleMapper()
    {
        return peopleMapper;
    }

    /**
     * Looks in cache for the necessary activity DTOs and returns them if found. Otherwise, makes a database call, puts
     * them in cache, and returns them.
     * 
     * @param activityIds
     *            the list of ids that should be found.
     * @param userName
     *            user that requested the activities. This is used to determine if this user has "starred" any items
     *            being returned. Can be set to null if starring is not needed.
     * @return list of ActivityDTO objects.
     */
    @SuppressWarnings("unchecked")
    public List<ActivityDTO> execute(final List<Long> activityIds, final String userName)
    { // Checks to see if there's any real work to do
        if (activityIds == null || activityIds.size() == 0)
        {
            log.info("No activities passed in - returning empty list of Activities");
            return new ArrayList<ActivityDTO>();
        }

        log.info("Looking for activitys with ids: " + activityIds.toString());

        List<String> stringKeys = new ArrayList<String>();
        for (long key : activityIds)
        {
            stringKeys.add(CacheKeys.ACTIVITY_BY_ID + key);
        } // Finds activities in the cache.

        Map<String, ActivityDTO> activities = (Map<String, ActivityDTO>) (Map<String, ? >) getCache().multiGet(
                stringKeys); // Determines if any of the activities were missing from the cache
        List<Long> uncachedActivityKeys = new ArrayList<Long>();
        for (long activityKey : activityIds)
        {
            if (!activities.containsKey(CacheKeys.ACTIVITY_BY_ID + activityKey))
            {
                uncachedActivityKeys.add(activityKey);
            }
        }
        if (uncachedActivityKeys.size() != 0)
        { // One or more of the activities were missing in the cache so go to the database
            log.info("Looking for uncached activitys with ids: " + uncachedActivityKeys.toString());

            Map<String, ActivityDTO> activityMap = new HashMap<String, ActivityDTO>();
            Criteria criteria = getHibernateSession().createCriteria(Activity.class);
            ProjectionList fields = Projections.projectionList();
            fields.add(getColumn("id"));
            fields.add(getColumn("verb"));
            fields.add(getColumn("baseObjectType"));
            fields.add(Projections.property("baseObject").as("baseObjectProperties"));
            fields.add(Projections.property("recipientStreamScope.id").as("destinationStreamId"));
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
            criteria.setProjection(fields);
            criteria.add(Restrictions.in("this.id", uncachedActivityKeys));

            ModelViewResultTransformer<ActivityDTO> resultTransformer = new ModelViewResultTransformer<ActivityDTO>(
                    new ActivityDTOFactory());
            criteria.setResultTransformer(resultTransformer);
            List<ActivityDTO> results = criteria.list();
            for (ActivityDTO activity : results)
            {
                // fills in data from cached view of stream
                List<Long> streamIds = new ArrayList<Long>();
                streamIds.add(activity.getDestinationStream().getId());
                List<StreamScope> streams = streamMapper.execute(streamIds);
                if (streams.size() > 0)
                {
                    activity.getDestinationStream().setDisplayName(streams.get(0).getDisplayName());
                    activity.getDestinationStream().setUniqueIdentifier(streams.get(0).getUniqueKey());
                    activity.getDestinationStream().setDestinationEntityId(streams.get(0).getDestinationEntityId());

                    if (streams.get(0).getScopeType() == ScopeType.PERSON)
                    {
                        activity.getDestinationStream().setType(EntityType.PERSON);
                    }
                    else if (streams.get(0).getScopeType() == ScopeType.GROUP)
                    {
                        activity.getDestinationStream().setType(EntityType.GROUP);
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
                loadCommentInfo(userName, activity); // set the first/last comment and comment count.
                activityMap.put(CacheKeys.ACTIVITY_BY_ID + activity.getId(), activity);
            }
            for (String key : activityMap.keySet())
            {
                getCache().set(key, activityMap.get(key));
            }
            activities.putAll(activityMap);
        }
        List<ActivityDTO> results = new ArrayList<ActivityDTO>();

        if (activities.size() != 0) // gets starred activities
        {
            List<String> thisUser = new ArrayList<String>();
            thisUser.add(userName);
            List<PersonModelView> thisPerson = peopleMapper.execute(thisUser);
            List<Long> starred = new ArrayList();
            if (thisPerson.size() > 0)
            {
                starred = starredActivitiesMapper.execute(thisPerson.get(0).getEntityId());
            }

            // Puts the activities in the same order as they were passed in.
            Date currentServerDate = new Date();
            for (long id : activityIds)
            {
                ActivityDTO activity = activities.get(CacheKeys.ACTIVITY_BY_ID + id);
                if (activity != null)
                {
                    activity.setStarred(starred.contains(activity.getId()));
                    activity.setServerDateTime(currentServerDate);
                    if (activity.getFirstComment() != null)
                    {
                        activity.getFirstComment().setServerDateTime(currentServerDate);
                    }
                    if (activity.getLastComment() != null)
                    {
                        activity.getLastComment().setServerDateTime(currentServerDate);
                    }

                    activityDeletePropertySetter.execute(userName, activity);

                    setCommentDeletable(userName, activity);
                    results.add(activity);
                }
            }
        }

        return results;
    }

    /**
     * Looks in cache for the necessary activity DTO and returns it if found. Otherwise, makes a database call, puts
     * them in cache, and returns it.
     * 
     * @param activityId
     *            id that should be found.
     * @param userName
     *            user that requested the activities. This is used to determine if this user has "starred" any items
     *            being returned. Can be set to null if starring is not needed.
     * @return ActivityDTO object.
     */
    public ActivityDTO execute(final Long activityId, final String userName)
    {
        return execute(Collections.singletonList(activityId), userName).get(0);
    }

    /**
     * Load the first/last comments of an activity if present, also sets the comment count.
     * 
     * @param userName
     *            Username of current user.
     * @param activity
     *            ActivityDTO to load comment info for.
     */
    private void loadCommentInfo(final String userName, final ActivityDTO activity)
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

    /**
     * If activity has comments, determine if current user can delete them and set deletable property accordingly.
     * 
     * @param userName
     *            The current user's username.
     * @param activity
     *            The activity to examine for comments to set.
     */
    private void setCommentDeletable(final String userName, final ActivityDTO activity)
    {
        if (activity.getFirstComment() == null)
        {
            return;
        }
        List<CommentDTO> comments = new ArrayList<CommentDTO>(2);
        comments.add(activity.getFirstComment());
        if (activity.getLastComment() != null)
        {
            comments.add(activity.getLastComment());
        }

        commentDeletePropertySetter.execute(userName, activity, comments);
    }

    /**
     * @return the commentIdListDAO
     */
    public GetOrderedCommentIdsByActivityId getCommentIdListDAO()
    {
        return commentIdListDAO;
    }

    /**
     * @param inCommentIdListDAO
     *            the commentIdListDAO to set
     */
    public void setCommentIdListDAO(final GetOrderedCommentIdsByActivityId inCommentIdListDAO)
    {
        this.commentIdListDAO = inCommentIdListDAO;
    }

    /**
     * @return the commentsByIdDAO
     */
    public GetCommentsById getCommentsByIdDAO()
    {
        return commentsByIdDAO;
    }

    /**
     * @param inCommentsByIdDAO
     *            the commentsByIdDAO to set
     */
    public void setCommentsByIdDAO(final GetCommentsById inCommentsByIdDAO)
    {
        this.commentsByIdDAO = inCommentsByIdDAO;
    }

    /**
     * @return the commentDeletePropertySetter
     */
    public CommentDeletePropertyStrategy getCommentDeletePropertySetter()
    {
        return commentDeletePropertySetter;
    }

    /**
     * @param inCommentDeletePropertySetter
     *            the commentDeletePropertySetter to set
     */
    public void setCommentDeletePropertySetter(final CommentDeletePropertyStrategy inCommentDeletePropertySetter)
    {
        this.commentDeletePropertySetter = inCommentDeletePropertySetter;
    }

    /**
     * @return the activityDeletePropertySetter
     */
    public ActivityDeletePropertyStrategy getActivityDeletePropertySetter()
    {
        return activityDeletePropertySetter;
    }

    /**
     * @param inActivityDeletePropertySetter
     *            the activityDeletePropertySetter to set
     */
    public void setActivityDeletePropertySetter(final ActivityDeletePropertyStrategy inActivityDeletePropertySetter)
    {
        this.activityDeletePropertySetter = inActivityDeletePropertySetter;
    }
}
