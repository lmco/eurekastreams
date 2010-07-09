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
package org.eurekastreams.server.action.validation.profile;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * This class handles validation for request to follow a person through the Follow action.
 *
 */
public class SetFollowingPersonStatusValidation implements ValidationStrategy<PrincipalActionContext>
{
    /**
     * Local instance of the GetPeopleByAccountIds mapper.
     */
    private final GetPeopleByAccountIds mapper;

    /**
     * Constructor for the FollowingPersonValidator that sets up the mapper instances.
     *
     * @param inMapper
     *            - instance of the GetPeopleByAccountIds mapper.
     */
    public SetFollowingPersonStatusValidation(final GetPeopleByAccountIds inMapper)
    {
        mapper = inMapper;
    }

    /**
     * Enforces validation for the SetFollowingStatusAction when following a user.
     * - Action will only allow following of a person.
     * - Both follower and target must be valid unique ids.
     *
     * {@inheritDoc}
     */
    @Override
    public void validate(final PrincipalActionContext inActionContext) throws ValidationException
    {
        SetFollowingStatusRequest inRequest = (SetFollowingStatusRequest) inActionContext.getParams();

        ValidationException vex = new ValidationException();

        if (!inRequest.getTargetEntityType().equals(EntityType.PERSON))
        {
            vex.addError("EntityType", "This action only supports following a person.");
        }

        PersonModelView followerResult = mapper.fetchUniqueResult(inRequest.getFollowerUniqueId());

        PersonModelView targetResult = mapper.fetchUniqueResult(inRequest.getTargetUniqueId());

        if (followerResult == null || targetResult == null)
        {
            vex.addError("FollowerAndTarget", "Follower and Target unique ids for valid users must be supplied.");
        }

        if (vex.getErrors().size() > 0)
        {
            throw vex;
        }
    }

}
