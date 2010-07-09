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
package org.eurekastreams.web.client.ui.common.stream;

import java.util.ArrayList;
import java.util.HashMap;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamReinitializeRequestEvent;
import org.eurekastreams.web.client.events.StreamSearchBeginEvent;
import org.eurekastreams.web.client.events.StreamSearchRequestMoreEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.LabeledTextBox;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.common.stream.StreamPanel.Mode;
import org.eurekastreams.web.client.ui.common.stream.filters.search.StreamSearchDialogContent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Stream searching widget.
 */
public class StreamSearchComposite extends FlowPanel implements Bindable
{
    /**
     * The search button.
     */
    Label searchGo = new Label("Search");

    /**
     * The search term.
     */
    LabeledTextBox searchTerm = new LabeledTextBox("search all activity");

    /**
     * Search term label.
     */
    Label searchTermLabel = new Label();

    /**
     * Search description.
     */
    FlowPanel searchDescription = new FlowPanel();

    /**
     * Save search button.
     */
    Label saveSearch = new Label("+ Save Search");

    /**
     * Close button.
     */
    Label closeButton = new Label("close");

    /**
     * Search duration.
     */
    Label searchDuration = new Label();

    /**
     * The title label.
     */
    private Label titleLbl = null;

    /**
     * The view.
     */
    private StreamView streamView;

    /**
     * In label.
     */
    private Label in = new Label(" in ");

    /**
     * View label.
     */
    private Label viewLbl = new Label("");

    /** Link to add a gadget for the displayed stream. */
    private Hyperlink addGadgetLink;

    /**
     * The mode of the list (needed for the "add gadget" link).
     */
    Mode mode = Mode.LIST;

    /**
     * The streamScope of the list (also needed for the "add gadget" link).
     */
    StreamScope streamScope;

