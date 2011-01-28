/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.cache.cachedfieldpopulators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Populates CommentDTOs with properties that can be retrieved from cache.
 * 
 */
public class CommentDTOPopulator
{
    /**
     * DAO for finding person model views by id.
     */
    private DomainMapper<List<Long>, List<PersonModelView>> personDAO;

    /**
     * Constructor.
     * 
     * @param inPersonDAO
     *            The DomainMapper<List<Long>, List<PersonModelView>> instance.
     */
    public CommentDTOPopulator(final DomainMapper<List<Long>, List<PersonModelView>> inPersonDAO)
    {
        personDAO = inPersonDAO;
    }

    /**
     * Populates CommentDTOs with properties that can be retrieved from cache. If inCache is not null, the populated
     * commentDTO will be put in cache under CacheKeys.COMMENT_BY_ID + id key.
     * 
     * @param inCommentDTO
     *            The CommentDTO.
     * @param inCache
     *            The cache.
     */
    public void execute(final CommentDTO inCommentDTO, final Cache inCache)
    {
        List<CommentDTO> dtoList = new ArrayList<CommentDTO>(1);
        dtoList.add(inCommentDTO);
        execute(dtoList, null);
    }

    /**
     * Populates CommentDTOs with properties that can be retrieved from cache. If inCache is not null, the populated
     * commentDTO will be put in cache under CacheKeys.COMMENT_BY_ID + CommentDTO.getId() key.
     * 
     * @param inCommentDTOs
     *            The CommentDTOs to populate.
     * @param inCache
     *            The cache.
     */
    @SuppressWarnings("unchecked")
    public void execute(final List<CommentDTO> inCommentDTOs, final Cache inCache)
    {
        HashMap<Long, PersonModelView> commentAuthors = new HashMap<Long, PersonModelView>(inCommentDTOs.size());

        // get list of author entity ids so we can consolidate to one query.
        for (CommentDTO dto : inCommentDTOs)
        {
            commentAuthors.put(dto.getAuthorId(), null);
        }

        // get author personModelViews from DAO and shove them into map indexed by entityId.
        List<PersonModelView> authors = personDAO.execute(new ArrayList(commentAuthors.keySet()));
        for (PersonModelView pmv : authors)
        {
            commentAuthors.put(pmv.getEntityId(), pmv);
        }

        // loop yet again to populate and cache commentDTOs
        PersonModelView author;
        for (CommentDTO dto : inCommentDTOs)
        {
            author = commentAuthors.get(dto.getAuthorId());
            dto.setAuthorAccountId(author.getAccountId());
            dto.setAuthorDisplayName(author.getDisplayName());
            dto.setAuthorAvatarId(author.getAvatarId());
            if (inCache != null)
            {
                inCache.set(CacheKeys.COMMENT_BY_ID + dto.getId(), dto);
            }
        }
    }

}
