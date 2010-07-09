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
package org.eurekastreams.web.client.ui.common.stream;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.server.action.request.stream.GetActivitiesByCompositeStreamRequest;
import org.eurekastreams.server.action.request.stream.GetStreamSearchResultsRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.web.client.events.ChangeShowStreamRecipientEvent;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.MessageStreamUpdateEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.StreamReinitializeRequestEvent;
import org.eurekastreams.web.client.events.StreamRequestMoreEvent;
import org.eurekastreams.web.client.events.StreamSearchBeginEvent;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.events.data.DeletedActivityResponseEvent;
import org.eurekastreams.web.client.events.data.GotActivityListResponseEvent;
import org.eurekastreams.web.client.events.data.GotActivityResponseEvent;
import org.eurekastreams.web.client.events.data.GotActivitySearchResponseEvent;
import org.eurekastreams.web.client.model.ActivityListModel;
import org.eurekastreams.web.client.model.ActivityModel;
import org.eurekastreams.web.client.model.ActivitySearchModel;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Shows a person's activity stream as part of his or her profile.
 */
public class StreamPanel extends FlowPanel implements Bindable
{
    /**
     * What am I doing?
     * 
     */
    public enum Mode
    {
        /**
         * I'm searching.
         */
        SEARCHING,
        /**
         * I'm on a list.
         */
        LIST,
        /**
         * I'm on a single activity.
         */
        DETAIL,
        /**
         * A saved search.
         */
        SAVED_SEARCH
    }

    /**
     * The page size.
     */
    private static final int PAGESIZE = 10;
    /**
     * The mode.
     */
    private Mode mode = Mode.LIST;
    /**
     * The stream list.
     */
    private StreamListPanel stream;

    /**
     * The previous search term selected.
     */
    private String lastSearch = "";

    /**
     * The post widget.
     */
    private PostToStreamComposite postComposite = null;

    /**
     * Default max number of items.
     */
    private static final int DEFAULT_MAX_ITEMS = 10;

    /**
     * The search widget.
     */
    private StreamSearchComposite streamSearch = null;

    /**
     * Can the user post.
     */
    private boolean canPost = true;

    /**
     * Action Processor.
     */
    private ActionProcessor processor;

    /**
     * Post scope.
     */
    private StreamScope postScope;

    /**
     * Show the recipient.
     */
    private boolean showRecipient;

    /**
     * Title of stream.
     */
    private String title;

    /**
     * Composite stream.
     */
    private StreamView view;

    /**
     * Contents container for the list.
     */
    private FlowPanel listContents = new FlowPanel();

    /**
     * Contents for activity.
     */
    private FlowPanel activityContents = new FlowPanel();

    /**
     * Contents for posting activity.
     */
    private FlowPanel postContents = new FlowPanel();

    /**
     * Posting disabled panel.
     */
    private FlowPanel postingDisabled = new FlowPanel();

    /**
     * Panel for posting contents.
     */
    private FlowPanel shadowPanel = new FlowPanel();

    /**
     * The last item seen.
     */
    private long lastStreamItemId = Long.MAX_VALUE;

    /**
     * The newest item seen.
     */
    private long newestStreamItemId = 0L;

    /**
     * The request.
     */
    private GetActivitiesByCompositeStreamRequest messageRequest;

    /**
     * View id.
     */
    private long viewId = 0;

