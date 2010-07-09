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

import java.io.Serializable;
import java.util.Map;

/**
 * Holds settings for a settings page (personal, etc.) and supporting data.
 */
public class RetrieveSettingsResponse implements Serializable
{
    /** Settings key for chosen timezone. */
    public static final String SETTINGS_TIMEZONE = "timezone";

    /** Settings key for chosen notifications filters. */
    public static final String SETTINGS_NOTIFICATION_FILTERS = "notificationFilterList";

    /** Support data key for allowed timezones list. */
    public static final String SUPPORT_TIMEZONES = "timezones";

    /** Support data key for system default timezone. */
    public static final String SUPPORT_DEFAULT_TIMEZONE = "timezoneDefault" + "";

    /** Support data key for allowed notifier types. */
    public static final String SUPPORT_NOTIFIER_TYPES = "notifierTypes";

    /** Serialization id. */
    private static final long serialVersionUID = -1749571643293858371L;

    /** The actual settings. */
    private Map<String, Object> settings;

    /** Support data for presenting the settings. */
    private Map<String, Object> support;

    /**
     * Constructor.
     *
     * @param inSettings
     *            The settings.
     * @param inSupport
     *            Supporting data.
     */
    public RetrieveSettingsResponse(final Map<String, Object> inSettings, final Map<String, Object> inSupport)
    {
        settings = inSettings;
        support = inSupport;
    }

    /**
     * Constructor for serialization.
     */
    private RetrieveSettingsResponse()
    {
    }

    /**
     * @return The settings.
     */
    public Map<String, Object> getSettings()
    {
        return settings;
    }

    /**
     * @param inSettings
     *            The settings to store.
     */
    public void setSettings(final Map<String, Object> inSettings)
    {
        settings = inSettings;
    }

    /**
     * @return Supporting data.
     */
    public Map<String, Object> getSupport()
    {
        return support;
    }

    /**
     * @param inSupport
     *            Supporting data to store.
     */
    public void setSupport(final Map<String, Object> inSupport)
    {
        support = inSupport;
    }


}
