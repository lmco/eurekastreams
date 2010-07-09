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
package org.eurekastreams.server.action.request;

import java.io.Serializable;

/**
 * Person lookup request.
 * 
 */
public class PersonLookupRequest implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 8492951662570850676L;

    /**
     * Query string to search on.
     */
    private String queryString;

    /**
     * Max number of results to return.
     */
    private int maxResults;

    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    private PersonLookupRequest()
    {
        // no-op.
    }

    /**
     * Constructor.
     * 
     * @param inQueryString
     *            Query string to search on.
     * @param inMaxResutls
     *            Max number of results to return.
     */
    public PersonLookupRequest(final String inQueryString, final int inMaxResutls)
    {
        queryString = inQueryString;
        maxResults = inMaxResutls;
    }

    /**
     * @return the queryString
     */
    public String getQueryString()
    {
        return queryString;
    }

    /**
     * @param inQueryString
     *            the queryString to set
     */
    private void setQueryString(final String inQueryString)
    {
        queryString = inQueryString;
    }

    /**
     * @return the maxResults
     */
    public int getMaxResults()
    {
        return maxResults;
    }

    /**
     * @param inMaxResults
     *            the maxResults to set
     */
    private void setMaxResults(final int inMaxResults)
    {
        maxResults = inMaxResults;
    }

}
