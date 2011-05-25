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
package org.eurekastreams.server.action.response.settings;

import java.util.List;

import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.server.domain.Theme;

/**
 * Response object for PersonPropertiesGenerator.
 * 
 */
public class PersonPropertiesResponse
{
    /**
     * List of {@link TabTemplate}s.
     */
    private List<TabTemplate> tabTemplates;

    /**
     * {@link Theme}.
     */
    private Theme theme;

    /**
     * Constructor.
     * 
     * @param inTabTemplates
     *            List of {@link TabTemplate}s.
     * @param inTheme
     *            {@link Theme}.
     */
    public PersonPropertiesResponse(final List<TabTemplate> inTabTemplates, final Theme inTheme)
    {
        tabTemplates = inTabTemplates;
        theme = inTheme;
    }

    /**
     * @return the tabTemplates
     */
    public List<TabTemplate> getTabTemplates()
    {
        return tabTemplates;
    }

    /**
     * @param inTabTemplates
     *            the tabTemplates to set
     */
    public void setTabTemplates(final List<TabTemplate> inTabTemplates)
    {
        tabTemplates = inTabTemplates;
    }

    /**
     * @return the theme
     */
    public Theme getTheme()
    {
        return theme;
    }

    /**
     * @param inTheme
     *            the theme to set
     */
    public void setTheme(final Theme inTheme)
    {
        theme = inTheme;
    }
}
