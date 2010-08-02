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

package org.eurekastreams.server.persistence.mappers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.action.execution.CreatePersonActionFactory;
import org.eurekastreams.server.action.execution.PersistResourceExecution;
import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.eurekastreams.server.service.actions.strategies.EmailerFactory;
import org.eurekastreams.server.service.actions.strategies.PersonLookupStrategy;
import org.eurekastreams.server.service.actions.strategies.ReflectiveUpdater;

/**
 * This class is used to update system access based on configured membership criteria.
 */
public class RefreshMembership extends BaseDomainMapper
{
    /**
     * Local logger instance.
     */
    private final Log logger = LogFactory.getLog(RefreshMembership.class);

    /**
     * Stopwatch for timing.
     */
    private StopWatch stopWatch;

    /**
     * The settings mapper.
     */
    private DomainMapper<MapperRequest, SystemSettings> settingsMapper;

    /**
     * OrganizationMapper object.
     */
    private OrganizationMapper organizationMapper;

    /**
     * Person mapper.
     */
    private PersonMapper personMapper;

    /**
     * Group lookup strategy.
     */
    private PersonLookupStrategy groupLookupStrategy;

    /**
     * Attribute-based lookup strategy.
     */
    private PersonLookupStrategy attributeLookupStrategy;

    /**
     * Persist resource action.
     */
    private PersistResourceExecution<Person> persistResourceExecution;

    /**
     * Factory to create person.
     */
    private CreatePersonActionFactory createPersonActionFactory;

    /**
     * Emailer factory that sends email.
     */
    private EmailerFactory emailerFactory;

    /**
     * Base URL for the application.
     */
    private String baseUrl;

    // TODO Subject and body need be configurable eventually or at least stored in a config file.
    /**
     * Subject Body for Requesting access to a domain group.
     */
    private static final String SUBJECT = "Welcome to Eureka Streams";

    /**
     * Plain Text message Body For requesting Access to a DomainGroup.
     */
    private static final String BODY = "Congratulations! You have just been given access to Eureka Streams. "
            + "Eureka streams enables knowledge workers to make informed, timely decisions by finding "
            + "relevant colleagues and groups, following their stream of activity, and engaging in conversation.<br/>"
            + "<a href='@url'>Set up your profile</a><br/><br/>" + "Thank you,<br/>Eureka Product Team";

    /**
     * Constructor.
     *
     * @param inSettingsMapper
     *            mapper to get system settings.
     * @param inOrganizationMapper
     *            mapper to get organization data.
     * @param inPersonMapper
     *            mapper to get people.
     * @param inGroupLookupStrategy
     *            group lookup mapper.
     * @param inAttributeLookupStrategy
     *            person lookup mapper.
     * @param inCreatePersonActionFactory
     *            action factory persist user updates.
     * @param inEmailerFactory
     *            email factory to notify new users.
     * @param inBaseUrl
     *            the base url of the system.
     */
    public RefreshMembership(final DomainMapper<MapperRequest, SystemSettings> inSettingsMapper,
            final OrganizationMapper inOrganizationMapper, final PersonMapper inPersonMapper,
            final PersonLookupStrategy inGroupLookupStrategy, final PersonLookupStrategy inAttributeLookupStrategy,
            final CreatePersonActionFactory inCreatePersonActionFactory, final EmailerFactory inEmailerFactory,
            final String inBaseUrl)
    {
        settingsMapper = inSettingsMapper;
        organizationMapper = inOrganizationMapper;
        personMapper = inPersonMapper;
        groupLookupStrategy = inGroupLookupStrategy;
        attributeLookupStrategy = inAttributeLookupStrategy;
        createPersonActionFactory = inCreatePersonActionFactory;
        emailerFactory = inEmailerFactory;
        baseUrl = inBaseUrl;

        stopWatch = new StopWatch();
    }

