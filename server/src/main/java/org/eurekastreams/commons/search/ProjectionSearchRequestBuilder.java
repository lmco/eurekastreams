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
package org.eurekastreams.commons.search;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.transform.ResultTransformer;

/**
 * Hibernate Search request wrapper class. This class makes Hibernate Search/Lucene requests a little bit easier to
 * create.
 * 
 * Note: This is a work in progress for research and may be removed or heavily refactored. Don't spent too much time
 * understand what's going on here.
 */
public class ProjectionSearchRequestBuilder
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(ProjectionSearchRequestBuilder.class);

    /**
     * Log text to report which entities we're searching.
     */
    private String entityNames;

    /**
     * The EntityManager to operate on.
     */
    private EntityManager entityManager;

    /**
     * Set the entity manager.
     * 
     * @param newEntityManager
     *            the entity manager.
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager newEntityManager)
    {
        this.entityManager = newEntityManager;
    }

    /**
     * Default types of Entities to return - can be overridden per search.
     */
    private Class< ? >[] resultTypes;

    /**
     * Formatter of the string.
     */
    private String searchStringFormat;

    /**
     * Tranformer to use to convert projection to use.
     */
    private ResultTransformer resultTransformer;

    /**
     * The Query Parser to use to parse the Lucene Queries.
     */
    private QueryParserBuilder queryParserBuilder;

    /**
     * Query Parser for advanced searches.
     */
    private QueryParserBuilder advancedQueryParserBuilder;

    /**
     * Which fields to return in the search, space-separated.
     */
    private List<String> resultFields;

    /**
     * Empty constructor - if used, make sure all required properties are set. This is useful for Spring configuration
     * using abstract & child beans.
     */
    public ProjectionSearchRequestBuilder()
    {
    }

    /**
     * Build and return the search query, parsing the input native search string, adding extra parameters needed under
     * the hood including the search score and explanation (if explanation is requested -- it's expensive), the managed
     * entity (if requested - it'll need to go to the database to load that, and setting the projection.
     * 
     * @param nativeSearchString
     *            the native search string to parse and use
     * @return the FullTextQuery object, preloaded with the parsed query, all the parameters passed into this class'
     *         constructor, and other information we'll need later.
     */
    public org.hibernate.search.jpa.FullTextQuery buildQueryFromNativeSearchString(final String nativeSearchString)
    {
        if (log.isInfoEnabled())
        {
            log.info("User attempting native lucene search: " + nativeSearchString);
        }
        return prepareQuery(queryParserBuilder.buildQueryParser(), nativeSearchString);
    }

    /**
     * Build and return the search query, building a native search format using the format string and the input query
     * text, adding extra parameters needed under the hood including the search score and explanation (if explanation is
     * requested -- it's expensive), the managed entity (if requested - it'll need to go to the database to load that,
     * and setting the projection.
     * 
     * @param searchText
     *            text to search for
     * @return the FullTextQuery object, preloaded with the parsed query, all the parameters passed into this class'
     *         constructor, and other information we'll need later.
     */
    public org.hibernate.search.jpa.FullTextQuery buildQueryFromSearchText(final String searchText)
    {
        return buildQueryFromSearchText(searchText, "");
    }

    /**
     * Build and return the search query, building a native search format using the format string and the input query
     * text, adding extra parameters needed under the hood including the search score and explanation (if explanation is
     * requested -- it's expensive), the managed entity (if requested - it'll need to go to the database to load that,
     * and setting the projection.
     * 
     * @param constraints
     *            an additional native search string. It is appended to the native search string generated for the
     *            searchText.
     * @param searchText
     *            text to search for
     * @return the FullTextQuery object, preloaded with the parsed query, all the parameters passed into this class'
     *         constructor, and other information we'll need later.
     */
    public org.hibernate.search.jpa.FullTextQuery buildQueryFromSearchText(final String searchText,
            final String constraints)
    {
        String nativeSearchString;
        QueryParser theQueryParser;
        if (containsAdvancedSearchCharacters(searchText))
        {
            if (log.isInfoEnabled())
            {
                log.info("User attempting advanced search: " + searchText);
            }
            nativeSearchString = escape(searchText) + constraints;
            theQueryParser = advancedQueryParserBuilder.buildQueryParser();
        }
        else
        {
            if (log.isInfoEnabled())
            {
                log.info("User attempting standard search: " + searchText);
            }
            nativeSearchString = String.format(searchStringFormat, escape(searchText)) + constraints;
            theQueryParser = queryParserBuilder.buildQueryParser();
        }
        return prepareQuery(theQueryParser, nativeSearchString);
    }

    /**
     * Prepare the input luceneQuery.
     * 
     * @param inQueryParser
     *            the QueryParser to use
     * @param nativeSearchString
     *            the query to prepare
     * @return the fully prepared FullTextQuery
     */
    protected org.hibernate.search.jpa.FullTextQuery prepareQuery(final QueryParser inQueryParser,
            final String nativeSearchString)
    {
        Query luceneQuery;
        try
        {
            luceneQuery = inQueryParser.parse(nativeSearchString);
        }
        catch (ParseException e)
        {
            String message = "Unable to parse query: '" + nativeSearchString + "'";
            log.error(message);
            throw new RuntimeException(message);
        }

        if (log.isInfoEnabled())
        {
            log.info("Lucene query: " + nativeSearchString);
            log.info("Lucene query parsed as: " + luceneQuery.toString());
            log.info("Querying for objects of type: " + getEntityNames());
        }

        // get the FullTextQuery
        FullTextEntityManager ftem = getFullTextEntityManager();

        // wrap the FullTextQuery so we have more control over the control flow
        ProjectionFullTextQuery projectionFullTextQuery = new ProjectionFullTextQuery(ftem.createFullTextQuery(
                luceneQuery, resultTypes));

        // set the result format to projection
        List<String> parameters = buildFieldList();
        projectionFullTextQuery.setProjection(parameters.toArray(new String[parameters.size()]));

        // set the transformer
        projectionFullTextQuery.setResultTransformer(resultTransformer);

        return projectionFullTextQuery;
    }

    /**
     * Set the paging on the input query (zero-based).
     * 
     * @param query
     *            the query to set the paging on
     * @param from
     *            the starting (zero-based) index
     * @param to
     *            the ending (zero-based) index
     * @return the input query
     */
    public org.hibernate.search.jpa.FullTextQuery setPaging(final org.hibernate.search.jpa.FullTextQuery query,
            final int from, final int to)
    {
        query.setFirstResult(from);
        query.setMaxResults(to - from + 1);
        return query;
    }

    /**
     * Get a FullTextEntityManager from the entityManager.
     * 
     * @return a FullTextEntityManager from the entityManager.
     */
    protected FullTextEntityManager getFullTextEntityManager()
    {
        return Search.getFullTextEntityManager(entityManager);
    }

    /**
     * Build the list of fields to retrieve.
     * 
     * @return the list of fields to retrieve
     */
    protected List<String> buildFieldList()
    {
        // Set the projection
        List<String> parameters = new ArrayList<String>();
        if (resultFields != null)
        {
            parameters.addAll(resultFields);
        }

        // always load the ID, object class, and search score
        parameters.add(FullTextQuery.ID);
        parameters.add(FullTextQuery.OBJECT_CLASS);
        return parameters;
    }

    /**
     * Determine whether the input searchText is considered an advanced search.
     * 
     * @param searchText
     *            the search text
     * @return whether the input searchText is considered an advanced search.
     */
    protected boolean containsAdvancedSearchCharacters(final String searchText)
    {
        return searchText.indexOf('(') > -1 || searchText.indexOf(')') > -1 || searchText.indexOf('+') > -1
                || searchText.indexOf('-') > -1 || searchText.indexOf('?') > -1 || searchText.indexOf('*') > -1
                || searchText.indexOf("\"") > -1;
    }

    /**
     * Escape special characters that are used for grouping keywords, still allowing wildcard, but removing any bare
     * wildcards.
     * 
     * @param s
     *            the search text to escape
     * @return the input search text with all but wildcard characters escaped
     */
    public String escapeAllButWildcardCharacters(final String s)
    {
        final int negTwo = -2;
        StringBuffer sb = new StringBuffer();
        int stringLength = s.length();
        int lastGoodCharIndex = negTwo;
        for (int i = 0; i < stringLength; i++)
        {
            char c = s.charAt(i);

            // see if it's a bare wildcard
            if ((c == '*' || c == '?') && (lastGoodCharIndex < (i - 1) || isWhitespaceChar(s.charAt(i - 1))))
            {
                // this is a wildcard char with nothing before it throw an error letting the user know
                continue;
            }

            // These characters are part of the query syntax and must be escaped
            if (c == '\\' || c == ':' || c == '^' || c == '|' || c == '&' || c == '"' || c == '+' || c == '-'
                    || c == '!' || c == '(' || c == ')')
            {
                sb.append('\\');
            }
            sb.append(c);
            lastGoodCharIndex = i;
        }

        if (log.isDebugEnabled())
        {
            log.debug("Escaping '" + s + "' as '" + sb.toString() + "'");
        }
        return sb.toString();
    }

    /**
     * Return whether the input char is a whitespace char.
     * 
     * @param c
     *            the char to check
     * @return whether the input char is a whitespace char.
     */
    private boolean isWhitespaceChar(final char c)
    {
        return c == '\r' || c == '\n' || c == '\t' || c == ' ';
    }

    /**
     * Modified version of QueryString.escape to escape characters that this query builder does not allow. Allow: ", +,
     * -, !, (, ), ~, *, ?.
     * 
     * The allowed characters permit the advanced user to search ranges, exclude terms, mandate terms, use fuzzy and
     * wildcard search.
     * 
     * @param s
     *            the text to escape
     * @return a cleaned-up version of the input String
     */
    public String escape(final String s)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            // These characters are part of the query syntax and must be escaped
            if (c == '\\' || c == ':' || c == '^' || c == '|' || c == '&')
            {
                sb.append('\\');
            }
            sb.append(c);
        }

        if (log.isDebugEnabled())
        {
            log.debug("Escaping '" + s + "' as '" + sb.toString() + "'");
        }
        return sb.toString();
    }

    /**
     * Set the types of domain entity classes to search for.
     * 
     * @param theResultTypes
     *            the types of domain entity classes to search for.
     */
    public void setResultTypes(final Class< ? >[] theResultTypes)
    {
        this.resultTypes = theResultTypes;

        // get the log text
        entityNames = "";
        for (Class< ? > clazz : theResultTypes)
        {
            if (entityNames.length() > 0)
            {
                entityNames += ", ";
            }
            String className = clazz.getName();
            entityNames += className.substring(className.lastIndexOf('.') + 1);
        }
    }

    /**
     * Set the search string format, a String.format mask that the user's search terms are applied to as the first
     * parameter.
     * 
     * @param theSearchStringFormat
     *            the search string format, a String.format mask that the user's search terms are applied to as the
     *            first parameter.
     */
    public void setSearchStringFormat(final String theSearchStringFormat)
    {
        this.searchStringFormat = theSearchStringFormat;
    }

    /**
     * Set the result transformer, responsible for transforming the property/alias arrays into useful objects.
     * 
     * @param theResultTransformer
     *            the result transformer, responsible for transforming the property/alias arrays into useful objects.
     */
    public void setResultTransformer(final ResultTransformer theResultTransformer)
    {
        this.resultTransformer = theResultTransformer;
    }

    /**
     * Set the QueryParser to use to parse the formatted query string.
     * 
     * @param inQueryParserBuilder
     *            the QueryParserBuilder to use to build the QueryParser to parse the formatted query string.
     */
    public void setQueryParserBuilder(final QueryParserBuilder inQueryParserBuilder)
    {
        this.queryParserBuilder = inQueryParserBuilder;
    }

    /**
     * Set which result fields are to be returned by the search.
     * 
     * @param theResultFields
     *            which result fields are to be returned by the search
     */
    public void setResultFields(final List<String> theResultFields)
    {
        this.resultFields = theResultFields;
    }

    /**
     * QueryParserBuilder to use for advanced searches.
     * 
     * @param inAdvancedQueryParserBuilder
     *            the advancedQueryParserBuilder to set
     */
    public void setAdvancedQueryParserBuilder(final QueryParserBuilder inAdvancedQueryParserBuilder)
    {
        advancedQueryParserBuilder = inAdvancedQueryParserBuilder;
    }

    /**
     * Get the entity names for logging.
     * 
     * @return the entity names for logging
     */
    public String getEntityNames()
    {
        return entityNames;
    }
}
