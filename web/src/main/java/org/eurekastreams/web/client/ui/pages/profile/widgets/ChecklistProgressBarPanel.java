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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.pages.profile.Task;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * Draws out the checklist progress bar along with the link to launch the checklist.
 *
 */
public class ChecklistProgressBarPanel extends FlowPanel
{
    /**
     * This is multiplied with the percentage to get the length of the progress bar. The overall bar is 213 pixels, and
     * thus this number is 2.13 so when multiplies by 100 it is the size it should be.
     */
    private final double progressBarFactor = 2.13;

    /**
     * One hundred. This is due to math using 100 as the number we multiply a probablity with to get a percentage. For
     * more information please see http://en.wikipedia.org/wiki/Percentage
     */
    private final int oneHundred = 100;

    /**
     * All of the tasks.
     */
    private final List<Task> tasks = new ArrayList<Task>();

    /**
     * The completed tasks.
     */
    private final List<Task> completedTasks = new ArrayList<Task>();

    /**
     * The title of the modal.
     */
    private final String title;

    /**
     * The description of the modal.
     */
    private final String desc;

    /**
     * The percentage complete.
     */
    private int percentage;

    /**
     * The HTML element displaying the percentage complete.
     */
    private final HTML percentComplete;

    /**
     * The progress bar panel.
     */
    FlowPanel progressBar = new FlowPanel();

    /**
     * Default constructor.
     *
     * @param inTitle
     *            the title to put in the modal this thing launches.
     * @param inDesc
     *            the description for said modal.
     * @param inTarget
     *            the target URL.
     */
    public ChecklistProgressBarPanel(final String inTitle, final String inDesc, final CreateUrlRequest inTarget)
    {
        title = inTitle;
        desc = inDesc;

        percentComplete = new HTML("Your profile is <span class='percentage'>" + String.valueOf(percentage)
                + "%</span> complete");

        this.add(percentComplete);
        this.addStyleName("checklist-complete-sen");

        FlowPanel progressBarContainer = new FlowPanel();
        progressBarContainer.addStyleName("checklist-progress-bar");

        progressBarContainer.add(progressBar);

        this.add(progressBarContainer);

        Hyperlink launchCheckList = new Hyperlink("Launch Checklist", History.getToken());

        launchCheckList.addStyleName("launch-checklist");
        launchCheckList.addClickListener(new ClickListener()
        {
            public void onClick(final Widget arg0)
            {
                ProfileChecklistDialogContent dialogContent = new ProfileChecklistDialogContent(title, desc, tasks,
                        completedTasks, inTarget);
                Dialog dialog = new Dialog(dialogContent);
                dialog.setBgVisible(true);
                dialog.center();
                dialog.getContent().show();
            }
        });

        this.add(launchCheckList);
    }

    /**
     * Add a task to the checklist. This will cause the checklist to recompute its percentage.
     *
     * @param inTask
     *            the task.
     * @param completed
     *            whether or not it's completed.
     */
    public void addTask(final Task inTask, final boolean completed)
    {
        // Prevent "flickering"
        this.setVisible(false);

        if (completed)
        {
            completedTasks.add(inTask);
        }

        tasks.add(inTask);

        recalculatePercentage();
    }

    /**
     * Set a task in the checklist to complete. This will cause the checklist to recompute its percentage.
     *
     * @param inTask
     *            The task to mark complete.
     */
    public void setTaskComplete(final Task inTask)
    {
        if (tasks.contains(inTask) && !completedTasks.contains(inTask))
        {
            completedTasks.add(inTask);

            recalculatePercentage();
        }
    }

    /**
     * Recalculates the percentage complete and updates all of the elements.
     */
    private void recalculatePercentage()
    {
        float completedSizeFloat = completedTasks.size();
        float tasksSizeFloat = tasks.size();

        percentage = (int) (completedSizeFloat / tasksSizeFloat * oneHundred);

        this.setVisible(percentage < oneHundred);

        progressBar.setWidth((progressBarFactor * percentage) + "px");

        percentComplete.setHTML("Your profile is <span class='percentage'>" + String.valueOf(percentage)
                + "%</span> complete");
    }
}
