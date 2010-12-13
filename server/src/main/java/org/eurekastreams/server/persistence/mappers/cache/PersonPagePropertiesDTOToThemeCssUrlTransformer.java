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
package org.eurekastreams.server.persistence.mappers.cache;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonPagePropertiesDTO;

/**
 * Transformer to convert info from PersonPagePropertiesDTO to theme css url.
 * 
 */
public class PersonPagePropertiesDTOToThemeCssUrlTransformer implements Transformer<PersonPagePropertiesDTO, String>
{
    /**
     * Theme path prefix.
     */
    private final String themePathPrefix;

    /**
     *Theme path prefix.
     */
    private final String themeExtension;

    /**
     * Theme uuid separator.
     */
    private final String themeUuidSeparator;

    /**
     * Mapper to return most recent theme version.
     */
    private DomainMapper<String, String> themeVersionByUuidMapper;

    /**
     * Constructor.
     * 
     * @param inThemePathPrefix
     *            Theme path prefix.
     * @param inThemeExtension
     *            Theme path prefix.
     * @param inThemeUuidSeparator
     *            Theme uuid separator.
     * @param inThemeVersionByUuidMapper
     *            Mapper to return most recent theme version.
     */
    public PersonPagePropertiesDTOToThemeCssUrlTransformer(final String inThemePathPrefix,
            final String inThemeExtension, final String inThemeUuidSeparator,
            final DomainMapper<String, String> inThemeVersionByUuidMapper)
    {
        themePathPrefix = inThemePathPrefix;
        themeExtension = inThemeExtension;
        themeUuidSeparator = inThemeUuidSeparator;
        themeVersionByUuidMapper = inThemeVersionByUuidMapper;
    }

    @Override
    public String transform(final PersonPagePropertiesDTO inTransformType)
    {
        // generate url for theme with most current version (from cache)
        String result = null;
        String themeUuid = inTransformType.getThemeUuid();
        if (themeUuid != null)
        {
            String version = themeVersionByUuidMapper.execute(themeUuid);
            result = themePathPrefix + version + themeUuidSeparator + themeUuid + themeExtension;
        }

        return result;
    }
}
