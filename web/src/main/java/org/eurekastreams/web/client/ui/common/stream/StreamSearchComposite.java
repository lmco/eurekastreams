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

import java.util.HashMap;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.HideNotificationEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamRequestEvent;
import org.eurekastreams.web.client.events.StreamSearchBeginEvent;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.LabeledTextBox;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.stream.filters.list.CustomStreamDialogContent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

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
    Label saveSearch = new Label("+ Save Stream");

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
     * In label.
     */
    private Label in = new Label(" in ");

    /**
     * View label.
     */
    private Label viewLbl = new Label("");

    /**
     * Last request.
     */
    private String lastRequest = "";

    /** Link to add a gadget for the displayed stream. */
    private Hyperlink addGadgetLink;

    /**
     * The mode of the list (needed for the "add gadget" link).
     */
    // Mode mode = Mode.LIST;
    /**
     * The streamScope of the list (also needed for the "add gadget" link).
     */
    StreamScope streamScope;

    /**
     * Constructor.
     */
    public StreamSearchComposite()
    {
        FlowPanel streamSearch = new FlowPanel();

        this.addStyleName("stream-search-composite");
        streamSearch.addStyleName("search-header search-list");

        InlineLabel fader = new InlineLabel();
        fader.addStyleName("stream-title-fader");
        streamSearch.add(fader);

        FlowPanel titleWrapper = new FlowPanel();
        titleWrapper.addStyleName("title-wrapper");

        titleLbl = new Label();
        titleLbl.addStyleName("title");
        titleWrapper.add(titleLbl);

        streamSearch.add(titleWrapper);

        addGadgetLink = new InlineHyperlink("create app", "");
        addGadgetLink.addStyleName("add-as-gadget");
        streamSearch.add(addGadgetLink);

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
                onSearch();
            }
        });

        searchTerm.addKeyPressHandler(new KeyPressHandler()
        {
            public void onKeyPress(final KeyPressEvent event)
            {
                if (event.getCharCode() == KeyCodes.KEY_ENTER)
                {
                    onSearch();
                }
            }
        });

        saveSearch.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent arg0)
            {
                Stream newStream = new Stream();
                newStream.setRequest(lastRequest);
                
                Session.getInstance().getEventBus().notifyObservers(new HideNotificationEvent());
                CustomStreamDialogContent dialogContent = new CustomStreamDialogContent(newStream);
                Dialog dialog = new Dialog(dialogContent);
                dialog.setBgVisible(true);
                dialog.center();
            }
        });

        closeButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                EventBus.getInstance().notifyObservers(new StreamSearchBeginEvent(""));
                onSearchCanceled();
            }
        });

        EventBus.getInstance().addObserver(GotStreamResponseEvent.class, new Observer<GotStreamResponseEvent>()
        {
            public void update(final GotStreamResponseEvent event)
            {
                lastRequest = event.getJsonRequest();
            }
        });

        EventBus.getInstance().addObserver(StreamRequestEvent.class, new Observer<StreamRequestEvent>()
        {
            public void update(final StreamRequestEvent event)
            {
                if (event.getStreamId() == null)
                {
                    JSONObject json = JSONParser.parse(event.getJson()).isObject();

                    String queryString = "";
                    JSONObject query = json.get("query").isObject();

                    for (String key : query.keySet())
                    {
                        queryString += key + "/";
                        if (null != query.get(key).isArray())
                        {

                            JSONArray entArr = query.get(key).isArray();
                            for (int i = 0; i < entArr.size(); i++)
                            {
                                JSONObject entity = entArr.get(i).isObject();

                                if (i != 0)
                                {
                                    queryString += ",";
                                }

                                queryString += entity.get("type").isString().stringValue() + ":"
                                        + entity.get("name").isString().stringValue();
                            }
                        }
                        else
                        {
                            queryString += query.get(key).isString().stringValue() + "/";
                        }
                    }

                    setAddGadgetLink(event.getStreamName(), "query/" + queryString);
                }
                else
                {
                    setAddGadgetLink(event.getStreamName(), "saved/" + event.getStreamId());
                }
            }
        });
    }

    /**
     * When the search potentially changes.
     */
    private void onSearch()
    {
        EventBus.getInstance().notifyObservers(new StreamSearchBeginEvent(searchTerm.getText()));
    }

    /**
     * Update search widget.
     * 
     * @param inSearchTerm
     *            the search term.
     */
    public void setSearchTerm(final String inSearchTerm)
    {
        searchTerm.setText(inSearchTerm);
        searchDescription.setVisible(true);
        searchTermLabel.setText(searchTerm.getText());
        searchTerm.setText(searchTerm.getText());
        searchTerm.checkBox();
        addGadgetLink.setVisible(false);
    }

    /**
     * Called when a search is canceled.
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
     * Sets if the search can be changed.
     * 
     * @param canChange
     *            if the search can be changed.
     */
    public void setCanChange(final boolean canChange)
    {
        closeButton.setVisible(canChange);
        saveSearch.setVisible(canChange);
        searchTerm.setVisible(canChange);
        searchGo.setVisible(canChange);
    }

    /**
     * Builds and sets the link for adding the stream as a gadget.
     * 
     * @param gadgetTitle
     *            the gadget title.
     * @param streamQuery
     *            the stream query.
     */
    private void setAddGadgetLink(final String gadgetTitle, final String streamQuery)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("action", "addGadget");
        params.put("url", "{d7a58391-5375-4c76-b5fc-a431c42a7555}");
        params.put("prefs", "{\"streamQuery\":" + makeJsonString(streamQuery) + ",\"gadgetTitle\":"
                + makeJsonString(gadgetTitle) + "}");
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
}
