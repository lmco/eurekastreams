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
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabGroup;
import org.eurekastreams.server.persistence.exceptions.TabDeletionException;
import org.eurekastreams.server.persistence.exceptions.TabUndeletionException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for testing the JPA Implementation of the TabGroup Mapper interface. The tests contained in
 * here ensure proper interaction with the database.
 */
public class TabGroupMapperTest extends DomainEntityMapperTest
{
    /**
     * Log.
     */
    Log log = LogFactory.make();

    /**
     * JpaTabGroupMapper - the system under test.
     */
    @Autowired
    private TabGroupMapper jpaTabGroupMapper;

    /**
     * JpaTabMapper - a mapper we need to help assert tab existence.
     */
    @Autowired
    private TabMapper jpaTabMapper;

    /**
     * Reset the deleted tab expiration before each test.
     */
    @Before
    public void setup()
    {
        jpaTabGroupMapper.setUndeleteTabWindowInMinutes(undeleteWindowInMinutes);
    }

    /**
     * Test the DBUnit XML Dataset - Tabs.
     */
    @Test
    public void testDBUnitDatasetTabs()
    {
        final int expectedTabCount = 3;

        TabGroup tabGroup = jpaTabGroupMapper.findById(fordsStartPageId);
        List<Tab> tabs = tabGroup.getTabs();

        assertEquals("Expected the DBUnit-loaded TabGroup with ID=" + fordsStartPageId + " to have " + expectedTabCount
                + "(non-deleted) tabs", expectedTabCount, tabs.size());

        // Assert the order is 1,2,3
        assertEquals("Expected Ford's first tab in his first TabGroup to be called 'Ford Tab 1' from DBUnit setup.",
                "Ford Tab 1", tabs.get(0).getTabName());
        assertEquals("Expected Ford's first tab in his first TabGroup to be called 'Ford Tab 2' from DBUnit setup.",
                "Ford Tab 2", tabs.get(1).getTabName());
        assertEquals("Expected Ford's first tab in his first TabGroup to be called 'Ford Tab 3' from DBUnit setup.",
                "Ford Tab 3", tabs.get(2).getTabName());
    }

    /**
     * Test the domain entity name of the mapper - used for parent class generic operations.
     */
    @Test
    public void testGetDomainEntityName()
    {
        assertEquals("Domain entity name should be 'TabGroup'", "TabGroup", jpaTabGroupMapper.getDomainEntityName());
    }

    /**
     * Test persisting a start tabGroup.
     */
    @Test
    public void testInsert()
    {
        TabGroup p = new TabGroup();
        jpaTabGroupMapper.insert(p);
        assertTrue(p.getId() > 0);
    }

    /**
     * Test inserting a tab group, then finding the user by ID when the object is still in object cache.
     */
    @Test
    public void testFindByIdWhenCached()
    {
        assertSame("When finding a Person by ID and that object exists in object cache, "
                + "expected the cached instance to be returned.", jpaTabGroupMapper.findById(fordsStartPageId),
                jpaTabGroupMapper.findById(fordsStartPageId));
    }

    /**
     * Test adding a tab to a tab group persists when we update the tab group.
     */
    @Test
    public void testUpdateAddNewTab()
    {
        TabGroup tabGroup = jpaTabGroupMapper.findById(fordsStartPageId);
        tabGroup.getTabs().add(new Tab("Foo", Layout.THREECOLUMN));
        jpaTabGroupMapper.insert(tabGroup);

        getEntityManager().flush();
        getEntityManager().clear();

        tabGroup = jpaTabGroupMapper.findById(fordsStartPageId);
        assertEquals("Attemped updating a Person after adding a Tab to his first "
                + "TabGroup, then flushing and clearing the EntityManager.  "
                + "Expected to see the new tab after re-loading the Person.", "Foo", tabGroup.getTabs().get(
                tabGroup.getTabs().size() - 1).getTabName());
    }

