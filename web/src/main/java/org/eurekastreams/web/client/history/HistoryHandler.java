/*
 * Copyright (c) 2010-2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.history;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.HistoryViewsChangedEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PreSwitchedHistoryViewEvent;
import org.eurekastreams.web.client.events.PreventHistoryChangeEvent;
import org.eurekastreams.web.client.events.SwitchedHistoryViewEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.UpdateRawHistoryEvent;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;

/**
 * The HistoryHandler should be the ONLY history listener in the entire app. It should listen for history change events
 * and fire off one of two events. An event to indicate the current page/view has changed (which would rerender the page
 * but not in a reload) and/or an event to indicate the parameters of the page have changed (which will alert the
 * current view of this, not change it. It also eats an UpdateHistoryEvent which allows a view to change the history w/o
 * directly touching it, and involves helper methods to create history tokens so that manual Hyperlinks can be made
 * without hardcoding URLs.
 *
 */
public class HistoryHandler implements ValueChangeHandler<String>
{
    /**
     * The JSNI facade.
     */
    private final WidgetJSNIFacadeImpl jsniFacade = new WidgetJSNIFacadeImpl();

    /**
     * The values.
     */
    private Map<String, String> currentValues = new HashMap<String, String>();

    /**
     * The views.
     */
    private List<String> currentViews = new ArrayList<String>();

    /**
     * The page.
     */
    private Page currentPage = null;

    /**
     * Should we execute the value change.
     */
    private boolean fireValueChange = true;

    /**
     * Should we stop the history change?.
     */
    private boolean interruptHistoryChange = false;

    /**
     * The previous token.
     */
    private String previousToken = "";

    /**
     * Default Constructor.
     */
    public HistoryHandler()
    {
        EventBus eventBus = Session.getInstance().getEventBus();
        eventBus.addObserver(UpdateHistoryEvent.class, new Observer<UpdateHistoryEvent>()
        {
            public void update(final UpdateHistoryEvent event)
            {
                updateHistory(getHistoryToken(event.getRequest()));
            }
        });
        eventBus.addObserver(UpdateRawHistoryEvent.class, new Observer<UpdateRawHistoryEvent>()
        {
            public void update(final UpdateRawHistoryEvent event)
            {
                updateHistory(event.getHistoryToken());
            }
        });
        eventBus.addObserver(PreventHistoryChangeEvent.class, new Observer<PreventHistoryChangeEvent>()
        {
            public void update(final PreventHistoryChangeEvent event)
            {
                interruptHistoryChange = true;
            }
        });
        History.addValueChangeHandler(this);
    }

    /**
     * Handles updating history when requested.
     *
     * @param historyToken
     *            The history token for the new location.
     */
    private void updateHistory(final String historyToken)
    {
        previousToken = History.getToken();
        onValueChange(historyToken);
        fireValueChange = false;
        jsniFacade.setHistoryToken(historyToken, true);

        // in case setting the history token above doesn't cause onValueChange to be called, reset
        // fireValueChange so it will work properly next time around
        fireValueChange = true;
    }

    /**
     * On Value Change.
     *
     * @param historyToken
     *            the history token.
     */
    private void onValueChange(final String historyToken)
    {
        if (fireValueChange)
        {
            // save old state
            List<String> originalViews = currentViews;
            Page originalPage = currentPage;
            Map<String, String> originalValues = currentValues;

            // parse and store new state
            CreateUrlRequest parsed = parseHistoryToken(historyToken);
            currentPage = parsed.getPage();
            currentViews = parsed.getViews();
            currentValues = parsed.getParameters();

            // check if the "views" part has changed
            boolean viewUpdated = false;
            if (originalViews.size() == currentViews.size())
            {
                for (int i = 0; i < currentViews.size(); i++)
                {
                    if (!currentViews.get(i).equals(originalViews.get(i)))
                    {
                        viewUpdated = true;
                        break;
                    }
                }
            }
            else
            {
                viewUpdated = true;
            }
            if (viewUpdated)
            {
                EventBus.getInstance().notifyObservers(new HistoryViewsChangedEvent(currentViews));
            }

            // check if the page has changed
            if (originalPage != currentPage)
            {
                // Let developers know we're about to switch the view. Prep for it if necessary. If you want us
                // to stop, throw a PreventHistoryChangeEvent and we'll set the boolean to stop it from going.
                Session.getInstance().getEventBus()
                        .notifyObservers(new PreSwitchedHistoryViewEvent(currentPage, currentViews));

                // We're all clear. Go ahead. These are the events you're looking for.
                if (!interruptHistoryChange)
                {
                    // Put the original events back into the event bus, wiping out all the events specific to the prior
                    // page.
                    Session.getInstance().getEventBus().restoreBufferedObservers();
                    // Clear all temporary timer jobs.
                    Session.getInstance().getTimer().clearTempJobs();
                    // Tell listeners the URL has indicated a page/view change.

                    Session.getInstance().getEventBus()
                            .notifyObservers(new SwitchedHistoryViewEvent(currentPage, currentViews));
                }
                // A developer has halted the process. He probably sees something he needs to alert the user
                // of before they switch the page. Roll everything back.
                else
                {
                    currentViews = originalViews;
                    currentPage = originalPage;
                    currentValues = originalValues;

                    interruptHistoryChange = false;
                    fireValueChange = false;
                    jsniFacade.setHistoryToken(previousToken, true);
                    fireValueChange = true;

                    return;
                }
            }

            Session.getInstance().getEventBus()
                    .notifyObservers(new UpdatedHistoryParametersEvent(currentValues, viewUpdated));
        }
        fireValueChange = true;
    }

