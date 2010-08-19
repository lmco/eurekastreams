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
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.server.action.validation.ValidationTestHelper;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.server.domain.TabType;
import org.eurekastreams.server.persistence.exceptions.GadgetDeletionException;
import org.eurekastreams.server.persistence.exceptions.GadgetUndeletionException;
import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.transaction.AfterTransaction;

/**
 * This class is responsible for testing the JPA Implementation of the Tab Mapper interface. The tests contained in here
 * ensure proper interaction with the database.
 */
public class TabMapperTest extends DomainEntityMapperTest
{

    /**
     * This field holds the test instance of jpaTabMapper.
     */
    @Autowired
    private TabMapper jpaTabMapper;

    /**
     * This field holds the test instance of jpaGadgetMapper.
     */
    @Autowired
    private GadgetMapper jpaGadgetMapper;

    /**
     * Instance to hold any test post transaction actions if needed.
     */
    private PostTransactionAction afterTransactionAction = null;

    /**
     * This will execute after every transaction, tests can use this method by assigning an inner class to
     * afterTransactionAction instance. See testGadgetDefinition method for example.
     */
    @AfterTransaction
    public void executeAfterTransactionAction()
    {
        if (afterTransactionAction == null)
        {
            return;
        }
        afterTransactionAction.execute();
        afterTransactionAction = null;
    }

    /**
     * Test the data that other tests expect in the DBUnit data set.
     */
    @Test
    public void testDBUnitDataset()
    {
        Tab fordTab1 = jpaTabMapper.findById(fordsFirstTabId);
        Tab fordTab2 = jpaTabMapper.findById(fordsSecondTabId);
        Tab fordTab3 = jpaTabMapper.findById(fordsThirdTabId);

        // Test the tab names
        assertEquals("DBUnit not setup as expected - ford's first tab not named 'Ford Tab 1'", "Ford Tab 1", fordTab1
                .getTabName());
        assertEquals("DBUnit not setup as expected - ford's second tab not named 'Ford Tab 2'", "Ford Tab 2", fordTab2
                .getTabName());
        assertEquals("DBUnit not setup as expected - ford's third tab not named 'Ford Tab 3'", "Ford Tab 3", fordTab3
                .getTabName());

        // Test the tab layouts
        assertEquals("DBUnit not setup as expected - ford's first tab isnt' of type TWOCOLUMN", Layout.TWOCOLUMN,
                fordTab1.getTabLayout());
        assertEquals("DBUnit not setup as expected - ford's second tab isnt' of type TWOCOLUMNLEFTWIDE",
                Layout.TWOCOLUMNLEFTWIDE, fordTab2.getTabLayout());
        assertEquals("DBUnit not setup as expected - ford's third tab isnt' of type THREECOLUMN", Layout.THREECOLUMN,
                fordTab3.getTabLayout());
    }

    /**
     * Make sure Ford's 1st tab is setup properly in DBUnit.
     */
    @Test
    public void testDBUnitDataSetGadgetsInFord1Tab()
    {
        final int fordFirstTabExpectedGadgetCount = 3;
        final int zoneOne = 0;
        final int zoneThree = 2;

        Tab fordTab1 = jpaTabMapper.findById(fordsFirstTabId);

        assertEquals("Ford's 1st tab didn't have an expected number of gadgets.", fordFirstTabExpectedGadgetCount,
                fordTab1.getGadgets().size());

        // the gadgets property is sorted. put them in a HashTable, keyed by ID
        List<Gadget> tab1Gadgets = new ArrayList<Gadget>(fordTab1.getGadgets());
        HashMap<Long, Gadget> tab1Map = new HashMap<Long, Gadget>();
        for (Gadget g : tab1Gadgets)
        {
            tab1Map.put(g.getId(), g);
        }

        // ** test gadget definitions
        String message = "A gadget in Ford's first tab has an unexpected gadget url";
        // 2 google gadgets
        assertEquals(message, "http://www.example.com/gadget1.xml", tab1Map.get(fordsFirstTabFirstGadgetId)
                .getGadgetDefinition().getUrl());
        assertEquals(message, "http://www.example.com/gadget1.xml", tab1Map.get(fordsFirstTabSecondGadgetId)
                .getGadgetDefinition().getUrl());

        // 1 example gadget
        assertEquals(message, "http://www.example.com/gadget2.xml", tab1Map.get(fordsFirstTabThirdGadgetId)
                .getGadgetDefinition().getUrl());

        // ** test zoneNumbers
        message = "A gadget in Ford's first tab has an unexpected zoneNumber";
        assertEquals(message, zoneOne, tab1Map.get(fordsFirstTabFirstGadgetId).getZoneNumber());
        assertEquals(message, zoneOne, tab1Map.get(fordsFirstTabSecondGadgetId).getZoneNumber());
        assertEquals(message, zoneThree, tab1Map.get(fordsFirstTabThirdGadgetId).getZoneNumber());

        // ** test zoneIndexes
        message = "A gadget in Ford's first tab has an unexpected zoneIndex";
        assertEquals(message, 0, tab1Map.get(fordsFirstTabFirstGadgetId).getZoneIndex());
        assertEquals(message, 1, tab1Map.get(fordsFirstTabSecondGadgetId).getZoneIndex());
        assertEquals(message, 0, tab1Map.get(fordsFirstTabThirdGadgetId).getZoneIndex());
    }

