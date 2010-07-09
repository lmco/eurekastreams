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
 * Group lookup request.
 * 
 */
public class GroupLookupRequest implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 7364851662570850676L;

    /**
     * Query string to search on.
     */
    private String queryString;

    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    private GroupLookupRequest()
    {
        // no-op.
    }

    /**
     * Constructor.
     * 
     * @param inQueryString
     *            Query string to search on.
     */
    public GroupLookupRequest(final String inQueryString)
    {
        queryString = inQueryString;
    }

    /**
     * @return the queryString
     */
    public String getQueryString()
    {
        return queryString;
    }
}