    /**
     * OnValueChange gets called when the history changes.
     *
     * @param event
     *            the event.
     */
    public void onValueChange(final ValueChangeEvent<String> event)
    {
        onValueChange(event.getValue());
    }

    /**
     * Parses a history token into its component parts.
     *
     * @param historyToken
     *            History token.
     * @return Component parts as a CreateUrlRequest.
     */
    public CreateUrlRequest parseHistoryToken(final String historyToken)
    {
        HashMap<String, String> parameters = new HashMap<String, String>();

        String concatenatedViews = historyToken;

        int questionMarkIndex = historyToken.indexOf("?");
        if (questionMarkIndex >= 0)
        {
            concatenatedViews = historyToken.substring(0, questionMarkIndex);

            // get the sub string of parameters var=1&var2=2&var3=3
            String[] paramString = historyToken.substring(questionMarkIndex + 1).split("&");
            for (int i = 0; i < paramString.length; i++)
            {
                String[] substr = paramString[i].split("=");
                if (substr.length == 2)
                {
                    parameters.put(jsniFacade.urlDecode(substr[0]), jsniFacade.urlDecode(substr[1]));
                }
            }
        }

        String[] tokens = concatenatedViews.split("/", 2);
        Page page = Page.toEnum(tokens[0]);
        List<String> views = tokens.length > 1 ? Arrays.asList(tokens[1].split("/")) : Collections.EMPTY_LIST;

        return new CreateUrlRequest(page, views, parameters);
    }

    /**
     * Gets a history token given the params.
     *
     * @param request
     *            the request.
     * @return the token.
     */
    public String getHistoryToken(final CreateUrlRequest request)
    {
        Page inPage = currentPage;
        List<String> inViews = currentViews;

        // determine page
        if (request.getPage() != null)
        {
            inPage = request.getPage();
        }
        if (inPage == null)
        {
            inPage = Page.START;
        }

        // determine views
        if (request.getViews() != null)
        {
            inViews = request.getViews();
        }

        // determine parameters
        Map<String, String> parameters;
        if (request.getReplacePrevious())
        {
            parameters = request.getParameters();
        }
        else
        {
            parameters = new HashMap<String, String>(currentValues);
            for (Entry<String, String> entry : request.getParameters().entrySet())
            {
                if (entry.getValue() == null)
                {
                    parameters.remove(entry.getKey());
                }
                else
                {
                    parameters.put(entry.getKey(), entry.getValue());
                }
            }
        }

        // stringify page and views
        StringBuilder sb = new StringBuilder(inPage.toString());
        for (String view : inViews)
        {
            if (view != null && !view.isEmpty())
            {
                sb.append("/").append(view);
            }
        }

        // stringify parameters
        String prefix = "?";
        for (Entry<String, String> entry : parameters.entrySet())
        {
            sb.append(prefix).append(jsniFacade.urlEncode(entry.getKey())).append("=")
                    .append(jsniFacade.urlEncode(entry.getValue()));
            prefix = "&";
        }

        return sb.toString();
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
        return currentValues.get(key);
    }

    /**
     * Gets the views.
     *
     * @return the views.
     */
    public List<String> getViews()
    {
        return currentViews;
    }

    /**
     * @return The current page.
     */
    public Page getPage()
    {
        return currentPage;
    }

    /**
     * @return A collection holding the current history parameters.
     */
    public Map<String, String> getParameters()
    {
        return Collections.unmodifiableMap(currentValues);
    }
}
