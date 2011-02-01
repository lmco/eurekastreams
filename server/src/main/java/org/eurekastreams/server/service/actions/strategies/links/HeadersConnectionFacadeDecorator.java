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
package org.eurekastreams.server.service.actions.strategies.links;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * ConnectionFacadeDecorator for adding static headers to the supplied connection.
 *
 */
public class HeadersConnectionFacadeDecorator implements ConnectionFacadeDecorator
{
    /**
     * Map of header keys and values to add to the connection.
     */
    private final Map<String, String> headersMap;

    /**
     * Constructor.
     *
     * @param inHeadersMap
     *            Map of header keys and values to add to the connection.
     */
    public HeadersConnectionFacadeDecorator(final Map<String, String> inHeadersMap)
    {
        headersMap = inHeadersMap;
    }

    /**
     * {@inheritDoc}. Loops through the headers supplied and adds them to the connection object to be used when making
     * the request.
     */
    @Override
    public void decorate(final HttpURLConnection inConnection, final String inAccountId)
    {
        for (Entry<String, String> header : headersMap.entrySet())
        {
            inConnection.addRequestProperty(header.getKey(), header.getValue());
        }
    }

}
