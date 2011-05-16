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
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.profile.SaveImageRequest;
import org.eurekastreams.server.persistence.mappers.BannerableMapper;
import org.eurekastreams.server.persistence.mappers.requests.UpdateCachedBannerMapperRequest;
import org.eurekastreams.server.persistence.mappers.stream.BaseArgCachedDomainMapper;
import org.eurekastreams.server.service.actions.strategies.ImageWriter;

/**
 * Saves the banner to the disk and updates the appropriate entity to be associated with the new banner.
 * 
 */
public class UpdateBannerExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Local log instance for this class.
     */
    private final Log log = LogFactory.make();

    /**
     * BannerableMapper used to retrieve entity from the db.
     */
    private final BannerableMapper mapper;

    /**
     * Cache mapper for updating the banner id of a bannerable object.
     */
    private final BaseArgCachedDomainMapper<UpdateCachedBannerMapperRequest, Object> cacheMapper;

    /**
     * Actually used to read in the stored image to verify that it is a valid one.
     */
    private final ImageWriter imageWriter;

    /**
     * Default constructor.
     * 
     * @param inMapper
     *            the banner mapper.
     * @param inCacheMapper
     *            the cache mapper to update the banner id.
     * @param inImageWriter
     *            used to validate the image
     */
    public UpdateBannerExecution(final BannerableMapper inMapper,
            final BaseArgCachedDomainMapper<UpdateCachedBannerMapperRequest, Object> inCacheMapper,
            final ImageWriter inImageWriter)
    {
        mapper = inMapper;
        cacheMapper = inCacheMapper;
        imageWriter = inImageWriter;
    }

    /**
     * {@inheritDoc}. Takes an UpdateBannerRequest object, stores the banner to the disk and then updates the associated
     * entity with the new banner id.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        try
        {
            SaveImageRequest currentRequest = (SaveImageRequest) inActionContext.getParams();
            imageWriter.write(currentRequest.getFileItem(), "n" + currentRequest.getImageId());
            mapper.updateBannerId(currentRequest.getEntityId(), currentRequest.getImageId());
            mapper.flush();
            cacheMapper.execute(new UpdateCachedBannerMapperRequest(currentRequest.getImageId(), currentRequest
                    .getEntityId()));
        }
        catch (Exception ex)
        {
            log.error("Error running SaveBannerAction", ex);
            throw new ExecutionException("Error occurred saving the supplied banner", ex);
        }

        return null;
    }
}
