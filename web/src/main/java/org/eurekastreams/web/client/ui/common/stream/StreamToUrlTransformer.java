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
package org.eurekastreams.web.client.ui.common.stream;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Transforms a stream into a URL.
 */
public class StreamToUrlTransformer
{
    /**
     * Transform the stream.
     * 
     * @param streamId
     *            the saved stream ID (if it has one).
     * @param jsonRequest
     *            the reques to create the stream.
     * @return the stream URL.
     */
    public String getUrl(final String streamId, final String jsonRequest)
    {
        if (streamId == null)
        {
            JSONObject query = JSONParser.parse(jsonRequest).isObject().get("query").isObject();

            String queryString = "";

            for (String key : query.keySet())
            {
                queryString += key + "/";
                if (null != query.get(key).isArray())
                {

                    JSONArray entArr = query.get(key).isArray();
                    for (int i = 0; i < entArr.size(); i++)
                    {
                        JSONObject entity = entArr.get(i).isObject();

                        if (i != 0)
                        {
                            queryString += ",";
                        }

                        queryString += entity.get("type").isString().stringValue() + ":"
                                + entity.get("name").isString().stringValue();
                    }
                    queryString += "/";
                }
                else
                {
                    queryString += query.get(key).isString().stringValue() + "/";
                }
            }

            return "query/" + queryString;
        }
        else
        {
            return "saved/" + streamId;
        }
    }
}
