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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Slide animation.
 */
public class SlideAnimation extends Animation
{
    /**
     * Direction of the slide.
     */
    public enum Direction
    {
        /**
         * Left.
         */
        Left,
        /**
         * Right.
         */
        Right
    }

    /**
     * The element to act on.
     */
    private Element elem;

    /**
     * Width of children.
     */
    private int childWidth = 0;

    /**
     * Direction of slide.
     */
    private Direction direction = Direction.Left;

    /**
     * Slide.
     * 
     * @param inDirection
     *            the direction.
     * @param widget
     *            widget to add.
     * @param panel
     *            panel to act on.
     * @param duration
     *            duration.
     */
    public void slide(final Direction inDirection, final Widget widget, final FlowPanel panel, final int duration)
    {
        direction = inDirection;
        elem = panel.getElement();

        childWidth = elem.getFirstChildElement().getClientWidth();

        if (direction.equals(Direction.Left))
        {
            panel.add(widget);
        }
        else
        {
            panel.insert(widget, 0);
            elem.getStyle().setRight(childWidth, Unit.PX);
        }
        run(duration);
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
        if (direction.equals(Direction.Left))
        {
            elem.getStyle().setRight(childWidth * progress, Unit.PX);
        }
        else
        {
            elem.getStyle().setRight(childWidth * (1.0F - progress), Unit.PX);
        }
    }

    /**
     * Animation complete.
     */
    @Override
    protected void onComplete()
    {
        super.onComplete();

        if (direction.equals(Direction.Left))
        {
            elem.getFirstChildElement().removeFromParent();
        }
        else
        {
            elem.getFirstChildElement().getNextSiblingElement().removeFromParent();
        }

        elem.getStyle().setRight(0, Unit.PX);
    }

}
