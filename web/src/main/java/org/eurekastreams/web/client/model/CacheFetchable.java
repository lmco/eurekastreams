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
package org.eurekastreams.web.client.model;

import java.io.Serializable;

/**
 * Models that are fetchable.
 * 
 * @param <RQ>
 *            Type of request made by the application to the model.
 * @param <RS>
 *            Type of response returned from the model.
 */
public interface CacheFetchable<RQ extends Serializable, RS extends Serializable> 
{
	/**
	 * Synchronously returns the cached value for a given request (if
	 * available).
	 * 
	 * @param request
	 *            request.
	 * @return The cached response if available, else null.
	 */
	RS fetchFromCache(RQ request);
}
