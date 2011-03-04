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
package org.eurekastreams.web.client.ui.common.form.elements;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Makes a basic form element with a drop down.
 *
 */
public class BasicDropDownFormElement extends FlowPanel implements FormElement
{
    /**
     * The text box.
     */
    private ListBox dropDown = new ListBox();
    /**
     * The label.
     */
    private Label label = new Label();
    /**
     * Puts a (required) on the form.
     */
    private Label requiredLabel = new Label();
    /**
     * instructions for the element.
     */
    private Label instructions = new Label();
    /**
     * The key that this corresponds to in the model.
     */
    private String key = "";

    /**
     * Common setup for a basic text area form element.
     *
     * @param labelVal
     *            the label (i.e. "Quote").
     * @param inKey
     *            the key in the model (i.e. lastName).
     * @param inInstructions
     *            the instructions to show under it.
     * @param required
     *            whether or not this element is required.
     */

    protected BasicDropDownFormElement(final String labelVal, final String inKey, final String inInstructions,
            final boolean required)
    {
        key = inKey;
        label.setText(labelVal);
        label.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formLabel());

        if (required)
        {
            requiredLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().requiredFormLabel());
            requiredLabel.setText("(required)");
        }

        instructions.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formInstructions());
        instructions.setText(inInstructions);
        
        instructions.setVisible(inInstructions.length() > 0);

        this.add(label);
        this.add(requiredLabel);
        this.add(dropDown);
        this.add(instructions);
    }

    /**
     * Creates a basic text area form element.
     *
     * @param labelVal
     *            the label (i.e. "Quote").
     * @param inKey
     *            the key in the model (i.e. lastName).
     * @param values
     *            the values in the drop down.
     * @param currentValue
     *            the default value of the element.
     * @param inInstructions
     *            the instructions to show under it.
     * @param required
     *            whether or not this element is required.
     */
    public BasicDropDownFormElement(final String labelVal, final String inKey, final List<String> values,
            final String currentValue, final String inInstructions, final boolean required)
    {
        this(labelVal, inKey, inInstructions, required);

        for (String value : values)
        {
            dropDown.addItem(value);
            if (value.equals(currentValue))
            {
                dropDown.setSelectedIndex(dropDown.getItemCount() - 1);
            }
        }
    }

    /**
     * Creates a basic text area form element.
     * 
     * @param labelVal
     *            the label (i.e. "Quote").
     * @param inKey
     *            the key in the model (i.e. lastName).
     * @param values
     *            the values in the drop down, map of value (as map key) to display text (as map value). The values thus
     *            must be unique, whereas the text need not be.
     * @param currentValue
     *            the default value of the element. This matches against the values, not the display text.
     * @param inInstructions
     *            the instructions to show under it.
     * @param required
     *            whether or not this element is required.
     */
    public BasicDropDownFormElement(final String labelVal, final String inKey, final Map<String, String> values,
            final String currentValue, final String inInstructions, final boolean required)
    {
        this(labelVal, inKey, inInstructions, required);

        for (Entry<String, String> entry : values.entrySet())
        {
            dropDown.addItem(entry.getValue(), entry.getKey());
            if (entry.getKey().equals(currentValue))
            {
                dropDown.setSelectedIndex(dropDown.getItemCount() - 1);
            }
        }
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
     * Gets the value of the text box.
     *
     * @return the value.
     */
    public String getValue()
    {
        // Note: This works for both the list and map constructor approaches. With the list, the value is the same as
        // the display text, so this will return the expected value.
        return dropDown.getValue(dropDown.getSelectedIndex());
    }

    /**
     * Disables an item so it cannot be selected.
     *
     * @param value
     *            Value of item to disable.
     */
    public void disableValue(final String value)
    {
        Element option = findElementByValue(value);
        if (option != null)
        {
            option.setAttribute("disabled", "disabled");
        }
    }

    /**
     * Finds an item in the drop-down by value.
     *
     * @param value
     *            Value to search for.
     * @return The DOM element for the item.
     */
    private Element findElementByValue(final String value)
    {
        NodeList<Element> options = dropDown.getElement().getElementsByTagName("option");
        for (int i = 0; i < options.getLength(); i++)
        {
            Element option = options.getItem(i);
            if (option.getAttribute("value").equals(value))
            {
                return option;
            }
        }
        return null;
    }

    /**
     * Gets called if this element has an error.
     *
     * @param errMessage
     *            the error Message.
     */
    public void onError(final String errMessage)
    {
        label.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formError());
    }

    /**
     * Gets called if this element was successful.
     */
    public void onSuccess()
    {
        label.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().formError());
    }
}
