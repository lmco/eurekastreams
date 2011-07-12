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

            log.fine("Session established:  " + sessionId);

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

            log.fine("Error establishing session: " + inCaught);

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
        log.finest("Entering establishSession");

        // wait until all request batches have returned, and don't send out multiple session requests
        if (outstandingMessages > 0 || establishingSession)
        {
            return;
        }

        log.fine("About to establish session");

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
        log.finest("Entering fireQueuedRequests");

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
        log.finest("Entering sendRequests");

        if (requestSource.isEmpty())
        {
            return;
        }

        log.fine("sending " + requestSource.size() + " requests.");

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

        // clear the queue
        // requestSource may not be the queue - it may be the index - but we know that either way, all messages that
        // were in the queue will have been sent, since everything that is in the queue is also in the index.
        requestQueue.clear();

        // send the message and handle results
        // Note: The onFailure method of the callback can be called inline by service.execute on certain errors. If that
        // occurred AND onSendBatchFailure also threw an exception AND since service.execute doesn't catch the
        // exceptions from callbacks (at least in GWT 2.2 with a draft-mode compile - I checked the generated
        // JavaScript), then outstandingMessages would get decremented twice and onSendBatchFailure would be called
        // twice; this is being prevented by the try/catch block around the call to onSendBatchFailure.
        // onSendBatchSuccess is also protected just for good measure.
        try
        {
            outstandingMessages++;
            service.execute(requests, new AsyncCallback<ActionRequest[]>()
            {
                public void onSuccess(final ActionRequest[] inResult)
                {
                    outstandingMessages--;
                    try
                    {
                        onSendBatchSuccess(inResult);
                    }
                    catch (Exception ex)
                    {
                        log.log(Level.WARNING, "Unhandled exception in onSendBatchSuccess", ex);
                    }
                }

                public void onFailure(final Throwable inCaught)
                {
                    outstandingMessages--;
                    try
                    {
                        onSendBatchFailure(requests, inCaught);
                    }
                    catch (Exception ex)
                    {
                        log.log(Level.WARNING, "Unhandled exception in onSendBatchFailure", ex);
                    }
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
        log.fine("Batch success with " + responses.length + " responses.");

        for (ActionRequest response : responses)
        {
            // ideally, we'd have a class representing the entire batch and batch-level exceptions (like the session
            // exception) would come back in there, but since we don't, we have to check on individual requests
            if (response.getResponse() instanceof SessionException)
            {
                log.fine("Found a SessionException in the response batch.");

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
        log.fine("Batch failure with exception " + caught);

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
            // TODO: Come up with a better idea than this. Most batch-level exceptions are communications exceptions
            // which really should be handled at the comm level, not at the individual action level. Plus most action
            // requesters don't handle failure cases anyway, so an error causes the app to just sit there, usually with
            // an eternal spinner. Would be better to have this handled in a single place in a consistent way.
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
}
