/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotAllPopularHashTagsResponseEvent;
import org.eurekastreams.web.client.model.AllPopularHashTagsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.autocomplete.AutoCompleteDropDownPanel;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.KeyUpHandler;

/**
 * Post top panel.
 *
 */
public class PostToStreamTextboxPanel extends AutoCompleteDropDownPanel
{
    /**
     * Hashtag array.
     */
    private JsArrayString hashTagArray;
    /**
     * Default constructor.
     */
    public PostToStreamTextboxPanel()
    {
        super("", ElementType.TEXTAREA);
    }

    /**
     * Adds a handler for keypresses.
     * 
     * @param handler
     *            the handler.
     */
    public void addKeystrokeHandler(final KeyUpHandler handler)
    {
        getTextArea().addKeyUpHandler(handler);
    }

    /**
     * Sets up the auto complete.
     *
     * @param taId
     *            the text area id.
     * @param acdId
     *            the auto complete area id.
     * @param url
     *            the url.
     */
    @Override
    protected void setUpAutoComplete(final String taId,
            final String acdId, final String url)
    {

        Session.getInstance().getEventBus().addObserver(GotAllPopularHashTagsResponseEvent.class,
                new Observer<GotAllPopularHashTagsResponseEvent>()
                {
                    public void update(final GotAllPopularHashTagsResponseEvent arg1)
                    {
                        hashTagArray = (JsArrayString) JsArrayString.createArray();
                        int i = 0;
                        for (String hashtag : arg1.getResponse())
                        {
                            hashTagArray.set(i, hashtag);
                            i++;
                        }
                    }
                });
        AllPopularHashTagsModel.getInstance().fetch(null, true);
        setUpAutoCompleteJSON(taId, acdId, url, "", " ", hashTagArray);
    }

    /**
     * Sets up the auto complete.
     *
     * @param taId
     *            the text area id.
     * @param acdId
     *            the auto complete area id.
     * @param url
     *            the url.
     * @param inResultsListName
     *            the name of the results list returned.
     *  @param inDelimiter
     *            the item delimiter string.
     *            @param hashTagArray the hash tag array.
     */
    private static native void setUpAutoCompleteJSON(final String taId,
            final String acdId, final String url, final String inResultsListName,
            final String inDelimiter, final JsArrayString hashTagArray)
    /*-{
       var autocompleteConfig={
          config:{
              delimiter:inDelimiter,
              textAreaId:taId,
                 autoCompleteDiv:acdId
          }
       };

       var oDS;
       var myAutoComp;

       oDS = new $wnd.YAHOO.util.LocalDataSource(hashTagArray);

       myAutoComp =new $wnd.YAHOO.widget.AutoComplete(autocompleteConfig.config.textAreaId,
           autocompleteConfig.config.autoCompleteDiv, oDS);
       myAutoComp.queryDelay=.2;

       myAutoComp.generateRequest=function(sQuery){return sQuery+"/";};
       myAutoComp.delimChar=autocompleteConfig.config.delimiter;

       if (myAutoComp.itemSelectEvent != null)
       {
           myAutoComp.itemSelectEvent.subscribe(function(sType, sArgs) {
            @org.eurekastreams.web.client.ui.common.stream.PostToStreamTextboxPanel::onItemSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(sArgs[2]);
           });
       }
    }-*/;

}
