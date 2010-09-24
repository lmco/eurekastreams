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
package org.eurekastreams.server.service.actions.strategies;

import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.CreatePersonActionFactory;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverser;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the CreateOrganizationAction.
 */
@SuppressWarnings("unchecked")
public class OrganizationCreatorTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * The current user's account id.
     */
    private String accountId = "sdlkfjsdlfjs";


    /**
     * task handler action context.
     */
    private TaskHandlerActionContext<PrincipalActionContext> taskHandlerActionContext = context
            .mock(TaskHandlerActionContext.class);

    /**
     * Action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);
    /**
     * Collection to hold action requests queued up for async processing.
     */
    private List<UserActionRequest> userActionRequests = new ArrayList<UserActionRequest>();

    /**
     * Current user principal.
     */
    private Principal userPrincipal = context.mock(Principal.class);

    /**
     * The mock org mapper to be used by the action.
     */
    private OrganizationMapper orgMapperMock = context.mock(OrganizationMapper.class);

    /**
     * The mock CreatePersonActionFactory to be used.
     */
    private CreatePersonActionFactory personActionFactoryMock = context.mock(CreatePersonActionFactory.class);

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        userActionRequests = new ArrayList<UserActionRequest>();

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getUserActionRequests();
                will(returnValue(userActionRequests));

                allowing(actionContext).getPrincipal();
                will(returnValue(userPrincipal));

                allowing(userPrincipal).getAccountId();
                will(returnValue(accountId));
            }
        });
    }

    /**
     * Build an organization based on the input form being fully filled out with valid data.
     *
     * @throws Exception
     *             not expected
     */
    @SuppressWarnings("deprecation")
    @Test
    public void getSuccess() throws Exception
    {
        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("orgParent", "");

        userActionRequests = new ArrayList<UserActionRequest>();

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getUserActionRequests();
                will(returnValue(userActionRequests));

                allowing(orgMapperMock).findByShortName(with(any(String.class)));
                oneOf(orgMapperMock).getRootOrganization();
            }
        });

        OrganizationCreator sut = new OrganizationCreator(orgMapperMock);
        Assert.assertNotNull(sut.get(null, formData));
        context.assertIsSatisfied();
    }

    /**
     * Build an organization based on the input form being fully filled out with valid data and with a non-root
     * parent org defined.
     *
     * @throws Exception
     *             not expected
     */
    @SuppressWarnings("deprecation")
    @Test
    public void getSuccessWithParentOrgDefined() throws Exception
    {
        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        final String subOrgShortname = "someSubOrg";
        formData.put("orgParent", subOrgShortname);

        userActionRequests = new ArrayList<UserActionRequest>();

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getUserActionRequests();
                will(returnValue(userActionRequests));

                allowing(orgMapperMock).findByShortName(with(any(String.class)));
            }
        });

        OrganizationCreator sut = new OrganizationCreator(orgMapperMock);
        Assert.assertNotNull(sut.get(null, formData));
        context.assertIsSatisfied();
    }

    /**
     * Build an organization based on the input form being fully filled out with valid data.
     *
     * @throws Exception
     *             not expected
     */
    @SuppressWarnings("deprecation")
    @Test
    public void persistSuccess() throws Exception
    {
        final Organization newOrg = context.mock(Organization.class);
        final StreamScope streamScope = context.mock(StreamScope.class);
        final long newOrgId = 19382L;

        final Organization parentOrg = context.mock(Organization.class, "parentOrg");

        final List<Person> attribMembers = new ArrayList<Person>();
        attribMembers.add(new Person("id1", "Homer", "Jay", "Simpson", "Homey"));
        attribMembers.add(new Person("id2", "Max", "X", "Power", "Homer"));

        final List<Person> groupMembers = new ArrayList<Person>();
        groupMembers.add(new Person("id2", "Homer", "Jay", "Simpson", "Homey"));
        groupMembers.add(new Person("id3", "Max", "X", "Power", "Homer"));

        final long id = 1L;
        String newName = "NEW org name here";

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("id", Long.toString(id));
        formData.put("name", newName);
        formData.put("membershipCriteria", "somegroup");

        context.checking(new Expectations()
        {
            {
                allowing(newOrg).getId();
                will(returnValue(newOrgId));

                allowing(newOrg).getParentOrganization();
                will(returnValue(parentOrg));

                oneOf(newOrg).getShortName();
                will(returnValue("blah"));

                oneOf(orgMapperMock).findByShortName("blah");
                will(returnValue(null));

                allowing(newOrg).getCoordinators();
                will(returnValue(new HashSet<Person>()));

                oneOf(orgMapperMock).insert(newOrg);
                oneOf(newOrg).getStreamScope();
                will(returnValue(streamScope));
                
                oneOf(streamScope).setDestinationEntityId(newOrgId);

                oneOf(orgMapperMock).updateChildOrganizationCount(with(same(parentOrg)));
                oneOf(orgMapperMock).flush();
            }
        });

        try
        {
            OrganizationCreator sut = new OrganizationCreatorSubclass(orgMapperMock);
            sut.persist(taskHandlerActionContext, formData, newOrg);
            context.assertIsSatisfied();
        }
        catch (Exception e)
        {
            fail(e + ": something bad happened while persisting");
        }
    }

    /**
     * Attempt persist with with org of same short name already present.
     *
     * @throws Exception
     *             Only ValidationException expected.
     */
    @SuppressWarnings("deprecation")
    @Test(expected = ValidationException.class)
    public void persistFailDupShortName() throws Exception
    {
        final Organization newOrg = context.mock(Organization.class, "newOrg");
        final Organization dupOrg = context.mock(Organization.class, "dupOrg");

        final long id = 1L;
        String newName = "NEW org name here";

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("id", Long.toString(id));
        formData.put("name", newName);
        formData.put("membershipcriteria", "somegroup");

        final OrganizationHierarchyTraverser orgTraverser = context.mock(OrganizationHierarchyTraverser.class);

        context.checking(new Expectations()
        {
            {
                one(orgTraverser).traverseHierarchy(newOrg);

                // this gets called by OrganizationPersister constructor.
                oneOf(personActionFactoryMock).getCreatePersonAction(with(any(PersonMapper.class)),
                        with(any(UpdaterStrategy.class)));

                oneOf(newOrg).getShortName();
                will(returnValue("blah"));

                oneOf(orgMapperMock).findByShortName("blah");
                will(returnValue(dupOrg));
            }
        });

        OrganizationCreator sut = new OrganizationCreatorSubclass(orgMapperMock);
        sut.persist(taskHandlerActionContext, formData, newOrg);
        context.assertIsSatisfied();

    }

    /**
     * subclass of OrganizationCreator to mock out the OrganizationHierarchyTraverser that it needs to instantiate.
     */
    private class OrganizationCreatorSubclass extends OrganizationCreator
    {
        /**
         * Constructor - pass-thru to the parent.
         *
         * @param inOrganizationMapper
         *            The org mapper.
         */
        @SuppressWarnings("deprecation")
        public OrganizationCreatorSubclass(final OrganizationMapper inOrganizationMapper)
        {
            super(inOrganizationMapper);
        }
    }

}