    /**
     * This test deletes a record and then ensures that the record still remains in the database so that it can be
     * undeleted.
     * 
     * @throws TabDeletionException
     *             thrown on error during tab deletion.
     */
    @Test
    public void testDeletedRecordRemainsAvailableinDBForUndelete() throws TabDeletionException
    {
        TabGroup fordsTabGroup = jpaTabGroupMapper.findById(fordsStartPageId);
        Tab fordTab1 = jpaTabMapper.findById(fordsFirstTabId);

        long pageId = fordsTabGroup.getId();
        long tabId = fordTab1.getId();
        int tabIndex = fordTab1.getTabIndex();

        // delete the first tab
        jpaTabGroupMapper.deleteTab(fordTab1);
        jpaTabGroupMapper.flush();

        Boolean deletedFlag = (Boolean) getEntityManager().createQuery(
                "select deleted from Tab where id = :tabId and "
                        + "tabGroupId = :tabGroupId and deleted = true and tabIndex = :tabIndex").setParameter(
                "tabGroupId", pageId).setParameter("tabId", tabId).setParameter("tabIndex", tabIndex).getSingleResult();

        assertEquals("Expected that after deleting a tab, it's still tied to the "
                + "tabGroup, the tabIndex is null, and the tab is marked as deleted", true, deletedFlag.booleanValue());
    }

    /**
     * Test deleting the first tab in a collection leaves the collection with the other tabs intact.
     * 
     * @throws TabDeletionException
     *             thrown on error during tab deletion.
     */
    @Test
    public void testDeletingTabRemovesFromCollection() throws TabDeletionException
    {
        TabGroup tabGroup = jpaTabGroupMapper.findById(fordsStartPageId);
        Tab fordTab1 = jpaTabMapper.findById(fordsFirstTabId);

        // delete the first tab
        jpaTabGroupMapper.deleteTab(fordTab1);
        jpaTabGroupMapper.flush();

        // clear the entityManager so we can re-query the collection
        getEntityManager().clear();

        // re-get and assert
        tabGroup = jpaTabGroupMapper.findById(fordsStartPageId);

        assertEquals("Expected 2 tabs after deleting the first one.", 2, tabGroup.getTabs().size());

        assertEquals("Expected the previously second tab to be the first now after deleting the first one.",
                fordsSecondTabId, tabGroup.getTabs().get(0).getId());
    }

    /**
     * Test deleting a tab, then undeleting it.
     */
    @Test
    public void testDeleteThenUndelete()
    {
        TabGroup tabGroup = jpaTabGroupMapper.findById(fordsStartPageId);
        Tab fordTab2 = jpaTabMapper.findById(fordsSecondTabId);

        final int expectedTabCountAfterDeletingAndUndeleting = 3;

        // delete the first tab
        try
        {
            jpaTabGroupMapper.deleteTab(fordTab2);
        }
        catch (TabDeletionException e)
        {
            throw new RuntimeException(e);
        }
        jpaTabGroupMapper.flush();

        // clear the entityManager so we can re-query the collection
        getEntityManager().clear();

        try
        {
            jpaTabGroupMapper.undeleteTab(fordsSecondTabId);
        }
        catch (TabUndeletionException e)
        {
            log.error(e);
        }
        jpaTabGroupMapper.flush();

        // clear the entityManager so we can re-query the collection
        getEntityManager().clear();

        // re-get and assert
        tabGroup = jpaTabGroupMapper.findById(fordsStartPageId);
        assertEquals("Expected 3 tabs in Ford's startPage after deleting and undeleting.",
                expectedTabCountAfterDeletingAndUndeleting, tabGroup.getTabs().size());

        assertEquals(
                "Expected the previously 1st tab to still be 1st after deleting and undeleting the original second tab",
                fordsFirstTabId, tabGroup.getTabs().get(0).getId());

        assertEquals("Expected the original 2nd tab to 2nd again after deleting and undeleting it", fordsSecondTabId,
                tabGroup.getTabs().get(1).getId());

        assertEquals("Expected the previously 2nd tab to be 3rd after deleting and undeleting the original second tab",
                fordsThirdTabId, tabGroup.getTabs().get(2).getId());
    }

