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
package org.eurekastreams.server.persistence;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.Task;

/**
 * This class provides the mapper functionality for Task objects.
 */
public class TaskMapper extends DomainEntityMapper<Task>
{
    /**
     * Constructor.
     *
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public TaskMapper(final QueryOptimizer inQueryOptimizer)
    {
        super(inQueryOptimizer);
    }

    /**
     * Get the domain entity name for the generic query operations.
     *
     * @return the domain entity name for the gadget query operations.
     */
    @Override
    protected String getDomainEntityName()
    {
        return "Task";
    }

    /**
     * Find the task by its name and gadget def id.
     *
     * @param name
     *            the task name,
     * @param gadgetDefId
     *            the gadget def id.
     * @return the task.
     */
    @SuppressWarnings("unchecked")
    public Task findByNameAndGadgetDefId(final String name, final Long gadgetDefId)
    {
        Query q = getEntityManager().createQuery("from Task where gadgetDefinition.id = :gadgetDefId and name = :name")
                .setParameter("gadgetDefId", gadgetDefId).setParameter("name", name);

        List results = q.getResultList();

        return (results.size() == 0) ? null : (Task) results.get(0);
    }
}
