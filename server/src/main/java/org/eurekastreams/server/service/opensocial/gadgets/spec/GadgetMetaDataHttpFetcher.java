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
package org.eurekastreams.server.service.opensocial.gadgets.spec;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.GeneralGadgetDefinition;
import org.eurekastreams.server.domain.gadgetspec.EnumValuePairDTO;
import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.server.domain.gadgetspec.UserPrefDTO;
import org.eurekastreams.server.domain.gadgetspec.UserPrefDTO.DataType;

/**
 * This class provides the mechanism for retrieving the gadget metadata contained with gadget definitions that are
 * supplied.
 *
 */
public class GadgetMetaDataHttpFetcher implements GadgetMetaDataFetcher
{
    /**
     * Base url for the Current App where the metadata request will be made to.
     */
    private String currentAppContextBaseUrl;

    /**
     * Constant byte size for characters.
     */
    private static final int CHAR_BYTE = 1024;

    /**
     * Local logging instance.
     */
    private final Log logger = LogFactory.getLog(GadgetMetaDataHttpFetcher.class);

    /**
     * Setter for the Current Application Context Base Url to be used for retrieving gadget metadata from the container.
     *
     * @param inCurrentAppContextBaseUrl
     *            - base url for the gadget metadata fetcher.
     */
    public void setCurrentAppContextBaseUrl(final String inCurrentAppContextBaseUrl)
    {
        currentAppContextBaseUrl = inCurrentAppContextBaseUrl;
    }

    /**
     * Retrieve the gadget metadata for the gadget definitions passed into the class.
     *
     * @param gadgetDefs
     *            Map of gadget definitions with the string key as the gadget def url.
     * @return List of GadgetMetaData objects that contain the metadata for the Gadget definitions passed in.
     * @throws Exception
     *             Error occurs on retrieving gadget metadata.
     */
    public List<GadgetMetaDataDTO> getGadgetsMetaData(final Map<String, GeneralGadgetDefinition> gadgetDefs)
            throws Exception
    {
        if (gadgetDefs.isEmpty())
        {
            return Collections.EMPTY_LIST;
        }

        try
        {
            // Make the http request here.
            StringWriter output = new StringWriter();
            URL endpoint = new URL(currentAppContextBaseUrl + "/gadgets/metadata");
            logger.debug("Target url for metadata request " + endpoint.toString());
            HttpURLConnection urlConnection = null;
            try
            {
                // Open the HttpConnection and make the POST.
                urlConnection = (HttpURLConnection) endpoint.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setAllowUserInteraction(false);
                urlConnection.setRequestProperty("Content-type", "application/json");
                OutputStream out = urlConnection.getOutputStream();
                try
                {
                    Writer writer = new OutputStreamWriter(out, "UTF-8");
                    pipe(new StringReader(assembleMetaDataRequestContext(gadgetDefs)), writer);
                    writer.close();
                }
                catch (IOException e)
                {
                    String ioErrorMsg = "IOException occurred writing params to outputstream.";
                    logger.error(ioErrorMsg, e);
                    throw new Exception(ioErrorMsg);
                }
                finally
                {
                    if (out != null)
                    {
                        out.close();
                    }
                }

                // Retrieve the response.
                InputStream in = urlConnection.getInputStream();
                try
                {
                    Reader reader = new InputStreamReader(in);
                    pipe(reader, output);
                    reader.close();
                }
                catch (IOException iox)
                {
                    String ioErrorMsg = "IOException occurred reading response from request";
                    logger.error(ioErrorMsg, iox);
                    throw iox;
                }
                finally
                {
                    if (in != null)
                    {
                        in.close();
                    }
                }

                logger.debug("This is the output of the MetaData request: " + output.toString());
            }
            catch (IOException ioex)
            {
                throw ioex;
            }
            finally
            {
                if (urlConnection != null)
                {
                    urlConnection.disconnect();
                }
            }

            return mapGadgetMetaDataJSONToObject(output.toString(), gadgetDefs);
        }
        catch (Exception ex)
        {
            String msg = "Error occurred retrieving gadget metadata " + ex;
            logger.error(msg, ex);
            throw ex;
        }
    }

