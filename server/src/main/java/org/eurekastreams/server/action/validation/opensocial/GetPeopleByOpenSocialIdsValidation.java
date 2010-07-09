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
package org.eurekastreams.server.action.validation.opensocial;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.opensocial.GetPeopleByOpenSocialIdsRequest;

/**
 * This class provides the validation for the GetPeopleByOpenSocialIds action.
 *
 */
public class GetPeopleByOpenSocialIdsValidation implements ValidationStrategy<PrincipalActionContext>
{

    /**
     * {@inheritDoc}
     *
     * Validate the type of relationship provided in the request matches the set of options.
     */
    @Override
    public void validate(final PrincipalActionContext inActionContext) throws ValidationException
    {
        GetPeopleByOpenSocialIdsRequest currentRequest = (GetPeopleByOpenSocialIdsRequest) inActionContext.getParams();

        if (!currentRequest.getTypeOfRelationshipForPeopleReturned().equals("self")
                && !currentRequest.getTypeOfRelationshipForPeopleReturned().equals("friends"))
        {
            throw new ValidationException("Unsupported relationship type provided.");
        }

    }

}
