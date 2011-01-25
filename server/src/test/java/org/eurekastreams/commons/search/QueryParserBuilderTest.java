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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.junit.Test;

/**
 * Test fixture for QueryParserBuilder.
 */
public class QueryParserBuilderTest
{
    /**
     * Test buildQueryParser with the arguments passed into the constructor.
     */
    @Test
    public void testBuildQueryParser()
    {
        String defaultField = "fooBar";
        StandardAnalyzer analyzer = new StandardAnalyzer();

        QueryParserBuilder sut = new QueryParserBuilder(defaultField, analyzer, "OR");

        // build our query parser
        QueryParser parser = sut.buildQueryParser();

        // make sure the fields were passed into the QueryParser
        assertEquals(defaultField, parser.getField());
        assertSame(analyzer, parser.getAnalyzer());

        // assert that we get different (thread-unsafe) QueryParsers with every request to buildQueryParser()
        assertNotSame(sut.buildQueryParser(), sut.buildQueryParser());

        // ... but make sure they use the same analyzer instances
        assertSame(sut.buildQueryParser().getAnalyzer(), sut.buildQueryParser().getAnalyzer());
    }
}
