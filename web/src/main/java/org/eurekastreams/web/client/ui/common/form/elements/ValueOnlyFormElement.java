/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.form.elements;

import java.io.Serializable;

/**
 * Basically, the equivalent of a hidden field in our magic form builder class, except since we do everything in
 * javascript, why not store the hidden values in here as well?
 * 
 */
public class ValueOnlyFormElement implements FormElement
{
    /**
     * The key.
     */
    private String key;
    /**
     * The value.
     */
    private Serializable value;

    /**
     * Get the key.
     * 
     * @return the key.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Get the value.
     * 
     * @return the value.
     */
    public Serializable getValue()
    {
        return value;
    }

    /**
     * Sets the value of the element.
     * 
     * @param inValue
     *            The value to set.
     */
    public void setValue(final Serializable inValue)
    {
        value = inValue;
    }

    /**
     * Creates a value only form element (like a JS hidden field) that gets submitted and persisted with the form.
     * 
     * @param inKey
     *            the key.
     * @param inValue
     *            the value.
     */
    public ValueOnlyFormElement(final String inKey, final Serializable inValue)
    {
        key = inKey;
        value = inValue;
    }

    /**
     * Do nothing on error because nobody can see you.
     * 
     * @param errMessage
     *            the error message no one cares about.
     */
    public void onError(final String errMessage)
    {
        // This is a value only widget, what could I do?
    }

    /**
     * Do nothing on success because no one cares.
     */
    public void onSuccess()
    {
        // This is a value only widget, what could I do?
    }

}
