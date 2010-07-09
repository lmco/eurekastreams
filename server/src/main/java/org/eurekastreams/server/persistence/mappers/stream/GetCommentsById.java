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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.hibernate.ModelViewResultTransformer;
import org.eurekastreams.server.domain.stream.Comment;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.cachedfieldpopulators.CommentDTOPopulator;
import org.eurekastreams.server.search.factories.CommentDTOFactory;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.hibernate.Criteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO object that returns sorted list of fully populated CommentDTO objects associated with the
 * id list passed in. Returned list is sorted by Comment id.
 *
 */
public class GetCommentsById extends CachedDomainMapper implements DomainMapper<List<Long>, List<CommentDTO>>
{
    /**
     * ModelViewResultsTransformer for CommentDTOs.
     */
    private ModelViewResultTransformer<CommentDTO> resultTransformer =
        new ModelViewResultTransformer<CommentDTO>(new CommentDTOFactory());
    
    /**
     * Comparator for sorting Comments.
     */
    private CommentDTOComparator comparator = new CommentDTOComparator();
    
    /**
     * The CommentDTOPopulator instance.
     */
    private CommentDTOPopulator commentDTOPopulator;    
    
    /**
     * Constructor.
     * @param inCommentDTOPopulator The CommentDTOPopulator instance.
     */
    public GetCommentsById(final CommentDTOPopulator inCommentDTOPopulator)
    {
        commentDTOPopulator = inCommentDTOPopulator;
    }

    /**
     * Returns sorted list of fully populated CommentDTO objects associated with the
     * id list passed in. Returned list is sorted DESC. by Comment id.
     * @param inCommentIds the list of comment ids.
     * @return Sorted list of fully populated CommentDTO objects associated with the
     * id list passed in. Returned list is sorted ascending by Comment id.
     */
    @Override
    public List<CommentDTO> execute(final List<Long> inCommentIds)
    {
        // short-circuit if null or empty list of ids.
        if (inCommentIds == null || inCommentIds.size() == 0)
        {
            return new ArrayList<CommentDTO>(0);
        }        
        
        // Finds comments in the cache.
        Map<String, CommentDTO> cachedComments = getCachedComments(inCommentIds);        
        
        //Get list of commentDTOs from DB.
        List<CommentDTO> commentDTOs = 
            getCommentDTOsFromDataSource(getUncachedIds(inCommentIds, cachedComments));
        
        //Fully populate and cache the new DTOs
        commentDTOPopulator.execute(commentDTOs, getCache());    
                
        //Add previously cached DTOs to list for full set
        commentDTOs.addAll(cachedComments.values());
        
        //Sort based on comment id (DESC).
        Collections.sort(commentDTOs, comparator);
                
        Date currentDate = new Date();
        for (CommentDTO currentComment : commentDTOs)
        {
            currentComment.setServerDateTime(currentDate);
        }
        return commentDTOs;
    }

    /**
     * Queries DB to get CommentDTOs associated with ids passed in.
     * @param unCachedIds Ids for DTOs to be returned.
     * @return List of CommentDTOs associated with ids passed in.
     */
    @SuppressWarnings("unchecked")
    private List<CommentDTO> getCommentDTOsFromDataSource(final List<Long> unCachedIds)
    {
        List<CommentDTO> results = null;
        
        // One or more of the activities were missing in the cache so go to the database
        if (unCachedIds.size() != 0)
        {            
            Criteria criteria = getHibernateSession().createCriteria(Comment.class);
            ProjectionList fields = Projections.projectionList();
            fields.add(getColumn("id"));
            fields.add(getColumn("body"));
            fields.add(getColumn("timeSent"));
            fields.add(Projections.property("author.id").as("authorId"));
            fields.add(Projections.property("target.id").as("activityId"));
            criteria.setProjection(fields);
            
            criteria.setResultTransformer(resultTransformer);

            criteria.add(Restrictions.in("this.id", unCachedIds));               
            
            results = criteria.list();
        }
        return (results == null) ? new ArrayList<CommentDTO>(0) : results;
    }

    /**
     * Given the full list of CommentIds requested, and a map of CommentDTOs that were present in
     * cache, returns the list of Comment ids that need to be loaded from DB.
     * @param inCommentIds Complete list of Comment ids requested.
     * @param cachedComments Map of CommentDTOs from complete list that were present in cache.
     * @return List of Comment ids that need ot be loaded from DB.
     */
    private List<Long> getUncachedIds(final List<Long> inCommentIds, final Map<String, CommentDTO> cachedComments)
    {
        List<Long> results = new ArrayList<Long>();
        
        // if not all are found, determine which ones where not and
        if (cachedComments.size() != inCommentIds.size())
        {
            // Determines if any of the activities were missing from the cache
            for (long commentId : inCommentIds)
            {
                if (!cachedComments.containsKey(CacheKeys.COMMENT_BY_ID + commentId))
                {
                    results.add(commentId);
                }
            }
        }        
        return results;
    }

    /**
     * Returns map of CommentDTOs, keyed by cacheKey, from list of Comment ids that were
     * present in the cache.
     * @param inCommentIds Full list of Comment ids requested.
     * @return Map of CommentDTOs, keyed by cacheKey, from list of Comment ids that were
     * present in the cache.
     */
    @SuppressWarnings("unchecked")
    private Map<String, CommentDTO> getCachedComments(final List<Long> inCommentIds)
    {
        List<String> keys = new ArrayList<String>(inCommentIds.size());
        for (long key : inCommentIds)            
        {
            keys.add(CacheKeys.COMMENT_BY_ID + key);
        }
        return (Map<String, CommentDTO>) (Map<String, ? >) getCache().multiGet(keys);
    }
    
    /**
     * CommentDTO comaparator for sorting the list. NOTE: This comparator is used to 
     * sort the comment list by comment id in ascending manner (most recent first).
     *
     */
    public class CommentDTOComparator implements Comparator<CommentDTO>
    {
        /**
         * Compares two CommentDTO objects based on id value. This is set up
         * to create a ascending list of commmentDTOs based on id.
         * @param inComment1 first commentDTO.
         * @param inComment2 second commentDTO.
         * @return 1/-1/0 based on if comment1 id is &gt;/&lt;/== comment2 id.
         */
        @Override
        public int compare(final CommentDTO inComment1, final CommentDTO inComment2)
        {
            long id1 = inComment1.getId();
            long id2 = inComment2.getId();
            
            if (id1 > id2)
            {
                return 1;
            }
            else if (id1 < id2)
            {
                return -1;
            }
            else
            {
                return 0;
            }
        }        
    }

}
