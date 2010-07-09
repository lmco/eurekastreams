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
package org.eurekastreams.server.service.actions.strategies;

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.persistence.DomainGroupMapper;

/**
 * Retrieves parent organization of group.
 */
public class GroupRecipientRetriever implements RecipientRetriever
{
    /**
     * Group mapper.
     */
    private DomainGroupMapper groupMapper;

    /**
     * Constructor.
     * 
     * @param inGroupMapper
     *            The Group mapper.
     */
    public GroupRecipientRetriever(final DomainGroupMapper inGroupMapper)
    {
        groupMapper = inGroupMapper;
    }

    /**
     * Retrieves parent org for group specified in PostMessageRequest.
     * 
     * @param inActivity
     *            The ActivityDTO object.
     * @return The parent organization of message recipient.
     */
    public Organization getParentOrganization(final ActivityDTO inActivity)
    {
        // TODO This could probably be faster with specialized query.
        return groupMapper.findByShortName(
                inActivity.getDestinationStream().getUniqueIdentifier()).getParentOrganization();
    }

    /**
     * Retrieve streamscope of a group recipient stream.
     * {@inheritDoc}
     */
    public StreamScope getStreamScope(final ActivityDTO inActivityDTO)
    {
        return groupMapper.findByShortName(
                inActivityDTO.getDestinationStream().getUniqueIdentifier()).getStreamScope();
    }

    /**
     * Get whether the destination stream of the input activity is public.
     * 
     * @param inActivityDTO
     *            the activity dto to check
     * @return whether the destination stream of the input activity is public.
     */
    @Override
    public Boolean isDestinationStreamPublic(final ActivityDTO inActivityDTO)
    {
        return groupMapper.findByShortName(
                inActivityDTO.getDestinationStream().getUniqueIdentifier()).isPublicGroup();
    }
}
