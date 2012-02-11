/*
 * Copyright (c) 2009-2012 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.restlets;

import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.GeneralGadgetDefinition;
import org.eurekastreams.server.domain.gadgetspec.EnumValuePairDTO;
import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.server.domain.gadgetspec.UserPrefDTO;
import org.eurekastreams.server.domain.gadgetspec.UserPrefDTO.DataType;
import org.eurekastreams.server.persistence.GadgetMapper;
import org.eurekastreams.server.service.opensocial.gadgets.spec.GadgetMetaDataFetcher;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * This class provides the Restlet implementation for creating user preferences form ui for an OpenSocial gadget.
 *
 */
public class UserPrefsFormUIResource extends SmpResource
{
    /**
     * Local logger instance.
     */
    private static Log logger = LogFactory.getLog(UserPrefsFormUIResource.class);

    /**
     * Key for retrieving the Gadget Id from the rest url.
     */
    private static final String MODULE_ID_KEY = "moduleid";

    /**
     * Key for retrieving the Gadget Definition Url from the rest url.
     */
    private static final String GADGET_DEF_URL_KEY = "url";

    /**
     * Key for retrieving the saved user preferences.
     */
    private static final String SAVED_USER_PREFS = "saveduserprefs";

    /**
     * Error message when user preferences cannot be retrieved.
     */
    private static final String USER_PREFS_ERROR_MESSAGE = "Error occurred retrieving preferences for this gadget.";

    /**
     * Default format for encoding/decoding url's retrieved in parameters.
     */
    private static final String DEFAULT_URL_ENCODING = "UTF-8";

    /**
     * Local storage for the module Id of the calling gadget.
     */
    private Long moduleId;

    /**
     * List of user preferences retrieved from the url passed in.
     */
    private Map<String, String> savedUserPrefs;

    /**
     * Local instance of the gadget mapper.
     */
    private GadgetMapper gadgetMapper;

    /**
     * Local instance of the gadget metadata fetcher.
     */
    private GadgetMetaDataFetcher gadgetMetaFetcher;

    /**
     * URI for the gadget definition to retrieve user preferences for.
     */
    private String gadgetDefUrl;

    /**
     * Initialize the parameters for the request. attributes handled: moduleId - refers to the gadget instance id (not
     * to be confused with the application id. url - gadget definition url target to generate preferences script for.
     * saveduserprefs - querystring formatted (and url encoded) collection of key value pairs representing the currently
     * saved user settings. This data is important for generating prepopulated inputs.
     *
     * @param request
     *            - current request information.
     */
    @Override
    protected void initParams(final Request request)
    {
        // Retrieve the module id and gadget def url for the
        // metadata request.
        if (request.getAttributes().containsKey(MODULE_ID_KEY)
                && request.getAttributes().containsKey(GADGET_DEF_URL_KEY))
        {
            try
            {
                URI tempGadgetDefURI = new URI((String) request.getAttributes().get(GADGET_DEF_URL_KEY));
                gadgetDefUrl = URLDecoder.decode(tempGadgetDefURI.toString(), DEFAULT_URL_ENCODING);
                String tempAppId = (String) request.getAttributes().get(MODULE_ID_KEY);
                logger.debug("Retreived moduleId from querystring: " + tempAppId);
                moduleId = Long.parseLong(tempAppId.trim());
            }
            catch (Exception ex)
            {
                logger.error("Error occurred retrieving the module id from " + "the request, defaulting to 0 " + ex);
                moduleId = 0L;
            }
        }

        // Saved User Preferences are optional and thus pulled off the request seperately.
        savedUserPrefs = new HashMap<String, String>();

        if (request.getAttributes().containsKey(SAVED_USER_PREFS))
        {
            String urlUserPreferences = (String) request.getAttributes().get(SAVED_USER_PREFS);
            List<String> tempUserPrefsList = new ArrayList<String>(Arrays.asList(urlUserPreferences.split("&")));
            for (String userPref : tempUserPrefsList)
            {
                if ((userPref.trim().length() > 3) && userPref.trim().contains("="))
                {
                    try
                    {
                        String[] userPrefBreakdown = userPref.split("=");
                        savedUserPrefs.put(URLDecoder.decode(userPrefBreakdown[0], DEFAULT_URL_ENCODING),
                                URLDecoder.decode(userPrefBreakdown[1], DEFAULT_URL_ENCODING));
                    }
                    catch (Exception ex)
                    {
                        logger.error("Error occurred retrieving the user " + "preferences from the url string for: "
                                + userPref);
                    }
                }
            }
        }
    }

