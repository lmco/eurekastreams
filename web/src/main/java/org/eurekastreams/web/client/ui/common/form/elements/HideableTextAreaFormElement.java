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
import com.google.gwt.user.client.ui.Widget;

/**
 * A hideable textarea. uses a check box to see whether you want to show the form area.
 * 
 */
public class HideableTextAreaFormElement extends FlowPanel implements FormElement
{
    /**
     * TempValue that is retained in case users change their mind.
     */
    String retainedValue;
    /**
     * The form element key.
     */
    String key;
    /**
     * The value of the form element.
     */
    String value;
    /**
     * The label to go in front of the element.
     */
    String label;
    /**
     * The instructions.
     */
    String instructions;
    /**
     * The instructions under the checkbox.
     */
    String checkboxInstructions;

    /**
     * If the element is required.
     */
    boolean required;

    /**
     * the site Label CheckBox.
     */
    private BasicCheckBoxFormElement hidingCheckBox;
    /**
     * the site Label Txt.
     */
    private BasicTextAreaFormElement hidableTxtArea;

    /**
     * Creates a hideable textArea Form element.
     * 
     * @param inSize
     *            The max size for the text box.
     * @param inLabel
     *            The label test.
     * @param inKey
     *            The value name of the element.
     * @param inValue
     *            The value of the element.
     * @param inInstructions
     *            The instructions for the element.
     * @param inCheckboxInstructions
     *            The instructions describing what the checkbox is hiding.
     * @param inRequired
     *            If the field is required.
     */
    public HideableTextAreaFormElement(final int inSize, final String inLabel, final String inKey,
            final String inValue, final String inInstructions, final String inCheckboxInstructions,
            final boolean inRequired)
    {
        key = inKey;
        value = inValue;
        label = inLabel;
        instructions = inInstructions;
        checkboxInstructions = inCheckboxInstructions;
        required = inRequired;

        hidingCheckBox = new BasicCheckBoxFormElement(label, "", instructions, false, true);

        FlowPanel checkboxInstructionsPanel = new FlowPanel();
        checkboxInstructionsPanel.getElement().setInnerHTML(checkboxInstructions);
        checkboxInstructionsPanel.addStyleName("form-instructions");
        this.addStyleName("hideable-textarea");

        hidableTxtArea = new BasicTextAreaFormElement(inSize, null, "", value, null, true);
        hidingCheckBox.addClickListener(hidePanel);

        hidableTxtArea.addStyleName("site-label");

        this.add(hidingCheckBox);        
        this.add(hidableTxtArea);
        this.add(checkboxInstructionsPanel);

        if (hidableTxtArea.getValue().isEmpty())
        {
            hidingCheckBox.setSelected(false);
            hidableTxtArea.setVisible(false);
        }

    }

    /**
     * The CL to hide the panel if the CheckBox is not selected.
     */
    ClickListener hidePanel = new ClickListener()
    {
        public void onClick(final Widget arg0)
        {
            if ((Boolean) hidingCheckBox.getValue())
            {
                hidableTxtArea.setValue(retainedValue);
                hidableTxtArea.setVisible(true);
            }
            else
            {
                retainedValue = hidableTxtArea.getValue();
                hidableTxtArea.setValue(null);
                hidableTxtArea.setVisible(false);
            }

        }
    };

    /**
     * Returns the key.
     * 
     * @return The key.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Returns the Value.
     * 
     * @return The value of the element.
     */
    public Serializable getValue()
    {
        // TODO replace this with a more sophisticated way to relay this case.
        // returning null if this is required the checkbox is enabled and not filled in.
        if (required && ("".equals(hidableTxtArea.getValue().trim())) && (Boolean) hidingCheckBox.getValue())
        {
            return null;
        }

        return hidableTxtArea.getValue();
    }

    /**
     * clear the retained value for this widget.
     */
    public void clearRetainedValue()
    {
        retainedValue = null;
    }

    /**
     * Gets called if this element has an error.
     * 
     * @param errMessage
     *            the error Message.
     */
    public void onError(final String errMessage)
    {
        hidingCheckBox.getLabel().addStyleName("form-error");
    }

    /**
     * Gets called if this element was successful.
     */
    public void onSuccess()
    {
        hidingCheckBox.getLabel().removeStyleName("form-error");
    }
}