    /**
     * Make sure Ford's 3rd tab is setup properly in DBUnit.
     */
    @Test
    public void testDBUnitDataSetGadgetsInFord3Tab()
    {
        final int expectedNumberOfGadgetsIn3rdTab = 7;
        Tab fordTab3 = jpaTabMapper.findById(fordsThirdTabId);

        assertEquals("Ford's 3rd tab didn't have an expected number of gadgets.", expectedNumberOfGadgetsIn3rdTab,
                fordTab3.getGadgets().size());

        // the gadgets property is sorted. put them in a HashTable, keyed by ID
        List<Gadget> tab3Gadgets = new ArrayList<Gadget>(fordTab3.getGadgets());
        HashMap<Long, Gadget> tab3Map = new HashMap<Long, Gadget>();
        for (Gadget g : tab3Gadgets)
        {
            tab3Map.put(g.getId(), g);
        }

        // ** test gadget definitions
        String message = "A gadget in Ford's third tab has an unexpected gadget url";

        // 2 google gadgets
        assertEquals(message, "http://www.example.com/gadget1.xml", tab3Map.get(fordsThirdTabFirstGadgetId)
                .getGadgetDefinition().getUrl());
        assertEquals(message, "http://www.example.com/gadget3.xml", tab3Map.get(fordsThirdTabThirdGadgetId)
                .getGadgetDefinition().getUrl());

        // 4 example gadgets
        assertEquals(message, "http://www.example.com/gadget2.xml", tab3Map.get(fordsThirdTabSecondGadgetId)
                .getGadgetDefinition().getUrl());
        assertEquals(message, "http://www.example.com/gadget2.xml", tab3Map.get(fordsThirdTabFourthGadgetId)
                .getGadgetDefinition().getUrl());
        assertEquals(message, "http://www.example.com/gadget2.xml", tab3Map.get(fordsThirdTabFifthGadgetId)
                .getGadgetDefinition().getUrl());
        assertEquals(message, "http://www.example.com/gadget2.xml", tab3Map.get(fordsThirdTabSixthGadgetId)
                .getGadgetDefinition().getUrl());
        assertEquals(message, "http://www.example.com/gadget2.xml", tab3Map.get(fordsThirdTabSeventhGadgetId)
                .getGadgetDefinition().getUrl());

        // ** test zoneNumbers
        message = "A gadget in Ford's first tab has an unexpected zoneNumber";
        assertEquals(message, 0, tab3Map.get(fordsThirdTabFirstGadgetId).getZoneNumber());
        assertEquals(message, 0, tab3Map.get(fordsThirdTabSecondGadgetId).getZoneNumber());
        assertEquals(message, 0, tab3Map.get(fordsThirdTabThirdGadgetId).getZoneNumber());
        assertEquals(message, 1, tab3Map.get(fordsThirdTabFourthGadgetId).getZoneNumber());

        // ** test zoneIndexes
        message = "A gadget in Ford's first tab has an unexpected zoneIndex";
        assertEquals(message, 0, tab3Map.get(fordsThirdTabFirstGadgetId).getZoneIndex());
        assertEquals(message, 1, tab3Map.get(fordsThirdTabSecondGadgetId).getZoneIndex());
        assertEquals(message, 2, tab3Map.get(fordsThirdTabThirdGadgetId).getZoneIndex());
        assertEquals(message, 0, tab3Map.get(fordsThirdTabFourthGadgetId).getZoneIndex());
    }

    /**
     * This test ensures that the tab listed in the DBUnit Test database is configured correctly and throws an exception
     * when it is trying to be retrieved.
     */
    @Test
    @ExpectedException(javax.persistence.NoResultException.class)
    public void testDeletedTabDBUnitDatasetSetup()
    {
        // We expect an exception on this one.
        jpaTabMapper.findById(fordsDeletedTabId);
    }

    /**
     * Make sure the Tab.getGadgets() getter returns the Gadgets in order of ZoneNumber, ZoneIndex.
     */
    @Test
    public void testGadgetSortOrder()
    {
        // Test Ford's first tab (zoneNumber, zoneIndex):
        Tab fordTab1 = jpaTabMapper.findById(fordsFirstTabId);
        // (1, 0)
        assertEquals("", fordsFirstTabFirstGadgetId, fordTab1.getGadgets().get(0).getId());
        // (1, 1)
        assertEquals("", fordsFirstTabSecondGadgetId, fordTab1.getGadgets().get(1).getId());
        // (3, 0)
        assertEquals("", fordsFirstTabThirdGadgetId, fordTab1.getGadgets().get(2).getId());

        // Test Ford's third tab (zoneNumber, zoneIndex)
        Tab fordTab3 = jpaTabMapper.findById(fordsThirdTabId);
        // (1, 0)
        assertEquals("", fordsThirdTabFirstGadgetId, fordTab3.getGadgets().get(0).getId());
        // (1, 1)
        assertEquals("", fordsThirdTabSecondGadgetId, fordTab3.getGadgets().get(1).getId());
        // (1, 2)
        assertEquals("", fordsThirdTabThirdGadgetId, fordTab3.getGadgets().get(2).getId());
        // (2, 0)
        assertEquals("", fordsThirdTabFourthGadgetId, fordTab3.getGadgets().get(3).getId());
    }

