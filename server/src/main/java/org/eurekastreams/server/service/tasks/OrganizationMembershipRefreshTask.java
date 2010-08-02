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
package org.eurekastreams.server.service.tasks;

import java.util.List;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverser;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverserBuilder;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.eurekastreams.server.service.actions.strategies.OrganizationPopulator;

/**
 * The OrganizationMembershipFreshTask class is responsible for re-assessing the membership criteria of an organization
 * and either add members, remove members, or leave members in place.
 *
 * The logic of the refreshMembership method is a pre-order tree traversal starting at the root organization. Since the
 * root organization is a container (has no membership criteria), no populate is called and the child organizations are
 * retrieved.
 *
 * As the current implemented logic is in the OrganizationPopulator exiting people in the system are moved from their
 * existing organization into the organization including int the call to populate; this logic will change in the future.
 *
 */

public class OrganizationMembershipRefreshTask
{

    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(OrganizationMembershipRefreshTask.class);

    /**
     * OrganizationMapper object.
     */
    private OrganizationMapper organizationMapper;

    /**
     * PersonMapper object.
     */
    private PersonMapper personMapper;

    /**
     * OrganizationPopulator object.
     */
    private OrganizationPopulator organizationPopulator;

    /**
     * Organization hierarchy traverser builder object.
     */
    private OrganizationHierarchyTraverserBuilder organizationTraverserBuilder;

    /**
     * The root organization.
     */
    private Organization rootOrganization;

    /**
     * Local instance of a stopwatch for tracking the duration of the job.
     */
    private StopWatch stopWatch;

    /**
     * The settings mapper.
     */
    private DomainMapper<MapperRequest, SystemSettings> settingsMapper;

    /**
     * No parameter constructor to make Spring happy.
     */
    public OrganizationMembershipRefreshTask()
    {
        // no-op
    }

    /**
     * Constructor for the class.
     *
     * @param inOrganizationMapper
     *            The organization mapper.
     * @param inOrganizationPopulator
     *            The organization populator.
     * @param inPersonMapper
     *            The person mapper.
     * @param inOrganizationTraverserBuilder
     *            The organization traverser builder.
     * @param inSettingsMapper
     *            the settings mapper.
     */
    public OrganizationMembershipRefreshTask(final OrganizationMapper inOrganizationMapper,
            final OrganizationPopulator inOrganizationPopulator, final PersonMapper inPersonMapper,
            final OrganizationHierarchyTraverserBuilder inOrganizationTraverserBuilder,
            final DomainMapper<MapperRequest, SystemSettings> inSettingsMapper)
    {
        this.organizationMapper = inOrganizationMapper;
        this.organizationPopulator = inOrganizationPopulator;
        this.personMapper = inPersonMapper;
        this.organizationTraverserBuilder = inOrganizationTraverserBuilder;
        this.settingsMapper = inSettingsMapper;

        rootOrganization = organizationMapper.getRootOrganization();

        stopWatch = new StopWatch();
    }

    /**
     * The main method called from <code>execute</code>. This is a recursive method performing a preorder tree traversal
     * on the organization structure.
     *
     * @throws Exception
     *             The exception
     */
    private void refreshMembership() throws Exception
    {
        List<MembershipCriteria> membershipCriteria = settingsMapper.execute(null).getMembershipCriteria();

        for (MembershipCriteria criterion : membershipCriteria)
        {
            organizationPopulator.populate(criterion.getCriteria(), rootOrganization, null);
        }
    }

    /**
     * The main method of the class, this method gets executed to satisfy the job.
     *
     * The method will obtain the root organization and loop on the child organizations For each child organization, the
     * method will be invoked
     *
     * @throws Exception
     *             The exception
     *
     */
    public void execute() throws Exception
    {
        stopWatch.start();

        // Create the organization hierarchy traverser from the builder
        OrganizationHierarchyTraverser organizationTraverser = organizationTraverserBuilder
                .getOrganizationHierarchyTraverser();

        // Purge all related organizations from people to start membership
        // re-assignment
        personMapper.purgeRelatedOrganizations();

        // Recurse through all of the organization and assign membership and
        // build back up the related organizations list
        refreshMembership();

        // Pull a list of people who are assigned to an organization but lost
        // membership to that organization
        // and for each person check their parent to see if they have
        // membership. Assign them to that parent if they
        // do have membership. Stop if you reach the Root Organization and
        // assign the person to the Root organization.
        for (Person person : personMapper.findOrphanedPeople())
        {
            person.setAccountLocked(true);
        }

        // Update the statistics for all affected groups
        organizationMapper.updateOrganizationStatistics(organizationTraverser);

        personMapper.flush();

        stopWatch.stop();
        log.info("Membership Refresh Job: elapsed time: " + DurationFormatUtils.formatDurationHMS(stopWatch.getTime()));
    }
}
