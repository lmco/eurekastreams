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
package org.eurekastreams.web.client.ui.common.stream.filters.list;

import java.io.Serializable;
import java.util.LinkedList;

import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.web.client.ui.common.form.elements.FormElement;
import org.eurekastreams.web.client.ui.common.form.elements.StreamScopeFormElement;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * Form element for picking a list in a saved search.
 *
 */
public class StreamListFormElement extends FlowPanel implements FormElement
{
    /**
     * The label.
     */
    private Label label = new Label("Stream");
    /**
     * Everyone button.
     */
    private RadioButton everyone = new RadioButton("list", "Everyone");
    /**
     * Parent org button.
     */
    private RadioButton parentOrg = new RadioButton("stuff");
    /**
     * Following button.
     */
    private RadioButton following = new RadioButton("list", "Following");
    /**
     * My lists button.
     */
    private RadioButton myLists = new RadioButton("list");

    /**
     * My saved items.
     */
    private RadioButton starred = new RadioButton("list", "Saved");



    /**
     * Maximum name length.
     */
    private static final int MAX_NAME = 50;


    /**
     * Default constructor.
     *
     * @param jsonRequest
     *            the id of the default view.
     */
    public StreamListFormElement(final String jsonRequest)
    {
        this.addStyleName("stream-lists");
        label.addStyleName("form-label");
        myLists.addStyleName("my-lists");
        boolean isChecked = false;




        this.add(label);

        this.add(following);
        this.add(parentOrg);
        this.add(everyone);
        this.add(starred);

        this.add(myLists);

        this.add(new StreamScopeFormElement("scopes", new LinkedList<StreamScope>(), "",
                "Enter the name of an employee or group stream.", false, true, "/resources/autocomplete/entities/",
                MAX_NAME));

        if (jsonRequest == null)
        {
            everyone.setChecked(true);
        }

    }

    /**
     * Gets the key.
     *
     * @return the key.
     */
    public String getKey()
    {
        return "streamRequest";
    }



    /**
     * Gets the value.
     *
     * @return the value.
     */
    public Serializable getValue()
    {
        return null;

    }

    /**
     * Gets called if this element has an error.
     *
     * @param errMessage
     *            the error Message.
     */
    public void onError(final String errMessage)
    {
        label.addStyleName("form-error");
    }

    /**
     * Gets called if this element was successful.
     */
    public void onSuccess()
    {
        label.removeStyleName("form-error");

    }
}
