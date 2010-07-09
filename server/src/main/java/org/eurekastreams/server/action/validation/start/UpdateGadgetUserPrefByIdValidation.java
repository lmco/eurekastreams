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
package org.eurekastreams.server.action.validation.start;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.start.GadgetUserPrefActionRequest;

/**
 * Validation for UpdateGadgetUserPrefById Execution.
 * 
 */
public class UpdateGadgetUserPrefByIdValidation implements ValidationStrategy<ActionContext>
{

    /**
     * Validation for UpdateGadgetUserPrefById Execution.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @throws ValidationException
     *             if JSON is invalid.
     */
    @Override
    public void validate(final ActionContext inActionContext) throws ValidationException
    {
        GadgetUserPrefActionRequest currentRequest = (GadgetUserPrefActionRequest) inActionContext.getParams();

        if (currentRequest.getGadgetUserPref().length() > 0)
        {
            try
            {
                JSONObject.fromObject(currentRequest.getGadgetUserPref());
            }
            catch (JSONException jex)
            {
                throw new ValidationException("Gadget User Prefs must be valid JSON format.");
            }
        }
    }

}
