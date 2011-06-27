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

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.dto.DisplayInfoSettable;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetItemsByPointerIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Populate transient data for DisplayInfoSettable.
 */
public class DisplayInfoSettableDataPopulator
{
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
     * @param inGetPersonModelViewsByAccountIdsMapper
     *            Mapper to get a list of PersonModelViews from a list of AccountIds.
     * @param inGetGroupModelViewsByShortNameMapper
     *            Mapper to get a list of GroupModelViews from a list of group shortNames.
     */
    public DisplayInfoSettableDataPopulator(
            final DomainMapper<List<String>, List<PersonModelView>> inGetPersonModelViewsByAccountIdsMapper,
            final GetItemsByPointerIds<DomainGroupModelView> inGetGroupModelViewsByShortNameMapper)
    {
        getPersonModelViewsByAccountIdsMapper = inGetPersonModelViewsByAccountIdsMapper;
        getGroupModelViewsByShortNameMapper = inGetGroupModelViewsByShortNameMapper;
    }

    /**
     * Populate transient data in DisplaySettables.
     * 
     * @param inCurrentUserId
     *            Current user id.
     * @param inDisplaySettables
     *            the DTOs to update display settings for
     * @return Populated DisplayInfoSettable.
     */
    public List<DisplayInfoSettable> execute(final long inCurrentUserId,
            final List<DisplayInfoSettable> inDisplaySettables)
    {
        // sort by entity type to optimize calls to cache.
        List<String> personAccountIds = new ArrayList<String>();
        List<String> groupShortNames = new ArrayList<String>();

        String uniqueKey;
        for (DisplayInfoSettable ds : inDisplaySettables)
        {
            uniqueKey = ds.getStreamUniqueKey();
            switch (ds.getEntityType())
            {
            case PERSON:
                if (!personAccountIds.contains(uniqueKey))
                {
                    personAccountIds.add(uniqueKey);
                }
                break;
            case GROUP:
                if (!groupShortNames.contains(uniqueKey))
                {
                    groupShortNames.add(uniqueKey);
                }
                break;
            default:
                break;
            }
        }

        // get group/person dtos from cache/db
        List<PersonModelView> personDTOs = getPersonModelViewsByAccountIdsMapper.execute(personAccountIds);
        List<DomainGroupModelView> groupDTOs = getGroupModelViewsByShortNameMapper.execute(groupShortNames);

        // map the people and groups by short names
        Map<String, PersonModelView> peopleByAccountIdsMap = new HashMap<String, PersonModelView>();
        Map<String, DomainGroupModelView> domainGroupsByShortNamesMap = new HashMap<String, DomainGroupModelView>();

        for (PersonModelView person : personDTOs)
        {
            peopleByAccountIdsMap.put(person.getAccountId(), person);
        }
        for (DomainGroupModelView domainGroup : groupDTOs)
        {
            domainGroupsByShortNamesMap.put(domainGroup.getShortName(), domainGroup);
        }

        // set transient data in DTOs for group and people.
        PersonModelView pmv;
        DomainGroupModelView gmv;

        // there could be duplicates in the input list, so loop across the input list
        for (DisplayInfoSettable ds : inDisplaySettables)
        {
            if (ds.getEntityType() == EntityType.PERSON)
            {
                pmv = peopleByAccountIdsMap.get(ds.getStreamUniqueKey());
                if (pmv != null)
                {
                    ds.setAvatarId(pmv.getAvatarId());
                    ds.setDisplayName(pmv.getDisplayName());
                }
            }
            else if (ds.getEntityType() == EntityType.GROUP)
            {
                gmv = domainGroupsByShortNamesMap.get(ds.getStreamUniqueKey());
                if (gmv != null)
                {
                    ds.setAvatarId(gmv.getAvatarId());
                    ds.setDisplayName(gmv.getDisplayName());
                }
            }
        }

        return inDisplaySettables;
    }
}
