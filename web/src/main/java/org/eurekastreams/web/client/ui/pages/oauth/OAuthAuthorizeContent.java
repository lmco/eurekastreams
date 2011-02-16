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
package org.eurekastreams.web.client.ui.pages.oauth;

import org.eurekastreams.commons.client.ActionProcessor;

import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * UI for getting authorization approval from the user for an OAuth request.
 */
public class OAuthAuthorizeContent extends Composite implements HistoryListener
{
    /**
     * Controller.
     */
    OAuthAuthorizeContentController controller;

    /**
     * Constructor.
     * @param inProcessor
     *          the action processor.
     * @param uri
     *          the uri that could contain additional oauth parameters.
     */
    public OAuthAuthorizeContent(final ActionProcessor inProcessor, final String uri)
    {
        FlowPanel containerPanel = new FlowPanel();
        FlowPanel mainPanel = new FlowPanel();


        /*String[] urlTokens = uri.split("?");

        String querystring = "";
        String token = "";

        if (urlTokens.length >= 2)
        {
            querystring = urlTokens[1];
            if (querystring.startsWith("oauth_token="))
            {
                token = querystring.substring(12);
            }
        }

        Label title = new Label("OAuth Authorize: " + uri + "; token: " + token);*/
        Label title = new Label("OAuth Authorize: Yes or No?");

        mainPanel.add(title);
        containerPanel.add(mainPanel);
        initWidget(containerPanel);

        controller = new OAuthAuthorizeContentController(inProcessor);
        controller.init();
    }

    /**
     * Implements history listener.
     *
     * @param historyToken
     *            the history token.
     */
    public void onHistoryChanged(final String historyToken)
    {
    }
}
