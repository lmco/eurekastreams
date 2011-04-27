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
package org.eurekastreams.web.client.ui.pages.master;

import java.io.Serializable;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionProcessorImpl;
import org.eurekastreams.commons.client.ActionRPCService;
import org.eurekastreams.commons.client.ActionRPCServiceAsync;
import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.UsageMetricDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamRequestEvent;
import org.eurekastreams.web.client.events.SwitchedHistoryViewEvent;
import org.eurekastreams.web.client.history.HistoryHandler;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.UsageMetricModel;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ConnectEntryPoint implements EntryPoint
{
    /**
     * Mandatory ID for the HTML element in which to create the widget.
     */
    private static final String WIDGET_ELEMENT_ID = "widget-rootpanel";

    /** The action processor. */
    private ActionProcessor processor;

    /** The root panel. */
    private RootPanel rootPanel;

    /** The session. */
    private final Session session = Session.getInstance();

    /** For creating the widget pages. */
    private final ConnectPageFactory pageFactory = new ConnectPageFactory();

    /** URL for launching the main app. */
    private String mainAppLaunchUrl;

    /**
     * Module load.
     */
    public void onModuleLoad()
    {
        // The entry point will be invoked on full-app startup, so do nothing if the appropriate widget element is not
        // found
        rootPanel = RootPanel.get(WIDGET_ELEMENT_ID);
        if (rootPanel == null)
        {
            return;
        }
        rootPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectWidgetRoot());

        StaticResourceBundle.INSTANCE.coreCss().ensureInjected();
        StaticResourceBundle.INSTANCE.yuiCss().ensureInjected();

        ActionRPCServiceAsync service = (ActionRPCServiceAsync) GWT.create(ActionRPCService.class);
        ((ServiceDefTarget) service).setServiceEntryPoint("/gwt_rpc");
        processor = new ActionProcessorImpl(service);

        session.setActionProcessor(processor);
        session.setEventBus(EventBus.getInstance());

        processor.makeRequest(new ActionRequestImpl<Serializable>("noOperation", null), new AsyncCallback<String>()
        {
            public void onFailure(final Throwable caught)
            {
                onSessionInitFailure(caught);
            }

            public void onSuccess(final String sessionId)
            {
                ActionProcessorImpl.setCurrentSessionId(sessionId);

                // this must be the first action called so that the session is handled correctly
                processor.makeRequest(new ActionRequestImpl<PersonModelView>("getPersonModelView", null),
                        new AsyncCallback<PersonModelView>()
                        {
                            public void onFailure(final Throwable caught)
                            {
                                onPersonFetchFailure(caught);
                            }

                            public void onSuccess(final PersonModelView person)
                            {
                                session.setCurrentPerson(person);
                                session.setCurrentPersonRoles(person.getRoles());
                                session.setHistoryHandler(new HistoryHandler());

                                determineLaunchPage();

                                // catch attempts to go to profile pages and pop them up in a new window
                                final EventBus eventBus = Session.getInstance().getEventBus();
                                eventBus.addObserver(SwitchedHistoryViewEvent.class,
                                        new Observer<SwitchedHistoryViewEvent>()
                                        {
                                            public void update(final SwitchedHistoryViewEvent ev)
                                            {
                                                switch (ev.getPage())
                                                {
                                                case PEOPLE:
                                                case GROUPS:
                                                case ORGANIZATIONS:
                                                    String url = mainAppLaunchUrl + Window.Location.getHash();
                                                    Window.open(url, "_blank", "");
                                                    History.back();
                                                    break;
                                                default:
                                                    break;
                                                }
                                            }
                                        });

                                recordStreamViewMetrics();

                                Session.getInstance().getEventBus().bufferObservers();

                                buildPage();
                            }
                        });
            }
        });
    }

    /**
     * Record stream view metrics.
     */
    private void recordStreamViewMetrics()
    {
        Session.getInstance().getEventBus().addObserver(StreamRequestEvent.class, new Observer<StreamRequestEvent>()
        {
            public void update(final StreamRequestEvent event)
            {
                UsageMetricDTO umd = new UsageMetricDTO(false, true);
                umd.setMetricDetails(event.getJson());
                UsageMetricModel.getInstance().insert(umd);
            }
        });
    }

    /**
     * Invoked on failure to establish the session.
     *
     * @param caught
     *            Error returned.
     */
    private void onSessionInitFailure(final Throwable caught)
    {
        final Label errLabel = new Label("Eureka is down for maintence and will be back as soon as possible.");
        RootPanel.get(WIDGET_ELEMENT_ID).add(errLabel);
    }

    /**
     * Invoked on failure to retrieve person info.
     *
     * @param caught
     *            Error returned.
     */
    private void onPersonFetchFailure(final Throwable caught)
    {
        final Label errLabel = new Label("Eureka is down for maintence and will be back as soon as possible.");
        RootPanel.get(WIDGET_ELEMENT_ID).add(errLabel);
    }

    /**
     * Builds a page displaying the desired widget.
     */
    private void buildPage()
    {
        WidgetJSNIFacadeImpl util = new WidgetJSNIFacadeImpl();

        String widgetName = util.getParameter("widget");
        if (widgetName != null)
        {
            Widget widget = pageFactory.createPageWithHistory(widgetName, util);
            if (widget != null)
            {
                rootPanel.add(widget);
                return;
            }
        }

        final Label errLabel = new Label("Eureka is down for maintence and will be back as soon as possible.");
        RootPanel.get(WIDGET_ELEMENT_ID).add(errLabel);
    }

    /**
     * Determines the URL to use to launch the main app.
     */
    private void determineLaunchPage()
    {
        String param = Window.Location.getParameter("_main");
        if (param != null && !param.isEmpty())
        {
            mainAppLaunchUrl = param;
        }
        else
        {
            String path = Window.Location.getPath();
            int index = path.lastIndexOf('/');
            if (index >= 0 && path.length() > index + 1)
            {
                UrlBuilder builder = new UrlBuilder();
                builder.setProtocol(Window.Location.getProtocol());
                builder.setHost(Window.Location.getHost());
                try
                {
                    builder.setPort(Integer.parseInt(Window.Location.getPort()));
                }
                catch (Exception ex)
                {
                    int makeCheckstyleShutUp = 1;
                }
                builder.setPath(path.substring(0, index));
                mainAppLaunchUrl = builder.buildString();
            }
        }
    }
}
