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
 * A hideable rich text area. Uses a check box to see whether you want to show the form area.
 */
public class HideableRichTextAreaFormElement extends FlowPanel implements FormElement
{
   /**
     * The key.
     */
    private String key;
    
    /**
     * if the element is retired.
     */
    private boolean required;

    /**
     * the site Label CheckBox.
     */
    private BasicCheckBoxFormElement hidingCheckBox;
    
    /**
     * the site Label Txt.
     */
    private RichTextAreaFormElement txtArea;

    /**
     * Creates a hideable rich text area form element.
     * 
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
    public HideableRichTextAreaFormElement(final String inLabel, final String inKey, final String inValue,
            final String inInstructions, final String inCheckboxInstructions, final boolean inRequired)
    {
        key = inKey;
        required = inRequired;

        hidingCheckBox = new BasicCheckBoxFormElement(inLabel, "", inInstructions, false, true);

        FlowPanel checkboxInstructionsPanel = new FlowPanel();
        checkboxInstructionsPanel.getElement().setInnerHTML(inCheckboxInstructions);
        checkboxInstructionsPanel.addStyleName("form-instructions");

        txtArea = new RichTextAreaFormElement("Message ", "richTextArea", inValue, "", required);
        hidingCheckBox.addClickListener(hidePanel);
        hidingCheckBox.addStyleName("hiding-checkboxes");

        txtArea.addStyleName("site-label hideable-richtextarea yui-skin-sam");

        this.add(hidingCheckBox);
        this.add(txtArea);
        this.add(checkboxInstructionsPanel);

        if (txtArea.getValue().equals(""))
        {
            hidingCheckBox.setSelected(false);
            txtArea.setVisible(false);
        }
    }

    /**
     * The CL to hide the Panel.
     */
    ClickListener hidePanel = new ClickListener()
    {
        public void onClick(final Widget arg0)
        {
            if ((Boolean) hidingCheckBox.getValue())
            {
                txtArea.setVisible(true);
            }
            else
            {
                txtArea.setVisible(false);
            }

        }
    };

    /**
     * clear retained value in box.
     */
    public void clearRetainedValue()
    {
        if (!txtArea.isVisible())
        {
            txtArea.clearEditor();
        }
    }
    
    /**
     * Returns if the checkbox is checked.
     * 
     * @return if element is hidden.
     */
    public boolean isChecked()
    {
        return (Boolean) hidingCheckBox.getValue();
    }

    /**
     * get key.
     * 
     * @return the key.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * get value.
     * 
     * @return value.
     */
    public Serializable getValue()
    {

        //returning null if this is required the checkbox is enabled and is empty.
        if (required && txtArea.isEmpty() && txtArea.isVisible())
        {
            return null;
        }
        else if (txtArea.isVisible())
        {
            return txtArea.getValue();
        }
        else
        {
            return "";
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
        hidingCheckBox.getLabel().addStyleName("form-error");
    }

    /**
     * Gets called if this element was successful.
     */
    public void onSuccess()
    {
        hidingCheckBox.getLabel().removeStyleName("form-error");
    }

    /**
     * Adds a clicklistener to the CheckBox.
     * 
     * @param cL
     *            The ClickListener to set.
     */
    public void addCheckBoxClickListener(final ClickListener cL)
    {
        hidingCheckBox.addClickListener(cL);
    }
}
