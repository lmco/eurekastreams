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
import java.util.ArrayList;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;

/**
 * Transforms JSON request to a request for a single person by ID.
 */
public class SingleUserPersistenceRequestTransformer implements PersistenceDataSourceRequestTransformer
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Person mapper.
     */
    private GetPeopleByAccountIds personMapper;

    /**
     * The request key.
     */
    private String reqKey;

    /**
     * If the single result should be returned as an array (necessary to support certain mappers).
     */
    private Boolean returnAsArray;

    /**
     * If this is only valid for the current user.
     */
    private Boolean requireCurrentUser;

    /**
     * Default constructor.
     * 
     * @param inPersonMapper
     *            person mapper.
     * @param inReqKey
     *            the relevant request key.
     * @param inReturnAsArray
     *            if the single result should be returned as an array (necessary to support certain mappers).
     * @param inRequireCurrentUser
     *            if this is only valid for the current user.
     */
    public SingleUserPersistenceRequestTransformer(final GetPeopleByAccountIds inPersonMapper, final String inReqKey,
            final Boolean inReturnAsArray, final Boolean inRequireCurrentUser)
    {
        personMapper = inPersonMapper;
        reqKey = inReqKey;
        returnAsArray = inReturnAsArray;
        requireCurrentUser = inRequireCurrentUser;
    }

    /**
     * Transforms JSON request to a request for a single person by ID.
     * 
     * @param request
     *            the JSON request.
     * @param userEntityId
     *            the user entity ID.
     * @return the request for the saved activity mapper.
     */
    public Serializable transform(final JSONObject request, final Long userEntityId)
    {
        String accountId = request.getString(reqKey);

        Long requestAccountId = personMapper.fetchId(accountId);

        // If it doesn't require the current user, or the request is for the current user.
        if (!requireCurrentUser || userEntityId.equals(requestAccountId))
        {
            if (!returnAsArray)
            {
                return requestAccountId;
            }
            else
            {
                // Has to be a specific list implementation to satisfy Serializable.
                final ArrayList<Long> userList = new ArrayList<Long>();
                userList.add(requestAccountId);

                return userList;
            }
        }
        else
        {
            if (log.isDebugEnabled())
            {
                log.debug("User was: " + userEntityId + " Request from: " + requestAccountId);
            }
            
            return 0L;
        }
    }
}
