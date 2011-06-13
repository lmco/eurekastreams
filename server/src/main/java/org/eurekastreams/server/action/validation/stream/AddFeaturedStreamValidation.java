/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import org.apache.commons.lang.StringUtils;
import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Verification strategy for adding a featured stream.
 * 
 */
public class AddFeaturedStreamValidation implements ValidationStrategy<ActionContext>
{
    /**
     * Max description length.
     */
    private final int maxLength;

    /**
     * Find by id mapper.
     */
    private DomainMapper<FindByIdRequest, StreamScope> streamScopeMapper;

    /**
     * Constructor.
     * 
     * @param inMaxLength
     *            Max description length.
     * @param inStreamScopeMapper
     *            Find by id mapper.
     */
    public AddFeaturedStreamValidation(final int inMaxLength,
            final DomainMapper<FindByIdRequest, StreamScope> inStreamScopeMapper)
    {
        maxLength = inMaxLength;
        streamScopeMapper = inStreamScopeMapper;
    }

    @Override
    public void validate(final ActionContext inActionContext) throws ValidationException
    {
        FeaturedStreamDTO dto = (FeaturedStreamDTO) inActionContext.getParams();

        // verify description length.
        String description = dto.getDescription();
        if (StringUtils.isEmpty(description) || (description.length() > maxLength))
        {
            throw new ValidationException("Description must be present and less than " + maxLength + " characters");
        }

        // verify streamscope not null and is correct type.
        StreamScope streamScope = streamScopeMapper.execute(new FindByIdRequest("StreamScope", dto.getStreamId()));
        if (streamScope == null)
        {
            throw new ValidationException("Stream not found");
        }

        // put in state so action doesn't need to look it up again or use placeholder object.
        inActionContext.getState().put("streamScope", streamScope);

        // verify that featured stream belongs to person or group.
        if (streamScope.getScopeType() != ScopeType.PERSON && streamScope.getScopeType() != ScopeType.GROUP)
        {
            throw new ValidationException("Only person and group streams supported.");
        }

    }
}
