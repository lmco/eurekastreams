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

import java.util.HashMap;
import java.util.Map.Entry;

import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;

import com.google.gwt.user.client.HistoryListener;

/**
 * The history state.
 */
public class HistoryState implements HistoryListener
{
    /**
     * The JSNI facade.
     */
    private WidgetJSNIFacadeImpl jsniFacade;

    /**
     * The values.
     */
    private HashMap<String, String> values = new HashMap<String, String>();

    /**
     * The view.
     */
    private String view = "";

    /**
     * Constructor.
     *
     * @param inJsniFacade
     *            the jsni facade.
     */
    public HistoryState(final WidgetJSNIFacadeImpl inJsniFacade)
    {
        jsniFacade = inJsniFacade;

        onHistoryChanged(jsniFacade.getHistoryToken());
        jsniFacade.addHistoryListener(this);

    }

    /**
     * Set the value of a history parameter.
     *
     * @param key
     *            the key to use.
     * @param value
     *            the value to use.
     * @param persist
     *            true to persist to the history token value.
     */
    public void setValue(final String key, final String value, final boolean persist)
    {
        values.put(key, value);

        if (persist)
        {
            persistToken();
        }
    }

    /**
     * Removes a history parameter.
     *
     * @param key
     *            the key to remove.
     * @param persist
     *            true to persist to the history token value.
     */
    public void removeValue(final String key, final boolean persist)
    {
        values.remove(key);
        if (persist)
        {
            persistToken();
        }
    }

    /**
     * Sets the view in the history state.
     *
     * @param inView
     *            the view to set.
     * @param resetValues
     *            if the history values should be reset.
     * @param persist
     *            true to persist to the history token value.
     */
    public void setView(final String inView, final boolean resetValues, final boolean persist)
    {
        view = inView;

        if (resetValues)
        {
            values.clear();
        }

        if (persist)
        {
            persistToken();
        }
    }

    /**
     * Gets the view.
     *
     * @return the view.
     */
    public String getView()
    {
        return jsniFacade.urlDecode(view);
    }

    /**
     * Persist to the history token.
     */
    private void persistToken()
    {
        String historyString = jsniFacade.urlEncode(view);

        String prefix = "?";

        for (Entry<String, String> entry : values.entrySet())
        {
            historyString += prefix + jsniFacade.urlEncode(entry.getKey()) + "="
                    + jsniFacade.urlEncode(entry.getValue());
            prefix = "&";
        }

        jsniFacade.setHistoryToken(historyString, true);
    }

    /**
     * Get the value from the history state..
     *
     * @param key
     *            the key to use.
     * @return the value.
     */
    public String getValue(final String key)
    {
        if (values.containsKey(key))
        {
            return values.get(key);
        }
        else
        {
            return "";
        }
    }

    /**
     * Called when the history changes.
     *
     * @param historyToken
     *            on the history token changed.
     */
    public void onHistoryChanged(final String historyToken)
    {
        values.clear();

        if (historyToken.contains("?"))
        {
            // ? position
            int questionMarkIndex = historyToken.indexOf("?") + 1;

            view = historyToken.substring(0, questionMarkIndex - 1);

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
            view = jsniFacade.urlDecode(historyToken);
        }
    }
}
