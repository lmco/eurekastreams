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
package org.eurekastreams.server.service.opensocial.core.config;

import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.inject.AbstractModule;

/**
 * Module which configures Guice using a Spring context and a list of strategies. Allows the app to be pluggable at
 * runtime.
 */
public class SpringContextGuiceModule extends AbstractModule
{
    /** Log. */
    private Log log = LogFactory.make();

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void configure()
    {
        ApplicationContext appContext =
                new ClassPathXmlApplicationContext("classpath*:conf/applicationContext-container.xml");

        bind(BeanFactory.class).toInstance(appContext);

        List<SpringGuiceConfigurator> list =
                (List<SpringGuiceConfigurator>) appContext.getBean("springGuiceConfigurators");

        if (log.isInfoEnabled())
        {
            log.info("About to configure Guice using " + list.size() + " configurators.");
        }

        for (SpringGuiceConfigurator configurator : list)
        {
            configurator.configure(binder(), appContext);
        }
    }
}
