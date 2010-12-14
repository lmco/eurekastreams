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
package org.eurekastreams.web.client.ui.pages.profile.widgets;

import java.util.HashMap;

import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.profile.Task;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

/**
 * Renders out one task for the checklist.
 *
 */
public class TaskPanel extends FlowPanel
{
    /**
     * The title of the task.
     */
    private final Hyperlink defaultTaskTitle;

    /**
     * Default constructor.
     *
     * @param task
     *            the task.
     * @param done
     *            if it is done or not.
     * @param percentage
     *            the percentage the task represents.
     * @param inTarget
     *            the target.
     */
    public TaskPanel(final Task task, final boolean done, final int percentage, final CreateUrlRequest inTarget)
    {
        this.addStyleName("checklist-task");

        Label percentageLabel = new Label(String.valueOf(percentage) + "%");
        if (done)
        {
            percentageLabel.addStyleName("checklist-done");
        }
        else
        {
            percentageLabel.addStyleName("checklist-not-done");
        }
        this.add(percentageLabel);

        FlowPanel taskPanel = new FlowPanel();
        taskPanel.addStyleName("checklist-task-desc");

        if (task.getTab() != "")
        {
            HashMap<String, String> linkParams = new HashMap<String, String>();
            linkParams.put("tab", task.getTab());
            inTarget.setParameters(linkParams);
        }

        defaultTaskTitle = new Hyperlink(task.getName(), Session.getInstance().generateUrl(inTarget));

        defaultTaskTitle.addStyleName("task-title");

        taskPanel.add(defaultTaskTitle);
        taskPanel.add(new Label(task.getDescription()));
        this.add(taskPanel);
    }

    /**
     * Adds a listener to the task title.
     *
     * @param listener
     *            the listener.
     */
    public void addTaskClickListener(final ClickListener listener)
    {
        defaultTaskTitle.addClickListener(listener);
    }
}
