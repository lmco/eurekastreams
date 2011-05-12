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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Bannerable;
import org.eurekastreams.server.persistence.mappers.BannerableMapper;
import org.eurekastreams.server.persistence.mappers.requests.UpdateCachedBannerMapperRequest;
import org.eurekastreams.server.persistence.mappers.stream.BaseArgCachedDomainMapper;
import org.eurekastreams.server.service.actions.strategies.ImageWriter;

/**
 * Action to remove the banner id for a group or org.
 * 
 */
public class DeleteBannerExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Mapper used to get bannerable entity data from the db.
     */
    private BannerableMapper mapper = null;

    /**
     * Mapper used to update the banner id for the cached version of the entity.
     */
    private BaseArgCachedDomainMapper<UpdateCachedBannerMapperRequest, Object> updateCachedBannerMapper;

    /**
     * The image writer to write stuff to the disk.
     */
    private ImageWriter imageWriter;

    /**
     * Constructor for action.
     * 
     * @param inMapper
     *            the bannerableEntity mapper.
     * @param inUpdateCachedBannerMapper
     *            the cache mapper to update the domain group or org.
     * @param inImageWriter
     *            image writer
     */
    public DeleteBannerExecution(final BannerableMapper inMapper,
            final BaseArgCachedDomainMapper<UpdateCachedBannerMapperRequest, Object> inUpdateCachedBannerMapper,
            final ImageWriter inImageWriter)
    {
        imageWriter = inImageWriter;
        mapper = inMapper;
        updateCachedBannerMapper = inUpdateCachedBannerMapper;
    }

    /**
     * Returns true or false if the banner was removed for the group or org.
     * 
     * @param inActionContext
     *            the action context.
     * @return true if the banner was removed for the group or org.
     * 
     * @throws ExecutionException
     *             upon exception.
     */
    @Override
    public Bannerable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        try
        {
            Long entityId = (Long) inActionContext.getParams();

            Bannerable bannerObject = mapper.getBannerableDTO(entityId);

            // If bannerId wasn't already Null then you need to delete the avatar from the server.
            if (bannerObject.getBannerId() != null)
            {
                imageWriter.delete("n" + bannerObject.getBannerId());
            }
            // Update the db.
            boolean result = mapper.updateBannerId(entityId, null);
            // Update the cache.
            updateCachedBannerMapper.execute(new UpdateCachedBannerMapperRequest(null, entityId));

            return bannerObject;
        }
        catch (Exception ex)
        {
            log.error("Error deleting banner.", ex);
            throw new ExecutionException("Error deleting banner.", ex);
        }
    }
}
