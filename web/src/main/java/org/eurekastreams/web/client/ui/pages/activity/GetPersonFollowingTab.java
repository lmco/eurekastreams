package org.eurekastreams.web.client.ui.pages.activity;

import org.eurekastreams.server.action.request.profile.GetFollowersFollowingRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotPersonFollowersResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonFollowingResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonJoinedGroupsResponseEvent;
import org.eurekastreams.web.client.model.PersonFollowersModel;
import org.eurekastreams.web.client.model.PersonFollowingModel;
import org.eurekastreams.web.client.model.PersonJoinedGroupsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.pagedlist.GroupRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.PagedListPanel;
import org.eurekastreams.web.client.ui.common.pagedlist.PersonRenderer;

public class GetPersonFollowingTab
{
    /**
     * Creates and sets up the connections tab content.
     * 
     * @param inPerson
     *            Person whose profile is being displayed.
     * @return Tab content.
     */
    public static PagedListPanel getFollowingTab(final PersonModelView inPerson)
    {
        final PagedListPanel connectionTabContent = new PagedListPanel("connections");

        Session.getInstance().getEventBus().addObserver(GotPersonFollowersResponseEvent.class,
                new Observer<GotPersonFollowersResponseEvent>()
                {
                    public void update(final GotPersonFollowersResponseEvent event)
                    {
                        connectionTabContent.render(event.getResponse(), "No one is following this person");
                    }
                });

        Session.getInstance().getEventBus().addObserver(GotPersonFollowingResponseEvent.class,
                new Observer<GotPersonFollowingResponseEvent>()
                {
                    public void update(final GotPersonFollowingResponseEvent event)
                    {
                        connectionTabContent.render(event.getResponse(), "This person is not following anyone");
                    }
                });

        Session.getInstance().getEventBus().addObserver(GotPersonJoinedGroupsResponseEvent.class,
                new Observer<GotPersonJoinedGroupsResponseEvent>()
                {
                    public void update(final GotPersonJoinedGroupsResponseEvent event)
                    {
                        connectionTabContent.render(event.getResponse(), "This person is not following any groups");
                    }
                });

        connectionTabContent.addSet("Followers", PersonFollowersModel.getInstance(), new PersonRenderer(false),
                new GetFollowersFollowingRequest(EntityType.PERSON, inPerson.getAccountId(), 0, 0));

        connectionTabContent.addSet("Following", PersonFollowingModel.getInstance(), new PersonRenderer(false),
                new GetFollowersFollowingRequest(EntityType.PERSON, inPerson.getAccountId(), 0, 0));

        connectionTabContent.addSet("Groups", PersonJoinedGroupsModel.getInstance(), new GroupRenderer(),
                new GetFollowersFollowingRequest(EntityType.GROUP, inPerson.getAccountId(), 0, 0));

        return connectionTabContent;
    }
}
