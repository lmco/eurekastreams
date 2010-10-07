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
package org.eurekastreams.server.service.actions.strategies.links;

import java.net.HttpURLConnection;

/**
 * Interface that describes a decorator for a connection facade.  Each decorator
 * takes in an HttpUrlConnection to decorate..
 *
 */
public interface ConnectionFacadeDecorator
{
    /**
     * Retrieve the list of headers that need to be added to the connection.
     * @param inConnection - HttpUrlConnection to decorate.
     * @param inAccountId - Account id of the user making the request.
     */
    void decorate(final HttpURLConnection inConnection, final String inAccountId);
}
