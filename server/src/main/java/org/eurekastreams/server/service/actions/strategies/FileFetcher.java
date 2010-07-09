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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Fetch a resource from the file system.
 */
public class FileFetcher implements ResourceFetcher
{

    /**
     * Build an InputStream to read the contents of the specified file.
     * 
     * @param location
     *            the path to the file to be read.
     * @return an InputStream for the specified file
     * @throws FileNotFoundException
     *             thrown if the location does not point to a readable file
     */
    public InputStream getInputStream(final String location)
            throws FileNotFoundException
    {
        return new FileInputStream(location);
    }

}
