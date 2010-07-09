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
package org.eurekastreams.commons.search.bridge;

import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.hibernate.search.bridge.StringBridge;

/**
 * Field bridge that converts the input object to a String, then runs it through an Analyzer. This can be used with a
 * StandardAnalyzer, for example, to lowercase and remove stop words and punctuation, which will be faster than runnin a
 * custom SortComparator.
 */
public class StandardAnalyzerSortFieldBridge implements StringBridge
{
    /**
     * Instance of the logger.
     */
    private Log log = LogFactory.getLog(StandardAnalyzerSortFieldBridge.class);

    /**
     * The analyzer to run the field through for indexing.
     */
    private StandardAnalyzer analyzer = new StandardAnalyzer();

    /**
     * Convert the input object to String, tokenize it with the analyzer, then join the chunks together spaces.
     *
     * @param obj
     *            the object to convert
     * @return a sortable string representation of the input object
     */
    @Override
    public String objectToString(final Object obj)
    {
        log.info("...");
        if (obj == null)
        {
            return null;
        }
        log.info("Parsing '" + obj.toString() + "'");
        StringBuilder outputSb = new StringBuilder();
        TokenStream stream = analyzer.tokenStream(null, new StringReader(obj.toString()));
        Token token = new Token();
        try
        {
            while ((token = stream.next(token)) != null)
            {
                if (outputSb.length() > 0)
                {
                    outputSb.append(" ");
                }
                outputSb.append(token.term());
            }
        }
        catch (IOException e)
        {
            log.error("Error parsing '" + obj.toString() + "'", e);
            return null;
        }
        String output = outputSb.toString();

        if (log.isInfoEnabled())
        {
            log.info("Parsed '" + obj.toString() + "' as '" + output + "'");
        }
        return output;
    }
}
