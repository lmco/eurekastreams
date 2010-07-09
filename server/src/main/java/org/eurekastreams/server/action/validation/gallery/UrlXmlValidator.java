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
package org.eurekastreams.server.action.validation.gallery;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.service.actions.strategies.ContextHolder;
import org.eurekastreams.server.service.actions.strategies.ResourceFetcher;

/**
 * Implementation of GalleryItemValidator that validates XML from provided URL via provided in schema/XSD.
 *
 */
public class UrlXmlValidator implements ValidationStrategy<ServiceActionContext>
{
    /**
     * url key.
     */
    private static final String URL_KEY = "url";

    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(UrlXmlValidator.class);

    /**
     * Schema String to use.
     */
    private String schemaString;

    /**
     * Path to xsd file. Ex. /themes/openSocialGadget_0-9.xsd
     */
    private String xsdPath;

    /**
     * The ServletContext is used to figure out where files belong.
     */
    private ContextHolder contextHolder;

    /**
     * Fetcher for the XML theme definition.
     */
    private ResourceFetcher xmlFetcher = null;

    /**
     * Constructor.
     *
     * @param inSchema
     *            The schema string.
     * @param inXsdPath
     *            The path to xsd file.
     * @param inResourceFetcher
     *            The ResourceFetcher implementation.
     * @param inContextHolder
     *            The contextHolder object.
     */
    public UrlXmlValidator(final String inSchema, final String inXsdPath, final ResourceFetcher inResourceFetcher,
            final ContextHolder inContextHolder)
    {
        schemaString = inSchema;
        xsdPath = inXsdPath;
        xmlFetcher = inResourceFetcher;
        contextHolder = inContextHolder;
    }

    /**
     * Validates xml from given Url returning true if it passes validation, false otherwise.
     *
     * @param inActionContext
     *            the action context
     * @throws ValidationException
     *             on error
     */
    @Override
    @SuppressWarnings("unchecked")
    public void validate(final ServiceActionContext inActionContext) throws ValidationException
    {
        Map<String, Serializable> fields = (Map<String, Serializable>) inActionContext.getParams();
        String galleryItemUrl = (String) fields.get(URL_KEY);
        try
        {
            String fullXsdPath = contextHolder.getContext().getRealPath("/") + xsdPath;
            log.debug("Attempt to validate xml at: " + galleryItemUrl + "with xsd at: " + fullXsdPath);

            SchemaFactory schemaFactory = SchemaFactory.newInstance(schemaString);
            Schema schema = schemaFactory.newSchema(new StreamSource(fullXsdPath));

            Validator validator = schema.newValidator();

            // TODO: stuff the input stream into the context for execute()
            InputStream galleryItemInputStream = xmlFetcher.getInputStream(galleryItemUrl);

            validator.validate(new StreamSource(galleryItemInputStream));

            log.debug("Success validating xml at: " + galleryItemUrl);
        }
        catch (Exception e)
        {
            ValidationException ve = new ValidationException();
            ve.addError(URL_KEY, "Valid url is required");
            throw ve;
        }
    }

}
