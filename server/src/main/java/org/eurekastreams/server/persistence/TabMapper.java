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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.server.domain.TabType;
import org.eurekastreams.server.persistence.exceptions.GadgetDeletionException;
import org.eurekastreams.server.persistence.exceptions.GadgetUndeletionException;

/**
 * This class provides the mapper functionality for Tab entities.
 */
public class TabMapper
{
    /**
     * Constructor.
     *
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public TabMapper(final QueryOptimizer inQueryOptimizer)
    {
        queryOptimizer = inQueryOptimizer;
    }

    /**
     * The QueryOptimizer to use for specialized functions.
     */
    private QueryOptimizer queryOptimizer;

    /**
     * The entity manager to use for all ORM operations.
     */
    private EntityManager entityManager;

    /**
     * Default value for undeleteGadgetWindowInMinutes.
     */
    private final int defaultUndeleteGadgetWindowInMinutes = 20;

    /**
     * The number of minutes to allow a gadget to be undeleted in, defaults to
     * 20 minutes.
     */
    private int undeleteGadgetWindowInMinutes = defaultUndeleteGadgetWindowInMinutes;

    /**
     * Logger instance for this class.
     */
    private Log logger = LogFactory.getLog(TabMapper.class);

    /**
     * Temp Method to get templates based on type.
     * Note: this method only returns a single result, but queries all
     * tab templates.  There is no way for the query to distinguish which tab template comes
     * back.
     *
     * @param type
     *            The TabType.
     * @return the TabTemplate.
     */
    public TabTemplate getTabTemplate(final TabType type)
    {
        Query q = entityManager.createQuery("FROM TabTemplate t where t.type=:tabTemplateType").setParameter(
                "tabTemplateType", type);
        return (TabTemplate) q.getSingleResult();
    }

    /**
     * Set the number of minutes to allow a gadget to be undeleted in.
     *
     * @param undeleteWindowInMinutes
     *            the number of minutes to allow a tab to be undeleted in.
     */
    public void setUndeleteGadgetWindowInMinutes(final int undeleteWindowInMinutes)
    {
        this.undeleteGadgetWindowInMinutes = undeleteWindowInMinutes;
    }

    /**
     * Set the entity manager to use for all ORM operations.
     *
     * @param inEntityManager
     *            the entity manager to use for all ORM operations.
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager inEntityManager)
    {
        this.entityManager = inEntityManager;
    }

    /**
     * Find the Tab by id and eagerly load the gadget collection.
     *
     * @param tabId
     *            ID of the Tab to look up.
     *
     * @return the entity with the input
     */
    public Tab findById(final Long tabId)
    {
        Query q = entityManager.createQuery(
                "from Tab t left join fetch t.template where t.id = :tabId and t.deleted = 'false'").setParameter(
                "tabId", tabId);
        Tab tab = (Tab) q.getSingleResult();

        // Touch the gadgets so that they will be eagerly loaded.
        tab.getGadgets().size();

        return tab;
    }

    /**
     * Find the parent Tab of the input Gadget id.
     *
     * @param gadgetId
     *            the Gadget id to find the parent Tab for.
     * @return the parent Tab
     */
    public TabTemplate findByGadgetId(final Long gadgetId)
    {
        return getTabTemplateByGadgetId(gadgetId, false);
    }

    /**
     * This method retrieves a tab with only a gadget id to start with.
     * @param gadgetId - id of the gadget used to retrieve the container tab.
     * @return - container tab of the gadget id passed in.
     */
    public Tab findTabByGadgetId(final Long gadgetId)
    {
        Query q = entityManager.createQuery(
                "SELECT t FROM Tab t, Gadget g "
                + "WHERE g.id = :gadgetId "
                + "AND g.template.id = t.template.id").setParameter("gadgetId", gadgetId);

        Tab tab = (Tab) q.getSingleResult();

        //Touch the gadgets so that they will be eagerly loaded.
        tab.getGadgets().size();

        return tab;
    }
    /**
     * Find the Tab by id.
     *
     * @param tabId
     *            ID of the Tab to look up
     * @return the entity with the input
     */
    public Tab findById(final Integer tabId)
    {
        return findById(tabId.longValue());
    }

    /**
     * Update all entities that have changed since they were loaded within the
     * same context.
     */
    public void flush()
    {
        entityManager.flush();
    }

