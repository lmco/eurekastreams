/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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

import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.SharedResource;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.hibernate.search.bridge.StringBridge;

/**
 * Class bridge to get a recipient code for this message in the form [G|P][ID] - for person with the ID of 123, the code
 * will be P123. For group with ID 456, the indexed value is G456.
 */
public class ActivityRecipientClassBridge implements StringBridge
{
    /**
     * Mapper to lookup group ids by short names.
     */
    private static GetDomainGroupsByShortNames getDomainGroupsByShortNames;

    /**
     * Mapper to lookup people id by account id.
     */
    private static DomainMapper<String, Long> getPersonIdByAccountIdMapper;

    /**
     * Mapper to lookup SharedResource by unique key.
     */
    private static DomainMapper<SharedResourceRequest, SharedResource> getSharedResourceByUniqueKeyMapper;

    /**
     * Convert the input Message or Activity object into an ID representing the recipient, either a person or domain
     * group. Both are represented as their ID, prefixed with "P" for person, "G" for group. This bridge temporarily
     * handles both Activity and Message entities during the transition phase from Message to Activity. Message support
     * will be removed when the entity is.
     * 
     * @param msgObject
     *            the Message or Activity
     * @return the input Message object into an ID representing the recipient, either a person or domain group - the id
     *         prefixed with "P" or "G" for person or group
     */
    @Override
    public String objectToString(final Object msgObject)
    {
        if (getPersonIdByAccountIdMapper == null)
        {
            throw new RuntimeException("Person Cache was not set in the MessageRecipientIdClassBridge.");
        }

        if (getDomainGroupsByShortNames == null)
        {
            throw new RuntimeException("getDomainGroupsByShortNames was not set in the MessageRecipientIdClassBridge.");
        }

        Activity activity = (Activity) msgObject;
        StreamScope scope = activity.getRecipientStreamScope();
        switch (scope.getScopeType())
        {
        case GROUP:
            return "g" + getDomainGroupsByShortNames.fetchId(scope.getUniqueKey());
        case PERSON:
            return "p" + getPersonIdByAccountIdMapper.execute(scope.getUniqueKey());
        case RESOURCE:
            return "r" + getSharedResourceByUniqueKeyMapper.execute(//
                    new SharedResourceRequest(scope.getUniqueKey())).getId();
        default:
            throw new RuntimeException("Unknown/unhandled recipient type: " + scope.getScopeType());
        }
    }

    /**
     * @param inGetDomainGroupsByShortNames
     *            the getDomainGroupsByShortNames to set
     */
    public static void setGetDomainGroupsByShortNames(final GetDomainGroupsByShortNames inGetDomainGroupsByShortNames)
    {
        ActivityRecipientClassBridge.getDomainGroupsByShortNames = inGetDomainGroupsByShortNames;
    }

    /**
     * @param inGetPersonIdByAccountIdMapper
     *            the mapper to get person id by account id
     */
    public static void setGetPersonIdByAccountIdMapper(final DomainMapper<String, Long> inGetPersonIdByAccountIdMapper)
    {
        ActivityRecipientClassBridge.getPersonIdByAccountIdMapper = inGetPersonIdByAccountIdMapper;
    }

    /**
     * @param inGetSharedResourceByUniqueKeyMapper
     *            the mapper to get SharedResource by key.
     */
    public static void setGetSharedResourceByUniqueKeyMapper(
            final DomainMapper<SharedResourceRequest, SharedResource> inGetSharedResourceByUniqueKeyMapper)
    {
        ActivityRecipientClassBridge.getSharedResourceByUniqueKeyMapper = inGetSharedResourceByUniqueKeyMapper;
    }

}
