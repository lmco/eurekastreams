/*
 * Copyright (c) 2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.services;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.eurekastreams.commons.actions.context.ClientPrincipalActionContextImpl;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.eurekastreams.server.service.restlets.support.JsonFieldObjectExtractor;
import org.hibernate.bytecode.buildtime.ExecutionException;
import org.restlet.data.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Provides a "rest-like" API for Eureka Streams allowing access to the action framework.
 */
@Controller
public class ActionApiController
{
    /** Log. */
    private final Logger log = LoggerFactory.getLogger(LogFactory.getClassName());

    /** The context from which this service can load action beans. */
    private final BeanFactory beanFactory;

    /** Service Action Controller. */
    private final ActionController serviceActionController;

    /** Principal populators. */
    private final List<Transformer<Request, Principal>> principalExtractors;

    /** Client populators. */
    private final List<Transformer<Request, String>> clientExtractors;

    /** JSON Factory for building JSON Generators. */
    private final JsonFactory jsonFactory;

    /** Only allow read-only actions. */
    private final boolean readOnly;

    /** Extracts request from the parameters block. */
    private final JsonFieldObjectExtractor jsonFieldObjectExtractor;

    /** Action types map. */
    private final Map<String, String> actionTypes;

    /** Action rewrite name map. */
    private final Map<String, String> actionRewrites;

    /** If the session should be verified. */
    private boolean verifySession = true;

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
     * @param inJsonFieldObjectExtractor
     *            Extracts request from the parameters block.
     * @param inReadOnly
     *            Only allow read-only actions.
     * @param inActionTypes
     *            action types map.
     * @param inActionRewrites
     *            action rewrite map.
     * @param inVerifySession
     *            if the session should be verified.
     * @param inBeanFactory
     *            The context from which this service can load action beans.
     */
    public ActionApiController(final ActionController inServiceActionController,
            final List<Transformer<Request, Principal>> inPrincipalExtractors,
            final List<Transformer<Request, String>> inClientExtractors, final JsonFactory inJsonFactory,
            final JsonFieldObjectExtractor inJsonFieldObjectExtractor, final boolean inReadOnly,
            final Map<String, String> inActionTypes, final Map<String, String> inActionRewrites,
            final boolean inVerifySession, final BeanFactory inBeanFactory)
    {
        serviceActionController = inServiceActionController;
        principalExtractors = inPrincipalExtractors;
        clientExtractors = inClientExtractors;
        jsonFactory = inJsonFactory;
        jsonFieldObjectExtractor = inJsonFieldObjectExtractor;
        readOnly = inReadOnly;
        actionTypes = inActionTypes;
        actionRewrites = inActionRewrites;
        verifySession = inVerifySession;
        beanFactory = inBeanFactory;
    }

