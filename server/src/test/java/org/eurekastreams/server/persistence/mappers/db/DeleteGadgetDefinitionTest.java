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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests DeleteGadgetDefinition.
 */
public class DeleteGadgetDefinitionTest extends MapperTest
{
    /** System under test. */
    private DeleteGadgetDefinition sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUpEach()
    {
        sut = new DeleteGadgetDefinition();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        final long id = 1831L;
        final long gadgetId = 5039L;

        // pre-check
        List list = getEntityManager().createQuery("FROM GadgetDefinition WHERE id=:id").setParameter("id", id)
                .getResultList();
        assertEquals(1, list.size());

        // dataset.xml didn't have any deleted gadgets (at time of writing), so make sure there are some
        getEntityManager()
                .createQuery("UPDATE VERSIONED Gadget SET deleted = true, dateDeleted = current_date() WHERE id=:id")
                .setParameter("id", gadgetId).executeUpdate();

        // act
        sut.execute(id);

        // post-check
        list = getEntityManager().createQuery("FROM GadgetDefinition WHERE id=:id").setParameter("id", id)
                .getResultList();
        assertTrue(list.isEmpty());
    }
}
