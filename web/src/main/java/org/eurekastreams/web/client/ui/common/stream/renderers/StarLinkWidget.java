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
package org.eurekastreams.web.client.ui.common.stream.renderers;

import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.action.request.stream.SetActivityStarRequest;
import org.eurekastreams.server.action.request.stream.SetActivityStarRequest.StarActionType;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * A star panel.
 */
public class StarLinkWidget extends InlineLabel
{
    /** Current state of activity. */
    private boolean starred;

    /**
     * Default constructor.
     *
     * @param isStarred
     *            whether its starred.
     * @param activityId
     *            the activity ID.
     */
    public StarLinkWidget(final Boolean isStarred, final Long activityId)
    {
        starred = isStarred;

        addStyleName(StaticResourceBundle.INSTANCE.coreCss().linkedLabel());
        setText();

        addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                SetActivityStarRequest request = new SetActivityStarRequest(
                        activityId, starred ? StarActionType.REMOVE_STAR
                                : StarActionType.ADD_STAR);

                Session.getInstance().getActionProcessor().makeRequest(
                        new ActionRequestImpl<Boolean>("setActivityStar",
                                request), new AsyncCallback<Boolean>()
                        {
                            /* implement the async call back methods */
                            public void onFailure(final Throwable caught)
                            {
                                // TODO handle error.
                            }

                            public void onSuccess(final Boolean result)
                            {
                                starred = !starred;
                                setText();
                            }
                        });
            }

        });
    }

    /**
     * Sets the text based on the current state.
     */
    private void setText()
    {
        setText(starred ? "Unsave" : "Save");
    }
}
