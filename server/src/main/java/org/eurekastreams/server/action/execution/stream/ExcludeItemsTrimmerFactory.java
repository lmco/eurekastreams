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
package org.eurekastreams.server.action.execution.stream;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Factory to create ExcludeItemsTrimmers.
 */
public class ExcludeItemsTrimmerFactory implements ActivityQueryListTrimmerFactory
{
    /** Null trimmer. */
    private final NullTrimmer nullTrimmer = new NullTrimmer();

    /**
     * {@inheritDoc}
     */
    @Override
    public ListTrimmer getTrimmer(final JSONObject inRequest, final Long inUserEntityId)
    {
        try
        {
            JSONObject obj = inRequest.optJSONObject("exclude");
            if (obj != null)
            {
                JSONArray array = obj.optJSONArray("ids");
                if (array != null)
                {
                    List<Long> ids = new ArrayList<Long>();
                    for (int i = 0; i < array.size(); i++)
                    {
                        long id = array.optLong(i, -1);
                        if (id >= 0)
                        {
                            ids.add(id);
                        }
                    }
                    if (!ids.isEmpty())
                    {
                        return new ExcludeItemsTrimmer(ids);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            return nullTrimmer;
        }
        return nullTrimmer;
    }
}