    /**
     * Wrote this test to ensure that GadgetDefinition is always eagerly loaded by gadget. This test was result of a bug
     * fix for null definitions within a gadget. NOTE: transaction must be ended before assert is called otherwise
     * assert will cause hibernate to lazy-load the gadgetDefinition, even if getEntityManager().clear() is used. This
     * test is what prompted the afterTransactionAction pattern.
     */
    @Test
    public void testGadgetDefinition()
    {
        Tab fordTab1 = jpaTabMapper.findById(fordsFirstTabId);
        final Gadget g = fordTab1.getGadgets().get(0);
        afterTransactionAction = new PostTransactionAction()
        {
            public void execute()
            {
                assertNotNull(g);
                junit.framework.Assert.assertEquals("http://www.example.com/gadget1.xml", g.getGadgetDefinition()
                        .getUrl());

            }
        };
    }

    /**
     * Test that the tab must have at least one character in its name.
     */
    @Test
    public void testTabNameLessThanMinLengthConstraint()
    {
        boolean exceptionOccurred = false;
        String newTabName = "";

        try
        {
            Tab fordTab1 = jpaTabMapper.findById(fordsFirstTabId);
            fordTab1.setTabName(newTabName);
            getEntityManager().flush();
        }
        catch (InvalidStateException validationException)
        {
            exceptionOccurred = true;

            InvalidValue[] values = validationException.getInvalidValues();

            assertEquals(TabTemplate.MAX_TAB_NAME_MESSAGE, values[0].getMessage());
            assertEquals("Expected the property name 'tabName' to be the offending property name.", "tabName",
                    values[0].getPropertyName());
        }
        assertTrue("Expected a InvalidStateException to be thrown when an empty string " + "is set as the tabName",
                exceptionOccurred);
    }

    /**
     * Test that a tab must not have more than 50 characters in its name.
     */
    @Test
    public void testTabNameLongerThanMaxLengthConstraint()
    {
        boolean exceptionOccurred = false;
        String newTabName = ValidationTestHelper.generateString(TabTemplate.MAX_TAB_NAME_LENGTH + 1);

        assertEquals("Test new tab name should be 17 characters long", TabTemplate.MAX_TAB_NAME_LENGTH + 1, newTabName
                .length());

        try
        {
            Tab fordTab1 = jpaTabMapper.findById(fordsFirstTabId);
            fordTab1.setTabName(newTabName);
            getEntityManager().flush();
        }
        catch (InvalidStateException validationException)
        {
            exceptionOccurred = true;

            InvalidValue[] values = validationException.getInvalidValues();

            assertEquals(TabTemplate.MAX_TAB_NAME_MESSAGE, values[0].getMessage());
            assertEquals("Expected the property name 'tabName' to be the offending property name.", "tabName",
                    values[0].getPropertyName());
        }
        assertTrue("Expected a InvalidStateException to be thrown when a 17-character string "
                + "is set as the tabName", exceptionOccurred);
    }

    /**
     * Test that a tab can have a tab name that's 1 character long.
     */
    @Test
    public void testTabNameAtMinLengthConstraint()
    {
        Tab fordTab1 = jpaTabMapper.findById(fordsFirstTabId);
        fordTab1.setTabName("1");
        getEntityManager().flush();
    }

    /**
     * Test that a tab can have a tab name that's 16 characters long.
     */
    @Test
    public void testTabNameAtMaxLengthConstraint()
    {
        String newTabName = ValidationTestHelper.generateString(TabTemplate.MAX_TAB_NAME_LENGTH);

        assertEquals("Test new tab name should be 17 characters long", TabTemplate.MAX_TAB_NAME_LENGTH, newTabName
                .length());

        Tab fordTab1 = jpaTabMapper.findById(fordsFirstTabId);
        fordTab1.setTabName(newTabName);
        getEntityManager().flush();
    }

