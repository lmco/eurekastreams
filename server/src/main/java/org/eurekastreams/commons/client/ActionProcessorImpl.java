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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eurekastreams.commons.exceptions.SessionException;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The queuable action processor takes in requests to the action rpc service. It has the ability to queue up requests
 * and send them as one request instead of multiple. This is used on page init to save on HTTP transfers.
 */
@SuppressWarnings("rawtypes")
public class ActionProcessorImpl implements ActionProcessor
{
    /** Log. */
    Logger log = Logger.getLogger("ActionProcessorImpl");

    /** The RPC service to call. */
    private final ActionRPCServiceAsync service;

    /** For generating request IDs. */
    private int lastRequestId = 0;

    /** Ordered index of all requests. */
    private final Map<Integer, RequestInfo> requestIndex = new LinkedHashMap<Integer, RequestInfo>();

    /** The queue of action requests. */
    private final List<RequestInfo> requestQueue = new ArrayList<RequestInfo>();

    /** Current session id. */
    private String sessionId = null;

    /** Current number of outstanding message bundles. */
    private int outstandingMessages = 0;

    /** If normal processing is suspended to establish a session with the server. */
    private boolean establishingSession = false;

    /** If requests should be held in the queue (to allow batching). */
    private boolean holdQueue = false;

    /** Callback to notify app of session establish success/failure. */
    private final AsyncCallback<String> appEstablishSessionCallback;

    /**
     * Standard constructor. Takes in an Action RPC Service object.
     *
     * @param inService
     *            the action rpc service to use.
     * @param inAppEstablishSessionCallback
     *            Callback to notify app of session establish success/failure.
     */
    public ActionProcessorImpl(final ActionRPCServiceAsync inService,
            final AsyncCallback<String> inAppEstablishSessionCallback)
    {
        service = inService;
        appEstablishSessionCallback = inAppEstablishSessionCallback;

        log.setLevel(Level.FINEST);
    }

    /**
     * Handling for success and failure of establishing a session.
     */
    private final AsyncCallback<String> establishSessionCallback = new AsyncCallback<String>()
    {
        public void onSuccess(final String inResult)
        {
            sessionId = inResult;
            establishingSession = false;

            log.info("Session established:  " + sessionId);

            // let app know if it's interested
            if (appEstablishSessionCallback != null)
            {
                try
                {
                    appEstablishSessionCallback.onSuccess(inResult);
                }
                catch (Exception ex)
                {
                    @SuppressWarnings("unused")
                    int swallowExceptionsFromIllBehavedAppCode = 1;
                }
            }

            // session is now established, so send any requests
            sendRequests(requestIndex.values());
        }

        public void onFailure(final Throwable inCaught)
        {
            establishingSession = false;

            log.info("Error establishing session: " + inCaught);

            // Let app know about failure so it can try to do something about it
            if (appEstablishSessionCallback != null)
            {
                try
                {
                    appEstablishSessionCallback.onFailure(inCaught);
                }
                catch (Exception ex)
                {
                    @SuppressWarnings("unused")
                    int swallowExceptionsFromIllBehavedAppCode = 1;
                }
            }
        }
    };

    /**
     * Initiates establishing of a session.
     */
    private void establishSession()
    {
        log.fine("Entering establishSession");

        // wait until all request batches have returned, and don't send out multiple session requests
        if (outstandingMessages > 0 || establishingSession)
        {
            return;
        }

        log.info("About to establish session");

        // discard the queue, since we will re-request using the index after the session is established
        requestQueue.clear();

        // the outstanding message count SHOULD already be zero, but if it's not (namely if it went negative somehow),
        // we'd like to not have this whole thing hang
        outstandingMessages = 0;

        establishingSession = true;

        try
        {
            service.establishSession(establishSessionCallback);
        }
        catch (Exception ex)
        {
            establishSessionCallback.onFailure(ex);
        }
    }

    /**
     * Attempts to send any queued actions.
     */
    public void fireQueuedRequests()
    {
        log.fine("Entering fireQueuedRequests");

        // establish session instead
        if (sessionId == null)
        {
            establishSession();
            return;
        }

        // send the messages
        sendRequests(requestQueue);
    }

