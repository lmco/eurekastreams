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

import java.io.Serializable;

import org.eurekastreams.web.client.ui.common.LabeledTextBox;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Creates a short name form element.
 * 
 */
public class ShortnameFormElement extends FlowPanel implements FormElement
{
    /**
     * The text box.
     */
    private LabeledTextBox textBox = new LabeledTextBox("");
    /**
     * The label.
     */
    private Label label = new Label();
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
     * Shortname length.
     */
    private static final int SHORTNAME_LENGTH = 20;

    /**
     * Creates a basic text area form element.
     * 
     * @param labelVal
     *            the label (i.e. "Quote").
     * @param inKey
     *            the key in the model (i.e. lastName).
     * @param value
     *            the default value of the element.
     * @param inUrl
     *            the url.
     * @param inInstructions
     *            the instructions to show under it.
     * @param required
     *            whether or not this textbox is required.
     */
    public ShortnameFormElement(final String labelVal, final String inKey, final String value, final String inUrl,
            final String inInstructions, final boolean required)
    {
        this.addStyleName("shortname-form-element");

        key = inKey;
        label.setText(labelVal);
        label.addStyleName("form-label");
        textBox.setMaxLength(SHORTNAME_LENGTH);
        textBox.setText(value);
        textBox.checkBox();
        final Label url = new Label(inUrl);
        url.addStyleName("shortname-url");

        if (required)
        {
            requiredLabel.addStyleName("required-form-label");
            requiredLabel.setText("(required)");
        }

        instructions.addStyleName("form-instructions");
        instructions.setText(inInstructions);

        // Fix IE bug, shows empty divs
        instructions.setVisible(instructions.getText().length() > 0);

        textBox.addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent arg0)
            {
                textBox.setText(textBox.getText().toLowerCase());
            }
        });

        // Need to do this to fix an especially nasty IE CSS bug (input margin inheritance)
        final SimplePanel textWrapper = new SimplePanel();
        textWrapper.addStyleName("input-wrapper");
        textWrapper.add(textBox);

        this.add(label);
        this.add(url);
        this.add(textWrapper);
        this.add(requiredLabel);
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
        label.addStyleName("form-error");
    }

    /**
     * Gets called if this element was successful.
     */
    public void onSuccess()
    {
        label.removeStyleName("form-error");
    }
}
