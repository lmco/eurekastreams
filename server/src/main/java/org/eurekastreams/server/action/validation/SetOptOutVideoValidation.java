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
package org.eurekastreams.server.action.validation;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.TutorialVideo;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Validation for if a user wishes to opt out of a video.
 * 
 * Validates that the opt out video exists in the database.
 *
 */
public class SetOptOutVideoValidation implements ValidationStrategy<ServiceActionContext>
{

    /**
     * Used to validate that the video is a real one.
     */
    private FindByIdMapper<TutorialVideo> optOutVideoMapper;

    /**
     * @param inOptOutVideoMapper
     *            the video mapper.
     */
    public SetOptOutVideoValidation(final FindByIdMapper<TutorialVideo> inOptOutVideoMapper)
    {
        optOutVideoMapper = inOptOutVideoMapper;
    }

    /**
     * Validates that the opted out video exists in the TutorialVideo DB.
     * 
     * @param inActionContext
     *            the actionContext.
     * @throws ValidationException
     *             if validation fails.
     */
    @Override
    public void validate(final ServiceActionContext inActionContext) throws ValidationException
    {
        Long videoID = (Long) inActionContext.getParams();

        if (optOutVideoMapper.execute(new FindByIdRequest("TutorialVideo", videoID)) == null)
        {
            throw (new ValidationException("Not a valid video id"));
        }
    }
}
