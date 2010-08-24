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
package org.eurekastreams.server.persistence.mappers.ldap.callback;

import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.ContextMapperCallbackHandler;

/**
 * Return {@link CollectingNameClassPairCallbackHandler} based on an {@link ContextMapper}.
 * 
 */
public class ContextMapperCallbackHandlerFactory implements CallbackHandlerFactory
{
    /**
     * {@link ContextMapper}.
     */
    private ContextMapper mapper;

    /**
     * Constructor.
     * 
     * @param inMapper
     *            {@link ContextMapper}.
     */
    public ContextMapperCallbackHandlerFactory(final ContextMapper inMapper)
    {
        mapper = inMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollectingNameClassPairCallbackHandler getCallbackHandler()
    {
        return new ContextMapperCallbackHandler(mapper);
    }
}
