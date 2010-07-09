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

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Makes a basic RadioButton group.
 *
 */
public class BasicRadioButtonGroupFormElement extends FlowPanel implements FormElement
{
    /**
     * The Radio Button Group Name.
     */
    private String radioButtonGroupName;
    /**
     * The panel holding all radio buttons.
     */
    private FlowPanel radioButtonPanel = new FlowPanel();

    /**
     * The value.
     */
    private Serializable value;
    /**
     * The label for the button group.
     */
    private Label label = new Label();
    /**
     * instructions for the element.
     */
    private Label instructions = new Label();
    /**
     * The key that this corresponds to in the model.
     */
    private String key = "";

    /**
     * clear buttons out of group.
     */
    public void clearGroup()
    {
        radioButtonPanel.clear();
    }

    /**
     * Creates a basic radio button group form element.
     *
     * @param labelVal
     *            the label (i.e. "Quote").
     * @param inKey
     *            the key in the model (i.e. lastName).
     * @param groupName
     *            the default value of the element.
     * @param inInstructions
     *            the instructions to show under it.
     */
    public BasicRadioButtonGroupFormElement(final String labelVal, final String inKey, final String groupName,
            final String inInstructions)
    {
        key = inKey;
        radioButtonGroupName = groupName;
        label.setText(labelVal);
        label.addStyleName("form-label");
        radioButtonPanel.addStyleName("form-radioButtonGroup");
        radioButtonPanel.clear();
        instructions.addStyleName("form-instructions");
        instructions.setText(inInstructions);

        this.add(label);
        this.add(radioButtonPanel);
        this.add(instructions);
        
        instructions.setVisible(inInstructions.length() > 0);
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
     * Adds a radio button to the group defaulted to not selected.
     *
     * @param inLabel
     *            label on the right of the radio button.
     * @param inInstructions
     *            Instructions under the button.
     * @param inValue
     *            The value to return if the button is selected.
     * @return The radio button form element added.
     */
    public BasicRadioButtonFormElement addRadioButton(final String inLabel, final String inInstructions,
            final Serializable inValue)
    {
        return addRadioButton(inLabel, inInstructions, inValue, false, null);
    }

    /**
     * Adds a radio button to the group defaulted to not selected.
     *
     * @param inLabel
     *            label on the right of the radio button.
     * @param inInstructions
     *            Instructions under the button.
     * @param inValue
     *            The value to return if the button is selected.
     * @param selected
     *            Whether the radio button is selected.
     * @return The radio button form element added.
     */
    public BasicRadioButtonFormElement addRadioButton(final String inLabel, final String inInstructions,
            final Serializable inValue, final Boolean selected)
    {
        return addRadioButton(inLabel, inInstructions, inValue, selected, null);
    }

    /**
     * Adds a radio button to the group defaulted to not selected.
     *
     * @param inLabel
     *            label on the right of the radio button.
     * @param inInstructions
     *            Instructions under the button.
     * @param inValue
     *            The value to return if the button is selected.
     * @param selected
     *            Whether the radio button is selected.
     * @param listener
     *            Any Listener you want to add to the button.
     * @return The radio button form element added.
     */
    public BasicRadioButtonFormElement addRadioButton(final String inLabel, final String inInstructions,
            final Serializable inValue, final Boolean selected, final ClickListener listener)
    {
        // if button is selected make it the value of the group. The last button added as selected will be the value of
        // the group.
        if (selected)
        {
            value = inValue;
        }

        final BasicRadioButtonFormElement rbElement =
                new BasicRadioButtonFormElement(inValue, inLabel, inInstructions, radioButtonGroupName, selected);

        // Add a click listener to set the value of the group. This is a work around GWT 1.5 does nto support the value
        // element in check boxes or radio buttons
        rbElement.addClickListener(new ClickListener()
        {
            public void onClick(final Widget arg0)
            {
                value = rbElement.getValue();
            }
        });

        if (null != listener)
        {
            rbElement.addClickListener(listener);
        }

        radioButtonPanel.add(rbElement);

        return rbElement;
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

    /**
     * Returns the value of the Button group.
     *
     * @return value the Value of the object
     */
    public Serializable getValue()
    {
        return value;
    }

}
