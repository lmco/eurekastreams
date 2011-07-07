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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionProcessorImpl;
import org.eurekastreams.commons.client.ActionRPCService;
import org.eurekastreams.commons.client.ActionRPCServiceAsync;
import org.eurekastreams.server.domain.AvatarUrlGenerator;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.TermsOfServiceDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.UsageMetricDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.FormLoginCompleteEvent;
import org.eurekastreams.web.client.events.GadgetStateChangeEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.SwitchedHistoryViewEvent;
import org.eurekastreams.web.client.events.TermsOfServiceAcceptedEvent;
import org.eurekastreams.web.client.events.UpdateGadgetPrefsEvent;
import org.eurekastreams.web.client.events.data.GotBulkEntityResponseEvent;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.events.data.GotSystemSettingsResponseEvent;
import org.eurekastreams.web.client.history.HistoryHandler;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacade;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.BulkEntityModel;
import org.eurekastreams.web.client.model.StartTabsModel;
import org.eurekastreams.web.client.model.SystemSettingsModel;
import org.eurekastreams.web.client.model.UsageMetricModel;
import org.eurekastreams.web.client.ui.PeriodicEventManager;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.TimerFactory;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.login.LoginDialogContent;
import org.eurekastreams.web.client.ui.common.dialog.lookup.EmployeeLookupContent;
import org.eurekastreams.web.client.ui.common.dialog.message.MessageDialogContent;
import org.eurekastreams.web.client.ui.common.dialog.tos.TermsOfServiceDialogContent;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.googlecode.gchart.client.GChart;
import com.googlecode.gchart.client.GChartCanvasFactory;
import com.googlecode.gchart.client.GChartCanvasLite;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ApplicationEntryPoint implements EntryPoint
{
    /**
     * GWT canvas factory for charts.
     */
    public class GWTCanvasBasedCanvasFactory implements GChartCanvasFactory
    {
        /**
         * Create canvas element.
         *
         * @return canvas element.
         */
        public GChartCanvasLite create()
        {
            return new GWTCanvasBasedCanvasLite();
        }
    }

    /**
     * Relative URL of page to redirect to for users without access.
     */
    private static final String ACCESS_DENIED_PAGE = "/requestaccess.html";

    /**
     * Mandatory ID for the HTML body element to identify that the full app should be created.
     */
    private static final String FULL_APP_ELEMENT_ID = "full-app-rootpanel";

    /**
     * Idle timeout in seconds.
     */
    private static final int APP_IDLE_TIMEOUT = 5 * 60;

    /** Display TOS. */
    private boolean displayTOS = true;

    /** The action processor. */
    private ActionProcessor processor;

    /** The root panel. */
    private RootPanel rootPanel;

    /** The master page. */
    private MasterComposite master;

    /** The jsni facade. */
    private final WidgetJSNIFacade jSNIFacade = new WidgetJSNIFacadeImpl();

    /** The session. */
    private final Session session = Session.getInstance();

    /** Employee lookup modal. */
    private static EmployeeLookupContent dialogContent;

    /**
     * 10 minutes.
     */
    private static final int SESSION_POLLING_TIME = 900000;

    /**
     * Shows the login.
     */
    private void showLogin()
    {
        Dialog.showCentered(new LoginDialogContent());
    }

    /**
     * Method that gets called during load of the EntryPoint.
     */
    public void onModuleLoad()
    {
        GChart.setCanvasFactory(new GWTCanvasBasedCanvasFactory());

        // The entry point will be invoked when just a Eureka Connect widget is desired, so do nothing if the
        // appropriate full-app element is not found
        rootPanel = RootPanel.get(FULL_APP_ELEMENT_ID);
        if (rootPanel == null)
        {
            return;
        }

        ActionRPCServiceAsync service = (ActionRPCServiceAsync) GWT.create(ActionRPCService.class);
        ((ServiceDefTarget) service).setServiceEntryPoint("/gwt_rpc");
        processor = new ActionProcessorImpl(service, new AsyncCallback<String>()
        {
            public void onSuccess(final String inResult)
            {
            }

            public void onFailure(final Throwable inCaught)
            {
                if (inCaught.getMessage().contains("NO_CREDENTIALS"))
                {
                    showLogin();
                }
                else if (inCaught.getMessage().contains("LOGIN_DISABLED"))
                {
                    Window.Location.assign(ACCESS_DENIED_PAGE);
                }
                else
                {
                    Dialog.showCentered(new MessageDialogContent("Unable to Establish Connection", "Please Refresh."));
                }
            }
        });

        StaticResourceBundle.INSTANCE.coreCss().ensureInjected();
        StaticResourceBundle.INSTANCE.yuiCss().ensureInjected();

        session.setActionProcessor(processor);
        session.setEventBus(EventBus.getInstance());
        session.setPeriodicEventManager(new PeriodicEventManager(APP_IDLE_TIMEOUT, new TimerFactory(), processor));

        master = new MasterComposite();

        EventBus.getInstance().addObserver(FormLoginCompleteEvent.class, new Observer<FormLoginCompleteEvent>()
        {
            public void update(final FormLoginCompleteEvent event)
            {
                Window.Location.reload();
            }
        });

        EventBus.getInstance().addObserver(TermsOfServiceAcceptedEvent.class,
                new Observer<TermsOfServiceAcceptedEvent>()
                {
                    public void update(final TermsOfServiceAcceptedEvent event)
                    {
                        displayTOS = false;
                        loadPerson();
                    }
                });

        setUpGwtFunctions();

        loadPerson();
    }

    /**
     * Load the person.
     */
    private void loadPerson()
    {
        processor.makeRequest("getPersonModelViewForStartup", null, new AsyncCallback<PersonModelView>()
        {
            /* implement the async call back methods */
            public void onFailure(final Throwable caught)
            {
                rootPanel.add(master);

                // TODO: This REALLY NEEDS to distinguish between types of errors - it's REALLY ANNOYING to keep hitting
                // refresh for nothing when the server is down, etc.
                Dialog.showCentered(new MessageDialogContent("Unable to Establish Connection", "Please Refresh."));
            }

            public void onSuccess(final PersonModelView resultMV)
            {
                // If user needs to accept ToS, short circuit here.
                if (!resultMV.getTosAcceptance())
                {
                    displayToS();
                    return;
                }

                session.setAuthenticationType(resultMV.getAuthenticationType());
                session.setCurrentPerson(resultMV);
                session.setCurrentPersonRoles(resultMV.getRoles());
                jSNIFacade.setViewer(resultMV.getOpenSocialId(), resultMV.getAccountId());
                jSNIFacade.setOwner(resultMV.getOpenSocialId());
                processor.setQueueRequests(true);

                // create the StartTabs model before the event bus is buffered, so it's event wiring stays
                // intact for the life of the app
                StartTabsModel.getInstance();

                recordStreamViewMetrics();
                recordPageViewMetrics();

                session.setHistoryHandler(new HistoryHandler());
                Session.getInstance().getEventBus().bufferObservers();
                History.fireCurrentHistoryState();

                processor.setQueueRequests(true);
                master.renderHeaderAndFooter();
                processor.fireQueuedRequests();
                processor.setQueueRequests(false);

                session.getPeriodicEventManager().start();
                rootPanel.add(master);
            }
        });
    }

    /**
     * Shows the ToS modal.
     *
     */
    private void displayToS()
    {

        Session.getInstance().getEventBus()
                .addObserver(GotSystemSettingsResponseEvent.class, new Observer<GotSystemSettingsResponseEvent>()
                {
                    public void update(final GotSystemSettingsResponseEvent event)
                    {
                        if (displayTOS)
                        {
                            Dialog.showCentered(new TermsOfServiceDialogContent(new TermsOfServiceDTO(event
                                    .getResponse().getTermsOfService()), false));
                        }
                    }

                });

        SystemSettingsModel.getInstance().fetch(null, true);
    }

    /**
     * Record stream view metrics.
     */
    private void recordStreamViewMetrics()
    {
        // TODO: This is listening to the stream response event as the request stream event is called
        // twice on activity page for some reason (profile pages work correctly). Somewhere
        // this is filtered down to only one call to the server to get the stream, so response
        // event works fine for metrics, but should track down why request it double-firing.
        Session.getInstance().getEventBus()
                .addObserver(GotStreamResponseEvent.class, new Observer<GotStreamResponseEvent>()
                {
                    public void update(final GotStreamResponseEvent event)
                    {
                        UsageMetricDTO umd = new UsageMetricDTO(false, true);
                        umd.setMetricDetails(event.getJsonRequest());
                        UsageMetricModel.getInstance().insert(umd);
                    }
                });
    }

    /**
     * Record page view metrics.
     */
    private void recordPageViewMetrics()
    {
        Session.getInstance().getEventBus()
                .addObserver(SwitchedHistoryViewEvent.class, new Observer<SwitchedHistoryViewEvent>()
                {
                    public void update(final SwitchedHistoryViewEvent event)
                    {
                        UsageMetricDTO umd = new UsageMetricDTO(true, false);
                        umd.setMetricDetails(event.getPage().toString());
                        UsageMetricModel.getInstance().insert(umd);
                    }
                });
    }

    /**
     * Fires off a gadget change state event.
     *
     * @param id
     *            the gadget id
     * @param view
     *            the view to set.
     * @param params
     *            the optional parameters.
     */
    public static void changeGadgetState(final int id, final String view, final String params)
    {
        GadgetStateChangeEvent event = new GadgetStateChangeEvent(new Long(id), "gadgetId", view, params);
        EventBus.getInstance().notifyObservers(event);
    }

    /**
     * Fires of the UpdateGadgetPrefsEvent when called from the gadget container.
     *
     * @param inId
     *            - id of the gadget being updated.
     * @param inPrefs
     *            - updated preferences for the gadget.
     */
    public static void updateGadgetPrefs(final int inId, final String inPrefs)
    {
        UpdateGadgetPrefsEvent event = new UpdateGadgetPrefsEvent(new Long(inId), inPrefs);
        EventBus.getInstance().notifyObservers(event);
    }

    /**
     * Get the save command object.
     *
     * @return the save command
     */
    private static Command getEmployeeSelectedCommand()
    {
        return new Command()
        {
            public void execute()
            {
                PersonModelView result = dialogContent.getPerson();
                AvatarUrlGenerator urlGen = new AvatarUrlGenerator(EntityType.PERSON);
                String imageUrl = urlGen.getSmallAvatarUrl(result.getId(), result.getAvatarId());

                callEmployeeSelectedHandler(result.getAccountId(), result.getDisplayName(), imageUrl);
            }
        };
    }

    /**
     * Launch the employee lookup modal.
     */
    public static void launchEmployeeLookup()
    {
        dialogContent = new EmployeeLookupContent(getEmployeeSelectedCommand());
        Dialog.showCentered(dialogContent);
    }

    /**
     * Call the handler when the employee lookup is done.
     *
     * @param ntid
     *            the ntid.
     * @param displayName
     *            the display name.
     * @param avatarUrl
     *            the avatarurl.
     */
    private static native void callEmployeeSelectedHandler(final String ntid, final String displayName,
            final String avatarUrl)
    /*-{
           $wnd.empLookupCallback(ntid, displayName, avatarUrl);
    }-*/;

    /**
     * Get the people from the server, convert them to JSON, and feed them back to the handler.
     *
     * @param ntids
     *            the ntids.
     * @param callbackIndex
     *            the callback index.
     */
    public static void bulkGetPeople(final String[] ntids, final int callbackIndex)
    {
        Session.getInstance().getEventBus()
                .addObserver(GotBulkEntityResponseEvent.class, new Observer<GotBulkEntityResponseEvent>()
                {
                    public void update(final GotBulkEntityResponseEvent arg1)
                    {
                        List<String> ntidList = Arrays.asList(ntids);
                        JsArray<JavaScriptObject> personJSONArray = (JsArray<JavaScriptObject>) JavaScriptObject
                                .createArray();
                        int count = 0;

                        if (ntidList.size() == arg1.getResponse().size())
                        {
                            boolean notCorrectResponse = false;
                            for (Serializable person : arg1.getResponse())
                            {
                                PersonModelView personMV = (PersonModelView) person;
                                if (ntidList.contains(personMV.getAccountId()))
                                {
                                    AvatarUrlGenerator urlGen = new AvatarUrlGenerator(EntityType.PERSON);
                                    String imageUrl = urlGen.getSmallAvatarUrl(personMV.getId(),
                                            personMV.getAvatarId());

                                    JsArrayString personJSON = (JsArrayString) JavaScriptObject.createObject();
                                    personJSON.set(0, personMV.getAccountId());
                                    personJSON.set(1, personMV.getDisplayName());
                                    personJSON.set(2, imageUrl);

                                    personJSONArray.set(count, personJSON);
                                    count++;
                                }
                                else
                                {
                                    notCorrectResponse = true;
                                    break;
                                }
                            }
                            if (!notCorrectResponse)
                            {
                                callGotBulkPeopleCallback(personJSONArray, callbackIndex);
                            }
                        }
                    }
                });

        ArrayList<StreamEntityDTO> entities = new ArrayList<StreamEntityDTO>();

        for (int i = 0; i < ntids.length; i++)
        {
            StreamEntityDTO dto = new StreamEntityDTO();
            dto.setUniqueIdentifier(ntids[i]);
            dto.setType(EntityType.PERSON);
            entities.add(dto);
        }

        if (ntids.length == 0)
        {
            JsArray<JavaScriptObject> personJSONArray = (JsArray<JavaScriptObject>) JavaScriptObject.createArray();
            callGotBulkPeopleCallback(personJSONArray, callbackIndex);
        }
        else
        {
            BulkEntityModel.getInstance().fetch(entities, false);
        }
    }

    /**
     * Call the handler with the JSON data.
     *
     * @param data
     *            the data.
     * @param callbackIndex
     *            the callback index.
     */
    private static native void callGotBulkPeopleCallback(final JsArray data, final int callbackIndex)
    /*-{
           $wnd.bulkGetPeopleCallback[callbackIndex](data);
    }-*/;

    /**
     * Returns an additional property value given a key.
     *
     * @param key
     *            the key.
     * @return the value.
     */
    public static String getAddtionalProperty(final String key)
    {
        return Session.getInstance().getCurrentPerson().getAdditionalProperties().get(key);
    }

    /**
     * This method exposes the GWT History.newItem method to javascript. This is so gadgets can have access to the
     * browsers history token without triggering a refresh in IE. Amazingly, this function wouldn't even need to exist
     * if it weren't for IE. Yay for inter-browser support!
     */
    private static native void setUpGwtFunctions()
    /*-{
        $wnd.gwt_newHistoryItem = function(token) {
                @com.google.gwt.user.client.History::newItem(Ljava/lang/String;)(token);
        }

        $wnd.gwt_getAdditionalProperty = function(key) {
                return @org.eurekastreams.web.client.ui.pages.master.ApplicationEntryPoint::getAddtionalProperty(Ljava/lang/String;)(key);
        }

        $wnd.gwt_bulkGetPeople = function(ntids, handler) {
                if ($wnd.bulkGetPeopleCallback == null)
                {
                    $wnd.bulkGetPeopleCallback = [];
                }
                $wnd.bulkGetPeopleCallback.push(handler);
                @org.eurekastreams.web.client.ui.pages.master.ApplicationEntryPoint::bulkGetPeople([Ljava/lang/String;I)(ntids, $wnd.bulkGetPeopleCallback.length-1);
        }

        $wnd.gwt_launchEmpLookup = function(handler) {
                $wnd.empLookupCallback = handler;
                @org.eurekastreams.web.client.ui.pages.master.ApplicationEntryPoint::launchEmployeeLookup()();
        }

        $wnd.gwt_changeGadgetState = function(id, view, params) {
                @org.eurekastreams.web.client.ui.pages.master.ApplicationEntryPoint::changeGadgetState(ILjava/lang/String;Ljava/lang/String;)(id, view, params);
        }

        $wnd.gwt_updateGadgetPrefs = function(id, prefs) {
                @org.eurekastreams.web.client.ui.pages.master.ApplicationEntryPoint::updateGadgetPrefs(ILjava/lang/String;)(id, prefs);
        }

        $wnd.gwt_triggerShowNotificationEvent = function(notification) {
                var notificationEvent = @org.eurekastreams.web.client.events.ShowNotificationEvent::getInstance(Ljava/lang/String;)(notification);

                var s = @org.eurekastreams.web.client.ui.Session::getInstance()();
                var eb = s.@org.eurekastreams.web.client.ui.Session::getEventBus()();
                eb.@org.eurekastreams.web.client.events.EventBus::notifyObservers(Ljava/lang/Object;)(notificationEvent);
        }


        $wnd.jQuery('body').ready(function() {
                $wnd.jQuery('body').mousemove(function(e) {
                   $wnd.mouseXpos = e.pageX;
                   $wnd.mouseYpos = e.pageY;
                });
        });
    }-*/;

    /**
     * Get the user agent (for detecting IE7).
     *
     * @return the user agent.
     */
    public static native String getUserAgent()
    /*-{
        return navigator.userAgent.toLowerCase();
    }-*/;

    /**
     * GWT Canvas for charts.
     */
    public class GWTCanvasBasedCanvasLite extends GWTCanvas implements GChartCanvasLite
    {
        /**
         * GChartCanvasLite requires CSS/RGBA color strings, but GWTCanvas uses its own Color class instead, so we wrap.
         *
         * @param cssColor
         *            the color.
         */
        public void setStrokeStyle(final String cssColor)
        {
            // Sharp angles of default MITER can overwrite adjacent pie slices
            setLineJoin(GWTCanvas.ROUND);
            setStrokeStyle(new Color(cssColor));
        }

        /**
         * Set the fill style.
         *
         * @param cssColor
         *            the color.
         */
        public void setFillStyle(final String cssColor)
        {
            setFillStyle(new Color(cssColor));
        }
    }

}
