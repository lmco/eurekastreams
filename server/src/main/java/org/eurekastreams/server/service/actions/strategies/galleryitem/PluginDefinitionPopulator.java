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
package org.eurekastreams.server.service.actions.strategies.galleryitem;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.GeneralGadgetDefinition;
import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.server.domain.gadgetspec.UserPrefDTO;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.plugins.PluginDefinition;
import org.eurekastreams.server.service.opensocial.gadgets.spec.GadgetMetaDataFetcher;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * populates a plugin.
 * 
 */
public class PluginDefinitionPopulator implements GalleryItemPopulator<PluginDefinition>
{
    /**
     * Error if object type is not recognized.
     */
    static final String OBJECTTYPE_ERROR = "Unrecognized ObjectType. ";

    /**
     * Error message if UpdateFrequency is of an invalid value.
     */
    static final String UPDATE_FREQUENCY_ERROR = "UpdateFrequency is not a valid value. ";

    /**
     * Error message if required feature is not present.
     */
    static final String FEATURE_ERROR = "Required plugin feature not present. ";

    /**
     * Error message if plugin url is not valid.
     */
    static final String CANT_FIND_PLUGIN = "Plugin could not be located at that url. ";

    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(PluginDefinitionPopulator.class);

    /**
     * Meta data fetcher.
     */
    private GadgetMetaDataFetcher metaDataFetcher = null;

    /**
     * the data to enter if nothing is given.
     */
    private Long defaultUpdateFrequency;

    /**
     * the data to enter if nothing is given.
     */
    private BaseObjectType defaultObjectType;

    /**
     * Constructor.
     * 
     * @param inMetaDataFetcher
     *            meta data fetcher.
     * @param inDefaultUpdateFrequency
     *            the default value if it is not in the plugin.
     * @param inDefaultObjectType
     *            the default value if it is not in the plugin.
     */
    public PluginDefinitionPopulator(final GadgetMetaDataFetcher inMetaDataFetcher,
            final Long inDefaultUpdateFrequency, final BaseObjectType inDefaultObjectType)
    {
        metaDataFetcher = inMetaDataFetcher;
        defaultUpdateFrequency = inDefaultUpdateFrequency;
        defaultObjectType = inDefaultObjectType;
    }

    /**
     * Populates a plugin definition.
     * 
     * @param inPluginDefinition
     *            the plugin definition to populate
     * @param inPluginDefinitionUrl
     *            the plugin definition url.
     */
    @Override
    public void populate(final PluginDefinition inPluginDefinition,
            final String inPluginDefinitionUrl)
    {
        boolean hasFeature = false;

        ValidationException ve = new ValidationException();

        String errorMessage = "";

        // ensure the gadget is a plugin type.
        try
        {
            hasFeature = checkForRequiredFeature(inPluginDefinitionUrl);
        }
        catch (Exception ex)
        {
            log.debug(CANT_FIND_PLUGIN, ex);
            ve.addError("url", CANT_FIND_PLUGIN);
            throw ve;
        }

        final Map<String, GeneralGadgetDefinition> gadgetDefs = new HashMap<String, GeneralGadgetDefinition>();
        gadgetDefs.put(inPluginDefinitionUrl, inPluginDefinition);

        log.info("Fetching gadget data");

        try
        {
            List<GadgetMetaDataDTO> meta = metaDataFetcher.getGadgetsMetaData(gadgetDefs);

            if (meta.size() > 0)
            {
                /*
                 * These two fields are the only fields being populated because the other values are populate via
                 * Shindig on each ui request.
                 */

                GadgetMetaDataDTO metadata = meta.get(0);

                for (UserPrefDTO up : metadata.getUserPrefs())
                {
                    if ("updateFrequency".equals(up.getName()))
                    {
                        try
                        {
                            Long updateFreq = Long.parseLong(up.getDefaultValue());
                            inPluginDefinition.setUpdateFrequency(updateFreq);
                        }
                        catch (Exception ex)
                        {
                            errorMessage += UPDATE_FREQUENCY_ERROR;
                        }
                    }
                    else if ("objectType".equals(up.getName()))
                    {
                        try
                        {
                            inPluginDefinition.setObjectType(BaseObjectType.valueOf(up.getDefaultValue()));
                        }
                        catch (Exception ex)
                        {
                            errorMessage += OBJECTTYPE_ERROR;
                        }
                    }
                }
            }

            // Check for all required elements
            if (!hasFeature)
            {
                errorMessage += FEATURE_ERROR;
            }

            // add see if a error message was created. If so add it as a validation exceptions. If not check for
            // defaults.
            if (!errorMessage.equals(""))
            {
                ve.addError("url", errorMessage);

            }
            else
            {
                if (inPluginDefinition.getObjectType() == null)
                {
                    inPluginDefinition.setObjectType(defaultObjectType);
                }
                if (inPluginDefinition.getUpdateFrequency() == null)
                {
                    inPluginDefinition.setUpdateFrequency(defaultUpdateFrequency);
                }
            }

            // Check if any errors exist
            if (!ve.getErrors().isEmpty())
            {
                throw ve;
            }

        }
        catch (ValidationException ve2)
        {
            throw ve2;
        }
        catch (Exception ex)
        {
            ve.addError("url", "Error retreiving plugin data.");
            log.debug(ex.fillInStackTrace());
            throw ve;
        }

    }

    /**
     * Checks for the required plugin feature before updating the plugin def.
     * 
     * @param inPluginDefinitionUrl
     *            The url to the plugin definition.
     * @return true if the feature is found and false if not found.
     * @throws XPathExpressionException
     *             not expected.
     * @throws ParserConfigurationException
     *             not expected.
     * @throws SAXException
     *             not expected.
     * @throws IOException
     *             expected if the url is invalid.
     */
    private boolean checkForRequiredFeature(final String inPluginDefinitionUrl) throws XPathExpressionException,
            ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(inPluginDefinitionUrl);
        XPath xpath = XPathFactory.newInstance().newXPath();

        // this is used to get the amount of required feature we need to check.
        XPathExpression getFeatureAmountExpr = xpath.compile("//Module/ModulePrefs/Require");

        NodeList nodeResults = (NodeList) getFeatureAmountExpr.evaluate(doc, XPathConstants.NODESET);

        for (int i = 1; i <= nodeResults.getLength(); i++)
        {
            XPathExpression getFeatureExpr = xpath.compile("//Module/ModulePrefs/Require[" + i + "]/@feature");
            String thing = (String) getFeatureExpr.evaluate(doc, XPathConstants.STRING);
            if (thing.equals("eurekastreams-streamplugin"))
            {
                return true;
            }
        }
        return false;
    }
}
