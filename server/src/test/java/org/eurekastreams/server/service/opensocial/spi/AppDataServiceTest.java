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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.testing.FakeGadgetToken;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.SocialSpiException;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.UserId.Type;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.exceptions.GeneralException;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.action.execution.opensocial.DeleteAppDataExecution;
import org.eurekastreams.server.action.principal.OpenSocialPrincipalPopulator;
import org.eurekastreams.server.domain.AppData;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Class for the AppDataService implementation.
 * 
 */
public class AppDataServiceTest
{
    /**
     * This is a fake security token taken from Shindig for testing.
     */
    private static final SecurityToken FAKETOKEN = new FakeGadgetToken();

    /**
     * A test UserId object to be used during the tests.
     */
    private UserId testId = new UserId(Type.userId, "123456");

    /**
     * A test GroupId object to be used during the tests.
     */
    private GroupId testGroupId = new GroupId(GroupId.Type.self, "654321");

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
     * Mocked instance of the Action for retrieving application data from the database.
     */
    private ServiceAction getAppDataAction = context.mock(ServiceAction.class, "getAppDataAction");

    /**
     * Mocked instance of the {@link OpenSocialPrincipalPopulator}.
     */
    private OpenSocialPrincipalPopulator principalPopulatorMock = context.mock(OpenSocialPrincipalPopulator.class);

    /**
     * Instance of the {@link Principal} interface for tests.
     */
    private Principal principalMock = context.mock(Principal.class);
    
    /**
     * Instance of ServiceActionContext for tests.
     */
    private ServiceActionContext actionContext = context.mock(ServiceActionContext.class);

    /**
     * Mocked instance of the {@link ServiceActionController}.
     */
    private ServiceActionController serviceActionControllerMock = context.mock(ServiceActionController.class);

    /**
     * Mocked instance of the action for updating application data from the database.
     */
    private ServiceAction updateDataAction = context.mock(ServiceAction.class, "updateDataAction");
    
    /**
     * Mocked instance of the action for deleting application data from the database.
     */
    private ServiceAction deleteDataAction = context.mock(ServiceAction.class, "deleteDataAction");

    /**
     * Mocked instance of the action for deleting application data from the database.
     */
    private DeleteAppDataExecution deleteDataExecution = context.mock(DeleteAppDataExecution.class);

    /**
     * Mocked instance of AppData to set expectations for the tests.
     */
    private AppData appData = context.mock(AppData.class);

    /**
     * String to use for test application ids.
     */
    private static final String TEST_APP_ID = "123";

    /**
     * Instance of the AppDataService that will be tested.
     */
    private AppDataServiceImpl sut;

    /**
     * Setup method.
     */
    @Before
    public void setUp()
    {
        sut = new AppDataServiceImpl(getAppDataAction, serviceActionControllerMock, principalPopulatorMock,
                updateDataAction, deleteDataAction);
    }

