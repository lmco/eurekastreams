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
package org.eurekastreams.web.client.model;

import java.io.Serializable;

import org.eurekastreams.server.action.request.UpdateStickyActivityRequest;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.web.client.events.data.UpdatedGroupStickyActivityEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Model for setting/clearing the sticky activity for a group.
 */
public class GroupStickyActivityModel extends BaseModel implements Insertable<ActivityDTO>, Deletable<Long>
{
    /**
     * Singleton.
     */
    private static GroupStickyActivityModel model = new GroupStickyActivityModel();

    /**
     * @return the singleton
     */
    public static GroupStickyActivityModel getInstance()
    {
        return model;
    }

    /**
     * Sets the activity as the sticky activity for its group.
     * 
     * @param activity
     *            The activity.
     */
    public void insert(final ActivityDTO activity)
    {
        final long streamEntityId = activity.getDestinationStream().getDestinationEntityId();
        super.callWriteAction("updateGroupStickyActivity",
                new UpdateStickyActivityRequest(streamEntityId, activity.getId()),
                new OnSuccessCommand<Serializable>()
                {
                    public void onSuccess(final Serializable response)
                    {
                        GroupModel.getInstance().clearCache();
                        Session.getInstance().getEventBus()
                                .notifyObservers(new UpdatedGroupStickyActivityEvent(streamEntityId, activity));
                    }
                });
    }

    /**
     * Clears the sticky activity for the group.
     * 
     * @param groupId
     *            The group to have it's sticky activity cleared.
     */
    public void delete(final Long groupId)
    {
        super.callWriteAction("updateGroupStickyActivity", new UpdateStickyActivityRequest(groupId, null),
                new OnSuccessCommand<Serializable>()
                {
                    public void onSuccess(final Serializable response)
                    {
                        GroupModel.getInstance().clearCache();
                        Session.getInstance().getEventBus()
                                .notifyObservers(new UpdatedGroupStickyActivityEvent(groupId, null));
                    }
                });
    }
}