    /**
     * Builds and sends a batch of requests to the server.
     *
     * @param requestSource
     *            Where to get the list of requests from.
     */
    private void sendRequests(final Collection<RequestInfo> requestSource)
    {
        log.fine("Entering sendRequests");

        if (requestSource.isEmpty())
        {
            return;
        }

        log.info("sending " + requestSource.size() + " requests.");

        // prepare list to send
        // Note: we really only need one session id per batch (since it's one HTTP request), so we could set it only
        // on the first request batch and then only check it on the first request of the batch in ActionRPCServiceImpl
        final ActionRequest[] requests = new ActionRequest[requestSource.size()];
        int i = 0;
        for (RequestInfo info : requestSource)
        {
            requests[i] = info.getRequest();
            requests[i].setSessionId(sessionId);
            i++;
        }


// final ActionRequest[] requests = (requestSource.toArray(new ActionRequest[requestSource.size()]));
        //
        //
        // final ActionProcessorRequestImpl[] requests = (requestSource
        // .toArray(new ActionProcessorRequestImpl[requestSource.size()]));
        // // Note: we really only need one session id per batch (since it's one HTTP request), so we could set it only
        // on
        // // the first request batch and then only check it on the first request of the batch in ActionRPCServiceImpl
        // for (ActionRequest request : requests)
        // {
        // request.setSessionId(sessionId);
        // }
        // ^#$(@^#@($));


        // clear the queue
        // requestSource may not be the queue - it may be the index - but we know that either way, all messages that
        // were in the queue will have been sent, since everything that is in the queue is also in the index.
        requestQueue.clear();

        try
        {
            outstandingMessages++;
            service.execute(requests, new AsyncCallback<ActionRequest[]>()
            {
                public void onSuccess(final ActionRequest[] inResult)
                {
                    outstandingMessages--;
                    onSendBatchSuccess(inResult);
                }

                public void onFailure(final Throwable inCaught)
                {
                    outstandingMessages--;
                    onSendBatchFailure(requests, inCaught);
                }
            });
        }
        catch (Exception ex)
        {
            outstandingMessages--;
            onSendBatchFailure(requests, ex);
        }
    }

    /**
     * Handles a "successful" batch of requests - namely the HTTP interaction worked. There may be a session exception
     * (stored in each response) or individual responses may have failed.
     *
     * @param responses
     *            The responses from the server.
     */
    @SuppressWarnings("unchecked")
    private void onSendBatchSuccess(final ActionRequest[] responses)
    {
        log.info("Batch success with " + responses.length + " responses.");

        for (ActionRequest response : responses)
        {
            // ideally, we'd have a class representing the entire batch and batch-level exceptions (like the session
            // exception) would come back in there, but since we don't, we have to check on individual requests
            if (response.getResponse() instanceof SessionException)
            {
                log.info("Found a SessionException in the response batch.");

                sessionId = null;
            }
            else
            {
                RequestInfo info = requestIndex.remove(response.getId());

                log.finer("Done with request " + response.getId() + " ("
                        + (info == null ? "<NOT FOUND>" : info.getRequest().getActionKey()) + ") with result "
                        + response.getResponse());

                if (info != null && info.getCallback() != null)
                {
                    try
                    {
                        if (response.getResponse() instanceof Throwable)
                        {
                            info.getCallback().onFailure((Throwable) response.getResponse());
                        }
                        else
                        {
                            info.getCallback().onSuccess(response.getResponse());
                        }
                    }
                    catch (Throwable ex)
                    {
                        // swallow exceptions from callbacks - a fault in a callback shouldn't break everything else
                        @SuppressWarnings("unused")
                        int doNothing = 1;
                    }
                }
            }
        }

        // could have discovered above that the session was lost - re-establish it (when the last outstanding message
        // comes in, but establishSession will check that for us)
        if (sessionId == null)
        {
            establishSession();
        }
    }

    /**
     * Handles failure of a batch of requests - this may be sync (failed to get it "out the door" such as serialization
     * failure) or async (error from the HTTP interaction).
     *
     * @param requests
     *            The batch of requests that was being sent.
     * @param caught
     *            The exception.
     */
    private void onSendBatchFailure(final ActionRequest[] requests, final Throwable caught)
    {
        log.info("Batch failure with exception " + caught);

        // handle batch-level exceptions (of which session failure is the only one we're dealing with)
        if (caught instanceof SessionException)
        {
            sessionId = null;
            // note: establishSession will insure there are no outstanding messages
            establishSession();
        }
        else
        {
            // any exception other than a session exception gets distributed to each request
            for (ActionRequest request : requests)
            {
                RequestInfo info = requestIndex.remove(request.getId());
                if (info != null && info.getCallback() != null)
                {
                    try
                    {
                        info.getCallback().onFailure(caught);
                    }
                    catch (Throwable ex)
                    {
                        // swallow exceptions from callbacks - a fault in a callback shouldn't break everything else
                        @SuppressWarnings("unused")
                        int doNothing = 1;
                    }
                }
            }
        }
    }

