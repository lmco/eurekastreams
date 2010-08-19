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

import java.util.List;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.Comment;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.cachedfieldpopulators.CommentDTOPopulator;
import org.eurekastreams.server.persistence.mappers.requests.InsertActivityCommentRequest;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.hibernate.Session;
import org.hibernate.search.Search;

/**
 * Mapper for inserting a new comment for an activity.
 * 
 */
public class InsertActivityComment extends BaseArgCachedDomainMapper<InsertActivityCommentRequest, CommentDTO>
{
    /**
     * The CommentDTOPopulator instance.
     */
    private CommentDTOPopulator commentDTOPopulator;

    /**
     * Constructor.
     * 
     * @param inCommentDTOPopulator
     *            The CommentDTOPopulator.
     */
    public InsertActivityComment(final CommentDTOPopulator inCommentDTOPopulator)
    {
        commentDTOPopulator = inCommentDTOPopulator;
    }

    /**
     * Inserts the comment for an activity.
     * 
     * @param inRequest
     *            The request object for inserting a comment.
     * @return The commentDTO object representing the inserted comment.
     */
    @SuppressWarnings("unchecked")
    @Override
    public CommentDTO execute(final InsertActivityCommentRequest inRequest)
    {
        final Activity activity = (Activity) getHibernateSession().load(Activity.class, inRequest.getActivityId());
        
        // create comment and persist to DB.
        Comment comment = new Comment((Person) getHibernateSession().load(Person.class, inRequest.getUserId()), 
                activity, 
                inRequest.getContent().trim());
        
        getEntityManager().persist(comment);
        
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setActivityId(inRequest.getActivityId());
        commentDTO.setAuthorId(inRequest.getUserId());
        commentDTO.setId(comment.getId());
        commentDTO.setTimeSent(comment.getTimeSent());
        commentDTO.setBody(inRequest.getContent());
        
        //fully popluate and cache the commentDTO
        commentDTOPopulator.execute(commentDTO, getCache());
        
        //if present update activityDTO in cache.
        String activityDTOKey = CacheKeys.ACTIVITY_BY_ID + inRequest.getActivityId();
        ActivityDTO activityDTO = (ActivityDTO) getCache().get(activityDTOKey);
        if (activityDTO != null)
        {
            if (activityDTO.getCommentCount() == 0)
            {
                activityDTO.setFirstComment(commentDTO);
            }
            else
            {
                activityDTO.setLastComment(commentDTO);
            }
            activityDTO.setCommentCount(activityDTO.getCommentCount() + 1);
            getCache().set(activityDTOKey, activityDTO);
        }
        
        //if present, update commentId list for activity in cache.
        String commentIdListKey = CacheKeys.COMMENT_IDS_BY_ACTIVITY_ID + inRequest.getActivityId();
        List<Long> commentIds = getCache().getList(commentIdListKey);
        if (commentIds != null)
        {
            commentIds.add(commentDTO.getId());
            getCache().setList(commentIdListKey, commentIds);  
        }
        
        commentDTO.setDeletable(true);

        Search.getFullTextSession((Session) getEntityManager().getDelegate()).index(activity);
        
        return commentDTO;
    }
}