    /**
     * Mark the input gadget as deleted so that it may be undeleted later on.
     *
     * @param gadgetToDelete
     *            The gadget to delete.
     * @throws GadgetDeletionException
     *             thrown when the caller tries to delete a gadget from a tab
     *             that doesn't own the input gadget.
     */
    public void deleteGadget(final Gadget gadgetToDelete) throws GadgetDeletionException
    {

        // find the tab to which the input gadget belongs
        TabTemplate tab = null;
        try
        {
            tab = getTabTemplateByGadgetId(gadgetToDelete.getId(), false);
        }
        catch (Exception ex)
        {
            throw new GadgetDeletionException("Could not find either the specified Gadget or Tab", gadgetToDelete
                    .getId());
        }

        // remove the gadget from the collection
        if (tab.getGadgets().size() > 1)
        {
            tab.getGadgets().remove(gadgetToDelete);
            tab.getGadgets().add(gadgetToDelete);
        }

        // rearrange the gadgetIndexes of the other gadgets
        for (Gadget currentGadget : tab.getGadgets())
        {
            if (currentGadget.getZoneNumber() == gadgetToDelete.getZoneNumber()
                    && currentGadget.getZoneIndex() > gadgetToDelete.getZoneIndex())
            {
                currentGadget.setZoneIndex(currentGadget.getZoneIndex() - 1);
            }
        }

        // mark gadget as deleted, and re-attach it to the tab, because the
        // previous statement detatched it.
        markGadgetAsDeleted(tab, gadgetToDelete);

        // clean up all previously deleted gadgets that are no longer in the
        // undelete time frame.
        cleanUpDeletedGadgets();

    }

    /**
     * Implementation of the undelete method from the TabMapper interface.
     *
     * @param gadgetId
     *            id of the gadget to be undeleted.
     * @return gadget object represented by the gadget id.
     * @throws GadgetUndeletionException
     *             thrown when error undeleting a gadget.
     */

    public Gadget undeleteGadget(final long gadgetId) throws GadgetUndeletionException
    {
        // make sure the gadget exists in the input tab
        TabTemplate template = null;
        try
        {
            template = getTabTemplateByGadgetId(gadgetId, true);
        }
        catch (Exception ex)
        {
            throw new GadgetUndeletionException("Could not find either the specified gadget or tab for gadgetId="
                    + gadgetId, gadgetId);
        }

        /* get the deleted gadget from the database */
        Gadget gadgetToUndelete = (Gadget) entityManager.createQuery(
                "from Gadget where id = :gadgetId and deleted = true").setParameter("gadgetId", gadgetId)
                .getSingleResult();

        if (gadgetToUndelete == null)
        {
            throw new GadgetUndeletionException("Failure when trying to get gadget with id=" + gadgetId, gadgetId);
        }

        try
        {
            /*
             * bump up the zone index of each gadget in the same zone as the
             * gadget to be undeleted
             */
            for (Gadget currentGadget : template.getGadgets())
            {

                if (currentGadget.getZoneNumber() == gadgetToUndelete.getZoneNumber()
                        && currentGadget.getZoneIndex() >= gadgetToUndelete.getZoneIndex())
                {
                    currentGadget.setZoneIndex(currentGadget.getZoneIndex() + 1);
                }
            }

            /* add the gadget back into the collection */
            template.getGadgets().add(gadgetToUndelete);

            /* update the status of the undeleted gadget in the database */
            entityManager.createQuery(
                    "update versioned Gadget set deleted = false, " + "dateDeleted = null, tabTemplateId = :tabId "
                            + "where id = :gadgetId").setParameter("gadgetId", gadgetToUndelete.getId()).setParameter(
                    "tabId", template.getId()).executeUpdate();

            return gadgetToUndelete;
        }
        catch (Exception ex)
        {
            throw new GadgetUndeletionException("An error occurred while trying to undelete the gadget with gadgetId="
                    + gadgetId, gadgetId);
        }
    }

    /**
     * Get the tab by gadget id.
     *
     * @param gadgetId
     *            The id of the gadget to find the tab for.
     *
     * @param isDeleted
     *            whether to look for deleted or undeleted gadget.
     *
     * @return the TabGroup that owns the input tabId.
     */
    private TabTemplate getTabTemplateByGadgetId(final long gadgetId, final boolean isDeleted)
    {
        return (TabTemplate) entityManager.createQuery(
                "select g.template from Gadget g where g.id = :gadgetId and g.deleted = :isDeleted").setParameter(
                "gadgetId", gadgetId).setParameter("isDeleted", isDeleted).getSingleResult();
    }

