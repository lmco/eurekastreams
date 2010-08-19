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
package org.eurekastreams.server.service.actions.strategies.activity.datasources;

import java.io.Serializable;

import net.sf.json.JSONObject;

/**
 * Persistence data source transformer. Turn the JSON into something more mapper appropriate.
 * 
 */
public interface PersistenceDataSourceRequestTransformer
{
    /**
     * JSON -> request object.
     * 
     * @param request
     *            the JSON.
     * @param userEntityId
     *            the user entity ID.
     * @return the request object.
     */
    Serializable transform(JSONObject request, Long userEntityId);
}
