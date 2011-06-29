/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.animation;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;

/**
 * Expand and collapse animation.
 */
public class ExpandCollapseAnimation extends Animation
{
    /**
     * The target element.
     */
    private Element element = null;

    /**
     * Starting Height.
     */
    private int height = 0;

    /**
     * Expanding/Collapsing state.
     */
    private boolean isExpanding = false;

    /**
     * Default duration.
     */
    private static final int DEFAULT_DURATION = 500;

    /**
     * Duration in milliseconds.
     */
    private int milliseconds = DEFAULT_DURATION;

    /**
     * Constructor.
     * 
     * @param inElement
     *            the element.
     * @param inMilliseconds
     *            duration.
     */
    public ExpandCollapseAnimation(final Element inElement, final int inMilliseconds)
    {
        element = inElement;
        milliseconds = inMilliseconds;
    }

    public void expand()
    {
        expandWithPadding(0);
    }

    /**
     * Expand.
     * 
     * @param padding
     *            the amount of padding to use.
     */
    public void expandWithPadding(final int padding)
    {
        height = padding;
        Element child = element.getFirstChildElement();

        while (child != null)
        {
            height += child.getClientHeight();
            child = child.getNextSiblingElement();
        }

        isExpanding = true;
        run(milliseconds);
    }

    /**
     * Collapse.
     */
    public void collapse()
    {
        if (isExpanded())
        {
            isExpanding = false;
            run(milliseconds);
        }
    }

    /**
     * Expand to a specific height.
     * 
     * @param inHeight
     *            the height.
     */
    public void expand(final int inHeight)
    {
        height = inHeight;
        isExpanding = true;
        run(milliseconds);
    }

    /**
     * Animation update.
     * 
     * @param progress
     *            animation progress.
     */
    @Override
    protected void onUpdate(final double progress)
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

    /**
     * Animation complete.
     */
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

    /**
     * Toggle.
     */
    public void toggle()
    {
        toggleWithPadding(0);

    }

    /**
     * Get is expanded.
     */
    public boolean isExpanded()
    {
        return element.getClientHeight() > 0;
    }

    /**
     * Toggle with padding.
     * 
     * @param padding
     *            padding.
     */
    public void toggleWithPadding(final int padding)
    {
        if (isExpanded())
        {
            collapse();
        }
        else
        {
            expandWithPadding(padding);
        }

    }
}
