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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.QueryParser;

/**
 * Class to build a QueryParser - needed because the QueryParser is not thread-safe. An instance of this class can be
 * defined in Spring config and we can use it to build QueryParsers at runtime per request from otherwise thread-safe
 * code.
 */
public class QueryParserBuilder
{
    /**
     * Default field to use.
     */
    private String defaultField;

    /**
     * Analyzer to pass into the QueryParser.
     */
    private Analyzer analyzer;

    /**
     * Constructor.
     *
     * @param inDefaultField
     *            the default field to use if none are provided.
     * @param inAnalyzer
     *            the Analyzer to use to parse the query string
     */
    public QueryParserBuilder(final String inDefaultField, final Analyzer inAnalyzer)
    {
        defaultField = inDefaultField;
        analyzer = inAnalyzer;
    }

    /**
     * Build a QueryParser with the constructor-fed default field and analyzer.
     *
     * @return a QueryParser with the constructor-fed default field and analyzer.
     */
    public QueryParser buildQueryParser()
    {
        return new QueryParser(defaultField, analyzer);
    }
}
