/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.requests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lucene search request.
 */
public class LuceneSearchRequest
{
    /**
     * The fields.
     */
    private Map<String, Float> fields = new HashMap<String, Float>();

    /**
     * The search string.
     */
    private String searchString = "";

    /**
     * The fields to sort on.
     */
    private List<String> sortFields = new ArrayList<String>();

    /**
     * Target object type.
     */
    private Class< ? > objectType;

    /**
     * Index of first result.
     */
    private int firstResult = 0;

    /**
     * Max results.
     */
    private int maxResults = 0;

    /**
     * @param inSearchString
     *            the searchSearch to set
     */
    public void setSearchString(final String inSearchString)
    {
        this.searchString = inSearchString;
    }

    /**
     * @return the searchString
     */
    public String getSearchString()
    {
        return searchString;
    }

    /**
     * @param inFields
     *            the fields to set
     */
    public void setFields(final Map<String, Float> inFields)
    {
        this.fields = inFields;
    }

    /**
     * @return the fields
     */
    public Map<String, Float> getFields()
    {
        return fields;
    }

    /**
     * @param inSortFields
     *            the sortFields to set
     */
    public void setSortFields(final List<String> inSortFields)
    {
        this.sortFields = inSortFields;
    }

    /**
     * @return the sortFields
     */
    public List<String> getSortFields()
    {
        return sortFields;
    }

    /**
     * @param inObjectType
     *            the objectType to set
     */
    public void setObjectType(final Class< ? > inObjectType)
    {
        this.objectType = inObjectType;
    }

    /**
     * @return the objectType
     */
    public Class< ? > getObjectType()
    {
        return objectType;
    }

    /**
     * @param inFirstResult
     *            the firstResult to set
     */
    public void setFirstResult(final int inFirstResult)
    {
        this.firstResult = inFirstResult;
    }

    /**
     * @return the firstResult
     */
    public int getFirstResult()
    {
        return firstResult;
    }

    /**
     * @param inMaxResults
     *            the maxResults to set
     */
    public void setMaxResults(final int inMaxResults)
    {
        this.maxResults = inMaxResults;
    }

    /**
     * @return the maxResults
     */
    public int getMaxResults()
    {
        return maxResults;
    }

}
