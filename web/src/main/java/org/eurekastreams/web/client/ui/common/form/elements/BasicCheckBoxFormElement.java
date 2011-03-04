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

import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Makes a basic text area (multi line text box) form element.
 * 
 */
public class BasicCheckBoxFormElement extends FlowPanel implements FormElement
{
    /**
     * Checkbox object.
     */
    private CheckBox cb;
    /**
     * The label.
     */
    private Label label = new Label();
    /**
     * Puts a (required) on the form.
     */
    private Label requiredLabel = new Label();
    
    /**
     * Instructions for the control.
     */
    private Label instructions = new Label();
    /**
     * The key that this corresponds to in the model.
     */
    private String key = "";
    
    /**
     * True when you want getValue() to return true if unchecked.
     */
    private boolean reverseValue = false;
    
    /**
     * Body of the check box.
     */
    private FlowPanel body;

    /**
     * Creates a basic text area form element.
     * 
     * @param labelVal
     *            the label (i.e. "Quote").
     * @param inKey
     *            the key in the model (i.e. lastName).
     * @param inCheckBoxText
     *            the instructions to show under it.
     * @param required
     *            if the element is required.
     * @param checked
     *            default value of checked.
     */
    public BasicCheckBoxFormElement(final String labelVal, final String inKey, final String inCheckBoxText,
            final boolean required, final boolean checked)
    {
        this(labelVal, inKey, inCheckBoxText, "", required, checked);
    }
    
    /**
     * Creates a basic text area form element.
     * 
     * @param labelVal
     *            the label (i.e. "Quote").
     * @param inKey
     *            the key in the model (i.e. lastName).
     * @param inCheckBoxText
     *            the instructions to show under it.
     * @param inInstructions
     *            the form element instructions.            
     * @param required
     *            if the element is required.
     * @param checked
     *            default value of checked.
     */
    public BasicCheckBoxFormElement(final String labelVal, final String inKey, final String inCheckBoxText, 
            final String inInstructions, final boolean required, final boolean checked)
    {
        if (required)
        {
            requiredLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().requiredFormLabel());
            requiredLabel.setText("(required)");
        }
        body = new FlowPanel();
        key = inKey;
        label.setText(labelVal);
        label.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formLabel());
        body.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formCheckBox());
        
        instructions.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formInstructions());
        instructions.setText(inInstructions);

        // Fix IE bug, shows empty divs
        instructions.setVisible(instructions.getText().length() > 0);
        
        cb = new CheckBox(inCheckBoxText);
        cb.setValue(checked);
        this.add(label);
        this.add(instructions);
        this.add(body);
        body.add(cb);
        this.add(requiredLabel);
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
     * Appends additional widgets to the body of the checkbox.
     * 
     * @param widget
     *            Wedget to append.
     */
    public void addAdditionalInstructions(final Widget widget)
    {
        body.add(widget);
    }

    /**
     * @param enabled
     *            what to set it too.
     */
    public void setEnabled(final boolean enabled)
    {
        cb.setEnabled(enabled);
        if (!enabled)
        {
            cb.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formCheckBoxDisabled());
        }
        else
        {
            cb.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().formCheckBoxDisabled());
        }
    }
    
    /**
     * Set to true when you want "getValue" to return true while the checkbox is unchecked.
     * 
     * @param inReverseValue
     *            Whether or not to reverse the output.
     */
    public void setReverseValue(final boolean inReverseValue)
    {
        reverseValue = inReverseValue;
    }

    /**
     * @return if it is enabled.
     */
    public boolean isEnabled()
    {
        return cb.isEnabled();
    }

    /**
     * @param selected
     *            to set it to checked or not.
     */
    public void setSelected(final boolean selected)
    {
        cb.setValue(selected);
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
     * 
     * @return Value to save to the DB.
     */
    public Serializable getValue()
    {
        if (reverseValue)
        {
            return !cb.getValue();
        }
        else
        {
            return cb.getValue();
        }
    }

    /**
     * adds a ClickListener to the CheckBox element.
     * 
     * @param cL
     *            The CLicklistener to add.
     */
    public void addClickListener(final ClickListener cL)
    {
        cb.addClickListener(cL);
    }

    /**
     * Returns the label element so that a nesting form element can 
     * set it's style on success and failure.
     * 
     * @return the label element.
     */
    public Label getLabel()
    {
        return label;
    }

}
