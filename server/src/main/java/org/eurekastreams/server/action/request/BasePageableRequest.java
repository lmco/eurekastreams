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


/**
 * Base implementation for pageable requests.
 */
public abstract class BasePageableRequest implements PageableRequest
{
    /** Fingerprint. */
    private static final long serialVersionUID = 5707194703847691656L;

    /**
     * The start index for items to return.
     */
    private int startIndex;

    /**
     * The end index for items to return.
     */
    private int endIndex;

    /**
     * Constructor for serialization.
     */
    protected BasePageableRequest()
    {
    }

    /**
     * Constructor.
     *
     * @param inStartIndex
     *            The start index for items to return.
     * @param inEndIndex
     *            The end index for items to return.
     */
    protected BasePageableRequest(final int inStartIndex, final int inEndIndex)
    {
        startIndex = inStartIndex;
        endIndex = inEndIndex;
    }

    /**
     * @return The start index for for items to return.
     */
    public Integer getStartIndex()
    {
        return startIndex;
    }

    /**
     * @param inStartIndex
     *            the start index for for items to return.
     */
    public void setStartIndex(final Integer inStartIndex)
    {
        startIndex = inStartIndex;
    }

    /**
     * @return the end index for for items to return.
     */
    public Integer getEndIndex()
    {
        return endIndex;
    }

    /**
     * @param inEndIndex
     *            the end index for for items to return.
     */
    public void setEndIndex(final Integer inEndIndex)
    {
        endIndex = inEndIndex;
    }
}
