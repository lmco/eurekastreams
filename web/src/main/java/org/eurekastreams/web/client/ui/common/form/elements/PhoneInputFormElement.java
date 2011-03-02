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

import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

/**
 * Extends a sub text box form element (this is kind of a hack, because it should be able to do either sub or regular.
 * Right now all our phone inputs are basic text box, so agiley, I did it this way. Refactor when the need arises to
 * have non sub phone elements.
 * 
 */

// TODO cstephe-I suggest we get rid of sub test box form element.
// We should just be able to pass in a class if not class is passed in it uses the form-element class
public class PhoneInputFormElement extends BasicTextBoxFormElement implements FormElement
{
    /**
     * The length of the phone number if there happens to be a leading 1.
     */
    private static final int LENGTH_OF_PHONE_WITH_LEADING_ONE = 11;

    /**
     * Creates a text box input that correctly formats a phone input.
     * 
     * @param labelVal
     *            the label.
     * @param inKey
     *            the key on the model.
     * @param value
     *            the value to default to.
     * @param inInstructions
     *            the instructions for the element.
     * @param required
     *            whether or not this is required.
     */
    public PhoneInputFormElement(final String labelVal, final String inKey, final String value,
            final String inInstructions, final boolean required)
    {
        super(labelVal, inKey, formatPhoneNumber(value), inInstructions, required);
    }

    /**
     * Format the phone number by injecting dashes between the numbers (e.g. 6105550424 becomes 610-555-0424).
     * 
     * @param inPhone
     *            the unformated phone number.
     * @return the formated phone number.
     */
    private static String formatPhoneNumber(final String inPhone)
    {
        if (inPhone == null)
        {
            return "";
        }
        String phoneNumber = inPhone;
        phoneNumber = phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6);
        return phoneNumber;
    }

    /**
     * Before we give back the value from what the user typed in, strip out any character thats not numeric. Also, if
     * the leading number is a 1, strip that out as well. So 1 (610) 555-0424 becomes 6105550424.
     * 
     * @return the formatted value.
     */
    @Override
    public String getValue()
    {
        String value = super.getValue().toString().replaceAll("[^0-9]", "");
        if (value.length() == LENGTH_OF_PHONE_WITH_LEADING_ONE && value.charAt(0) == '1')
        {
            value = value.substring(1);
        }
        return value;
    }
}
