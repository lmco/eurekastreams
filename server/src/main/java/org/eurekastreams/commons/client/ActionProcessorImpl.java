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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The queuable action processor takes in requests to the action rpc service. It has the ability to queue up requests
 * and send them as one request instead of multiple. This is used on page init to save on HTTP transfers.
 * 
 */
public class ActionProcessorImpl implements ActionProcessor
{
    /**
     * The session id.
     */
    private static String sessionId = null;
    /**
     * The RPC service to call.
     */
    private ActionRPCServiceAsync service;

    /**
     * The boolean that controls whether the action processor queues or not.
     */
    private boolean queueRequests = false;

    /**
     * The queue of action requests.
     */
    @SuppressWarnings("unchecked")
    private List<ActionRequest> requests = new ArrayList<ActionRequest>();

    /**
     * The counter variable for the queue.
     */
    private Integer actionId = new Integer(0);

    /**
     * HashMap of ActionRequest objects.
     */
    @SuppressWarnings("unchecked")
    private HashMap<Integer, ActionRequest> requestDict = new HashMap<Integer, ActionRequest>();

    /**
     * Standard constructor. Takes in an Action RPC Service object.
     * 
     * @param inService
     *            the action rpc service to use.
     */
    public ActionProcessorImpl(final ActionRPCServiceAsync inService)
    {
        service = inService;
    }

    /**
     * Fires the action requests in the queue.
     */
    @SuppressWarnings("unchecked")
    public void fireQueuedRequests()
    {
        if (!requests.isEmpty())
        {
            final ActionRequest[] requestArr = (ActionRequest[]) (requests.toArray(new ActionRequest[requests.size()]));

            // empty the queue now that we've copied all the requests to the array
            requests.clear();

            service.execute(requestArr, new AsyncCallback<ActionRequest[]>()
            {
                /* implement the async call back methods */
                public void onFailure(final Throwable caught)
                {
                    for (ActionRequest response : requestArr)
                    {
                        response.executeCallbacks(caught);
                    }
                }

                public void onSuccess(final ActionRequest[] results)
                {
                    for (ActionRequest response : results)
                    {
                        ActionRequest request = requestDict.get(response.getId());
                        if (request != null)
                        {
                            request.executeCallbacks(response.getResponse());
                            requestDict.remove(response.getId());
                        }
                    }
                }
            });
        }
    }

    /**
     * Makes a request to the action processor. If the queue is on it only queue's the request, otherwise it fires it
     * off.
     * 
     * @param request
     *            The ActionRequest object.
     * @param callback
     *            The AsyncCallback object.
     */
    @SuppressWarnings("unchecked")
    public void makeRequest(final ActionRequest request, final AsyncCallback callback)
    {
        addToQueue(request, callback);
        if (!queueRequests)
        {
            fireQueuedRequests();
        }
    }

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
    @SuppressWarnings("unchecked")
    public void makeRequest(final String actionKey, final Serializable param, final AsyncCallback callback)
    {
        makeRequest(new ActionRequestImpl(actionKey, param), callback);
    }

    /**
     * Turns the queue on or off.
     * 
     * @param queue
     *            The value for enabling or disabling queue.
     */
    public void setQueueRequests(final boolean queue)
    {
        queueRequests = queue;
    }

    /**
     * Adds an action the queue. Equivalent to makeRequest(...) if the queue is turned on.
     * 
     * @param request
     *            The ActionRequest object.
     * @param callback
     *            The AsyncCallback object.
     */
    @SuppressWarnings("unchecked")
    private void addToQueue(final ActionRequest request, final AsyncCallback callback)
    {
        request.setSessionId(sessionId);
        request.addCallback(callback);
        request.setId(actionId);
        requestDict.put(actionId, request);
        requests.add(request);
        actionId++;
    }

    /**
     * This is just here for tests.
     * 
     * @param inSessionId
     *            the session id.
     */
    public void setSessionId(final String inSessionId)
    {
        sessionId = inSessionId;
    }

    /**
     * This is just here for tests.
     * 
     * @param inSessionId
     *            the session id.
     */
    public static void setCurrentSessionId(final String inSessionId)
    {
        sessionId = inSessionId;
    }
}
