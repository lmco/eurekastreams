/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PreSwitchedHistoryViewEvent;
import org.eurekastreams.web.client.events.PreventHistoryChangeEvent;
import org.eurekastreams.web.client.events.SwitchedHistoryViewEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
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
    private WidgetJSNIFacadeImpl jsniFacade = new WidgetJSNIFacadeImpl();

    /**
     * The values.
     */
    private HashMap<String, String> values = new HashMap<String, String>();

    /**
     * The views.
     */
    private List<String> views = new ArrayList<String>();

    /**
     * The page.
     */
    private Page page = null;

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
        Session.getInstance().getEventBus().addObserver(UpdateHistoryEvent.class, new Observer<UpdateHistoryEvent>()
        {
            public void update(final UpdateHistoryEvent event)
            {
                previousToken = History.getToken();
                onValueChange(getHistoryToken(event.getRequest()));
                fireValueChange = false;
                jsniFacade.setHistoryToken(getHistoryToken(event.getRequest()), true);

                // in case setting the history token above doesn't cause onValueChange to be called, reset
                // fireValueChange so it will work properly next time around
                fireValueChange = true;
            }
        });

        Session.getInstance().getEventBus().addObserver(PreventHistoryChangeEvent.class,
                new Observer<PreventHistoryChangeEvent>()
                {
                    public void update(final PreventHistoryChangeEvent event)
                    {
                        interruptHistoryChange = true;
                    }
                });
        History.addValueChangeHandler(this);

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
            List<String> originalViews = views;
            Page originalPage = page;
            HashMap<String, String> originalValues = values;
            values.clear();

            if (historyToken.contains("?"))
            {
                // ? position
                int questionMarkIndex = historyToken.indexOf("?") + 1;

                setPageAndView(historyToken.substring(0, questionMarkIndex - 1));

                // get the sub string of parameters var=1&var2=2&var3=3
                String[] arStr = historyToken.substring(questionMarkIndex, historyToken.length()).split("&");

                for (int i = 0; i < arStr.length; i++)
                {
                    String[] substr = arStr[i].split("=");

                    if (substr.length == 2)
                    {
                        values.put(jsniFacade.urlDecode(substr[0]), jsniFacade.urlDecode(substr[1]));
                    }
                }
            }
            else
            {
                setPageAndView(historyToken);
            }

            boolean viewUpdated = false;

            if (originalViews.size() == views.size())
            {
                for (int i = 0; i < views.size() && !viewUpdated; i++)
                {
                    if (!views.get(i).equals(originalViews.get(i)))
                    {
                        viewUpdated = true;
                    }
                }
            }
            else
            {
                viewUpdated = true;
            }

            // The view has updated.
            if (viewUpdated || originalPage != page)
            {
                // Let developers know we're about to switch the view. Prep for it if necessary. If you want us
                // to stop, throw a PreventHistoryChangeEvent and we'll set the boolean to stop it from going.
                Session.getInstance().getEventBus().notifyObservers(new PreSwitchedHistoryViewEvent(page, views));

                // We're all clear. Go ahead. These are the events you're looking for.
                if (!interruptHistoryChange)
                {
                    // Put the original events back into the event bus, wiping out all the events specific to the prior
                    // page.
                    Session.getInstance().getEventBus().restoreBufferedObservers();
                    // Clear all temporary timer jobs.
                    Session.getInstance().getTimer().clearTempJobs();
                    // Tell listeners the URL has indicated a page/view change.

                    Session.getInstance().getEventBus().notifyObservers(new SwitchedHistoryViewEvent(page, views));
                }
                // A developer has halted the process. He probably sees something he needs to alert the user
                // of before they switch the page. Roll everything back.
                else
                {
                    views = originalViews;
                    page = originalPage;
                    values = originalValues;

                    interruptHistoryChange = false;
                    fireValueChange = false;
                    jsniFacade.setHistoryToken(previousToken, true);
                    fireValueChange = true;
                    return;
                }
            }

            Session.getInstance().getEventBus().notifyObservers(new UpdatedHistoryParametersEvent(values));
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
     * Helper method since I need to do this twice in the method above.
     *
     * @param token
     *            the token.
     */
    private void setPageAndView(final String token)
    {
        String[] tokens = token.split("/", 2);
        page = Page.toEnum(tokens[0]);

        if (tokens.length > 1)
        {
            views = Arrays.asList(tokens[1].split("/"));
        }
        else
        {
            views = new ArrayList<String>();
        }

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
        Page inPage = page;
        List<String> inViews = views;
        HashMap<String, String> previousParams = (HashMap<String, String>) values.clone();

        if (request.getPage() != null)
        {
            inPage = request.getPage();
        }

        if (inPage == null)
        {
            inPage = Page.START;
        }

        if (request.getViews() != null)
        {
            inViews = request.getViews();
        }

        if (request.getReplacePrevious())
        {
            previousParams.clear();
        }

        for (String key : request.getParameters().keySet())
        {
            previousParams.put(key, request.getParameters().get(key));
            if (request.getParameters().containsKey(key) && request.getParameters().get(key) == null)
            {
                previousParams.remove(key);
            }
        }

        String historyString;

        historyString = inPage.toString();

        for (String view : inViews)
        {
            if (view != null && !view.equals(""))
            {
                historyString += "/";
                historyString += view;
            }
        }

        String prefix = "?";

        for (Entry<String, String> entry : previousParams.entrySet())
        {
            historyString += prefix + jsniFacade.urlEncode(entry.getKey()) + "="
                    + jsniFacade.urlEncode(entry.getValue());
            prefix = "&";
        }

        return historyString;
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
        return values.get(key);
    }

    /**
     * Gets the views.
     *
     * @return the views.
     */
    public List<String> getViews()
    {
        return views;
    }

    /**
     * @return The current page.
     */
    public Page getPage()
    {
        return page;
    }
}
