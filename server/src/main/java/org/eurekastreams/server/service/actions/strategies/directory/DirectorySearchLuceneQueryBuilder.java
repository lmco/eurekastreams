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

import org.eurekastreams.commons.search.LuceneFieldBooster;

/**
 * Strategy to build a Lucene query string for searching the directory.
 */
public class DirectorySearchLuceneQueryBuilder
{
    /**
     * Search format mask.
     */
    private String searchFormatMask;

    /**
     * Field booster.
     */
    private LuceneFieldBooster fieldBooster;

    /**
     * Field boost factor.
     */
    private int fieldBoostFactor;

    /**
     * Constructor.
     * 
     * @param inSearchFormatMask
     *            the search format mask.
     * @param inFieldBooster
     *            the field booster.
     * @param inFieldBoostFactor
     *            the boost factor (recommend 50).
     */
    public DirectorySearchLuceneQueryBuilder(final String inSearchFormatMask, final LuceneFieldBooster inFieldBooster,
            final int inFieldBoostFactor)
    {
        searchFormatMask = inSearchFormatMask;
        fieldBooster = inFieldBooster;
        fieldBoostFactor = inFieldBoostFactor;
    }

    /**
     * Modify the input search string mask, boosting a field if requested, scoping the search to a parent organization
     * id if requested, and passing in a userId clause that will add the ability to fetch private groups that the
     * current user is a follower or coordinator for.
     * 
     * @param searchText
     *            the search term the user entered, already escaped to prevent query-injection
     * @param weightedField
     *            the field to boost
     * @param userId
     *            the current user's Person Id - extends ExtendedUserDetails if logged in
     * @return a native lucene query
     */
    public String buildNativeQuery(final String searchText, final String weightedField, final long userId)
    {
        String queryMask = searchFormatMask;

        // weight a field if asked by the client
        if (weightedField.length() > 0)
        {
            queryMask = fieldBooster.boostField(queryMask, weightedField, fieldBoostFactor);
        }

        // the query mask expects two terms - the search term, then an additional permission clause - optional, only
        // passed in if user is logged in
        return String.format(queryMask, searchText);
    }
}
