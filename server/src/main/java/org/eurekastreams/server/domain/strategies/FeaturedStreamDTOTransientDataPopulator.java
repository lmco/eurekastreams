/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.domain.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetItemsByPointerIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Populate transient data in {@link FeaturedStreamDTO}s.
 * 
 */
public class FeaturedStreamDTOTransientDataPopulator
{
    /**
     * {@link FollowerStatusPopulator}.
     */
    private FollowerStatusPopulator<FeaturedStreamDTO> followerStatusPopulator;

    /**
     * Mapper to get a list of PersonModelViews from a list of AccountIds.
     */
    private DomainMapper<List<String>, List<PersonModelView>> getPersonModelViewsByAccountIdsMapper;

    /**
     * Mapper to get a list of DomainGroupModelViews from a list of group short names.
     */
    private GetItemsByPointerIds<DomainGroupModelView> getGroupModelViewsByShortNameMapper;

    /**
     * Constructor.
     * 
     * @param inFollowerStatusPopulator
     *            {@link FollowerStatusPopulator}.
     * @param inGetPersonModelViewsByAccountIdsMapper
     *            Mapper to get a list of PersonModelViews from a list of AccountIds.
     * @param inGetGroupModelViewsByShortNameMapper
     *            Mapper to get a list of GroupModelViews from a list of group shortNames.
     */
    public FeaturedStreamDTOTransientDataPopulator(
            final FollowerStatusPopulator<FeaturedStreamDTO> inFollowerStatusPopulator,
            final DomainMapper<List<String>, List<PersonModelView>> inGetPersonModelViewsByAccountIdsMapper,
            final GetItemsByPointerIds<DomainGroupModelView> inGetGroupModelViewsByShortNameMapper)
    {
        followerStatusPopulator = inFollowerStatusPopulator;
        getPersonModelViewsByAccountIdsMapper = inGetPersonModelViewsByAccountIdsMapper;
        getGroupModelViewsByShortNameMapper = inGetGroupModelViewsByShortNameMapper;
    }

    /**
     * Populate transient data in {@link FeaturedStreamDTO}s.
     * 
     * @param inCurrentUserId
     *            Current user id.
     * @param inFeaturedStreamDTOs
     *            {@link FeaturedStreamDTO} to populate.
     * @return Populated {@link FeaturedStreamDTO}.
     */
    public List<FeaturedStreamDTO> execute(final long inCurrentUserId,
            final List<FeaturedStreamDTO> inFeaturedStreamDTOs)
    {
        // Set follower status on all dtos.
        followerStatusPopulator.execute(inCurrentUserId, inFeaturedStreamDTOs, FollowerStatus.NOTFOLLOWING);

        // sort by entity type to optimize calls to cache.
        Map<String, FeaturedStreamDTO> personStreams = new HashMap<String, FeaturedStreamDTO>();
        Map<String, FeaturedStreamDTO> groupStreams = new HashMap<String, FeaturedStreamDTO>();

        for (FeaturedStreamDTO fs : inFeaturedStreamDTOs)
        {
            switch (fs.getEntityType())
            {
            case PERSON:
                personStreams.put(fs.getStreamUniqueKey(), fs);
                break;
            case GROUP:
                groupStreams.put(fs.getStreamUniqueKey(), fs);
                break;
            default:
                break;
            }
        }

        // get group/person dtos from cache/db
        List<PersonModelView> personDTOs = getPersonModelViewsByAccountIdsMapper.execute(new ArrayList<String>(
                personStreams.keySet()));
        List<DomainGroupModelView> groupDTOs = getGroupModelViewsByShortNameMapper.execute(new ArrayList<String>(
                groupStreams.keySet()));

        // set transient data in DTOs for group and people.
        FeaturedStreamDTO tempStream;
        for (PersonModelView pmv : personDTOs)
        {
            tempStream = personStreams.get(pmv.getAccountId());
            tempStream.setAvatarId(pmv.getAvatarId());
            tempStream.setDisplayName(pmv.getDisplayName());
        }

        for (DomainGroupModelView dgmv : groupDTOs)
        {
            tempStream = groupStreams.get(dgmv.getShortName());
            tempStream.setAvatarId(dgmv.getAvatarId());
            tempStream.setDisplayName(dgmv.getDisplayName());
        }

        return inFeaturedStreamDTOs;
    }
}
