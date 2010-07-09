/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.actions.strategies;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Retrieve a resource specified by a URL.
 */
public class URLFetcher implements ResourceFetcher
{
    /**
     * Provide an InputStream for the specified URL.
     * 
     * @param location
     *            a URL
     * @return an InputStream for the specified URL
     * @throws IOException
     *             throws MalformedURLException if location is not a valid URL;
     *             IOException if the resource cannot be read
     */
    public InputStream getInputStream(final String location) throws IOException
    {
        URL url = new URL(location);
        return url.openStream();
    }

}
