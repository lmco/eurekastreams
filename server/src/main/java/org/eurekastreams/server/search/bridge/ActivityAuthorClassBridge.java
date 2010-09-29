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

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.hibernate.search.bridge.StringBridge;

/**
 * Class bridge to get for activity author.
 */
public class ActivityAuthorClassBridge implements StringBridge
{
    /**
     * Mapper to lookup group ids by short names.
     */
    private static GetDomainGroupsByShortNames getDomainGroupsByShortNames;

    /**
     * Mapper to lookup person ids by account ids.
     */
    private static GetPeopleByAccountIds getPeopleByAccountIds;

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
        switch (actorType)
        {
        case GROUP:
            return "g" + getDomainGroupsByShortNames.fetchId(activity.getActorId());
        case PERSON:
            return "p" + getPeopleByAccountIds.fetchId(activity.getActorId());
        default:
            throw new RuntimeException("Unknown/unhandled recipient type: " + actorType);
        }
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
     * @param inGetPeopleByAccountIds
     *            the getPeopleByAccountIds to set
     */
    public static void setGetPeopleByAccountIds(final GetPeopleByAccountIds inGetPeopleByAccountIds)
    {
        ActivityAuthorClassBridge.getPeopleByAccountIds = inGetPeopleByAccountIds;
    }
}
