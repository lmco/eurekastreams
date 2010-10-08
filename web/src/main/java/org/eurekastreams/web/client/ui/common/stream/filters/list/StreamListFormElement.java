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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamScopeAddedEvent;
import org.eurekastreams.web.client.events.data.GotBulkEntityResponseEvent;
import org.eurekastreams.web.client.model.BulkEntityModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.form.elements.FormElement;
import org.eurekastreams.web.client.ui.common.form.elements.StreamScopeFormElement;
import org.eurekastreams.web.client.ui.common.stream.StreamJsonRequestFactory;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Form element for picking a list in a saved search.
 * 
 */
public class StreamListFormElement extends FlowPanel implements FormElement
{
    /**
     * The label.
     */
    private Label label = new Label("Stream");

    /**
     * Stream options.
     */
    private ListBox streamOptions = new ListBox();

    /**
     * Scopes form element.
     */
    private StreamScopeFormElement scopes;

    /**
     * Maximum name length.
     */
    private static final int MAX_NAME = 50;

    /**
     * Default constructor.
     * 
     * @param json
     *            the id of the default view.
     */
    public StreamListFormElement(final JSONObject json)
    {
        scopes = new StreamScopeFormElement("scopes", new LinkedList<StreamScope>(), "",
                "Enter the name of an employee or group stream.", false, true, "/resources/autocomplete/entities/",
                MAX_NAME);

        this.addStyleName("stream-lists");
        label.addStyleName("form-label");
        this.add(label);

        this.add(streamOptions);

        streamOptions.addItem("Everyone", "");
        streamOptions.addItem("Following", StreamJsonRequestFactory.FOLLOWED_BY_KEY);
        streamOptions.addItem(Session.getInstance().getCurrentPerson().getParentOrganizationName(),
                StreamJsonRequestFactory.PARENT_ORG_KEY);
        streamOptions.addItem("Saved", StreamJsonRequestFactory.SAVED_KEY);
        streamOptions.addItem("Groups I've Joined", StreamJsonRequestFactory.JOINED_GROUPS_KEY);
        streamOptions.addItem("Posted To", StreamJsonRequestFactory.RECIPIENT_KEY);
        streamOptions.addItem("Authored By", StreamJsonRequestFactory.AUTHOR_KEY);
        streamOptions.addItem("Liked By", StreamJsonRequestFactory.LIKER_KEY);

        streamOptions.addChangeHandler(new ChangeHandler()
        {
            public void onChange(final ChangeEvent event)
            {
                scopes.setVisible(hasStreamScopes(getSelected()));
            }
        });

        if (json == null)
        {
            streamOptions.setSelectedIndex(0);
            scopes.setVisible(false);
        }
        else
        {
            if (json.containsKey(StreamJsonRequestFactory.RECIPIENT_KEY))
            {
                setSelectedByValue(StreamJsonRequestFactory.RECIPIENT_KEY);
            }
            else if (json.containsKey(StreamJsonRequestFactory.SAVED_KEY))
            {
                setSelectedByValue(StreamJsonRequestFactory.SAVED_KEY);
            }
            else if (json.containsKey(StreamJsonRequestFactory.PARENT_ORG_KEY))
            {
                setSelectedByValue(StreamJsonRequestFactory.PARENT_ORG_KEY);
            }
            else if (json.containsKey(StreamJsonRequestFactory.FOLLOWED_BY_KEY))
            {
                setSelectedByValue(StreamJsonRequestFactory.FOLLOWED_BY_KEY);
            }
            else if (json.containsKey(StreamJsonRequestFactory.AUTHOR_KEY))
            {
                setSelectedByValue(StreamJsonRequestFactory.AUTHOR_KEY);
            }
            else if (json.containsKey(StreamJsonRequestFactory.LIKER_KEY))
            {
                setSelectedByValue(StreamJsonRequestFactory.LIKER_KEY);
            }
            else if (json.containsKey(StreamJsonRequestFactory.JOINED_GROUPS_KEY))
            {
                setSelectedByValue(StreamJsonRequestFactory.JOINED_GROUPS_KEY);
            }
            else
            {
                setSelectedByValue("");
            }

            if (hasStreamScopes(getSelected()))
            {
                Session.getInstance().getEventBus().addObserver(GotBulkEntityResponseEvent.class,
                        new Observer<GotBulkEntityResponseEvent>()
                        {
                            public void update(final GotBulkEntityResponseEvent event)
                            {
                                JSONArray recipientArray = json.get(getSelected()).isArray();

                                for (int i = 0; i < recipientArray.size(); i++)
                                {
                                    JSONObject recipient = (JSONObject) recipientArray.get(i);
                                    String uniqueId = recipient.get(StreamJsonRequestFactory.ENTITY_UNIQUE_ID_KEY)
                                            .isString().stringValue();
                                    String displayName = getEntityDisplayName(EntityType.valueOf(recipient.get(
                                            StreamJsonRequestFactory.ENTITY_TYPE_KEY).isString().stringValue()),
                                            uniqueId, event.getResponse());

                                    ScopeType scopeType = ScopeType.valueOf(recipient.get(
                                            StreamJsonRequestFactory.ENTITY_TYPE_KEY).isString().stringValue());

                                    StreamScope scope = new StreamScope(scopeType, uniqueId);
                                    scope.setDisplayName(displayName);

                                    Session.getInstance().getEventBus().notifyObservers(
                                            new StreamScopeAddedEvent(scope));
                                }

                                Session.getInstance().getEventBus().removeObserver(GotBulkEntityResponseEvent.class,
                                        this);
                            }
                        });

                ArrayList<StreamEntityDTO> entities = new ArrayList<StreamEntityDTO>();

                JSONArray recipientArray = json.get(getSelected()).isArray();
                for (int i = 0; i < recipientArray.size(); i++)
                {
                    JSONObject recipient = (JSONObject) recipientArray.get(i);
                    StreamEntityDTO entity = new StreamEntityDTO();
                    entity.setType(EntityType.valueOf(recipient.get(StreamJsonRequestFactory.ENTITY_TYPE_KEY)
                            .isString().stringValue()));
                    entity.setUniqueIdentifier(recipient.get(StreamJsonRequestFactory.ENTITY_UNIQUE_ID_KEY).isString()
                            .stringValue());
                    entities.add(entity);
                }
                BulkEntityModel.getInstance().fetch(entities, false);
            }
        }

        this.add(scopes);

    }

