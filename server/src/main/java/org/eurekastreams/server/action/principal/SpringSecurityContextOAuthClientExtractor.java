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
package org.eurekastreams.server.action.principal;

import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.oauth.provider.ConsumerDetails;

/**
 * Extracts the identity of the client for the current request. Specifically, returns the OAuth consumer key to uniquely
 * identify the client.
 */
public class SpringSecurityContextOAuthClientExtractor implements Transformer<Object, String>
{
    /** Log. */
    private static Logger logger = LoggerFactory.getLogger(SpringSecurityContextOAuthClientExtractor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String transform(final Object inTransformType)
    {
        try
        {
            SecurityContext securityCtx = SecurityContextHolder.getContext();
            Authentication auth = securityCtx.getAuthentication();
            if (auth != null)
            {
                Object obj = auth.getPrincipal();
                if (obj instanceof ConsumerDetails)
                {
                    return ((ConsumerDetails) obj).getConsumerKey();
                }
            }
        }
        catch (Exception ex)
        {
            logger.info("Unable to populate principal from SecurityContext.", ex);
        }
        return null;
    }
}
