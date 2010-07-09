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
package org.eurekastreams.server.service.restlets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.Request;
import org.restlet.resource.ResourceException;
import org.eurekastreams.server.domain.CompositeEntity;
import org.eurekastreams.server.domain.Task;
import org.eurekastreams.server.persistence.CompositeEntityMapper;

/**
 * The entry resource for completed tasks.
 * 
 */
public class CompositeEntityCompletedTaskResource extends WritableResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(CompositeEntityCompletedTaskResource.class);

    /**
     * the entity short name.
     */
    private String entityShortName;
    /**
     * the task name.
     */
    private String taskName;
    /**
     * The gadget def id.
     */
    private Long gadgetDefId;
    /**
     * the persom mapper.
     */
    private CompositeEntityMapper entityMapper;


    /**
     * Initialize parameters from the request object.
     *            the context of the request
     * @param request
     *            the client's request
     */
    protected void initParams(final Request request)
    {
        entityShortName = (String) request.getAttributes().get("shortName");
        gadgetDefId = Long.decode((String) request.getAttributes().get(
                "gadgetDefId"));
        taskName = (String) request.getAttributes().get("taskName");
        taskName = taskName.replace("%20", " ");
    }

    /**
     * The Entity Mapper. Used by tests.
     * 
     * @param inEntityMapper
     *            mapper.
     */
    public void setEntityMapper(final CompositeEntityMapper inEntityMapper)
    {
        entityMapper = inEntityMapper;
    }


    /**
     * Delete a completed task (Uncomplete a task).
     * 
     * @throws ResourceException
     *             error.
     */
    @Override
    public void removeRepresentations() throws ResourceException
    {

        CompositeEntity myEntity = entityMapper.findByShortName(entityShortName);
        for (int i = myEntity.getCompletedTasks().size() - 1; i >= 0; i--)
        {
            Task task = myEntity.getCompletedTasks().get(i);
            if (task.getName().equals(taskName)
                    && task.getGadgetDefinition().getId() == gadgetDefId)
            {
                myEntity.getCompletedTasks().remove(i);
                break;
            }
        }

        entityMapper.flush();
    }
}
