/*
 * Copyright (c) 2010-2012 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.strategies.CommentDeletePropertyStrategy;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.activity.ActivityFilter;

/**
 * Execution strategy for getting a single activity by id.
 */
public class GetActivityByIdExecutionStrategy implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper to get the actual Activities.
     */
    private final DomainMapper<List<Long>, List<ActivityDTO>> bulkActivitiesMapper;

    /**
     * DAO for finding comment ids.
     */
    private final DomainMapper<Long, List<Long>> commentIdsByActivityIdDAO;

    /**
     * DAO for finding comments by id.
     */
    private final DomainMapper<List<Long>, List<CommentDTO>> commentsByIdDAO;

    /**
     * Strategy for setting Deletable property on CommentDTOs.
     */
    private final CommentDeletePropertyStrategy commentDeletableSetter;

    /**
     * List of filters to apply to action.
     */
    private final List<ActivityFilter> filters;

    /**
     * Mapper to lookup a PersonModelView from an account id.
     */
    private final DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper;

    /**
     * Constructor.
     *
     * @param inBulkActivitiesMapper
     *            Mapper to get the ActivitieDTOs.
     * @param inCommentIdsByActivityIdDAO
     *            DAO for finding comment ids for an activity.
     * @param inCommentsByIdDAO
     *            DAO for finding comments by id.
     * @param inCommentDeletableSetter
     *            Strategy for setting deletable property on comments.
     * @param inGetPersonModelViewByAccountIdMapper
     *            Mapper to lookup a PersonModelView from an account id.
     * @param inFilters
     *            Filters to apply to activity List.
     */
    public GetActivityByIdExecutionStrategy(final DomainMapper<List<Long>, List<ActivityDTO>> inBulkActivitiesMapper,
            final DomainMapper<Long, List<Long>> inCommentIdsByActivityIdDAO,
            final DomainMapper<List<Long>, List<CommentDTO>> inCommentsByIdDAO,
            final CommentDeletePropertyStrategy inCommentDeletableSetter,
            final DomainMapper<String, PersonModelView> inGetPersonModelViewByAccountIdMapper,
            final List<ActivityFilter> inFilters)
    {
        bulkActivitiesMapper = inBulkActivitiesMapper;
        commentIdsByActivityIdDAO = inCommentIdsByActivityIdDAO;
        commentsByIdDAO = inCommentsByIdDAO;
        commentDeletableSetter = inCommentDeletableSetter;
        getPersonModelViewByAccountIdMapper = inGetPersonModelViewByAccountIdMapper;
        filters = inFilters;
    }

    /**
     * Gets a single ActivityDTO for a given activity ID.
     *
     * @param inActionContext
     *            the action context containing the id of the activity to fetch
     * @return the activityDTO or null if no matching activity was found.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext)
    {
        String accountId = inActionContext.getPrincipal().getAccountId();
        Long activityId = (Long) inActionContext.getParams();

        // look up activity based on id passed in.
        List<ActivityDTO> results = new ArrayList<ActivityDTO>(bulkActivitiesMapper.execute(Collections
                .singletonList(activityId)));

        // short-circuit if no results for activity.
        if (results.size() == 0)
        {
            return null;
        }

        // load full comment list into DTO from cache and return ActivityDTO.
        // This is not maintained in DTO to avoid excess memory usage and passing
        // extra data over network when not needed.
        loadAllComments(accountId, results.get(0));

        // execute filter strategies.
        // (Although the interface would allow/imply it, these "filters" don't actually filter out any activities. They
        // alter data on the activities before sending them to the client. Which is why it's ok to filter after fetching
        // comments.)
        PersonModelView person = getPersonModelViewByAccountIdMapper.execute(accountId);
        for (ActivityFilter filter : filters)
        {
            filter.filter(results, person);
        }

        return results.get(0);
    }

    /**
     * Loads all the comments for a given Activity into the DTO.
     *
     * @param inUserAccountId
     *            the accountid of the user making this request.
     * @param inActivityDTO
     *            The ActivityDTO to load comments for.
     * @return ActivityDTO will full comment list populated.
     */
    private ActivityDTO loadAllComments(final String inUserAccountId, final ActivityDTO inActivityDTO)
    {
        // get the comments for activity, set deletable property one them,
        // and set full list to ActivityDTO
        List<CommentDTO> comments = commentsByIdDAO.execute(commentIdsByActivityIdDAO.execute(inActivityDTO.getId()));
        commentDeletableSetter.execute(inUserAccountId, inActivityDTO, comments);
        inActivityDTO.setComments(comments);

        return inActivityDTO;
    }
}
