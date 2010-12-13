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
package org.eurekastreams.server.search.modelview;

import java.io.Serializable;
import java.util.List;

/**
 * Tab and theme info for a Person.
 * 
 */
public class PersonPagePropertiesDTO implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -8335987987789189477L;

    /**
     * Theme file location.
     */
    private String themeCssFile;

    /**
     * Theme uuid.
     */
    private String themeUuid;

    /**
     * List of TabDTOs.
     */
    private List<TabDTO> tabDTOs;

    /**
     * @return the themeCssFile
     */
    public String getThemeCssFile()
    {
        return themeCssFile;
    }

    /**
     * @param inThemeCssFile
     *            the themeCssFile to set
     */
    public void setThemeCssFile(final String inThemeCssFile)
    {
        themeCssFile = inThemeCssFile;
    }

    /**
     * @return the tabDTOs
     */
    public List<TabDTO> getTabDTOs()
    {
        return tabDTOs;
    }

    /**
     * @param inTabDTOs
     *            the tabDTOs to set
     */
    public void setTabDTOs(final List<TabDTO> inTabDTOs)
    {
        tabDTOs = inTabDTOs;
    }

    /**
     * @return the themeUuid
     */
    public String getThemeUuid()
    {
        return themeUuid;
    }

    /**
     * @param inThemeUuid
     *            the themeUuid to set
     */
    public void setThemeUuid(final String inThemeUuid)
    {
        themeUuid = inThemeUuid;
    }

}
