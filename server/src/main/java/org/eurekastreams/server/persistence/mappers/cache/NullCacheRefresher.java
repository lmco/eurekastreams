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
package org.eurekastreams.server.persistence.mappers.cache;

import org.eurekastreams.server.persistence.mappers.chained.RefreshStrategy;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * A do-nothing cache refresher.
 *
 * @param <Request> request type.
 * @param <Response> response type.
 */
public class NullCacheRefresher<Request, Response> extends CachedDomainMapper implements
RefreshStrategy<Request, Response>
{
    /**
     * Do nothing. If we did something we wouldn't be much of a null cache refresher would we?
     * @param request who cares?
     * @param response again, nothing to see here. This is not the param you're looking for.
     */
    @Override
    public void refresh(final Request request, final Response response)
    {
    }

}
