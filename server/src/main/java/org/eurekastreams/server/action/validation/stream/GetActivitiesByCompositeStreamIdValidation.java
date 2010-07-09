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
package org.eurekastreams.server.action.validation.stream;

import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.stream.GetActivitiesByCompositeStreamRequest;
import org.eurekastreams.server.persistence.mappers.stream.CompositeStreamActivityIdsMapper;

/**
 * This class validates that the {@link GetActivitiesByCompositeStreamRequest} object contains a valid composite stream
 * id.
 *
 */
public class GetActivitiesByCompositeStreamIdValidation implements ValidationStrategy<PrincipalActionContext>
{
    /**
     * Local logger instance.
     */
    private Log logger = LogFactory.make();

    /**
     * {@link CompositeStreamActivityIdsMapper}.
     */
    private final CompositeStreamActivityIdsMapper idsMapper;

    /**
     * Constructor.
     *
     * @param inIdsMapper
     *            - instance of the {@link CompositeStreamActivityIdsMapper}.
     */
    public GetActivitiesByCompositeStreamIdValidation(final CompositeStreamActivityIdsMapper inIdsMapper)
    {
        idsMapper = inIdsMapper;
    }

    /**
     * {@inheritDoc}.
     *
     * This method takes a {@link GetActivitiesByCompositeStreamRequest} object.
     */
    @Override
    public void validate(final PrincipalActionContext inActionContext) throws ValidationException
    {
        GetActivitiesByCompositeStreamRequest request = (GetActivitiesByCompositeStreamRequest) inActionContext
                .getParams();
        try
        {
            List<Long> activityIds = idsMapper.execute(request.getCompositeStreamId(), inActionContext.getPrincipal()
                    .getId());
            inActionContext.getState().put("activityIds", activityIds);
        }
        catch (RuntimeException rex)
        {
            logger.info("Invalid composite stream id detected", rex);
            throw new ValidationException("Invalid composite stream id");
        }
    }

}
