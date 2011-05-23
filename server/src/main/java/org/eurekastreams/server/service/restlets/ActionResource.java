/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import javax.resource.spi.IllegalStateException;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.eurekastreams.commons.actions.context.ClientPrincipalActionContextImpl;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.eurekastreams.server.service.actions.strategies.ApplicationContextHolder;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.springframework.util.Assert;

/**
 * Endpoint for the API (invoking actions).
 */
public class ActionResource extends SmpResource
{
    /** Logger. */
    private final Log log = LogFactory.getLog(ActionResource.class);

    /** Service Action Controller. */
    private final ActionController serviceActionController;

    /** Principal populators. */
    private final List<Transformer<Request, Principal>> principalExtractors;

    /** Client populators. */
    private final List<Transformer<Request, String>> clientExtractors;

    /** JSON Factory for building JSON Generators. */
    private final JsonFactory jsonFactory;

    /** Holds the app context. */
    private final ApplicationContextHolder applicationContextHolder;

    /** Only allow read-only actions. */
    private final boolean readOnly;

    /**
     * User principal.
     */
    private Principal principal;

    /**
     * The action to execute.
     */
    private String actionKey;

    /**
     * The request type.
     */
    private String requestType;

    /**
     * Parameters to the restlet in JSON. Contains the request and any other needed data.
     */
    private String paramsJSON;

    /** ID of client submitting the request. */
    private String clientUniqueId;

    /**
     * Default constructor.
     * 
     * @param inServiceActionController
     *            the action controller.
     * @param inPrincipalExtractors
     *            the principal extractors.
     * @param inClientExtractors
     *            Strategies to extract the client.
     * @param inJsonFactory
     *            the json factory.
     * @param inApplicationContextHolder
     *            app context holder.
     * @param inReadOnly
     *            Only allow read-only actions.
     */
    public ActionResource(final ActionController inServiceActionController,
            final List<Transformer<Request, Principal>> inPrincipalExtractors,
            final List<Transformer<Request, String>> inClientExtractors, final JsonFactory inJsonFactory,
            final ApplicationContextHolder inApplicationContextHolder, final boolean inReadOnly)
    {
        serviceActionController = inServiceActionController;
        principalExtractors = inPrincipalExtractors;
        clientExtractors = inClientExtractors;
        jsonFactory = inJsonFactory;
        applicationContextHolder = inApplicationContextHolder;
        readOnly = inReadOnly;
    }

    /**
     * init the params.
     *
     * @param request
     *            the request object.
     */
    @Override
    protected void initParams(final Request request)
    {
        principal = getPrincipal(request);
        clientUniqueId = getClient(request);
        Assert.notNull(principal, "Principal object cannot be null.");
        actionKey = (String) request.getAttributes().get("action");
        requestType = (String) request.getAttributes().get("requestType");
        try
        {
            paramsJSON = URLDecoder.decode((String) request.getAttributes().get("paramsJSON"), "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new ExecutionException(ex);
        }
    }

    /**
     * GET the activites.
     *
     * @param variant
     *            the variant.
     * @return the JSON.
     * @throws ResourceException
     *             the exception.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        String jsString = "";

        try
        {
            // get the action
            Object springBean = applicationContextHolder.getContext().getBean(actionKey);

            // get parameter
            Serializable actionParameter = getRequestObject();

            // create ServiceActionContext.
            PrincipalActionContext actionContext = new ClientPrincipalActionContextImpl(actionParameter, principal,
                    clientUniqueId);
            actionContext.setActionId(actionKey);

            log.debug("executing action: " + actionKey + " for user: " + principal.getAccountId());

            Serializable result = "empty result";
            if (springBean instanceof ServiceAction)
            {
                ServiceAction action = (ServiceAction) springBean;
                if (readOnly && !action.isReadOnly())
                {
                    throw new IllegalStateException("Action requested is not read-only.");
                }
                result = serviceActionController.execute(actionContext, action);
            }
            else if (springBean instanceof TaskHandlerServiceAction)
            {
                TaskHandlerServiceAction action = (TaskHandlerServiceAction) springBean;
                if (readOnly && !action.isReadOnly())
                {
                    throw new IllegalStateException("Action requested is not read-only.");
                }
                result = serviceActionController.execute(actionContext, action);
            }

            StringWriter writer = new StringWriter();
            JsonGenerator jsonGenerator = jsonFactory.createJsonGenerator(writer);
            jsonGenerator.writeObject(result);
            writer.close();

            jsString = writer.toString();
        }
        catch (Exception ex)
        {
            log.error("Error excecuting action " + actionKey + " from restlet.", ex);
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Error executing action.");
        }

        Representation rep = new StringRepresentation(jsString, MediaType.TEXT_PLAIN);
        rep.setExpirationDate(new Date(0L));
        return rep;
    }

    /**
     * Returns Principal for given account id.
     *
     * @param inRequest
     *            Request to get principal from.
     * @return Principal for given account id.
     */
    private Principal getPrincipal(final Request inRequest)
    {
        for (Transformer<Request, Principal> extractor : principalExtractors)
        {
            Principal result = extractor.transform(inRequest);
            if (result != null)
            {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the client used for the request (if available).
     *
     * @param inRequest
     *            Request to get client from.
     * @return Client for the current request.
     */
    private String getClient(final Request inRequest)
    {
        for (Transformer<Request, String> extractor : clientExtractors)
        {
            String result = extractor.transform(inRequest);
            if (result != null)
            {
                return result;
            }
        }
        return null;
    }

    /**
     * Go from JSON to Request object.
     *
     * @return the request object.
     * @throws Exception
     *             possible exceptions.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Serializable getRequestObject() throws Exception
    {
        if (requestType.toLowerCase().equals("null"))
        {
            return null;
        }

        final JSONObject paramsAsObject = JSONObject.fromObject(paramsJSON);

        if (requestType.toLowerCase().equals("long"))
        {
            return paramsAsObject.getLong("request");
        }
        else if (requestType.toLowerCase().equals("int"))
        {
            return paramsAsObject.getInt("request");
        }
        else if (requestType.toLowerCase().equals("string"))
        {
            return paramsAsObject.getString("request");
        }
        else if (requestType.toLowerCase().equals("boolean"))
        {
            return paramsAsObject.getBoolean("request");
        }
        else
        {
            Class returnType = Class.forName(requestType);

            String requestAsString = paramsAsObject.getString("request");
            ObjectMapper objMapper = new ObjectMapper();
            Object requestAsObject = objMapper.readValue(requestAsString, returnType);

            /*
             * This is nicer, but some of our types don't deserialize properly with it. JSONObject requestAsJson =
             * paramsAsObject.getJSONObject("request"); Object requestAsObject = JSONObject.toBean(requestAsJson,
             * returnType);
             */

            return (Serializable) returnType.cast(requestAsObject);
        }
    }
}
