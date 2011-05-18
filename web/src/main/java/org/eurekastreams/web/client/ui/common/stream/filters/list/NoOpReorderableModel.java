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
package org.eurekastreams.web.client.ui.common.stream.filters.list;

import org.eurekastreams.server.action.request.stream.SetStreamOrderRequest;
import org.eurekastreams.web.client.model.Reorderable;

/**
 * Reorderble model that does nothing.
 */
public class NoOpReorderableModel implements Reorderable<SetStreamOrderRequest>
{
    /**
     * Do nothing.
     * 
     * @param inRequest
     *            the request, which I'll gladly ignore
     */
    public void reorder(final SetStreamOrderRequest inRequest)
    {
        // no-op
    }

}
