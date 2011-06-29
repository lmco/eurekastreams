package org.eurekastreams.web.client.events;

import com.google.gwt.user.client.ui.Widget;

public class PagerResponseEvent
{
    private Widget widget;
    private String key;

    /*
     * @param inKey the key.
     */
    public void setKey(final String inKey)
    {
        this.key = inKey;
    }

    /**
     * @param inWidget
     *            the widget to set
     */
    public void setWidget(final Widget inWidget)
    {
        this.widget = inWidget;
    }

    /**
     * @return the widget
     */
    public Widget getWidget()
    {
        return widget;
    }

    public String getKey()
    {
        return key;
    }
}
