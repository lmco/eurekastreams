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

import org.eurekastreams.server.action.response.settings.RetrieveSettingsResponse;

/**
 * Retrieves settings for a settings display.
 */
public interface SettingsRetriever
{
    /**
     * Retrieves settings.
     *
     * @param id
     *            Id of entity for which to retrieve settings.
     * @param response
     *            Retrieve settings response message to which the retriever will add its contribution.
     */
    void retrieve(long id, RetrieveSettingsResponse response);
}
