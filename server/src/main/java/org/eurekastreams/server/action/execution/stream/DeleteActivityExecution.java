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
package org.eurekastreams.server.action.execution.stream;

import java.util.List;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.DeleteFromSearchIndexRequest;
import org.eurekastreams.server.action.request.stream.DeleteActivityCacheUpdateRequest;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.requests.DeleteActivityRequest;
import org.eurekastreams.server.persistence.mappers.stream.DeleteActivity;
import org.eurekastreams.server.persistence.mappers.stream.GetOrderedCommentIdsByActivityId;
import org.eurekastreams.server.persistence.mappers.stream.GetPersonIdsWithStarredActivity;

/**
 * Delete activity action.
 * 
 */
public class DeleteActivityExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Delete activity DAO.
     */
    private DeleteActivity deleteActivityDAO;

    /**
     * DAO for finding comment ids.
     */
    private GetOrderedCommentIdsByActivityId commentIdsByActivityIdDAO;

    /**
     * DAO for getting person Ids for users that have deleted activity starred.
     */
    private GetPersonIdsWithStarredActivity getPersonIdsWithStarredActivityDAO;

    /**
     * Constructor.
     * 
     * @param inDeleteActivityDAO
     *            The DeleteAction DAO.
     * @param inCommentIdsByActivityIdDAO
     *            commments by activity id DAO.
     * @param inGetPersonIdsWithStarredActivityDAO
     *            persons with starred Activity DAO.
     */
    public DeleteActivityExecution(final DeleteActivity inDeleteActivityDAO,
            final GetOrderedCommentIdsByActivityId inCommentIdsByActivityIdDAO,
            final GetPersonIdsWithStarredActivity inGetPersonIdsWithStarredActivityDAO)
    {
        deleteActivityDAO = inDeleteActivityDAO;
        commentIdsByActivityIdDAO = inCommentIdsByActivityIdDAO;
        getPersonIdsWithStarredActivityDAO = inGetPersonIdsWithStarredActivityDAO;
    }

    /**
     * Deletes the activity specified.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     * @return True if successful.
     */
    @Override
    public Boolean execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        Long activityId = (Long) inActionContext.getActionContext().getParams();
        Long currentUserId = inActionContext.getActionContext().getPrincipal().getId();

        // get all comment ids for this activity before removal from DB.
        List<Long> commentIds = commentIdsByActivityIdDAO.execute(activityId);

        // get people who have this activity starred before removal from DB.
        List<Long> personIdsWithActivityStarred = getPersonIdsWithStarredActivityDAO.execute(activityId);

        // delete activity/comments/stars from DB and update current user's views.
        ActivityDTO activity = deleteActivityDAO.execute(new DeleteActivityRequest(currentUserId, activityId));

        // action has already been deleted or was never present, short-circuit here.
        if (activity == null)
        {
            return Boolean.TRUE;
        }

        // submit request for additional cache updates due to activity deletion.
        inActionContext.getUserActionRequests().add(
                new UserActionRequest("deleteActivityCacheUpdate", null, new DeleteActivityCacheUpdateRequest(activity,
                        commentIds, personIdsWithActivityStarred)));

        // Put an action on the queue to delete the activity from search index.
        inActionContext.getUserActionRequests().add(
                new UserActionRequest("deleteFromSearchIndexAction", null, new DeleteFromSearchIndexRequest(
                        Activity.class, activityId)));

        return Boolean.TRUE;
    }
}
