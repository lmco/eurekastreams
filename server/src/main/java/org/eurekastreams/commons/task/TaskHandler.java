/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.commons.task;

import org.eurekastreams.commons.server.UserActionRequest;

/**
 * Submits actions for asynchronous execution. 
 */
public interface TaskHandler
{
    /**
     * Returns true if follower/following relationship exists false otherwise.
     * 
     * @param inUserActionRequest
     *            The user action request object to perform asynchronously.
     * @throws Exception 
     * 		not expected.
     */
    void handleTask(UserActionRequest inUserActionRequest) throws Exception;
    
}
