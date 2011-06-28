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
package org.eurekastreams.web.client.events.data;

import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.domain.PagedSet;

/**
 * Search results response event.
 * 
 */
public class GotSearchResultsResponseEvent extends BaseDataResponseEvent<PagedSet<ModelView>>
{
    /**
     * The caller key.
     */
    private String callerKey = "";

    /**
     * Default constructor.
     * 
     * @param inCallerKey
     *            the caller key.
     * @param inResponse
     *            response.
     */
    public GotSearchResultsResponseEvent(final String inCallerKey, final PagedSet<ModelView> inResponse)
    {
        super(inResponse);
        setCallerKey(inCallerKey);
    }

    /**
     * @param inCallerKey
     *            the callerKey to set
     */
    public void setCallerKey(final String inCallerKey)
    {
        this.callerKey = inCallerKey;
    }

    /**
     * @return the callerKey
     */
    public String getCallerKey()
    {
        return callerKey;
    }

}
