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

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;

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
     * Recipient type key.
     */
    private static final String RECIPIENT_TYPE_KEY = "type";

    /**
     * Recipient unique ID key.
     */
    private static final String RECIPIENT_UNIQUE_ID_KEY = "name";

    /**
     * Recipient key.
     */
    private static final String RECIPIENT_KEY = "recipient";

    /**
     * Sort key.
     */
    private static final String FOLLOWED_BY_KEY = "followedBy";

    /**
     * Sort key.
     */
    private static final String PARENT_ORG_KEY = "parentOrg";

    /**
     * Sort key.
     */
    private static final String SAVED_KEY = "savedBy";

    /**
     * Everyone button.
     */
    private RadioButton everyone = new RadioButton("list", "Everyone");
    /**
     * Parent org button.
     */
    private RadioButton parentOrg = new RadioButton("list");
    /**
     * Following button.
     */
    private RadioButton following = new RadioButton("list", "Following");
    /**
     * My lists button.
     */
    private RadioButton myLists = new RadioButton("list");

    /**
     * My saved items.
     */
    private RadioButton starred = new RadioButton("list", "Saved");

    /**
     * The radio buttons.
     */
    private List<RadioButton> radioButtons = new ArrayList<RadioButton>();

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

        Session.getInstance().getEventBus().addObserver(StreamScopeAddedEvent.getEvent(),
                new Observer<StreamScopeAddedEvent>()
                {
                    public void update(final StreamScopeAddedEvent arg1)
                    {
                        myLists.setChecked(true);
                    }
                });

        this.addStyleName("stream-lists");
        label.addStyleName("form-label");
        myLists.addStyleName("my-lists");

        parentOrg.setText(Session.getInstance().getCurrentPerson().getParentOrganizationName());
        parentOrg.setFormValue("parentOrg");

        this.add(label);

        radioButtons.add(following);
        radioButtons.add(parentOrg);
        radioButtons.add(everyone);
        radioButtons.add(starred);
        radioButtons.add(myLists);

        this.add(following);
        this.add(parentOrg);
        this.add(everyone);
        this.add(starred);
        this.add(myLists);

        if (json == null)
        {
            following.setChecked(true);
        }
        else
        {
            if (json.containsKey(RECIPIENT_KEY))
            {
                Session.getInstance().getEventBus().addObserver(GotBulkEntityResponseEvent.class,
                        new Observer<GotBulkEntityResponseEvent>()
                        {
                            public void update(final GotBulkEntityResponseEvent event)
                            {
                                JSONArray recipientArray = json.get(RECIPIENT_KEY).isArray();

                                for (int i = 0; i < recipientArray.size(); i++)
                                {
                                    JSONObject recipient = (JSONObject) recipientArray.get(i);
                                    String uniqueId = recipient.get(RECIPIENT_UNIQUE_ID_KEY).isString().stringValue();
                                    String displayName = getEntityDisplayName(EntityType.valueOf(recipient.get(
                                            RECIPIENT_TYPE_KEY).isString().stringValue()), uniqueId, event
                                            .getResponse());

                                    ScopeType scopeType = ScopeType.valueOf(recipient.get(RECIPIENT_TYPE_KEY)
                                            .isString().stringValue());

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

                JSONArray recipientArray = json.get(RECIPIENT_KEY).isArray();
                for (int i = 0; i < recipientArray.size(); i++)
                {
                    JSONObject recipient = (JSONObject) recipientArray.get(i);
                    StreamEntityDTO entity = new StreamEntityDTO();
                    entity.setType(EntityType.valueOf(recipient.get(RECIPIENT_TYPE_KEY).isString().stringValue()));
                    entity.setUniqueIdentifier(recipient.get(RECIPIENT_UNIQUE_ID_KEY).isString().stringValue());
                    entities.add(entity);
                }
                BulkEntityModel.getInstance().fetch(entities, false);
                myLists.setChecked(true);
            }
            else if (json.containsKey(SAVED_KEY))
            {
                starred.setChecked(true);
            }
            else if (json.containsKey(PARENT_ORG_KEY))
            {
                parentOrg.setChecked(true);
            }
            else if (json.containsKey(FOLLOWED_BY_KEY))
            {
                following.setChecked(true);
            }
            else
            {
                everyone.setChecked(true);
            }
        }

        this.add(scopes);

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
    private String getEntityDisplayName(
            final EntityType type, final String accountId, final List<Serializable> entities)
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
        return "Name Unknown";
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
        if (myLists.isChecked())
        {
            return scopes.getValue();
        }
        else
        {
            for (RadioButton button : radioButtons)
            {
                if (button.isChecked())
                {
                    return button.getText();
                }
            }

        }
        return null;

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