    /**
     * This method converts the MetaData JSON into a List of GadgetMetaData objects.
     *
     * @param inGadgetsJSON
     *            raw results from the metadata request.
     * @param gadgetDefs
     *            Map of gadget definitions with the string key as the gadget def url.
     * @return List of GadgetMetaData objects from the request.
     */
    private List<GadgetMetaDataDTO> mapGadgetMetaDataJSONToObject(final String inGadgetsJSON,
            final Map<String, GeneralGadgetDefinition> gadgetDefs)
    {
        List<GadgetMetaDataDTO> currentGadgetMetaData = new ArrayList<GadgetMetaDataDTO>();
        JSONObject gadgetsMetaData = JSONObject.fromObject(inGadgetsJSON.toString());
        JSONArray gadgetsJSON = gadgetsMetaData.getJSONArray("gadgets");
        GadgetMetaDataDTO currentGadget;

        for (int index = 0; index < gadgetsJSON.size(); index++)
        {
            JSONObject gadgetJSON = gadgetsJSON.getJSONObject(index);
            currentGadget = new GadgetMetaDataDTO(gadgetDefs.get(gadgetJSON.get("url")));
            // set the user prefs here as a list.
            currentGadget.setTitle(gadgetJSON.containsKey("title") ? gadgetJSON.getString("title") : "");
            currentGadget.setAuthor(gadgetJSON.containsKey("author") ? gadgetJSON.getString("author") : "");
            currentGadget.setDescription(gadgetJSON.containsKey("description") ? gadgetJSON.getString("description")
                    : "");
            JSONObject gadgetUserPrefsJSON = gadgetJSON.getJSONObject("userPrefs");
            List<UserPrefDTO> userPrefs = new ArrayList<UserPrefDTO>();
            UserPrefDTO currentUserPref;
            JSONObject currentUserPrefJSON;
            String currentUserPrefKey;
            for (Object userPrefKey : gadgetUserPrefsJSON.keySet())
            {
                currentUserPrefKey = (String) userPrefKey;
                currentUserPrefJSON = gadgetUserPrefsJSON.getJSONObject(currentUserPrefKey);
                currentUserPref = new UserPrefDTO();
                currentUserPref.setName(currentUserPrefKey);
                currentUserPref.setDisplayName(currentUserPrefJSON.getString("displayName"));
                currentUserPref.setDataType(currentUserPrefJSON.getString("type"));
                currentUserPref.setDefaultValue(currentUserPrefJSON.getString("default"));
                if (currentUserPref.getDataType().name() == DataType.ENUM.name())
                {
                    // Unordered easy access map of enum values.
                    Map<String, String> enumValues = new HashMap<String, String>();
                    String enumKey;
                    String userPrefEnumValue;
                    // parse enum values here
                    for (Object enumKeyObject : currentUserPrefJSON.getJSONObject("enumValues").keySet())
                    {
                        enumKey = (String) enumKeyObject;
                        userPrefEnumValue = (String) currentUserPrefJSON.getJSONObject("enumValues").get(enumKey);
                        enumValues.put(enumKey, userPrefEnumValue);
                    }
                    currentUserPref.setEnumValues(enumValues);

                    // Ordered enum values for creating preferences.
                    List<EnumValuePairDTO> orderedEnumValues = new LinkedList<EnumValuePairDTO>();
                    JSONArray orderedEnumValuesJson = currentUserPrefJSON.getJSONArray("orderedEnumValues");
                    JSONObject currentOrderedEnumValueJson;
                    for (int arrayIndex = 0; arrayIndex < orderedEnumValuesJson.size(); arrayIndex++)
                    {
                        currentOrderedEnumValueJson = orderedEnumValuesJson.getJSONObject(arrayIndex);
                        orderedEnumValues.add(new EnumValuePairDTO(currentOrderedEnumValueJson.getString("value"),
                                currentOrderedEnumValueJson.getString("displayValue")));
                    }
                    currentUserPref.setOrderedEnumValues(orderedEnumValues);
                }

                if (currentUserPrefJSON.containsKey("required"))
                {
                    currentUserPref.setRequired(currentUserPrefJSON.getString("required"));
                }
                userPrefs.add(currentUserPref);
            }
            currentGadget.setUserPrefs(userPrefs);
            currentGadgetMetaData.add(currentGadget);
        }
        return currentGadgetMetaData;
    }

    /**
     * This method assembles the context for the metadata request to the OpenSocial container.
     *
     * @param gadgetDefs
     *            Map of gadget definitions with the string key as the gadget def url.
     * @return String representation of the JSON Context.
     */
    private String assembleMetaDataRequestContext(final Map<String, GeneralGadgetDefinition> gadgetDefs)
    {
        // Assemble the json for requesting the metadata for a gadget.
        JSONObject metaDataParams = new JSONObject();
        JSONObject contextParams = new JSONObject();
        contextParams.put("country", "default");
        contextParams.put("language", "default");
        contextParams.put("view", "preview");
        contextParams.put("container", "eureka");
        metaDataParams.put("context", contextParams);

        JSONArray gadgetParams = new JSONArray();
        JSONObject gadgetDefParam;
        for (Entry<String, GeneralGadgetDefinition> currentGadgetDef : gadgetDefs.entrySet())
        {
            gadgetDefParam = new JSONObject();
            gadgetDefParam.put("url", currentGadgetDef.getKey());
            gadgetDefParam.put("moduleId", currentGadgetDef.getValue().getId());
            gadgetParams.add(gadgetDefParam);
        }
        metaDataParams.put("gadgets", gadgetParams);

        logger.debug("Created JSON for metadata request: " + metaDataParams.toString());

        return metaDataParams.toString();
    }

    /**
     * Helper method for copying a Reader into a writer.
     *
     * @param reader
     *            - containing the reader content to be written.
     * @param writer
     *            - target writer.
     * @throws IOException
     *             on stream errors.
     */
    private static void pipe(final Reader reader, final Writer writer) throws IOException
    {
        char[] buf = new char[CHAR_BYTE];
        int read = 0;
        while ((read = reader.read(buf)) >= 0)
        {
            writer.write(buf, 0, read);
        }
        writer.flush();
    }
}
