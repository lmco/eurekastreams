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
package org.eurekastreams.commons.client;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Handles requests made to actions on the server. Is able to queue requests into one giant request.
 */
public interface ActionProcessor
{
    /**
     * Sets whether the action processor is queued or not. Should only be queued during a single transaction, such as
     * initing the page or deleting something.
     *
     * @param queue
     *            if set to true, queue up the action processor until FireQueuedRequests is called
     */
    void setQueueRequests(boolean queue);

    /**
     * Makes a request to the action rpc service. If the action processor is queueable then it adds it to the queue and
     * waits until the FireQueuedRequests method is called. DEPRECATED: we don't want your request objects - we're using
     * our own to insure we are in control!
     *
     * @param request
     *            the ActionRequest to send to the server
     * @param callback
     *            the AsyncCallback to call after the request is handled. Please provide an OnFailure and an OnSuccess
     */
    @SuppressWarnings("rawtypes")
    @Deprecated
    void makeRequest(ActionRequest request, AsyncCallback callback);

    /**
     * Makes a request to the action rpc service. If the action processor is queueable then it adds it to the queue and
     * waits until the FireQueuedRequests method is called
     *
     * @param actionKey
     *            - identify the action to load.
     * @param param
     *            - parameter to pass to the action during execution.
     * @param callback
     *            the AsyncCallback to call after the request is handled. Please provide an OnFailure and an OnSuccess
     */
    @SuppressWarnings("rawtypes")
    void makeRequest(final String actionKey, final Serializable param, AsyncCallback callback);

    /**
     * Fires all the queue'd requests in the action processors queue. It turns them into one monolithic request to cut
     * down HTTP requests made to the server.
     */
    void fireQueuedRequests();
}