    /**
     * Constructor.
     *
     * @param inActionProcessor
     *            the action processor.
     * @param title
     *            the title.
     * @param inView
     *            the view of the search.
     */
    public StreamSearchComposite(final ActionProcessor inActionProcessor, final String title, final StreamView inView)
    {
        streamView = inView;
        FlowPanel streamSearch = new FlowPanel();

        this.addStyleName("stream-search-composite");
        streamSearch.addStyleName("search-header search-list");

        InlineLabel fader = new InlineLabel();
        fader.addStyleName("stream-title-fader");
        streamSearch.add(fader);

        FlowPanel titleWrapper = new FlowPanel();
        titleWrapper.addStyleName("title-wrapper");

        titleLbl = new Label(title);
        titleLbl.addStyleName("title");
        titleWrapper.add(titleLbl);

        streamSearch.add(titleWrapper);

        addGadgetLink = new InlineHyperlink("create app", "");
        addGadgetLink.addStyleName("add-as-gadget");
        streamSearch.add(addGadgetLink);

        if (streamView != null)
        {
            setAddGadgetLink(null, null, null);
        }

        searchTerm.setTitle("search this stream");
        streamSearch.add(searchTerm);

        searchGo.addStyleName("search-list-button");
        streamSearch.add(searchGo);

        searchDescription.setVisible(false);
        searchDescription.addStyleName("search-description");

        closeButton.addStyleName("close");
        searchDescription.add(closeButton);

        saveSearch.addStyleName("save-search");
        searchDescription.add(saveSearch);

        Label searchResultsFor = new Label("Results for: ");
        searchResultsFor.addStyleName("search-results-for");

        searchDescription.add(searchResultsFor);
        searchTermLabel.addStyleName("search-term");
        viewLbl.addStyleName("search-term");
        in.addStyleName("search-results-for");
        searchDescription.add(searchTermLabel);
        searchDescription.add(in);
        searchDescription.add(viewLbl);
        searchDescription.add(searchDuration);

        in.setVisible(false);
        viewLbl.setVisible(false);

        this.add(streamSearch);
        this.add(searchDescription);

        searchGo.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                search();
            }
        });

        searchTerm.addKeyPressHandler(new KeyPressHandler()
        {
            public void onKeyPress(final KeyPressEvent event)
            {
                if (event.getCharCode() == KeyCodes.KEY_ENTER)
                {
                    search();
                }
            }
        });

        closeButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                onSearchCanceled();

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("streamSearch", null);

                Session.getInstance().getEventBus().notifyObservers(
                        new UpdateHistoryEvent(new CreateUrlRequest(params, false)));
            }
        });

        saveSearch.addClickListener(new ClickListener()
        {
            public void onClick(final Widget arg0)
            {
                DialogContent dialogContent =
                        new StreamSearchDialogContent(searchTerm.getText(), searchTerm.getText(), streamView);
                Dialog dialog = new Dialog(dialogContent);
                dialog.setBgVisible(true);
                dialog.center();
            }
        });

        Session.getInstance().getEventBus().addObserver(StreamSearchRequestMoreEvent.class,
                new Observer<StreamSearchRequestMoreEvent>()
                {
                    public void update(final StreamSearchRequestMoreEvent event)
                    {
                        onSearchUpdated();
                    }
                });

        Session.getInstance().getEventBus().addObserver(StreamReinitializeRequestEvent.class,
                new Observer<StreamReinitializeRequestEvent>()
                {
                    public void update(final StreamReinitializeRequestEvent event)
                    {
                        if (Session.getInstance().getParameterValue("streamSearch") == null)
                        {
                            onSearchCanceled();
                        }
                    }
                });

        Session.getInstance().getEventBus().addObserver(StreamSearchBeginEvent.class,
                new Observer<StreamSearchBeginEvent>()
                {
                    public void update(final StreamSearchBeginEvent event)
                    {
                        onSearchUpdated();
                        saveSearch.removeStyleName("saved");

                        saveSearch.setVisible((event.getSearch() == null));
                        closeButton.setVisible((event.getSearch() == null));
                        searchTerm.setVisible((event.getSearch() == null));
                        searchGo.setVisible((event.getSearch() == null));
                        in.setVisible((event.getSearch() != null));
                        viewLbl.setVisible((event.getSearch() != null));

                        // Hides the add gadget link when search results are shown but shows link
                        // when viewing a saved search from the activity page.
                        addGadgetLink.setVisible(!searchTerm.isVisible() || !closeButton.isVisible());

                        if (event.getSearch() != null)
                        {
                            viewLbl.setText(event.getSearch().getStreamView().getName());
                            titleLbl.setText(event.getSearch().getName());
                            streamView = event.getSearch().getStreamView();
                            setAddGadgetLink(event.getSearchText().replaceAll(" ", "+"), String.valueOf(event
                                    .getSearch().getId()), event.getSearch().getName());
                        }
                        mode = Mode.SEARCHING;
                    }
                });
    }

    /**
     * When the search potentially changes.
     */
    public void onSearchUpdated()
    {
        searchDescription.setVisible(true);
        searchTermLabel.setText(Session.getInstance().getParameterValue("streamSearch"));
        searchTerm.setText(Session.getInstance().getParameterValue("streamSearch"));
        searchTerm.checkBox();
        addGadgetLink.setVisible(false);
    }

    /**
     * Called when a search is cancelled.
     */
    public void onSearchCanceled()
    {
        searchTermLabel.setText("");
        searchTerm.setText("");
        searchTerm.checkBox();
        searchDescription.setVisible(false);
        addGadgetLink.setVisible(true);
    }

    /**
     * Search.
     */
    private void search()
    {
        if (searchTerm.getText() != "")
        {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("streamSearch", searchTerm.getText());
            params.put("viewId", Long.toString(streamView.getId()));

            Session.getInstance().getEventBus().notifyObservers(
                    new UpdateHistoryEvent(new CreateUrlRequest(params, false)));
        }
    }

    /**
     * Sets the view on a change.
     *
     * @param inView
     *            the view.
     */
    public void setStreamView(final StreamView inView)
    {
        streamView = inView;

        // a null stream scope is valid for custom filters since they don't have scope.
        streamScope = null;

        if (streamView != null)
        {
            if (streamView.getIncludedScopes() != null && streamView.getIncludedScopes().size() > 0)
            {
                streamScope = new ArrayList<StreamScope>(streamView.getIncludedScopes()).get(0);
            }

            setAddGadgetLink(streamScope == null ? null : streamScope.getUniqueKey(), null, null);
        }

        searchTerm.checkBox();
    }

    /**
     * Set the title text.
     *
     * @param title
     *            the text.
     */
    public void setTitleText(final String title)
    {
        titleLbl.setText(title);
        searchTerm.setVisible(true);
        searchGo.setVisible(true);
    }

    /**
     * Builds and sets the link for adding the stream as a gadget.
     *
     * @param inShortName
     *            the short name for the gadget link (or null if not applicable).
     * @param inSearchId
     *            the id of the stream search for the gadget link (or null if not applicable).
     * @param inSearchName
     *            the name of the stream search for the gadget link (or null if not applicable).
     */
    private void setAddGadgetLink(final String inShortName, final String inSearchId, final String inSearchName)
    {
        String filterType = "compositestream";
        // default to composite stream incase of null streamScope.
        String streamType = "compositestream";
        String filterId = String.valueOf(streamView.getId());
        String searchId = "";

        String gadgetTitle = streamView.getName();
        if (mode == Mode.SEARCHING || mode == Mode.SAVED_SEARCH)
        {
            filterType = "streamsearch";
            streamType = "streamsearch";
            gadgetTitle = inSearchName;
            searchId = filterId;
            filterId = inSearchId;
        }
        else if (streamScope != null && streamScope.getScopeType() == ScopeType.GROUP)
        {
            streamType = "groupstream";
        }
        else if (streamScope != null && streamScope.getScopeType() == ScopeType.PERSON)
        {
            streamType = "personstream";
        }
        else if (streamScope != null && streamScope.getScopeType() == ScopeType.ORGANIZATION)
        {
            streamType = "orgstream";
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("action", "addGadget");
        params.put("url", "{d7a58391-5375-4c76-b5fc-a431c42a7555}");
        params.put("prefs", "{\"filterId\":" + makeJsonString(filterId) + ",\"filterType\":"
                + makeJsonString(filterType) + ",\"streamType\":" + makeJsonString(streamType) + ",\"gadgetTitle\":"
                + makeJsonString(gadgetTitle) + ",\"searchId\":" + makeJsonString(searchId) + ",\"shortName\":"
                + makeJsonString(inShortName) + "}");
        String url = Session.getInstance().generateUrl(new CreateUrlRequest(Page.START, params));

        addGadgetLink.setTargetHistoryToken(url);
    }

    // TODO: We should have a utility class that takes a map of key-value pairs and builds the entire JSON object
    // representation for it, including handling the escaping.
    /**
     * Creates the JSON representation of a string value. (Escapes characters and adds string delimiters or returns null
     * keyword as applicable.) See http://www.json.org/ for syntax. Assumes the string contains no control characters.
     *
     * @param input
     *            Input string, possibly null.
     * @return JSON string representation.
     */
    private static native String makeJsonString(final String input) /*-{
           return input == null ? 'null' : '"' + input.replace(/\\/g,'\\\\').replace(/"/g,'\\"') + '"';
       }-*/;

    /**
     * Sets the mode.
     *
     * @param inMode
     *            the mode to set.
     */
    public void setMode(final Mode inMode)
    {
        mode = inMode;
    }
}
