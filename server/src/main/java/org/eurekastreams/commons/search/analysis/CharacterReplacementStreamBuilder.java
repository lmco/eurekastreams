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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Utility to build a Reader from an input reader, converting a specific character to a String.
 */
public final class CharacterReplacementStreamBuilder
{
    /**
     * Private constructor.
     */
    private CharacterReplacementStreamBuilder()
    {
    }

    /**
     * Return a new reader with a specific character in the input stream with a string.
     *
     * @param inReader
     *            the reader
     * @param inReplaceFrom
     *            the text to replace from
     * @param inReplaceTo
     *            the text to replace to
     * @return a new reader with the hashtags replaced
     */
    public static Reader buildReplacementReader(final Reader inReader, final char inReplaceFrom,
            final String inReplaceTo)
    {
        int c;
        StringWriter sw = new StringWriter();
        try
        {
            while ((c = inReader.read()) != -1)
            {
                if (c == inReplaceFrom)
                {
                    sw.write(inReplaceTo);
                }
                else
                {
                    sw.write(c);
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error while parsing content: " + e.getMessage(), e);
        }
        return new StringReader(sw.toString());
    }
}
