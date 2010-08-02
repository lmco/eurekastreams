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
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.UpdateMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.eurekastreams.server.service.actions.strategies.UpdaterStrategy;

/**
 * Removes a single membership criteria.
 */
public class RemoveMembershipCriteriaExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * The system settings update mapper.
     *
     */
    private UpdateMapper<SystemSettings> updateMapper;

    /**
     * The system settings finder mapper.
     *
     */
    private DomainMapper<MapperRequest, SystemSettings> finder;

    /**
     * The strategy used to update the system settings.
     */
    private UpdaterStrategy updater;

    /**
     * Constructor.
     *
     * @param inFinder
     *            The finder mapper.
     * @param inUpdater
     *            The updater.
     * @param inUpdateMapper
     *            The update mapper.
     */
    public RemoveMembershipCriteriaExecution(final DomainMapper<MapperRequest, SystemSettings> inFinder,
            final UpdaterStrategy inUpdater, final UpdateMapper<SystemSettings> inUpdateMapper)
    {
        finder = inFinder;
        updater = inUpdater;
        updateMapper = inUpdateMapper;
    }

    /**
     * This action removes the specified membership criteria from the system settings.
     *
     * @param inActionContext
     *            {@link ActionContext}.
     * @return {@link SystemSettings}.
     */
    @Override
    public SystemSettings execute(final ActionContext inActionContext)
    {
        MembershipCriteria criterion = (MembershipCriteria) inActionContext.getParams();
        log.info("removing criteria " + criterion.getCriteria());

        SystemSettings systemSettings = finder.execute(null);
        List<MembershipCriteria> criteria = systemSettings.getMembershipCriteria();

        for (int i = 0; i < criteria.size(); i++)
        {
            MembershipCriteria c = criteria.get(i);
            log.info("comparing: " + c.getCriteria() + " and " + criterion.getCriteria());
            if (c.getCriteria().equals(criterion.getCriteria()))
            {
                criteria.remove(i);
                log.info("criteria " + c.getCriteria() + " removed.");
                break;
            }
        }

        systemSettings.setMembershipCriteria(criteria);

        updater.setProperties(systemSettings, new HashMap<String, Serializable>());
        updateMapper.execute(null);

        return systemSettings;
    }

}
