/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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

import java.util.HashSet;

import org.eurekastreams.server.domain.TutorialVideoDTO;

/**
 * client model for tutorialvideos.
 * 
 */
public class TutorialVideoModel extends BaseModel implements Fetchable<Long>
{
    /**
     * Singleton.
     */
    private static TutorialVideoModel model = new TutorialVideoModel();

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static TutorialVideoModel getInstance()
    {
        return model;
    }

    /**
     * @param request
     *            the id of the tutorial video.
     * @param useClientCacheIfAvailable
     *            if it should use cache.
     * 
     */
    public void fetch(final Long request, final boolean useClientCacheIfAvailable)
    {
        super.callReadAction("getTutorialVideo", request, new OnSuccessCommand<HashSet<TutorialVideoDTO>>()
        {
            public void onSuccess(final HashSet<TutorialVideoDTO> response)
            {
                // Note: This is turned off until we update the videos
                // Session.getInstance().getEventBus().notifyObservers(new GetTutorialVideoResponseEvent(response));
            }
        }, useClientCacheIfAvailable);

    }
}
