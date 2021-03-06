/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.commons.actions.context;

/**
 * This class creates a specification of the {@link ActionContext} that includes retrieving
 * the {@link Principal} object.
 */
public interface PrincipalActionContext extends ActionContext
{
    /**
     * Retrieve the current {@link Principal} object for this instance.
     * @return current instance of the {@link Principal} object.
     */
    Principal getPrincipal();
}
