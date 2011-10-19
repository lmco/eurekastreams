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
package org.eurekastreams.server.service.restlets.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.codehaus.jackson.map.ObjectMapper;
import org.eurekastreams.commons.exceptions.ExecutionException;

/**
 * Extracts a field of a JSON object as a Java object. Used by the API (ActionResource) for request parsing.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class JsonFieldObjectExtractor
{
    /** JSON object deserializer. (Jackson) */
    private final ObjectMapper objMapper = new ObjectMapper();

    /** Internal interface for methods to extract elements from a JSON array. */
    private static interface ArrayElementExtractor
    {
        /**
         * Extract the element and convert to appropriate type.
         *
         * @param index
         *            Index in JSON array.
         * @param jsonArray
         *            JSON array.
         * @return Returned value as object.
         * @throws Exception
         *             On error.
         */
        Object extract(int index, JSONArray jsonArray) throws Exception;
    }

    /** Map of methods for extracting elements from a JSON array by type. */
    @SuppressWarnings("serial")
    private final Map<String, ArrayElementExtractor> arrayExtractors = // \n
    new HashMap<String, JsonFieldObjectExtractor.ArrayElementExtractor>()
    {
        {
            put("long", new ArrayElementExtractor()
            {
                public Object extract(final int inIndex, final JSONArray inJsonArray)
                {
                    return inJsonArray.getLong(inIndex);
                }
            });
            put("int", new ArrayElementExtractor()
            {
                public Object extract(final int inIndex, final JSONArray inJsonArray)
                {
                    return inJsonArray.getInt(inIndex);
                }
            });
            put("string", new ArrayElementExtractor()
            {
                public Object extract(final int inIndex, final JSONArray inJsonArray)
                {
                    return inJsonArray.getString(inIndex);
                }
            });
            put("boolean", new ArrayElementExtractor()
            {
                public Object extract(final int inIndex, final JSONArray inJsonArray)
                {
                    return inJsonArray.getBoolean(inIndex);
                }
            });
        }
    };

    /**
     * Fills a list with objects created based on the contents of a JSON array.
     *
     * @param jsonArray
     *            The source JSON array.
     * @param list
     *            The list to fill.
     * @param targetType
     *            The type of the elements.
     * @throws Exception
     *             On error.
     */
    private void fillList(final JSONArray jsonArray, final Collection list, final String targetType) throws Exception
    {
        // Unimplemented feature: could support nested collections - check for brackets here like extract() does

        ArrayElementExtractor extractor = arrayExtractors.get(targetType.toLowerCase());
        if (extractor == null)
        {
            final Class targetClass = Class.forName(targetType);
            extractor = new ArrayElementExtractor()
            {
                public Object extract(final int inIndex, final JSONArray inJsonArray) throws Exception
                {
                    String requestAsString = inJsonArray.getString(inIndex);
                    Object requestAsObject = objMapper.readValue(requestAsString, targetClass);
                    // cast here is just to insure object is actually of requested type
                    return targetClass.cast(requestAsObject);
                }
            };
        }
        int length = jsonArray.size();
        for (int i = 0; i < length; i++)
        {
            list.add(extractor.extract(i, jsonArray));
        }
    }

    /**
     * Gets a field from a JSON object and returns it as an object of the specified type. Supports lists.
     *
     * @param jsonContainer
     *            The source JSON object.
     * @param fieldName
     *            The name of the field to extract from the JSON object.
     * @param targetType
     *            The type of the Java object the extracted field should be returned as. Can be a list type by using the
     *            format "listType[elementType]".
     * @return Java object extracted.
     * @throws Exception
     *             On error.
     */
    public Object extract(final JSONObject jsonContainer, final String fieldName, final String targetType)
            throws Exception
    {
        int pos = targetType.indexOf('[');
        if (pos >= 0)
        {
            int endPos = targetType.lastIndexOf(']');
            if (endPos < pos)
            {
                throw new ExecutionException("Mismatched brackets in type name.");
            }
            String listType = targetType.substring(0, pos);
            String elementType = targetType.substring(pos + 1, endPos);

            // Unimplemented feature: if listType is an empty string, then handle it as an array

            Class listClass = Class.forName(listType);
            if (!Collection.class.isAssignableFrom(listClass))
            {
                throw new ExecutionException("List class '" + listType + "' must implement Collection.");
            }
            Collection list;
            try
            {
                list = (Collection) listClass.getConstructor().newInstance();
            }
            catch (Exception ex)
            {
                throw new ExecutionException("Cannot instantiate list class '" + listType + "'.");
            }

            fillList(jsonContainer.getJSONArray(fieldName), list, elementType);
            return list;
        }
        else
        {
            String targetTypeLower = targetType.toLowerCase();

            if (targetTypeLower.equals("long"))
            {
                return jsonContainer.getLong(fieldName);
            }
            else if (targetTypeLower.equals("int"))
            {
                return jsonContainer.getInt(fieldName);
            }
            else if (targetTypeLower.equals("string"))
            {
                return jsonContainer.getString(fieldName);
            }
            else if (targetTypeLower.equals("boolean"))
            {
                return jsonContainer.getBoolean(fieldName);
            }
            else
            {
                Class returnType = Class.forName(targetType);

                String requestAsString = jsonContainer.getString(fieldName);
                Object requestAsObject = objMapper.readValue(requestAsString, returnType);

                /*
                 * Here's a nicer technique (since it doesn't require converting to string then parsing), but some of
                 * our types don't deserialize properly with it. JSONObject requestAsJson =
                 * jsonContainer.getJSONObject(fieldName); Object requestAsObject = JSONObject.toBean(requestAsJson,
                 * returnType);
                 */

                return returnType.cast(requestAsObject);
            }
        }
    }
}