    /**
     * Test deleting a gadget, then undeleting it.
     */
    @Test
    public void testDeleteThenUndelete()
    {
        Tab tab = jpaTabMapper.findById(fordsFirstTabId);
        Gadget fordGadget1 = jpaGadgetMapper.findById(fordsFirstTabFirstGadgetId);

        final int expectedGadgetCountAfterDeletingAndUndeleting = 3;

        // delete the first gadget
        try
        {
            jpaTabMapper.deleteGadget(fordGadget1);
        }
        catch (GadgetDeletionException e)
        {
            throw new RuntimeException(e);
        }
        jpaTabMapper.flush();

        // clear the getEntityManager() so we can re-query the collection
        getEntityManager().clear();

        try
        {
            jpaTabMapper.undeleteGadget(fordsFirstTabFirstGadgetId);
        }
        catch (GadgetUndeletionException e)
        {
            throw new RuntimeException(e);
        }
        jpaTabMapper.flush();

        // clear the getEntityManager() so we can re-query the collection
        getEntityManager().clear();

        // re-get and assert
        tab = jpaTabMapper.findById(fordsFirstTabId);
        assertEquals("Expected 3 gadgets in Ford's startPage after deleting and undeleting.",
                expectedGadgetCountAfterDeletingAndUndeleting, tab.getGadgets().size());

        assertEquals("Expected the undeleted gadget to be 1st again.", fordsFirstTabFirstGadgetId, tab.getGadgets()
                .get(0).getId());

        assertEquals("Expected the previously 1st gadget to be 2nd after deleting and undeleting the first",
                fordsFirstTabSecondGadgetId, tab.getGadgets().get(1).getId());

        assertEquals("Expected the previously 2nd gadget to be 3rd after deleting and undeleting the first",
                fordsFirstTabThirdGadgetId, tab.getGadgets().get(2).getId());
    }

    /**
     * Test deleting the only gadget in a zone, then undeleting it.
     */
    @Test
    public void testDeleteThenUndeleteTheOnlyGadgetOnAPage()
    {
        Tab tab = jpaTabMapper.findById(carlsFirstTabId);
        Gadget carlGadget1 = jpaGadgetMapper.findById(carlsFirstTabFirstGadgetId);

        final int expectedGadgetCountAfterDeletingAndUndeleting = 1;

        // delete the first gadget
        try
        {
            jpaTabMapper.deleteGadget(carlGadget1);
        }
        catch (GadgetDeletionException e)
        {
            throw new RuntimeException(e);
        }
        jpaTabMapper.flush();

        // clear the getEntityManager() so we can re-query the collection
        getEntityManager().clear();

        try
        {
            jpaTabMapper.undeleteGadget(carlsFirstTabFirstGadgetId);
        }
        catch (GadgetUndeletionException e)
        {
            throw new RuntimeException(e);
        }
        jpaTabMapper.flush();

        // clear the getEntityManager() so we can re-query the collection
        getEntityManager().clear();

        // re-get and assert
        tab = jpaTabMapper.findById(carlsFirstTabId);
        assertEquals("Expected 1 gadget in Carl's startPage after deleting and undeleting.",
                expectedGadgetCountAfterDeletingAndUndeleting, tab.getGadgets().size());

        assertEquals("Expected the undeleted gadget to be 1st again.", carlsFirstTabFirstGadgetId, tab.getGadgets()
                .get(0).getId());
    }

    /**
     * This test deletes a record and then ensures that the record still remains in the database so that it can be
     * undeleted.
     * 
     * @throws GadgetDeletionException
     *             thrown on error during gadget deletion.
     */
    @Test
    public void testDeletedRecordRemainsAvailableinDBForUndelete() throws GadgetDeletionException
    {
        Tab fordsTab = jpaTabMapper.findById(fordsFirstTabId);
        Gadget fordGadget1 = jpaGadgetMapper.findById(fordsFirstTabFirstGadgetId);

        // delete the first gadget of the first tab
        jpaTabMapper.deleteGadget(fordGadget1);
        jpaTabMapper.flush();

        Boolean deletedFlag = (Boolean) getEntityManager().createQuery(
                "select deleted from Gadget where id = :gadgetId and "
                        + "tabTemplateId = :tabTemplateId and deleted = true").setParameter("tabTemplateId",
                fordsTab.getTemplate().getId()).setParameter("gadgetId", fordsFirstTabFirstGadgetId).getSingleResult();

        assertEquals("Expected that after deleting a gadget, it's still tied to the " + "tab, the gadgetIndex is null,"
                + " and the gadget is marked as deleted", true, deletedFlag.booleanValue());
    }

    /**
     * Verify the configurable time window for permanent deletion of a gadget - for a gadgets that's still within the
     * window.
     * 
     * @throws GadgetDeletionException
     *             thrown on exception deleting the tab.
     */
    @Test
    public void testDeletedGadgetWithinWindowShouldRemainAfterAnotherDelete() throws GadgetDeletionException
    {
        final int undeleteGadgetWindowInMinutes = 110;
        final int minutesSinceTestGadgetWasDeleted = 100;

        // set the undelete gadget window
        jpaTabMapper.setUndeleteGadgetWindowInMinutes(undeleteGadgetWindowInMinutes);

        // set the date the gadget was deleted
        GregorianCalendar gadgetDeletionDate = new GregorianCalendar();
        gadgetDeletionDate.add(Calendar.MINUTE, -minutesSinceTestGadgetWasDeleted);

        getEntityManager().createQuery(
                "update versioned Gadget set " + "dateDeleted = :deletedTimestamp " + "where id = :gadgetId")
                .setParameter("deletedTimestamp", gadgetDeletionDate.getTime()).setParameter("gadgetId",
                        fordsDeletedGadgetId).executeUpdate();

        // delete the active gadget
        jpaTabMapper.deleteGadget(jpaGadgetMapper.findById(fordsFirstTabFirstGadgetId));

        // make sure that the deleted gadget is still available
        int resultCount = getEntityManager().createQuery(
                "select gadgetDefinition from Gadget where id = :gadgetId and deleted = true").setParameter("gadgetId",
                fordsDeletedGadgetId).getResultList().size();

        assertEquals(
                "After deleting a gadget, the gadget that was deleted within the undo window should still be present.",
                1, resultCount);
    }

