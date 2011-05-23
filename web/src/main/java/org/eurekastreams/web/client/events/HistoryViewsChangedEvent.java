package org.eurekastreams.web.client.events;

import java.util.List;

/**
 * History views have changed, page has stayed the same.
 */
public class HistoryViewsChangedEvent
{
    /**
     * The views.
     */
    private List<String> views;

    /**
     * Constructor.
     * @param inViews the new views.
     */
    public HistoryViewsChangedEvent(final List<String> inViews)
    {
        views = inViews;
    }
    
    public List<String> getViews() 
    {
        return views;
    }

}
