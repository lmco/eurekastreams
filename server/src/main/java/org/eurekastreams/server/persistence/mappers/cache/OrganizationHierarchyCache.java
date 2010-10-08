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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetDirectChildOrgIds;
import org.eurekastreams.server.persistence.mappers.GetRecursiveChildOrgIds;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

/**
 * Temporary cache wrapper. This will be replaced by mappers that can load the information if not already in cache.
 */
public class OrganizationHierarchyCache extends CachedDomainMapper
{
    /**
     * GetOrganizationsByShortNames.
     */
    private GetOrganizationsByShortNames getOrganizationsByShortNames;

    /**
     * Mapper to get recursive child org ids.
     */
    private GetRecursiveChildOrgIds getRecursiveChildOrgIdsMapper;

    /**
     * mapper to get all parent org ids for an org id.
     */
    private DomainMapper<Long, List<Long>> getRecursiveParentOrgIdsMapper;

    /**
     * Mapper to get the direct child org ids.
     */
    private GetDirectChildOrgIds getDirectChildOrgIdsMapper;

    /**
     * Mapper to get organizations from short names.
     */
    private GetOrganizationsByShortNames getOrganizationsByShortNamesMapper;

    /**
     * Setter for GetOrganizationsByShortNames.
     *
     * @param inGetOrganizationsByShortNames
     *            GetOrganizationsByShortNames
     */
    public void setGetOrganizationsByShortNames(final GetOrganizationsByShortNames inGetOrganizationsByShortNames)
    {
        getOrganizationsByShortNames = inGetOrganizationsByShortNames;
    }

    /**
     * Get the ids of all of the parent organizations above the organization with the input id.
     *
     * @param organizationId
     *            the organization to check parents for
     * @return the ids of all of the parent organizations above the organization with the input id
     */
    public List<Long> getParentOrganizations(final long organizationId)
    {
        return getRecursiveParentOrgIdsMapper.execute(organizationId);
    }

    /**
     * Get the ids of the org with the input id and all of the parent organizations above it.
     *
     * @param organizationId
     *            the organization to get the parent tree for
     * @return a list of the parent organization ids in order, with the top-most parent at the zero-index, the org's own
     *         id last
     */
    public List<Long> getSelfAndParentOrganizations(final long organizationId)
    {
        ArrayList<Long> results = new ArrayList<Long>(getParentOrganizations(organizationId));
        results.add(organizationId);
        return results;
    }

    /**
     * Get the direct child organizations of the organization with the input organization id.
     *
     * @param organizationId
     *            the org to fetch children for
     * @return a set of organization ids of and all direct children, not including the input organization id
     */
    public Set<Long> getDirectChildOrganizations(final long organizationId)
    {
        return getDirectChildOrgIdsMapper.execute(organizationId);
    }

    /**
     * Recursively get the child organizations of the organization with the input organization id.
     *
     * @param organizationId
     *            the org to fetch children for
     * @return a set of organization ids of and all recursive children, not including the input organization id
     */
    public Set<Long> getRecursiveChildOrganizations(final long organizationId)
    {
        return getRecursiveChildOrgIdsMapper.execute(organizationId);
    }

    /**
     * Get the input org and all children recursively.
     *
     * @param organizationId
     *            the organization to fetch the children and self for
     * @return a set of organization ids including the input organizationid and all children, recursively
     */
    public Set<Long> getSelfAndRecursiveChildOrganizations(final long organizationId)
    {
        Set<Long> results = getRecursiveChildOrganizations(organizationId);
        results.add(organizationId);
        return results;
    }

    /**
     * Get the organization id from the input short name.
     *
     * @param inOrgShortName
     *            the short name of the group to look up
     * @return the id for the organization with the input shortname
     */
    public Long getOrganizationIdFromShortName(final String inOrgShortName)
    {
        OrganizationModelView org = getOrganizationsByShortNamesMapper.fetchUniqueResult(inOrgShortName);
        return org.getEntityId();
    }

    /**
     * Get the organization display name from the input short name.
     *
     * @param inOrgShortName
     *            the short name of the group to look up
     * @return the displayname for the organization with the input shortname
     */
    public String getOrganizationDisplayNameFromShortName(final String inOrgShortName)
    {
        OrganizationModelView org = getOrganizationsByShortNames.fetchUniqueResult(inOrgShortName);
        return org.getName();
    }

    /**
     * @param inGetRecursiveChildOrgIdsMapper
     *            the getRecursiveChildOrgIdsMapper to set
     */
    public void setGetRecursiveChildOrgIdsMapper(final GetRecursiveChildOrgIds inGetRecursiveChildOrgIdsMapper)
    {
        this.getRecursiveChildOrgIdsMapper = inGetRecursiveChildOrgIdsMapper;
    }

    /**
     * @param inGetDirectChildOrgIdsMapper
     *            the getDirectChildOrgIdsMapper to set
     */
    public void setGetDirectChildOrgIdsMapper(final GetDirectChildOrgIds inGetDirectChildOrgIdsMapper)
    {
        this.getDirectChildOrgIdsMapper = inGetDirectChildOrgIdsMapper;
    }

    /**
     * @param inGetOrganizationsByShortNamesMapper
     *            the getOrganizationsByShortNamesMapper to set
     */
    public void setGetOrganizationsByShortNamesMapper(
            final GetOrganizationsByShortNames inGetOrganizationsByShortNamesMapper)
    {
        this.getOrganizationsByShortNamesMapper = inGetOrganizationsByShortNamesMapper;
    }

    /**
     * @param inGetRecursiveParentOrgIdsMapper
     *            the getRecursiveParentOrgIdsMapper to set
     */
    public void setGetRecursiveParentOrgIdsMapper(final DomainMapper<Long, List<Long>> inGetRecursiveParentOrgIdsMapper)
    {
        this.getRecursiveParentOrgIdsMapper = inGetRecursiveParentOrgIdsMapper;
    }
}
