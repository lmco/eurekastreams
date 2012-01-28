/*
 * Copyright (c) 2011-2012 Lockheed Martin Corporation
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
package org.eurekastreams.commons.server;

import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.exceptions.GeneralException;
import org.eurekastreams.commons.exceptions.InvalidActionException;
import org.eurekastreams.commons.exceptions.SessionException;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * Limits exceptions being sent to the client / users are appropriate (do not reveal internal details). Additionally,
 * returned exceptions are also serializable via GWT and JSON.
 */
public class ExceptionSanitizer implements Transformer<Exception, Exception>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public Exception transform(final Exception ex)
    {
        if (ex instanceof ValidationException)
        {
            return ex;
        }
        else if (ex instanceof AuthorizationException)
        {
            // Remove any nested exceptions
            return (ex.getCause() == null) ? ex : new AuthorizationException(ex.getMessage());
        }
        else if (ex instanceof GeneralException)
        {
            // Remove any nested exceptions (particularly want to insure no PersistenceExceptions get sent - they
            // are not serializable plus contain details that should not be exposed to users)
            return (ex.getCause() == null) ? ex : new GeneralException(ex.getMessage());
        }
        else if (ex instanceof ExecutionException)
        {
            // Remove any nested exceptions
            return (ex.getCause() == null) ? ex : new ExecutionException(ex.getMessage());
        }
        else if (ex instanceof InvalidActionException)
        {
            return new GeneralException("Invalid action.");
        }
        else if (ex instanceof NoSuchBeanDefinitionException)
        {
            return new GeneralException("Invalid action.");
        }
        else if (ex instanceof SessionException)
        {
            return new SessionException();
        }
        else
        {
            return new GeneralException("Error performing action.");
        }
    }
}
