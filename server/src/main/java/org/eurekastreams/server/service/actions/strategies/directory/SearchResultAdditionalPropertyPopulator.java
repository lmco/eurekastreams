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

import java.util.List;

import org.eurekastreams.commons.search.explanation.FieldMatchDeterminer;
import org.eurekastreams.commons.search.modelview.FieldMatch;
import org.eurekastreams.commons.search.modelview.ModelView;

/**
 * Strategy to populate additional properties on search results.
 */
public class SearchResultAdditionalPropertyPopulator implements TransientPropertyPopulator
{
    /**
     * Field Match Determiner - responsible for deciding which fields were matches for the search text.
     */
    private FieldMatchDeterminer fieldMatchDeterminer;

    /**
     * Constructor.
     *
     * @param inFieldMatchDeterminer
     *            the FieldMatchDeterminer to set
     */
    public SearchResultAdditionalPropertyPopulator(final FieldMatchDeterminer inFieldMatchDeterminer)
    {
        fieldMatchDeterminer = inFieldMatchDeterminer;
    }

    /**
     * Populate any transient properties in the result set.
     *
     * @param results
     *            the results to update
     * @param userPersonId
     *            the currently logged-in user's Person Id
     * @param searchText
     *            the text the user searched for
     */
    public void populateTransientProperties(final List<ModelView> results, final long userPersonId,
            final String searchText)
    {
        // Figure out which query terms matched which fields
        for (ModelView result : results)
        {
            FieldMatch match = fieldMatchDeterminer.determineFieldMatches(result.getSearchIndexExplanationString(),
                    searchText);
            result.setFieldMatch(match);
        }
    }
}
