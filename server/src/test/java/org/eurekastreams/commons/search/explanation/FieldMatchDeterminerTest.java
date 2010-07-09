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
package org.eurekastreams.commons.search.explanation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.search.analysis.TextStemmerAnalyzer;
import org.eurekastreams.commons.search.modelview.FieldMatch;
import org.junit.Test;

/**
 * Test fixture for FieldMatchDeterminer.
 */
public class FieldMatchDeterminerTest
{
    /**
     * Test determineFieldMatches() with a TextStemmerAnalyzer.
     */
    @Test
    public void testDetermineFieldMatches()
    {
        FieldMatchDeterminer sut = new FieldMatchDeterminer();
        List<String> fieldsToAnalyze = new ArrayList<String>();
        fieldsToAnalyze.add("typeOfAuto");
        fieldsToAnalyze.add("name");

        sut.setFieldsToAnalyze(fieldsToAnalyze);
        sut.setSearchAnalyzer(new TextStemmerAnalyzer());
        FieldMatch matches = sut.determineFieldMatches(getExplanation(), "ford cars");

        assertTrue(matches.isMatch("typeOfAuto"));
        assertTrue(matches.isMatch("name"));
        assertFalse(matches.isMatch("foobar"));

        // note that the keyword that lucene found was "car", but we passed that in as "cars", so that's what it's
        // telling us we matched.
        Object[] matchedKeywords = matches.getMatchingKeywords("typeOfAuto").toArray();
        assertEquals(1, matchedKeywords.length);
        assertEquals("cars", matchedKeywords[0]);

        // note that these keywords come back in alphabetical order
        matchedKeywords = matches.getMatchingKeywords("name").toArray();
        assertEquals(2, matchedKeywords.length);
        assertEquals("cars", matchedKeywords[0]);
        assertEquals("ford", matchedKeywords[1]);
    }

    /**
     * Test determining fields when no fields are being inspected.
     */
    @Test
    public void testDetermineFieldMatchesWithNoFields()
    {
        FieldMatchDeterminer sut = new FieldMatchDeterminer();
        sut.setFieldsToAnalyze(new ArrayList<String>());
        sut.setSearchAnalyzer(new TextStemmerAnalyzer());

        FieldMatch matches = sut.determineFieldMatches(getExplanation(), "ford cars");

        assertFalse(matches.isMatch("typeOfAuto"));
        Object[] matchedKeywords = matches.getMatchingKeywords("typeOfAuto").toArray();
        assertEquals(0, matchedKeywords.length);

        matchedKeywords = matches.getMatchingKeywords("name").toArray();
        assertEquals(0, matchedKeywords.length);
    }

    /**
     * Test determining fields when one field is being inspected.
     */
    @Test
    public void testDetermineFieldMatchesWithOneFields()
    {
        FieldMatchDeterminer sut = new FieldMatchDeterminer();
        List<String> fieldsToAnalyze = new ArrayList<String>();
        fieldsToAnalyze.add("typeOfAuto");

        sut.setFieldsToAnalyze(fieldsToAnalyze);
        sut.setSearchAnalyzer(new TextStemmerAnalyzer());
        FieldMatch matches = sut.determineFieldMatches(getExplanation(), "ford cars");

        assertTrue(matches.isMatch("typeOfAuto"));
        assertFalse(matches.isMatch("name"));
        assertFalse(matches.isMatch("foobar"));

        // note that the keyword that lucene found was "car", but we passed that in as "cars", so that's what it's
        // telling us we matched.
        Object[] matchedKeywords = matches.getMatchingKeywords("typeOfAuto").toArray();
        assertEquals(1, matchedKeywords.length);
        assertEquals("cars", matchedKeywords[0]);

        // note that these keywords come back in alphabetical order
        matchedKeywords = matches.getMatchingKeywords("name").toArray();
        assertEquals(0, matchedKeywords.length);
    }

    /**
     * Get a canned explanation String as it's returned by Lucene.
     *
     * @return a canned explanation String as it's returned by Lucene
     */
    private String getExplanation()
    {
        return "0.4852078 = (MATCH) product of:\n" + "            1.6982272 = (MATCH) sum of:\n"
                + "              0.5762654 = (MATCH) product of:\n" + "                1.1525308 = (MATCH) sum of:\n"
                + "                  1.1525308 = (MATCH) weight(typeOfAuto:car^5.0 in 1056), product of:\n"
                + "                    0.27270508 = queryWeight(typeOfAuto:car), product of:\n"
                + "                      4.22629 = idf(docFreq=42, numDocs=1082)\n"
                + "                      0.06452587 = queryNorm\n"
                + "                    4.22629 = (MATCH) fieldWeight(typeOfAuto:car in 1056), product of:\n"
                + "                      1.0 = tf(termFreq(typeOfAuto:car)=1)\n"
                + "                      4.22629 = idf(docFreq=42, numDocs=1082)\n"
                + "                      1.0 = fieldNorm(field=typeOfAuto, doc=1056)\n"
                + "                0.5 = coord(1/2)\n" + "              1.1219617 = (MATCH) sum of:\n"
                + "                0.86984557 = (MATCH) weight(name:ford in 1056), product of:\n"
                + "                  0.21297395 = queryWeight(name:ford), product of:\n"
                + "                    6.601196 = idf(docFreq=3, numDocs=1082)\n"
                + "                    0.032262936 = queryNorm\n"
                + "                  4.0842814 = (MATCH) fieldWeight(name:ford in 1056), product of:\n"
                + "                    1.4142135 = tf(termFreq(name:ford)=2)\n"
                + "                    6.601196 = idf(docFreq=3, numDocs=1082)\n"
                + "                    0.4375 = fieldNorm(field=name, doc=1056)\n"
                + "                0.2521161 = (MATCH) weight(name:car in 1056), product of:\n"
                + "                  0.13635254 = queryWeight(name:car), product of:\n"
                + "                    4.22629 = idf(docFreq=42, numDocs=1082)\n"
                + "                    0.032262936 = queryNorm\n"
                + "                  1.849002 = (MATCH) fieldWeight(name:car in 1056), product of:\n"
                + "                    1.0 = tf(termFreq(name:car)=1)\n"
                + "                    4.22629 = idf(docFreq=42, numDocs=1082)\n"
                + "                    0.4375 = fieldNorm(field=name, doc=1056)\n"
                + "            0.2857143 = coord(2/7)";

    }
}
