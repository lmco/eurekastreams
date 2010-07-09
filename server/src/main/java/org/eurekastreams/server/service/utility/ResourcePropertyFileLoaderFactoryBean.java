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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Reads a property file identified by a resource (as defined by Spring) and it in a Properties object.
 */
public class ResourcePropertyFileLoaderFactoryBean implements FactoryBean, InitializingBean, ResourceLoaderAware
{
    /** Resource loader (from Spring). Note: Should be able to autowire this via @Autowire, but it didn't work. */
    private ResourceLoader resourceLoader;

    /** Path to the resource. */
    private String resourcePath;

    /** Properties loaded. */
    private Properties properties;

    /**
     * Constructor.
     *
     * @param inResourcePath
     *            Path to the resource.
     */
    public ResourcePropertyFileLoaderFactoryBean(final String inResourcePath)
    {
        resourcePath = inResourcePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getObject() throws Exception
    {
        return properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class getObjectType()
    {
        return Properties.class;
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
            throw new FileNotFoundException("Required properties file resource '" + resourcePath + "' not found.");
        }
        properties = new Properties();

        InputStream stream = resource.getInputStream();
        try
        {
            properties.load(stream);
        }
        finally
        {
            stream.close();
        }
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
