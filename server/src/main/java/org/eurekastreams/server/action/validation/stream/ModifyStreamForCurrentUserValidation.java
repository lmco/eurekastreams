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

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.Stream;

/**
 * Validates the action to modify a current users streams.
 */
public class ModifyStreamForCurrentUserValidation implements ValidationStrategy<PrincipalActionContext>
{
    /**
     * Local logger instance for this class.
     */
    private final Log log = LogFactory.make();
    
    /**
     * Max number of streams.
     */
    private static final int MAX_STREAMS = 25;

    /**
     * Validates modifying the current user's streams.
     * 
     * @param inActionContext
     *            the action context.
     * @throws ValidationException
     *             on validation error.
     */
    @Override
    public void validate(final PrincipalActionContext inActionContext) throws ValidationException
    {
        ValidationException valEx = new ValidationException();

        Stream stream = (Stream) inActionContext.getParams();

        JSONObject object = null;
        try
        {
            object = JSONObject.fromObject(stream.getRequest());
        }
        catch (JSONException ex)
        {
            valEx.addError("stream", "Malformed JSON. Try again later.");
            throw valEx;
        }

        if (stream.getName() == null || stream.getName().length() == 0)
        {
            valEx.addError("name", "Stream must have a name.");
            throw valEx;
        }

        JSONObject query = object.getJSONObject("query");

        for (Object key : query.keySet())
        {
            String keyStr = (String) key;

            try
            {
                JSONArray arr = query.getJSONArray(keyStr);

                if (arr.size() == 0)
                {
                    valEx.addError("stream", "Add at least one stream");
                    throw valEx;
                }
                else if (arr.size() > MAX_STREAMS)
                {
                    valEx.addError("stream", "Maximum number of streams allowed is " + MAX_STREAMS);
                    throw valEx;
                }
            }
            catch (JSONException ex)
            {
                // do nothing.
                log.trace("Item is not an array. Ignored");
            }
        }
    }
}
