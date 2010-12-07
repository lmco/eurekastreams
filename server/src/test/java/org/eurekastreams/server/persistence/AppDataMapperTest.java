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
package org.eurekastreams.server.persistence;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eurekastreams.server.domain.AppData;
import org.eurekastreams.server.domain.AppDataValue;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.GalleryItemCategory;
import org.eurekastreams.server.domain.Person;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for the JpaAppDataMapper.
 */
public class AppDataMapperTest extends DomainEntityMapperTest
{
    /**
     * Instance of the mapper to be tested.
     */
    @Autowired
    private AppDataMapper jpaAppDataMapper;

    /**
     * Instance of the AppDataValue mapper for testing.
     */
    @Autowired
    private AppDataValueMapper jpaAppDataValueMapper;

    /**
     * Url to use when creating local instances of GadgetDefinition.
     */
    private final String testGadgetDefinitionUrl = "http://www.example.com/gadget2.xml";

    /**
     * test application id.
     */
    private final Long testAppDataId = 3497L;

    /**
     * test application data value id.
     */
    private final Long testAppDataValueId = 5371L;

    /**
     * test person opensocial id.
     */
    private final String testOpenSocialPersonId = "2d359911-0977-418a-9490-57e8252b1a42";

    /**
     * test person id.
     */
    private final long testPersonId = 4507L;

    /**
     * test gadget definition id.
     */
    private final Long testGadgetDefinitionId = 4789L;

    /**
     * Simple test for dataset setup.
     */
    @Test
    public void testDatasetSetup()
    {
        AppData testAppData = jpaAppDataMapper.findById(new Long(testAppDataId));
        assertEquals("Person is not correct for retrieved AppData", testPersonId, testAppData.getPerson().getId());
        assertEquals("Viewcount is incorrect for retrieved AppData", "2", testAppData.getValues().get("viewcount"));
        assertEquals("Answer to question1answer is incorrect for retrieved AppData", "true", testAppData.getValues()
                .get("question1answer"));
    }

    /**
     * Simple test to ensure the AppDataValue DataSet setup.
     */
    @Test
    public void testAppDataValueDatasetSetup()
    {
        AppDataValue testAppDataValue = jpaAppDataValueMapper.findById(new Long(testAppDataValueId));
        assertEquals("Key is not correct.", "viewcount", testAppDataValue.getName());
        assertEquals("Value is not correct.", "2", testAppDataValue.getValue());

    }

    /**
     * Test if data retrieved by Gadget and Person Id's is correct.
     */
    @Test
    public void testFindByAppAndPersonId()
    {
        AppData testAppData = jpaAppDataMapper.findOrCreateByPersonAndGadgetDefinitionIds(testGadgetDefinitionId,
                testOpenSocialPersonId);
        assertEquals("Person is not correct for retrieved AppData", testOpenSocialPersonId, testAppData.getPerson()
                .getOpenSocialId());
        assertEquals("GadgetDefinition is not correct for retrieved AppData", testGadgetDefinitionUrl, testAppData
                .getGadgetDefinition().getUrl());

    }

    /**
     * Testing insert capabilities.
     */
    @Test
    public void testInsert()
    {
        AppData appDataTestInstance = getNewAppDataObject();
        jpaAppDataMapper.insert(appDataTestInstance);
        assertTrue("Insert didn't work correctly, id is bad", appDataTestInstance.getId() > 0);
    }

    /**
     * Test to verify that data has been removed from the object and persisted to the db.
     * 
     * @throws Exception
     *             when an exception is encountered.
     */
    @Test
    public void testRemoveDataItem() throws Exception
    {
        AppData appDataTestInstance = jpaAppDataMapper.findById(testAppDataId);
        jpaAppDataMapper.deleteAppDataValueByKey(appDataTestInstance.getId(), "viewcount");
        jpaAppDataMapper.flush();
        getEntityManager().clear();
        AppData appDataTestInstanceRemoved = jpaAppDataMapper.findById(testAppDataId);
        assertFalse("Data was not removed", appDataTestInstanceRemoved.getValues().containsKey("viewcount"));
    }

    /**
     * Try to delete an app data value with a key that doesn't exist.
     * 
     * @throws Exception
     *             when an error is encountered.
     */
    @Test
    public void testRemoveDataKeyThatDoesntExist() throws Exception
    {
        AppData appDataTestInstance = jpaAppDataMapper.findById(testAppDataId);
        jpaAppDataMapper.deleteAppDataValueByKey(appDataTestInstance.getId(), "nonexistent");
        jpaAppDataMapper.flush();
        getEntityManager().clear();
        AppData appDataTestInstanceRemoved = jpaAppDataMapper.findById(testAppDataId);
        assertFalse("Data was not removed", appDataTestInstanceRemoved.getValues().containsKey("nonexistent"));
    }

    /**
     * Try to delete an app data value with a key that is attempting to use SQL Injection since keys can be entered by
     * users.
     * 
     * @throws Exception
     *             when an error is encountered.
     */
    @Test
    public void testRemoveDataKeyThatIsBad() throws Exception
    {
        AppData appDataTestInstance = jpaAppDataMapper.findById(testAppDataId);
        jpaAppDataMapper.deleteAppDataValueByKey(appDataTestInstance.getId(), "viewcount' OR 1=1");
        jpaAppDataMapper.flush();
        getEntityManager().clear();
        AppData appDataTestInstanceRemoved = jpaAppDataMapper.findById(testAppDataId);
        assertTrue("Data was removed with a bad key", appDataTestInstanceRemoved.getValues().containsKey("viewcount"));
    }

    /**
     * Test the ability to update the value of an app data item.
     */
    @Test
    public void testUpdateDataItem()
    {
        AppData appDataTestInstance = jpaAppDataMapper.findById(testAppDataId);
        Map<String, String> appDataValues = new HashMap<String, String>(appDataTestInstance.getValues());
        appDataValues.put("viewcount", "4");
        appDataTestInstance.setValues(appDataValues);
        jpaAppDataMapper.flush();
        getEntityManager().clear();
        AppData appDataTestInstanceUpdated = jpaAppDataMapper.findById(testAppDataId);
        assertTrue("Data was not removed", appDataTestInstanceUpdated.getValues().containsKey("viewcount"));
        assertEquals("Data was not updated", "4", appDataTestInstanceUpdated.getValues().get("viewcount"));
    }

    /**
     * Helper class to create a fresh instance of an AppData object for testing.
     * 
     * @return AppData object for testing.
     */
    private AppData getNewAppDataObject()
    {
        Map<String, String> testDataValues = new HashMap<String, String>();
        testDataValues.put("favoritegame", "gearsofwar");
        testDataValues.put("firstpet", "snake");
        AppData testInputAppData = new AppData();
        testInputAppData.setValues(testDataValues);
        testInputAppData.setGadgetDefinition(new GadgetDefinition(testGadgetDefinitionUrl,
                UUID.randomUUID().toString(), new GalleryItemCategory("somecategory")));
        testInputAppData.setPerson(new Person("acole", "Augustus", "jay", "Cole", "Cole Train"));

        return testInputAppData;
    }
}
