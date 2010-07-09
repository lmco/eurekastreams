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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.stream.SetStreamFilterOrderRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.PersonMapper;

/**
 * Reorders the stream views displayed on the activity page..
 *
 */
public class SetStreamViewOrderExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Local logger instance.
     */
    private final Log log = LogFactory.make();

    /**
     * Mapper used to retrieve and save the page that holds the tabs.
     */
    private final PersonMapper personMapper;

    /**
     * Constructor.
     *
     * @param inPersonMapper
     *            injecting the mapper
     */
    public SetStreamViewOrderExecution(final PersonMapper inPersonMapper)
    {
        personMapper = inPersonMapper;
    }

    /**
     * {@inheritDoc}. Move the Stream view order on the Activity page.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        log.debug("entering");
        SetStreamFilterOrderRequest request = (SetStreamFilterOrderRequest) inActionContext.getParams();

        Person person = personMapper.findByAccountId(inActionContext.getPrincipal().getAccountId());

        List<StreamView> views = person.getStreamViewDefinitions();

        // Find the tab to be moved
        int oldIndex = -1;

        for (int i = 0; i < views.size(); i++)
        {
            if (views.get(i).getId() == request.getFilterId())
            {
                oldIndex = i;
            }
        }

        StreamView movingView = views.get(oldIndex);

        // move the tab
        views.remove(oldIndex);
        views.add(request.getNewIndex(), movingView);

        person.setStreamViewHiddenLineIndex(request.getHiddenLineIndex());

        personMapper.flush();

        return Boolean.TRUE;
    }

}
