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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersonAndStreamViewIdRequest;

/**
 * Deletes a StreamView and any related stream searches from the database.
 */
public class DeleteStreamViewAndRelatedSearches extends BaseArgDomainMapper<PersonAndStreamViewIdRequest, Boolean>
{
    /**
     * Logger.
     */
    private static Log log = LogFactory.make();

    /**
     * Deletes a stream view and related searches.
     *
     * @param inPersonAndStreamIdRequest
     *            the request object containing the stream view id to delete, and owner of the stream view
     * @return true
     */
    @Override
    @SuppressWarnings("unchecked")
    public Boolean execute(final PersonAndStreamViewIdRequest inPersonAndStreamIdRequest)
    {
        long streamViewId = inPersonAndStreamIdRequest.getStreamViewId();

        // find the stream view
        List<StreamView> streamViews = getEntityManager().createQuery("FROM StreamView WHERE id = :id").setParameter(
                "id", streamViewId).getResultList();
        if (streamViews.size() == 0)
        {
            log.info("Couldn't find the stream view #" + streamViewId);
            return true;
        }

        // get all the stream searches for this stream view
        List<StreamSearch> allStreamSearches = getEntityManager().createQuery(
                "from StreamSearch where streamView.id = :id").setParameter("id", streamViewId).getResultList();
        for (StreamSearch search : allStreamSearches)
        {
            // for each stream search, remove it from the list of stream searches for each all person that has it (just
            // in case this is possible, because it is in the database)

            // find the person that owns this stream search
            Person searchOwner = search.getPerson();
            if (searchOwner.getId() == inPersonAndStreamIdRequest.getPerson().getId())
            {
                searchOwner = inPersonAndStreamIdRequest.getPerson();
            }

            // loop across this person's stream searches to remove this one from the list
            int personSearchCount = searchOwner.getStreamSearches().size();
            for (int i = 0; i < personSearchCount; i++)
            {
                StreamSearch personSearch = searchOwner.getStreamSearches().get(i);
                if (personSearch.getStreamView().getId() == streamViewId)
                {
                    log.info("Found stream search #" + personSearch.getId() + " for user " + searchOwner.getAccountId()
                            + " which is based on the stream view #" + streamViewId
                            + " that's being deleted.  Removing from the user's collection and deleting it.");

                    searchOwner.getStreamSearches().remove(personSearch);
                    personSearchCount--;
                }
            }

            getEntityManager().remove(search);
        }

        // now remove the view, flush, refresh, and return true
        StreamView view = streamViews.get(0);
        view.setIncludedScopes(null);
        getEntityManager().remove(view);
        getEntityManager().flush();
        return true;
    }
}
