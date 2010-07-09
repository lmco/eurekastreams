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
package org.eurekastreams.server.service.actions.strategies.activity.plugins;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Gets the entity id for adding feed subscriptions.
 */
public interface GetEntityIdForFeedSubscription 
{
	/**
	 * Gets the entity id.
	 * @param params hash map of params.
	 * @return the entity id.
	 */
	long getEntityId(final HashMap<String, Serializable> params);
}
