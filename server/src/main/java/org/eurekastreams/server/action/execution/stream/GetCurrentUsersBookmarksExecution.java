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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;

/**
 * Get the streams for the current user.
 */
public class GetCurrentUsersBookmarksExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Streams mapper.
     */
    private DomainMapper<Long, List<StreamScope>> getUserBookmarksMapper;

    /**
     * Bookmark Transformer.
     */
    private Transformer<List<StreamScope>, List<StreamFilter>> bookmarkTransformer;

    /**
     * Constructor.
     * 
     * @param inGetUserBookmarksMapper
     *            mapper for user bookmarks.
     * @param inBookmarkTransformer
     *            transformer for bookmarks.
     */
    public GetCurrentUsersBookmarksExecution(final DomainMapper<Long, List<StreamScope>> inGetUserBookmarksMapper,
            final Transformer<List<StreamScope>, List<StreamFilter>> inBookmarkTransformer)
    {
        getUserBookmarksMapper = inGetUserBookmarksMapper;
        bookmarkTransformer = inBookmarkTransformer;
    }

    /**
     * Execute the action.
     * 
     * @param inActionContext
     *            the action context.
     * @return the list of stream filters for the current user.
     * @exception ExecutionException
     *                not expected.
     */
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {

        final List<StreamFilter> filters = bookmarkTransformer.transform(getUserBookmarksMapper.execute(inActionContext
                .getPrincipal().getId()));

        return (Serializable) filters;

    }
}
