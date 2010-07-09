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
package org.eurekastreams.server.action.validation.stream;

import java.util.HashMap;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.ActivityVerb;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.search.modelview.CommentDTO;

/**
 * Class to help test suites with building activity dto's.
 *
 */
public final class PostActivityTestHelpers
{
    /**
     * Default constructor override.
     */
    private PostActivityTestHelpers()
    {
        //overriding the default constructor
    }

    /**
     * Helper method to build an ActivityDTO with a DestinationStream in various states.
     * @param streamState - state of the DestinationStream to build into the ActivityDTO for testing.
     * @param shareVerb - flag to set the verb in the ActivityDTO to Share for testing
     *  as well as add the test comment.
     * @param inTestComment - test comment to build the dto with.
     * @param inDestinationId - id of the destination stream to build the action with.
     * @return ActivityDTO populated for test.
     */
    public static ActivityDTO buildActivityDTO(final DestinationStreamTestState streamState,
            final boolean shareVerb, final CommentDTO inTestComment, final Long inDestinationId)
    {
        ActivityDTO currentActivity = new ActivityDTO();
        StreamEntityDTO destinationStream;
        switch(streamState)
        {
            case NULLSTREAM:
                currentActivity.setDestinationStream(null);
                break;
            case NULLIDENTIFIER:
                destinationStream = new StreamEntityDTO();
                currentActivity.setDestinationStream(destinationStream);
                break;
            case EMPTYIDENTIFIER:
                destinationStream = new StreamEntityDTO();
                destinationStream.setUniqueIdentifier("");
                destinationStream.setType(EntityType.PERSON);
                currentActivity.setDestinationStream(destinationStream);
                break;
            case INVALIDTYPE:
                destinationStream = new StreamEntityDTO();
                destinationStream.setUniqueIdentifier("destStream");
                destinationStream.setType(EntityType.ORGANIZATION);
                break;
            default:
                destinationStream = new StreamEntityDTO();
                destinationStream.setUniqueIdentifier("destStream");
                destinationStream.setType(EntityType.PERSON);
                destinationStream.setDestinationEntityId(inDestinationId);
                currentActivity.setDestinationStream(destinationStream);
                break;
        }
        if (shareVerb)
        {
            //Share is not in the verb validators dictionary and will cause an error to be thrown on validation.
            currentActivity.setVerb(ActivityVerb.SHARE);
            currentActivity.setFirstComment(inTestComment);
        }
        else
        {
            currentActivity.setVerb(ActivityVerb.POST);
        }
        currentActivity.setBaseObjectType(BaseObjectType.NOTE);
        HashMap<String, String> objProperties = new HashMap<String, String>();
        objProperties.put("Content", "stuff");
        currentActivity.setBaseObjectProperties(objProperties);

        return currentActivity;
    }

    /**
     * Enum that describes the state of the DestinationStream to build into an ActivityDTO
     * for test.
     *
     */
    public static enum DestinationStreamTestState
    {
        /**
         * Destination stream is null.
         */
        NULLSTREAM,

        /**
         * Destination Stream identifier is null.
         */
        NULLIDENTIFIER,

        /**
         * Destination Stream Identifier is empty.
         */
        EMPTYIDENTIFIER,

        /**
         * Destination Stream is valid for test.
         */
        VALID,

        /**
         * Destination Stream that is not Person or Group.
         */
        INVALIDTYPE

    }
}
