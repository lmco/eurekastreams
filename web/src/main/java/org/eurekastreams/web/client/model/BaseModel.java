/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.model;

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.commons.client.ActionRequest;
import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.exceptions.GeneralException;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.data.ValidationExceptionResponseEvent;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.notifier.Notification;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Base model. Gets you free CRUD.
 *
 */
public abstract class BaseModel
{
    /**
     * Cached requests objects, by action key.
     */
    private HashMap<String, Serializable> cachedRequests = new HashMap<String, Serializable>();
    /**
     * Cached response objects, by action key.
     */
    private HashMap<String, Serializable> cachedData = new HashMap<String, Serializable>();

    /**
     * onSuccess command.
     *
     * @param <T>
     *            response type.
     */
    public interface OnSuccessCommand<T extends Serializable>
    {
        /**
         * onSuccess.
         *
         * @param response
         *            response.
         */
        void onSuccess(T response);
    }

    /**
     * Call read action.
     *
     * @param actionKey
     *            the action.
     * @param request
     *            the request.
     * @param successCommand
     *            the success command.
     * @param useClientCacheIfAvailable
     *            use the cache.
     */
    protected void callReadAction(final String actionKey, final Serializable request,
            final OnSuccessCommand successCommand, final boolean useClientCacheIfAvailable)
    {
        if (cachedData.get(actionKey) != null
                && useClientCacheIfAvailable
                && ((request == null && cachedRequests.get(actionKey) == null) || (areRequestsEqual(cachedRequests
                        .get(actionKey), request))))
        {
            successCommand.onSuccess(cachedData.get(actionKey));
        }
        else
        {
            cachedRequests.put(actionKey, request);
            callAction(actionKey, request, successCommand, useClientCacheIfAvailable);
        }
    }

    /**
     * Call write action.
     *
     * @param actionKey
     *            the action key.
     * @param request
     *            the request.
     * @param successCommand
     *            successcommand.
     */
    protected void callWriteAction(final String actionKey, final Serializable request,
            final OnSuccessCommand successCommand)
    {
        cachedData.clear();
        callAction(actionKey, request, successCommand, false);
    }

    /**
     * Call an action.
     *
     * @param actionKey
     *            action key.
     * @param request
     *            request.
     * @param successCommand
     *            on successs command.
     * @param useClientCacheIfAvailable
     *            use the cache.
     */
    private void callAction(final String actionKey, final Serializable request, final OnSuccessCommand successCommand,
            final boolean useClientCacheIfAvailable)
    {
        ActionRequest<Serializable> serverRqst = new ActionRequestImpl<Serializable>(actionKey, request);
        Session.getInstance().getActionProcessor().makeRequest(serverRqst, new AsyncCallback<Serializable>()
        {
            public void onFailure(final Throwable caught)
            {
                cachedRequests.remove(actionKey);
                cachedData.remove(actionKey);

                if (caught instanceof ValidationException)
                {
                    Session.getInstance().getEventBus().notifyObservers(
                            new ValidationExceptionResponseEvent((ValidationException) caught));
                }
                else if (caught instanceof ExecutionException)
                {
                    Session.getInstance().getEventBus().notifyObservers(
                            new ShowNotificationEvent(new Notification(
                                    "Error occurred, please refresh and try again.")));
                }
                else if (caught instanceof GeneralException)
                {
                    Session.getInstance().getEventBus()
                            .notifyObservers(
                                    new ShowNotificationEvent(new Notification(
                                            "Error occurred, please refresh and try again.")));
                }

            }

            public void onSuccess(final Serializable result)
            {
                cachedData.put(actionKey, result);

                if (successCommand != null)
                {
                    successCommand.onSuccess(result);
                }
            }
        });

    }

    /**
     * Calls the native comparer.
     *
     * @param request1
     *            the request.
     * @param request2
     *            the request.
     * @return whether this request is equal to the last fetch request.
     */
    private boolean areRequestsEqual(final Serializable request1, final Serializable request2)
    {
        return nativeCompareRequest(request1, request2);
    }

    /**
     * Clear cache for this model.
     */
    public void clearCache()
    {
        cachedData.clear();
    }

    /**
     * Native comparer of objects. Dropping down to javascript here because I don't have reflection available.
     *
     * @param request1
     *            first request.
     * @param request2
     *            second request.
     * @return isEqual.
     */
    public static native boolean nativeCompareRequest(final Serializable request1, final Serializable request2)
    /*-{

        for(var p in request2)
        {
            if (request1[p] != request2[p])
            {
                return false;
            }
        }

        return true;
    }-*/;

}
