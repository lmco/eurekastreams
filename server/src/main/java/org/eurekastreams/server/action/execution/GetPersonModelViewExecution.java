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
package org.eurekastreams.server.action.execution;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.persistence.mappers.GetRecursiveOrgCoordinators;
import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.springframework.security.context.SecurityContextHolder;

/**
 * Strategy to get current user's {@link PersonModelView}.
 *
 */
public class GetPersonModelViewExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper to get the root org.
     */
    private GetRootOrganizationIdAndShortName rootOrgMapper;

    /**
     * The mapper to get back all the coordinators of the root org and below.
     */
    private GetRecursiveOrgCoordinators recursiveOrgMapperDownTree;

    /**
     * The mapper to get back all the coordinators of the root org and below.
     */
    private GetRecursiveOrgCoordinators recursiveOrgMapperUpTree;

    /**
     * Person Mapper used to retrieve person from the cache.
     */
    private GetPeopleByAccountIds peopleMapper = null;

    /**
     * Constructor that sets up the mapper.
     *
     * @param inRecursiveOrgMapperDownTree
     *            recursive org mapper.
     * @param inRecursiveOrgMapperUpTree
     *            recursive org mapper.
     * @param inRootOrgMapper
     *            root org mapper.
     * @param inPeopleMapper
     *            - instance of PersonMapper
     */
    public GetPersonModelViewExecution(final GetRecursiveOrgCoordinators inRecursiveOrgMapperDownTree,
            final GetRecursiveOrgCoordinators inRecursiveOrgMapperUpTree,
            final GetRootOrganizationIdAndShortName inRootOrgMapper, final GetPeopleByAccountIds inPeopleMapper)
    {
        rootOrgMapper = inRootOrgMapper;
        recursiveOrgMapperDownTree = inRecursiveOrgMapperDownTree;
        recursiveOrgMapperUpTree = inRecursiveOrgMapperUpTree;
        peopleMapper = inPeopleMapper;
    }

    /**
     * Get current user's {@link PersonModelView}. This includes setting the ToSAcceptance and authentication type
     * properties.
     *
     * @param inActionContext action context.
     * @return {@link PersonModelView}.
     */
    @Override
    public PersonModelView execute(final PrincipalActionContext inActionContext)
    {
        PersonModelView person = peopleMapper.fetchUniqueResult(inActionContext.getPrincipal().getAccountId());

        // TODO: fill out other roles here as necessary
        if (recursiveOrgMapperDownTree.isOrgCoordinatorRecursively(person.getEntityId(),
                rootOrgMapper.getRootOrganizationId()))
        {
            person.getRoles().add((Role.ORG_COORDINATOR));
        }
        if (recursiveOrgMapperUpTree.isOrgCoordinatorRecursively(person.getEntityId(),
                rootOrgMapper.getRootOrganizationId()))
        {
            person.getRoles().add((Role.ROOT_ORG_COORDINATOR));
        }

        ExtendedUserDetails userDetails = (ExtendedUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        person.setTosAcceptance(userDetails.getToSAcceptance());
        person.setAuthenticationType(userDetails.getAuthenticationType());
        return person;
    }

}