    /**
     * Updates system membership/permissions. For each person in the given membershipCriteria system setting, add the
     * user if not already in the system, unlock the user if previously locked, and lock if an existing user is no
     * longer in the users specified by the criteria.
     *
     * @throws Exception
     *             on error.
     */
    @SuppressWarnings("unchecked")
    public void execute() throws Exception
    {
        logger.info("Starting membership refresh task.");
        stopWatch.reset();
        stopWatch.start();

        persistResourceExecution = createPersonActionFactory.getCreatePersonAction(personMapper,
                new ReflectiveUpdater());

        SystemSettings settings = settingsMapper.execute(null);
        List<MembershipCriteria> membershipCriteria = settings.getMembershipCriteria();

        boolean shouldSendEmail = settings.getSendWelcomeEmails();

        PersonLookupStrategy lookupStrategy = null;
        Set<Long> validPeopleIds = new HashSet<Long>();

        for (MembershipCriteria criterion : membershipCriteria)
        {
            String ldapQuery = criterion.getCriteria();
            logger.info("Processing criteria: " + ldapQuery);

            if (ldapQuery.contains("="))
            {
                lookupStrategy = attributeLookupStrategy;
            }
            else
            {
                lookupStrategy = groupLookupStrategy;
            }

            List<Person> people = lookupStrategy.findPeople(ldapQuery, new Integer(Integer.MAX_VALUE));
            logger.info(people.size() + " people found for criteria.");

            Organization rootOrganization = null;
            for (Person person : people)
            {
                try
                {
                    String accountId = person.getAccountId();
                    Person existingPerson = personMapper.findByAccountId(accountId);

                    if (existingPerson == null)
                    {
                        // The person does not yet exist
                        logger.info("New user found, adding to database: " + accountId);
                        final HashMap<String, Serializable> personData = person.getProperties(Boolean.FALSE);
                        if (rootOrganization == null)
                        {
                            rootOrganization = organizationMapper.getRootOrganization();
                        }
                        personData.put("organization", rootOrganization);

                        Person newPerson = (Person) persistResourceExecution
                                .execute(new TaskHandlerActionContext<PrincipalActionContext>(
                                        new PrincipalActionContext()
                                        {
                                            private static final long serialVersionUID = 9196683601970713330L;

                                            @Override
                                            public Principal getPrincipal()
                                            {
                                                throw new
                                                // line break
                                                RuntimeException("No principal available for this execution.");
                                            }

                                            @Override
                                            public Serializable getParams()
                                            {
                                                return personData;
                                            }

                                            @Override
                                            public Map<String, Object> getState()
                                            {
                                                return null;
                                            }
                                        }, null));

                        validPeopleIds.add(newPerson.getId());

                        // Send email notification if necessary
                        if (shouldSendEmail)
                        {
                            notifyUser(newPerson.getEmail(), newPerson.getAccountId());
                        }
                    }
                    else
                    {
                        validPeopleIds.add(existingPerson.getId());

                        if (existingPerson.isAccountLocked())
                        {
                            logger.info("Unlocking account for user: " + accountId);
                            existingPerson.setAccountLocked(false);
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.error("Error processing person: " + person.getAccountId());
                }
            }
        }

        // Checks to see if any valid ids were found. If not, there was most likely an ldap issue and the
        // account locking procedure will lock all users unnecessarily.
        if (!validPeopleIds.isEmpty())
        {
            List<Long> allPeopleIds = getEntityManager().createQuery(
                    "SELECT id from Person where accountLocked = false").getResultList();
            List<Long> lockedPeopleIds = new ArrayList<Long>();

            for (long id : allPeopleIds)
            {
                if (!validPeopleIds.contains(id))
                {
                    lockedPeopleIds.add(id);
                }
            }

            for (long id : lockedPeopleIds)
            {
                Person person = personMapper.findById(id);
                person.setAccountLocked(true);
                logger.info("Locking account for user: " + person.getAccountId());
            }
        }

        personMapper.flush();
        stopWatch.stop();
        logger.info("Membership refresh task completed, elapsed time: "
                + DurationFormatUtils.formatDurationHMS(stopWatch.getTime()));
    }

    /**
     * Sends a welcome email to a new user.
     *
     * @param emailAddress
     *            the user's email address.
     * @param accountId
     *            the user's account id.
     */
    protected void notifyUser(final String emailAddress, final String accountId)
    {
        if (emailAddress != null && emailAddress != "")
        {
            try
            {
                MimeMessage msg = emailerFactory.createMessage();
                emailerFactory.setTo(msg, emailAddress);
                emailerFactory.setSubject(msg, SUBJECT);
                emailerFactory.setHtmlBody(msg, BODY.replace("@url", baseUrl + "#people/" + accountId));
                emailerFactory.sendMail(msg);
                logger.info("New user email sent to: " + emailAddress);
            }
            catch (MessagingException ex)
            {
                logger.error("Failed to send new user email to: " + emailAddress);
            }
        }
    }
}
