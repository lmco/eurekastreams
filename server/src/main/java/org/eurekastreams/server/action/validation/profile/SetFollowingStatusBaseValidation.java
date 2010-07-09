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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.Follower;

/**
 * Base set of validation rules for setting the following status on either a group or a person.
 * This class is then decorated by more specific validation rules for following a person or
 * following a group.
 *
 */
public class SetFollowingStatusBaseValidation implements ValidationStrategy<PrincipalActionContext>
{
    /**
     * Local instance of logger for this class.
     */
    private final Log logger = LogFactory.make();

    /**
     * Instance of decorator class for this base validation strategy.
     */
    private final ValidationStrategy<PrincipalActionContext> decoratorValidationStrategy;

    /**
     * Constructor.
     *
     * @param inDecoratorValidationStrategy
     *            - instance of ValidationStrategy that this validation strategy is being decorated by.
     */
    public SetFollowingStatusBaseValidation(
            final ValidationStrategy<PrincipalActionContext> inDecoratorValidationStrategy)
    {
        decoratorValidationStrategy = inDecoratorValidationStrategy;
    }

    /**
     * Validation method.  Ensures the two base validation rules:
     * - The follower unique id exists and the target unique id exists.
     * - the follower status is valid. (i.e. not NOTSPECIFIED)
     * {@inheritDoc}.
     */
    @Override
    public void validate(final PrincipalActionContext inActionContext) throws ValidationException
    {
        SetFollowingStatusRequest inRequest = (SetFollowingStatusRequest) inActionContext.getParams();

        ValidationException vex = new ValidationException();

        if (inRequest.getFollowerUniqueId().length() <= 0 || inRequest.getTargetUniqueId().length() <= 0)
        {
            vex.addError("FollowerAndTarget", "This action requires a follower and target unique id");
            logger.error("Validation error - " + vex.getErrors().get("FollowerAndTarget"));
            // if this occurs, throw the error now, no point continuing since the following calls will fail.
            throw vex;
        }

        if (inRequest.getFollowerStatus().equals(Follower.FollowerStatus.NOTSPECIFIED))
        {
            vex.addError("FollowingStatus", "This action does not accept setting FollowerStatus of NOTSPECIFIED");
            logger.error("Validation error - " + vex.getErrors().get("FollowingStatus"));
            throw vex;
        }

        decoratorValidationStrategy.validate(inActionContext);

    }

}
