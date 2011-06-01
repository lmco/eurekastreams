/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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

import org.eurekastreams.web.client.ui.common.LabeledTextBox;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Creates a basic single line text box form element.
 *
 */
public class BasicTextBoxFormElement extends FlowPanel implements FormElement
{
    /**
     * The text box.
     */
    private final LabeledTextBox textBox = new LabeledTextBox("");
    /**
     * The label.
     */
    private final Label label = new Label();
    /**
     * Puts a (required) on the form.
     */
    Label requiredLabel = new Label();
    /**
     * instructions for the element.
     */
    Label instructions = new Label();
    /**
     * The key that this corresponds to in the model.
     */
    private String key = "";

    /**
     * Creates a basic text area form element.
     *
     * @param size
     *            the size of the text box.
     * @param matchSize
     *            flag to match the length of the field in the UI with the number of characters allowed (size).
     * @param labelVal
     *            the label (i.e. "Quote").
     * @param inKey
     *            the key in the model (i.e. lastName).
     * @param value
     *            the default value of the element.
     * @param inInstructions
     *            the instructions to show under it.
     * @param required
     *            whether or not this textbox is required.
     */
    public BasicTextBoxFormElement(final int size, final boolean matchSize, final String labelVal, final String inKey,
            final String value, final String inInstructions, final boolean required)
    {
        this(labelVal, inKey, value, inInstructions, required);
        setLimitedSize(size, matchSize);
    }

    /**
     * Creates a basic text area form element.
     *
     * @param emptyLabel
     *            the label to display when the text box is empty.
     * @param labelVal
     *            the label (i.e. "Quote").
     * @param inKey
     *            the key in the model (i.e. lastName).
     * @param value
     *            the default value of the element.
     * @param inInstructions
     *            the instructions to show under it.
     * @param required
     *            whether or not this textbox is required.
     */
    public BasicTextBoxFormElement(final String emptyLabel, final String labelVal, final String inKey,
            final String value, final String inInstructions, final boolean required)
    {
        this(labelVal, inKey, value, inInstructions, required);
        textBox.setLabel(emptyLabel);
    }

    /**
     * Creates a basic text area form element.
     *
     * @param emptyLabel
     *            the label to display when the text box is empty.
     * @param size
     *            the size of the text box.
     * @param matchSize
     *            flag to match the length of the field in the UI with the number of characters allowed (size).
     * @param labelVal
     *            the label (i.e. "Quote").
     * @param inKey
     *            the key in the model (i.e. lastName).
     * @param value
     *            the default value of the element.
     * @param inInstructions
     *            the instructions to show under it.
     * @param required
     *            whether or not this textbox is required.
     */
    public BasicTextBoxFormElement(final String emptyLabel, final int size, final boolean matchSize,
            final String labelVal, final String inKey, final String value, final String inInstructions,
            final boolean required)
    {
        this(labelVal, inKey, value, inInstructions, required);
        textBox.setLabel(emptyLabel);
        setLimitedSize(size, matchSize);
    }

    /**
     * Creates a basic text area form element.
     *
     * @param labelVal
     *            the label (i.e. "Quote").
     * @param inKey
     *            the key in the model (i.e. lastName).
     * @param value
     *            the default value of the element.
     * @param inInstructions
     *            the instructions to show under it.
     * @param required
     *            whether or not this textbox is required.
     */
    public BasicTextBoxFormElement(final String labelVal, final String inKey, final String value,
            final String inInstructions, final boolean required)
    {
        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().basicTextbox());

        key = inKey;
        label.setText(labelVal);
        label.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formLabel());
        textBox.setText(value);
        textBox.checkBox();

        if (required)
        {
            requiredLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().requiredFormLabel());
            requiredLabel.setText("(required)");
        }

        instructions.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formInstructions());
        instructions.setText(inInstructions);

        // Fix IE bug, shows empty divs
        instructions.setVisible(instructions.getText().length() > 0);

        // Need to do this to fix an especially nasty IE CSS bug (input margin inheritance)
        final SimplePanel textWrapper = new SimplePanel();
        textWrapper.addStyleName(StaticResourceBundle.INSTANCE.coreCss().inputWrapper());
        textWrapper.add(textBox);

        this.add(requiredLabel);
        this.add(label);
        this.add(textWrapper);
        this.add(instructions);
    }

    /**
     * Sets the text box as focused.
     */
    public void setFocus()
    {
        textBox.setFocus(true);
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
    public Serializable getValue()
    {
        return textBox.getText();
    }

    /**
     * Sets the value of the text box.
     *
     * @param inValue
     *            the inValue.
     */
    public void setValue(final String inValue)
    {
        textBox.setText(inValue);
        textBox.checkBox();
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

    /**
     * Called when the text box has a limited size.
     *
     * @param inSize
     *            The # of characters the text box can hold.
     * @param matchSize
     *            flag to match the length of the field in the UI with the number of characters allowed (size).
     */
    private void setLimitedSize(final int inSize, final boolean matchSize)
    {
        textBox.setMaxLength(inSize);
        if (matchSize)
        {
            textBox.setVisibleLength(inSize);
            textBox.addStyleName(StaticResourceBundle.INSTANCE.coreCss().variable());
        }
    }

    /**
     * Returns the text box itself, for manipulation.
     *
     * @return the text box.
     */
    public LabeledTextBox getTextBox()
    {
        return textBox;
    }
}
