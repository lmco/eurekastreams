/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.eurekastreams.testing.DBUnitFixtureSetup;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for Mapper tests - sets up the dataset.xml loading.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml" })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public abstract class MapperTest
{
    /**
     * EntityManager injected in for low-level ORM hits like flush & clear.
     */
    private EntityManager entityManager;

    /**
     * Getter for EntityManager.
     * 
     * @return the EntityManager instance.
     */
    protected EntityManager getEntityManager()
    {
        return entityManager;
    }

    /**
     * Set the entity manager - used for low-level ORM hits like flush & clear.
     * 
     * @param inEntityManager
     *            the EntityManager to inject
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager inEntityManager)
    {
        entityManager = inEntityManager;
    }

    /**
     * Load the DBUnit XML for the all tests in this suite. Override this in the subclass if you want it to load a
     * different dataset file.
     * 
     * @throws Exception
     *             If error occurs during setup.
     */
    @BeforeClass
    public static void setUpOnce() throws Exception
    {
        // Load up the DBUnit data set
        DBUnitFixtureSetup.loadDataSet("/dataset.xml");
    }
}
