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

import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Makes a basic text area (multi line text box) form element.
 *
 */
public class BasicTextAreaFormElement extends Composite implements FormElement
{
    /**
     * The text box.
     */
    private final TextArea textBox = new TextArea();
    /**
     * The label.
     */
    private final Label label = new Label();
    /**
     * Puts a (required) on the form.
     */
    private final Label requiredLabel = new Label();

    /**
     * The key that this corresponds to in the model.
     */
    private String key = "";
    /**
     * The max chars of the text area.
     */
    private final Integer size;
    /**
     * The count down label.
     */
    private Label countDown = new Label();

    /** Current length of text entered. */
    private int contentLength;

    // /** If the text entered is over the limit. */
    // private boolean overLimit;
    //
    // /** If the text area is empty. */
    // private boolean empty;

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
    public BasicTextAreaFormElement(final String labelVal, final String inKey, final String value,
            final String inInstructions, final boolean required)
    {
        this(null, labelVal, inKey, value, inInstructions, required);
    }

    /**
     * Creates a basic text area form element.
     *
     * @param inSize
     *            the size (in chars) the text are can hold.
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
    public BasicTextAreaFormElement(final Integer inSize, final String labelVal, final String inKey,
            final String value, final String inInstructions, final boolean required)
    {
        FlowPanel main = new FlowPanel();

        key = inKey;
        label.setText(labelVal);
        label.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formLabel());
        textBox.setText(value);
        size = inSize;

        if (required)
        {
            requiredLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().requiredFormLabel());
            requiredLabel.setText("(required)");
        }

        main.add(label);

        main.add(requiredLabel);

        // Need to do this to fix an especially nasty IE CSS bug (input margin inheritance)
        final SimplePanel textWrapper = new SimplePanel();
        textWrapper.add(textBox);
        textWrapper.addStyleName(StaticResourceBundle.INSTANCE.coreCss().inputWrapper());

        main.add(textWrapper);

        if (size != null)
        {
            countDown = new Label(Integer.toString(size - textBox.getText().length()));
            countDown.addStyleName(StaticResourceBundle.INSTANCE.coreCss().charactersRemaining());
            main.add(countDown);

            textBox.addKeyUpHandler(new KeyUpHandler()
            {
                public void onKeyUp(final KeyUpEvent event)
                {
                    onTextChanges();
                }
            });

            textBox.addChangeHandler(new ChangeHandler()
            {
                public void onChange(final ChangeEvent event)
                {
                    onTextChanges();
                }
            });
        }

        if (inInstructions != null && !inInstructions.isEmpty())
        {
            Label instructions = new Label(inInstructions);
            instructions.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formInstructions());
            main.add(instructions);
        }

        initWidget(main);
    }

    /**
     * Gets triggered whenever the text box changes.
     */
    protected void onTextChanges()
    {
        contentLength = textBox.getText().length();
        int charsRemaining = size - contentLength;
        countDown.setText(Integer.toString(charsRemaining));
        if (charsRemaining >= 0 && charsRemaining != size)
        {
            countDown.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().overCharacterLimit());
        }
        else if (charsRemaining != size)
        {
            countDown.addStyleName(StaticResourceBundle.INSTANCE.coreCss().overCharacterLimit());
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
        return textBox.getText();
    }

    /**
     * sets the value of the text box.
     *
     * @param inValue
     *            The Value to set.
     */
    public void setValue(final String inValue)
    {
        textBox.setText(inValue);
    }

    /**
     * Removes all content from the entry field.
     */
    public void clear()
    {
        textBox.setText("");

        // insure countdown and internal status are properly set - textBox.setText wasn't raising the value changed
        // event during testing
        if (contentLength != 0 && size != null)
        {
            onTextChanges();
        }
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
     * @return If over the allowed text entry limit.
     */
    public boolean isOverLimit()
    {
        return contentLength > size;
    }

    /**
     * @return If contains any entered text.
     */
    public boolean isEmpty()
    {
        return (size != null) ? (contentLength == 0) : (textBox.getText().isEmpty());
    }
}
