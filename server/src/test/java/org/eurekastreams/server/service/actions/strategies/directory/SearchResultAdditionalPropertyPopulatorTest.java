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
package org.eurekastreams.server.service.actions.strategies.directory;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.search.explanation.FieldMatchDeterminer;
import org.eurekastreams.commons.search.modelview.FieldMatch;
import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for SearchResultAdditionalPropertyPopulator.
 */
public class SearchResultAdditionalPropertyPopulatorTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Field Match Determiner - responsible for deciding which fields were matches for the search text.
     */
    private FieldMatchDeterminer fieldMatchDeterminer = context.mock(FieldMatchDeterminer.class);

    /**
     * System under test.
     */
    private TransientPropertyPopulator sut = new SearchResultAdditionalPropertyPopulator(fieldMatchDeterminer);

    /**
     * Search text.
     */
    private final String searchText = "boooooo";

    /**
     * The first result's explanation.
     */
    private final String result1Explanation = "fooBar";

    /**
     * The second result's explanation.
     */
    private final String result2Explanation = "fooBar2";

    /**
     * The FieldMatch for the first result.
     */
    private final FieldMatch match1 = context.mock(FieldMatch.class, "match1");

    /**
     * The FieldMatch for the second result.
     */
    private final FieldMatch match2 = context.mock(FieldMatch.class, "match2");

    /**
     * First result.
     */
    private ModelView result1;

    /**
     * Second result.
     */
    private ModelView result2;

    /**
     * Search results.
     */
    private List<ModelView> results = new ArrayList<ModelView>();

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        results = new ArrayList<ModelView>();
        result1 = new DomainGroupModelView();
        result1.setSearchIndexExplanationString(result1Explanation);
        result2 = new PersonModelView();
        result2.setSearchIndexExplanationString(result2Explanation);
        results.add(result1);
        results.add(result2);
    }

    /**
     * Test populateTransientProperties.
     */
    @Test
    public void testPopulateTransientPropertiesWithLoggedInUser()
    {
        context.checking(new Expectations()
        {
            {
                one(fieldMatchDeterminer).determineFieldMatches(result1Explanation, searchText);
                will(returnValue(match1));

                one(fieldMatchDeterminer).determineFieldMatches(result2Explanation, searchText);
                will(returnValue(match2));
            }
        });

        // call sut
        sut.populateTransientProperties(results, 3L, searchText);

        // make sure the model views got their matches
        assertSame(match1, result1.getFieldMatch());
        assertSame(match2, result2.getFieldMatch());

        context.assertIsSatisfied();
    }

    /**
     * Test populateTransientProperties.
     */
    @Test
    public void testPopulateTransientPropertiesWithLoggedOutUser()
    {
        context.checking(new Expectations()
        {
            {
                one(fieldMatchDeterminer).determineFieldMatches(result1Explanation, searchText);
                will(returnValue(match1));

                one(fieldMatchDeterminer).determineFieldMatches(result2Explanation, searchText);
                will(returnValue(match2));
            }
        });

        // call sut
        sut.populateTransientProperties(results, 0L, searchText);

        // make sure the model views got their matches
        assertSame(match1, result1.getFieldMatch());
        assertSame(match2, result2.getFieldMatch());

        context.assertIsSatisfied();
    }
}
