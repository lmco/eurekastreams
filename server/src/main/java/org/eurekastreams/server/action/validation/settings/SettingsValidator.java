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
package org.eurekastreams.server.action.validation.settings;

import java.util.Map;

import org.eurekastreams.commons.actions.context.Principal;

/**
 * Validates FormBuilder settings presented to the server for update.
 */
public interface SettingsValidator
{
    /**
     * Validates settings.
     *
     * @param settings
     *            Map of settings.
     * @param user
     *            User performing the update.
     */
    void validate(Map<String, Object> settings, Principal user);
}
