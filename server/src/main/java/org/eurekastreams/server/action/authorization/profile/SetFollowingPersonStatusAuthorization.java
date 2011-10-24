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
package org.eurekastreams.server.action.authorization.profile;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;

/**
 * This class provides the authorization strategy for enforcing business rules when a user requests to follow another
 * user.
 *
 */
public class SetFollowingPersonStatusAuthorization implements AuthorizationStrategy<PrincipalActionContext>
{

    /**
     * local logger.
     */
    private final Log logger = LogFactory.make();

    /**
     * Auth error.
     */
    private static final String AUTH_ERROR_FOLLOWING_SELF = "People cannot follow themselves.";

    /**
     * Auth error.
     */
    private static final String AUTH_ERROR_NONOWNER_FOLLOWING = "Only a user can request to follow another user.";

    /**
     * This Authorization Strategy enforces the following business rules: - User cannot follow themselves. - User
     * calling the action is the only one who can request to follower another user.
     *
     * {@inheritDoc}
     */
    @Override
    public void authorize(final PrincipalActionContext inActionContext) throws AuthorizationException
    {
        SetFollowingStatusRequest request = (SetFollowingStatusRequest) inActionContext.getParams();

        // User cannot follow themselves
        if (request.getFollowerUniqueId() != null && request.getFollowerUniqueId().equals(request.getTargetUniqueId()))
        {
            logger.error("Error occurred authorizing Following a person: " + AUTH_ERROR_FOLLOWING_SELF);
            throw new AuthorizationException(AUTH_ERROR_FOLLOWING_SELF);
        }

        // The user calling the action is the only one who can request to follow another user.
        if (request.getFollowerUniqueId() != null && !request.getFollowerUniqueId().equals(inActionContext.getPrincipal().getAccountId()))
        {
            logger.error("Error occurred authorizing Following a person: " + AUTH_ERROR_NONOWNER_FOLLOWING);
            throw new AuthorizationException(AUTH_ERROR_NONOWNER_FOLLOWING);
        }
    }

}
