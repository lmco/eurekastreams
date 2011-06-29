package org.eurekastreams.web.client.ui.common.pager;

import org.eurekastreams.server.action.request.stream.GetStreamsUserIsFollowingRequest;
import org.eurekastreams.server.domain.BasicPager;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PagerResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonFollowingResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonalInformationResponseEvent;
import org.eurekastreams.web.client.model.PersonFollowingModel;
import org.eurekastreams.web.client.ui.common.pagedlist.ModelViewRenderer;
import org.eurekastreams.web.client.ui.common.pagedlist.TwoColumnPagedListRenderer;

import com.google.gwt.user.client.ui.FlowPanel;

public class FollowingPagerStrategy implements PagerStrategy
{
    private PersonFollowingModel model = PersonFollowingModel.getInstance();
    private PagerResponseEvent responseEvent = new PagerResponseEvent();
    private BasicPager pager = new BasicPager();

    private TwoColumnPagedListRenderer twoColListRenderer = new TwoColumnPagedListRenderer();
    private ModelViewRenderer itemRenderer = new ModelViewRenderer();
    private String entityKey;

    public FollowingPagerStrategy()
    {
        pager.setPageSize(10);
        responseEvent.setKey(getKey());

        EventBus.getInstance().addObserver(GotPersonFollowingResponseEvent.class,
                new Observer<GotPersonFollowingResponseEvent>()
                {

                    public void update(GotPersonFollowingResponseEvent event)
                    {
                        pager.setMaxCount(event.getResponse().getTotal());

                        FlowPanel responsePanel = new FlowPanel();
                        twoColListRenderer.render(responsePanel, itemRenderer, event.getResponse(),
                                "Not Following Anyone");
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
        model.fetch(new GetStreamsUserIsFollowingRequest(entityKey, pager.getStartItem(), pager.getEndItem()), false);
    }

    public void next()
    {
        pager.nextPage();
        model.fetch(new GetStreamsUserIsFollowingRequest(entityKey, pager.getStartItem(), pager.getEndItem()), false);

    }

    public void prev()
    {
        pager.previousPage();
        model.fetch(new GetStreamsUserIsFollowingRequest(entityKey, pager.getStartItem(), pager.getEndItem()), false);
    }

    public String getKey()
    {
        return "following";
    }
}