    /**
     * Test deleting the first gadget in a collection leaves the collection with the other gadgets intact.
     * 
     * @throws GadgetDeletionException
     *             thrown on error during tab deletion.
     */
    @Test
    public void testDeletingGadgetRemovesFromCollection() throws GadgetDeletionException
    {
        Tab tab = jpaTabMapper.findById(fordsFirstTabId);
        Gadget fordTab1Gadget1 = jpaGadgetMapper.findById(fordsFirstTabFirstGadgetId);

        // delete the first gadget
        jpaTabMapper.deleteGadget(fordTab1Gadget1);
        jpaTabMapper.flush();

        // clear the getEntityManager() so we can re-query the collection
        getEntityManager().clear();

        // re-get and assert
        tab = jpaTabMapper.findById(fordsFirstTabId);

        assertEquals("Expected 2 gadgets after deleting the first one.", 2, tab.getGadgets().size());

        assertEquals("Expected the previously second gadget to be the first now after deleting the first one.",
                fordsFirstTabSecondGadgetId, tab.getGadgets().get(0).getId());
    }

    /**
     * Verify the configurable time window for permanent deletion of a Gadget - for a gadget that's outside the window.
     * 
     * @throws GadgetDeletionException
     *             thrown on exception deleting the gadget.
     */
    @Test
    public void testDeletedGadgetOutsideWindowShouldBePermanentlyDeletedAfterAnotherDelete()
            throws GadgetDeletionException
    {
        final int undeleteGadgetWindowInMinutes = 90;
        final int minutesSinceTestGadgetWasDeleted = 100;

        // set the undelete gadget window
        jpaTabMapper.setUndeleteGadgetWindowInMinutes(undeleteGadgetWindowInMinutes);

        // set the date the gadget was deleted
        GregorianCalendar gadgetDeletionDate = new GregorianCalendar();
        gadgetDeletionDate.add(Calendar.MINUTE, -minutesSinceTestGadgetWasDeleted);
        getEntityManager().createQuery(
                "update versioned Gadget set " + "dateDeleted = :deletedTimestamp " + "where id = :gadgetId")
                .setParameter("deletedTimestamp", gadgetDeletionDate.getTime()).setParameter("gadgetId",
                        fordsDeletedGadgetId).executeUpdate();

        // delete the active gadget
        jpaTabMapper.deleteGadget(jpaGadgetMapper.findById(fordsFirstTabFirstGadgetId));

        // make sure that the deleted gadget is still available
        int resultCount = getEntityManager().createQuery(
                "select gadgetDefinition from Gadget where id = :gadgetId and deleted = true").setParameter("gadgetId",
                fordsDeletedGadgetId).getResultList().size();

        assertEquals("After deleting a Gadget, the Gadget that was deleted outside the undo "
                + "window should still be permanently deleted.", 0, resultCount);
    }

    /**
     * Test finding a tab by id.
     */
    @Test
    public void testFindById()
    {
        Long longId = fordsFirstTabId;
        assertNotNull(jpaTabMapper.findById(longId.intValue()));
    }

    /**
     * This test verifies that deleted items expire and are purged from the database after a period of time.
     * 
     * @throws GadgetDeletionException
     *             thrown on error during gadget deletion.
     */
    @Test
    public void testUndeleteExpiration() throws GadgetDeletionException
    {
        long fordsActiveGadgetId = fordsFirstTabFirstGadgetId;

        // check the existence of an already-deleted gadget
        GadgetDefinition gadgetDefinition = (GadgetDefinition) getEntityManager().createQuery(
                "select gadgetDefinition from Gadget where id = :gadgetId and deleted = true").setParameter("gadgetId",
                fordsDeletedGadgetId).getSingleResult();

        assertEquals("Could not find the already-deleted gadget", "http://www.example.com/gadget2.xml",
                gadgetDefinition.getUrl());

        // delete an item other than the one that is already deleted.
        jpaTabMapper.deleteGadget(jpaGadgetMapper.findById(fordsActiveGadgetId));

        // now make sure that the already-deleted gadget is gone - it should
        // have expired away
        int resultCount = getEntityManager().createQuery(
                "select gadgetDefinition from Gadget where id = :gadgetId and deleted = true").setParameter("gadgetId",
                fordsDeletedGadgetId).getResultList().size();

        assertEquals("After deleting a gadget, the gadget that was deleted a "
                + "long time ago should have been permanently deleted.", 0, resultCount);
    }

