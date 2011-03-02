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
package org.eurekastreams.web.client.ui.common.form.elements;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamScopeAddedEvent;
import org.eurekastreams.web.client.events.StreamScopeDeletedEvent;
import org.eurekastreams.web.client.ui.common.autocomplete.AutoCompleteEntityDropDownPanel;
import org.eurekastreams.web.client.ui.common.stream.filters.list.StreamScopeListPanel;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Form element for stream scopes.
 *
 */
public class StreamScopeFormElement extends FlowPanel implements FormElement
{
    /**
     * The label.
     */
    private Label listMembers;
    /**
     * The scope list panel.
     */
    private StreamScopeListPanel scopeListPanel;

    /**
     * The key.
     *
     * @return the key.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * The form element's key/id.
     */
    private String key;

    /**
     * Flag to allow multiple stream scopes to be selected.
     */
    private boolean allowMultiple;
    
    /**
     * Maximum number of items allowed to be added if allowMultiple is set to true.
     */
    private int maxItems;
    
    /**
     * The value.
     *
     * @return the value.
     */
    public Serializable getValue()
    {
        if (allowMultiple)
        {
            return scopeListPanel.getScopes();
        }
        else
        {
            return scopeListPanel.getScopes().size() > 0 ? scopeListPanel.getScopes().get(0).getUniqueKey() : "";
        }
    }

    /**
     * Default constructor.
     *
     * @param inKey
     *            the key name for the element.
     * @param inScopes
     *            the scopes.
     * @param inTitle
     *            the form element title.
     * @param inInstructions
     *            the instructions.
     * @param isRequired
     *            is the element required.
     * @param inAllowMultiple
     *            does this element allow multiple scopes to be selected.
     * @param inAutoCompleteUrl
     *            the url used to retrieve search results for the autocomplete box.
     * @param inMaxLength
     *            the maximum characters for the autocomplete textbox.
     * @param inMaxItems
     *            the maximum scopes allowed to be added - ignored if inAllowMultiple is false.
     */
    public StreamScopeFormElement(final String inKey, final LinkedList<StreamScope> inScopes, final String inTitle,
            final String inInstructions, final boolean isRequired, final boolean inAllowMultiple,
            final String inAutoCompleteUrl, final int inMaxLength, final int inMaxItems)
    {
        key = inKey;
        allowMultiple = inAllowMultiple;
        maxItems = inMaxItems;
        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().scopeFormElement());

        listMembers = new Label(inTitle);
        listMembers.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formLabel());

        final Label instructions = new Label(inInstructions);
        instructions.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formInstructions());
        
        final Set<String> uniqueKeys = new HashSet<String>();
        for (StreamScope scope : inScopes)
        {
            uniqueKeys.add(scope.getUniqueKey());
        }

        scopeListPanel = new StreamScopeListPanel(inScopes);

        final AutoCompleteEntityDropDownPanel autoComplete = new AutoCompleteEntityDropDownPanel(inAutoCompleteUrl);
        autoComplete.setMaxLength(inMaxLength);
        autoComplete.setOnItemSelectedCommand(new AutoCompleteEntityDropDownPanel.OnItemSelectedCommand()
        {
            public void itemSelected(final JavaScriptObject obj)
            {
                if (!getEntityType(obj.toString()).equals("NOTSET"))
                {
                    EventBus.getInstance().notifyObservers(
                            new StreamScopeAddedEvent(new StreamScope(getDisplayName(obj.toString()), ScopeType
                                    .valueOf(getEntityType(obj.toString())), getUniqueId(obj.toString()), Long
                                    .parseLong(getStreamScopeId(obj.toString())))));
                }
            }
        });
        
        EventBus.getInstance().addObserver(StreamScopeAddedEvent.getEvent(), new Observer<StreamScopeAddedEvent>()
        {
            public void update(final StreamScopeAddedEvent obj)
            {
                uniqueKeys.add(obj.getScope().getUniqueKey());
                autoComplete.clearText();

                if (!allowMultiple || uniqueKeys.size() >= maxItems)
                {
                    autoComplete.setVisible(false);
                    instructions.setVisible(false);
                }
            }
        });

        EventBus.getInstance().addObserver(StreamScopeDeletedEvent.getEvent(), new Observer<StreamScopeDeletedEvent>()
        {
            public void update(final StreamScopeDeletedEvent obj)
            {
                uniqueKeys.remove(obj.getScope().getUniqueKey());
                if (!allowMultiple || uniqueKeys.size() < maxItems)
                {
                    autoComplete.setVisible(true);
                    instructions.setVisible(true);
                }
            }
        });

        if ((!allowMultiple && !inScopes.isEmpty()) || uniqueKeys.size() >= maxItems)
        {
            autoComplete.setVisible(false);
            instructions.setVisible(false);
        }

        this.add(listMembers);
        this.add(scopeListPanel);
        this.add(autoComplete);
        if (isRequired)
        {
            Label requiredLabel = new Label("(required)");
            requiredLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().requiredFormLabel());
            this.add(requiredLabel);
        }

        this.add(instructions);
    }

    /**
     * Gets the display name of the JSON object.
     *
     * @param jsObj
     *            the JSON object.
     * @return the display name.
     */
    private native String getDisplayName(final String jsObj) /*-{
           var jsArray = jsObj.split(",");

           var ret = "";

           for(var i=0;i<jsArray.length-3;i++)
           {
               ret = ret + jsArray[i];
               if (i != jsArray.length-4)
               {
                   ret = ret + ",";
               }
           }
           return ret;
       }-*/;

    /**
     * Gets the entity type of the JSON object.
     *
     * @param jsObj
     *            the JSON object.
     * @return the entity type.
     */
    private native String getEntityType(final String jsObj) /*-{
       var jsArray = jsObj.split(",");
       return jsArray[jsArray.length-3];
    }-*/;

    /**
     * Gets the unique id of the JSON object.
     *
     * @param jsObj
     *            the JSON object.
     * @return the unique id.
     */
    private native String getUniqueId(final String jsObj) /*-{
       var jsArray = jsObj.split(",");
       return jsArray[jsArray.length-2];
    }-*/;

    /**
     * Gets the StreamScope id of the JSON object.
     *
     * @param jsObj
     *            the JSON object.
     * @return the StreamScope id.
     */
    private native String getStreamScopeId(final String jsObj) /*-{
       var jsArray = jsObj.split(",");
       return jsArray[jsArray.length-1];
    }-*/;

    /**
     * Gets called if this element has an error.
     *
     * @param errMessage
     *            the error Message.
     */
    public void onError(final String errMessage)
    {
        listMembers.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formError());
    }

    /**
     * Gets called if this element was successful.
     */
    public void onSuccess()
    {
        listMembers.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().formError());
    }

}
