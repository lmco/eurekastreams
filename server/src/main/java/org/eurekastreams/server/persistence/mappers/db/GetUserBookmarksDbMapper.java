/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.List;

import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Get streams for a user.
 */
public class GetUserBookmarksDbMapper extends BaseArgDomainMapper<Long, List<StreamScope>> implements
        DomainMapper<Long, List<StreamScope>>
{
    /**
     * Get the bookmarks for a user.
     * 
     * @param inUserEntityId
     *            the user id.
     * @return the bookmarks.
     */
    public List<StreamScope> execute(final Long inUserEntityId)
    {
        List<StreamScope> bookmarks = getEntityManager().createQuery(
                "SELECT bookmarks from Person p where p.id = :userId order by scopeId").setParameter("userId",
                inUserEntityId).getResultList();

        return bookmarks;
    }

}
