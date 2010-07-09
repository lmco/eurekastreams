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
package org.eurekastreams.server.service.utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Reads a resource (as defined by Spring) and returns its content as a string. Used to enable beans which expect to
 * receive a simple value to have that value loaded from a resource (e.g. file).
 */
public class ResourceContentLoaderFactoryBean implements FactoryBean, InitializingBean, ResourceLoaderAware
{
    /** Resource loader (from Spring). Note: Should be able to autowire this via @Autowire, but it didn't work. */
    private ResourceLoader resourceLoader;

    /** Path to the resource. */
    private String resourcePath;

    /** Content of the resource. */
    private String resourceContent;

    /**
     * Constructor.
     *
     * @param inResourcePath
     *            Path to the resource.
     */
    public ResourceContentLoaderFactoryBean(final String inResourcePath)
    {
        resourcePath = inResourcePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getObject() throws Exception
    {
        return resourceContent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class getObjectType()
    {
        return String.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSingleton()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        Resource resource = resourceLoader.getResource(resourcePath);
        if (!resource.exists())
        {
            throw new FileNotFoundException("Required resource '" + resourcePath + "' not found.");
        }
        // TODO:  Using Apache Commons IO would make this simpler (and probably more efficient)
        // The entire remainder of this method would be:
        // resourceContent = IOUtils.toString(resource.getInputStream());
        // See http://commons.apache.org/io/api-release/org/apache/commons/io/IOUtils.html#toString(java.io.InputStream)
        final int bufferSize = 16 * 1024;
        char[] buffer = new char[bufferSize];
        Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
        StringBuilder builder = new StringBuilder();
        try
        {
            int numRead;
            while ((numRead = reader.read(buffer)) > 0)
            {
                builder.append(buffer, 0, numRead);
            }
        }
        finally
        {
            reader.close();
        }
        resourceContent = builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResourceLoader(final ResourceLoader inResourceLoader)
    {
        resourceLoader = inResourceLoader;
    }
}
