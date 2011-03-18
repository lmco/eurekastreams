/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.stream.renderers;

import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotActivityLikersResponseEvent;
import org.eurekastreams.web.client.model.ActivityLikersModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.BaseDialogContent;
import org.eurekastreams.web.client.ui.common.pagedlist.PersonRenderer;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The people who liked the activity modal.
 */
public class LikersDialogContent extends BaseDialogContent
{
    /**
     * Main flow panel.
     */
    private final FlowPanel body = new FlowPanel();

    /**
     * Activity id.
     */
    private final Long activityId;

    /**
     * Default constructor.
     * @param inActivityId activity id.
     */
    public LikersDialogContent(final Long inActivityId)
    {
        activityId = inActivityId;
    }

    /**
     * Gets the body panel.
     *
     * @return the body.
     */
    public Widget getBody()
    {
        return body;
    }

    /**
     * CSS class name.
     * @return the css class name.
     */
    public String getCssName()
    {
        return StaticResourceBundle.INSTANCE.coreCss().likerModal();
    }

    /**
     * Return the title.
     * @return the title.
     */
    public String getTitle()
    {
        return "Who Liked This Activity";
    }

    /**
     * Show.
     */
    @Override
    public void show()
    {
        body.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formSubmitSpinny());
        body.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formProcessingSpinny());
        ActivityLikersModel.getInstance().fetch(activityId, false);

        Session.getInstance().getEventBus().addObserver(GotActivityLikersResponseEvent.class,
                new Observer<GotActivityLikersResponseEvent>()
                {
                    public void update(final GotActivityLikersResponseEvent event)
                    {
                        body.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().formSubmitSpinny());
                        body.removeStyleName("form-processing-spinny");

                        PersonRenderer renderer = new PersonRenderer(false);

                        FlowPanel scrollable = new FlowPanel();
                        scrollable.addStyleName(StaticResourceBundle.INSTANCE.coreCss().likersContent());
                        body.add(scrollable);

                        for (PersonModelView person : event.getResponse())
                        {
                            scrollable.add(renderer.render(person));
                        }

                        Session.getInstance().getEventBus().removeObserver(
                                GotActivityLikersResponseEvent.class, this);
                    }
                });
    }

}
