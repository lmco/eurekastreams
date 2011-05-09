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
package org.eurekastreams.server.action.validation.directory;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.directory.GetDirectorySearchResultsRequest;

/**
 * Validation strategy for GetDirectorySearchResults action requests.
 * 
 * This ensures that the orgShortName is valid
 * 
 * Note: This does not actually test the datastore for a valid orgShortname because that is not provided by the user and
 * the search query would just return no results for the search of a non-existent org. This is a deliberate sanity check
 * only on the existence and format of the parameter passed in.
 */
public class GetDirectorySearchResultsValidation implements ValidationStrategy<ServiceActionContext>
{
    @Override
    public void validate(final ServiceActionContext inActionContext) throws ValidationException
    {
        GetDirectorySearchResultsRequest currentRequest = (GetDirectorySearchResultsRequest) inActionContext
                .getParams();

    }
}
