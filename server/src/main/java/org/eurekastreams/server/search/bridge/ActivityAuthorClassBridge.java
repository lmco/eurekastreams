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
package org.eurekastreams.server.search.bridge;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.hibernate.search.bridge.StringBridge;

/**
 * Class bridge to get for activity author.
 */
public class ActivityAuthorClassBridge implements StringBridge
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Mapper to lookup group ids by short names.
     */
    private static GetDomainGroupsByShortNames getDomainGroupsByShortNames;

    /**
     * Mapper to lookup people ids by account ids.
     */
    private static DomainMapper<String, Long> getPersonIdByAccountIdMapper;

    /**
     * Convert the input Message or Activity object into an ID representing the author.
     *
     * @param msgObject
     *            the Message or Activity
     * @return the input Message object with the ID of the author.
     */
    @Override
    public String objectToString(final Object msgObject)
    {
        Activity activity = (Activity) msgObject;
        EntityType actorType = activity.getActorType();

        String result = null;

        switch (actorType)
        {
        case GROUP:
            result = "g" + getDomainGroupsByShortNames.fetchId(activity.getActorId());
            break;
        case PERSON:
            result = "p" + getPersonIdByAccountIdMapper.execute(activity.getActorId());
            break;
        default:
            throw new RuntimeException("Unknown/unhandled recipient type: " + actorType);
        }

        if (log.isInfoEnabled())
        {
            log.info("Bridge converted activity with actorId " + activity.getActorId() + " to " + result);
        }
        return result;
    }

    /**
     * @param inGetDomainGroupsByShortNames
     *            the getDomainGroupsByShortNames to set
     */
    public static void setGetDomainGroupsByShortNames(final GetDomainGroupsByShortNames inGetDomainGroupsByShortNames)
    {
        ActivityAuthorClassBridge.getDomainGroupsByShortNames = inGetDomainGroupsByShortNames;
    }

    /**
     * Set the mapper to lookup person ids from account ids.
     *
     * @param inGetPersonIdByAccountIdMapper
     *            mapper to get person id from accountid for a person
     */
    public static void setGetPersonIdByAccountIdMapper(final DomainMapper<String, Long> inGetPersonIdByAccountIdMapper)
    {
        ActivityAuthorClassBridge.getPersonIdByAccountIdMapper = inGetPersonIdByAccountIdMapper;
    }
}
