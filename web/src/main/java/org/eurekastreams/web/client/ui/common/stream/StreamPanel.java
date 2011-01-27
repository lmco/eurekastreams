/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.web.client.events.ChangeActivityModeEvent;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.MessageStreamUpdateEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.StreamReinitializeRequestEvent;
import org.eurekastreams.web.client.events.StreamRequestEvent;
import org.eurekastreams.web.client.events.StreamRequestMoreEvent;
import org.eurekastreams.web.client.events.StreamSearchBeginEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.events.data.DeletedActivityResponseEvent;
import org.eurekastreams.web.client.events.data.GotActivityResponseEvent;
import org.eurekastreams.web.client.events.data.GotStreamResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.ActivityModel;
import org.eurekastreams.web.client.model.StreamModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Page to test advanced search features.
 */
public class StreamPanel extends FlowPanel
{
    /**
     * ID of last seen activity.
     */
    private long lastSeenId = 0L;

    /**
     * Error label.
     */
    private Label error = new Label("");

    /**
     * Stream panel.
     */
    private StreamListPanel stream = null;

    /**
     * JSON Query.
     */
    private String jsonQuery = "";

    /**
     * Panel for posting contents.
     */
    private FlowPanel shadowPanel = new FlowPanel();

    /**
     * The post widget.
     */
    private PostToStreamComposite postComposite = null;

    /**
     * Stream search.
     */
    private StreamSearchComposite streamSearch;

    /**
     * The stream name.
     */
    private String streamName = "";

    /**
     * Posting disabled panel.
     */
    private FlowPanel postingDisabled = new FlowPanel();

    /**
     * Post content panel.
     */
    private FlowPanel postContent = new FlowPanel();

    /**
     * Activity Detail Panel.
     */
    private FlowPanel activityDetailPanel = new FlowPanel();

    /**
     * Activity ID.
     */
    private Long activityId = 0L;

    /**
     * Stream ID.
     */
    private Long streamId = 0L;
    
    /**
     * Group ID.
     */
    private Long groupId = 0L;

    /**
     * Search string.
     */
    private String search = "";

    /**
     * Sort value.
     */
    private String sort = "";

    /**
     * Sort panel.
     */
    private StreamSortPanel sortPanel = new StreamSortPanel();

    /**
     * Panel to hold a message when the subject is locked.
     */
    private FlowPanel lockedMessage = new FlowPanel();

