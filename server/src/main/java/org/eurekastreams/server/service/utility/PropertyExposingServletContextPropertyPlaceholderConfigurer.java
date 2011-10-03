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
package org.eurekastreams.server.service.utility;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;


/**
 * Derivation of Spring's class that loads property files and substitutes their values into the Spring config files. We
 * want access to the merged set of properties that Spring used so we can use them in the application (e.g. in
 * notification templates), but Spring does not provide access it. Hence this class, which captures Properties object
 * and provides a method to get it.
 *
 * This class is inherently tied to the implementation of Spring. It was based on version 2.5.6.
 *
 * There are two features which are not supported by the Properties object provided: 1. Access to values in the servlet
 * context. 2. Property references in property definitions (recursion). As such, looking up a property in the returned
 * Properties object may result in a different value than what would have been substituted into the Spring configuration
 * file. The reason is that the Spring classes do not perform a simple lookup on the Properties object.
 *
 * This class also provides a Map that allows property lookups without the above limitations. It has higher overhead,
 * and ONLY supports the get() method, but provides full compatibility with Spring (servlet access and recursive
 * definitions).
 */
public class PropertyExposingServletContextPropertyPlaceholderConfigurer extends
        org.springframework.web.context.support.ServletContextPropertyPlaceholderConfigurer
{
    /** Decryptor for handling encrypted configuration. */
    private final StringEncryptor stringEncryptor;

    /**
     * Constructor.
     * 
     * @param inStringEncryptor
     *            Decryptor for handling encrypted configuration.
     */
    public PropertyExposingServletContextPropertyPlaceholderConfigurer(final StringEncryptor inStringEncryptor)
    {
        stringEncryptor = inStringEncryptor;
    }

    // ---------- "APIs" - methods providing properties (accessed in Spring config) ----------

    /**
     * @return The raw merged properties.
     */
    public Properties getRawProperties()
    {
        return properties;
    }

    /**
     * @return Map allowing access to properties with full emulation of Spring-substituted values.
     */
    public Map<String, String> getPropertyAccessor()
    {
        return new PropertyAccessor();
    }

    // ---------- Support for encrypted properties ----------
    // Uses Jasypt; code here based on Jasypt's EncryptablePropertyPlaceholderConfigurer and
    // ServletContextPropertyPlaceholderConfigurer. (Cannot use their classes directly since they were declared as
    // final.)

    /**
     * {@inheritDoc}
     */
    @Override
    protected String convertPropertyValue(final String inOriginalValue)
    {
        return PropertyValueEncryptionUtils.isEncryptedValue(inOriginalValue) ? PropertyValueEncryptionUtils.decrypt(
                inOriginalValue, stringEncryptor) : inOriginalValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String resolvePlaceholder(final String inPlaceholder, final Properties inProps)
    {
        return convertPropertyValue(super.resolvePlaceholder(inPlaceholder, inProps));
    }

    // ---------- Supporting methods ----------

    /** The merged set of properties. */
    private Properties properties;


    /**
     * {@inheritDoc}
     */
    @Override
    protected void processProperties(final ConfigurableListableBeanFactory inBeanFactoryToProcess,
            final Properties inProps) throws BeansException
    {
        // save off the properties
        properties = inProps;

        super.processProperties(inBeanFactoryToProcess, inProps);
    }

    // ---------- Supporting methods specific to property accessor map ----------

    /** Value to replace with null. */
    private String nullValue;

    /** Prefix which must be precede property names for parser. */
    private String placeholderPrefix = DEFAULT_PLACEHOLDER_PREFIX;

    /** Prefix which must be follow property names for parser. */
    private String placeholderSuffix = DEFAULT_PLACEHOLDER_SUFFIX;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNullValue(final String inNullValue)
    {
        nullValue = inNullValue;
        super.setNullValue(inNullValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPlaceholderPrefix(final String inPlaceholderPrefix)
    {
        placeholderPrefix = inPlaceholderPrefix;
        super.setPlaceholderPrefix(inPlaceholderPrefix);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPlaceholderSuffix(final String inPlaceholderSuffix)
    {
        placeholderSuffix = inPlaceholderSuffix;
        super.setPlaceholderSuffix(inPlaceholderSuffix);
    }

    /**
     * Map allowing access to properties with nested property support and servlet context access.
     */
    private class PropertyAccessor implements Map<String, String>
    {
        /** Exception message for write operations. */
        private static final String READ_ONLY_MSG = // \n
        "This map represents a merge of property files and a servlet context and is thus read-only.";

        /** Exception message for unsupported operations. */
        private static final String POOR_CORRESPONDENCE_MSG = // \n
        "This map represents dynamically computed content, thus this operation does not apply.";

        /**
         * {@inheritDoc}
         */
        @Override
        public int size()
        {
            throw new UnsupportedOperationException(POOR_CORRESPONDENCE_MSG);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isEmpty()
        {
            throw new UnsupportedOperationException(POOR_CORRESPONDENCE_MSG);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean containsKey(final Object inKey)
        {
            throw new UnsupportedOperationException(POOR_CORRESPONDENCE_MSG);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean containsValue(final Object inValue)
        {
            throw new UnsupportedOperationException(POOR_CORRESPONDENCE_MSG);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String get(final Object inKey)
        {
            String key = placeholderPrefix + inKey.toString() + placeholderSuffix;

            // taken from PropertyPlaceholderConfigurer.PlaceholderResolvingStringValueResolver.resolveStringValue()
            String value = parseStringValue(key, properties, new HashSet());
            return (value.equals(nullValue) ? null : value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String put(final String inKey, final String inValue)
        {
            throw new UnsupportedOperationException(READ_ONLY_MSG);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String remove(final Object inKey)
        {
            throw new UnsupportedOperationException(READ_ONLY_MSG);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void putAll(final Map< ? extends String, ? extends String> inM)
        {
            throw new UnsupportedOperationException(READ_ONLY_MSG);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clear()
        {
            throw new UnsupportedOperationException(READ_ONLY_MSG);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Set<String> keySet()
        {
            throw new UnsupportedOperationException(POOR_CORRESPONDENCE_MSG);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Collection<String> values()
        {
            throw new UnsupportedOperationException(POOR_CORRESPONDENCE_MSG);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Set<java.util.Map.Entry<String, String>> entrySet()
        {
            throw new UnsupportedOperationException(POOR_CORRESPONDENCE_MSG);
        }
    }
}
