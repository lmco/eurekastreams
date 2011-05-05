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

import java.awt.Image;
import java.awt.image.BufferedImage;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.profile.ResizeAvatarRequest;
import org.eurekastreams.server.domain.AvatarEntity;
import org.eurekastreams.server.persistence.DomainEntityMapper;
import org.eurekastreams.server.service.actions.strategies.CacheUpdater;
import org.eurekastreams.server.service.actions.strategies.EntityFinder;
import org.eurekastreams.server.service.actions.strategies.HashGeneratorStrategy;
import org.eurekastreams.server.service.actions.strategies.ImageWriter;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;

/**
 * Gets the avatar and resizes it to 2 thumbnails based on input.
 * 
 * @param <T>
 *            the avatar entity type.
 */
public class ResizeAvatarExecution<T extends AvatarEntity> implements
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
     * The hash generator strategy to generate a unique hash for the avatar id.
     */
    private HashGeneratorStrategy hasher;

    /**
     * The image writer to write to the disk.
     */
    private ImageWriter imageWriter;

    /**
     * Strategy for updating the cache after saving the avatar (optional).
     */
    private CacheUpdater cacheUpdaterStategy;

    /**
     * The width and height of the normal avatar.
     */
    private static final int NORMAL_AVATAR_SIZE = 75;

    /**
     * The width and height of the small avatar.
     */
    private static final int SMALL_AVATAR_SIZE = 50;

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
     * The finder.
     */
    private EntityFinder<T> finder;

    /**
     * The constructor for the resize avatar action.
     * 
     * @param inMapper
     *            the person mapper.
     * @param inHasher
     *            the hasher.
     * @param inImageWriter
     *            the image writer.
     * @param inFinder
     *            the finder.
     */
    public ResizeAvatarExecution(final DomainEntityMapper<T> inMapper, final HashGeneratorStrategy inHasher,
            final ImageWriter inImageWriter, final EntityFinder<T> inFinder)
    {
        this.imageWriter = inImageWriter;
        this.hasher = inHasher;
        this.mapper = inMapper;
        this.finder = inFinder;

    }

    @Override
    public T execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        ResizeAvatarRequest request = (ResizeAvatarRequest) inActionContext.getActionContext().getParams();
        Integer x = request.getX();
        Integer y = request.getY();
        Integer size = request.getSize();
        Boolean refreshFiles = request.getRefreshFiles();
        T avatarEntity;
        try
        {
            avatarEntity = finder.findEntity(inActionContext.getActionContext().getPrincipal(), request.getEntityId());
        }
        catch (Exception e1)
        {
            throw new ExecutionException(e1);
        }

        String avatarId = avatarEntity.getAvatarId();

        if (refreshFiles)
        {
            // Delete the three files there now.
            imageWriter.delete("n" + avatarId);
            imageWriter.delete("s" + avatarId);

            String originalPath = "o" + avatarId;

            avatarId = hasher.hash(((Long) avatarEntity.getId()).toString());
            avatarEntity.setAvatarId(avatarId);

            imageWriter.rename(originalPath, "o" + avatarId);
        }

        avatarEntity.setAvatarCropX(x);
        avatarEntity.setAvatarCropY(y);
        avatarEntity.setAvatarCropSize(size);
        mapper.flush();

        try
        {
            BufferedImage bufferedImage = (BufferedImage) imageWriter.read("o" + avatarId);

            imageWriter.write(createResizedCopy(bufferedImage, x, y, NORMAL_AVATAR_SIZE, size), "n" + avatarId);
            imageWriter.write(createResizedCopy(bufferedImage, x, y, SMALL_AVATAR_SIZE, size), "s" + avatarId);

        }
        catch (Exception e)
        {
            log.error("Error resizing avatar", e);
            return null;
        }

        // if we have a cache updating strategy, call it.
        if (cacheUpdaterStategy != null)
        {
            inActionContext.getUserActionRequests().addAll(
                    cacheUpdaterStategy.getUpdateCacheRequests(inActionContext.getActionContext().getPrincipal(),
                            request.getEntityId()));
        }

        return avatarEntity;
    }

    /**
     * Creates a resized thumbnail of the image in memory.
     * 
     * @param originalImage
     *            the original image to resize.
     * @param x
     *            the starting point of the crop.
     * @param y
     *            the starting point of the crop.
     * @param scaleSize
     *            the size to scale it to.
     * @param cropSize
     *            the size of the crop to scale it from.
     * @return the scaled image.
     */
    private BufferedImage createResizedCopy(final Image originalImage, final int x, final int y, final int scaleSize,
            final int cropSize)
    {
        BufferedImage crop = ((BufferedImage) originalImage).getSubimage(x, y, cropSize, cropSize);

        ResampleOp resampleOp = new ResampleOp(scaleSize, scaleSize);
        resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
        return resampleOp.filter(crop, null);
    }
}
