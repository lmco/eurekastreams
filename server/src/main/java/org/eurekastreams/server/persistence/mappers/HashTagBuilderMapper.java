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
package org.eurekastreams.server.persistence.mappers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.server.domain.stream.HashTag;

/**
 * Mapper that builds a new HashTag from its content.
 */
public class HashTagBuilderMapper implements DomainMapper<List<String>, List<HashTag>>
{
    /**
     * Return a new HashTag from the input content.
     *
     * @param inHashTags
     *            the hash tags contents
     * @return a new HashTag from the input content
     */
    @Override
    public List<HashTag> execute(final List<String> inHashTags)
    {
        // weed out dupes with hashset
        Set<String> hashTagContents = new HashSet<String>();
        for (String htContent : inHashTags)
        {
            if (!htContent.startsWith("#"))
            {
                hashTagContents.add("#" + htContent.toLowerCase());
            }
            else
            {
                hashTagContents.add(htContent.toLowerCase());
            }
        }

        List<HashTag> results = new ArrayList<HashTag>();
        for (String htContent : hashTagContents)
        {
            results.add(new HashTag(htContent));
        }
        return results;
    }

}
