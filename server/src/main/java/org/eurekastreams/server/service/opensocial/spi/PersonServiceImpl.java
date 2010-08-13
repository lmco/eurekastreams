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
package org.eurekastreams.server.service.opensocial.spi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.action.principal.PrincipalPopulatorTransWrapper;
import org.eurekastreams.server.action.request.opensocial.GetPeopleByOpenSocialIdsRequest;
import org.eurekastreams.server.domain.AvatarUrlGenerator;
import org.eurekastreams.server.domain.EntityType;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * This class is the Eureka Streams implementation of the Shindig interface for retrieving OpenSocial information about
 * People.
 * 
 */
public class PersonServiceImpl implements PersonService
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(PersonServiceImpl.class);

    /**
     * Get person action.
     */
    private ServiceAction getPersonAction;

    /**
     * Service Action Controller.
     */
    private ServiceActionController serviceActionController;

    /**
     * Principal populator.
     */
    private PrincipalPopulatorTransWrapper principalPopulator;

    /**
     * Instance of the GetPersonAction that is used to process Person requests.
     */
    private ServiceAction getPeopleAction;

    /**
     * Container base url to create profile url from.
     */
    private String containerBaseUrl;

    /**
     * Basic constructor for the PersonService implementation.
     * 
     * @param inGetPersonAction
     *            - this is the GetPersonAction that is injected into this class with Spring. By injecting with spring
     *            we can maintain the transaction nature of the Actions even in Shindig where Guice is used to wire up
     *            this implementation.
     * @param inGetPeopleAction
     *            - this is the GetPeopleAction that is injected into this class with Spring. This action is used to
     *            retrieve multiple person objects in a single request.
     * @param inOpenSocialPrincipalPopulator
     *            {@link PrincipalPopulatorTransWrapper}.
     * @param inServiceActionController
     *            {@link ServiceActionController}.
     * @param inContainerBaseUrl
     *            - string that contains the base url for the container to be used when generating links for an
     *            opensocial person.
     */
    @Inject
    public PersonServiceImpl(@Named("getPersonNoContext") final ServiceAction inGetPersonAction,
            @Named("getPeopleByOpenSocialIds") final ServiceAction inGetPeopleAction,
            final PrincipalPopulatorTransWrapper inOpenSocialPrincipalPopulator,
            final ServiceActionController inServiceActionController,
            @Named("eureka.container.baseurl") final String inContainerBaseUrl)
    {
        getPersonAction = inGetPersonAction;
        getPeopleAction = inGetPeopleAction;
        containerBaseUrl = inContainerBaseUrl;
        principalPopulator = inOpenSocialPrincipalPopulator;
        serviceActionController = inServiceActionController;
    }

    /**
     * This is the implementation method to retrieve a number of people generally associated with a group or by a set of
     * userids.
     * 
     * @param userIds
     *            - set of userids to retrieve.
     * @param groupId
     *            - group id to retrieve.
     * @param collectionOptions
     *            - collection options.
     * @param fields
     *            - fields to retrieve with these users.
     * @param token
     *            - security token for this request.
     * 
     * @return instance of person
     * 
     */
    @SuppressWarnings("unchecked")
    public Future<RestfulCollection<Person>> getPeople(final Set<UserId> userIds, final GroupId groupId,
            final CollectionOptions collectionOptions, final Set<String> fields, final SecurityToken token)
    {
        log.trace("Entering getPeople");
        List<Person> osPeople = new ArrayList<Person>();
        try
        {
            LinkedList<String> userIdList = new LinkedList<String>();
            for (UserId currentUserId : userIds)
            {
                if (!currentUserId.getUserId(token).equals("null"))
                {
                    userIdList.add(currentUserId.getUserId(token));
                }
            }

            log.debug("Sending getPeople userIdList to action: " + userIdList.toString());

            GetPeopleByOpenSocialIdsRequest currentRequest = new GetPeopleByOpenSocialIdsRequest(userIdList, groupId
                    .getType().toString().toLowerCase());
            ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalPopulator
                    .getPrincipal(token.getViewerId()));

            LinkedList<org.eurekastreams.server.domain.Person> people = 
                (LinkedList<org.eurekastreams.server.domain.Person>) serviceActionController
                    .execute(currentContext, getPeopleAction);

            log.debug("Retrieved " + people.size() + " people from action");

            for (org.eurekastreams.server.domain.Person currentPerson : people)
            {
                osPeople.add(convertToOSPerson(currentPerson));
            }
        }
        catch (Exception ex)
        {
            log.error("Error occurred retrieving people ", ex);
            throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }

        return ImmediateFuture.newInstance(new RestfulCollection<Person>(osPeople));
    }

    /**
     * This is the implementation of the getPerson method specified by Shindig. This is how Shindig's OpenSocial api
     * will interact with our database.
     * 
     * @param id
     *            - userid making the request.
     * @param fields
     *            - set of fields to be retrieved with this request.
     * @param token
     *            - token that goes with this request.
     * 
     * @return instance of Person object
     */
    public Future<Person> getPerson(final UserId id, final Set<String> fields, final SecurityToken token)
    {
        log.trace("Entering getPerson");
        Person osPerson = new PersonImpl();

        org.eurekastreams.server.domain.Person currentPerson;

        // Id is null, cannot proceed.
        if (id.getUserId(token).equals("null"))
        {
            log.debug("Id of the person requested was null");
            throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST, "No id supplied");
        }

        try
        {
            String openSocialId = id.getUserId(token);
            log.debug("User id requested is: " + openSocialId);

            // Get Principal object for current user.
            Principal currentUserPrincipal = principalPopulator.getPrincipal(openSocialId);

            // Create the actionContext
            PrincipalActionContext ac = new ServiceActionContext(currentUserPrincipal.getAccountId(),
                    currentUserPrincipal);

            // execute action.
            currentPerson = (org.eurekastreams.server.domain.Person) serviceActionController.execute(
                    (ServiceActionContext) ac, getPersonAction);

            osPerson = convertToOSPerson(currentPerson);
        }
        catch (NumberFormatException e)
        {
            log.error("number format exception " + e.getMessage());

            throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST, "Id supplied is bad.");
        }
        catch (Exception e)
        {
            log.error("Error occurred retrieving person " + e.getMessage());

            throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return ImmediateFuture.newInstance(osPerson);
    }

    /**
     * Helper method that converts a passed in eurekastreams Person object into a Shindig Person object.
     * 
     * @param inPerson
     *            - eurekastreams person to be converted.
     * @return converted person object.
     */
    private Person convertToOSPerson(final org.eurekastreams.server.domain.Person inPerson)
    {
        Person osPerson = new PersonImpl();
        // Populate the OpenSocial person properties.
        osPerson.setName(new NameImpl(inPerson.getFirstName() + " " + inPerson.getLastName()));
        osPerson.setDisplayName(inPerson.getPreferredName());
        osPerson.setId(inPerson.getOpenSocialId());
        osPerson.setAboutMe(inPerson.getBiography());
        osPerson.setProfileUrl(containerBaseUrl + "/#profile/" + inPerson.getAccountId());
        AvatarUrlGenerator generator = new AvatarUrlGenerator(EntityType.PERSON);

        osPerson.setThumbnailUrl(containerBaseUrl
                + generator.getSmallAvatarUrl(inPerson.getId(), inPerson.getAvatarId()));
        return osPerson;
    }
}
