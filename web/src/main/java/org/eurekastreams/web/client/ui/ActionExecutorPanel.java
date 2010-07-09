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
package org.eurekastreams.web.client.ui;

import java.io.Serializable;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionRequestImpl;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Action executor to execute an action by name - currently without any parameters.
 */
public class ActionExecutorPanel extends Composite
{
    /**
     * Execute the action with the input name, using the input action processor, using a popup notice to report the
     * status of the action.
     * 
     * @param actionProcessor
     *            the action processor
     * @param actionName
     *            the name of the action to execute
     */
    public ActionExecutorPanel(final ActionProcessor actionProcessor, final String actionName)
    {
        FlowPanel panel = new FlowPanel();
        initWidget(panel);

        actionProcessor.makeRequest(new ActionRequestImpl<Serializable>(actionName, null),
                new AsyncCallback<Serializable>()
                {
                    /* implement the async call back methods */
                    public void onFailure(final Throwable caught)
                    {
                        Window.alert("Fail");
                    }

                    public void onSuccess(final Serializable result)
                    {
                        Window.alert("Success");
                    }
                });
    }
}
