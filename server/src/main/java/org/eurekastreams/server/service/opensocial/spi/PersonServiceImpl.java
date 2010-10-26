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
import java.util.Collections;
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
import org.apache.shindig.social.core.model.AccountImpl;
import org.apache.shindig.social.core.model.ListFieldImpl;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Account;
import org.apache.shindig.social.opensocial.model.ListField;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.GroupId.Type;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.action.principal.PrincipalPopulatorTransWrapper;
import org.eurekastreams.server.action.request.opensocial.GetPeopleByOpenSocialIdsRequest;
import org.eurekastreams.server.action.request.profile.GetFollowersFollowingRequest;
import org.eurekastreams.server.domain.AvatarUrlGenerator;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.search.modelview.PersonModelView;

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
     * Instance of the GetFollowingAction that is used to process Friends requests.
     */
    private final ServiceAction getFollowingAction;
    
    /**
     * Container base url to create profile url from.
     */
    private String containerBaseUrl;

    /** Top-level domain used for users' accounts. */
    private final String accountTopLevelDomain;
    
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
     * @param inGetFollowingAction
     *            - this is the GetFollowingAction that is injected into this class with Spring. This action is used to
     *            retrieve the friends of the requestor.
     * @param inOpenSocialPrincipalPopulator
     *            {@link PrincipalPopulatorTransWrapper}.
     * @param inServiceActionController
     *            {@link ServiceActionController}.
     * @param inContainerBaseUrl
     *            - string that contains the base url for the container to be used when generating links for an
     *            opensocial person.
     * @param inAccountTopLevelDomain
     *          Top-level domain used for users' accounts.
     */
    @Inject
    public PersonServiceImpl(@Named("getPeopleByOpenSocialIds") final ServiceAction inGetPeopleAction,
            @Named("getFollowing") final ServiceAction inGetFollowingAction,
            final PrincipalPopulatorTransWrapper inOpenSocialPrincipalPopulator,
            final ServiceActionController inServiceActionController,
            @Named("eureka.container.baseurl") final String inContainerBaseUrl,
            @Named("eureka.user-account-tld") final String inAccountTopLevelDomain)
    {
        getPeopleAction = inGetPeopleAction;
        getFollowingAction = inGetFollowingAction;
        containerBaseUrl = inContainerBaseUrl;
        principalPopulator = inOpenSocialPrincipalPopulator;
        serviceActionController = inServiceActionController;
        accountTopLevelDomain = inAccountTopLevelDomain;
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
        LinkedList<PersonModelView> people = null;
        try
        {
            if(groupId.getType().equals(Type.friends))
            {
                Principal currentPrincipal = getPrincipal(token);
                if(currentPrincipal == null)
                {
                    throw new IllegalArgumentException("Invalid requestor");
                }
                
                GetFollowersFollowingRequest currentRequest = 
                    new GetFollowersFollowingRequest(EntityType.PERSON, currentPrincipal.getAccountId(), 
                            0, Integer.MAX_VALUE);
                
                ServiceActionContext currentContext = 
                    new ServiceActionContext(currentRequest, currentPrincipal);
                
                PagedSet<PersonModelView> peopleResults = (PagedSet<PersonModelView>) serviceActionController.execute(
                        currentContext, getFollowingAction);
                
                people = new LinkedList<PersonModelView>(peopleResults.getPagedSet());
            }
            else
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
    
                GetPeopleByOpenSocialIdsRequest currentRequest =
                        new GetPeopleByOpenSocialIdsRequest(userIdList, groupId.getType().toString().toLowerCase());
                
                ServiceActionContext currentContext =
                        new ServiceActionContext(currentRequest, getPrincipal(token));
    
                people = (LinkedList<PersonModelView>) serviceActionController.execute(
                                currentContext, getPeopleAction);
            }
            
            if (log.isDebugEnabled())
            {
                log.debug("Retrieved " + people.size() + " people from action");
            }

            for (PersonModelView currentPerson : people)
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
    * - userid making the request.
    * @param fields
    * - set of fields to be retrieved with this request.
    * @param token
    * - token that goes with this request.
    *
    * @return instance of Person object
    */
        public Future<Person> getPerson(final UserId id, final Set<String> fields, final SecurityToken token)
        {
            log.trace("Entering getPerson");
            Person osPerson = new PersonImpl();

            String openSocialId = null;

            // Retrieve the user id.
            if (id.getUserId(token) != null)
            {
                openSocialId = id.getUserId(token);
            }
            else
            // userId is null and so is the type cannot proceed.
            {
                log.debug("Id of the person requested was null");
                throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST, "No id supplied");
            }

            try
            {
                log.debug("User id requested is: " + openSocialId);

                LinkedList<String> userIdList = new LinkedList<String>();
                userIdList.add(openSocialId);

                // Build up request to retrieve a single person.
                GetPeopleByOpenSocialIdsRequest currentRequest = new GetPeopleByOpenSocialIdsRequest(userIdList, Type.all
                        .toString());

                // Create the actionContext
                PrincipalActionContext ac = new ServiceActionContext(currentRequest, getPrincipal(token));

                // execute action.
                LinkedList<PersonModelView> people = (LinkedList<PersonModelView>) serviceActionController.execute(
                        (ServiceActionContext) ac, getPeopleAction);

                if (people.size() > 0)
                {
                    osPerson = convertToOSPerson(people.getFirst());
                }
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
    * Get Principal object for current user. Currently this method allows an unauthenticated request to retrieve
    * opensocial information about a user. The authentication is handled within shindig, not here. There may be a need
    * for an authorization strategy to not allow this access even if you have anonymous auth configured in shindig.
    * TODO: Put in authorization strategy for the underlying action.
    *
    * @param inSecurityToken
    * - current security token for the request.
    * @return Principal object based on the security token or null if an anonymous request.
    */
        private Principal getPrincipal(final SecurityToken inSecurityToken)
        {
            Principal currentUserPrincipal = null;
            if (inSecurityToken.getViewerId() != null)
            {
                currentUserPrincipal = principalPopulator.getPrincipal(inSecurityToken.getViewerId());
            }
            return currentUserPrincipal;
        }
    
    /**
    * Helper method that converts a passed in eurekastreams Person object into a Shindig Person object.
    *
    * @param inPerson
    * - eurekastreams person to be converted.
    * @return converted person object.
    */
        private Person convertToOSPerson(final PersonModelView inPerson)
        {
            Person osPerson = new PersonImpl();
            // Populate the OpenSocial person properties.
            osPerson.setName(new NameImpl(inPerson.getDisplayName()));
            osPerson.setDisplayName(inPerson.getDisplayName());
            osPerson.setId(inPerson.getOpenSocialId());
            osPerson.setAboutMe(inPerson.getDescription());
            osPerson.setProfileUrl(containerBaseUrl + "/#people/" + inPerson.getAccountId());

            List<ListField> emailList = new ArrayList<ListField>();
            emailList.add(new ListFieldImpl("primary", inPerson.getEmail()));
            osPerson.setEmails(emailList);

            AvatarUrlGenerator generator = new AvatarUrlGenerator(EntityType.PERSON);
            osPerson.setThumbnailUrl(containerBaseUrl
                    + generator.getSmallAvatarUrl(inPerson.getId(), inPerson.getAvatarId()));

            osPerson.setAccounts(Collections.singletonList((Account) new AccountImpl(accountTopLevelDomain, null, inPerson
                    .getAccountId())));

            return osPerson;
        }
}
