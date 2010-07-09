/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.web.client;

import java.io.Serializable;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.action.CustomAction;
import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionRequest;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Provide support methods for the service tests.
 */
public final class CommandActionProcessorMockSupport
{
    /**
     * Hiding the default constructor.
     */
    private CommandActionProcessorMockSupport()
    {

    }

    /**
     * Calls onSuccess() on the AsyncCallback passed in with mocked RPC call, i.e. the RPC call that was recorded on the
     * mock immediately before this method was called.
     * 
     * @param <T>
     *            the return type of the callback.
     * 
     * @param inContext
     *            the mockery context.
     * @param inActionProcessorMock
     *            the mocked action processor.
     * @param inExecuteSequence
     *            the exection sequence.
     * 
     * @return a callback object set up with expectations.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> AsyncCallbackProxy<T> setupActionProcessor(final JUnit4Mockery inContext,
            final ActionProcessor inActionProcessorMock, final Sequence inExecuteSequence)
    {
        final AsyncCallbackProxy callbackProxy = new AsyncCallbackProxy();

        inContext.checking(new Expectations()
        {
            {
                /*
                 * setting the expectation that the getDomainObject() will be called once
                 */
                one(inActionProcessorMock).makeRequest(with(any(ActionRequest.class)), with(any(AsyncCallback.class)));
                inSequence(inExecuteSequence);

                /*
                 * Set up the behavior of the getDomainObject method.
                 * 
                 * When the mock's getDomainObject is called, it will not call the callback (like the app does), but
                 * instead will set the callback on the proxy, leaving it to the test class to control when the callback
                 * is called. It is set it up like this to imitate the asynchronous nature of rpc
                 */

                will(new CustomAction(
                        "Instead of call the success method on the callback, set the callback on the proxy")
                {
                    public Object invoke(final Invocation invocation) throws Throwable
                    {
                        final Object[] arguments = invocation.getParametersAsArray();
                        ActionRequest<T> request = (ActionRequest<T>) arguments[0];
                        callbackProxy.setRequest(request);
                        AsyncCallback<T> callback = (AsyncCallback<T>) arguments[arguments.length - 1];
                        callbackProxy.setCallback(callback);

                        return null;
                    }
                });
            }
        });
        return callbackProxy;
    }

    /**
     * Support class. This proxy allows us to simulate the asynchronous nature of the service call.
     * 
     * @param <T>
     * 
     */
    public static class AsyncCallbackProxy<T extends Serializable> implements AsyncCallback<T>
    {
        /**
         * The wrapped callback object.
         */
        private AsyncCallback<T> callback;

        /**
         * The request associated with the callback.
         */
        private ActionRequest<T> request;
        
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
         * Wrapping event handler.
         * 
         * @param result
         *            the new Tab from the service call
         */
        public void onSuccess(final T result)
        {
            callback.onSuccess(result);
        }

        /**
         * 
         * @param inCallback
         *            the callback to be wrapped
         */
        private void setCallback(final AsyncCallback<T> inCallback)
        {
            this.callback = inCallback;
        }

        /**
         * Setter for request.
         * 
         * @param inRequest
         *            the request
         */
        public void setRequest(final ActionRequest<T> inRequest)
        {
            request = inRequest;
        }

        /**
         * Getter.
         * 
         * @return the request
         */
        public ActionRequest<T> getRequest()
        {
            return request;
        }
    }
}
