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
package org.eurekastreams.web.client.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.search.modelview.AuthenticationType;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.InsertOptOutVideoResponseEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.history.HistoryHandler;
import org.eurekastreams.web.client.log.Log;
import org.eurekastreams.web.client.timer.Timer;

/**
 * The current session.
 */
public class Session
{
    /**
     * The logger.
     */
    private static Log logger = new Log();
    /**
     * The timer.
     */
    private static Timer timer = new Timer();
    /**
     * History handler.
     */
    private static HistoryHandler history = null;

    /**
     * Current person's roles.
     */
    private static Set<Role> currentPersonRoles = new HashSet<Role>();

    /**
     * Current person.
     */
    private static PersonModelView currentPerson = null;

    /**
     * Authentication type.
     */
    private static AuthenticationType authenticationType = AuthenticationType.NOTSET;

    /**
     * Action processor.
     */
    private static ActionProcessor actionProcessor = null;

    /**
     * Event bus.
     */
    private static EventBus eventBus = null;

    /**
     * Instance.
     */
    private static Session instance;

    /**
     * Handles invoking events periodically.
     */
    private PeriodicEventManager periodicEventManager;

    /**
     * The main thing to do in this constructor is set up any session observers.
     */
    public Session()
    {
        EventBus.getInstance().addObserver(InsertOptOutVideoResponseEvent.class,
                new Observer<InsertOptOutVideoResponseEvent>()
                {
                    public void update(final InsertOptOutVideoResponseEvent insertVideoResponse)
                    {
                        HashSet<Long> videoList = currentPerson.getOptOutVideos();
                        videoList.add(insertVideoResponse.getResponse());
                        currentPerson.setOptOutVideos(videoList);
                    }
                });
    }

    /**
     * Get the instance of the session.
     * 
     * @return the session.
     */
    public static Session getInstance()
    {
        if (instance == null)
        {
            instance = new Session();
        }

        return instance;
    }

    /**
     * Sets the instance of the session for unit testing.
     * 
     * @param inInstance
     *            New instance.
     */
    public static void setInstance(final Session inInstance)
    {
        instance = inInstance;
    }

    /**
     * Set the current person.
     * 
     * @param inCurrentPerson
     *            the current person.
     */
    public void setCurrentPerson(final PersonModelView inCurrentPerson)
    {
        currentPerson = inCurrentPerson;
    }

    /**
     * Get the current person.
     * 
     * @return the current person.
     */
    public PersonModelView getCurrentPerson()
    {
        return currentPerson;
    }

    /**
     * Set the current persons roles.
     * 
     * @param inRoles
     *            the roles.
     */
    public void setCurrentPersonRoles(final Set<Role> inRoles)
    {
        currentPersonRoles = inRoles;
    }

    /**
     * Get the current persons roles.
     * 
     * @return the roles.
     */
    public Set<Role> getCurrentPersonRoles()
    {
        return currentPersonRoles;
    }

    /**
     * @param inActionProcessor
     *            the actionProcessor to set
     */
    public void setActionProcessor(final ActionProcessor inActionProcessor)
    {
        Session.actionProcessor = inActionProcessor;
    }

    /**
     * @return the actionProcessor.
     */
    public ActionProcessor getActionProcessor()
    {
        return actionProcessor;
    }

    /**
     * @param inEventBus
     *            the eventBus to set.
     */
    public void setEventBus(final EventBus inEventBus)
    {
        Session.eventBus = inEventBus;
    }

    /**
     * @return the eventBus.
     */
    public EventBus getEventBus()
    {
        return eventBus;
    }

    /**
     * Get the timer.
     * 
     * @return the timer.
     */
    public Timer getTimer()
    {
        return timer;
    }

    /**
     * @return the authenticationType
     */
    public AuthenticationType getAuthenticationType()
    {
        return authenticationType;
    }

    /**
     * @param inAuthenticationType
     *            the authenticationType to set
     */
    public void setAuthenticationType(final AuthenticationType inAuthenticationType)
    {
        authenticationType = inAuthenticationType;
    }

    /**
     * Set history handler.
     * 
     * @param inHistory
     *            history handler.
     */
    public void setHistoryHandler(final HistoryHandler inHistory)
    {
        history = inHistory;
    }

    /**
     * Get a url.
     * 
     * @param request
     *            request.
     * @return the url.
     */
    public String generateUrl(final CreateUrlRequest request)
    {
        return history.getHistoryToken(request);
    }

    /**
     * Get the value of a current parameter. NOTE: Do NOT use this to "monitor" the history param, only to grab a one
     * time instance of it. Use the UpdatedHistoryParametersEvent to listen to a parameter.
     * 
     * @param key
     *            the key.
     * @return the value.
     */
    public String getParameterValue(final String key)
    {
        return history.getParameterValue(key);
    }

    /**
     * @return The current page.
     */
    public Page getUrlPage()
    {
        return history.getPage();
    }

    /**
     * Get the current views.
     * 
     * @return the views.
     */
    public List<String> getUrlViews()
    {
        return history.getViews();
    }

    /**
     * @return the periodicEventManager
     */
    public PeriodicEventManager getPeriodicEventManager()
    {
        return periodicEventManager;
    }

    /**
     * @param inPeriodicEventManager
     *            the periodicEventManager to set
     */
    public void setPeriodicEventManager(final PeriodicEventManager inPeriodicEventManager)
    {
        periodicEventManager = inPeriodicEventManager;
    }

    /**
     * Get the logger.
     * 
     * @return the logger.
     */
    public Log getLogger()
    {
        return logger;
    }
}
