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
package org.eurekastreams.server.domain.strategies;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.stream.BookmarkFilter;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Adapts Person Bookmark to the StreamFilter interface.
 */
public class BookmarkStreamFilterTransformer implements Transformer<List<StreamScope>, List<StreamFilter>>
{
    /**
     * Mapper for getting PersonModelViews from a list of account ids.
     */
    private DomainMapper<List<String>, List<PersonModelView>> getPersonModelViewsByAccountIdsMapper;

    /**
     * Group mapper for getting entity ID from short name.
     */
    private GetDomainGroupsByShortNames groupMapper;

    /**
     * Constructor.
     * 
     * @param inGetPersonModelViewsByAccountIdsMapper
     *            person mapper.
     * @param inGroupMapper
     *            group mapper;
     */
    public BookmarkStreamFilterTransformer(
            final DomainMapper<List<String>, List<PersonModelView>> inGetPersonModelViewsByAccountIdsMapper,
            final GetDomainGroupsByShortNames inGroupMapper)
    {
        getPersonModelViewsByAccountIdsMapper = inGetPersonModelViewsByAccountIdsMapper;
        groupMapper = inGroupMapper;
    }

    /**
     * Transform the bookmarks to filters.
     * 
     * @param bookmarks
     *            the bookmarks.
     * @return filters.
     */
    public List<StreamFilter> transform(final List<StreamScope> bookmarks)
    {
        List<String> personIds = new ArrayList<String>();
        List<String> groupIds = new ArrayList<String>();

        for (int i = 0; i < bookmarks.size(); i++)
        {
            ScopeType type = bookmarks.get(i).getScopeType();

            switch (type)
            {
            case PERSON:
                personIds.add(bookmarks.get(i).getUniqueKey());
                break;
            case GROUP:
                groupIds.add(bookmarks.get(i).getUniqueKey());
                break;
            default:
                throw new RuntimeException("Unhandled type.");
            }
        }

        final List<PersonModelView> people = getPersonModelViewsByAccountIdsMapper.execute(personIds);
        final List<DomainGroupModelView> groups = groupMapper.execute(groupIds);

        final List<StreamFilter> filters = new ArrayList<StreamFilter>();

        for (PersonModelView person : people)
        {
            final String name = person.getDisplayName();
            final String request = "{\"query\":{\"recipient\":[{\"type\":\"PERSON\",\"name\":\""
                    + person.getAccountId() + "\"}]}}";

            filters.add(new BookmarkFilter(0L, name, request));
        }

        for (DomainGroupModelView group : groups)
        {
            final String name = group.getName();
            final String request = "{\"query\":{\"recipient\":[{\"type\":\"GROUP\",\"name\":\"" + group.getShortName()
                    + "\"}]}}";

            filters.add(new BookmarkFilter(0L, name, request));
        }

        return filters;
    }

}
