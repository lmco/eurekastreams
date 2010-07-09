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

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.action.CustomAction;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * ActionProcessor mock support class.
 *
 */
public final class ActionProcessorMockSupport
{
    /**
     * Hide the no-op constructor.
     */
    private ActionProcessorMockSupport()
    {
    }

    /**
     * Setup action service helper method.
     * @param inContext Mock context.
     * @param inPageServiceAsyncMock ActionRPCServiceAsync mock.
     * @return AsyncCallback.
     */
    @SuppressWarnings("unchecked")
    public static AsyncCallback<ActionRequest[]> setupActionService(
            final JUnit4Mockery inContext,
            final ActionRPCServiceAsync inPageServiceAsyncMock)
    {
        final AsyncCallbackProxy callbackProxy = new AsyncCallbackProxy();

        inContext.checking(new Expectations()
        {
            {
                /*
                 * setting the expectation that the getDomainObject() will be
                 * called once
                 */
                one(inPageServiceAsyncMock).execute(
                        with(any(ActionRequest[].class)),
                        with(any(AsyncCallback.class)));

                /*
                 * Set up the behavior of the getDomainObject method.
                 *
                 * When the mock's getDomainObject is called, it will not call
                 * the callback (like the app does), but instead will set the
                 * callback on the proxy, leaving it to the test class to
                 * control when the callback is called. It is set it up like
                 * this to imitate the asynchronous nature of rpc
                 */

                will(new CustomAction(
                        "Instead of call the success method on the callback, set the callback on the proxy")
                {
                    public Object invoke(final Invocation invocation)
                            throws Throwable
                    {
                        final Object[] arguments = invocation
                                .getParametersAsArray();
                        AsyncCallback<ActionRequest[]> callback =
                            (AsyncCallback<ActionRequest[]>) arguments[arguments.length - 1];
                        callbackProxy.setCallback(callback);
                        return null;
                    }
                });
            }
        });
        return callbackProxy;
    }

    /**
     * Support class. This proxy allows us to simulate the asynchronous
     * nature of the service call.
     */
    @SuppressWarnings("unchecked")
    private static class AsyncCallbackProxy implements
            AsyncCallback<ActionRequest[]>
    {
        /**
         * The wrapped callback object.
         */
        private AsyncCallback callback;

        /**
         * Wrapping event handler.
         *
         * @param caught
         *            the exception that triggered the failure
         */
        public void onFailure(final Throwable caught)
        {
            callback.onFailure(caught);
        }

        /**
         *
         * @param inCallback
         *            the callback to be wrapped
         */
        private void setCallback(final AsyncCallback inCallback)
        {
            this.callback = inCallback;
        }

        /**
         * Wrapping event handler.
         *
         * @param result
         *            the new Tab from the service call
         */

        public void onSuccess(final ActionRequest[] result)
        {
            callback.onSuccess(result);

        }
    }
}