    /**
     * Test that undeleting a tab that's not deleted throws a TabUndeleteException.
     * 
     * @throws TabUndeletionException
     *             thrown on error during tab undeletion.
     */
    @Test
    public void testUndeleteThrowsExceptionWhenNotDeleted() throws TabUndeletionException
    {
        // try the deletion of the tab that's not deleted - should throw a
        // TabUndeletionException
        boolean exceptionOccurred = false;
        try
        {
            jpaTabGroupMapper.undeleteTab(fordsFirstTabId);
        }
        catch (TabUndeletionException ex)
        {
            exceptionOccurred = true;

            assertEquals("TabUndeletionException was thrown, but didn't contain the "
                    + " tab id that the caller was trying to delete from.", fordsFirstTabId, ex.getTabId());
        }

        assertTrue("TabUndeletionException didn't occur when trying to delete a Tab that wasn't deleted.",
                exceptionOccurred);
    }

    /**
     * This test is trying to undelete the Tab that is designated as delete in the DBUnity Test Database.
     * 
     * @throws TabUndeletionException
     *             thrown during tab undeletion.
     */
    @Test
    public void testUndeleteTab() throws TabUndeletionException
    {
        Boolean deletedFlag;

        jpaTabGroupMapper.undeleteTab(fordsDeletedTabId);
        jpaTabGroupMapper.flush();

        deletedFlag = (Boolean) getEntityManager().createQuery(
                "select de.deleted from Tab de " + "where de.id = :tabId").setParameter("tabId",
                new Long(fordsDeletedTabId)).getSingleResult();
        assertEquals(false, deletedFlag.booleanValue());

        assertEquals("The tab name of the undeleted tab was not as expected.", "Ford Tab 4", jpaTabMapper.findById(
                fordsDeletedTabId).getTabName());

    }

    /**
     * Test that undeleting a tab restores the tab to its original position and bumps the other tabs back one.
     * 
     * @throws TabUndeletionException
     *             thrown on error during tab undeletion.
     */
    @Test
    public void testUndeleteTabRestoresTabToItsOriginalPosition() throws TabUndeletionException
    {
        final int expectedTabCountBeforeUndelete = 3;

        // undelete the previously deleted tab
        TabGroup tabGroup = jpaTabGroupMapper.findById(fordsStartPageId);
        assertEquals("Expected 3 tabs in Ford's first tabGroup before undeleting the previously-deleted tab.",
                expectedTabCountBeforeUndelete, tabGroup.getTabs().size());

        jpaTabGroupMapper.undeleteTab(fordsDeletedTabId);
        jpaTabGroupMapper.flush();

        // clear the getEntityManager()'s cache so we're going back to the
        // databGroupase.
        tabGroup = jpaTabGroupMapper.findById(fordsStartPageId);
        assertEquals("Expected 4 tabs in Ford's third tabGroup after undeleting the previously-deleted tab.",
                expectedTabCountBeforeUndelete + 1, tabGroup.getTabs().size());

        assertEquals(
                "Expected the previously 1st tab to still be 1st after deleting and undeleting the original 2nd tab",
                fordsFirstTabId, tabGroup.getTabs().get(0).getId());

        assertEquals("Expected the undeleted original 2nd tab to be 2nd again.", fordsDeletedTabId, tabGroup.getTabs()
                .get(1).getId());

        assertEquals("Expected the previously 2nd tab to be 3rd after deleting and undeleting the original 2nd tab",
                fordsSecondTabId, tabGroup.getTabs().get(2).getId());

        assertEquals("Expected the previously 3rd tab to be 4th after deleting and undeleting the original 2nd tab",
                fordsThirdTabId, tabGroup.getTabs().get(3).getId());
    }

    /**
     * This test verifies that deleted items expire and are purged from the database after a period of time.
     * 
     * @throws TabDeletionException
     *             thrown on error during tab deletion.
     */
    @Test
    public void testUndeleteExpiration() throws TabDeletionException
    {
        long fordsActiveTabId = fordsFirstTabId;

        int resultCount = getEntityManager()
                .createQuery("select version from Tab where id = :tabId and deleted = true").setParameter("tabId",
                        fordsDeletedTabId).getResultList().size();

        assertEquals("Could not find the already-deleted Tab", 1, resultCount);

        // delete an item other than the one that is already deleted.
        jpaTabGroupMapper.deleteTab(jpaTabMapper.findById(fordsActiveTabId));

        // now make sure that the already-deleted tab is gone - it should have
        // expired away
        resultCount = getEntityManager().createQuery("select version from Tab where id = :tabId and deleted = true")
                .setParameter("tabId", fordsDeletedTabId).getResultList().size();

        assertEquals(
                "After deleting a Tab, the Tab that was deleted a long time ago should have been permanently deleted.",
                0, resultCount);

        // TODO: make sure none of the gadgets from that tab exist
    }