    /**
     * Executes a single action from the action framework as requested by the API.
     *
     * @param apiName
     *            API action name.
     * @param claimedSessionId
     *            Session ID provided by the client (for XSRF prevention).
     * @param parameters
     *            Request parameter data as a JSON string.
     * @param request
     *            HTTP request.
     * @param response
     *            HTTP response.
     * @throws IOException
     *             Only if setting an HTTP error code throws an error.
     */
    @RequestMapping(value = "executeSingle", method = RequestMethod.POST)
    public void executeSingle(@RequestParam(value = "apiName", required = false) final String apiName,
            @RequestParam(value = "sessionId", required = false) final String claimedSessionId,
            @RequestParam(value = "parameters", required = false) final String parameters,
            final HttpServletRequest request, final HttpServletResponse response) throws IOException
    {
        try
        {
            // verify session
            if (verifySession)
            {
                // get real session
                HttpSession session = request.getSession();
                if (session == null)
                {
                    throw new Exception("Request has no session.");
                }
                String realSessionId = session.getId();
                if (StringUtils.isBlank(realSessionId))
                {
                    throw new Exception("Request session has no valid ID.");
                }

                // compare with claimed session
                if (!realSessionId.equals(claimedSessionId))
                {
                    throw new Exception("Provided session ID does not match request session ID.");
                }
            }

            // get the principal (throws if none available)
            Principal principal = getPrincipal(request);

            // Skip determining the client - this flavor of the API is intended for preauth, so it would be directly
            // accessed by a user's app and thus the client would not apply. (Client is used for OAuth scenarios where
            // an app is accessing the API on behalf of a user.) When this code is updated/refactored to fully replace
            // ActionResource (the Noelios version of the API), then determining the client will need to be done.

            // determine request type
            String actionName = actionRewrites.containsKey(apiName) ? actionRewrites.get(apiName) : apiName;
            String requestType = actionTypes.get(actionName);
            if (requestType == null)
            {
                response.sendError(response.SC_NOT_FOUND);
                log.error("Request for unknown API '{}'.", actionName);
                return;
            }

            // get action parameter
            Serializable actionParameter = getRequestObject(parameters, requestType);

            // execute the action
            Serializable actionResult = performAction(actionName, actionParameter, principal, "");

            // prepare the response
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
            response.addHeader("Pragma", "no-cache");

            JsonGenerator jsonGenerator = jsonFactory.createJsonGenerator(response.getWriter());
            jsonGenerator.writeObject(actionResult);
        }
        catch (Exception ex)
        {
            log.error("Error performing action via API", ex);
            response.sendError(response.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Returns Principal for given account id.
     *
     * @param inRequest
     *            Request to get principal from.
     * @return Principal for given account id.
     */
    private Principal getPrincipal(final HttpServletRequest inRequest)
    {
        log.debug("Attempting to retrieve principal");

        for (Transformer<Request, Principal> extractor : principalExtractors)
        {
            // NOTE: Passing null because the extractors still use a Noelios request instead of a servlet request, so it
            // would not be compatible. This is ok, because the extractor used by this endpoint doesn't look at the
            // request anyway.
            Principal result = extractor.transform(null);
            if (result != null)
            {
                return result;
            }
        }

        throw new RuntimeException("No principal found");
    }

    /**
     * Go from JSON to Request object.
     * 
     * @param parameters
     *            Request parameter data as a JSON string.
     * @param requestType
     *            Name of the Java data type described by the request data.
     * 
     * @return the request object.
     * @throws Exception
     *             possible exceptions.
     */
    private Serializable getRequestObject(final String parameters, final String requestType) throws Exception
    {
        if (requestType.toLowerCase().equals("null"))
        {
            return null;
        }

        String parametersDecoded = parameters; // URLDecoder.decode(parameters, "UTF-8");
        final JSONObject paramsAsObject = JSONObject.fromObject(parametersDecoded);
        return (Serializable) jsonFieldObjectExtractor.extract(paramsAsObject, "request", requestType);
    }

    /**
     * Actually performs the action.
     *
     * @param actionName
     *            Name of action bean.
     * @param actionParameter
     *            Parameter to the action bean.
     * @param principal
     *            Principal.
     * @param clientUniqueId
     *            Unique ID for the source which sent the request (not presently used - will be of use when the OAuth
     *            endpoints are moved to this class).
     * @return The action result.
     */
    private Serializable performAction(final String actionName, final Serializable actionParameter,
            final Principal principal, final String clientUniqueId)
    {
        // get the action
        Object springBean = beanFactory.getBean(actionName);

        // create context
        PrincipalActionContext actionContext = new ClientPrincipalActionContextImpl(actionParameter, principal,
                clientUniqueId);
        actionContext.setActionId(actionName);

        log.debug("Executing action {} for user {}.", actionName, principal.getAccountId());

        // execute (or not) based on type of bean
        if (springBean instanceof ServiceAction)
        {
            ServiceAction action = (ServiceAction) springBean;

            if (readOnly && !action.isReadOnly())
            {
                throw new ExecutionException(String.format("Action '%s' is not read-only.", actionName));
            }
            return serviceActionController.execute(actionContext, action);
        }
        else if (springBean instanceof TaskHandlerServiceAction)
        {
            TaskHandlerServiceAction action = (TaskHandlerServiceAction) springBean;
            if (readOnly && !action.isReadOnly())
            {
                throw new ExecutionException(String.format("Action '%s' is not read-only.", actionName));
            }
            return serviceActionController.execute(actionContext, action);
        }
        else if (springBean == null)
        {
            throw new ExecutionException(String.format("Unknown bean '%s'.", actionName));
        }
        else
        {
            throw new ExecutionException(String.format("Bean '%s' is not an action.", actionName));
        }
    }
}
