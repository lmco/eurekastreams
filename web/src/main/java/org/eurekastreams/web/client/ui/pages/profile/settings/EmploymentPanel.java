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
package org.eurekastreams.web.client.ui.pages.profile.settings;

import java.util.HashMap;

import org.eurekastreams.server.domain.Job;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.events.BackgroundEmploymentEditCanceledEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.PersonalEmploymentModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.EditPanel;
import org.eurekastreams.web.client.ui.common.EditPanel.Mode;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

/**
 * Displays a single employment.
 */
public class EmploymentPanel extends FlowPanel
{
    /**
     * for converting date.getYear into yyyy format.
     */
    private static final int YEAR_CONVERSION = 1900;

    /**
     * The employment information panel.
     */
    private FlowPanel employmentPanel = new FlowPanel();

    /**
     * The update panel.
     */
    private CreateOrUpdateEmploymentPanel updateEmploymentPanel;
    
    /**
     * The page history token.
     */
    private String pageHistoryToken;

    /**
     * Default constructor.
     *
     * @param job
     *            The job being displayed.
     */
    public EmploymentPanel(final Job job)
    {
        this(job, false, "");
    }
    
    /**
     * Default constructor.
     *
     * @param job
     *            The job being displayed.
     * @param inPageHistoryToken
     *            The page history token.
     */
    public EmploymentPanel(final Job job, final String inPageHistoryToken)
    {
        this(job, false, inPageHistoryToken);
    }
    
    /**
     * Default constructor.
     *
     * @param job
     *            The job being displayed.
     * @param readOnly
     *            If the data can be edited.
     */
    public EmploymentPanel(final Job job, final boolean readOnly)
    {
        this(job, readOnly, "");
    }

    /**
     * Default constructor.
     *
     * @param job
     *            The job being displayed.
     * @param readOnly
     *            If the data can be edited.
     * @param inPageHistoryToken
     *            The page history token.
     */
    public EmploymentPanel(final Job job, final boolean readOnly, final String inPageHistoryToken)
    {
        pageHistoryToken = inPageHistoryToken;
        
        this.setStyleName("employment");

        if (!readOnly)
        {
            EditPanel editControls = new EditPanel(employmentPanel, Mode.EDIT_AND_DELETE);
            
            employmentPanel.add(editControls);

            editControls.addEditClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    employmentPanel.setVisible(false);
                    updateEmploymentPanel.setVisible(true);
                }
            });

            editControls.addDeleteClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    if (new WidgetJSNIFacadeImpl().confirm("Are you sure you want to delete this position?"))
                    {
                        PersonalEmploymentModel.getInstance().delete(job.getId());
                    }
                }
            });
            
            Session.getInstance().getEventBus().addObserver(BackgroundEmploymentEditCanceledEvent.class,
                    new Observer<BackgroundEmploymentEditCanceledEvent>()
                    {
                        public void update(final BackgroundEmploymentEditCanceledEvent arg1)
                        {
                            employmentPanel.setVisible(true);
                            updateEmploymentPanel.setVisible(false);
                        }
                    });
            
            updateEmploymentPanel = new CreateOrUpdateEmploymentPanel(job, pageHistoryToken);
            updateEmploymentPanel.setVisible(false);
            
            this.add(updateEmploymentPanel);
        }

        
        Hyperlink title = new Hyperlink(job.getTitle(), Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.SEARCH, "", getSearchParams(job.getTitle()))));
        title.setStyleName("title");
        employmentPanel.add(title);
        
        Hyperlink companyName = new Hyperlink(job.getCompanyName(), Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.SEARCH, "", getSearchParams(job.getCompanyName()))));        
        companyName.setStyleName("sub-title");
        Label employmentDates = new Label();
        if (job.getDateTo() == null)
        {
            employmentDates
                    .setText(Integer.toString(YEAR_CONVERSION + job.getDateFrom().getYear()) + " - " + "Present");
        }
        else
        {
            employmentDates.setText(Integer.toString(YEAR_CONVERSION + job.getDateFrom().getYear()) + " - "
                    + Integer.toString(YEAR_CONVERSION + job.getDateTo().getYear()));
        }
        employmentDates.setStyleName("description");

        Label description = new Label(job.getDescription());
        description.setStyleName("description");
        
        this.add(employmentPanel);

        employmentPanel.add(companyName);
        employmentPanel.add(employmentDates);
        employmentPanel.add(description);        
    }
    
    /**
     * Creates the search params link hash map.
     * 
     * @param query
     *            The query to search.
     * @return
     *            The hash map.
     */
    private HashMap<String, String> getSearchParams(final String query)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("boost", "jobs");
        params.put("query", query);
        
        return params;
    }
}
