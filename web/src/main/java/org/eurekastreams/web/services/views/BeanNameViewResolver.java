/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.services.views;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 * Resolves view names to views for Spring MVC using the view name as the name of a bean in the context.
 */
public class BeanNameViewResolver implements ViewResolver
{
    /** The context from which this service can load view beans. */
    private final BeanFactory beanFactory;

    /**
     * Constructor.
     *
     * @param inBeanFactory
     *            The context from which this service can load view beans.
     */
    public BeanNameViewResolver(final BeanFactory inBeanFactory)
    {
        beanFactory = inBeanFactory;
    }

    /**
     * {@inheritDoc}
     */
    public View resolveViewName(final String viewName, final java.util.Locale locale) throws Exception
    {
        if (beanFactory.containsBean(viewName))
        {
            Object bean = beanFactory.getBean(viewName);
            if (bean instanceof View)
            {
                return (View) bean;
            }
        }
        return null;
    };
}
