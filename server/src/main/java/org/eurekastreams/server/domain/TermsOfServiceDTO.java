/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.domain;

import java.io.Serializable;

/**
 * Terms of service DTO.
 */
public class TermsOfServiceDTO implements Serializable
{
    /**
     * Serial version ID.
     */
    private static final long serialVersionUID = -2415665784854656550L;

    /**
     * The terms of service.
     */
    private String termsOfService;

    /**
     * Constructor for serializer.
     */
    @SuppressWarnings("unused")
    private TermsOfServiceDTO()
    {
        //no-op
    }
    
    /**
     * Constructor.
     * @param inTermsOfService The terms of service.
     */
    public TermsOfServiceDTO(final String inTermsOfService)
    {
        // make links open in new window.
        termsOfService = inTermsOfService.replace("<a ", "<a target='_NEW' ");
    }

    /**
     * @return the termsOfService
     */
    public final String getTermsOfService()
    {
        return termsOfService;
    }

    /**
     * @param inTermsOfService
     *            the termsOfService to set
     */
    public final void setTermsOfService(final String inTermsOfService)
    {
        this.termsOfService = inTermsOfService;
    }
}
