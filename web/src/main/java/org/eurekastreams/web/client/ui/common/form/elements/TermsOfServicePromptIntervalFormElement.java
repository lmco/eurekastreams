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
package org.eurekastreams.web.client.ui.common.form.elements;

import java.io.Serializable;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * The terms of service prompt interval element. This is a very specific class for this element.
 */
public class TermsOfServicePromptIntervalFormElement extends FlowPanel implements FormElement
{
    /**
     * The value of the form element.
     */
    Integer value;

    /**
     * The key of the form element.
     */
    String key;

    /**
     * The text box that contains the value for the number of days before you should prompt.
     */
    private TextBox promptInterval = new TextBox();

    /**
     * Constructor.
     * 
     * @param inValue
     *            The value to set.
     * @param inKey
     *            The key of the item.
     */
    public TermsOfServicePromptIntervalFormElement(final Integer inValue, final String inKey)
    {
        value = inValue;
        key = inKey;

        promptInterval.setValue(Integer.toString(value));
        promptInterval.addStyleName("prompt-interval");
        promptInterval.setMaxLength(5);
        promptInterval.setName(key);

        Label promptLabel = new Label("Prompt Interval");
        promptLabel.addStyleName("form-label");

        this.add(promptLabel);
        this.add(new InlineLabel("every"));
        this.add(promptInterval);
        this.add(new InlineLabel("days"));

        this.addStyleName("tos-prompt-interval");
    }

    /**
     * Gets the key.
     * 
     * @return the key.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Sets the value.
     * 
     * @param inValue
     *            the value to set.
     */
    public void setValue(final int inValue)
    {
        value = inValue;
    }

    /**
     * Gets value.
     * 
     * @return the value.
     */
    public Serializable getValue()
    {
        return new Integer(promptInterval.getValue());
    }

    /**
     * Gets called if this element has an error.
     * 
     * @param errMessage
     *            the error Message.
     */
    public void onError(final String errMessage)
    {
        this.addStyleName("form-error");
    }

    /**
     * Gets called if this element was successful.
     */
    public void onSuccess()
    {
        this.removeStyleName("form-error");
    }
}
