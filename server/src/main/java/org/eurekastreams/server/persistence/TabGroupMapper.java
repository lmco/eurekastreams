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

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabGroup;
import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.server.persistence.exceptions.TabDeletionException;
import org.eurekastreams.server.persistence.exceptions.TabUndeletionException;

/**
 * This class provides the mapper functionality for TabGroup entities.
 */
public class TabGroupMapper extends DomainEntityMapper<TabGroup>
{
    /**
     * Constructor.
     * 
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public TabGroupMapper(final QueryOptimizer inQueryOptimizer)
    {
        super(inQueryOptimizer);
    }

    /**
     * logger.
     */
    private static Log logger = LogFactory.getLog(TabGroupMapper.class);

    /**
     * Default value for undeleteTabWindowInMinutes.
     */
    private final int defaultUndeleteTabWindowInMinutes = 20;

    /**
     * The number of minutes to allow a tab to be undeleted in, defaults to 20 minutes.
     */
    private int undeleteTabWindowInMinutes = defaultUndeleteTabWindowInMinutes;

    /**
     * Set the number of minutes to allow a tab to be undeleted in.
     * 
     * @param undeleteWindowInMinutes
     *            the number of minutes to allow a tab to be undeleted in.
     */
    public void setUndeleteTabWindowInMinutes(final int undeleteWindowInMinutes)
    {
        this.undeleteTabWindowInMinutes = undeleteWindowInMinutes;
    }

    /**
     * Get the name of the entity for JpaDomainEntityMapper.
     * 
     * @return The DomainEntityName.
     */
    @Override
    protected String getDomainEntityName()
    {
        return "TabGroup";
    }

    /**
     * Mark the input tab as deleted so that it may be undeleted later on.
     * 
     * @param tab
     *            The tab to delete.
     * @throws TabDeletionException
     *             thrown when the caller tries to delete a Tab from a TabGroup that doesn't own the input Tab.
     */
    public void deleteTab(final Tab tab) throws TabDeletionException
    {
        logger.debug("Attempting to delete the tab with id=" + tab.getId());

        // make sure the tab exists in the input TabGroup
        TabGroup tabGroup = null;
        try
        {
            tabGroup = getTabGroupByTabId(tab.getId(), false);
        }
        catch (Exception ex)
        {
            throw new TabDeletionException("Could not find either the specified Tab or TabGroup", ex, tab);
        }

        // remove the tab from the collection to rearrange the tabIndexes of the
        // other tabs
        if (tabGroup.getTabs().size() > 1)
        {
            tabGroup.getTabs().remove(tab);
        }

        // mark it as deleted, and re-attach it to the tabGroup, because the
        // previous statement detatched it.
        markTabAsDeleted(tabGroup, tab);

        // clean up all previously deleted tabs that are no longer in the
        // undelete time frame.
        cleanUpDeletedTabs();

    }

    /**
     * Implementation of the undelete method from the TabGroupMapper interface.
     * 
     * @param tabId
     *            id of the Tab to be undeleted.
     * @return Tab object represented by the Tab id.
     * @throws TabUndeletionException
     *             thrown when error undeleting a Tab.
     */