    /**
     * Constructor.
     * 
     * @param inProcessor
     *            the action processor.
     * @param inView
     *            the stream scope.
     * @param inCanPost
     *            if the user can post to this stream.
     * @param inShowRecipient
     *            show the recipient of messages.
     * @param inPostScope
     *            the scope that you are posting under.
     * @param inTitle
     *            title of stream.
     */
    public StreamPanel(final ActionProcessor inProcessor, final StreamView inView, final boolean inCanPost,
            final boolean inShowRecipient, final StreamScope inPostScope, final String inTitle)
    {
        this.addStyleName("layout-container");
        this.add(listContents);
        this.add(activityContents);

        processor = inProcessor;
        canPost = inCanPost;
        showRecipient = inShowRecipient;
        postScope = inPostScope;
        title = inTitle;
        view = inView;

        postComposite = new PostToStreamComposite(processor, postScope);

        shadowPanel.addStyleName("post-to-stream-container");
        shadowPanel.add(postComposite);

        EventBus.getInstance().addObserver(DeletedActivityResponseEvent.class,
                new Observer<DeletedActivityResponseEvent>()
                {
                    public void update(final DeletedActivityResponseEvent ev)
                    {
                        EventBus.getInstance().notifyObservers(
                                new ShowNotificationEvent(new Notification("Activity has been deleted")));
                    }
                });

        if (canPost)
        {
            postContents.add(shadowPanel);
            DeferredCommand.addCommand(new Command()
            {
                public void execute()
                {
                    postComposite.setUpMinimizer();
                }
            });
        }
        else if (postScope.getScopeType() != ScopeType.ORGANIZATION)
        {
            String entityRestrictingAccess = "";

            if (postScope.getScopeType() == ScopeType.GROUP)
            {
                entityRestrictingAccess = "group.";
            }
            else if (postScope.getScopeType() == ScopeType.PERSON)
            {
                entityRestrictingAccess = "user.";
            }

            FlowPanel postingDiabledMessage = new FlowPanel();
            postingDiabledMessage.getElement().setInnerHTML(
                    "Posting messages has been disabled by this " + entityRestrictingAccess);
            postingDisabled.addStyleName("posting-disabled-box");
            postingDisabled.add(postingDiabledMessage);
            postContents.add(postingDisabled);
        }
        listContents.add(postContents);

        streamSearch = new StreamSearchComposite(processor, title, view);
        listContents.add(streamSearch);

        listContents.add(new UnseenActivityNotificationPanel());

        if (view != null)
        {
            messageRequest = new GetActivitiesByCompositeStreamRequest(view.getId(), DEFAULT_MAX_ITEMS);
            viewId = view.getId();
            streamSearch.setStreamView(view);
        }
        else
        {
            messageRequest = new GetActivitiesByCompositeStreamRequest(null, DEFAULT_MAX_ITEMS);
        }

        stream = new StreamListPanel(new StreamMessageItemRenderer(showRecipient));

        listContents.add(stream);

        Session.getInstance().getEventBus().addObserver(StreamReinitializeRequestEvent.class,
                new Observer<StreamReinitializeRequestEvent>()
                {
                    public void update(final StreamReinitializeRequestEvent event)
                    {
                        newestStreamItemId = 0L;
                        lastStreamItemId = Long.MAX_VALUE;
                        updateStream();
                    }
                });

        Session.getInstance().getEventBus().addObserver(MessageStreamAppendEvent.class,
                new Observer<MessageStreamAppendEvent>()
                {
                    public void update(final MessageStreamAppendEvent evt)
                    {
                        newestStreamItemId = 0L;
                        lastStreamItemId = Long.MAX_VALUE;
                        updateStream();
                    }
                });

        Session.getInstance().getEventBus().addObserver(StreamRequestMoreEvent.class,
                new Observer<StreamRequestMoreEvent>()
                {
                    public void update(final StreamRequestMoreEvent evt)
                    {
                        stream.waitSpinner.setVisible(true);
                        updateStream();

                    }
                });

        Session.getInstance().getEventBus().addObserver(GotActivityListResponseEvent.class,
                new Observer<GotActivityListResponseEvent>()
                {
                    public void update(final GotActivityListResponseEvent event)
                    {
                        boolean hasMore = event.getResponse().getPagedSet().size() > messageRequest.getMaxResults();
                        while (event.getResponse().getPagedSet().size() > messageRequest.getMaxResults())
                        {
                            event.getResponse().getPagedSet().remove(event.getResponse().getPagedSet().size() - 1);
                        }

                        fireMessageUpdateEvent(event.getResponse(), "No activity has been posted.", hasMore);
                    }
                });

        Session.getInstance().getEventBus().addObserver(GotActivitySearchResponseEvent.class,
                new Observer<GotActivitySearchResponseEvent>()
                {
                    public void update(final GotActivitySearchResponseEvent event)
                    {
                        fireMessageUpdateEvent(event.getResponse(), "No results.",
                                event.getResponse().getTotal() > event.getResponse().getPagedSet().size());
                    }
                });

        Session.getInstance().getEventBus().addObserver(GotActivityResponseEvent.class,
                new Observer<GotActivityResponseEvent>()
                {
                    public void update(final GotActivityResponseEvent event)
                    {
                        activityContents.clear();
                        activityContents.add(new ActivityDetailPanel(event.getResponse(), showRecipient));
                    }
                });

        Session.getInstance().getEventBus().addObserver(UpdatedHistoryParametersEvent.class,
                new Observer<UpdatedHistoryParametersEvent>()
                {
                    public void update(final UpdatedHistoryParametersEvent event)
                    {
                        String searchText = event.getParameters().get("streamSearch");
                        String activityIdTxt = event.getParameters().get("activityId");

                        // put single-activity first, so that if you have both activity and search params, it will go to
                        // the single activity view. That way it can just remove the activity id parameter to return to
                        // the search.
                        if (activityIdTxt != null)
                        {
                            lastSearch = "";
                            mode = Mode.DETAIL;
                            listContents.setVisible(false);
                            activityContents.setVisible(true);
                            Session.getInstance().getEventBus().notifyObservers(
                                    StreamReinitializeRequestEvent.getEvent());
                        }
                        else if (searchText != null)
                        {
                            mode = Mode.SEARCHING;

                            String inputStreamViewId = event.getParameters().get("viewId");
                            String searchId = event.getParameters().get("searchId");
                            if (inputStreamViewId != null)
                            {
                                viewId = Long.valueOf(inputStreamViewId);
                            }
                            if (searchId != null)
                            {
                                mode = Mode.SAVED_SEARCH;
                            }
                            listContents.setVisible(true);
                            activityContents.setVisible(false);

                            // reinitializes the search only if the search is new - fixes a bug where two sets of
                            // identical search results were displayed
                            if (!lastSearch.equals(searchText))
                            {
                                Session.getInstance().getEventBus().notifyObservers(
                                        StreamReinitializeRequestEvent.getEvent());
                                Session.getInstance().getEventBus().notifyObservers(StreamSearchBeginEvent.getEvent());
                                lastSearch = searchText;
                            }
                        }
                        else
                        {
                            if (!Mode.LIST.equals(mode))
                            {
                                lastSearch = "";
                                mode = Mode.LIST;
                                listContents.setVisible(true);
                                activityContents.setVisible(false);

                                Session.getInstance().getEventBus().notifyObservers(
                                        StreamReinitializeRequestEvent.getEvent());

                            }
                        }
                        streamSearch.setMode(mode);
                    }
                }, true);

        Session.getInstance().getEventBus().addObserver(ChangeShowStreamRecipientEvent.class,
                new Observer<ChangeShowStreamRecipientEvent>()
                {

                    public void update(final ChangeShowStreamRecipientEvent event)
                    {
                        showRecipient = event.getValue();
                    }
                });

    }

