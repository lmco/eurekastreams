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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.BaseObjectType;

/**
 * Extractor of all content for an activity - unformatted, used for content analysis.
 */
public class ActivityContentExtractor
{
    /**
     * The logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Extract the unformatted content from the activity for analysis.
     *
     * @param inBaseObjectType
     *            the activity base type
     * @param inBaseObject
     *            the map of properties
     * @return unformatted content from the activity
     */
    public String extractContent(final BaseObjectType inBaseObjectType, final HashMap<String, String> inBaseObject)
    {
        StringBuffer sb = new StringBuffer();
        if (inBaseObject != null)
        {
            switch (inBaseObjectType)
            {
            case NOTE:
                if (inBaseObject.containsKey("content"))
                {
                    sb.append(inBaseObject.get("content"));
                }
                break;
            case BOOKMARK:
                if (inBaseObject.containsKey("content"))
                {
                    sb.append(inBaseObject.get("content"));
                }
                if (inBaseObject.containsKey("targetTitle"))
                {
                    sb.append(" ");
                    sb.append(inBaseObject.get("targetTitle"));
                }
                if (inBaseObject.containsKey("description"))
                {
                    sb.append(" ");
                    sb.append(inBaseObject.get("description"));
                }
                break;
            case FILE:
                if (inBaseObject.containsKey("targetTitle"))
                {
                    sb.append(inBaseObject.get("targetTitle"));
                }
                break;
            default:
                log.error("I don't know how to pull the content from activities of type: " + inBaseObject.toString());
                break;
            }
        }

        return sb.toString().length() > 0 ? sb.toString() : null;
    }
}
