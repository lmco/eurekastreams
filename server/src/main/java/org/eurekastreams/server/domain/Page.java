/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
import java.util.HashMap;

/**
 * Represents a page in our system.
 *
 */
public enum Page implements Serializable
{
    /**
     * Action executor page.
     */
    ACTION("actions"),
    /**
     * Advanced Search.
     */
    ADVANCED_SEARCH("advancedsearch"),
    /**
     * Search.
     */
    SEARCH("search"),
    /**
     * Settings page.
     */
    SETTINGS("settings"),
    /**
     * Authorize page.
     */
    AUTHORIZE("authorize"),
    /**
     * Gallery.
     */
    GALLERY("gallery"),
    /**
     * Activity.
     */
    ACTIVITY("activity"),
    /**
     * People profile.
     */
    PEOPLE("people"),
    /**
     * Personal settings.
     */
    PERSONAL_SETTINGS("personalsettings"),
    /**
     * Group profile.
     */
    GROUPS("groups"),
    /**
     * Group settings.
     */
    GROUP_SETTINGS("groupsettings"),
    /**
     * Organization profile.
     */
    ORGANIZATIONS("organizations"),
    /**
     * Org settings.
     */
    ORG_SETTINGS("orgsettings"),
    /**
     * New group page.
     */
    NEW_GROUP("newgroup"),
    /**
     * New org page.
     */
    NEW_ORG("neworg"),
    /**
     * Help page.
     */
    HELP("help"),
    /**
     * Start page.
     */
    START(""),
    /**
     * Widget for displaying a stream and allowing posts to it. (The name is per the product spec.)
     */
    WIDGET_COMMENT("widget-comment"),
    /**
     * Widget displaying a read-only view of a stream.
     */
    WIDGET_STREAM("widget-stream"),
    /**
     * Widget displaying a fully-interactive view of a stream.
     */
    WIDGET_FULL_STREAM("widget-fullstream"),
    /**
     * Widget to like or share a resource.
     */
    WIDGET_LIKE_SHARE("widget-likeshare"),
    /**
     * Widget displaying basic user profile information.
     */
    WIDGET_PROFILE_BADGE("widget-profilebadge");

    /**
     * human-readable name (ie, "Business Development" as opposed to enum.BD).
     */
    private final String displayableName;

    /**
     * for string-enum conversion.
     */
    private static HashMap<String, Page> map = new HashMap<String, Page>();

    static
    {
        Page[] types = Page.values();
        for (Page type : types)
        {
            map.put(type.toString(), type);
        }

    }

    /**
     * constructor for this enum.
     *
     * @param name
     *            a displayable name.
     */
    private Page(final String name)
    {
        displayableName = name;
    }

    /**
     * default constructor.
     */
    private Page()
    {
        displayableName = "";
    }

    /**
     *
     * @param displayableName
     *            to convert.
     * @return the enum corresponding to that string.
     */
    public static Page toEnum(final String displayableName)
    {
        if (map.containsKey(displayableName))
        {
            return map.get(displayableName);
        }

        return Page.START;
    }

    /**
     * @return the displayable name that was passed into the constructor.
     */
    @Override
    public String toString()
    {
        return displayableName;
    }
}
