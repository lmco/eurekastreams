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
package org.eurekastreams.web.client.ui.pages.accessdenied;

import org.eurekastreams.web.client.events.FormLoginCompleteEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.HistoryState;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.login.LoginDialogContent;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * Access denied page.
 * 
 */
public class AccessDeniedContent extends Composite implements Bindable
{
    /**
     * The history state object.
     */
    private HistoryState history = new HistoryState(new WidgetJSNIFacadeImpl());

    /**
     * default constructor.
     */
    public AccessDeniedContent()
    {

        FlowPanel panel = new FlowPanel();
        panel.addStyleName("access-denied");
        panel
                .add(new HTML(
                        "<strong>The Rush of Discovery</strong><p>Make informed decisions by "
                                + "finding relavant colleagues and groups, following their streams of "
                                + "activity, and engaging in conversations.</p>"));
        LoginDialogContent loginDialogContent = new LoginDialogContent();

        panel.add(loginDialogContent.getBody());

        FlowPanel accessDeniedPanel = new FlowPanel();
        accessDeniedPanel.addStyleName("notice");
        accessDeniedPanel.add(new HTML(
                "You do not have access to the system at this time. Send us "
                        + "a request to ask for access."));
        panel.add(accessDeniedPanel);
        initWidget(panel);

        Session.getInstance().getEventBus().addObserver(
                FormLoginCompleteEvent.class,
                new Observer<FormLoginCompleteEvent>()
                {
                    public void update(final FormLoginCompleteEvent arg1)
                    {
                        String url = "/";
                        if (!history.getValue("url").equals(""))
                        {
                            url = history.getValue("url");
                        }
                        Window.Location.assign(url);
                    }
                });
    }
}
