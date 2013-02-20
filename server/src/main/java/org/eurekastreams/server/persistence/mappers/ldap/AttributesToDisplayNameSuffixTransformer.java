/*
 * Copyright (c) 2013 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.ldap;

import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Transformer to return the displayNameSuffix from a javax.naming.directory.Attributes. This will look 
 * to match a field with a regex. If matched, the displayNameSuffix is returned.
 */
public class AttributesToDisplayNameSuffixTransformer implements Transformer<Attributes, String>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.getLog(AttributesToDisplayNameSuffixTransformer.class);

    /**
     * Name of the attribute to look at.
     */
    private String attributeName;

    /**
     * Regex to use to see if the user's attribute matches.
     */
    private String regularExpression;

    /**
     * The suffix to choose for the user if their attribute matches.
     */
    private String displayNameSuffix;

    /**
     * Constructor.
     * 
     * @param inAttributeName
     *            the attribute name to check for match
     * @param inRegularExpression
     *            the regular expression test the attribute value for
     * @param inDisplayNameSuffix
     *            the display name suffix to return if the regex matches
     */
    public AttributesToDisplayNameSuffixTransformer(final String inAttributeName, final String inRegularExpression,
            final String inDisplayNameSuffix)
    {
        attributeName = inAttributeName;
        regularExpression = inRegularExpression;
        displayNameSuffix = inDisplayNameSuffix;
    }

    /**
     * Transform the input Attributes to the display name suffix, or null if not found or not applicable.
     * 
     * @param inAttributes
     *            The Attributes to transform to the display name suffix
     * @return Transformed object.
     */
    public String transform(final Attributes inAttributes)
    {
        String result = null;

        try
        {
            Attribute attribute = inAttributes.get(attributeName);
            log.debug("Matching attribute(with brackets added): [" + attributeName + " = " + attribute.get() + "]");
            if (attribute != null && attribute.get().toString().matches(regularExpression))
            {
                log.debug("Matched - result (with brackets added): [" + displayNameSuffix + "]");
                result = displayNameSuffix;
            }
        }
        catch (Exception ex)
        {
            log.error("Error transforming Attributes to displayNameSuffix: ", ex);
            result = null;
        }

        return result;
    }
}
