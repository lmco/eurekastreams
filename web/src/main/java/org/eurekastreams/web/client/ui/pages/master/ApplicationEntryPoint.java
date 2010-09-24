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
package org.eurekastreams.web.client.ui.pages.master;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionProcessorImpl;
import org.eurekastreams.commons.client.ActionRPCService;
import org.eurekastreams.commons.client.ActionRPCServiceAsync;
import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.commons.exceptions.SessionException;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.TermsOfServiceDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.ChecklistRefreshEvent;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.FormLoginCompleteEvent;
import org.eurekastreams.web.client.events.GadgetStateChangeEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.TermsOfServiceAcceptedEvent;
import org.eurekastreams.web.client.events.UpdateGadgetPrefsEvent;
import org.eurekastreams.web.client.events.data.GotSystemSettingsResponseEvent;
import org.eurekastreams.web.client.history.HistoryHandler;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacade;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.AllPopularHashTagsModel;
import org.eurekastreams.web.client.model.NotificationCountModel;
import org.eurekastreams.web.client.model.SystemSettingsModel;
import org.eurekastreams.web.client.model.TutorialVideoModel;
import org.eurekastreams.web.client.ui.PeriodicEventManager;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.TimerFactory;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.login.LoginDialogContent;
import org.eurekastreams.web.client.ui.common.dialog.tos.TermsOfServiceDialogContent;
import org.eurekastreams.web.client.ui.pages.accessdenied.AccessDeniedContent;
import org.eurekastreams.web.client.ui.pages.setup.SystemSetupPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
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
    private ActionProcessor processor = new ActionProcessorImpl((ActionRPCServiceAsync) GWT
            .create(ActionRPCService.class));

    /**
     * The master page.
     */
    private MasterComposite master;

    /**
     * The jsni facade.
     */
    private WidgetJSNIFacade jSNIFacade = new WidgetJSNIFacadeImpl();

    /**
     * The session.
     */
    private Session session = Session.getInstance();

    /**
     * The login dialog.
     */
    private Dialog loginDialog;

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
            loadPerson();
            master = new MasterComposite();

            EventBus.getInstance().addObserver(FormLoginCompleteEvent.class, new Observer<FormLoginCompleteEvent>()
            {

                public void update(final FormLoginCompleteEvent event)
                {
                    loginDialog.setBgVisible(false);
                    loadPerson();
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
        }
    }

    /**
     * Load the person.
     */
    private void loadPerson()
    {

        this.processor.setQueueRequests(true);

        SystemSettingsModel.getInstance().fetch(null, false);
        TutorialVideoModel.getInstance().fetch(null, true);
        AllPopularHashTagsModel.getInstance().fetch(null, true);

        // fetch initial notification count here so request gets bundled with the other startup requests
        NotificationCountModel.getInstance().fetch(null, false);

        processor.makeRequest(new ActionRequestImpl<PersonModelView>("getPersonModelView", null),
                new AsyncCallback<PersonModelView>()
                {
                    /* implement the async call back methods */
                    public void onFailure(final Throwable caught)
                    {
                        RootPanel.get().add(master);
                        if (caught.getMessage().contains("NO_CREDENTIALS"))
                        {
                            showLogin();
                        }
                        else if (caught instanceof SessionException)
                        {
                            Window.Location.reload();
                        }
                        else if (caught.getMessage().contains("LOGIN_DISABLED"))
                        {
                            RootPanel.get().clear();
                            RootPanel.get().add(new AccessDeniedContent());
                        }
                        else
                        {
                            jSNIFacade.alert("Unrecoverable Error.  Please Try Again");
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

                        Person result = new Person(resultMV);
                        session.setAuthenticationType(resultMV.getAuthenticationType());
                        session.setCurrentPerson(result);
                        session.setCurrentPersonRoles(resultMV.getRoles());
                        jSNIFacade.setViewer(result.getOpenSocialId(), result.getAccountId());
                        jSNIFacade.setOwner(result.getOpenSocialId());
                        processor.setQueueRequests(true);

                        session.setHistoryHandler(new HistoryHandler());
                        master.render();

                        Session.getInstance().getEventBus().bufferObservers();
                        History.fireCurrentHistoryState();

                        processor.fireQueuedRequests();
                        processor.setQueueRequests(false);

                        session.getPeriodicEventManager().start();

                        RootPanel.get().add(master);
                    }
                });

        processor.fireQueuedRequests();
        processor.setQueueRequests(false);
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
     * Fires off a refresh checklist event onto the bus.
     */
    public static void refreshChecklist()
    {
        EventBus.getInstance().notifyObservers(ChecklistRefreshEvent.getEvent());
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
     * This method exposes the GWT History.newItem method to javascript. This is so gadgets can have access to the
     * browsers history token without triggering a refresh in IE. Amazingly, this function wouldn't even need to exist
     * if it weren't for IE. Yay for inter-browser support!
     */
    private static native void setUpGwtFunctions()
    /*-{
        $wnd.gwt_refreshChecklist = function() {
                @org.eurekastreams.web.client.ui.pages.master.ApplicationEntryPoint::refreshChecklist()();
        }

        $wnd.gwt_newHistoryItem = function(token) {
                @com.google.gwt.user.client.History::newItem(Ljava/lang/String;)(token);
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

        $wnd.jQuery($doc).ready(function() {
                $wnd.jQuery().mousemove(function(e) {
                   $wnd.mouseXpos = e.pageX;
                   $wnd.mouseYpos = e.pageY;
                });
        });
    }-*/;
}
