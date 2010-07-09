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

/**
 * Retrieve a resource by various means, depending on the source.
 */
public interface ResourceFetcher
{
    /**
     * Build an InputStream for the requested resource.
     * 
     * @param location
     *            identifies the resource to be loaded
     * @return an InputStream for the requested resource
     * @throws IOException
     *             thrown if the requested resource cannot be read
     */
    InputStream getInputStream(String location) throws IOException;
}
