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
package org.eurekastreams.server.service.restlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;

/**
 * Test class for the PersonPropertiesResource restlet.
 * 
 */
public class PersonPropertiesResourceTest
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
     * Action.
     */
    private ServiceAction getPersonIdsAction = context.mock(ServiceAction.class, "getPersonIdsAction");

    /**
     * Action.
     */
    private ServiceAction getPersonModelViewsAction = context.mock(ServiceAction.class, "getPersonModelViewsAction");

    /**
     * Service Action Controller.
     */
    private ServiceActionController serviceActionController = context.mock(ServiceActionController.class);

    /**
     * Lost of long ids for the users in the db.
     */
    private List<Long> userIds;

    /**
     * List of the person model views matching the ids passed in.
     */
    private List<PersonModelView> personModelViews;

    /**
     * System under test.
     */
    private PersonPropertiesResource sut;

    /**
     * Prepare the test.
     */
    @Before
    public void setup()
    {
        sut = new PersonPropertiesResource(getPersonModelViewsAction, serviceActionController, getPersonIdsAction);

        userIds = new ArrayList<Long>();
        userIds.add(1L);
        userIds.add(2L);

        personModelViews = new ArrayList<PersonModelView>();
        PersonModelView user1 = new PersonModelView();
        user1.setAccountId("testaccountid1");
        HashMap<String, String> addlPropertiesUser1 = new HashMap<String, String>();
        addlPropertiesUser1.put("property1", "value1");
        addlPropertiesUser1.put("property2", "value2");
        addlPropertiesUser1.put("property3", "value3");
        user1.setAdditionalProperties(addlPropertiesUser1);
        personModelViews.add(user1);

        PersonModelView user2 = new PersonModelView();
        user1.setAccountId("testaccountid2");
        HashMap<String, String> addlPropertiesUser2 = new HashMap<String, String>();
        addlPropertiesUser2.put("property1", "value1");
        addlPropertiesUser2.put("property2", "value2");
        addlPropertiesUser2.put("property3", "value3");
        user1.setAdditionalProperties(addlPropertiesUser2);
        personModelViews.add(user2);
    }

    /**
     * Test retrieving the json for all of the users.
     * 
     * @throws ResourceException
     *             - on error. Not expecting it in this test.
     * @throws IOException - on error.
     */
    @Test
    public void testGetJson() throws ResourceException, IOException
    {
        context.checking(new Expectations()
        {
            {
                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(equal(getPersonIdsAction)));
                will(returnValue(userIds));

                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(equal(getPersonModelViewsAction)));
                will(returnValue(personModelViews));
            }
        });

        Representation response = sut.represent(null);

        assertEquals(MediaType.APPLICATION_JSON, response.getMediaType());
        JSONObject responseJs = JSONObject.fromObject(response.getText());

        assertTrue(responseJs.containsKey("personProperties"));
        assertEquals(2, responseJs.getJSONArray("personProperties").size());
        assertEquals("testaccountid2", responseJs.getJSONArray("personProperties").getJSONObject(0).getString(
                "accountId"));
        assertEquals("value1", responseJs.getJSONArray("personProperties").getJSONObject(0).getString("property1"));
        assertEquals(4, responseJs.getJSONArray("personProperties").getJSONObject(0).keySet().size());
    }

    /**
     * Test receiving an error from the request.
     * 
     * @throws ResourceException
     *             - on error.
     */
    @Test(expected = ResourceException.class)
    public void testSimulatedFailure() throws ResourceException
    {
        context.checking(new Expectations()
        {
            {
                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(equal(getPersonIdsAction)));
                will(returnValue(userIds));

                oneOf(serviceActionController).execute(with(any(ServiceActionContext.class)),
                        with(equal(getPersonModelViewsAction)));
                will(throwException(new Exception()));
            }
        });

        sut.represent(null);
    }
}
