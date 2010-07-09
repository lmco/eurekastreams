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

import java.util.HashMap;

import javax.persistence.FlushModeType;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.PagedSet;

/**
 * This class provides the mapper functionality for GadgetDefinition entities.
 *
 * @param <GeneralGadgetDefinition> The Mapper for a GeneralGadgetDefinition.
 */
public abstract class CommonGadgetDefinitionMapper<GeneralGadgetDefinition> extends
        DomainEntityMapper<GeneralGadgetDefinition> implements GalleryItemMapper<GeneralGadgetDefinition>
{
    /**
     * Local logger instance.
     */
    private final Log logger = LogFactory.getLog(CommonGadgetDefinitionMapper.class);

    /**
     * Paramter for retrieving paged sets based on category.
     */
    static final String CATEGORY_PAGED_PARAMETER = "category";

    /**
     * Constructor.
     * 
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public CommonGadgetDefinitionMapper(final QueryOptimizer inQueryOptimizer)
    {
        super(inQueryOptimizer);
    }

    /**
     * Look up a gadget def by the UUID. If not found, will throw NoResultException.
     * 
     * @param uuid
     *            the identifying UUID
     * @return a gadget def
     */
    @SuppressWarnings("unchecked")
    public GeneralGadgetDefinition findByUUID(final String uuid)
    {
        Query q = getEntityManager().createQuery("from " + getDomainEntityName() + " where uuid = :uuid").setParameter(
                "uuid", uuid);

        return (GeneralGadgetDefinition) q.getSingleResult();
    }

    /**
     * Finds gadget definitions of a specified category sorted by popularity.
     * 
     * @param inCategory
     *            The category to which the gadget defs must belong.
     * @param inStart
     *            paging inStart.
     * @param inEnd
     *            paging inEnd.
     * @return a list of gadget def
     */
    public PagedSet<GeneralGadgetDefinition> findForCategorySortedByPopularity(final String inCategory,
            final int inStart, final int inEnd)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(CATEGORY_PAGED_PARAMETER, inCategory);

        String query = "FROM " + getDomainEntityName() + " gd "
                + "WHERE gd.category.name=:category and showInGallery = true " + "order by gd.numberOfUsers desc";

        PagedSet<GeneralGadgetDefinition> pagedSet = this.getPagedResults(inStart, inEnd, query, parameters);

        return pagedSet;
    }

    /**
     * Finds all gadget definitions.
     * 
     * @param inStart
     *            paging inStart.
     * @param inEnd
     *            paging inEnd.
     * 
     * @return the gadget.
     */
    public PagedSet<GeneralGadgetDefinition> findAll(final int inStart, final int inEnd)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();

        String query = "FROM " + getDomainEntityName() + " gd order by gd.id";

        PagedSet<GeneralGadgetDefinition> pagedSet = this.getPagedResults(inStart, inEnd, query, parameters);

        return pagedSet;
    }

    /**
     * Finds all gadget definitions sorted by popularity.
     * 
     * @param inStart
     *            paging inStart.
     * @param inEnd
     *            paging inEnd.
     * @return a list of gadget def
     */
    public PagedSet<GeneralGadgetDefinition> findSortedByPopularity(final int inStart, final int inEnd)
    {
        String query = "FROM " + getDomainEntityName() + " gd where showInGallery = true "
                + "order by gd.numberOfUsers desc";

        PagedSet<GeneralGadgetDefinition> pagedSet = this.getPagedResults(inStart, inEnd, query,
                new HashMap<String, Object>());

        return pagedSet;
    }

    /**
     * Finds gadget definitions of a specified category sorted by most recent.
     * 
     * @param inCategory
     *            The category to which the gadget defs must belong.
     * @param inStart
     *            paging inStart.
     * @param inEnd
     *            paging inEnd.
     * @return a list of gadget def
     */
    public PagedSet<GeneralGadgetDefinition> findForCategorySortedByRecent(final String inCategory, final int inStart,
            final int inEnd)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(CATEGORY_PAGED_PARAMETER, inCategory);

        String query = "FROM " + getDomainEntityName() + " WHERE category.name=:category and showInGallery = true "
                + "order by created desc";

        PagedSet<GeneralGadgetDefinition> pagedSet = this.getPagedResults(inStart, inEnd, query, parameters);

        return pagedSet;
    }

    /**
     * Finds all gadget definitions sorted by most recent.
     * 
     * @param inStart
     *            paging inStart.
     * @param inEnd
     *            paging inEnd.
     * @return a list of gadget def
     */
    public PagedSet<GeneralGadgetDefinition> findSortedByRecent(final int inStart, final int inEnd)
    {
        String query = "FROM " + getDomainEntityName() + " where showInGallery = true order by created desc";

        PagedSet<GeneralGadgetDefinition> pagedSet = this.getPagedResults(inStart, inEnd, query,
                new HashMap<String, Object>());

        return pagedSet;
    }

    /**
     * Delete a gadget definition.
     * 
     * @param inGadgetDefinition
     *            The gadget definition to delete.
     */
    public void delete(final GeneralGadgetDefinition inGadgetDefinition)
    {
        getEntityManager().remove(inGadgetDefinition);
    }
    
    /**
     * Look up the GeneralGadgetDefinition identified by this URL.
     * 
     * @param gadgetDefinitionLocation
     *            URL of the XML file that defines the GadgetDefinition
     * @return the GeneralGadgetDefinition specified by the URL
     */
    @SuppressWarnings("unchecked")
    public GeneralGadgetDefinition findByUrl(final String gadgetDefinitionLocation)
    {
        try
        {
            Query q = getEntityManager().createQuery(
                    "from " + getDomainEntityName() + " where url = :gadgetDefinitionUrl").setParameter(
                    "gadgetDefinitionUrl", gadgetDefinitionLocation.toLowerCase());

            // This avoids entity manager doing a flush before executing
            // the query. We don't want to get the object that is modified
            // in the persistenceContext back as a result.
            q.setFlushMode(FlushModeType.COMMIT);

            //TODO this shouldn't use an exception for logic.
            return (GeneralGadgetDefinition) q.getSingleResult();
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * Reindex definition.
     * 
     * @param def
     *            the gadget def.
     */
    public void reindex(final GeneralGadgetDefinition def)
    {
        getFullTextSession().index(def);
    }

    /** 
     * The Domain Entity name. Must be over written.
     * 
     * @return the table name for this entity.
     */
    protected abstract String getDomainEntityName();
    
    /**
     * Updates user counts for classes.
     */
    public abstract void refreshGadgetDefinitionUserCounts();
    
}
