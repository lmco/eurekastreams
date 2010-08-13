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
 * Parent class for JPA Domain Entity mappers.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml" })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public abstract class DomainEntityMapperTest
{
    /**
     * The GadgetDefinition ID for "http://www.google.com".
     */
    final long gadgetDefinitionId1 = 1831L;

    /**
     * The GadgetDefinition ID for "http://www.example.com".
     */
    final long gadgetDefinitionId2 = 4789L;

    /**
     * The tabGroupId for Ford's start page.
     */
    final long fordsStartPageId = 4231L;

    /**
     * The tabGroupId for Carl Sagan's start page.
     */
    final long carlSagansStartPageId = 5393L;

    /**
     * The tabId for Ford's first tab.
     */
    final long fordsFirstTabId = 1097L;

    /**
     * The tabId for Ford's second tab.
     */
    final long fordsSecondTabId = 1787L;

    /**
     * The tabId for Ford's third tab.
     */
    final long fordsThirdTabId = 3253L;

    /**
     * The tabId for Carl's first tab.
     */
    final long carlsFirstTabId = 4703L;

    /**
     * The tabId for Ford's deleted tab.
     */
    final long fordsDeletedTabId = 3187L;

    /**
     * The tabId for Ford's deleted tab.
     */
    final long fordsDeletedGadgetId = 3754L;

    /**
     * The themeId for the test theme.
     */
    final long testThemeId = 102L;

    /**
     * The number of minutes to reset the deleted tab window to before each
     * test.
     */
    final int undeleteWindowInMinutes = 20;

    /**
     * First gadget in Ford's first tab.
     */
    final long fordsFirstTabFirstGadgetId = 5039L;

    /**
     * Third gadget in Ford's first tab.
     */
    final long fordsFirstTabSecondGadgetId = 6863L;

    /**
     * Second gadget in Ford's first tab.
     */
    final long fordsFirstTabThirdGadgetId = 4969L;

    /**
     * First gadget in Ford's third tab.
     */
    final long fordsThirdTabFirstGadgetId = 7841L;

    /**
     * Second gadget in Ford's third tab.
     */
    final long fordsThirdTabSecondGadgetId = 5749L;

    /**
     * Third gadget in Ford's third tab.
     */
    final long fordsThirdTabThirdGadgetId = 7879L;

    /**
     * Fourth gadget in Ford's third tab.
     */
    final long fordsThirdTabFourthGadgetId = 7753L;

    /**
     * Fifth gadget in Ford's third tab.
     */
    final long fordsThirdTabFifthGadgetId = 3759L;

    /**
     * Sixth gadget in Ford's third tab.
     */
    final long fordsThirdTabSixthGadgetId = 3751L;

    /**
     * Seventh gadget in Ford's third tab.
     */
    final long fordsThirdTabSeventhGadgetId = 3781L;

    /**
     * First gadget in Carl's first tab.
     */
    final long carlsFirstTabFirstGadgetId = 1513L;

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
        this.entityManager = inEntityManager;
    }

    /**
     * Load the DBUnit XML for the all tests in this suite. Override this in the
     * subclass if you want it to load a different dataset file.
     *
     * @throws Exception
     *             If error occurs during setup.
     */
    @BeforeClass
    public static void setUp() throws Exception
    {
        // Load up the DBUnit data set
        DBUnitFixtureSetup.loadDataSet("/dataset.xml");
    }
}
