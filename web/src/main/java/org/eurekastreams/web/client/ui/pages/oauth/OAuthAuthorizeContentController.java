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
package org.eurekastreams.web.client.ui.pages.oauth;

import java.io.Serializable;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionRequestImpl;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Controller class for the OAuth authorize page.
 */
public class OAuthAuthorizeContentController
{
    /**
     * The action processor.
     */
    private ActionProcessor processor;

    /**
     * Constructor.
     * 
     * @param inProcessor
     *            the action processor.
     */
    public OAuthAuthorizeContentController(final ActionProcessor inProcessor)
    {
        processor = inProcessor;
    }

    /**
     * The init.
     */
    public void init()
    {
        processor.setQueueRequests(true);

        /*
         * Serializable[] params = { null, TabGroupType.START }; this.processor.makeRequest(new
         * ActionRequestImpl<Person>("getPerson", params), new AsyncCallback<Person>()
         * 
         * { public void onFailure(final Throwable caught) { }
         * 
         * public void onSuccess(final Person result) { result.getOpenSocialId(); result.getAccountId(); } });
         */

        processor.makeRequest(new ActionRequestImpl<Serializable>("oauthAuthorize", Window.Location
                .getParameter("oauth_token")), new AsyncCallback<Serializable>()
        {
            /* implement the async call back methods */
            public void onFailure(final Throwable caught)
            {
                Window.alert("Fail");
            }

            public void onSuccess(final Serializable result)
            {
                String callbackUrl = (String) result;
                if (callbackUrl.length() > 0)
                {
                    redirectToUrl(callbackUrl);
                }
            }
        });

    }

    /**
     * Method to redirect the user when Authorization is complete.
     * 
     * @param url
     *            - url to redirect to.
     */
    private static native void redirectToUrl(final String url)/*-{
                    $wnd.location = url;
                }-*/;
}