    /**
     * Verify the configurable time window for permanent deletion of a Tab - for a tab that's still within the window.
     * 
     * @throws TabDeletionException
     *             thrown on exception deleting the tab.
     */
    @Test
    public void testDeletedTabWithinWindowShouldRemainAfterAnotherDelete() throws TabDeletionException
    {
        final int undeleteTabWindowInMinutes = 110;
        final int minutesSinceTestTabWasDeleted = 100;

        // set the undelete tab window
        jpaTabGroupMapper.setUndeleteTabWindowInMinutes(undeleteTabWindowInMinutes);

        // set the date the tab was deleted
        GregorianCalendar tabDeletionDate = new GregorianCalendar();
        tabDeletionDate.add(Calendar.MINUTE, -minutesSinceTestTabWasDeleted);
        getEntityManager().createQuery(
                "update versioned Tab set " + "dateDeleted = :deletedTimestamp " + "where id = :tabId").setParameter(
                "deletedTimestamp", tabDeletionDate.getTime()).setParameter("tabId", fordsDeletedTabId).executeUpdate();

        // delete the active tab
        jpaTabGroupMapper.deleteTab(jpaTabMapper.findById(fordsFirstTabId));

        // make sure that the deleted tab is still available
        int resultCount = getEntityManager()
                .createQuery("select version from Tab where id = :tabId and deleted = true").setParameter("tabId",
                        fordsDeletedTabId).getResultList().size();

        assertEquals("After deleting a Tab, the Tab that was deleted within the undo window should still be present.",
                1, resultCount);
    }

    /**
     * Verify the configurable time window for permanent deletion of a Tab - for a tab that's outside the window.
     * 
     * @throws TabDeletionException
     *             thrown on exception deleting the tab.
     */
    @Test
    public void testDeletedTabOutsideWindowShouldBePermanentlyDeletedAfterAnotherDelete() throws TabDeletionException
    {
        final int undeleteTabWindowInMinutes = 90;
        final int minutesSinceTestTabWasDeleted = 100;

        // set the undelete tab window
        jpaTabGroupMapper.setUndeleteTabWindowInMinutes(undeleteTabWindowInMinutes);

        // set the date the tab was deleted
        GregorianCalendar tabDeletionDate = new GregorianCalendar();
        tabDeletionDate.add(Calendar.MINUTE, -minutesSinceTestTabWasDeleted);
        getEntityManager().createQuery(
                "update versioned Tab set " + "dateDeleted = :deletedTimestamp " + "where id = :tabId").setParameter(
                "deletedTimestamp", tabDeletionDate.getTime()).setParameter("tabId", fordsDeletedTabId).executeUpdate();

        // delete the active tab
        jpaTabGroupMapper.deleteTab(jpaTabMapper.findById(fordsFirstTabId));

        // make sure that the deleted tab is still available
        int resultCount = getEntityManager()
                .createQuery("select version from Tab where id = :tabId and deleted = true").setParameter("tabId",
                        fordsDeletedTabId).getResultList().size();

        assertEquals("After deleting a Tab, the Tab that was deleted outside the undo "
                + "window should still be permanently deleted.", 0, resultCount);
    }

    /**
     * Test that when we add a tab to the end of a Page's Tabs collection, then flush, clear, and re-get, the Tab's
     * tabIndex is set to the last index.
     */
    @Test
    public void testAddTabSetsTabIndexToLast()
    {
        TabGroup tabGroup = jpaTabGroupMapper.findById(fordsStartPageId);
        Tab tab = new Tab("FooBar", Layout.THREECOLUMN);

        tabGroup.getTabs().add(tab);

        jpaTabGroupMapper.flush();
        jpaTabGroupMapper.clear();

        int expectedIndex = tabGroup.getTabs().size() - 1;
        long tabId = tab.getId();

        tab = jpaTabMapper.findById(tabId);

        assertEquals("Expected tabIndex to be tabs.size()-1 when adding a tab to a tabGroup, after flush()",
                expectedIndex, tab.getTabIndex());
    }

}
