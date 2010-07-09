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
package org.eurekastreams.server.service.actions.strategies.directory;

import javax.persistence.NoResultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.hibernate.search.jpa.FullTextQuery;

/**
 * Get and hang onto the root organization.
 */
public class OrganizationIdGetter implements OrganizationIdentifierGetter
{
    /**
     * Instance of the logger.
     */
    private Log log = LogFactory.getLog(OrganizationIdGetter.class);

    /**
     * The search request builder to use to make all search requests.
     */
    private ProjectionSearchRequestBuilder searchRequestBuilder;

    /**
     * DB mapper to use if org has not been indexed yet.
     */
    private GetOrganizationsByShortNames orgByShortNameDBMapper;

    /**
     * Constructor.
     * 
     * @param inSearchRequestBuilder
     *            The search request builder to use to make all search requests.
     * @param inOrgByShortNameDBMapper
     *            DB mapper to use if org has not been indexed yet.
     */
    public OrganizationIdGetter(final ProjectionSearchRequestBuilder inSearchRequestBuilder,
            final GetOrganizationsByShortNames inOrgByShortNameDBMapper)
    {
        searchRequestBuilder = inSearchRequestBuilder;
        orgByShortNameDBMapper = inOrgByShortNameDBMapper;
    }

    /**
     * Get the organization Id.
     * 
     * @param shortName
     *            the short name.
     * @return the identifer.
     */
    public String getIdentifier(final String shortName)
    {
        // try to get org id from search, but due to indexing lag it may not
        // be there so, if no result is found, try looking it up in DB.
        log.info("Organization's ID is not known - searching...");
        FullTextQuery query = searchRequestBuilder.buildQueryFromSearchText(shortName);
        Long id = null;
        try
        {
            OrganizationModelView org = (OrganizationModelView) query.getSingleResult();
            id = org.getEntityId();
        }
        catch (NoResultException nre)
        {
            log.info("Organization " + shortName + " ID not in search index, searching DB");
            id = orgByShortNameDBMapper.fetchId(shortName);
            if (id == null)
            {
                throw nre;
            }
        }

        return Long.toString(id);
    }
}
