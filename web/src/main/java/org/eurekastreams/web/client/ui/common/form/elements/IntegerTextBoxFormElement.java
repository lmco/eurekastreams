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

/**
 * Creates a basic single line text box form element.
 * 
 */
public class IntegerTextBoxFormElement extends BasicTextBoxFormElement
{

    /**
     * Creates a basic text area form element.
     * 
     * @param size
     *            the size of the text box.
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
    public IntegerTextBoxFormElement(final int size, final String labelVal, final String inKey, final String value,
            final String inInstructions, final boolean required)
    {
        super(size, true, labelVal, inKey, value, inInstructions, required);
    }
    
    /**
     * Creates a basic text area form element.
     * 
     * @param inEmptyLabel
     *            the label on an empty text box.
     * @param size
     *            the size of the text box.
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
    public IntegerTextBoxFormElement(final String inEmptyLabel, final int size, final String labelVal, 
            final String inKey, final String value, final String inInstructions, final boolean required)
    {
        super(inEmptyLabel, size, true, labelVal, inKey, value, inInstructions, required);
    }

    /**
     * Gets the value of the text box.
     * 
     * @return the value.
     */
    @Override
    public Serializable getValue()
    {
        
        if (super.getValue().equals(""))
        {
            return null;
        }
        else
        {
            Serializable thisValue;
            
            try
            {
                thisValue = Integer.parseInt(super.getValue().toString());
            }
            catch (Exception e)
            {
                thisValue = null;
            }
            
            return thisValue;
        }
    }

}
