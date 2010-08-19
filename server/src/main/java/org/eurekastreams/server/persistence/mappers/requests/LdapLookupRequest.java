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
package org.eurekastreams.server.persistence.mappers.requests;

/**
 * Request object for LdapLookup mapper.
 * 
 */
public class LdapLookupRequest
{
    /**
     * Attribute query string.
     */
    private String queryString;

    /**
     * Max results to return.
     */
    private int searchUpperBound = Integer.MAX_VALUE;

    /**
     * String key used to look up template.
     */
    private String templateKey;

    /**
     * Constructor.
     * 
     * @param inQueryString
     *            attribute query string
     */
    public LdapLookupRequest(final String inQueryString)
    {
        this(inQueryString, Integer.MAX_VALUE);
    }

    /**
     * Constructor.
     * 
     * @param inQueryString
     *            attribute query string.
     * @param inSearchUpperBound
     *            max results.
     */
    public LdapLookupRequest(final String inQueryString, final int inSearchUpperBound)
    {
        this(inQueryString, inSearchUpperBound, null);
    }

    /**
     * Constructor.
     * 
     * @param inQueryString
     *            attribute query string.
     * @param inTemplateKey
     *            Key used to lookup search template, uses query string if null.
     */
    public LdapLookupRequest(final String inQueryString, final String inTemplateKey)
    {
        this(inQueryString, Integer.MAX_VALUE, inTemplateKey);
    }

    /**
     * Constructor.
     * 
     * @param inQueryString
     *            attribute query string.
     * @param inSearchUpperBound
     *            max results.
     * @param inTemplateKey
     *            Key used to lookup search template, uses query string if null.
     */
    public LdapLookupRequest(final String inQueryString, final int inSearchUpperBound, final String inTemplateKey)
    {
        queryString = inQueryString;
        searchUpperBound = inSearchUpperBound;
        templateKey = inTemplateKey;
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
    public void setQueryString(final String inQueryString)
    {
        queryString = inQueryString;
    }

    /**
     * @return the searchUpperBound
     */
    public int getSearchUpperBound()
    {
        return searchUpperBound;
    }

    /**
     * @return the templateKey
     */
    public String getTemplateKey()
    {
        return templateKey;
    }

}
