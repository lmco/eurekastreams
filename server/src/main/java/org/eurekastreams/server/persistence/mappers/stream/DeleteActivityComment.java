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
import java.util.List;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.search.modelview.CommentDTO;

/**
 * Deletes a comment and updates cache appropriately.
 *
 */
public class DeleteActivityComment extends BaseArgCachedDomainMapper<Long, Boolean>
{
    /**
     * Comment DAO.
     */
    private GetCommentsById commentByIdDAO;
    
    /**
     * Constructor.
     * @param inCommentByIdDAO Comment DAO.
     */
    public DeleteActivityComment(final GetCommentsById inCommentByIdDAO)
    {
        commentByIdDAO = inCommentByIdDAO;
    }

    /**
     * Deletes a comment and updates cache appropriately.
     * @param inCommentId Id of comment to delete.
     * @return true if successful.
     */
    @SuppressWarnings({ "unchecked" })
    @Override
    public Boolean execute(final Long inCommentId)
    {
        //get comment to delete (need this to get activity that comment is associated with).
        CommentDTO comment = getCommentById(inCommentId);        
        
        //short circuit here if comment to delete is not present.
        if (comment == null)
        {
            return true;
        }
        
        long activityId = comment.getActivityId();
        
        //delete comment from DB.
        getEntityManager().createQuery("DELETE FROM Comment WHERE id = :commentId")
            .setParameter("commentId", inCommentId).executeUpdate();
        
        Cache cache = getCache();
        
        //delete commentDTO from cache.
        cache.delete(CacheKeys.COMMENT_BY_ID + inCommentId);
        
        //if present, update commentId list for activity in cache.        
        String commentsByActivityKey = CacheKeys.COMMENT_IDS_BY_ACTIVITY_ID + activityId;
        cache.removeFromList(commentsByActivityKey, inCommentId);               
        
        //if present, update ActivityDTO in cache
        String activityByIdKey = CacheKeys.ACTIVITY_BY_ID + activityId;
        ActivityDTO activity = (ActivityDTO) cache.get(activityByIdKey);
        if (activity != null)
        {      
            List<Long> commentIds = cache.getList(commentsByActivityKey);
            updateActivityDTO(activity, inCommentId, commentIds);
            getCache().set(activityByIdKey, activity);
        }
        
        return true;
    }
    
    /**
     * Updates ActivityDTO first/last comment if needed.
     * @param inActivity ActivityDTO to update.
     * @param inDeletedCommentId Id of comment that was deleted.
     * @param inRemainingCommentIds Ordered list of remaining comments.
     */
    private void updateActivityDTO(final ActivityDTO inActivity, 
            final Long inDeletedCommentId, final List<Long> inRemainingCommentIds)
    {
        //set the commentCount.
        inActivity.setCommentCount(inRemainingCommentIds.size());
        
        //if deleted last comment, null out first and last comments in activity.
        if (inRemainingCommentIds.size() == 0)
        {            
            inActivity.setFirstComment(null); 
            inActivity.setLastComment(null);              
        }
        //more comments remain after deletion, adjust first/last as needed.
        else
        {                       
            // set new first commentDTO
            if (inActivity.getFirstComment() == null 
                || inActivity.getFirstComment().getId() != inRemainingCommentIds.get(0))
            {
                inActivity.setFirstComment(getCommentById(inRemainingCommentIds.get(0)));
            }

            // if only one comment remains last comment should be null.
            if (inRemainingCommentIds.size() == 1)
            {
                inActivity.setLastComment(null);
            }
            //last should be non null, see if we need to update.
            else if (inActivity.getLastComment() == null 
                || inActivity.getLastComment().getId() != inRemainingCommentIds.get(inRemainingCommentIds.size() - 1))
            {
                // set the new last comment.
                inActivity.setLastComment(getCommentById(inRemainingCommentIds.get(inRemainingCommentIds.size() - 1)));
            }            
        }        
    }
    
    /**
     * Returns CommentDTO for a given id, or null if not present.
     * @param inCommentId The comment id.
     * @return CommentDTO for a given id, or null if not present.
     */
    @SuppressWarnings("serial")
    private CommentDTO getCommentById(final Long inCommentId)
    {
        List<CommentDTO> comments = commentByIdDAO.execute(new ArrayList<Long>() { { add(inCommentId); } });        
        return comments.size() == 0 ? null : comments.get(0);
    }
    

}