    public Tab undeleteTab(final long tabId) throws TabUndeletionException
    {
        // make sure the Tab exists in the input tabGroup
        TabGroup tabGroup = null;
        long start;
        try
        {
            start = System.currentTimeMillis();
            logger.debug("***Getting tab group by tab Id");
            tabGroup = getTabGroupByTabId(tabId, true);
            logger.debug("***Done getting tab group by tab Id (" + (System.currentTimeMillis() - start) + "ms");
        }
        catch (Exception ex)
        {
            throw new TabUndeletionException("Could not find either the specified Tab or tabGroup for TabId=" + tabId,
                    tabId);
        }

        start = System.currentTimeMillis();
        logger.debug("***Getting tab to undelete by tab Id and status");
        /* get the deleted Tab from the tab group */
        Tab tabToUndelete = (Tab) getEntityManager().createQuery("from Tab where id = :TabId and deleted = true")
                .setParameter("TabId", tabId).getSingleResult();
        logger.debug("***Done Getting tab to undelete by tab Id and status (" + (System.currentTimeMillis() - start)
                + "ms");

        if (tabToUndelete == null)
        {
            throw new TabUndeletionException("Failure when trying to get Tab with id=" + tabId, tabId);
        }

        try
        {
            /*
             * re-insert the undeleted tab into the collection
             */
            tabGroup.getTabs().add(tabToUndelete.getTabIndex(), tabToUndelete);

            /* update the status of the undeleted Tab in the database */
            start = System.currentTimeMillis();
            logger.debug("***Update tab status");
            getEntityManager()
                    .createQuery(
                            "update versioned Tab set deleted = false, "
                                    + "dateDeleted = null, tabGroupId = :tabGroupId " + "where id = :TabId")
                    .setParameter("TabId", tabToUndelete.getId()).setParameter("tabGroupId", tabGroup.getId())
                    .executeUpdate();
            logger.debug("***Done Update tab status (" + (System.currentTimeMillis() - start) + "ms)");

            logger.debug("Un-deleted the tab with id=" + tabToUndelete.getId());

            /* update the status of the template of the undeleted Tab */
            start = System.currentTimeMillis();
            logger.debug("***Update tab template status");
            getEntityManager()
                    .createQuery(
                            "update versioned TabTemplate set deleted = false, " + "dateDeleted = null "
                                    + "where id = :TabTemplateId")
                    .setParameter("TabTemplateId", tabToUndelete.getTemplate().getId()).executeUpdate();
            logger.debug("***Done Update tab template status (" + (System.currentTimeMillis() - start) + "ms)");

            logger.debug("Un-deleted the tab template with id=" + tabToUndelete.getTemplate().getId());

            /* update the status of the template's gadgets of the undeleted Tab */
            start = System.currentTimeMillis();
            logger.debug("***Update tab template's gadgets status");
            getEntityManager()
                    .createQuery(
                            "update versioned Gadget set deleted = false, " + "dateDeleted = null "
                                    + "where template.id = :TabTemplateId")
                    .setParameter("TabTemplateId", tabToUndelete.getTemplate().getId()).executeUpdate();
            logger.debug(//
            "***Done Update tab template's gadgets status (" + (System.currentTimeMillis() - start) + "ms)");

            logger.debug("Un-deleted gadgets belonging to tab template with id=" + tabToUndelete.getTemplate().getId());

            // refresh the restored tab to reload previously deleted components
            start = System.currentTimeMillis();
            logger.debug("***entity manager flush");
            // NOTE: DO NOT use entitymanager.refresh! It's far far faster to throw away object and re-query for it.
            // getEntityManager().refresh(tabToUndelete);
            getEntityManager().flush();
            getEntityManager().clear();
            logger.debug("***Done entity manager flush (" + (System.currentTimeMillis() - start) + "ms)");

            start = System.currentTimeMillis();
            logger.debug("***Getting tab to return by tab Id and status");
            /* get the deleted Tab from the tab group */
            tabToUndelete = (Tab) getEntityManager()
                    .createQuery("from Tab t left join fetch t.template where t.id = :tabId and t.deleted = 'false'")
                    .setParameter("tabId", tabId).getSingleResult();
            // Touch the gadgets so that they will be eagerly loaded.
            tabToUndelete.getGadgets().size();
            logger.debug("***Done Getting tab to return by tab Id and status (" + (System.currentTimeMillis() - start)
                    + "ms)");

            return tabToUndelete;
        }
        catch (Exception ex)
        {
            throw new TabUndeletionException("An error occurred while trying to undelete the Tab with TabId=" + tabId,
                    tabId);
        }
    }

    /**
     * Get the TabGroup by Tab id.
     * 
     * @param tabId
     *            The id of the tab to find the TabGroup for.
     * 
     * @param isDeleted
     *            whether to look for deleted or undeleted Tab.
     * 
     * @return the TabGroup that owns the input tabId.
     */
    public TabGroup getTabGroupByTabId(final long tabId, final boolean isDeleted)
    {
        logger.debug("Looking for the tab group that contains the " + (isDeleted ? "deleted" : "active")
                + " tab with tabId=" + tabId);

        Query q = getEntityManager()
                .createQuery("select t.tabGroup from Tab t where t.id = :tabId and t.deleted = :isDeleted")
                .setParameter("tabId", tabId).setParameter("isDeleted", isDeleted);

        return (TabGroup) q.getSingleResult();
    }

