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
package org.eurekastreams.commons.search.modelview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.commons.model.DomainEntity;
import org.eurekastreams.commons.model.MyDomainEntity;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for ModelView.
 */
public class ModelViewTest
{
    /**
     * System under test.
     */
    private MyModelView sut;

    /**
     * Test setup method - initialize the sut.
     */
    @Before
    public void setUp()
    {
        sut = new MyModelView();
    }

    /**
     * Test the loadProperties method.
     */
    @Test
    public void testLoadProperties()
    {
        final long entityId = 0xF00BA7;
        final float searchScore = 3.0272F;
        final DomainEntity entity = new MyDomainEntity();

        Map<String, Object> props = new HashMap<String, Object>();
        props.put("__HSearch_id", entityId);
        props.put("__HSearch_Score", searchScore);
        props.put("__HSearch_This", entity);

        // sut
        sut.loadProperties(props);

        assertEquals(entityId, sut.getEntityId());
        assertEquals(searchScore, sut.getSearchIndexScore(), 0);
        assertEquals(entity, sut.getManagedEntity());
    }

    /**
     * Test the loadProperties method, with "id" instead of "__HSearch_id".
     */
    @Test
    public void testLoadPropertiesWithId()
    {
        final long entityId = 0xF00BA7;
        final float searchScore = 3.0272F;
        final DomainEntity entity = new MyDomainEntity();

        Map<String, Object> props = new HashMap<String, Object>();
        props.put("id", entityId);
        props.put("__HSearch_Score", searchScore);
        props.put("__HSearch_This", entity);

        // sut
        sut.loadProperties(props);

        assertEquals(entityId, sut.getEntityId());
        assertEquals(searchScore, sut.getSearchIndexScore(), 0);
        assertEquals(entity, sut.getManagedEntity());
    }

    /**
     * Test the fieldMatch property.
     */
    @Test
    public void testFieldMatchProperty()
    {
        FieldMatch fm = new FieldMatch();
        sut.setFieldMatch(fm);
        assertSame(fm, sut.getFieldMatch());
    }

    /**
     * Test the managedEntity property.
     */
    @Test
    public void testManagedEntityProperty()
    {
        DomainEntity entity = new MyDomainEntity();
        assertNull(sut.getManagedEntity());
        sut.setManagedEntity(entity);
        assertEquals(entity, sut.getManagedEntity());
    }

    /**
     * test the entityId property.
     */
    @Test
    public void testEntityIdProperty()
    {
        final long id = 0xDEADBEEF;
        assertEquals(ModelView.UNINITIALIZED_LONG_VALUE, sut.getEntityId());
        sut.setEntityId(id);
        assertEquals(id, sut.getEntityId());
    }

    /**
     * Test the searchIndexScore property.
     */
    @Test
    public void testSearchIndexScoreProperty()
    {
        final float score = 8.2382F;
        assertEquals(ModelView.UNINITIALIZED_FLOAT_VALUE, sut
                .getSearchIndexScore(), 0);
        sut.setSearchIndexScore(score);
        assertEquals(score, sut.getSearchIndexScore(), 0);
    }

    /**
     * Test the toString() method.
     */
    @Test
    public void testToString()
    {
        final long id = 38477L;
        // test the temporary version
        assertEquals("Turnip", sut.toString());
        sut.setEntityId(id);
        // test the generated version
        assertEquals("Turnip#" + id, sut.toString());
        // test the cached version now
        assertEquals("Turnip#" + id, sut.toString());
    }

    /**
     * Test the hasSearchIndexExplanation() method.
     */
    @Test
    public void testHasSearchIndexExplanation()
    {
        final String explanationString = "Hey there, whoa now.";

        // test uninitialized
        assertFalse(sut.hasSearchIndexExplanation());

        // set both explanation, assert now true
        sut.setSearchIndexExplanationString(explanationString);
        assertTrue(sut.hasSearchIndexExplanation());
    }

    /**
     * Test the searchIndexExplanationString property.
     */
    @Test
    public void testSearchIndexExplanationStringProperty()
    {
        final String explanation = "Hey there, whoa now.";
        assertEquals(ModelView.UNINITIALIZED_STRING_VALUE, sut
                .getSearchIndexExplanationString());
        sut.setSearchIndexExplanationString(explanation);
        assertEquals(explanation, sut.getSearchIndexExplanationString());
    }

    /**
     * Test the server date time property.
     */
    @Test
    public void testServerDateTime()
    {
        final Date currentServerDate = new Date();
        assertEquals(ModelView.UNINITIALIZED_DATE_VALUE, sut.getServerDateTime());
        sut.setServerDateTime(currentServerDate);
        assertEquals(currentServerDate, sut.getServerDateTime());
    }
}
