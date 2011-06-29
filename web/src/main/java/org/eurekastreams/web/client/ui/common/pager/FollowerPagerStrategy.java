package org.eurekastreams.web.client.ui.common.pager;

import org.eurekastreams.server.action.request.profile.GetFollowersFollowingRequest;
import org.eurekastreams.server.domain.BasicPager;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PagerResponseEvent;
import org.eurekastreams.web.client.events.data.GotGroupModelViewInformationResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonFollowersResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonalInformationResponseEvent;
import org.eurekastreams.web.client.model.PersonFollowersModel;
import org.eurekastreams.web.client.ui.common.pagedlist.PersonRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.TwoColumnPagedListRenderer;

import com.google.gwt.user.client.ui.FlowPanel;

public class FollowerPagerStrategy implements PagerStrategy
{
    private PersonFollowersModel model = PersonFollowersModel.getInstance();
    private PagerResponseEvent responseEvent = new PagerResponseEvent();
    private BasicPager pager = new BasicPager();

    private TwoColumnPagedListRenderer twoColListRenderer = new TwoColumnPagedListRenderer();
    private PersonRenderer personRenderer = new PersonRenderer(false);
    private EntityType entityType;
    private String entityKey;

    public FollowerPagerStrategy()
    {
        pager.setPageSize(10);
        responseEvent.setKey(getKey());

        EventBus.getInstance().addObserver(GotPersonFollowersResponseEvent.class,
                new Observer<GotPersonFollowersResponseEvent>()
                {

                    public void update(GotPersonFollowersResponseEvent event)
                    {
                        pager.setMaxCount(event.getResponse().getTotal());

                        FlowPanel responsePanel = new FlowPanel();
                        twoColListRenderer.render(responsePanel, personRenderer, event.getResponse(), "No Followers");
                        responseEvent.setWidget(responsePanel);
                        EventBus.getInstance().notifyObservers(responseEvent);
                    }
                });

        EventBus.getInstance().addObserver(GotPersonalInformationResponseEvent.class,
                new Observer<GotPersonalInformationResponseEvent>()
                {
                    public void update(final GotPersonalInformationResponseEvent event)
                    {
                        entityKey = event.getResponse().getAccountId(); 
                        entityType = EntityType.PERSON;
                    }
                });

        EventBus.getInstance().addObserver(GotGroupModelViewInformationResponseEvent.class,
                new Observer<GotGroupModelViewInformationResponseEvent>()
                {
                    public void update(final GotGroupModelViewInformationResponseEvent event)
                    {
                        entityKey = event.getResponse().getShortName(); 
                        entityType = EntityType.GROUP;
                    }
                });
    }

    public boolean hasNext()
    {
        return pager.isNextPageable();
    }

    public boolean hasPrev()
    {
        return pager.isPreviousPageable();
    }

    public void init()
    {
        model.fetch(new GetFollowersFollowingRequest(entityType, entityKey, pager.getStartItem(), pager.getEndItem()),
                false);
    }

    public void next()
    {
        pager.nextPage();
        model.fetch(new GetFollowersFollowingRequest(entityType, entityKey, pager.getStartItem(), pager.getEndItem()),
                false);

    }

    public void prev()
    {
        pager.previousPage();
        model.fetch(new GetFollowersFollowingRequest(entityType, entityKey, pager.getStartItem(), pager.getEndItem()),
                false);
    }

    public String getKey()
    {
        return "follower";
    }
}
