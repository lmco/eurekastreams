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
package org.eurekastreams.server.action.execution.settings;

import java.io.Serializable;
import java.util.Map;

import org.eurekastreams.commons.actions.context.Principal;

/**
 * Defines a strategy for saving FormBuilder settings.
 */
public interface SettingsUpdater
{
    /**
     * Updates/persists settings.
     *
     * @param settings
     *            Map of settings.
     * @param user
     *            User performing the update.
     */
    void update(Map<String, Serializable> settings, Principal user);
}