    /**
     * Mark the input gadget as deleted so that it's no longer returned in
     * queries but can be undeleted later on. The gadget would have just been
     * removed from the gadget, so we need to set the tabId back to the gadget
     * so that it's ignored by the collection.
     *
     * @param template
     *            The TabTemplate that contains the gadget.
     * @param gadget
     *            The gadget to mark as deleted.
     */
    private void markGadgetAsDeleted(final TabTemplate template, final Gadget gadget)
    {
        GregorianCalendar currentDateTime = new GregorianCalendar();

        entityManager.createQuery(
                "update versioned Gadget set deleted = true, "
                        + "dateDeleted = :deletedTimestamp, tabTemplateId = :tabTemplateId " + "where id = :gadgetId")
                .setParameter("deletedTimestamp", currentDateTime.getTime()).setParameter("gadgetId", gadget.getId())
                .setParameter("tabTemplateId", template.getId()).executeUpdate();
    }

    /**
     * Clean up deleted gadgets here using the expired date set earlier.
     * Currently this is hard-coded to be at least 20 (configurable) minutes
     * since the gadget was originally deleted, but could be much longer because
     * it is dependent on the next gadget that is deleted. If one gadget is
     * deleted on Jan 1st and the next gadget is deleted on March 1st, the 1st
     * gadget will remain flagged as deleted in the database until March 1st so
     * we definitely need a full timestamp for this object.
     */
    private void cleanUpDeletedGadgets()
    {
        GregorianCalendar expiredDateTime = new GregorianCalendar();
        expiredDateTime.add(Calendar.MINUTE, -undeleteGadgetWindowInMinutes);

        entityManager.createQuery(
                "delete from Gadget de where de.deleted = true " + "and de.dateDeleted < :expiredTimestamp")
                .setParameter("expiredTimestamp", expiredDateTime.getTime()).executeUpdate();
    }

    /**
     * This method is responsible for moving a gadget from one location to
     * another on the any gadget page.
     * @param gadgetId - id of the gadget that is being moved.
     * @param sourceTabTemplateId - id of the tab template where the gadget is moving
     *  from.
     * @param sourceZoneIndex - index of the position in the zone that the gadget
     *  is moving from.
     * @param sourceZoneNumber - number of the zone that the gadget is moving from.
     * @param targetTabTemplateId - id of the tab template where the gadget is moving to.
     * @param targetZoneIndex - index of the position in the zone that the gadget
     *  is moving to.
     * @param targetZoneNumber - number of the zone that the gadget is moving to.
     */
    public void moveGadget(final Long gadgetId,
            final Long sourceTabTemplateId,
            final Integer sourceZoneIndex,
            final Integer sourceZoneNumber,
            final Long targetTabTemplateId,
            final Integer targetZoneIndex,
            final Integer targetZoneNumber)
    {
        logger.debug("Moving gadget: " + gadgetId + " from tab templateid: " + sourceTabTemplateId
                + " zoneNumber: " + sourceZoneNumber + " zoneIndex: " + sourceZoneIndex + " To "
                + " tab templateid: " + targetTabTemplateId + " zoneNumber: " + targetZoneNumber
                + " zoneIndex: " + targetZoneIndex);

        //Create space for gadget in target tab template
        entityManager.createQuery("UPDATE versioned Gadget g SET g.zoneIndex = g.zoneIndex + 1 "
                + "WHERE g.template.id =:targetTabTemplateId "
                + "AND g.zoneIndex >=:targetZoneIndex "
                + "AND g.zoneNumber =:targetZoneNumber "
                + "AND g.deleted = false")
                    .setParameter("targetTabTemplateId", targetTabTemplateId)
                    .setParameter("targetZoneIndex", targetZoneIndex)
                    .setParameter("targetZoneNumber", targetZoneNumber).executeUpdate();

        //move gadget to target tab template
        entityManager.createQuery("UPDATE versioned Gadget g SET g.zoneIndex =:targetZoneIndex, "
                + "g.zoneNumber =:targetZoneNumber, "
                + "g.template.id =:targetTabTemplateId "
                + "WHERE g.id =:gadgetId")
                .setParameter("targetZoneIndex", targetZoneIndex)
                .setParameter("targetZoneNumber", targetZoneNumber)
                .setParameter("targetTabTemplateId", targetTabTemplateId)
                .setParameter("gadgetId", gadgetId).executeUpdate();

        //clean up indexes from source tab template
        entityManager.createQuery("UPDATE versioned Gadget g SET g.zoneIndex = g.zoneIndex - 1 "
                + "WHERE g.template.id =:sourceTabTemplateId "
                + "AND g.zoneIndex >:sourceZoneIndex "
                + "AND g.zoneNumber =:sourceZoneNumber "
                + "AND g.deleted = false")
                    .setParameter("sourceTabTemplateId", sourceTabTemplateId)
                    .setParameter("sourceZoneIndex", sourceZoneIndex)
                    .setParameter("sourceZoneNumber", sourceZoneNumber).executeUpdate();
    }

}