    /**
     * This test is trying to undelete the gadget that is designated as delete in the DBUnity Test Database.
     * 
     * @throws GadgetUndeletionException
     *             thrown during gadget undeletion.
     */
    @Test
    public void testUndeleteGadget() throws GadgetUndeletionException
    {
        Boolean deletedFlag;

        jpaTabMapper.undeleteGadget(fordsDeletedGadgetId);
        jpaTabMapper.flush();

        deletedFlag = (Boolean) getEntityManager().createQuery(
                "select de.deleted from Gadget de " + "where de.id = :gadgetId").setParameter("gadgetId",
                new Long(fordsDeletedGadgetId)).getSingleResult();
        assertEquals(false, deletedFlag.booleanValue());

        assertEquals("The gadget url of the undeleted gadget was not as expected.",
                "http://www.example.com/gadget2.xml", jpaGadgetMapper.findById(fordsDeletedGadgetId)
                        .getGadgetDefinition().getUrl());

    }

    /**
     * Test that undeleting a gadget restores the gadget to its original position and bumps the other gadgets back one.
     * 
     * @throws GadgetUndeletionException
     *             thrown on error during gadget undeletion.
     */
    @Test
    public void testUndeleteGadgetRestoresGadgetToItsOriginalPosition() throws GadgetUndeletionException
    {
        final int expectedGadgetCountBeforeUndelete = 7;

        // undelete the previously deleted gadget
        Tab tab = jpaTabMapper.findById(fordsThirdTabId);
        assertEquals("Expected 7 gadget in Ford's first tab before undeleting the previously-deleted gadget.",
                expectedGadgetCountBeforeUndelete, tab.getGadgets().size());

        jpaTabMapper.undeleteGadget(fordsDeletedGadgetId);
        jpaTabMapper.flush();

        // clear the getEntityManager()'s cache so we're going back to the
        // database.
        tab = jpaTabMapper.findById(fordsThirdTabId);
        assertEquals("Expected 8 gadgets in Ford's third tab after undeleting the previously-deleted gadget.",
                expectedGadgetCountBeforeUndelete + 1, tab.getGadgets().size());

        assertEquals("Expected the undeleted gadget to be 1st again.", fordsThirdTabFirstGadgetId, tab.getGadgets()
                .get(0).getId());

        assertEquals("Expected the previously 1st gadget to be 2nd after deleting and undeleting the first",
                fordsThirdTabSecondGadgetId, tab.getGadgets().get(1).getId());

        assertEquals("Expected the previously 2nd gadget to be 3rd after deleting and undeleting the first",
                fordsThirdTabThirdGadgetId, tab.getGadgets().get(2).getId());

        assertEquals("Expected the previously 2nd gadget to be 3rd after deleting and undeleting the first",
                fordsThirdTabFourthGadgetId, tab.getGadgets().get(3).getId());
    }

    /**
     * Test that undeleting a gadget that's not deleted throws a GadgetUndeleteException.
     * 
     * @throws GadgetUndeletionException
     *             thrown on error during gadget undeletion.
     */
    @Test
    public void testUndeleteThrowsExceptionWhenNotDeleted() throws GadgetUndeletionException
    {
        // try the deletion of the gadget that's not deleted - should throw a
        // GadgetUndeletionException
        boolean exceptionOccurred = false;
        try
        {
            jpaTabMapper.undeleteGadget(fordsFirstTabFirstGadgetId);
        }
        catch (GadgetUndeletionException ex)
        {
            exceptionOccurred = true;

            assertEquals("GadgetUndeletionException was thrown, but didn't contain the "
                    + " gadget id that the caller was trying to delete from.", fordsFirstTabFirstGadgetId, ex
                    .getGadgetId());
        }

        assertTrue("GadgetUndeletionException didn't occur when trying to delete a gadget that wasn't deleted.",
                exceptionOccurred);
    }

    /**
     * Test that getTabTemplate based on type actually returns the correct template.
     */
    @Test
    public void testGetTabTemplate()
    {
        final long welcomeId = 5555L;
        final long orgAboutId = 6666L;
        final long personAboutId = 7777L;
        final long applicationsId = 8888L;

        TabTemplate welcome = jpaTabMapper.getTabTemplate(TabType.WELCOME);
        TabTemplate orgAbout = jpaTabMapper.getTabTemplate(TabType.ORG_ABOUT);
        TabTemplate personAbout = jpaTabMapper.getTabTemplate(TabType.PERSON_ABOUT);
        TabTemplate applications = jpaTabMapper.getTabTemplate(TabType.APP);

        assertEquals(welcomeId, welcome.getId());
        assertEquals(TabType.WELCOME, welcome.getType());

        assertEquals(orgAboutId, orgAbout.getId());
        assertEquals(TabType.ORG_ABOUT, orgAbout.getType());

        assertEquals(personAboutId, personAbout.getId());
        assertEquals(TabType.PERSON_ABOUT, personAbout.getType());

        assertEquals(applicationsId, applications.getId());
        assertEquals(TabType.APP, applications.getType());

    }

