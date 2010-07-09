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

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Theme;

/**
 * This class provides the mapper functionality for Theme objects.
 */
public class ThemeMapper extends DomainEntityMapper<Theme> implements
        GalleryItemMapper<Theme>
{
    /**
     * Constructor.
     *
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public ThemeMapper(final QueryOptimizer inQueryOptimizer)
    {
        super(inQueryOptimizer);
    }

    /**
     * Return the name of domain entity type mapped by this mapper.
     *
     * @return the name
     */
    @Override
    protected String getDomainEntityName()
    {
        return "Theme";
    }

    /**
     * Look up the theme identified by this URL.
     *
     * @param themeLocation
     *            URL of the XML file that defines the Theme
     * @return the Theme specified by the URL
     */
    public Theme findByUrl(final String themeLocation)
    {
    	try
    	{
	        Theme theme = null;
	        Query q = getEntityManager().createQuery(
	                "from Theme where themeUrl = :themeUrl").setParameter(
	                "themeUrl", themeLocation.toLowerCase());

	        //This avoids entity manager doing a flush before executing
	        //the query. We don't want to get the object that is modified
	        //in the persistenceContext back as a result.
	        q.setFlushMode(FlushModeType.COMMIT);

	        return (Theme) q.getSingleResult();
    	}
    	catch (Exception ex)
    	{
    		return null;
    	}
    }

    /**
     * Look up a theme by the UUID. If not found, will throw NoResultException.
     *
     * @param uuid
     *            the identifying UUID
     * @return a theme
     */
    public Theme findByUUID(final String uuid)
    {
        Query q = getEntityManager().createQuery(
                "from Theme where uuid = :uuid").setParameter("uuid",
                uuid.toLowerCase());

        return (Theme) q.getSingleResult();
    }

    /**
     * Find the default theme.
     *
     * @return a theme
     */
    public Theme findDefault()
    {
        Query q = getEntityManager().createQuery("from Theme order by id");
        return (Theme) q.getResultList().get(0);
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
    public PagedSet<Theme> findForCategorySortedByPopularity(
            final String inCategory, final int inStart, final int inEnd)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("category", inCategory);

        String query = "FROM Theme t " + "WHERE t.category.name=:category "
                + "order by size(t.users) desc";

        PagedSet<Theme> pagedSet = this.getPagedResults(inStart, inEnd, query,
                parameters);

        populateTheme(pagedSet);

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
    public PagedSet<Theme> findSortedByPopularity(final int inStart,
            final int inEnd)
    {
        String query = "FROM Theme t " + "order by size(t.users) desc)";

        PagedSet<Theme> pagedSet = this.getPagedResults(inStart, inEnd, query,
                new HashMap<String, Object>());

        populateTheme(pagedSet);

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
    public PagedSet<Theme> findForCategorySortedByRecent(
            final String inCategory, final int inStart, final int inEnd)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("category", inCategory);

        String query = "FROM Theme " + "WHERE category.name=:category "
                + "order by created desc";

        PagedSet<Theme> pagedSet = this.getPagedResults(inStart, inEnd, query,
                parameters);

        populateTheme(pagedSet);

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
    public PagedSet<Theme> findSortedByRecent(final int inStart, final int inEnd)
    {
        String query = "FROM Theme " + "order by created desc";

        PagedSet<Theme> pagedSet = this.getPagedResults(inStart, inEnd, query,
                new HashMap<String, Object>());

        populateTheme(pagedSet);

        return pagedSet;
    }

    /**
     * populate various attributes for each theme.
     *
     * @param inPagedSet
     *            the paged set.
     */
    private void populateTheme(final PagedSet<Theme> inPagedSet)
    {
        for (Theme t : inPagedSet.getPagedSet())
        {
            // touch the owner to get it because its lazy loaded
            if (t.getOwner() != null)
            {
                t.getOwner().getEmail();
            }

            // determine the number of users and set it
            int numUsers = (int) getQueryOptimizer().determineCollectionSize(t.getUsers());
            t.setNumberOfUsers(numUsers);
        }
    }

    /**
     * Delete a theme.
     *
     * @param inTheme
     *            The theme to delete.
     */
    public void delete(final Theme inTheme)
    {
        // get the default gallery item
        Theme defaultTheme = findDefault();

        for (Person personUser : inTheme.getUsers())
        {
            personUser.setTheme(defaultTheme);
        }
        flush();
        refresh(inTheme);
        getEntityManager().remove(inTheme);
    }
}
