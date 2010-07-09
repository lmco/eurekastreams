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
package org.eurekastreams.web.client.ui.common;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.OListElement;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Ordered list.
 */
public class OLPanel extends ComplexPanel
{
    /**
     * List Element.
     */
    private OListElement list;

    /**
     * Sole constructor.
     */
    public OLPanel()
    {
        list = Document.get().createOLElement();
        setElement(list);
    }

    /**
     * Adds a List Item (LI) element to the unordered list panel.
     * 
     * @param child
     *            the widget to add to the LI tag
     */
    @Override
    public void add(final Widget child)
    {
        Element li = Document.get().createLIElement().cast();
        list.appendChild(li);
        super.add(child, li);
    }

    /**
     * Get the list.
     * 
     * @return the list
     */
    protected OListElement getList()
    {
        return list;
    }
}