    /**
     * Initialize page.
     * 
     * @param showRecipients
     *            if recipients should be shown.
     */
    public StreamPanel(final Boolean showRecipients)
    {
        this.addStyleName("layout-container");

        stream = new StreamListPanel(new StreamMessageItemRenderer(showRecipients));
        stream.addStyleName("stream");
        stream.setVisible(false);

        shadowPanel.addStyleName("post-to-stream-container");
        shadowPanel.setVisible(false);

        streamSearch = new StreamSearchComposite();

        lockedMessage.setVisible(false);
        error.setVisible(false);

        postContent.add(shadowPanel);
        this.add(postContent);
        this.add(streamSearch);
        this.add(new UnseenActivityNotificationPanel());
        this.add(sortPanel);
        this.add(error);
        this.add(lockedMessage);
        this.add(stream);
        this.add(activityDetailPanel);

        stream.reinitialize();

        EventBus.getInstance().addObserver(UpdatedHistoryParametersEvent.class,
                new Observer<UpdatedHistoryParametersEvent>()
                {
                    public void update(final UpdatedHistoryParametersEvent event)
                    {
                        checkHistory(event.getParameters());

                        // Only process this once.
                        EventBus.getInstance().removeObserver(UpdatedHistoryParametersEvent.class, this);
                    }
                }, true);

        EventBus.getInstance().addObserver(UpdatedHistoryParametersEvent.class,
                new Observer<UpdatedHistoryParametersEvent>()
                {
                    public void update(final UpdatedHistoryParametersEvent event)
                    {
                        if (checkHistory(event.getParameters()))
                        {
                            EventBus.getInstance().notifyObservers(StreamReinitializeRequestEvent.getEvent());
                        }
                    }
                });

        EventBus.getInstance().addObserver(GotActivityResponseEvent.class, new Observer<GotActivityResponseEvent>()
        {
            public void update(final GotActivityResponseEvent event)
            {
                setSingleActivityMode();
                activityDetailPanel.clear();
                activityDetailPanel.add(new ActivityDetailPanel(event.getResponse(), showRecipients));
            }
        });

        EventBus.getInstance().addObserver(StreamRequestMoreEvent.class, new Observer<StreamRequestMoreEvent>()
        {
            public void update(final StreamRequestMoreEvent arg1)
            {
                JSONObject jsonObj = StreamJsonRequestFactory.getJSONRequest(jsonQuery);
                jsonObj = StreamJsonRequestFactory.setMaxId(lastSeenId, jsonObj);

                // Must be sorted by date to request more.
                jsonObj = StreamJsonRequestFactory.setSort("date", jsonObj);

                StreamModel.getInstance().fetch(jsonObj.toString(), false);
            }
        });

        EventBus.getInstance().addObserver(GotStreamResponseEvent.class, new Observer<GotStreamResponseEvent>()
        {
            public void update(final GotStreamResponseEvent event)
            {
                PagedSet<ActivityDTO> activity = event.getStream();

                int numberOfActivities = activity.getPagedSet().size();

                if (numberOfActivities > 0)
                {
                    lastSeenId = activity.getPagedSet().get(numberOfActivities - 1).getId();
                }

                MessageStreamUpdateEvent updateEvent = new MessageStreamUpdateEvent(activity);
                updateEvent.setMoreResults(activity.getTotal() > activity.getPagedSet().size());

                error.setText("");
                error.setVisible(false);
                EventBus.getInstance().notifyObservers(updateEvent);
                stream.setVisible(true);
            }
        });

        EventBus.getInstance().addObserver(StreamReinitializeRequestEvent.class,
                new Observer<StreamReinitializeRequestEvent>()
                {
                    public void update(final StreamReinitializeRequestEvent event)
                    {
                        EventBus.getInstance().notifyObservers(new StreamRequestEvent(streamName, jsonQuery, true));
                    }
                });

        EventBus.getInstance().addObserver(MessageStreamAppendEvent.class, new Observer<MessageStreamAppendEvent>()
        {
            public void update(final MessageStreamAppendEvent evt)
            {
                if ("date".equals(sortPanel.getSort()))
                {
                    EventBus.getInstance().notifyObservers(StreamReinitializeRequestEvent.getEvent());
                }
                else
                {
                    sortPanel.updateSelected("date", true);
                }
            }
        });

        EventBus.getInstance().addObserver(StreamRequestEvent.class, new Observer<StreamRequestEvent>()
        {
            public void update(final StreamRequestEvent event)
            {
                if (event.getForceReload() || !event.getJson().equals(jsonQuery))
                {
                    streamName = event.getStreamName();
                    jsonQuery = event.getJson();
                    if (activityId != 0L)
                    {
                        ActivityModel.getInstance().fetch(activityId, false);
                    }
                    else
                    {
                        setListMode();
                        stream.reinitialize();
                        boolean showTitleAsLink = false;
                        String shortName = "";

                        String updatedJson = jsonQuery;

                        JSONObject queryObject = JSONParser.parse(updatedJson).isObject().get("query").isObject();

                        // Only show cancel option if search is not part of the view.
                        Boolean canChange = !queryObject.containsKey("keywords");

                        if (queryObject.containsKey("keywords"))
                        {
                            final String streamSearchText = queryObject.get("keywords").isString().stringValue();

                            streamSearch.setSearchTerm(streamSearchText);

                            updatedJson = StreamJsonRequestFactory.setSearchTerm(streamSearchText,
                                    StreamJsonRequestFactory.getJSONRequest(updatedJson)).toString();
                        }
                        else if (search.length() > 0)
                        {
                            streamSearch.setSearchTerm(search);

                            updatedJson = StreamJsonRequestFactory.setSearchTerm(search,
                                    StreamJsonRequestFactory.getJSONRequest(updatedJson)).toString();

                        }
                        // see if the stream belongs to a group and set up the stream title as a link
                        else if (queryObject.containsKey("recipient")
                                && queryObject.get("recipient").isArray().size() == 1)
                        {
                            JSONArray recipientArr = queryObject.get("recipient").isArray();
                            JSONObject recipientObj = recipientArr.get(0).isObject();

                            if (recipientObj.get("type").isString().stringValue().equals("GROUP"))
                            {
                                shortName = recipientObj.get("name").isString().stringValue();

                                // only show the link if viewing the stream on the activity page
                                if (Session.getInstance().getUrlPage() == Page.ACTIVITY)
                                {
                                    showTitleAsLink = true;
                                }
                            }
                            streamSearch.onSearchCanceled();
                        }

                        else
                        {
                            streamSearch.onSearchCanceled();
                        }

                        sort = sortPanel.getSort();

                        updatedJson = StreamJsonRequestFactory.setSort(sort,
                                StreamJsonRequestFactory.getJSONRequest(updatedJson)).toString();

                        streamSearch.setTitleText(streamName, shortName, showTitleAsLink);
                        streamSearch.setCanChange(canChange);

                        StreamModel.getInstance().fetch(updatedJson, false);
                    }
                }
            }
        });

        EventBus.getInstance().addObserver(StreamSearchBeginEvent.class, new Observer<StreamSearchBeginEvent>()
        {
            public void update(final StreamSearchBeginEvent event)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new UpdateHistoryEvent(new CreateUrlRequest("search", String.valueOf(event.getSearchText()),
                                false)));
            }
        });

        EventBus.getInstance().addObserver(DeletedActivityResponseEvent.class,
                new Observer<DeletedActivityResponseEvent>()
                {
                    public void update(final DeletedActivityResponseEvent ev)
                    {
                        EventBus.getInstance().notifyObservers(
                                new ShowNotificationEvent(new Notification("Activity has been deleted")));
                    }
                });
    }

    /**
     * Put the widget in list mode.
     */
    private void setListMode()
    {
        streamSearch.setVisible(true);
        postContent.setVisible(true);
        stream.setVisible(true);
        sortPanel.setVisible(true);
        activityDetailPanel.clear();
        EventBus.getInstance().notifyObservers(new ChangeActivityModeEvent(false));
    }

    /**
     * Put the widget in single activity mode.
     */
    private void setSingleActivityMode()
    {
        sortPanel.setVisible(false);
        streamSearch.setVisible(false);
        postContent.setVisible(false);
        stream.setVisible(false);
        EventBus.getInstance().notifyObservers(new ChangeActivityModeEvent(true));
    }

    /**
     * Check the history for changes.
     * 
     * @param history
     *            the history.
     * @return true if it has changed.
     */
    private Boolean checkHistory(final HashMap<String, String> history)
    {
        Boolean hasChanged = false;

        Long newActivityId = 0L;
        Long newStreamId = 0L;
        Long newGroupId = 0L;
        String newSearch = "";
        String newSort = sortPanel.getSort();

        if (null != history)
        {
            if (history.containsKey("activityId"))
            {
                newActivityId = Long.parseLong(history.get("activityId"));
            }

            if (history.containsKey("streamId"))
            {
                newStreamId = Long.parseLong(history.get("streamId"));
            }

            if (history.containsKey("groupId"))
            {
                newGroupId = Long.parseLong(history.get("groupId"));
            }

            if (history.containsKey("search"))
            {
                newSearch = history.get("search");
            }
        }

        // Only process if the stream/group ID has not changed. Handled elsewhere otherwise.
        if (streamId.equals(newStreamId) && groupId.equals(newGroupId))
        {
            hasChanged = !(newActivityId.equals(activityId) && newSearch.equals(search) && newSort.equals(sort));
        }

        streamId = newStreamId;
        activityId = newActivityId;
        search = newSearch;
        sort = newSort;
        groupId = newGroupId;

        return hasChanged;
    }

    /**
     * Set the stream scope to post to.
     * 
     * @param streamScope
     *            the scope.
     * @param postingEnabled
     *            if posting is enabled.
     */
    public void setStreamScope(final StreamScope streamScope, final Boolean postingEnabled)
    {
        postingDisabled.setVisible(!postingEnabled);
        shadowPanel.setVisible(postingEnabled);

        if (postComposite == null && postingEnabled)
        {
            postComposite = new PostToStreamComposite(Session.getInstance().getActionProcessor(), streamScope);
            shadowPanel.add(postComposite);
            DeferredCommand.addCommand(new Command()
            {
                public void execute()
                {
                    postComposite.setUpMinimizer();
                }
            });
        }
        else if (postingEnabled)
        {
            postComposite.setScope(streamScope);
        }
        else
        {
            FlowPanel postingDisabledMessage = new FlowPanel();

            postingDisabledMessage.getElement().setInnerHTML("Posting messages has been disabled for this stream.");
            postingDisabled.addStyleName("posting-disabled-box");
            postingDisabled.add(postingDisabledMessage);
            postContent.add(postingDisabled);
        }
    }

    /**
     * Set the locked message panel content.
     * 
     * @param inPanel
     *            the panel content to set as the locked message.
     */
    public void setLockedMessagePanel(final Panel inPanel)
    {
        lockedMessage.setVisible(true);
        lockedMessage.add(inPanel);
    }
}
