/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.pages.master;

import java.io.Serializable;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionProcessorImpl;
import org.eurekastreams.commons.client.ActionRPCService;
import org.eurekastreams.commons.client.ActionRPCServiceAsync;
import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.message.MessageDialogContent;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ConnectEntryPoint implements EntryPoint
{
    /**
     * The action processor.
     */
    private final ActionProcessor processor = new ActionProcessorImpl((ActionRPCServiceAsync) GWT
            .create(ActionRPCService.class));

    /**
     * Module load.
     */
    public void onModuleLoad()
    {
        processor.makeRequest(new ActionRequestImpl<PersonModelView>("noOperation", null),
                new AsyncCallback<Serializable>()
                {
                    public void onFailure(final Throwable caught)
                    {
                        Dialog
                                .showDialog(new MessageDialogContent("Unable to Establish Connection",
                                        "Please Refresh."));
                    }

                    public void onSuccess(final Serializable sessionId)
                    {

                        ActionProcessorImpl.setCurrentSessionId((String) sessionId);
                    }
                });
    }
}
