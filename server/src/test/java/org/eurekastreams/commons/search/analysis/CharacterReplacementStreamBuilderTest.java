/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.commons.search.analysis;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

/**
 * Test fixture for CharacterReplacementStreamBuilder.
 */
public class CharacterReplacementStreamBuilderTest
{
    /**
     * Test buildReplacementReader with no replacements.
     */
    @Test
    public void testBuildReplacementReaderWithNoReplacements()
    {
        String input = "'Replace all' tick's i'n thi's text'";
        String expected = "'Replace all' tick's i'n thi's text'";
        assertEquals(expected, readToEnd(CharacterReplacementStreamBuilder.buildReplacementReader(new StringReader(
                input), '[', "]")));
    }

    /**
     * Test buildReplacementReader with replacements.
     */
    @Test
    public void testBuildReplacementReaderWithReplacements()
    {
        String input = "'Replace all' tick's i'n thi's text'";
        String expected = "FReplace allF tickFs iFn thiFs textF";
        assertEquals(expected, readToEnd(CharacterReplacementStreamBuilder.buildReplacementReader(new StringReader(
                input), '\'', "F")));
    }

    /**
     * Convert the input reader to a string.
     *
     * @param inReader
     *            the reader to read from
     * @return a string of the contents of the input reader
     */
    private String readToEnd(final Reader inReader)
    {
        StringBuffer sb = new StringBuffer();
        try
        {
            int c;
            while ((c = inReader.read()) != -1)
            {
                sb.append((char) c);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
}
