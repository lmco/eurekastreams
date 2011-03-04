/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.pages.profile.widgets;

import org.eurekastreams.server.action.request.profile.GetCurrentUserFollowingStatusRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotPersonFollowerStatusResponseEvent;
import org.eurekastreams.web.client.model.BaseModel;
import org.eurekastreams.web.client.model.CurrentUserPersonFollowingStatusModel;
import org.eurekastreams.web.client.model.Deletable;
import org.eurekastreams.web.client.model.GroupMembersModel;
import org.eurekastreams.web.client.model.Insertable;
import org.eurekastreams.web.client.model.PersonFollowersModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * This panel and its MVC classes handle the follow button shown on a profile page.
 */
public class FollowPanel extends FlowPanel
{

    /**
     * Label for follow/unfollow me button.
     */
    Label followMe = new Label("");

    /**
     * The id of the entity whose data are being shown.
     */
    private String entityId = null;

    /**
     * Current status.
     */
    private Follower.FollowerStatus status;

    /**
     * The model.
     */
    private BaseModel model;

    /**
     * Constructor.
     * 
     * @param inEntityId
     *            the id of the entity being viewed
     * @param type
     *            the type of the entity being viewed
     */
    public FollowPanel(final String inEntityId, final EntityType type)
    {
        entityId = inEntityId;

        if (!entityId.equals(Session.getInstance().getCurrentPerson().getAccountId()))
        {

            if (type.equals(EntityType.PERSON))
            {
                model = PersonFollowersModel.getInstance();
            }
            else
            {
                model = GroupMembersModel.getInstance();
            }
            this.add(followMe);
            followMe.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    SetFollowingStatusRequest request;

                    switch (status)
                    {
                    case FOLLOWING:
                        request = new SetFollowingStatusRequest(
                                Session.getInstance().getCurrentPerson().getAccountId(), entityId, type, false,
                                Follower.FollowerStatus.NOTFOLLOWING);
                        ((Deletable<SetFollowingStatusRequest>) model).delete(request);
                        onFollowerStatusChanged(Follower.FollowerStatus.NOTFOLLOWING);
                        break;
                    case NOTFOLLOWING:
                        request = new SetFollowingStatusRequest(
                                Session.getInstance().getCurrentPerson().getAccountId(), entityId, type, false,
                                Follower.FollowerStatus.FOLLOWING);
                        ((Insertable<SetFollowingStatusRequest>) model).insert(request);
                        onFollowerStatusChanged(Follower.FollowerStatus.FOLLOWING);
                        break;
                    default:
                        // do nothing.
                        break;
                    }
                }
            });

            Session.getInstance().getEventBus().addObserver(GotPersonFollowerStatusResponseEvent.class,
                    new Observer<GotPersonFollowerStatusResponseEvent>()
                    {
                        public void update(final GotPersonFollowerStatusResponseEvent event)
                        {
                            onFollowerStatusChanged(event.getResponse());
                        }
                    });

            CurrentUserPersonFollowingStatusModel.getInstance().fetch(
                    new GetCurrentUserFollowingStatusRequest(entityId, type), true);
        }
    }

    /**
     * When the following status changes.
     * 
     * @param inStatus
     *            status.
     */
    private void onFollowerStatusChanged(final Follower.FollowerStatus inStatus)
    {
        status = inStatus;

        switch (inStatus)
        {
        case FOLLOWING:
            followMe.removeStyleName(followMe.getStyleName());
            followMe.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileUnfollowMeBtn());
            break;
        case NOTFOLLOWING:
            followMe.removeStyleName(followMe.getStyleName());
            followMe.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileFollowMeBtn());
            break;
        default:
            followMe.removeStyleName(followMe.getStyleName());
            followMe.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileDisabledFollowMeBtn());
            break;
        }
    }
}
