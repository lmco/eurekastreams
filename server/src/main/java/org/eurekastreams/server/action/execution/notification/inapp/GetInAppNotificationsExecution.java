/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.notification.inapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.AvatarEntity;
import org.eurekastreams.server.domain.InAppNotificationDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.GetItemsByPointerIdsMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetItemsByPointerIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This strategy gets all application alerts for a given user up to a configured max number.
 */
public class GetInAppNotificationsExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /** Logger. */
    private static Logger log = LoggerFactory.getLogger(LogFactory.getClassName());

    /** Mapper to get in-app notifs. */
    private final BaseArgDomainMapper<Long, List<InAppNotificationDTO>> alertMapper;

    /** Mapper to get persons. */
    private final GetItemsByPointerIdsMapper<String, PersonModelView> personsMapper;

    /** Mapper to get groups. */
    private final GetItemsByPointerIds<DomainGroupModelView> groupsMapper;

    /** Provides the category for each notification type. */
    private final Map<NotificationType, String> notificationTypeToCategory;

    /**
     * Constructor.
     *
     * @param inAlertMapper
     *            the alert mapper to set.
     * @param inPersonsMapper
     *            Mapper to get persons.
     * @param inGroupsMapper
     *            Mapper to get groups.
     * @param inNotificationTypeCategories
     *            Map providing the category for each notification type.
     */
    public GetInAppNotificationsExecution(final BaseArgDomainMapper<Long, List<InAppNotificationDTO>> inAlertMapper,
            final GetItemsByPointerIdsMapper<String, PersonModelView> inPersonsMapper,
            final GetItemsByPointerIds<DomainGroupModelView> inGroupsMapper,
            final Map<NotificationType, String> inNotificationTypeCategories)
    {
        alertMapper = inAlertMapper;
        personsMapper = inPersonsMapper;
        groupsMapper = inGroupsMapper;
        notificationTypeToCategory = inNotificationTypeCategories;
    }

    /**
     * {@inheritDoc} This method calls a mapper to retrieve all application alerts for the current user (up to a
     * specifiec max count).
     */
    @Override
    @SuppressWarnings("unchecked")
    public Serializable execute(final PrincipalActionContext inActionContext)
    {
        // ---- get the notifications ----
        long userId = inActionContext.getPrincipal().getId();
        List<InAppNotificationDTO> results = alertMapper.execute(userId);

        if (results.isEmpty())
        {
            return (Serializable) Collections.EMPTY_LIST;
        }

        // ---- add "live" data (not stored in database) ----

        // set filter category
        for (InAppNotificationDTO item : results)
        {
            item.setFilterCategory(notificationTypeToCategory.get(item.getNotificationType()));
        }

        // -- get avatar IDs (stale ones cause a 404 so prevent that) --
        Map<String, PersonModelView> personLookup = new HashMap<String, PersonModelView>();
        Map<String, DomainGroupModelView> groupLookup = new HashMap<String, DomainGroupModelView>();

        // build lists of needed entities
        for (InAppNotificationDTO notif : results)
        {
            switch (notif.getAvatarOwnerType())
            {
            case PERSON:
                personLookup.put(notif.getAvatarOwnerUniqueId(), null);
                break;
            case GROUP:
                groupLookup.put(notif.getAvatarOwnerUniqueId(), null);
                break;
            default:
                int makeCheckstyleShutUpBecauseTheresNothingToDoHere = 1;
            }
        }
        // fetch and store
        for (PersonModelView person : personsMapper.execute(new ArrayList(personLookup.keySet())))
        {
            personLookup.put(person.getAccountId(), person);
        }
        for (DomainGroupModelView group : groupsMapper.execute(new ArrayList(groupLookup.keySet())))
        {
            groupLookup.put(group.getUniqueId(), group);
        }
        // lookup
        for (InAppNotificationDTO notif : results)
        {
            AvatarEntity entity = null;
            switch (notif.getAvatarOwnerType())
            {
            case PERSON:
                entity = personLookup.get(notif.getAvatarOwnerUniqueId());
                break;
            case GROUP:
                entity = groupLookup.get(notif.getAvatarOwnerUniqueId());
                break;
            default:
                int makeCheckstyleShutUpBecauseTheresNothingToDoHere = 1;
            }
            if (entity != null && entity.getAvatarId() != null)
            {
                notif.setAvatarId(entity.getAvatarId());
            }
        }

        log.trace("Found {} notifications for user {}", results.size(), userId);

        return new ArrayList(results);
    }
}
