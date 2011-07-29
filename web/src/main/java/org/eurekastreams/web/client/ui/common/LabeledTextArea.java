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
package org.eurekastreams.web.client.ui.common;

import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * Makes a textarea show a grayed out label when it's empty.
 */
public class LabeledTextArea extends TextArea
{
    /**
     * If the box is labeled.
     */
    private boolean labeled = false;

    /**
     * The label text.
     */
    private String label;

    /** If the textbox currently has the focus (since I didn't find a GWT API to query for it). */
    private boolean hasFocus = false;

    /**
     * Constructor.
     * 
     * @param inLabel
     *            the label text.
     */
    @UiConstructor
    public LabeledTextArea(final String inLabel)
    {
        label = inLabel;

        final TextArea thisBuffered = this;
        


        this.addFocusListener(new FocusListener()
        {

            public void onFocus(final Widget sender)
            {
                hasFocus = true;
                if (labeled)
                {
                    thisBuffered.setText("");
                    thisBuffered.getElement().removeAttribute("spellcheck");
                }
                thisBuffered.selectAll();
            }

            public void onLostFocus(final Widget sender)
            {
                hasFocus = false;
                checkBox();
            }
        });

        reset();
    }

    /**
     * Checks if the textbox is empty.
     * 
     * @return true if the box is empty.
     */
    public boolean isEmpty()
    {
        return (labeled || super.getText().length() == 0);
    }

    /**
     * Check if the box should be labeled.
     */
    public void checkBox()
    {
        if (isEmpty())
        {
            reset();
        }
        else
        {
            labeled = false;
            this.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().emptyLabeledTextbox());
            this.getElement().removeAttribute("spellcheck");
        }
    }

    /**
     * Reset.
     */
    public void reset()
    {
        if (!hasFocus)
        {
            labeled = true;
            this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().emptyLabeledTextbox());
            this.getElement().setAttribute("spellcheck", "false");
            super.setText(label);
        }
        else
        {
            super.setText("");
            this.getElement().removeAttribute("spellcheck");
        }
    }

    /**
     * Overrides setText to also clear the label of the box.
     * 
     * @param text
     *            The text to put in the text box.
     */
    @Override
    public void setText(final String text)
    {
        labeled = false;
        this.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().emptyLabeledTextbox());
        this.getElement().removeAttribute("spellcheck");
        super.setText(text);
    }
    
    /**
     * Overrides getText to not return the label.
     * 
     * @return the text.
     */
    @Override
    public String getText()
    {
        if (labeled)
        {
            return "";
        }
        else
        {
            return super.getText();
        }
    }

    /**
     * Sets the label for an empty text box.
     * 
     * @param inLabel
     *            The label.
     */
    public void setLabel(final String inLabel)
    {
        label = "  " + inLabel;
        reset();
    }

    /**
     * returns the empty text box label.
     * 
     * @return the label
     */
    public String getLabel()
    {
        return label;
    }
}