    /**
     * Update the stream.
     */
    public void updateStream()
    {
        switch (mode)
        {
        case DETAIL:
            ActivityModel.getInstance().fetch(Long.valueOf(Session.getInstance().getParameterValue("activityId")),
                    false);
            break;
        case LIST:
            messageRequest.setMaxActivityId(lastStreamItemId);

            ActivityListModel.getInstance().fetch(
                    new GetActivitiesByCompositeStreamRequest(messageRequest.getCompositeStreamId(), messageRequest
                            .getMaxResults() + 1, messageRequest.getMaxActivityId()), false);
            break;
        case SEARCHING:
            ActivitySearchModel.getInstance().fetch(
                    new GetStreamSearchResultsRequest(viewId, Session.getInstance().getParameterValue("streamSearch"),
                            PAGESIZE, lastStreamItemId), false);
            break;
        case SAVED_SEARCH:
            ActivitySearchModel.getInstance().fetch(
                    new GetStreamSearchResultsRequest(viewId, Session.getInstance().getParameterValue("streamSearch"),
                            PAGESIZE, lastStreamItemId), false);
            break;
        default:
            break;
        }
    }

    /**
     * Fire the message update event.
     * 
     * @param result
     *            the result.
     * @param noMessageDisplay
     *            the no message display.
     * @param moreResults
     *            are there more results.
     */
    private void fireMessageUpdateEvent(final PagedSet<ActivityDTO> result, final String noMessageDisplay,
            final boolean moreResults)
    {
        MessageStreamUpdateEvent event = new MessageStreamUpdateEvent(result);
        event.setMoreResults(moreResults);
        event.setNoResultsMessage(noMessageDisplay);

        event.setStreamId(viewId);
        event.setLatestActivity(0L);

        if (!result.getPagedSet().isEmpty())
        {

            if (result.getPagedSet().get(0).getId() > newestStreamItemId)
            {
                newestStreamItemId = result.getPagedSet().get(0).getId();
            }
            event.setLatestActivity(newestStreamItemId);

            lastStreamItemId = result.getPagedSet().get(result.getPagedSet().size() - 1).getId();
        }

        Session.getInstance().getEventBus().notifyObservers(event);
    }

    /**
     * Set the view.
     * 
     * @param inView
     *            the view.
     */
    public void setView(final StreamView inView)
    {
        if (view == null || inView.getId() != view.getId())
        {
            view = inView;
            viewId = view.getId();
            messageRequest = new GetActivitiesByCompositeStreamRequest(view.getId(), DEFAULT_MAX_ITEMS);
            streamSearch.setTitleText(view.getName());
            streamSearch.setStreamView(view);
            streamSearch.setMode(mode);
            Session.getInstance().getEventBus().notifyObservers(StreamReinitializeRequestEvent.getEvent());
        }
    }

    /**
     * Set the scope of the post widget.
     * 
     * @param inScope
     *            the scope.
     */
    public void setPostScope(final StreamScope inScope)
    {
        if (postComposite != null)
        {
            postComposite.setScope(inScope);
        }
    }

    /**
     * Set if panel should allow activity posts or not.
     * 
     * @param inPostable
     *            If panel should allow activity posts or not.
     */
    public void setPostable(final boolean inPostable)
    {
        postContents.clear();
        if (inPostable)
        {
            postContents.add(shadowPanel);
            DeferredCommand.addCommand(new Command()
            {
                public void execute()
                {
                    postComposite.setUpMinimizer();
                }
            });
        }
        else
        {
            FlowPanel postingDisabledMessage = new FlowPanel();
            postingDisabledMessage.getElement().setInnerHTML("Posting messages has been disabled by this group");
            postingDisabled.addStyleName("posting-disabled-box");
            postingDisabled.add(postingDisabledMessage);
            postContents.add(postingDisabled);
        }

    }

}
