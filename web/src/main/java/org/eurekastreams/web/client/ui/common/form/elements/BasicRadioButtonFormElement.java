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
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Basic RadioButton.
 */
public class BasicRadioButtonFormElement extends FlowPanel
{
    /**
     * The value of the radio button. This is used because GWT 1.5 does not support the radio button value attribute.
     * Fixed in 1.6 rc.
     */
    private Serializable value;
    /**
     * The label for the Radio Button.
     */
    private String label = "";
    /**
     * The Radio Button.
     */
    private RadioButton rb;

    /** Instructions widget. */
    private Panel instructionsPanel;

    /**
     * Constructor for a radio button element.
     *
     * @param inValue
     *            the value the group should return if this button is selected.
     * @param inLabel
     *            The label for this button.
     * @param inInstructions
     *            Instructios for this button.
     * @param inGroupName
     *            The name of the group to add the button too.
     * @param selected
     *            Whether the button is selected.
     */
    public BasicRadioButtonFormElement(final Serializable inValue, final String inLabel, final String inInstructions,
            final String inGroupName, final Boolean selected)
    {

        label = inLabel;
        value = inValue;

        rb = new RadioButton(inGroupName, label);
        rb.setChecked(selected);
        rb.addStyleName("form-radioButton");

        this.add(rb);
        if (inInstructions != null && !inInstructions.isEmpty())
        {
            instructionsPanel = new FlowPanel();
            instructionsPanel.addStyleName("form-instructions");
            add(instructionsPanel);

            instructionsPanel.add(new InlineLabel(inInstructions));
        }
    }

    /**
     * @return the value
     */
    public Serializable getValue()
    {
        return value;
    }

    /**
     * @return the label
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * @param clickListener
     *            Allows for the addition of click listeners to this button. Adds ability to set a value of a button
     *            group when a radio button is clicked.
     */
    public void addClickListener(final ClickListener clickListener)
    {
        rb.addClickListener(clickListener);
    }

    /**
     * Adds additional widgets to the instructions area.
     * 
     * @param widget
     *            Widget to add.
     */
    public void addToInstructions(final Widget widget)
    {
        instructionsPanel.add(widget);
    }
}
