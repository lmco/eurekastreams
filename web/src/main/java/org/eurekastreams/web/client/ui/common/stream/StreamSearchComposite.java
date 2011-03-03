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
package org.eurekastreams.web.client.ui.common.stream;

import java.util.HashMap;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.stream.Stream;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.HideNotificationEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamSearchBeginEvent;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.LabeledTextBox;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.stream.filters.list.CustomStreamDialogContent;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

/**
 * Stream searching widget.
 */
public class StreamSearchComposite extends FlowPanel
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
     * Title wrapper.
     */
    FlowPanel titleWrapper = new FlowPanel();

    /**
     * Link for group stream titles.
     */
    Hyperlink titleLink = new Hyperlink();

    /**
     * In label.
     */
    private final Label in = new Label(" in ");

    /**
     * View label.
     */
    private final Label viewLbl = new Label("");

    /**
     * Last request.
     */
    private String lastRequest = "";

    /** Link to add a gadget for the displayed stream. */
    private final Hyperlink addGadgetLink;

    /**
     * Stream to URL transformer.
     */
    private final StreamToUrlTransformer streamUrlTransformer = new StreamToUrlTransformer();

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

        streamSearch.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchHeader());
        streamSearch.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchList());

        InlineLabel fader = new InlineLabel();
        fader.addStyleName(StaticResourceBundle.INSTANCE.coreCss().streamTitleFader());
        streamSearch.add(fader);

        titleWrapper.addStyleName(StaticResourceBundle.INSTANCE.coreCss().titleWrapper());

        titleLbl = new Label();
        titleLbl.addStyleName(StaticResourceBundle.INSTANCE.coreCss().title());
        titleWrapper.add(titleLbl);

        titleWrapper.add(titleLink);
        titleLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().title());
        titleLink.setVisible(false);

        streamSearch.add(titleWrapper);

        addGadgetLink = new InlineHyperlink("create app", "");
        addGadgetLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().addAsGadget());
        streamSearch.add(addGadgetLink);

        searchTerm.setTitle("search this stream");
        streamSearch.add(searchTerm);

        searchGo.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchListButton());
        streamSearch.add(searchGo);

        searchDescription.setVisible(false);
        searchDescription.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchDescription());

        closeButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().close());
        searchDescription.add(closeButton);

        saveSearch.addStyleName(StaticResourceBundle.INSTANCE.coreCss().saveSearch());
        searchDescription.add(saveSearch);

        Label searchResultsFor = new Label("Results for: ");
        searchResultsFor.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchResultsFor());

        searchDescription.add(searchResultsFor);
        searchTermLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchTerm());
        viewLbl.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchTerm());
        in.addStyleName(StaticResourceBundle.INSTANCE.coreCss().searchResultsFor());
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
            public void onClick(final ClickEvent arg0)
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
                EventBus.getInstance().notifyObservers(new StreamSearchBeginEvent(null));
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

        EventBus.getInstance().addObserver(GotStreamResponseEvent.class, new Observer<GotStreamResponseEvent>()
        {
            public void update(final GotStreamResponseEvent event)
            {
                // For the app's location, use the current URL minus a few parameters we know we don't want. (They are
                // used by other lists, but get left in the URL when switching tabs.)
                // We don't build the URL from the stream id, since that doesn't take search terms into account.
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("listId", null);
                params.put("listFilter", null);
                params.put("listSort", null);
                params.put("startIndex", null);
                params.put("endIndex", null);
                String url = Session.getInstance().generateUrl(new CreateUrlRequest(params));

                String search = Session.getInstance().getParameterValue("search");
                String stream = Session.getInstance().getParameterValue("streamId");

                addGadgetLink.setVisible(search == null);

                setAddGadgetLink(titleLbl.getText(), streamUrlTransformer.getUrl(stream, event.getJsonRequest()), url);
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
    }

    /**
     * Set the title text, generating a hyperlink for group stream titles.
     *
     * @param title
     *            the text.
     * @param shortName
     *            the short name key for this stream.
     * @param makeLink
     *            flag to indicate if the title should be a link.
     */
    public void setTitleText(final String title, final String shortName, final boolean makeLink)
    {
        titleLbl.setText(title);
        searchTerm.setVisible(true);
        searchGo.setVisible(true);

        if (makeLink)
        {
            String url = Session.getInstance().generateUrl(new CreateUrlRequest(Page.GROUPS, shortName));
            titleLink.setTargetHistoryToken(url);
            titleLink.setHTML(title);

            titleLbl.setVisible(false);
            titleLink.setVisible(true);
        }
        else
        {
            titleLbl.setVisible(true);
            titleLink.setVisible(false);
        }
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
     * @param location
     *            the location of the stream.
     */
    private void setAddGadgetLink(final String gadgetTitle, final String streamQuery, final String location)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("action", "addGadget");
        params.put("url", "{d7a58391-5375-4c76-b5fc-a431c42a7555}");
        params.put("prefs", "{\"streamQuery\":" + makeJsonString(streamQuery) + ",\"gadgetTitle\":"
                + makeJsonString(gadgetTitle) + ",\"streamLocation\":" + makeJsonString(location) + "}");
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
    private static native String makeJsonString(final String input)
    /*-{
         return input == null ? 'null' : '"' + input.replace(/\\/g,'\\\\').replace(/"/g,'\\"') + '"';
       }-*/;
}

