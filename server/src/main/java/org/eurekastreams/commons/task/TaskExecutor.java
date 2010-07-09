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
 * The Task Executor interface.
 *
 */
public interface TaskExecutor
{
    /**
     * The execute method requiring a UserActionRequest object.
     * 
     * @param inUserActionRequest  the UserActionRequest object that contains the action to execute.
     */
    void execute(final UserActionRequest inUserActionRequest);

}
