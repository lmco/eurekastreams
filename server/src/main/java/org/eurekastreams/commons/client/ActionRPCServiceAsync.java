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
package org.eurekastreams.commons.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Provides one endpoint for all service requests.
 */
public interface ActionRPCServiceAsync
{
    /**
     * Make the call to the ServerAction.
     *
     * @param request
     *            the request to be executed
     * @param callback
     *            the response from the server, including the original request.
     */
    @SuppressWarnings("unchecked")
    void execute(ActionRequest request,
            AsyncCallback<ActionRequest> callback);

    /**
     * Make calls to multiple ServerActions.
     *
     * @param requests
     *            the requests to be executed
     * @param callback
     *            the response from the server.
     */
    @SuppressWarnings("unchecked")
    void execute(ActionRequest[] requests,
            AsyncCallback<ActionRequest[]> callback);
}
