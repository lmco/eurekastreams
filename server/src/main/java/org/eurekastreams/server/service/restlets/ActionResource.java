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
package org.eurekastreams.server.service.restlets;

import java.io.Serializable;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.commons.client.ActionRequest;
import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.ApplicationContextHolder;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * REST end point for stream filters.
 *
 */
public class ActionResource extends SmpResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(ActionResource.class);

    /**
     * Open Social Id. TODO: this should be eliminated when we have OAuth.
     */
    private String accountid;

    /**
     * Service Action Controller.
     */
    private ActionController serviceActionController;

    /**
     * Principal populator.
     */
    private PrincipalPopulator principalPopulator;

    /**
     * JSONP Callback.
     */
    private String callback;

    /**
     * The action to execute.
     */
    private String actionKey;

    /**
     * The request type.
     */
    private String requestType;

    /**
     * The request JSON.
     */
    private String requestJSON;

    /**
     * JSON Factory for building JSON Generators.
     */
    private JsonFactory jsonFactory;

    /**
     * Holds the app context.
     */
    private ApplicationContextHolder applicationContextHolder;

    /**
     * Can this action be executed by anyone.
     */
    private boolean anonymous;

    /**
     * Person mapper.
     */
    private DomainMapper<String, PersonModelView> getPersonMVByAccountId;

    /**
     * Default constructor.
     *
     * @param inServiceActionController
     *            the action controller.
     * @param inPrincipalPopulator
     *            the principal populator.
     * @param inJsonFactory
     *            the json factory.
     * @param inApplicationContextHolder app context holder.
     * @param inGetPersonMVByAccountId get person.
     * @param inAnonymous is this servlet anonymous?
     */
    public ActionResource(final ActionController inServiceActionController,
            final PrincipalPopulator inPrincipalPopulator, final JsonFactory inJsonFactory,
            final ApplicationContextHolder inApplicationContextHolder,
            final DomainMapper<String, PersonModelView> inGetPersonMVByAccountId,
            final boolean inAnonymous)
    {
        serviceActionController = inServiceActionController;
        principalPopulator = inPrincipalPopulator;
        jsonFactory = inJsonFactory;
        applicationContextHolder = inApplicationContextHolder;
        getPersonMVByAccountId = inGetPersonMVByAccountId;
        anonymous = inAnonymous;
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
        accountid = (String) request.getAttributes().get("accountid");

        if (anonymous) // It's anonymous, trust what they tell us in the header or the URL, in that order.
        {
            if (accountid == null)
            {
                accountid = (String) request.getAttributes().get("accountidFromUrl");
            }
        }

        callback = (String) request.getAttributes().get("callback");
        actionKey = (String) request.getAttributes().get("action");
        requestType = (String) request.getAttributes().get("requestType");
        requestJSON = URLDecoder.decode((String) request.getAttributes().get("requestJSON"));
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
            PersonModelView pmv = getPersonMVByAccountId.execute(accountid);
            Serializable actionParameter = getRequestObject();
            log.debug("executing action for user: " + accountid);
            ActionRequest request = new ActionRequestImpl(actionKey, actionParameter);
            Object springBean = applicationContextHolder.getContext().getBean(request.getActionKey());
            Serializable result = "empty result";

            if (springBean instanceof ServiceAction)
            {
                ServiceAction action = (ServiceAction) springBean;

                ServiceActionContext actionContext = new ServiceActionContext(actionParameter, principalPopulator
                        .getPrincipal(pmv.getOpenSocialId()));
                actionContext.setActionId(request.getActionKey());
                result = serviceActionController.execute(actionContext, action);
            }
            else if (springBean instanceof TaskHandlerServiceAction)
            {
                TaskHandlerServiceAction action = (TaskHandlerServiceAction) springBean;

                ServiceActionContext actionContext = new ServiceActionContext(actionParameter, principalPopulator
                        .getPrincipal(pmv.getOpenSocialId()));
                actionContext.setActionId(request.getActionKey());
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
            log.error("Error excecuting action " + actionKey + " from restlet: " + ex.toString());
        }

        // JSONP
        if (null != callback)
        {
            jsString = callback + "(" + jsString + ")";
        }

        Representation rep = new StringRepresentation(jsString, MediaType.TEXT_PLAIN);
        rep.setExpirationDate(new Date(0L));

        return rep;
    }

    /**
     * Go from JSON to Request object.
     *
     * @return the request object.
     * @throws Exception possible exceptions.
     */
    @SuppressWarnings("unchecked")
    private Serializable getRequestObject() throws Exception
    {
        final JSONObject request = JSONObject.fromObject(requestJSON);

        if (requestType.toLowerCase().equals("null"))
        {
            return null;
        }
        else if (requestType.toLowerCase().equals("long"))
        {
            return request.getLong("value");
        }
        else if (requestType.toLowerCase().equals("int"))
        {
            return request.getInt("value");
        }
        else if (requestType.toLowerCase().equals("string"))
        {
            return request.getString("value");
        }
        else if (requestType.toLowerCase().equals("boolean"))
        {
            return request.getBoolean("value");
        }
        else
        {
            Class returnType = Class.forName(requestType);
            ObjectMapper objMapper = new ObjectMapper();
            return (Serializable) Class.forName(requestType).cast(objMapper.readValue(requestJSON, returnType));
        }
    }
}
