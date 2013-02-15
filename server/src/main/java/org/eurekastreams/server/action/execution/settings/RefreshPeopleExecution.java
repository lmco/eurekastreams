/*
 * Copyright (c) 2010-2013 Lockheed Martin Corporation
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

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.CreatePersonRequest;
import org.eurekastreams.server.action.request.SetPersonLockedStatusRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;

/**
 * Use {@link PersonSource} to create/lock/unlock user accounts.
 * 
 */
public class RefreshPeopleExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Source of person information for users.
     */
    private final PersonSource source;

    /**
     * Action key for create action to be called for new user.
     */
    private String createPersonActionKey = null;

    /**
     * Action key for set status action to be called for users.
     */
    private String lockPersonActionKey = null;

    /**
     * Action key for refresh action to be called for users.
     */
    private String refreshPersonActionKey = null;

    /**
     * {@link GetNonLockedPersonIds}.
     */
    private final DomainMapper<Boolean, List<String>> personIdsByLockedStatusDAO;

    /**
     * The settings mapper.
     */
    private final DomainMapper<MapperRequest, SystemSettings> settingsMapper;

    /**
     * Constructor.
     * 
     * @param inSource
     *            {@link PersonSource}.
     * @param inCreatePersonActionKey
     *            Action key for create action.
     * @param inLockPersonAccountActionKey
     *            action key for lock/unlock action.
     * @param inRefreshPersonActionKey
     *            action key for refresh action.
     * @param inGetPersonIdsByLockedStatus
     *            Gets locked/unlocked users (GetPersonAccountIdsByLockedStatus).
     * @param inSettingsMapper
     *            {@link FindSystemSettings}.
     */
    public RefreshPeopleExecution(final PersonSource inSource, final String inCreatePersonActionKey,
            final String inLockPersonAccountActionKey, final String inRefreshPersonActionKey,
            final DomainMapper<Boolean, List<String>> inGetPersonIdsByLockedStatus,
            final DomainMapper<MapperRequest, SystemSettings> inSettingsMapper)
    {
        source = inSource;
        createPersonActionKey = inCreatePersonActionKey;
        lockPersonActionKey = inLockPersonAccountActionKey;
        refreshPersonActionKey = inRefreshPersonActionKey;
        personIdsByLockedStatusDAO = inGetPersonIdsByLockedStatus;
        settingsMapper = inSettingsMapper;
    }

    /**
     * Get updated info for all users of system and generate UserActionRequests to refresh them.
     * 
     * @param inActionContext
     *            {@link TaskHandlerActionContext}.
     * @return null.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        // Get all current users that should be in system
        log.info("Getting users from PersonSource ids...");
        Long start = System.currentTimeMillis();
        Set<Person> people = source.getPeople();
        Long time = System.currentTimeMillis() - start;
        log.info("Found " + people.size() + " users to refresh (" + time + "ms).");

        // Get locked/unlocked user ids
        log.info("Getting all unlocked user ids...");
        Set<String> unLockedUserAccountIds = new HashSet<String>(personIdsByLockedStatusDAO.execute(false));
        log.info("Found " + unLockedUserAccountIds.size() + " currently unlocked user accounts.");

        log.info("Getting all locked user ids...");
        Set<String> lockedUserAccountIds = new HashSet<String>(personIdsByLockedStatusDAO.execute(true));
        log.info("Found " + lockedUserAccountIds.size() + " currently locked user accounts.");

        // determine if userActionRequests should be queued.
        boolean queueCreatePerson = createPersonActionKey != null && !createPersonActionKey.isEmpty();
        boolean queueLockAccounts = lockPersonActionKey != null && !lockPersonActionKey.isEmpty();

        // get system setting to determine if emails should be sent to created users.
        SystemSettings settings = settingsMapper.execute(null);
        boolean shouldSendEmail = settings.getSendWelcomeEmails();

        // counts for summary log.
        int toLock = 0;
        int toUnlock = 0;
        int toCreate = 0;

        for (Person p : people)
        {
            String acctId = p.getAccountId();

            if (lockedUserAccountIds.contains(acctId))
            {
                if (log.isInfoEnabled())
                {
                    log.info("Found user AcctId: " + acctId + " (" + p.getDisplayName() + ") to be unlocked.");
                }

                // Queue action to refresh user info from AD - even if we're going to unlock the person, we should still
                // update the database from LDAP
                inActionContext.getUserActionRequests().add(new UserActionRequest(refreshPersonActionKey, null, p));

                if (queueLockAccounts)
                {
                    inActionContext.getUserActionRequests().add(
                            new UserActionRequest(lockPersonActionKey, null, new SetPersonLockedStatusRequest(acctId,
                                    false)));
                }
                toUnlock++;
            }
            else if (unLockedUserAccountIds.contains(acctId))
            {
                log.debug("Queuing up a refresh of person: " + acctId);

                // Queue action to refresh user info from AD
                inActionContext.getUserActionRequests().add(new UserActionRequest(refreshPersonActionKey, null, p));

                // remove from unlocked list, when done looping remaining ids will be locked.
                unLockedUserAccountIds.remove(acctId);
            }
            else
            {
                if (log.isInfoEnabled())
                {
                    log.info("Found user id: " + acctId + " (" + p.getDisplayName() + ") to be created.");
                }
                if (queueCreatePerson)
                {
                    inActionContext.getUserActionRequests().add(
                            new UserActionRequest(createPersonActionKey, null, new CreatePersonRequest(p,
                                    shouldSendEmail)));
                }
                toCreate++;
            }
        }

        // Everyone that hasn't been removed from the unLockedUserAccountIds collection by this point needs to be
        // locked.
        toLock = unLockedUserAccountIds.size();
        log.info("Determined there are " + unLockedUserAccountIds.size() + " user accounts to lock.");
        for (String id : unLockedUserAccountIds)
        {
            if (log.isInfoEnabled())
            {
                log.info("Found user AcctId: " + id + " to be locked.");
            }
            if (queueLockAccounts)
            {
                inActionContext.getUserActionRequests().add(
                        new UserActionRequest(lockPersonActionKey, null, new SetPersonLockedStatusRequest(id, true)));
            }
        }

        log.info("Summary: Lock: " + toLock + " unlock: " + toUnlock + " Create: " + toCreate
                + ". LOCK-UNLOCK ENABLED: " + queueLockAccounts + " CREATE ENABLED: " + queueCreatePerson);
        return null;
    }
}