    /**
     * Test the method to Get Person Data.
     * 
     * @throws Exception
     *             - covers all errors
     */
    @Test
    public void testGetPersonData() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(principalPopulatorMock).getPrincipal(with(any(String.class)));
                will(returnValue(principalMock));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(appData));

                // If the requested appdata is not found, these two calls will not be made.
                allowing(appData).getPerson().getId();
                allowing(appData).getValues();
            }
        });

        Set<UserId> userIds = new HashSet<UserId>();
        userIds.add(new UserId(UserId.Type.userId, "321"));
        sut.getPersonData(userIds, testGroupId, TEST_APP_ID, Person.Field.DEFAULT_FIELDS, FAKETOKEN);
        // Not asserting the output of the getPersonData call because Shindig handles it.
        // Only testing here that the appropriate calls have been made and the
        // objects have been correctly created.
        context.assertIsSatisfied();
    }

    /**
     * Test the method to Get Person Data.
     * 
     * @throws Exception
     *             - covers all errors
     */
    @Test(expected = SocialSpiException.class)
    public void testGetPersonDataWithNullUserId() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(principalPopulatorMock).getPrincipal(with(any(String.class)));
                will(returnValue(principalMock));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(appData));

                // If the requested appdata is not found, these two calls will not be made.
                allowing(appData).getPerson().getId();
                allowing(appData).getValues();
            }
        });

        Set<UserId> userIds = new HashSet<UserId>();
        UserId nullUserId = null;
        userIds.add(nullUserId);
        sut.getPersonData(userIds, testGroupId, TEST_APP_ID, Person.Field.DEFAULT_FIELDS, FAKETOKEN);
        // Not asserting the output of the getPersonData call because Shindig handles it.
        // Only testing here that the appropriate calls have been made and the
        // objects have been correctly created.
        context.assertIsSatisfied();
    }

    /**
     * Test Exception handling for the method to Get Person Data.
     * 
     * @throws Exception
     *             - covers all errors
     */
    @Test(expected = SocialSpiException.class)
    public void testGetPersonDataException() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(principalPopulatorMock).getPrincipal(with(any(String.class)));
                will(returnValue(principalMock));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new GeneralException()));
            }
        });

        Set<UserId> userIds = new HashSet<UserId>();
        userIds.add(testId);
        sut.getPersonData(userIds, testGroupId, TEST_APP_ID, Person.Field.DEFAULT_FIELDS, FAKETOKEN);

        context.assertIsSatisfied();
    }
    
    /**
     * Test the method to Delete Person Data.
     * 
     * @throws Exception
     *          not expected.
     */
    @Test
    public void testDeletePersonData() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(principalPopulatorMock).getPrincipal(with(any(String.class)));
                will(returnValue(principalMock));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
            }
        });

        sut.deletePersonData(testId, testGroupId, TEST_APP_ID, Person.Field.DEFAULT_FIELDS, FAKETOKEN);

        context.assertIsSatisfied();
    }    

    /**
     * Test the method to Delete Person Data and throw an Exception.
     * 
     * @throws Exception
     *             - covers all errors
     */
    @Test(expected = SocialSpiException.class)
    public void testDeletePersonDataException() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(principalPopulatorMock).getPrincipal(with(any(String.class)));
                will(returnValue(principalMock));
                
                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new ExecutionException()));
            }
        });

        sut.deletePersonData(testId, testGroupId, TEST_APP_ID, Person.Field.DEFAULT_FIELDS, FAKETOKEN);

        context.assertIsSatisfied();
    }

    /**
     * Test the method to update person data.
     * 
     * @throws Exception
     *             - covers all errors
     */
    @Test
    public void testUpdatePersonData() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(principalPopulatorMock).getPrincipal(with(any(String.class)));
                will(returnValue(principalMock));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(returnValue(appData));
            }
        });
        sut.updatePersonData(testId, testGroupId, TEST_APP_ID, Person.Field.DEFAULT_FIELDS,
                new HashMap<String, String>(), FAKETOKEN);

        // Not asserting the output of the getPersonData call because Shindig handles it.
        // Only testing here that the appropriate calls have been made and the
        // objects have been correctly created.
        context.assertIsSatisfied();
    }

    /**
     * Test Exception handling in the method.
     * 
     * @throws Exception
     *             errors to be caught by caller.
     */
    @Test(expected = SocialSpiException.class)
    public void testUpdatePersonDataException() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(principalPopulatorMock).getPrincipal(with(any(String.class)));
                will(returnValue(principalMock));

                oneOf(serviceActionControllerMock).execute(with(any(ServiceActionContext.class)),
                        with(any(ServiceAction.class)));
                will(throwException(new Exception()));
            }
        });
        sut.updatePersonData(testId, testGroupId, TEST_APP_ID, Person.Field.DEFAULT_FIELDS,
                new HashMap<String, String>(), FAKETOKEN);

        context.assertIsSatisfied();
    }
}
