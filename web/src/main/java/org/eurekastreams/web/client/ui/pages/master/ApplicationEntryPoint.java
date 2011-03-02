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
import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.commons.exceptions.SessionException;
import org.eurekastreams.server.domain.AvatarUrlGenerator;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.TermsOfServiceDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.FormLoginCompleteEvent;
import org.eurekastreams.web.client.events.GadgetStateChangeEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.TermsOfServiceAcceptedEvent;
import org.eurekastreams.web.client.events.UpdateGadgetPrefsEvent;
import org.eurekastreams.web.client.events.data.GotBulkEntityResponseEvent;
import org.eurekastreams.web.client.events.data.GotSystemSettingsResponseEvent;
import org.eurekastreams.web.client.history.HistoryHandler;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacade;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.BulkEntityModel;
import org.eurekastreams.web.client.model.StartTabsModel;
import org.eurekastreams.web.client.model.SystemSettingsModel;
import org.eurekastreams.web.client.ui.PeriodicEventManager;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.TimerFactory;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.login.LoginDialogContent;
import org.eurekastreams.web.client.ui.common.dialog.lookup.EmployeeLookupContent;
import org.eurekastreams.web.client.ui.common.dialog.message.MessageDialogContent;
import org.eurekastreams.web.client.ui.common.dialog.tos.TermsOfServiceDialogContent;
import org.eurekastreams.web.client.ui.pages.accessdenied.AccessDeniedContent;
import org.eurekastreams.web.client.ui.pages.setup.SystemSetupPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ApplicationEntryPoint implements EntryPoint
{
    /**
     * Idle timeout in seconds.
     */
    private static final int APP_IDLE_TIMEOUT = 5 * 60;

    /**
     * Display TOS.
     */
    private boolean displayTOS = true;
    /**
     * The action processor.
     */
    private final ActionProcessor processor = new ActionProcessorImpl((ActionRPCServiceAsync) GWT
            .create(ActionRPCService.class));

    /**
     * The master page.
     */
    private MasterComposite master;

    /**
     * The jsni facade.
     */
    private final WidgetJSNIFacade jSNIFacade = new WidgetJSNIFacadeImpl();

    /**
     * The session.
     */
    private final Session session = Session.getInstance();

    /**
     * The login dialog.
     */
    private Dialog loginDialog;

    /**
     * Employee lookup modal.
     */
    private static EmployeeLookupContent dialogContent;

    /**
     * Shows the login.
     */
    private void showLogin()
    {
        loginDialog = new Dialog(new LoginDialogContent());
        loginDialog.setBgVisible(true);
        loginDialog.center();
        loginDialog.getContent().show();
    }

    /**
     * Method that gets called during load of the EntryPoint.
     */
    public void onModuleLoad()
    {
        ((CssResource) StaticResourceBundle.INSTANCE.coreCss()).ensureInjected();
        StaticResourceBundle.INSTANCE.yuiCss().ensureInjected();
        
        if (getUserAgent().contains("msie 7"))
        {
            StaticResourceBundle.INSTANCE.ieCss().ensureInjected();
        }
        
        session.setActionProcessor(processor);
        session.setEventBus(EventBus.getInstance());
        session.setPeriodicEventManager(new PeriodicEventManager(APP_IDLE_TIMEOUT, new TimerFactory(), processor));

        if (History.getToken().equals("setup"))
        {
            RootPanel.get().add(new SystemSetupPanel());
        }
        else if (History.getToken().equals("requestaccess"))
        {
            RootPanel.get().add(new AccessDeniedContent());
        }
        else
        {
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

            processor.makeRequest(new ActionRequestImpl<PersonModelView>("noOperation", null),
                    new AsyncCallback<Serializable>()
                    {
                        public void onFailure(final Throwable caught)
                        {
                            if (caught.getMessage().contains("NO_CREDENTIALS"))
                            {
                                showLogin();
                            }
                            else if (caught.getMessage().contains("LOGIN_DISABLED"))
                            {
                                RootPanel.get().clear();
                                RootPanel.get().add(new AccessDeniedContent());
                            }
                            else
                            {
                                Dialog.showDialog(new MessageDialogContent("Unable to Establish Connection",
                                        "Please Refresh."));
                            }
                        }

                        public void onSuccess(final Serializable sessionId)
                        {

                            ActionProcessorImpl.setCurrentSessionId((String) sessionId);

                            loadPerson();
                        }
                    });
        }
    }

    /**
     * Load the person.
     */
    private void loadPerson()
    {
        // this must be the first action called so that the session is handled correctly
        processor.makeRequest(new ActionRequestImpl<PersonModelView>("getPersonModelView", null),
                new AsyncCallback<PersonModelView>()
                {
                    /* implement the async call back methods */
                    public void onFailure(final Throwable caught)
                    {
                        RootPanel.get().add(master);

                        if (caught instanceof SessionException)
                        {
                            Dialog
                                    .showDialog(new MessageDialogContent("Unable to Establish Session",
                                            "Please Refresh."));
                        }
                        else
                        {
                            Dialog.showDialog(new MessageDialogContent("Unable to Establish Connection",
                                    "Please Refresh."));
                        }
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

                        session.setHistoryHandler(new HistoryHandler());
                        Session.getInstance().getEventBus().bufferObservers();
                        History.fireCurrentHistoryState();

                        processor.setQueueRequests(true);
                        master.renderHeaderAndFooter();
                        processor.fireQueuedRequests();
                        processor.setQueueRequests(false);

                        session.getPeriodicEventManager().start();
                        RootPanel.get().add(master);
                    }
                });
    }

    /**
     * Shows the ToS modal.
     * 
     */
    private void displayToS()
    {

        Session.getInstance().getEventBus().addObserver(GotSystemSettingsResponseEvent.class,
                new Observer<GotSystemSettingsResponseEvent>()
                {
                    public void update(final GotSystemSettingsResponseEvent event)
                    {
                        if (displayTOS)
                        {

                            TermsOfServiceDialogContent tosDialog = new TermsOfServiceDialogContent(
                                    new TermsOfServiceDTO(event.getResponse().getTermsOfService()), false);

                            final Dialog dialog = new Dialog(tosDialog);
                            dialog.setBgVisible(true);
                            dialog.center();
                            dialog.getContent().show();
                        }
                    }

                });

        SystemSettingsModel.getInstance().fetch(null, true);
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
        Dialog newDialog = new Dialog(dialogContent);
        newDialog.setBgVisible(true);
        newDialog.center();
        newDialog.getContent().show();
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
        Session.getInstance().getEventBus().addObserver(GotBulkEntityResponseEvent.class,
                new Observer<GotBulkEntityResponseEvent>()
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
                                    String imageUrl = urlGen
                                            .getSmallAvatarUrl(personMV.getId(), personMV.getAvatarId());

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

    
}
