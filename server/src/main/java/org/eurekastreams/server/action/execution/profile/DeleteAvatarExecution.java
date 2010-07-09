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
package org.eurekastreams.server.action.execution.profile;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.AvatarEntity;
import org.eurekastreams.server.persistence.DomainEntityMapper;
import org.eurekastreams.server.service.actions.strategies.CacheUpdater;
import org.eurekastreams.server.service.actions.strategies.EntityFinder;
import org.eurekastreams.server.service.actions.strategies.ImageWriter;

/**
 * Deletes an entity's avatar.
 *
 * @param <T>
 *            the avatar entity type.
 */
public class DeleteAvatarExecution<T extends AvatarEntity> implements
        TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Instance of the logger.
     */
    private Log log = LogFactory.make();

    /**
     * PersonMapper used to retrieve person from the db.
     */
    private DomainEntityMapper<T> mapper = null;

    /**
     * The image writer to write stuff to the disk.
     */
    private ImageWriter imageWriter;

    /**
     * Strategy for updating the cache after saving the avatar (optional).
     */
    private CacheUpdater cacheUpdaterStategy;

    /**
     * The finder.
     */
    private EntityFinder<T> finder;

    /**
     * Set the optional strategy to update the cache after saving the avatar.
     *
     * @param inCacheUpdaterStrategy
     *            the strategy to update the cache after saving the avatar
     */
    public void setCacheUpdaterStategy(final CacheUpdater inCacheUpdaterStrategy)
    {
        this.cacheUpdaterStategy = inCacheUpdaterStrategy;
    }

    /**
     * Constructor for action.
     *
     * @param inMapper
     *            the person mapper.
     * @param inImageWriter
     *            the image writer.
     * @param inFinder
     *            the finder.
     */
    public DeleteAvatarExecution(final DomainEntityMapper<T> inMapper,
            final ImageWriter inImageWriter, final EntityFinder<T> inFinder)
    {
        this.imageWriter = inImageWriter;
        this.mapper = inMapper;
        this.finder = inFinder;

    }

    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        Long entityId = (Long) inActionContext.getActionContext().getParams();

        T avatarEntity;
        try
        {
            avatarEntity = finder.findEntity(inActionContext.getActionContext().getPrincipal(), entityId);
        }
        catch (Exception e)
        {
            throw new ExecutionException(e);
        }

        String avatarId = avatarEntity.getAvatarId();

        if (avatarId != null)
        {
            imageWriter.delete("o" + avatarId);
            imageWriter.delete("n" + avatarId);
            imageWriter.delete("s" + avatarId);
        }

        avatarEntity.setAvatarId(null);
        mapper.flush();

        // if we have a cache updating strategy, call it.
        if (cacheUpdaterStategy != null)
        {
            inActionContext.getUserActionRequests().addAll(
                    cacheUpdaterStategy.getUpdateCacheRequests(inActionContext.getActionContext().getPrincipal(),
                            avatarEntity.getId()));
        }

        return Boolean.TRUE;
    }
}
