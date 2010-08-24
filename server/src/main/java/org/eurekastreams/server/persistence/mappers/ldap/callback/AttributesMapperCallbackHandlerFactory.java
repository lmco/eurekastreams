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

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.AttributesMapperCallbackHandler;
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler;

/**
 * Return {@link CollectingNameClassPairCallbackHandler} based on an {@link AttributesMapper}.
 * 
 */
public class AttributesMapperCallbackHandlerFactory implements CallbackHandlerFactory
{
    /**
     * {@link AttributesMapper}.
     */
    private AttributesMapper mapper;

    /**
     * Constructor.
     * 
     * @param inMapper
     *            {@link AttributesMapper}.
     */
    public AttributesMapperCallbackHandlerFactory(final AttributesMapper inMapper)
    {
        mapper = inMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollectingNameClassPairCallbackHandler getCallbackHandler()
    {
        return new AttributesMapperCallbackHandler(mapper);
    }

}
