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
package org.eurekastreams.server.service.restlets;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.action.request.start.GadgetUserPrefActionRequest;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * This class provides the resource endpoint for retrieving Gadget user preferences. This rest
 * endpoint acts only as a transport for the action, so the test for the action covers the functionality of this
 * RESTlet.
 *
 */
public class GadgetUserPrefsResource extends WritableResource
{
    /**
     * Local logger instance.
     */
    private static Log logger = LogFactory.make();

    /**
     * Key for retrieving the Gadget Id from the rest url.
     */
    private static final String MODULE_ID_KEY = "moduleid";

    /**
     * Error message when user preferences cannot be retrieved.
     */
    private static final String USER_PREFS_GET_ERROR_MESSAGE = "Error occurred retrieving preferences for this gadget.";

    /**
     * Error message when user preferences cannot be updated.
     */
    private static final String USER_PREFS_UPDATE_ERROR_MESSAGE = "Error occurred updating "
            + "preferences for this gadget.";

    /**
     * Error message when the module id is invalid.
     */
    private static final String USER_PREFS_MODULEID_ERROR_MESSAGE = "Module ID passed in is invalid.";

    /**
     * Error message when the module id is invalid.
     */
    private static final String USER_PREFS_PARAMS_ERROR_MESSAGE = "Module ID or User Prefs passed in are invalid.";

    /**
     * Action.
     */
    private ServiceAction getGadgetUserPrefsServiceAction;

    /**
     * Service Action Controller.
     */
    private ServiceActionController serviceActionController;

    /**
     * Action for updating the User Prefs.
     */
    private ServiceAction updateGadgetUserPrefsAction;

    /**
     * Id of the gadget instance for which the user prefs belong.
     */
    private Long moduleId;

    /**
     * Retrieve the parameters from the rest url. {@inheritDoc}
     */
    @Override
    protected void initParams(final Request request)
    {
        if (request.getAttributes().containsKey(MODULE_ID_KEY))
        {
            try
            {
                String tempModuleId = (String) request.getAttributes().get(MODULE_ID_KEY);
                moduleId = Long.parseLong(tempModuleId);
            }
            catch (NumberFormatException nex)
            {
                logger.error(USER_PREFS_MODULEID_ERROR_MESSAGE, nex);
                moduleId = 0L;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        if (moduleId != null && moduleId > 0)
        {
            try
            {
                // create the request
                GadgetUserPrefActionRequest request = new GadgetUserPrefActionRequest(moduleId);

                // Create the actionContext
                PrincipalActionContext ac = new ServiceActionContext(request, null);

                // execute action and return results.
                String userPrefs = (String) serviceActionController.execute((ServiceActionContext) ac,
                        getGadgetUserPrefsServiceAction);
                logger.debug("Returning the userprefs: " + userPrefs);
                Representation rep = new StringRepresentation(userPrefs, MediaType.APPLICATION_JSON);
                rep.setExpirationDate(new Date());
                return rep;
            }
            catch (Exception ex)
            {
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, USER_PREFS_GET_ERROR_MESSAGE);
            }
        }
        else
        {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, USER_PREFS_MODULEID_ERROR_MESSAGE);
        }
    }

    /**
     * Handle PUT requests.
     *
     * @param entity
     *            the resource's new representation
     * @throws ResourceException
     *             hopefully not
     */
    @Override
    public void storeRepresentation(final Representation entity) throws ResourceException
    {
        String userPrefs = "";
        try
        {
            userPrefs = entity.getText();
            logger.debug("Retrieved userprefs: " + userPrefs);
        }
        catch (IOException ioex)
        {
            logger.error("Error occurred reading entity for request.", ioex);
            userPrefs = "";
        }

        if (moduleId != null && moduleId > 0 && userPrefs.length() > 0)
        {
            // create the request.
            GadgetUserPrefActionRequest currentRequest = new GadgetUserPrefActionRequest(moduleId, userPrefs);

            // Create the actionContext
            PrincipalActionContext ac = new ServiceActionContext(currentRequest, null);

            try
            {
                // execute action and return results.
                String userPrefsResults = (String) serviceActionController.execute((ServiceActionContext) ac,
                        updateGadgetUserPrefsAction);
                logger.debug("Updated the userprefs: " + userPrefsResults);
            }
            catch (Exception ex)
            {
                logger.error("Error updating preferences: " + ex);
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, USER_PREFS_UPDATE_ERROR_MESSAGE);
            }
        }
        else
        {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, USER_PREFS_PARAMS_ERROR_MESSAGE);
        }
    }

    /**
     * Set the getGadgetUserPrefs service action.
     *
     * @param inGetGadgetUserPrefsServiceAction
     *            the getGadgetUserPrefs service action
     */
    public void setGetGadgetUserPrefsServiceAction(final ServiceAction inGetGadgetUserPrefsServiceAction)
    {
        getGadgetUserPrefsServiceAction = inGetGadgetUserPrefsServiceAction;
    }

    /**
     * Set the service action controller.
     *
     * @param inServiceActionController
     *            the service action controller
     */
    public void setServiceActionController(final ServiceActionController inServiceActionController)
    {
        serviceActionController = inServiceActionController;
    }

    /**
     * Set the updateGadgetUserPreferences action.
     *
     * @param inUpdateGadgetUserPrefsAction
     *            the updateGadgetUserPreferences action
     */
    public void setUpdateGadgetUserPrefsAction(final ServiceAction inUpdateGadgetUserPrefsAction)
    {
        updateGadgetUserPrefsAction = inUpdateGadgetUserPrefsAction;
    }
}
