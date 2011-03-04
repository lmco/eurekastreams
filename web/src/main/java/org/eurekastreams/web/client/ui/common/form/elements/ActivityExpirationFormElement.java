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

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The activity Expiration Form element. This is a very specific class for this element.
 * 
 */
public class ActivityExpirationFormElement extends FlowPanel implements FormElement
{
    /**
     * the value to be retained if the.
     */
    Integer retainedValue = 1;

    /**
     * The value of the form element.
     */
    Integer value;

    /**
     * The key of the form element.
     */
    String key;

    /**
     * the site Label CheckBox.
     */
    private BasicCheckBoxFormElement hidingCheckBox;

    /**
     * the site Label Txt.
     */
    private IntegerTextBoxFormElement hidableIntTextBox;

    /**
     * if the element is required.
     */
    private boolean required;

    /**
     * constructor.
     * 
     * @param inValue
     *            The value to set.
     * @param inKey
     *            The key of the item.
     * @param inRequired
     *            if the element is required.
     */
    public ActivityExpirationFormElement(final Integer inValue, final String inKey, final boolean inRequired)
    {
        value = inValue;
        key = inKey;
        required = inRequired;

        hidingCheckBox = new BasicCheckBoxFormElement("Activity Expiration", "",
                "I would like to specify when posted activity will be deleted.", false, true);

        hidableIntTextBox = new IntegerTextBoxFormElement(3, "every ", "", value == null ? "" : value.toString(),
                " days", required);
        hidingCheckBox.addClickListener(hidePanel);
        hidableIntTextBox.addStyleName(StaticResourceBundle.INSTANCE.coreCss().activityExpiration());
        this.add(hidingCheckBox);
        this.add(hidableIntTextBox);

        if (hidableIntTextBox.getValue() == (Integer) 0)
        {
            hidingCheckBox.setSelected(false);
            hidableIntTextBox.setVisible(false);
        }
    }

    /**
     * CL to hide panel if not selected.
     */
    ClickListener hidePanel = new ClickListener()
    {
        public void onClick(final Widget arg0)
        {
            if ((Boolean) hidingCheckBox.getValue())
            {
                if (retainedValue != null)
                {
                    hidableIntTextBox.setValue(Integer.toString(retainedValue));
                }
                else
                {
                    hidableIntTextBox.setValue(Integer.toString(1));
                }
                hidableIntTextBox.setVisible(true);
            }
            else
            {
                retainedValue = (Integer) hidableIntTextBox.getValue();
                hidableIntTextBox.setValue(Integer.toString(0));
                hidableIntTextBox.setVisible(false);
            }

        }
    };

    /**
     * clear the retained value for this widget.
     */
    public void clearRetainedValue()
    {
        retainedValue = 1;
    }

    /**
     * gets key.
     * 
     * @return the Key.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Gets value.
     * 
     * @return the value.
     */
    public Serializable getValue()
    {
        // TODO replace this with a more sophisticated way to relay this case.
        // returning false if this is required the checkbox is enabled and not filled in.
        if (required && hidableIntTextBox.getValue() == null && (Boolean) hidingCheckBox.getValue())
        {
            return Boolean.FALSE;
        }

        return hidableIntTextBox.getValue();
    }

    /**
     * Gets called if this element has an error.
     * 
     * @param errMessage
     *            the error Message.
     */
    public void onError(final String errMessage)
    {
        hidingCheckBox.getLabel().addStyleName(StaticResourceBundle.INSTANCE.coreCss().formError());
    }

    /**
     * Gets called if this element was successful.
     */
    public void onSuccess()
    {
        hidingCheckBox.getLabel().removeStyleName(StaticResourceBundle.INSTANCE.coreCss().formError());
    }

}
