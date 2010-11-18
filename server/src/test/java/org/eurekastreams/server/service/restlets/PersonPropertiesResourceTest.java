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
import java.util.Map;

import net.sf.json.JSONObject;

import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.server.service.ServiceActionController;
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
    private ServiceAction getPersonAdditionalPropertiesAction = context.mock(ServiceAction.class);

    /**
     * Service Action Controller.
     */
    private ServiceActionController serviceActionController = context.mock(ServiceActionController.class);

    /**
     * List of the person objects from the db.
     */
    private List<Map<String, Object>> personObjects;

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
        sut = new PersonPropertiesResource(serviceActionController, getPersonAdditionalPropertiesAction);

        personObjects = new ArrayList<Map<String, Object>>();
        Map<String, Object> user1 = new HashMap<String, Object>();
        HashMap<String, String> addlPropertiesUser1 = new HashMap<String, String>();
        addlPropertiesUser1.put("property1", "value1");
        addlPropertiesUser1.put("property2", "value2");
        addlPropertiesUser1.put("property3", "value3");
        user1.put("accountId", "testaccountid1");
        user1.put("additionalProperties", addlPropertiesUser1);
        personObjects.add(user1);

        Map<String, Object> user2 = new HashMap<String, Object>();
        HashMap<String, String> addlPropertiesUser2 = new HashMap<String, String>();
        addlPropertiesUser2.put("property1", "value1");
        addlPropertiesUser2.put("property2", "value2");
        addlPropertiesUser2.put("property3", "value3");
        user2.put("accountId", "testaccountid2");
        user2.put("additionalProperties", addlPropertiesUser2);
        personObjects.add(user2);
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
                        with(equal(getPersonAdditionalPropertiesAction)));
                will(returnValue(personObjects));
            }
        });

        Representation response = sut.represent(null);

        assertEquals(MediaType.APPLICATION_JSON, response.getMediaType());
        JSONObject responseJs = JSONObject.fromObject(response.getText());

        assertTrue(responseJs.containsKey("personProperties"));
        assertEquals(2, responseJs.getJSONArray("personProperties").size());
        assertEquals("testaccountid1", responseJs.getJSONArray("personProperties").getJSONObject(0).getString(
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
                        with(equal(getPersonAdditionalPropertiesAction)));
                will(throwException(new Exception()));
            }
        });

        sut.represent(null);
    }
}
