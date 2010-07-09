/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Subclass the regular text input and give it a different style. Probably can
 * be done cleaner, but this works for now.
 * 
 */
public class SubTextBoxFormElement extends BasicTextBoxFormElement implements
        FormElement
{
    /**
     * Creates a regular text input box but styled differently, for the purposes
     * of indenting.
     * 
     * @param labelVal
     *            the label.
     * @param inKey
     *            the key.
     * @param value
     *            the value.
     * @param inInstructions
     *            the instructions.
     * @param required
     *            whether or not its required.
     */
    //TODO we should get rid of this class and just have a second constructor 
    //on basic that accepts a class name
    public SubTextBoxFormElement(final String labelVal, final String inKey,
            final String value, final String inInstructions,
            final boolean required)
    {
        super(labelVal, inKey, value, inInstructions, required);
        this.addStyleName("form-sub-element");
        FlowPanel clear = new FlowPanel();
        clear.addStyleName("clear");
        this.add(clear);
    }

}
