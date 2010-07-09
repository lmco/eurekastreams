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

import org.apache.log4j.Logger;
import org.eurekastreams.commons.server.UserActionRequest;

/**
 * This class puts a request into nothing.
 * 
 */
public class NullTaskHandler implements TaskHandler
{

    /**
     * The logger.
     */
    private Logger logger = Logger.getLogger(NullTaskHandler.class);
         
    /**
     * Puts a request into a message and places it on the queue.
     * 
     * @param inUserActionRequest
     *            the request
     */
    public void handleTask(final UserActionRequest inUserActionRequest) 
    {
    	// no op
    	logger.info("submit called with " + inUserActionRequest.getActionKey() + "|" 
    			+ "and " + inUserActionRequest.getParams().toString() + " as params.");
   }
}
