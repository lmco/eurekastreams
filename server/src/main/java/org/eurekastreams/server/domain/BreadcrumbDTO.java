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
package org.eurekastreams.server.domain;

import java.io.Serializable;

/**
 * Represents a single breadcrumb item.
 */
public class BreadcrumbDTO implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 1950886338295102535L;

    /**
     * The text to display for this breadcrumb.
     */
    private String text;

    /**
     * The target page.
     */
    private Page page;
    
    /**
     * The target page view.
     */
    private String view;

    /**
     * Constructor.
     * 
     * @param inText
     *            the text
     * @param inPage
     *            the page
     * @param inView
     *            the view
     */
    public BreadcrumbDTO(final String inText, final Page inPage, final String inView)
    {
        text = inText;
        page = inPage;
        view = inView;
    }
    
    /**
     * Empty constructor necessary for serialization.
     */
    @SuppressWarnings("unused")
    private BreadcrumbDTO()
    {
        //nothing to do.
    }

    /**
     * Getter.
     * 
     * @return text
     */
    public String getText()
    {
        return text;
    }

    /**
     * Getter.
     * 
     * @return page
     */
    public Page getPage()
    {
        return page;
    }
    
    /**
     * Getter. 
     * 
     * @return view
     */
    public String getView()
    {
        return view;
    }
}
