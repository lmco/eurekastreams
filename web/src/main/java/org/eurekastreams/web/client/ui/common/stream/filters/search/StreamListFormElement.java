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
package org.eurekastreams.web.client.ui.common.stream.filters.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.server.domain.stream.GroupStreamDTO;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.domain.stream.StreamView.Type;
import org.eurekastreams.web.client.ui.common.form.elements.FormElement;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
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
     * Everyone button.
     */
    private RadioButton everyone = new RadioButton("list", "Everyone");
    /**
     * Parent org button.
     */
    private RadioButton parentOrg;
    /**
     * Following button.
     */
    private RadioButton following = new RadioButton("list", "Following");
    /**
     * My lists button.
     */
    private RadioButton myLists = new RadioButton("list", "My Lists & Groups");

    /**
     * My saved items.
     */
    private RadioButton starred = new RadioButton("list", "Saved");

    /**
     * Optional radio button to show if search was created from org/group/personal profile stream.
     */
    private RadioButton fromStream;

    /**
     * My lists drop down.
     */
    private ListBox lists = new ListBox();
    /**
     * everyone id.
     */
    private Long everyoneId;
    /**
     * parent org id.
     */
    private Long parentOrgId;
    /**
     * following id.
     */
    private Long followingId;

    /**
     * Starred id.
     */
    private Long starredId;

    /**
     * Default radio buttons.
     */
    private HashMap<Long, RadioButton> defaultButtons = new HashMap<Long, RadioButton>();

    /**
     * String constant for prefix of fromStream radio button.
     */
    private static final String FROM_STREAM_PREFIX = "Stream: ";

    /**
     * Default constructor.
     * 
     * @param inViews
     *            the views.
     * @param defaultViewId
     *            the id of the default view.
     * @param defaultViewName
     *            the name of the default view.
     */
    public StreamListFormElement(final List<StreamFilter> inViews, final Long defaultViewId,
            final String defaultViewName)
    {
        // sorts the list of views by name
        ArrayList<StreamFilter> sortedViews = new ArrayList<StreamFilter>(inViews);
        Collections.sort(sortedViews, new Comparator<StreamFilter>()
        {
            public int compare(final StreamFilter f1, final StreamFilter f2)
            {
                return f1.getName().compareTo(f2.getName());
            }
        });

        this.addStyleName("stream-lists");
        label.addStyleName("form-label");
        myLists.addStyleName("my-lists");
        int dropDownIndex = 0;
        boolean isChecked = false;

        for (int i = 0; i < sortedViews.size(); i++)
        {
            StreamFilter filter = sortedViews.get(i);

            if (filter.getClass() == StreamView.class && ((StreamView) filter).getType().equals(Type.EVERYONE))
            {
                everyoneId = filter.getId();
                everyone.setFormValue(String.valueOf(filter.getId()));
                if (new Long(filter.getId()).equals(defaultViewId))
                {
                    everyone.setChecked(true);
                    isChecked = true;
                }
            }
            else if (filter.getClass() == StreamView.class && ((StreamView) filter).getType().equals(Type.PEOPLEFOLLOW))
            {
                followingId = filter.getId();
                following.setFormValue(String.valueOf(filter.getId()));
                if (new Long(filter.getId()).equals(defaultViewId))
                {
                    following.setChecked(true);
                    isChecked = true;
                }
            }
            else if (filter.getClass() == StreamView.class && ((StreamView) filter).getType().equals(Type.PARENTORG))
            {
                parentOrg = new RadioButton("list", filter.getName());
                parentOrgId = filter.getId();
                parentOrg.setFormValue(String.valueOf(filter.getId()));
                if (new Long(filter.getId()).equals(defaultViewId))
                {
                    parentOrg.setChecked(true);
                    isChecked = true;
                }
            }
            else if (filter.getClass() == StreamView.class && ((StreamView) filter).getType().equals(Type.STARRED))
            {
                starredId = filter.getId();
                starred.setFormValue(String.valueOf(filter.getId()));
                if (new Long(filter.getId()).equals(defaultViewId))
                {
                    starred.setChecked(true);
                    isChecked = true;
                }
            }
            else if (filter.getClass() == GroupStreamDTO.class)
            {
                lists.addItem(filter.getName(), String.valueOf(((GroupStreamDTO) filter).getStreamView().getId()));

                if (new Long(((GroupStreamDTO) filter).getStreamId()).equals(defaultViewId))
                {
                    myLists.setChecked(true);
                    isChecked = true;
                    lists.setSelectedIndex(dropDownIndex);
                }
                dropDownIndex++;
            }
            else
            {
                lists.addItem(filter.getName(), String.valueOf(filter.getId()));
                if (new Long(filter.getId()).equals(defaultViewId))
                {
                    myLists.setChecked(true);
                    isChecked = true;
                    lists.setSelectedIndex(dropDownIndex);
                }
                dropDownIndex++;
            }
        }

        defaultButtons.put(followingId, following);
        defaultButtons.put(parentOrgId, parentOrg);
        defaultButtons.put(everyoneId, everyone);
        defaultButtons.put(starredId, starred);

        this.add(label);

        this.add(following);
        this.add(parentOrg);
        this.add(everyone);
        this.add(starred);

        // auto-selects the radio button for lists/groups when an item is selected from the list
        lists.addChangeHandler(new ChangeHandler()
        {
            public void onChange(final ChangeEvent event)
            {
                myLists.setChecked(true);
            }
        });

        this.add(myLists);
        this.add(lists);

        if (lists.getItemCount() == 0)
        {
            myLists.setVisible(false);
            lists.setVisible(false);
        }

        if (defaultViewId == null)
        {
            everyone.setChecked(true);
        }
        else if (defaultViewId != null && !isChecked)
        {
            fromStream = new RadioButton("list", FROM_STREAM_PREFIX + defaultViewName);
            fromStream.setFormValue(String.valueOf(defaultViewId));
            defaultButtons.put(defaultViewId, fromStream);
            fromStream.setChecked(true);
            this.add(fromStream);
        }

    }

    /**
     * Gets the key.
     * 
     * @return the key.
     */
    public String getKey()
    {
        return "streamViewId";
    }

    /**
     * Get Stream View name.
     * 
     * @return the name,
     */
    public String getStreamViewName()
    {
        for (RadioButton button : defaultButtons.values())
        {
            if (button.isChecked())
            {
                return button.getText().replace(FROM_STREAM_PREFIX, "");
            }
        }

        return lists.getItemText((lists.getSelectedIndex()));

    }

    /**
     * Gets the value.
     * 
     * @return the value.
     */
    public Serializable getValue()
    {
        for (RadioButton button : defaultButtons.values())
        {
            if (button.isChecked())
            {
                return Long.valueOf(button.getFormValue());
            }
        }
        return Long.valueOf(lists.getValue(lists.getSelectedIndex()));

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
