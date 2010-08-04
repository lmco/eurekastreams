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

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;

/**
 * Turn followedBy;jsmith to his DB id.
 *
 */
public class FollowedByPersistenceRequestTransformer implements PersistenceDataSourceRequestTransformer
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
     * Default constructor.
     *
     * @param inPersonMapper
     *            person mapper.
     */
    public FollowedByPersistenceRequestTransformer(final GetPeopleByAccountIds inPersonMapper)
    {
        personMapper = inPersonMapper;
    }


    /**
     * Transform JSON to Long.
     * @param request the JSON request.
     * @return the DB id of the user.
     */
    @Override
    public Long transform(final JSONObject request)
    {
        String accountId = request.getString("followedBy");

        log.info("Looking for cache key for activities followed by " + accountId);

        return personMapper.fetchUniqueResult(accountId).getId();

    }

}