    /**
     * Get the selected item.
     * 
     * @return the selected item.
     */
    private String getSelected()
    {
        return streamOptions.getValue(streamOptions.getSelectedIndex());
    }

    /**
     * Determine if there are stream scopes to render.
     * 
     * @param selected
     *            the selected item.
     * @return if there are stream scopes to render.
     */
    private Boolean hasStreamScopes(final String selected)
    {
        return (selected.equals(StreamJsonRequestFactory.RECIPIENT_KEY)
                || selected.equals(StreamJsonRequestFactory.AUTHOR_KEY) || selected
                .equals(StreamJsonRequestFactory.LIKER_KEY));
    }

    /**
     * Set selected by value.
     * 
     * @param selectedValue
     *            the selected value.
     */
    private void setSelectedByValue(final String selectedValue)
    {
        for (int i = 0; i < streamOptions.getItemCount(); i++)
        {
            if (streamOptions.getValue(i).equals(selectedValue))
            {
                streamOptions.setSelectedIndex(i);
            }
        }

        scopes.setVisible(hasStreamScopes(selectedValue));
    }

    /**
     * Get the person.
     * 
     * @param type
     *            the type.
     * @param accountId
     *            account id.
     * @param entities
     *            the person.
     * @return the person.
     */
    private String getEntityDisplayName(final EntityType type, final String accountId, 
            final List<Serializable> entities)
    {
        for (Serializable entity : entities)
        {
            if (type.equals(EntityType.PERSON) && entity instanceof PersonModelView
                    && ((PersonModelView) entity).getUniqueId().equals(accountId))
            {
                return ((PersonModelView) entity).getDisplayName();
            }
            if (type.equals(EntityType.GROUP) && entity instanceof DomainGroupModelView
                    && ((DomainGroupModelView) entity).getUniqueId().equals(accountId))
            {
                return ((DomainGroupModelView) entity).getName();
            }
        }
        return null;
    }

    /**
     * Gets the key.
     * 
     * @return the key.
     */
    public String getKey()
    {
        return "stream";
    }

    /**
     * Gets the value.
     * 
     * @return the value.
     */
    public Serializable getValue()
    {
        String value = streamOptions.getValue(streamOptions.getSelectedIndex());

        JSONObject jsonObject = StreamJsonRequestFactory.getEmptyRequest();

        if (value.equals(StreamJsonRequestFactory.FOLLOWED_BY_KEY))
        {
            jsonObject = StreamJsonRequestFactory.setSourceAsFollowing(jsonObject);
        }
        else if (value.equals(StreamJsonRequestFactory.SAVED_KEY))
        {
            jsonObject = StreamJsonRequestFactory.setSourceAsSaved(jsonObject);
        }
        else if (value.equals(StreamJsonRequestFactory.PARENT_ORG_KEY))
        {
            jsonObject = StreamJsonRequestFactory.setSourceAsParentOrg(jsonObject);
        }
        else if (value.equals(StreamJsonRequestFactory.JOINED_GROUPS_KEY))
        {
            jsonObject = StreamJsonRequestFactory.setSourceAsJoinedGroups(jsonObject);
        }
        else if (value.equals(StreamJsonRequestFactory.RECIPIENT_KEY))
        {
            StreamJsonRequestFactory.initRecipient(jsonObject);

            for (StreamScope scope : (LinkedList<StreamScope>) scopes.getValue())
            {
                jsonObject = StreamJsonRequestFactory.addRecipient(EntityType.valueOf(scope.getScopeType().toString()),
                        scope.getUniqueKey(), jsonObject);
            }
        }
        else if (value.equals(StreamJsonRequestFactory.LIKER_KEY))
        {
            StreamJsonRequestFactory.initLikers(jsonObject);

            for (StreamScope scope : (LinkedList<StreamScope>) scopes.getValue())
            {
                jsonObject = StreamJsonRequestFactory.addLiker(EntityType.valueOf(scope.getScopeType().toString()),
                        scope.getUniqueKey(), jsonObject);
            }
        }
        else if (value.equals(StreamJsonRequestFactory.AUTHOR_KEY))
        {
            StreamJsonRequestFactory.initAuthors(jsonObject);

            for (StreamScope scope : (LinkedList<StreamScope>) scopes.getValue())
            {
                jsonObject = StreamJsonRequestFactory.addAuthor(EntityType.valueOf(scope.getScopeType().toString()),
                        scope.getUniqueKey(), jsonObject);
            }
        }

        return jsonObject.toString();
    }

    /**
     * Gets called if this element has an error.
     * 
     * @param errMessage
     *            the error Message.
     */
    public void onError(final String errMessage)
    {
        label.addStyleName("form-error");
    }

    /**
     * Gets called if this element was successful.
     */
    public void onSuccess()
    {
        label.removeStyleName("form-error");

    }
}