    /**
     * Makes a request to the action processor. If the queue is on it only queue's the request, otherwise it fires it
     * off. DEPRECATED: we don't want your request objects - we're using our own to insure we are in control!
     *
     * @param request
     *            The ActionRequest object.
     * @param callback
     *            The AsyncCallback object.
     */
    @Deprecated
    public void makeRequest(final ActionRequest request, final AsyncCallback callback)
    {
        makeRequest(request.getActionKey(), request.getParam(), callback);
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
    public void makeRequest(final String actionKey, final Serializable param, final AsyncCallback callback)
    {
        log.finer("Make request for " + actionKey);

        int id = ++lastRequestId;
        RequestInfo info = new RequestInfo(id, actionKey, param, callback);
        requestIndex.put(id, info);
        requestQueue.add(info);

        if (!holdQueue)
        {
            fireQueuedRequests();
        }
    }

    /**
     * Turns the queue on or off.
     *
     * @param queue
     *            The value for enabling or disabling queue.
     */
    public void setQueueRequests(final boolean queue)
    {
        holdQueue = queue;

        // send any queued requests when turning off queuing (since callers are likely to forget)
        if (!holdQueue)
        {
            fireQueuedRequests();
        }
    }

    /* -------- Nested implementation classes -------- */

    /**
     * Holds information about a request.
     */
    private static class RequestInfo
    {
        /** The request to send. */
        private final ActionRequest< ? extends Serializable> request;

        /** The requestor's callback. */
        private final AsyncCallback callback;

        /**
         * Constructor.
         *
         * @param inId
         *            ID of the request.
         * @param inActionKey
         *            Name of the action to perform.
         * @param inParam
         *            Parameter to be passed to the ServiceAction.
         * @param inCallback
         *            Requestor's callback.
         */
        @SuppressWarnings("deprecation")
        public RequestInfo(final int inId, final String inActionKey, final Serializable inParam,
                final AsyncCallback inCallback)
        {
            // request = new ServerRequest(inId, inActionKey, inParam);
            request = new ActionRequestImpl<Serializable>(inActionKey, inParam);
            request.setId(inId);
            callback = inCallback;
        }

        /**
         * @return the request
         */
        public ActionRequest< ? extends Serializable> getRequest()
        {
            return request;
        }

        /**
         * @return the callback
         */
        public AsyncCallback getCallback()
        {
            return callback;
        }
    }

    // /**
    // * The request to send to the server. Hide this implementation from the outside world, since it's none of their
    // * business, other than for the GWT compiler and serializer.
    // */
    // static class ServerRequest implements Serializable, ActionRequest<Serializable>
    // {
    // /** Name of the action to perform. */
    // private String actionKey;
    //
    // /** ID of the request; unique to the ActionProcessor from which it came. */
    // private int id;
    //
    // /** Session id. */
    // private String sessionId;
    //
    // /** Parameter to be passed to the ServiceAction. */
    // private Serializable param;
    //
    // /** The response generated by the ServerAction. */
    // private Serializable response;
    //
    // /**
    // * Constructor.
    // */
    // public ServerRequest()
    // {
    // }
    //
    // /**
    // * Constructor.
    // *
    // * @param inId
    // * ID of the request.
    // * @param inActionKey
    // * Name of the action to perform.
    // * @param inParam
    // * Parameter to be passed to the ServiceAction.
    // */
    // public ServerRequest(final int inId, final String inActionKey, final Serializable inParam)
    // {
    // id = inId;
    // actionKey = inActionKey;
    // param = inParam;
    // }
    //
    // /**
    // * @return the actionKey
    // */
    // @Override
    // public String getActionKey()
    // {
    // return actionKey;
    // }
    //
    // /**
    // * @param inActionKey
    // * the actionKey to set
    // */
    // @Override
    // public void setActionKey(final String inActionKey)
    // {
    // actionKey = inActionKey;
    // }
    //
    // /**
    // * @return the id
    // */
    // @Override
    // public int getId()
    // {
    // return id;
    // }
    //
    // /**
    // * @param inId
    // * the id to set
    // */
    // @Override
    // public void setId(final int inId)
    // {
    // id = inId;
    // }
    //
    // /**
    // * @return the sessionId
    // */
    // @Override
    // public String getSessionId()
    // {
    // return sessionId;
    // }
    //
    // /**
    // * @param inSessionId
    // * the sessionId to set
    // */
    // @Override
    // public void setSessionId(final String inSessionId)
    // {
    // sessionId = inSessionId;
    // }
    //
    // /**
    // * @return the param
    // */
    // @Override
    // public Serializable getParam()
    // {
    // return param;
    // }
    //
    // /**
    // * @param inParam
    // * the param to set
    // */
    // @Override
    // public void setParam(final Serializable inParam)
    // {
    // param = inParam;
    // }
    //
    // /**
    // * @return the response
    // */
    // @Override
    // public Serializable getResponse()
    // {
    // return response;
    // }
    //
    // /**
    // * @param inResponse
    // * the response to set
    // */
    // @Override
    // public void setResponse(final Serializable inResponse)
    // {
    // response = inResponse;
    // }
    // }
}