    /**
     * This method injects the gadget mapper.
     *
     * @param inGadgetMapper
     *            mapper.
     */
    public void setGadgetMapper(final GadgetMapper inGadgetMapper)
    {
        gadgetMapper = inGadgetMapper;
    }

    /**
     * This method injects the gadget metadata fetcher.
     *
     * @param inGadgetMetaFetcher
     *            - instance of GadgetMetaFetcher to set locally.
     */
    public void setGadgetMetaDataFetcher(final GadgetMetaDataFetcher inGadgetMetaFetcher)
    {
        gadgetMetaFetcher = inGadgetMetaFetcher;
    }

    /**
     * Provide the settings output for the gadget definitions supplied.
     *
     * @param variant
     *            - context.
     * @return Representation of the response.
     *
     * @throws ResourceException
     *             when an error is encountered with the representation code.
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        // retrieve gadget metadata with preferences from shindig.
        // assembly script based on types and number of preferences.
        if (moduleId != null && moduleId > 0 && gadgetDefUrl != null)
        {
            StringBuilder sb = new StringBuilder();
            try
            {
                Map<String, GeneralGadgetDefinition> gadgetDefs = new HashMap<String, GeneralGadgetDefinition>();
                GadgetDefinition targetGadgetDef = gadgetMapper.findById(moduleId).getGadgetDefinition();
                gadgetDefs.put(gadgetDefUrl, targetGadgetDef);
                List<GadgetMetaDataDTO> gadgetMetaData = gadgetMetaFetcher.getGadgetsMetaData(gadgetDefs);
                for (GadgetMetaDataDTO currentGMeta : gadgetMetaData)
                {
                    logger.debug("Retrieved Metadata for Gadget" + targetGadgetDef.getId() + " Author: "
                            + currentGMeta.getAuthor() + " UserPrefsSize: " + currentGMeta.getUserPrefs().size());
                    buildUserPreferencesUI(currentGMeta, sb);
                }
            }
            catch (Exception ex)
            {
                logger.error("Error occurred retrieving User Preferences script", ex);
                sb.append("var htmlString_" + moduleId + "='';");
                sb.append("htmlString_" + moduleId + " += '<div class=\"gadget-preferences\">"
                        + USER_PREFS_ERROR_MESSAGE + "</div>';");
                sb.append("gadget_callback_" + moduleId + "(htmlString_" + moduleId + ");");
            }
            logger.debug("Returning the edit settings script: " + sb.toString());
            Representation rep = new StringRepresentation(sb.toString(), MediaType.APPLICATION_JAVASCRIPT);
            rep.setExpirationDate(new Date());
            return rep;
        }
        else
        {
            logger.error("Bad request:  Invalid application id passed to endpoint");
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Invalid application id passed to endpoint");
        }
    }

    /**
     * This method constructs the user preferences for the gadget MetaData supplied.
     *
     * @param gadgetMetaData
     *            GadgetMetaDataDTO object that contains the info for retrieving the User Preferences ui.
     * @param sb
     *            String builder to which the string representation of the user preferences ui will be appended.
     */
    private void buildUserPreferencesUI(final GadgetMetaDataDTO gadgetMetaData, final StringBuilder sb)
    {
        // initial wrappings
        sb.append("var htmlString_").append(moduleId).append("='");

        int userPrefIndex = 0;
        String userPrefPrefix = "m_" + moduleId;
        sb.append("<div style=\"display:none\" class=\"gadget-preferences\">");
        for (UserPrefDTO userPref : gadgetMetaData.getUserPrefs())
        {
            // If the value has already been saved, use the saved value.
            if (savedUserPrefs.size() > 0 && savedUserPrefs.containsKey("up_" + userPref.getName()))
            {
                userPref.setDefaultValue(savedUserPrefs.get("up_" + userPref.getName()));
            }

            final DataType dataType = userPref.getDataType();
            final String inputId = userPrefPrefix + "_" + userPrefIndex;
            final String inputName = userPrefPrefix + "_up_" + userPref.getName();
            if (dataType == DataType.HIDDEN)
            {
                sb.append("<input type=\"hidden\" id=\"").append(inputId).append("\" name=\"").append(inputName)
                        .append("\" value=\"").append(StringEscapeUtils.escapeHtml(userPref.getDefaultValue()))
                        .append("\" />");
            }
            else
            {
                sb.append("<div class=\"input-area\"><div class=\"label-area\"><label class=\"label\">");
                sb.append(userPref.getDisplayName());
                sb.append("</label></div><div class=\"input-box\">");

                if (dataType == DataType.ENUM)
                {
                    sb.append("<select class=\"drop-down\" id=\"").append(inputId).append("\" name=\"")
                            .append(inputName).append("\">");
                    for (EnumValuePairDTO currentEnumValue : userPref.getOrderedEnumValues())
                    {
                        sb.append("<option value=\"").append(currentEnumValue.getValue()).append("\" ");
                        if (userPref.getDefaultValue().equals(currentEnumValue.getValue()))
                        {
                            sb.append("selected ");
                        }
                        sb.append(">").append(currentEnumValue.getDisplayValue()).append("</option>");
                    }
                    sb.append("</select>");
                }
                else if (dataType.name() == DataType.LIST.name()) // == LIST
                {
                    // TODO: handle list here with a bunch of checkboxes.
                    logger.info("Handle user preferences type List");

                }
                else if (dataType.name() == DataType.BOOL.name()) // == BOOL
                {
                    sb.append("<input type=\"checkbox\" class=\"checkbox\" id=\"").append(inputId).append("\" name=\"")
                            .append(inputName).append("\" ");
                    try
                    {
                        if (Boolean.parseBoolean(userPref.getDefaultValue())
                                || Integer.parseInt(userPref.getDefaultValue()) > 0)
                        {
                            sb.append("checked ");
                        }
                    }
                    catch (NumberFormatException nex)
                    {
                        logger.info("Data passed in for boolean user preference"
                                + "was not correct string or number format, assuming false.");
                    }
                    sb.append("value=\"true\" onClick=\"document.getElementById(\\'").append(inputId)
                            .append("\\').value = (this.checked ? \\'1\\' : \\'0\\')\" />");
                }
                else
                {
                    sb.append("<input type=\"text\" class=\"text-box\" id=\"").append(inputId).append("\" name=\"")
                            .append(inputName).append("\" value=\"")
                            .append(StringEscapeUtils.escapeHtml(userPref.getDefaultValue().replace("'", "\\'")))
                            .append("\" />");
                }

                if (userPref.getRequired())
                {
                    sb.append("<div>Required</div>");
                }
                sb.append("</div></div>"); // closes input-area and input-box
            }

            userPrefIndex++;
        }
        // sb.append("</div>");
        sb.append("<input type=\"hidden\" id=\"").append(userPrefPrefix).append("_numfields\" value=\"")
                .append(gadgetMetaData.getUserPrefs().size()).append("\"/>");
        sb.append("</div>");

        // final wrappings
        sb.append("';gadget_callback_").append(moduleId).append("(unescape(htmlString_").append(moduleId).append("));");
    }
}
