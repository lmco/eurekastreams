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
package org.eurekastreams.commons.search.analysis;

import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.memory.SynonymMap;

/**
 * Factory to return a singleton SynonymMap.
 */
public final class SynonymMapFactory
{
    /**
     * Private empty constructor for this singleton.
     */
    private SynonymMapFactory()
    {
        // no-op
    }

    /**
     * Singleton Synonym map.
     */
    private static SynonymMap synonyms = null;

    /**
     * Initialize the synonym map.
     *
     * @param thesaurusInputStream
     *            InputStream of the WordNet synonyms file
     * @return true
     */
    public static boolean inform(final InputStream thesaurusInputStream)
    {
        if (synonyms == null)
        {
            synchronized (SynonymMapFactory.class)
            {
                Log initLog = LogFactory.getLog(SynonymMapFactory.class);
                initLog.debug("Initializing SynonymMap.");
                try
                {
                    // NOTE: the synonym stream is closed inside SynonymMap
                    synonyms = new SynonymMap(thesaurusInputStream);
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
                initLog.info("Testing SynonymMap.");
                if (initLog.isInfoEnabled())
                {
                    initLog.info("Synonyms for 'success': " + Arrays.toString(synonyms.getSynonyms("success")));
                }
                initLog.info("SynonymMap initializing complete.");
            }
        }
        return true;
    }

    /**
     * Get the singleton SynonymMap.
     *
     * @return the singleton SynonymMap.
     */
    public static SynonymMap getSynonymMap()
    {
        if (synonyms == null)
        {
            throw new RuntimeException("Synonyms not yet loaded.");
        }
        return synonyms;
    }
}
