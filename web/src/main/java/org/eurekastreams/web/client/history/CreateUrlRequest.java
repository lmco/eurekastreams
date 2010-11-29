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
package org.eurekastreams.web.client.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.domain.Page;

/**
 * Create url request.
 *
 */
public class CreateUrlRequest
{
    /**
     * The page (e.g. people).
     */
    private Page page = null;
    /**
     * The views.
     */
    private List<String> views = null;
    /**
     * Parameters.
     */
    private Map<String, String> parameters = new HashMap<String, String>();

    /**
     * Replace what's in the URL completely if true. If false, just add the params.
     */
    private Boolean replacePrevious = Boolean.FALSE;

    /**
     * Create URL based on current URL.
     */
    public CreateUrlRequest()
    {
    }

    /**
     * Constructor for taking in a page, views, and params. Full URL update. Example is if the user wanted to navigate
     * to #people/username1?action=edit
     *
     * @param inPage
     *            the page (people).
     * @param inViews
     *            the views (username1).
     * @param inParameters
     *            the params (action/edit).
     */
    public CreateUrlRequest(final Page inPage, final List<String> inViews, final Map<String, String> inParameters)
    {
        parameters = inParameters;
        page = inPage;
        views = inViews;
        replacePrevious = true;
    }

    /**
     * Constructor for taking in a page, views. Example is if the user wanted to navigate to #people/username1
     *
     * @param inPage
     *            the page (people).
     * @param inViews
     *            the views (username1).
     */
    public CreateUrlRequest(final Page inPage, final List<String> inViews)
    {
        parameters = new HashMap<String, String>();
        page = inPage;
        views = inViews;
        replacePrevious = true;
    }

    /**
     * Constructor for taking in a page, and params. Example is if the user wanted to navigate to #gallery?action=edit
     *
     * @param inPage
     *            the page (gallery)
     * @param inParameters
     *            the params (action/edit).
     */
    public CreateUrlRequest(final Page inPage, final Map<String, String> inParameters)
    {
        parameters = inParameters;
        page = inPage;
        views = new ArrayList<String>();
        replacePrevious = true;
    }

    /**
     * Constructor for navigating to another page with params and KEEPING the current params in tact.
     *
     * @param inPage
     *            the page.
     * @param inParameters
     *            the params.
     * @param inReplacePrevious
     *            whether or not to replace. If true, just the constructor w/o this boolean for cleanliness.
     */
    public CreateUrlRequest(final Page inPage, final Map<String, String> inParameters, final boolean inReplacePrevious)
    {
        parameters = inParameters;
        page = inPage;
        views = new ArrayList<String>();
        replacePrevious = inReplacePrevious;
    }

    /**
     * Constructor for taking in a page. Example is if the user wanted to navigate to #gallery
     *
     * @param inPage
     *            the page (gallery).
     */
    public CreateUrlRequest(final Page inPage)
    {
        page = inPage;
        views = new ArrayList<String>();
        replacePrevious = true;
    }

    /**
     * Constructor for taking in a page. Example is if the user wanted to navigate to #gallery
     *
     * @param inPage
     *            the page (gallery).
     * @param inView
     *            the view.
     */
    public CreateUrlRequest(final Page inPage, final String inView)
    {
        views = new ArrayList<String>();
        views.add(inView);
        page = inPage;
        replacePrevious = true;
    }

    /**
     * Constructor for taking in a page. Example is if the user wanted to navigate to #gallery
     *
     * @param inPage
     *            the page (gallery).
     * @param inView
     *            the view.
     * @param inParameters
     *            the params.
     */
    public CreateUrlRequest(final Page inPage, final String inView, final Map<String, String> inParameters)
    {
        views = new ArrayList<String>();
        views.add(inView);
        parameters = inParameters;
        page = inPage;
        replacePrevious = true;
    }

    /**
     * Constructor for just inserting/updating new params.
     *
     * @param inParameters
     *            the params.
     */
    public CreateUrlRequest(final Map<String, String> inParameters)
    {
        parameters = inParameters;
    }

    /**
     * Constructor for setting the params and setting the boolean for whether or not to wipe out. the other params,
     *
     * @param inParameters
     *            the params,
     * @param inReplace
     *            replace.
     */
    public CreateUrlRequest(final Map<String, String> inParameters, final Boolean inReplace)
    {
        parameters = inParameters;
        replacePrevious = inReplace;
    }

    /**
     * Constructor for setting ONE URL paramter key/value pair, and allowing you to clear the others.
     *
     * @param key
     *            the key.
     * @param value
     *            the value.
     * @param inReplace
     *            replace the others.
     */
    public CreateUrlRequest(final String key, final String value, final Boolean inReplace)
    {
        parameters.put(key, value);
        replacePrevious = inReplace;
    }

    /**
     * Constructor for taking in a page with a view and a single parameter.
     *
     * @param inPage
     *            the page (gallery).
     * @param inView
     *            the view.
     * @param key
     *            the key.
     * @param value
     *            the value.
     */
    public CreateUrlRequest(final Page inPage, final String inView, final String key, final String value)
    {
        page = inPage;
        views = Collections.singletonList(inView);
        parameters.put(key, value);
        replacePrevious = true;
    }

    /**
     * Constructor for setting ONE URL paramter key/value pair, and allowing you to clear the others.
     *
     * @param inPage
     *            the page.
     * @param key
     *            the key.
     * @param value
     *            the value.
     */
    public CreateUrlRequest(final Page inPage, final String key, final String value)
    {
        views = new ArrayList<String>();
        page = inPage;
        parameters.put(key, value);
        replacePrevious = true;
    }

    /**
     * Get the replace previous boolean.
     *
     * @return value.
     */
    public Boolean getReplacePrevious()
    {
        return replacePrevious;
    }

    /**
     * Get the parameters.
     *
     * @return the parameters.
     */
    public Map<String, String> getParameters()
    {
        return parameters;
    }

    /**
     * Get the page.
     *
     * @return the page.
     */
    public Page getPage()
    {
        return page;
    }

    /**
     * Get the view.
     *
     * @return the view.
     */
    public List<String> getViews()
    {
        return views;
    }

    /**
     * Set the link parameters.
     * @param inParameters the parameters.
     */
    public void setParameters(final HashMap<String, String> inParameters)
    {
        parameters = inParameters;

    }
}
