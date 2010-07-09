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
import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.Task;
import org.eurekastreams.server.persistence.GadgetDefinitionMapper;
import org.restlet.data.Request;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;

/**
 * The checklist resource.
 * 
 */
public class ChecklistResource extends WritableResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(ChecklistResource.class);

    /**
     * The gadget def id.
     */
    private Long gadgetDefId;

    /**
     * The gadget def mapper.
     */
    private GadgetDefinitionMapper gadgetDefinitionMapper;

    /**
     * Initialize parameters from the request object. the context of the request
     * 
     * @param request
     *            the client's request
     */
    @Override
    protected void initParams(final Request request)
    {
        gadgetDefId = Long.decode((String) request.getAttributes().get("gadgetDefId"));
    }

    /**
     * This method is only here to help in testing. I want to find a better way to get spring involved in the setup, and
     * to have spring inject a mocked mapper.
     * 
     * @param inGadgetDefMapper
     *            mapper.
     */
    public void setGadgetDefinitionMapper(final GadgetDefinitionMapper inGadgetDefMapper)
    {
        gadgetDefinitionMapper = inGadgetDefMapper;
    }

    /**
     * Issues an update (PUT).
     * 
     * @param entity
     *            the JSON coming in.
     * @throws ResourceException
     *             exception for malformed JSON.
     */
    @Override
    public void storeRepresentation(final Representation entity) throws ResourceException
    {
        GadgetDefinition gadgetDef = gadgetDefinitionMapper.findById(gadgetDefId);
        List<Task> tasks = gadgetDef.getTasks();

        try
        {
            JSONObject jsonObject = JSONObject.fromObject(entity.getText());
            JSONArray taskArray = jsonObject.getJSONArray("tasks");

            List<String> names = new LinkedList<String>();

            for (Object jsonTaskObj : taskArray)
            {
                JSONObject jsonTask = (JSONObject) jsonTaskObj;
                String newName = jsonTask.getString("name");
                String newDescription = jsonTask.getString("description");
                names.add(newName);
                int index = tasks.indexOf(new Task(newName, ""));
                if (index >= 0)
                {
                    tasks.get(index).setDescription(newDescription);
                }
                else
                {
                    tasks.add(new Task(newName, newDescription));
                }
            }

            for (int i = tasks.size() - 1; i >= 0; i--)
            {
                if (!names.contains(tasks.get(i).getName()))
                {
                    tasks.remove(i);
                }
            }

            gadgetDefinitionMapper.flush();

        }
        catch (IOException e)
        {
            log.error(e);
        }
    }
}
