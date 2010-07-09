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
package org.eurekastreams.commons.server;

import org.eurekastreams.commons.client.ActionRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.userdetails.UserDetails;

/**
 * Simple class to wrap the generation of the ActionExecutor object.
 *
 */
public class ActionExecutorFactory
{
    /**
     * Default constructor.
     */
    public ActionExecutorFactory()
    {
        //No implementation supplied, this is a simple factory class to wrap 
        //creation of the ActionExecutor object.
    }
    
    /**
     * Retrieve an instance of the ActionExecutor object.  This is the factory method.
     * @param inAppContext - instance of the {@link ApplicationContext} for this request.
     * @param inUserDetails - instance of the {@link UserDetails} for this request.
     * @param inActionRequest - instance of the {@link ActionRequest} for this request.
     * @return - instance of the ActionExecutor.
     */
    @SuppressWarnings("unchecked")
    public ActionExecutor getActionExecutor(
            final ApplicationContext inAppContext, 
            final UserDetails inUserDetails, 
            final ActionRequest inActionRequest)
    {
        return new ActionExecutor(inAppContext, inUserDetails, inActionRequest);
    }
}
