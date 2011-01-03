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
package org.eurekastreams.web.client.ui.common.stream.filters.list;

import java.util.LinkedList;

import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamScopeAddedEvent;
import org.eurekastreams.web.client.events.StreamScopeDeletedEvent;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Panel for a list of stream scopes.
 *
 */
public class StreamScopeListPanel extends FlowPanel
{
    /**
     * The scopes.
     */
    private LinkedList<StreamScope> scopes;


    /**
     * Default constructor.
     * @param inScopes the scopes.
     */
    public StreamScopeListPanel(final LinkedList<StreamScope> inScopes)
    {
        this.setVisible(false);
        this.addStyleName("stream-scope-container");
        this.getElement().setAttribute("id", "stream-scope-container");
        scopes = inScopes;
        render();

        EventBus.getInstance().addObserver(StreamScopeDeletedEvent.getEvent(),
                new Observer<StreamScopeDeletedEvent>()
                {
                    public void update(final StreamScopeDeletedEvent arg1)
                    {
                        int index = 0;
                        for (StreamScope scope : scopes)
                        {
                            if (scope.getUniqueKey().equals(
                                    arg1.getScope().getUniqueKey()))
                            {
                                scopes.remove(index);
                                render();
                                break;
                            }
                            index++;
                        }
                    }
                });

        EventBus.getInstance().addObserver(StreamScopeAddedEvent.getEvent(),
                new Observer<StreamScopeAddedEvent>()
                {
                    public void update(final StreamScopeAddedEvent arg1)
                    {
                    	boolean scopeFound = false;

                    	for (StreamScope scope : scopes)
                    	{
                    		if (scope.getUniqueKey().equals(arg1.getScope().getUniqueKey())
                    				&& scope.getScopeType().equals(arg1.getScope().getScopeType()))
                    		{
                    			scopeFound = true;
                    		}
                    	}

                    	if (!scopeFound)
                    	{
                    		scopes.add(arg1.getScope());
                        	render();
                        	scrollToBottom();
                    	}
                    }
                });
    }

    /**
     * JSNI method to scroll to the bottom of a div.
     */
    private static native void scrollToBottom()
    /*-{
            var objDiv = $doc.getElementById("stream-scope-container");
            objDiv.scrollTop = objDiv.scrollHeight;
    }-*/;

    /**
     * Gets the scopes.
     * @return the scopes.
     */
    public LinkedList<StreamScope> getScopes()
    {
        return scopes;
    }

    /**
     * Renders the scopes.
     */
    private void render()
    {
        this.clear();

        this.setVisible(scopes.size() > 0);
        for (StreamScope scope : scopes)
        {
            StreamScopePanel panel = new StreamScopePanel(scope);
            this.add(panel);
        }
    }
}
