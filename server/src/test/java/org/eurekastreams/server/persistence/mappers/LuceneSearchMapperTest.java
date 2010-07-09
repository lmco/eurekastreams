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
package org.eurekastreams.server.persistence.mappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.eurekastreams.commons.search.QueryParserBuilder;
import org.eurekastreams.server.domain.GalleryItem;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.persistence.mappers.requests.LuceneSearchRequest;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the lucene search mapper.
 */
public class LuceneSearchMapperTest
{
    /**
     * mock context.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private LuceneSearchMapper<GalleryItem> sut = new LuceneSearchMapper<GalleryItem>();

    /**
     * Query builder.
     */
    private QueryParserBuilder queryBuilder = context.mock(QueryParserBuilder.class);

    /**
     * Mock full text entity manager.
     */
    private FullTextEntityManager fullTextEm = context.mock(FullTextEntityManager.class);

    /**
     * Setup fixtures.
     */
    @Before
    public final void setUp()
    {
        sut.setQueryBuilder(queryBuilder);
        sut.setFullTextEntityManager(fullTextEm);
    }

    /**
     * Test executing.
     *
     * @throws ParseException
     *             shouldn't happen.
     */
    @Test
    public final void executeTest() throws ParseException
    {
        LuceneSearchRequest request = new LuceneSearchRequest();

        request.setMaxResults(2);
        request.setFirstResult(0);

        Map<String, Float> fields = new HashMap<String, Float>();
        fields.put("name", 2.0F);
        fields.put("title", 2.0F);
        fields.put("description", 1.0F);
        fields.put("author", 1.0F);
        request.setFields(fields);

        List<String> sortFields = new ArrayList<String>();
        sortFields.add("sort");
        request.setSortFields(sortFields);
        request.setObjectType(Theme.class);
        request.setSearchString("search text");

        context.checking(new Expectations()
        {
            {
                QueryParser parser = context.mock(QueryParser.class);
                Query query = context.mock(Query.class);

                oneOf(queryBuilder).buildQueryParser();
                will(returnValue(parser));

                oneOf(parser).parse(with(any(String.class)));
                will(returnValue(query));

                oneOf(fullTextEm).createFullTextQuery(query, Theme.class);
            }
        });

        sut.execute(request);
    }
}
