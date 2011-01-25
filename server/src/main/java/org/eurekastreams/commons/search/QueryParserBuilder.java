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

import java.security.InvalidParameterException;

import org.apache.commons.logging.Log;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.eurekastreams.commons.logging.LogFactory;

/**
 * Class to build a QueryParser - needed because the QueryParser is not thread-safe. An instance of this class can be
 * defined in Spring config and we can use it to build QueryParsers at runtime per request from otherwise thread-safe
 * code.
 */
public class QueryParserBuilder
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Default field to use.
     */
    private String defaultField;

    /**
     * Analyzer to pass into the QueryParser.
     */
    private Analyzer analyzer;

    /**
     * The default boolean operator (AND|OR).
     */
    private Operator defaultBooleanOperator;

    /**
     * Constructor.
     * 
     * @param inDefaultField
     *            the default field to use if none are provided.
     * @param inAnalyzer
     *            the Analyzer to use to parse the query string
     * @param inDefaultBooleanOperator
     *            the default boolean operator (AND, OR)
     */
    public QueryParserBuilder(final String inDefaultField, final Analyzer inAnalyzer,
            final String inDefaultBooleanOperator)
    {
        defaultField = inDefaultField;
        analyzer = inAnalyzer;
        if (inDefaultBooleanOperator.equalsIgnoreCase("OR"))
        {
            defaultBooleanOperator = Operator.OR;
        }
        else if (inDefaultBooleanOperator.equalsIgnoreCase("AND"))
        {
            defaultBooleanOperator = Operator.AND;
        }
        else
        {
            throw new InvalidParameterException("Valid values for default boolean operator: [AND, OR]");
        }
    }

    /**
     * Build a QueryParser with the constructor-fed default field and analyzer.
     * 
     * @return a QueryParser with the constructor-fed default field and analyzer.
     */
    public QueryParser buildQueryParser()
    {
        QueryParser qp = new QueryParser(defaultField, analyzer);
        log.info("Setting default boolean operator to " + defaultBooleanOperator.toString());
        qp.setDefaultOperator(defaultBooleanOperator);
        return qp;
    }
}
