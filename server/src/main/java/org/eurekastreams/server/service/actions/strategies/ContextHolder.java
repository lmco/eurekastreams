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

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ServletContextAware;

/**
 * Holds the ServletContext. This class is used to work around a problem we were having making sure Spring injected the
 * ServletContext into various pieces that needed it -- sometimes the injection was called, sometimes not. This thing
 * always gets the context. Add it as a constructor parameter to Spring beans that need the context.
 */
public class ContextHolder implements ServletContextAware
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(CSSBuilderDecorator.class);

    /**
     * Provides context information for the running servlet.
     */
    private ServletContext context = null;

    /**
     * Constructor.
     */
    public ContextHolder()
    {
    }

    /**
     * Setter called by Spring because of implementing ServletContextAware.
     * 
     * @param inContext
     *            the context
     */
    public void setServletContext(final ServletContext inContext)
    {
        log.debug("ContextHolder now has the ServletContext.");
        context = inContext;
    }

    /**
     * Getter.
     * 
     * @return the servlet context provided by Spring
     */
    public ServletContext getContext()
    {
        return context;
    }
}