    /**
     * This method tests the moveGadget method by moving a gadget within the same tab.
     */
    @Test
    public void testMoveGadgetWithinTab()
    {
        Tab fords1stTab = jpaTabMapper.findById(fordsFirstTabId);
        TabTemplate fords1stTabTemplate = fords1stTab.getTemplate();

        // Ensure starting state.
        assertEquals(fordsFirstTabFirstGadgetId, fords1stTabTemplate.getGadgets().get(0).getId());
        assertEquals("Zone number should be 0 before the move", 0, fords1stTabTemplate.getGadgets().get(0)
                .getZoneNumber());
        assertEquals("Zone index should be 0 before the move", 0, fords1stTabTemplate.getGadgets().get(0)
                .getZoneIndex());

        assertEquals(fordsFirstTabSecondGadgetId, fords1stTabTemplate.getGadgets().get(1).getId());
        assertEquals("Zone number should be 0 before the move", 0, fords1stTabTemplate.getGadgets().get(1)
                .getZoneNumber());
        assertEquals("Zone index should be 1 before the move", 1, fords1stTabTemplate.getGadgets().get(1)
                .getZoneIndex());

        assertEquals(fordsFirstTabThirdGadgetId, fords1stTabTemplate.getGadgets().get(2).getId());
        assertEquals("Zone number should be 2 before the move", 2, fords1stTabTemplate.getGadgets().get(2)
                .getZoneNumber());
        assertEquals("Zone number index be 0 before the move", 0, fords1stTabTemplate.getGadgets().get(2)
                .getZoneIndex());

        jpaTabMapper.moveGadget(fordsFirstTabFirstGadgetId, fordsFirstTabId, 0, 0, fordsFirstTabId, 1, 2);

        jpaTabMapper.flush();

        getEntityManager().clear();

        Tab fords1stTabAfterMove = jpaTabMapper.findById(fordsFirstTabId);
        TabTemplate fords1stTabTemplateAfterMove = fords1stTabAfterMove.getTemplate();

        // Assert after state.
        assertEquals(fordsFirstTabSecondGadgetId, fords1stTabTemplateAfterMove.getGadgets().get(0).getId());
        assertEquals("Zone number should be 0 after the move", 0, fords1stTabTemplateAfterMove.getGadgets().get(0)
                .getZoneNumber());
        assertEquals("Zone index should be 0 after the move", 0, fords1stTabTemplateAfterMove.getGadgets().get(0)
                .getZoneIndex());

        assertEquals(fordsFirstTabThirdGadgetId, fords1stTabTemplateAfterMove.getGadgets().get(1).getId());
        assertEquals("Zone number should be 2 after the move", 2, fords1stTabTemplateAfterMove.getGadgets().get(1)
                .getZoneNumber());
        assertEquals("Zone index should be 0 after the move", 0, fords1stTabTemplateAfterMove.getGadgets().get(1)
                .getZoneIndex());

        assertEquals(fordsFirstTabFirstGadgetId, fords1stTabTemplateAfterMove.getGadgets().get(2).getId());
        assertEquals("Zone number for moved gadget should be 2 after the move", 2, fords1stTabTemplateAfterMove
                .getGadgets().get(2).getZoneNumber());
        assertEquals("Zone index for moved gadget should be 1 after the move", 1, fords1stTabTemplateAfterMove
                .getGadgets().get(2).getZoneIndex());
    }

