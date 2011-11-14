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
package org.eurekastreams.web.services;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.server.domain.EntityIdentifier;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Identifiable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * MVC controller for endpoints returning contacts for emailing to a stream.
 */
@Controller
public class EmailContactController
{
    /** For executing actions. */
    private final ActionController serviceActionController;

    /** Principal populator. */
    private final PrincipalPopulator principalPopulator;

    /** For validating requests and selecting the right lookup action. */
    private final Map<EntityType, ServiceAction> typeToFetchActionIndex;

    /** Action to get stream token. */
    private final ServiceAction getTokenForStreamAction;

    /**
     * Constructor.
     *
     * @param inServiceActionController
     *            For executing actions.
     * @param inPrincipalPopulator
     *            Principal populator.
     * @param inTypeToFetchActionIndex
     *            For validating requests and selecting the right lookup action.
     * @param inGetTokenForStreamAction
     *            Action to get stream token.
     */
    public EmailContactController(final ActionController inServiceActionController,
            final PrincipalPopulator inPrincipalPopulator,
            final Map<EntityType, ServiceAction> inTypeToFetchActionIndex,
            final ServiceAction inGetTokenForStreamAction)
    {
        serviceActionController = inServiceActionController;
        principalPopulator = inPrincipalPopulator;
        typeToFetchActionIndex = inTypeToFetchActionIndex;
        getTokenForStreamAction = inGetTokenForStreamAction;
    }

    /**
     * Endpoint for returning a contact for posting to a stream.
     *
     * @param streamType
     *            Type of entity stream belongs to.
     * @param id
     *            ID of stream owner (person/group).
     * @param response
     *            HTTP response object.
     * @return View and model data to use to render contact.
     */
    @RequestMapping(value = "stream", method = RequestMethod.GET)
    public ModelAndView getStreamContact(@RequestParam("type") final EntityType streamType,
            @RequestParam("id") final long id, final HttpServletResponse response)
    {
        Principal principal = principalPopulator.getPrincipal(null, null);

        // get info about stream
        ServiceAction action = typeToFetchActionIndex.get(streamType);
        if (action == null)
        {
            throw new ExecutionException("Stream type not supported.");
        }
        Identifiable entity = (Identifiable) serviceActionController.execute(new ServiceActionContext(id, principal),
                action);

        // Get address
        String address = (String) serviceActionController.execute(new ServiceActionContext(new EntityIdentifier(
                streamType, id), principal), getTokenForStreamAction);

        ModelAndView mv = new ModelAndView("vcardView");
        mv.addObject("streamEntity", entity);
        mv.addObject("email", address);

        // This really should be part of the view, not the controller, but the Spring view classes (VelocityView and
        // parents) don't provide a way to set content disposition, nor a way to set arbitrary headers.
        response.setHeader("Content-Disposition", "attachment; filename=" + streamType.name().toLowerCase() + "-"
                + entity.getUniqueId() + ".vcf");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.addHeader("Pragma", "no-cache");

        return mv;
    }
}
