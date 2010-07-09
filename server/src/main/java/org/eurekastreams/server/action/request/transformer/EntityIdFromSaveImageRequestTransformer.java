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
package org.eurekastreams.server.action.request.transformer;

import java.io.Serializable;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.profile.SaveImageRequest;

/**
 * Returns entity id from SaveImageRequest object.
 * 
 */
public class EntityIdFromSaveImageRequestTransformer implements RequestTransformer
{

    /**
     * flag to determine if return value should be string.
     */
    private Boolean returnValueAsString = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable transform(final ActionContext inActionContext)
    {
        SaveImageRequest request = (SaveImageRequest) inActionContext.getParams();
        return returnValueAsString ? request.getEntityId().toString() : request.getEntityId();

    }

    /**
     * @param inReturnValueAsString
     *            the returnValueAsString to set
     */
    public void setReturnValueAsString(final Boolean inReturnValueAsString)
    {
        returnValueAsString = inReturnValueAsString;
    }

}