    /**
     * This method tests the moveGadget method by moving a gadget to another tab.
     */
    @Test
    public void testMoveGadgetToAnotherTab()
    {
        Tab fords1stTab = jpaTabMapper.findById(fordsFirstTabId);
        Tab fords3rdTab = jpaTabMapper.findById(fordsThirdTabId);
        TabTemplate fords1stTabTemplate = fords1stTab.getTemplate();
        TabTemplate fords3rdTabTemplate = fords3rdTab.getTemplate();

        // Ensure starting state.
        // First Tab
        assertEquals(fordsFirstTabFirstGadgetId, fords1stTabTemplate.getGadgets().get(0).getId());
        assertEquals("Zone number should be 0 before the move", 0, fords1stTabTemplate.getGadgets().get(0)
                .getZoneNumber());
        assertEquals("Zone index should be 0 before the move", 0, fords1stTabTemplate.getGadgets().get(0)
                .getZoneIndex());

        assertEquals(fordsFirstTabSecondGadgetId, fords1stTabTemplate.getGadgets().get(1).getId());
        assertEquals("Zone number should be 0 before the move", 0, fords1stTabTemplate.getGadgets().get(1)
                .getZoneNumber());
        assertEquals("Zone index should be 1 before the move", 1, fords1stTabTemplate.getGadgets().get(1)
                .getZoneIndex());

        assertEquals(fordsFirstTabThirdGadgetId, fords1stTabTemplate.getGadgets().get(2).getId());
        assertEquals("Zone number should be 2 before the move", 2, fords1stTabTemplate.getGadgets().get(2)
                .getZoneNumber());
        assertEquals("Zone number index be 0 before the move", 0, fords1stTabTemplate.getGadgets().get(2)
                .getZoneIndex());

        // Third tab
        assertEquals(fordsThirdTabFirstGadgetId, fords3rdTabTemplate.getGadgets().get(0).getId());
        assertEquals("Zone number should be 0 before the move", 0, fords3rdTabTemplate.getGadgets().get(0)
                .getZoneNumber());
        assertEquals("Zone index should be 0 before the move", 0, fords3rdTabTemplate.getGadgets().get(0)
                .getZoneIndex());

        assertEquals(fordsThirdTabSecondGadgetId, fords3rdTabTemplate.getGadgets().get(1).getId());
        assertEquals("Zone number should be 0 before the move", 0, fords3rdTabTemplate.getGadgets().get(1)
                .getZoneNumber());
        assertEquals("Zone index should be 1 before the move", 1, fords3rdTabTemplate.getGadgets().get(1)
                .getZoneIndex());

        assertEquals(fordsThirdTabThirdGadgetId, fords3rdTabTemplate.getGadgets().get(2).getId());
        assertEquals("Zone number should be 0 before the move", 0, fords3rdTabTemplate.getGadgets().get(2)
                .getZoneNumber());
        assertEquals("Zone index should be 2 before the move", 2, fords3rdTabTemplate.getGadgets().get(2)
                .getZoneIndex());

        assertEquals(fordsThirdTabFourthGadgetId, fords3rdTabTemplate.getGadgets().get(3).getId());
        assertEquals("Zone number should be 1 before the move", 1, fords3rdTabTemplate.getGadgets().get(3)
                .getZoneNumber());
        assertEquals("Zone index should be 0 before the move", 0, fords3rdTabTemplate.getGadgets().get(3)
                .getZoneIndex());

        jpaTabMapper.moveGadget(fordsFirstTabFirstGadgetId, fordsFirstTabId, 0, 0, fordsThirdTabId, 2, 0);

        jpaTabMapper.flush();

        getEntityManager().clear();

        Tab fords1stTabAfterMove = jpaTabMapper.findById(fordsFirstTabId);
        Tab fords3rdTabAfterMove = jpaTabMapper.findById(fordsThirdTabId);
        TabTemplate fords1stTabTemplateAfterMove = fords1stTabAfterMove.getTemplate();
        TabTemplate fords3rdTabTemplateAfterMove = fords3rdTabAfterMove.getTemplate();

        // Assert after state.
        assertEquals(fordsFirstTabSecondGadgetId, fords1stTabTemplateAfterMove.getGadgets().get(0).getId());
        assertEquals("Zone number should be 0 after the move", 0, fords1stTabTemplateAfterMove.getGadgets().get(0)
                .getZoneNumber());
        assertEquals("Zone index should be 0 after the move", 0, fords1stTabTemplateAfterMove.getGadgets().get(0)
                .getZoneIndex());

        assertEquals(fordsFirstTabThirdGadgetId, fords1stTabTemplateAfterMove.getGadgets().get(1).getId());
        assertEquals("Zone number should be 0 after the move", 2, fords1stTabTemplateAfterMove.getGadgets().get(1)
                .getZoneNumber());
        assertEquals("Zone index should be 1 after the move", 0, fords1stTabTemplateAfterMove.getGadgets().get(1)
                .getZoneIndex());

        // Third tab
        assertEquals(fordsThirdTabFirstGadgetId, fords3rdTabTemplateAfterMove.getGadgets().get(0).getId());
        assertEquals("Zone number should be 0 after the move", 0, fords3rdTabTemplateAfterMove.getGadgets().get(0)
                .getZoneNumber());
        assertEquals("Zone index should be 0 after the move", 0, fords3rdTabTemplateAfterMove.getGadgets().get(0)
                .getZoneIndex());

        assertEquals(fordsThirdTabSecondGadgetId, fords3rdTabTemplateAfterMove.getGadgets().get(1).getId());
        assertEquals("Zone number should be 0 after the move", 0, fords3rdTabTemplateAfterMove.getGadgets().get(1)
                .getZoneNumber());
        assertEquals("Zone index should be 1 after the move", 1, fords3rdTabTemplateAfterMove.getGadgets().get(1)
                .getZoneIndex());

        // Moved gadget
        assertEquals(fordsFirstTabFirstGadgetId, fords3rdTabTemplateAfterMove.getGadgets().get(2).getId());
        assertEquals("Zone number for moved gadget should be 0 after the move", 0, fords3rdTabTemplateAfterMove
                .getGadgets().get(2).getZoneNumber());
        assertEquals("Zone index for moved gadget should be 2 after the move", 2, fords3rdTabTemplateAfterMove
                .getGadgets().get(2).getZoneIndex());

        assertEquals(fordsThirdTabThirdGadgetId, fords3rdTabTemplateAfterMove.getGadgets().get(3).getId());
        assertEquals("Zone number should be 0 after the move", 0, fords3rdTabTemplateAfterMove.getGadgets().get(3)
                .getZoneNumber());
        assertEquals("Zone index should be 3 after the move", 3, fords3rdTabTemplateAfterMove.getGadgets().get(3)
                .getZoneIndex());

        assertEquals(fordsThirdTabFourthGadgetId, fords3rdTabTemplateAfterMove.getGadgets().get(4).getId());
        assertEquals("Zone number should be 1 after the move", 1, fords3rdTabTemplateAfterMove.getGadgets().get(4)
                .getZoneNumber());
        assertEquals("Zone index should be 0 after the move", 0, fords3rdTabTemplateAfterMove.getGadgets().get(4)
                .getZoneIndex());
    }

}
