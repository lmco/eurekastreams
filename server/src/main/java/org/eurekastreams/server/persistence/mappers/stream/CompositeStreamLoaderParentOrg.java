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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.OrganizationHierarchyCache;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Returns Activity ids for composite streams of parent org type.
 *
 */
public class CompositeStreamLoaderParentOrg extends BaseCompositeStreamLoader
{
    /**
     * DAO for looking up a person.
     */
    private GetPeopleByIds personDAO;
    
    /**
     * DAO for looking up an Organization.
     */
    private GetOrganizationsByIds organizationDAO;
    
    /**
     * Organization hierarchy cache.
     */
    private OrganizationHierarchyCache organizationHierarchyCache;
    
    /**
     * Constructor.
     * @param inPersonDAO DAO for looking up a person.
     * @param inOrganizationDAO DAO for looking up an Organization.
     */
    CompositeStreamLoaderParentOrg(final GetPeopleByIds inPersonDAO, 
            final GetOrganizationsByIds inOrganizationDAO)
    {
        personDAO = inPersonDAO;
        organizationDAO = inOrganizationDAO;
    }

    /**
     * Returns restrictions hashtable to be used in returning activityId list from datastore.
     * @param inCompositeStream the CompositeStream.
     * @param inUserId the user.
     * 
     * @return restrictions hashtable to be used in returning activityId list from datastore.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Hashtable<RestrictionType, HashSet> getActivityRestrictions(final StreamView inCompositeStream, 
            final long inUserId)
    {
        Hashtable results = new Hashtable<RestrictionType, HashSet>();
        PersonModelView pmv = getPersonById(inUserId);
        HashSet<Long> orgIds = new HashSet<Long>();
        //TODO: this should use memcache, not local cache, but that hasn't been implemented yet.
        orgIds.addAll(organizationHierarchyCache.getSelfAndRecursiveChildOrganizations(pmv.getParentOrganizationId()));
        results.put(RestrictionType.ORG_IDS, orgIds);
        return results;
    }

    /**
     * Get list of activity ids for given compositeStream and user from cache, if present, or null if not.
     * 
     * @param compositeStream
     *            The CompositeStream.
     * @param inUserId
     *            The user id.
     * @return List of activity ids for given compositeStream and user from cache, if present, or null if not.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected List<Long> getIdListFromCache(final StreamView compositeStream, final long inUserId)
    {        
        return getCache().getList(
        		CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM 
        		+ getPersonParentOrgCompositeStreamId(inUserId));  
    }

    /**
     * Sets the list of activity ids to cache for given CompositeStream and user.
     * 
     * @param inActivityIds
     *            The list of activity ids.
     * @param inCompositeStream
     *            The CompositeStream.
     * @param inUserId
     *            The user id.
     */
    @Override
    protected void setIdListToCache(final List<Long> inActivityIds, 
            final StreamView inCompositeStream, final long inUserId)
    {
        getCache().setList(
        		CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM 
        		+ getPersonParentOrgCompositeStreamId(inUserId), inActivityIds);
    }
    
    /**
     * Returns compositeStreamId for a person's parent org.
     * @param inUserId the user id.
     * @return compositeStreamId for a person's parent org.
     */
    @SuppressWarnings("serial")
    private Long getPersonParentOrgCompositeStreamId(final long inUserId)
    {
        final Long parentOrgId = getPersonById(inUserId).getParentOrganizationId();
        ArrayList<Long> ids = new ArrayList<Long>() { { add(parentOrgId); } };
        return organizationDAO.execute(ids).get(0).getCompositeStreamId();        
    }
    
    /**
     * Returns PersonModelView for a given user id.
     * @param inUserId The user id.
     * @return PersonModelView for a given user id.
     */
    @SuppressWarnings("serial")
    private PersonModelView getPersonById(final long inUserId)
    {
        ArrayList<Long> ids = new ArrayList<Long>() { { add(inUserId); } };
        return personDAO.execute(ids).get(0);
    }
    
    /**
     * Set the Organization Hierarchy Cache.
     * 
     * @param inOrgCache
     *            the orgCache to set
     */
    //TODO: this should use memcache, not local cache, but that hasn't been implemented yet.
    public void setOrganizationHierarchyCache(final OrganizationHierarchyCache inOrgCache)
    {
        this.organizationHierarchyCache = inOrgCache;
    }

}
