/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Widget to provide an "add app" button on a stream.
 */
public class StreamAddAppWidget extends Composite
{
    /** Stream to URL transformer. */
    private static final StreamToUrlTransformer STREAM_URL_TRANSFORMER = new StreamToUrlTransformer();

    /** Link to add a gadget for the displayed stream. */
    private final Hyperlink addGadgetLink;

    /** Wrapper for add gadget link. */
    private final FlowPanel addGadgetLinkWrapper = new FlowPanel();

    /** Text to display on app as stream title. */
    private String titleText;

    /**
     * Constructor.
     */
    public StreamAddAppWidget()
    {
        addGadgetLink = new InlineHyperlink("create app", "");
        addGadgetLinkWrapper.addStyleName(StaticResourceBundle.INSTANCE.coreCss().addAsGadget());
        addGadgetLinkWrapper.add(new SimplePanel()); // to hold image
        addGadgetLinkWrapper.add(addGadgetLink);

        initWidget(addGadgetLinkWrapper);

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

                addGadgetLinkWrapper.setVisible(search == null);

                setAddGadgetLink(titleText, STREAM_URL_TRANSFORMER.getUrl(stream, event.getJsonRequest()), url);
            }
        });
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

    /**
     * Sets the stream title.
     *
     * @param inTitleText
     *            The stream title.
     */
    public void setStreamTitle(final String inTitleText)
    {
        titleText = inTitleText;
    }
}
