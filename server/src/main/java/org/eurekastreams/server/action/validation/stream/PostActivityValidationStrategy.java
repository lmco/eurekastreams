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

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.service.actions.strategies.activity.ActivityValidator;

/**
 * This class contains the validation strategy for the PostActivityAction class.
 *
 */
public class PostActivityValidationStrategy implements ValidationStrategy<ServiceActionContext>
{

    /**
     * Local logger instance for this class.
     */
    private final Log log = LogFactory.make();

    /**
     * Map of verb validators.
     */
    private final Map<String, ActivityValidator> verbValidators;

    /**
     * Map of object validators.
     */
    private final Map<String, ActivityValidator> objectValidators;

    /**
     * The constructor for this Validation Strategy.
     * @param inVerbValidators - Map of configured verb validators.
     * @param inObjectValidators - Map of configured object validators.
     */
    public PostActivityValidationStrategy(final Map<String, ActivityValidator> inVerbValidators,
            final Map<String, ActivityValidator> inObjectValidators)
    {
        verbValidators = inVerbValidators;
        objectValidators = inObjectValidators;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void validate(final ServiceActionContext inActionContext)
    {
        ValidationException valEx = new ValidationException();

        PostActivityRequest currentRequest = (PostActivityRequest) inActionContext.getParams();

        ActivityDTO currentActivity = currentRequest.getActivityDTO();

        // All activities must have a destination stream.
        if (currentActivity.getDestinationStream() == null
                || currentActivity.getDestinationStream().getUniqueIdentifier() == null
                || currentActivity.getDestinationStream().getUniqueIdentifier().length() <= 0)
        {
            valEx.addError("destination_stream", "Activities require a Destination Stream.");
            throw valEx;
        }

        // All activities must be posted to either a person or group stream.
        if (!((currentActivity.getDestinationStream().getType() == EntityType.PERSON) || (currentActivity
                .getDestinationStream().getType() == EntityType.GROUP)))
        {
            valEx.addError("destination_stream_type", "Activities can only be submitted to Person or Group streams.");
            throw valEx;
        }

        // If there are verb and object validator instances available use them
        // to validate the
        // activity object, if not, throw an exception.
        if (verbValidators.containsKey(currentActivity.getVerb().name())
                && objectValidators.containsKey(currentActivity.getBaseObjectType().name()))
        {
            try
            {
                ActivityValidator currentVerbVal = verbValidators.get(currentActivity.getVerb().name());
                currentVerbVal.validate(currentActivity);

                ActivityValidator currentObjectVal = objectValidators.get(currentActivity.getBaseObjectType().name());
                currentObjectVal.validate(currentActivity);
            }
            catch (ValidationException vex)
            {
                log.error("ActivityValidators failed for this activity.", vex);
                for (Entry<String, String> validationError : vex.getErrors().entrySet())
                {
                    valEx.addError(validationError.getKey(), validationError.getValue());
                    log.error("Error key: " + validationError.getKey() + " message: " + validationError.getValue());
                }
                throw valEx;
            }
        }
        else
        {
            valEx.addError("validator_required",
                    "The supplied activity does not have a corresponding validator and cannot be persisted.");
            throw valEx;
        }
    }
}
