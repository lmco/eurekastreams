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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.server.domain.stream.HashTag;
import org.eurekastreams.server.persistence.mappers.ReadMapper;
import org.eurekastreams.server.persistence.mappers.chained.PartialMapperResponse;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * Partial results mapper for HashTags.
 */
public class PartialHashTagDbMapper extends
        ReadMapper<List<String>, PartialMapperResponse<List<String>, List<HashTag>>>
{
    /**
     *Find the hash tags with the input contents. The contents will be lowercased.
     *
     * @param inHashTagContents
     *            the hash tag contents to search for
     * @return a partial mapper response containing the hash tags found and a new request of the contents that weren't
     */
    @Override
    public PartialMapperResponse<List<String>, List<HashTag>> execute(final List<String> inHashTagContents)
    {
        Set<String> contents = new HashSet<String>();
        for (String content : inHashTagContents)
        {
            String hashTag = content.toLowerCase();
            if (!hashTag.startsWith("#"))
            {
                contents.add("#" + hashTag);
            }
            else
            {
                contents.add(hashTag);
            }
        }

        // find the hash tags that exist
        Criteria criteria = getHibernateSession().createCriteria(HashTag.class);
        criteria.add(Restrictions.in("this.content", contents));
        List<HashTag> results = criteria.list();

        // interpret the results
        List<String> hashtagsNotFound = new ArrayList<String>();
        hashtagsNotFound.addAll(contents);

        for (HashTag ht : results)
        {
            String lowerCasedContent = ht.getContent().toLowerCase();
            if (hashtagsNotFound.contains(lowerCasedContent))
            {
                hashtagsNotFound.remove(lowerCasedContent);
            }
        }

        if (hashtagsNotFound.size() > 0)
        {
            // missing some results
            return new PartialMapperResponse<List<String>, List<HashTag>>(results, hashtagsNotFound);
        }
        else
        {
            // found everything
            return new PartialMapperResponse<List<String>, List<HashTag>>(results);
        }
    }

}
