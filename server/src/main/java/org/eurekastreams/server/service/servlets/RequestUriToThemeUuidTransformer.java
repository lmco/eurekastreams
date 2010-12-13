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
package org.eurekastreams.server.service.servlets;

import org.apache.commons.lang.StringUtils;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;

/**
 * Transformer to parse theme uuid from request uri. Expecting to handle format of
 * /blah/whatever/somethemeversion<UUID_SEPARATOR>themuuid<FILE_EXTENSION>
 * 
 */
public class RequestUriToThemeUuidTransformer implements Transformer<String, String>
{
    /**
     * Uuid separator in css filename.
     */
    private final String uuidSeparator;

    /**
     * File name extension.
     */
    private final String fileExtension;

    /**
     * Constructor.
     * 
     * @param inUuidSeparator
     *            Uuid separator in css filename.
     * @param inFileExtension
     *            File name extension.
     */
    public RequestUriToThemeUuidTransformer(final String inUuidSeparator, final String inFileExtension)
    {
        uuidSeparator = inUuidSeparator;
        fileExtension = inFileExtension;
    }

    /**
     * Parse theme uuid from request uri. Expecting to handle format of
     * /blah/whatever/somethemeversion<UUID_SEPARATOR>themuuid<FILE_EXTENSION>
     * 
     * @param inTransformType
     *            Request uri.
     * @return lower case theme uuid from request uri.
     */
    @Override
    public String transform(final String inTransformType)
    {
        return inTransformType == null ? null : StringUtils.removeEndIgnoreCase(
                StringUtils.substringAfterLast(inTransformType, uuidSeparator), fileExtension).toLowerCase();
    }

}
