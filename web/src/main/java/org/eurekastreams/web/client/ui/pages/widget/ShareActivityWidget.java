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
package org.eurekastreams.web.client.ui.pages.widget;

import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotActivityResponseEvent;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.ActivityModel;
import org.eurekastreams.web.client.ui.common.dialog.DialogContentHost;
import org.eurekastreams.web.client.ui.common.stream.share.ShareMessageDialogContent;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Secondary widget used by the other widgets to share an activity.
 */
public class ShareActivityWidget extends Composite
{
    /**
     * Constructor.
     *
     * @param activityId
     *            ID of activity to share.
     */
    public ShareActivityWidget(final Long activityId)
    {
        final SimplePanel main = new SimplePanel();
        initWidget(main);

        EventBus.getInstance().addObserver(GotActivityResponseEvent.class, new Observer<GotActivityResponseEvent>()
        {
            public void update(final GotActivityResponseEvent ev)
            {
                ShareMessageDialogContent dialogContent = new ShareMessageDialogContent(ev.getResponse());
                dialogContent.setHost(new DialogContentHost()
                {
                    public void center()
                    {
                    }

                    public void hide()
                    {
                        WidgetJSNIFacadeImpl.nativeClose();
                    }
                });
                Widget dialogWidget = dialogContent.getBody();
                dialogWidget.addStyleName(StaticResourceBundle.INSTANCE.coreCss().embeddedWidget());
                dialogWidget.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectCommentWidget());
                main.add(dialogWidget);
            }
        });

        ActivityModel.getInstance().fetch(activityId, true);
    }
}
