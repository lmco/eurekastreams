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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Clear the Recursive Org Children ID cache for every Org up the tree from an input org.
 */
public class OrgParentHierarchyCacheCleaner extends CachedDomainMapper
{
    /**
     * Logger instance.
     */
    private Log log = LogFactory.getLog(OrgParentHierarchyCacheCleaner.class);

    /**
     * mapper to get all parent org ids for an org id.
     */
    private DomainMapper<Long, List<Long>> getRecursiveParentOrgIdsMapper;

    /**
     * Clear the Recursive Org Children ID cache for every Org up the tree from an input org.
     *
     * @param inOrgId
     *            the starting org
     */
    public void execute(final Long inOrgId)
    {
        // get all the parents of this org
        // - wrap the list in another list so we don't modify the original collection, which becomes a problem with the
        // in-memory test cache
        List<Long> parentOrgIds = getRecursiveParentOrgIdsMapper.execute(inOrgId);

        // Note: When a new organization is created, the @PostPersist method calls OrganizationHierarchyCacheLoader's
        // onPostPersist, which deletes the global cache entry CacheKeys.ORGANIZATION_TREE_DTO and the cache entry
        // CacheKeys.ORGANIZATION_DIRECT_CHILDREN for the new org's parent. If that were not the case, we'd need to do
        // it here (otherwise the parent's cached child org list would be missing the new org).

        for (Long recursiveParentOrgId : parentOrgIds)
        {
            log.info("Clearing cache for org with id: " + recursiveParentOrgId);

            // clear out the recursive child org ids, org
            log.info("Deleting recursive children org id cache for org with id: " + recursiveParentOrgId);
            getCache().delete(CacheKeys.ORGANIZATION_RECURSIVE_CHILDREN + recursiveParentOrgId);

            log.info("Deleting org cache for org with id: " + recursiveParentOrgId);
            getCache().delete(CacheKeys.ORGANIZATION_BY_ID + recursiveParentOrgId);
        }
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
