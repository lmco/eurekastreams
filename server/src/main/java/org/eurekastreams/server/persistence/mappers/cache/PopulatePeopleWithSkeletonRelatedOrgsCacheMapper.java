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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Cache mapper that populates a person's related organizations with skeleton orgs built from cache.
 */
public class PopulatePeopleWithSkeletonRelatedOrgsCacheMapper
{
    /**
     * Log.
     */
    private static Log log = LogFactory.make();

    /**
     * Mapper to get organization modelviews by ids.
     */
    private DomainMapper<List<Long>, List<OrganizationModelView>> getOrgsByIdsMapper;

    /**
     * Mapper to get person modelviews by ids.
     */
    private GetPeopleByIds getPeopleByIdsMapper;

    /**
     * Constructor.
     *
     * @param inGetOrgsByIdsMapper
     *            cache mapper to get org modelviews by ids
     * @param inGetPeopleByIdsMapper
     *            cache mapper to get people modelviews by id
     */
    public PopulatePeopleWithSkeletonRelatedOrgsCacheMapper(
            final DomainMapper<List<Long>, List<OrganizationModelView>> inGetOrgsByIdsMapper,
            final GetPeopleByIds inGetPeopleByIdsMapper)
    {
        getOrgsByIdsMapper = inGetOrgsByIdsMapper;
        getPeopleByIdsMapper = inGetPeopleByIdsMapper;
    }

    /**
     * Populate the input Person's related organizations with skeleton Orgs from cache.
     *
     * @param inPerson
     *            the person to populate related orgs from.
     */
    public void execute(final Person inPerson)
    {
        // -- get the org modelviews of related org ids
        // first, we need the related org ids, which are in cache, so get the person
        log.info("Loading related orgs for person " + inPerson.toString()
                + ", starting by loading the PersonModelView to get the related org ids.");
        PersonModelView pmv = getPeopleByIdsMapper.execute(Collections.singletonList(inPerson.getId())).get(0);

        log.info("Loading the related organization modelviews by related org ids found in PersonModelView: "
                + pmv.getRelatedOrganizationIds().toString());
        List<Organization> relatedOrgs = new ArrayList<Organization>();
        for (OrganizationModelView relatedOrgMv : getOrgsByIdsMapper.execute(pmv.getRelatedOrganizationIds()))
        {
            relatedOrgs.add(new Organization(relatedOrgMv));
        }
        inPerson.setRelatedOrganizations(relatedOrgs);
        log.info("Finished loading person's related orgs via cache.");
    }
}
