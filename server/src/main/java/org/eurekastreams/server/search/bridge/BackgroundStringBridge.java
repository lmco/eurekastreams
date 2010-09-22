/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.server.search.bridge;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Background;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.hibernate.search.bridge.StringBridge;

/**
 * String Bridge for a List&lt;Job&gt;.
 */
public class BackgroundStringBridge implements StringBridge
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Individual item list bridge.
     */
    BackgroundItemListStringBridge itemBridge = new BackgroundItemListStringBridge();

    /**
     * Convert the input List&lt;Job&gt; into a searchable String.
     *
     * @param backgroundObj
     *            the background to convert
     * @return a string concatenation of company name, description, industry, and title for all jobs passed in.
     */
    @Override
    public String objectToString(final Object backgroundObj)
    {
        if (backgroundObj == null || !(backgroundObj instanceof Background))
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        try
        {
            Background bg = (Background) backgroundObj;

            // note: the extra space in the beginning here is for easier unit testing
            sb.append(" ");

            // we currently only index SKILLS since that's all that's settable
            List<BackgroundItemType> handledTypes = new ArrayList<BackgroundItemType>();
            handledTypes.add(BackgroundItemType.SKILL);

            for (BackgroundItemType itemType : handledTypes)
            {
                sb.append(itemBridge.objectToString(bg.getBackgroundItems(itemType)));
            }

            sb.append(" ");
        }
        catch (Exception ex)
        {
            log.info("Error iterating through the list of background items - most likely because it's null, "
                    + "but not detectable because it's a lazy-loaded collection. ", ex);
        }
        return sb.toString();
    }
}
