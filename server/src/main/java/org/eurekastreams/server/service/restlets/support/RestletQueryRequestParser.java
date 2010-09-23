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
package org.eurekastreams.server.service.restlets.support;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.logging.LogFactory;

/**
 * Extracts a query from a restlet request.
 */
public class RestletQueryRequestParser
{
    /** Logger. */
    private Log log = LogFactory.make();

    /**
     * Global keywords.
     */
    private Collection<String> globalKeywords = null;

    /**
     * Multiple entity keywords.
     */
    private Collection<String> multipleEntityKeywords = null;

    /**
     * Keywords.
     */
    private Collection<String> keywords = null;

    /**
     * Constructor.
     *
     * @param inGlobalKeywords
     *            the global keywords.
     * @param inMultipleEntityKeywords
     *            the multiple entities keyword.
     * @param inKeywords
     *            the other keywords.
     */
    public RestletQueryRequestParser(final Collection<String> inGlobalKeywords,
            final Collection<String> inMultipleEntityKeywords, final Collection<String> inKeywords)
    {
        globalKeywords = inGlobalKeywords;
        multipleEntityKeywords = inMultipleEntityKeywords;
        keywords = inKeywords;
    }

    /**
     * Parses the request.
     *
     * @param path
     *            the path.
     * @param start
     *            Segment of path at which to begin.
     * @return the request.
     * @throws UnsupportedEncodingException
     *             thrown for bad request.
     */
    public JSONObject parseRequest(final String path, final int start) throws UnsupportedEncodingException
    {
        JSONObject json = new JSONObject();
        JSONObject query = new JSONObject();

        String[] parts = Pattern.compile("/").split(path);

        for (int i = start; i < parts.length - 1; i += 2)
        {
            log.debug("Found key: " + parts[i]);

            if (isMultipleEntityKeyword(parts[i]))
            {
                query.accumulate(parts[i], parseEntities(parts[i + 1]));
            }
            else if (isGlobalKeyword(parts[i]))
            {
                json.accumulate(parts[i], parts[i + 1]);
            }
            else if (isKeyword(parts[i]))
            {
                query.accumulate(parts[i], URLDecoder.decode(parts[i + 1], "UTF-8"));
            }
            else
            {
                throw new ValidationException("Unable to parse request, unrecognized keyword: " + parts[i]);
            }
        }

        json.accumulate("query", query);

        return json;
    }

    /**
     * Determine if the keyword is a multiple entity word.
     *
     * @param keyword
     *            the word.
     * @return true or false.
     */
    public boolean isMultipleEntityKeyword(final String keyword)
    {
        return multipleEntityKeywords.contains(keyword);
    }

    /**
     * Determine if the keyword is a global word.
     *
     * @param keyword
     *            the word.
     * @return true or false.
     */
    public boolean isGlobalKeyword(final String keyword)
    {
        return globalKeywords.contains(keyword);
    }

    /**
     * Determine if the keyword is recognized..
     *
     * @param keyword
     *            the word.
     * @return true or false.
     */
    public boolean isKeyword(final String keyword)
    {
        return keywords.contains(keyword);
    }

    /**
     * Parses entities from the request.
     *
     * @param entityString
     *            the request string.
     * @return the entities.
     */
    private JSONArray parseEntities(final String entityString)
    {
        JSONArray entityArr = new JSONArray();
        String[] parts = Pattern.compile(",").split(entityString);

        for (String part : parts)
        {
            String[] entity = part.split(":");

            JSONObject entityObj = new JSONObject();
            entityObj.accumulate("name", entity[1]);
            entityObj.accumulate("type", entity[0]);

            entityArr.add(entityObj);
        }

        return entityArr;
    }

}
