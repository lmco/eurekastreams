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
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 * This class is responsible for validating requests to follow a group.
 * 
 */
public class SetFollowingGroupStatusValidation implements ValidationStrategy<PrincipalActionContext>
{

    /**
     * Local instance of the GetDomainGroupsByShortNames mapper.
     */
    private final GetDomainGroupsByShortNames domainGroupMapper;

    /**
     * Constructor for the validator.
     * 
     * @param inDomainGroupMapper
     *            - instance of the GetDomainGroupsByShortNames mapper.
     */
    public SetFollowingGroupStatusValidation(final GetDomainGroupsByShortNames inDomainGroupMapper)
    {
        domainGroupMapper = inDomainGroupMapper;
    }

    /**
     * This is the method responsible for enforcing validation on the inputs of the request to add a follower to a
     * group. - Only followers to a group can be added through this action. - Target unique ids must point to
     * valid entities.
     * 
     * {@inheritDoc}
     */
    @Override
    public void validate(final PrincipalActionContext inActionContext) throws ValidationException
    {
        ValidationException vex = new ValidationException();
        SetFollowingStatusRequest inRequest = (SetFollowingStatusRequest) inActionContext.getParams();

        if (!inRequest.getTargetEntityType().equals(EntityType.GROUP))
        {
            vex.addError("EntityType", "This action only supports following a group.");
        }

        DomainGroupModelView targetResult = domainGroupMapper.fetchUniqueResult(inRequest.getTargetUniqueId());

        if (targetResult == null)
        {
            vex.addError("FollowerAndTarget", "Target unique id must refer to valid entity.");
        }

        if (vex.getErrors().size() > 0)
        {
            throw vex;
        }
    }

}
