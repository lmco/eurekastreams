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
package org.eurekastreams.server.service.actions.strategies.activity.plugins;

/**
 * Wrapper to hold onto an object mapper and a regex for specific URL mapping ala Flickr, Youtube, etc.
 * 
 */
public class SpecificUrlObjectMapper
{
    /**
     * The regex to match the URL against.
     */
    private String regex;
    /**
     * The object mapper to run when a match is found.
     */
    private ObjectMapper objectMapper;

    /**
     * Sets the regex.
     * 
     * @param inRegex
     *            the regex.
     */
    public void setRegex(final String inRegex)
    {
        regex = inRegex;
    }

    /**
     * Gets the regex.
     * 
     * @return the regex.
     */
    public String getRegex()
    {
        return regex;
    }

    /**
     * Sets the object mapper.
     * 
     * @param inObjectMapper
     *            object mapper.
     */
    public void setObjectMapper(final ObjectMapper inObjectMapper)
    {
        objectMapper = inObjectMapper;
    }

    /**
     * Gets the object mapper.
     * 
     * @return the object mapper.
     */
    public ObjectMapper getObjectMapper()
    {
        return objectMapper;
    }
}
