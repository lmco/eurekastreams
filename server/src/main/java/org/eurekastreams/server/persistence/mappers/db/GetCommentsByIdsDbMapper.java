/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.commons.hibernate.ModelViewResultTransformer;
import org.eurekastreams.server.domain.stream.Comment;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.cachedfieldpopulators.CommentDTOPopulator;
import org.eurekastreams.server.search.factories.CommentDTOFactory;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.hibernate.Criteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * Return list of fully populated CommentDTOs for provided comment ids, respecting order of request ids.
 * 
 */
public class GetCommentsByIdsDbMapper extends BaseArgDomainMapper<List<Long>, List<CommentDTO>>
{
    /**
     * ModelViewResultsTransformer for CommentDTOs.
     */
    private ModelViewResultTransformer<CommentDTO> resultTransformer = new ModelViewResultTransformer<CommentDTO>(
            new CommentDTOFactory());

    /**
     * The CommentDTOPopulator instance.
     */
    private CommentDTOPopulator commentDTOPopulator;

    /**
     * Constructor.
     * 
     * @param inCommentDTOPopulator
     *            The CommentDTOPopulator instance.
     */
    public GetCommentsByIdsDbMapper(final CommentDTOPopulator inCommentDTOPopulator)
    {
        commentDTOPopulator = inCommentDTOPopulator;
    }

    /**
     * Return list of fully populated CommentDTOs for provided comment ids, respecting order of request ids.
     * 
     * @param inRequest
     *            of comment ids.
     * @return List of fully populated CommentDTOs for provided comment ids, respecting order of request ids.
     */
    @Override
    public List<CommentDTO> execute(final List<Long> inRequest)
    {
        if (inRequest == null || inRequest.size() == 0)
        {
            return new ArrayList<CommentDTO>(0);
        }

        Criteria criteria = getHibernateSession().createCriteria(Comment.class);
        ProjectionList fields = Projections.projectionList();
        fields.add(getColumn("id"));
        fields.add(getColumn("body"));
        fields.add(getColumn("timeSent"));
        fields.add(Projections.property("author.id").as("authorId"));
        fields.add(Projections.property("target.id").as("activityId"));
        criteria.setProjection(fields);

        criteria.setResultTransformer(resultTransformer);

        criteria.add(Restrictions.in("this.id", inRequest));

        List<CommentDTO> commentDTOs = criteria.list();

        // Fully populate and cache the new DTOs
        commentDTOPopulator.execute(commentDTOs, null);

        return orderResults(inRequest, commentDTOs);
    }

    /**
     * Order Db results to match requested id order.
     * 
     * @param requestedIds
     *            Ids for DTOs to be returned.
     * @param results
     *            associated with ids passed in.
     * 
     * @return List of CommentDTOs ordered as ids passed in.
     */
    private List<CommentDTO> orderResults(final List<Long> requestedIds, final List<CommentDTO> results)
    {
        HashMap<Long, CommentDTO> commentDTOMap = new HashMap<Long, CommentDTO>();
        ArrayList<CommentDTO> orderedResults = new ArrayList<CommentDTO>();

        // push results into map.
        for (CommentDTO result : results)
        {
            commentDTOMap.put(result.getId(), result);
        }

        // loop old-school style so no dependency on list iterator order.
        int numRequested = requestedIds.size();
        for (int x = 0; x < numRequested; x++)
        {
            CommentDTO temp = commentDTOMap.get(requestedIds.get(x));
            if (temp != null)
            {
                orderedResults.add(temp);
            }
        }

        return orderedResults;
    }

}
