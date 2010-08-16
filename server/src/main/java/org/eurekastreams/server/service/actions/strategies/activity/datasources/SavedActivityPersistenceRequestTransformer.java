/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;

/**
 * Transforms JSON request to saved activity request.
 */
public class SavedActivityPersistenceRequestTransformer implements PersistenceDataSourceRequestTransformer
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(SavedActivityPersistenceRequestTransformer.class);

    /**
     * Person mapper.
     */
    private GetPeopleByAccountIds personMapper;

    /**
     * Default constructor.
     * 
     * @param inPersonMapper
     *            person mapper.
     */
    public SavedActivityPersistenceRequestTransformer(final GetPeopleByAccountIds inPersonMapper)
    {
        personMapper = inPersonMapper;
    }

    /**
     * Transforms the request.
     * 
     * @param request
     *            the JSON request.
     * @param userEntityId
     *            the user entity ID.
     * @return the request for the saved activity mapper.
     */
    public Serializable transform(final JSONObject request, final Long userEntityId)
    {
        String accountId = request.getString("savedBy");

        Long requestAccountId = personMapper.fetchId(accountId);

        if (userEntityId.equals(requestAccountId))
        {
            return userEntityId;
        }
        else
        {
            log.debug("User was: " + userEntityId + " Request from: " + requestAccountId);
            throw new AuthorizationException("Insufficent priveledges to access stream.");
        }
    }
}
