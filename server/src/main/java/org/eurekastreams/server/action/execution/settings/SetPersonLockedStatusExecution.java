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
package org.eurekastreams.server.action.execution.settings;

import java.io.Serializable;
import java.util.Collections;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.SetPersonLockedStatusRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.db.SetPersonLockedStatus;

/**
 * Action for setting a person's account locked status.
 *
 */
public class SetPersonLockedStatusExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * {@link SetPersonLockedStatus}.
     */
    private final SetPersonLockedStatus setLockedStatusDAO;

    /** Mapper to translate person id. */
    private final DomainMapper<String, Long> personIdMapper;

    /**
     * Constructor.
     *
     * @param inSetLockedStatusDAO
     *            {@link SetPersonLockedStatus}.
     * @param inPersonIdMapper
     *            For mapping accountid to id.
     */
    public SetPersonLockedStatusExecution(final SetPersonLockedStatus inSetLockedStatusDAO,
            final DomainMapper<String, Long> inPersonIdMapper)
    {
        setLockedStatusDAO = inSetLockedStatusDAO;
        personIdMapper = inPersonIdMapper;
    }

    /**
     * Set a person's locked status.
     *
     * @param inActionContext
     *            The action context.
     * @return null.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        SetPersonLockedStatusRequest request = (SetPersonLockedStatusRequest) inActionContext.getActionContext()
                .getParams();
        setLockedStatusDAO.execute(request);

        Long personId = personIdMapper.execute(request.getPersonAccountId());

        // when locking, just kill cache; when unlocking, reload
        if (request.getLockedStatus())
        {
            inActionContext.getUserActionRequests().add(
                    new UserActionRequest("deleteCacheKeysAction", null, (Serializable) Collections
                            .singleton(CacheKeys.PERSON_BY_ID + personId)));
        }
        else
        {
            inActionContext.getUserActionRequests().add(new UserActionRequest("cachePerson", null, personId));
        }

        return null;
    }

}
