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

import java.util.List;

import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.pages.profile.Task;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * The dialog of the checklist itself.
 *
 */
public class ProfileChecklistDialogContent implements DialogContent
{
    /**
     * One hundred. This is due to math using 100 as the number we multiply a probablity with to get a percentage. For
     * more information please see http://en.wikipedia.org/wiki/Percentage
     */
    private final int oneHundred = 100;

    /**
     * The command to close the dialog.
     */
    private WidgetCommand closeCommand = null;

    /**
     * The title.
     */
    private final String title;
    /**
     * The description.
     */
    private final String description;
    /**
     * The overall percentage done.
     */
    private final int percentage;
    /**
     * The percentage each item represents.
     */
    private final int percentPerItem;
    /**
     * The tasks to do.
     */
    private final List<Task> tasksToDo;
    /**
     * The completed tasks.
     */
    private final List<Task> completedTasks;

    /**
     * The content widget.
     */
    private final FlowPanel content = new FlowPanel();

    /**
     * The target URL.
     */
    private final CreateUrlRequest target;

    /**
     * Default constructor.
     *
     * @param inTitle
     *            the title.
     * @param inDescription
     *            the description.
     * @param inTasksToDo
     *            the tasks to do.
     * @param inCompletedTasks
     *            the completed tasks.
     * @param inTarget
     *            the target after clicking the checklist.
     */
    public ProfileChecklistDialogContent(final String inTitle, final String inDescription,
            final List<Task> inTasksToDo, final List<Task> inCompletedTasks, final CreateUrlRequest inTarget)
    {
        title = inTitle;
        description = inDescription;
        tasksToDo = inTasksToDo;
        completedTasks = inCompletedTasks;
        target = inTarget;

        float tasksSize = tasksToDo.size();
        float completedTasksSize = completedTasks.size();

        percentage = (int) (completedTasksSize / tasksSize * oneHundred);
        percentPerItem = (int) (oneHundred / tasksSize);
    }

    /**
     * Gets the widget body of the dialog.
     *
     * @return the widget.
     */
    public Widget getBody()
    {
        final DialogContent thisBuffered = this;
        Label descriptionLbl = new Label(description);
        descriptionLbl.addStyleName("checklist-desc");
        content.add(descriptionLbl);

        final FlowPanel taskContainer = new FlowPanel();
        taskContainer.addStyleName("task-container");
        content.add(taskContainer);

        // tasks
        for (Task task : tasksToDo)
        {
            boolean done = false;
            if (completedTasks.indexOf(task) >= 0)
            {
                done = true;
            }

            int itemPercentage = done ? percentPerItem : 0;

            TaskPanel taskPanel = new TaskPanel(task, done, itemPercentage, target);
            taskPanel.addTaskClickListener(new ClickListener()
            {
                public void onClick(final Widget arg0)
                {
                    thisBuffered.close();
                }
            });
            taskContainer.add(taskPanel);

        }

        FlowPanel bottomBar = new FlowPanel();
        bottomBar.addStyleName("checklist-bottom-bar");
        bottomBar.add(new HTML("Your profile is <strong>" + String.valueOf(percentage) + "%</strong> complete"));

        content.add(bottomBar);

        return content;
    }

    /**
     * Returns the CSS name of this object.
     *
     * @return the css name.
     */
    public String getCssName()
    {
        return "image-crop-dialog";
    }

    /**
     * Returns the title of the dialog.
     *
     * @return the title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * The command to call to close the dialog.
     *
     * @param command
     *            the close command.
     */
    public void setCloseCommand(final WidgetCommand command)
    {
        closeCommand = command;
    }

    /**
     * Call the close command.
     */
    public void close()
    {
        closeCommand.execute();
    }

    /**
     * Necessary for the interface.
     */
    public void show()
    {
    }
}
