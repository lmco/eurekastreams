/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.email;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to build and parse token content (for use in the email interface).
 */
public class TokenContentFormatter
{
    /** Metadata key: person performing the action. */
    public static final String META_KEY_ACTOR = "p";

    /** Metadata key: destination personal stream. */
    public static final String META_KEY_PERSON_STREAM = "ps";

    /** Metadata key: destination group stream. */
    public static final String META_KEY_GROUP_STREAM = "gs";

    /** Metadata key: activity ID. */
    public static final String META_KEY_ACTIVITY = "a";

    /** Log. */
    private final Logger log = LoggerFactory.getLogger(LogFactory.getClassName());

    /**
     * Encodes a token for a user and stream.
     *
     * @param streamEntityType
     *            Entity type of the stream.
     * @param streamEntityId
     *            ID of stream's entity (person/group).
     * @param personId
     *            Person ID of actor.
     * @return Token content string.
     */
    public String buildForStream(final EntityType streamEntityType, final long streamEntityId, final long personId)
    {
        String metaKey;
        switch (streamEntityType)
        {
        case PERSON:
            metaKey = META_KEY_PERSON_STREAM;
            break;
        case GROUP:
            metaKey = META_KEY_GROUP_STREAM;
            break;
        default:
            throw new IllegalArgumentException("Only person and group streams are allowed.");
        }

        return META_KEY_ACTOR + personId + metaKey + streamEntityId;
    }

    /**
     * Encodes a token for a user and activity.
     *
     * @param activityId
     *            Activity ID.
     * @param personId
     *            Person ID of actor.
     * @return Token content string.
     */
    public String buildForActivity(final long activityId, final long personId)
    {
        return META_KEY_ACTOR + personId + META_KEY_ACTIVITY + activityId;
    }

    /**
     * Encodes key-value data into a token.
     *
     * @param data
     *            Data.
     * @return Token content string.
     */
    public String build(final Map<String, Long> data)
    {
        // serialize the token data
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Long> entry : data.entrySet())
        {
            sb.append(entry.getKey());
            sb.append(entry.getValue());
        }

        return sb.toString();
    }


    /**
     * Decodes a token into key-value data.
     *
     * @param tokenContent
     *            Token content string.
     * @return Data.
     */
    public Map<String, Long> parse(final String tokenContent)
    {
              // parse the data from the string
        Map<String, Long> data = new HashMap<String, Long>();
        String toParse = new String(tokenContent);
        int len = toParse.length();
        int pos = 0;
        while (pos < len)
        {
            int startTag = pos;
            while (pos < len && Character.isLetter(toParse.charAt(pos)))
            {
                pos++;
            }
            int startValue = pos;
            while (pos < len && Character.isDigit(toParse.charAt(pos)))
            {
                pos++;
            }
            if (startTag == startValue || startValue == pos)
            {
                log.error("Error parsing token content - key or value empty.");
                return null;
            }
            String tag = toParse.substring(startTag, startValue);
            long value = Long.parseLong(toParse.substring(startValue, pos));
            data.put(tag, value);
        }

        return data;
    }
}
