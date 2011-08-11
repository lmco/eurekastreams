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
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Populate transient data for DisplayInfoSettable.
 */
public class DisplayInfoSettableDataPopulator implements
        DomainMapper<List<DisplayInfoSettable>, List<DisplayInfoSettable>>
{
    /**
     * Mapper to get a list of PersonModelViews from a list of AccountIds.
     */
    private final DomainMapper<List<Long>, List<PersonModelView>> getPersonModelViewsByIdsMapper;

    /**
     * Mapper to get a list of DomainGroupModelViews from a list of group short names.
     */
    private final DomainMapper<List<Long>, List<DomainGroupModelView>> getGroupModelViewsByIdsMapper;

    /**
     * Constructor.
     *
     * @param inGetPersonModelViewsByIdsMapper
     *            Mapper to get a list of PersonModelViews from a list of IDs.
     * @param inGetGroupModelViewsByIdsMapper
     *            Mapper to get a list of GroupModelViews from a list of group IDs.
     */
    public DisplayInfoSettableDataPopulator(
            final DomainMapper<List<Long>, List<PersonModelView>> inGetPersonModelViewsByIdsMapper,
            final DomainMapper<List<Long>, List<DomainGroupModelView>> inGetGroupModelViewsByIdsMapper)
    {
        getPersonModelViewsByIdsMapper = inGetPersonModelViewsByIdsMapper;
        getGroupModelViewsByIdsMapper = inGetGroupModelViewsByIdsMapper;
    }

    /**
     * Populate transient data in DisplaySettables.
     *
     * @param inDisplaySettables
     *            the DTOs to update display settings for
     * @return Populated DisplayInfoSettable.
     */
    public List<DisplayInfoSettable> execute(final List<DisplayInfoSettable> inDisplaySettables)
    {
        // sort by entity type to optimize calls to cache.
        List<Long> personIds = new ArrayList<Long>();
        List<Long> groupIds = new ArrayList<Long>();

        Long id;
        for (DisplayInfoSettable ds : inDisplaySettables)
        {
            id = ds.getEntityId();
            switch (ds.getEntityType())
            {
            case PERSON:
                if (!personIds.contains(id))
                {
                    personIds.add(id);
                }
                break;
            case GROUP:
                if (!groupIds.contains(id))
                {
                    groupIds.add(id);
                }
                break;
            default:
                break;
            }
        }

        // get group/person dtos from cache/db
        List<PersonModelView> personDTOs = getPersonModelViewsByIdsMapper.execute(personIds);
        List<DomainGroupModelView> groupDTOs = getGroupModelViewsByIdsMapper.execute(groupIds);

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
