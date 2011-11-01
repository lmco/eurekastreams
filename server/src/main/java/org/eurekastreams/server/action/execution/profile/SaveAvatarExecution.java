/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import java.awt.image.BufferedImage;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.InlineExecutionStrategyExecutor;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.profile.ResizeAvatarRequest;
import org.eurekastreams.server.action.request.profile.SaveImageRequest;
import org.eurekastreams.server.domain.AvatarEntity;
import org.eurekastreams.server.persistence.DomainEntityMapper;
import org.eurekastreams.server.service.actions.strategies.CacheUpdater;
import org.eurekastreams.server.service.actions.strategies.EntityFinder;
import org.eurekastreams.server.service.actions.strategies.ImageWriter;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;

/**
 * Saves the avatar to the disk. If it's too big, it resizes it to the correct size. Also, it generates two thumbnails
 * for it by calling the resizing action.
 *
 * @param <T>
 *            the avatar entity type.
 */
public class SaveAvatarExecution<T extends AvatarEntity> implements
        TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Instance of the logger.
     */
    private final Log log = LogFactory.make();

    /**
     * PersonMapper used to retrieve person from the db.
     */
    private DomainEntityMapper<T> mapper = null;

    /**
     * The avatar resizer action.
     */
    @SuppressWarnings("unchecked")
    private final TaskHandlerExecutionStrategy<PrincipalActionContext> avatarResizer;

    /**
     * The image writer.
     */
    private final ImageWriter imageWriter;

    /**
     * Strategy for updating the cache after saving the avatar (optional).
     */
    private CacheUpdater cacheUpdaterStategy;

    /**
     * The max width of the resized image.
     */
    private static final int SCALE_X = 400;

    /**
     * The max height of the resized image.
     */
    private static final int SCALE_Y = 250;

    /**
     * The finder.
     */
    private final EntityFinder<T> finder;

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
     * Default constructor.
     *
     * @param inMapper
     *            the person mapper.
     * @param inAction
     *            the resize action.
     * @param inImageWriter
     *            the image writer.
     * @param inFinder
     *            the finder.
     */
    @SuppressWarnings("unchecked")
    public SaveAvatarExecution(final DomainEntityMapper<T> inMapper,
            final TaskHandlerExecutionStrategy<PrincipalActionContext> inAction, final ImageWriter inImageWriter,
            final EntityFinder<T> inFinder)
    {
        this.imageWriter = inImageWriter;
        this.avatarResizer = inAction;
        this.mapper = inMapper;
        this.finder = inFinder;

    }

    /**
     * Saves the avatar to the disk. If it's too big, it resizes it to the correct size. Also, it generates two
     * thumbnails for it by calling the resizing action.
     *
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     * @return the entity.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        try
        {
            SaveImageRequest request = (SaveImageRequest) inActionContext.getActionContext().getParams();
            String avatarId = request.getImageId();
            Long entityId = request.getEntityId();

            T avatarEntity = finder.findEntity(inActionContext.getActionContext().getPrincipal(), entityId);

            String oldAvatarId = avatarEntity.getAvatarId();

            avatarEntity.setAvatarId(avatarId);

            mapper.flush();

            BufferedImage originalImage = imageWriter.getImageFromFile(request.getFileItem());

            Integer scaleX = SCALE_X;
            Integer scaleY = SCALE_Y;

            if (originalImage.getWidth(null) > originalImage.getHeight(null))
            {
                scaleY = (scaleX * originalImage.getHeight(null)) / originalImage.getWidth(null);

            }
            else
            {
                scaleX = (scaleY * originalImage.getWidth(null)) / originalImage.getHeight(null);
            }

            ResampleOp resampleOp = new ResampleOp(scaleX, scaleY);
            resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
            BufferedImage scaledImage = resampleOp.filter(originalImage, null);

            imageWriter.write(scaledImage, "o" + avatarId);

            // Generate two thumbnails of the file
            Integer x = 0;
            Integer y = 0;
            Integer cropSize = 0;

            if (scaledImage.getWidth(null) > scaledImage.getHeight(null))
            {
                cropSize = scaledImage.getHeight(null);
                x = (scaledImage.getWidth(null) - scaledImage.getHeight(null)) / 2;

            }
            else
            {
                cropSize = scaledImage.getWidth(null);
                y = (scaledImage.getHeight(null) - scaledImage.getWidth(null)) / 2;
            }

            ResizeAvatarRequest resizeRequest = new ResizeAvatarRequest(x, y, cropSize, Boolean.FALSE, entityId);

            T newEntity = (T) new InlineExecutionStrategyExecutor().execute(avatarResizer, resizeRequest,
                    inActionContext);

            // If all is well, delete the old avatar:
            if (oldAvatarId != null)
            {
                imageWriter.delete("o" + oldAvatarId);
                imageWriter.delete("n" + oldAvatarId);
                imageWriter.delete("s" + oldAvatarId);
            }

            // if we have a cache updating strategy, call it.
            if (cacheUpdaterStategy != null)
            {
                inActionContext.getUserActionRequests().addAll(
                        cacheUpdaterStategy.getUpdateCacheRequests(inActionContext.getActionContext().getPrincipal(),
                                entityId));
            }
            return newEntity;
        }
        catch (Exception ex)
        {
            throw new ExecutionException(ex);
        }
    }
}
