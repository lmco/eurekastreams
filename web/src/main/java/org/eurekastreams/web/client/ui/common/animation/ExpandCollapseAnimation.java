package org.eurekastreams.web.client.ui.common.animation;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;

public class ExpandCollapseAnimation extends Animation
{
    private Element element = null;
    private int height = 0;
    private boolean isExpanding = false;
    private int milliseconds = 500;

    public ExpandCollapseAnimation(final Element inElement, final int inDefaultHeight, final int inMilliseconds)
    {
        element = inElement;
        height = inDefaultHeight;
        milliseconds = inMilliseconds;
    }

    public void expand()
    {
        isExpanding = true;
        run(milliseconds);
    }

    public void collapse()
    {
        isExpanding = false;
        run(milliseconds);
    }
    
    public void expand(final int inHeight)
    {
        height = inHeight;
        expand();
    }

    @Override
    protected void onUpdate(double progress)
    {
        if (isExpanding)
        {
            int currentHeight = element.getClientHeight();
            int heightDiff = height - currentHeight;
            element.getStyle().setHeight(currentHeight + (heightDiff * progress), Unit.PX);
        }
        else
        {
            element.getStyle().setHeight(height * (1 - progress), Unit.PX);
        }
    }

    @Override
    protected void onComplete()
    {
        super.onComplete();
        if (isExpanding)
        {
            element.getStyle().setHeight(height, Unit.PX);
        }
        else
        {
            element.getStyle().setHeight(0.0, Unit.PX);
        }
    }

    public void toggle()
    {
        if (element.getClientHeight() > 0)
        {
            collapse();
        }
        else
        {
            expand();
        }

    }

}
