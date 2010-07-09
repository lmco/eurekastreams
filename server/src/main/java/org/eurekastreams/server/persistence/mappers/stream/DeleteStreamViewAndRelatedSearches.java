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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.List;

import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.domain.stream.StreamView;

/**
 * Deletes a StreamView and any related stream searches from the database.
 */
public class DeleteStreamViewAndRelatedSearches extends BaseArgCachedDomainMapper<Long, Boolean>
{
    /**
     * Deletes a stream view and related searches.
     * 
     * @param inStreamViewId
     *            the id of the stream view to delete.
     * @return true
     */
    @Override
    @SuppressWarnings("unchecked")
    public Boolean execute(final Long inStreamViewId)
    {
        // see if the streamview has related searches
        List<StreamSearch> searches = getEntityManager().createQuery("from StreamSearch where streamView.id = :id")
                .setParameter("id", inStreamViewId).getResultList();

        for (StreamSearch search : searches)
        {
            getEntityManager().remove(search);
        }

        List<StreamView> streamViews = getEntityManager().createQuery("FROM StreamView WHERE id = :id").setParameter(
                "id", inStreamViewId).getResultList();

        if (streamViews.size() > 0)
        {
            StreamView view = streamViews.get(0);
            view.setIncludedScopes(null);
            getEntityManager().remove(view);
        }
        getEntityManager().flush();
        return true;
    }
}