    /**
     * Mark the input tab as deleted so that it's no longer returned in queries but can be undeleted later on. The tab
     * would have just been removed from the TabGroup, so we need to set the tabGroupId back to the Tab, and the
     * tabIndex=null so that it's ignored by the collection.
     * 
     * Conditionally mark the tab template as deleted, if this tab is the last one so you can undelete the tab template
     * with it
     * 
     * @param tabGroup
     *            The tab group that contains tab.
     * @param tab
     *            The tab to mark as deleted.
     */
    private void markTabAsDeleted(final TabGroup tabGroup, final Tab tab)
    {
        GregorianCalendar currentDateTime = new GregorianCalendar();

        long count = getTabCountForTemplate(tab.getTemplate());

        // if you only have one tab left, mark the template and its gadgets
        // deleted too so you can undelete them
        if (count == 1)
        {
            getEntityManager()
                    .createQuery(
                            "update versioned Gadget set deleted = true, " + "dateDeleted = :deletedTimestamp "
                                    + "where template.id = :tabTemplateId")
                    .setParameter("deletedTimestamp", currentDateTime.getTime())
                    .setParameter("tabTemplateId", tab.getTemplate().getId()).executeUpdate();

            getEntityManager()
                    .createQuery(
                            "update versioned TabTemplate set deleted = true, " + "dateDeleted = :deletedTimestamp "
                                    + "where id = :tabTemplateId")
                    .setParameter("deletedTimestamp", currentDateTime.getTime())
                    .setParameter("tabTemplateId", tab.getTemplate().getId()).executeUpdate();
        }

        // still mark the tab deleted
        getEntityManager()
                .createQuery(
                        "update versioned Tab set deleted = true, " + "dateDeleted = :deletedTimestamp, "
                                + "tabIndex = :tabIndex, " + "tabGroupId = :tabGroupId " + "where id = :tabId")
                .setParameter("deletedTimestamp", currentDateTime.getTime()).setParameter("tabId", tab.getId())
                .setParameter("tabGroupId", tabGroup.getId()).setParameter("tabIndex", tab.getTabIndex())
                .executeUpdate();

    }

    /**
     * Returns number of Tabs associated with a TabTemplate.
     * 
     * @param template
     *            The Template to check.
     * @return Number of Tabs associated with a TabTemplate.
     */
    public long getTabCountForTemplate(final TabTemplate template)
    {
        String query = "select count( t ) from Tab t where t.template.id = :templateId ";
        Query countQuery = getEntityManager().createQuery(query);
        countQuery.setParameter("templateId", template.getId());

        long count = ((Long) countQuery.getSingleResult()).longValue();
        return count;
    }

    /**
     * Clean up deleted tabs here using the expired date set earlier. Currently this is hard-coded to be at least 20
     * (configurable) minutes since the tab was originally deleted, but could be much longer because it is dependent on
     * the next tab that is deleted. If one tab is deleted on Jan 1st and the next tab is deleted on March 1st, the 1st
     * tab will remain flagged as deleted in the database until March 1st so we definitely need a full timestamp for
     * this object.
     */
    private void cleanUpDeletedTabs()
    {
        GregorianCalendar expiredDateTime = new GregorianCalendar();
        expiredDateTime.add(Calendar.MINUTE, -undeleteTabWindowInMinutes);

        getEntityManager()
                .createQuery(
                        "delete from Gadget gd where gd.deleted = true " + "and gd.dateDeleted < :expiredTimestamp")
                .setParameter("expiredTimestamp", expiredDateTime.getTime()).executeUpdate();

        getEntityManager()
                .createQuery("delete from Tab de where de.deleted = true " + "and de.dateDeleted < :expiredTimestamp")
                .setParameter("expiredTimestamp", expiredDateTime.getTime()).executeUpdate();

        try
        {
            getEntityManager()
                    .createQuery(
                            "delete from TabTemplate de where de.deleted = true "
                                    + "and de.dateDeleted < :expiredTimestamp")
                    .setParameter("expiredTimestamp", expiredDateTime.getTime()).executeUpdate();
        }
        catch (Exception e)
        {
            // This should never happen because a tab template is not marked as deleted unless
            // there are no reference to it by active tabs
            logger.debug("Unable to delete a tab template because there is still a tab that references it");
        }

    }
}
