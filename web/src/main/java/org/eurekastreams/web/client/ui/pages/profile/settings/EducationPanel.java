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

import org.eurekastreams.server.domain.Enrollment;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.events.BackgroundEducationEditCanceledEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.PersonalEducationModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.EditPanel;
import org.eurekastreams.web.client.ui.common.EditPanel.Mode;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

/**
 * Displays an education entry.
 */
public class EducationPanel extends FlowPanel
{
    /**
     * For converting date.getYear into yyyy format.
     */
    private static final int YEAR_CONVERSION = 1900;

    /**
     * The education information panel.
     */
    private FlowPanel educationPanel = new FlowPanel();

    /**
     * The update panel.
     */
    private CreateOrUpdateEducationPanel updateEducationPanel;

    /**
     * Default constructor.
     *
     * @param enrollment
     *            The school being displayed.
     */
    public EducationPanel(final Enrollment enrollment)
    {
        this(enrollment, false, Session.getInstance().generateUrl(new CreateUrlRequest()));
    }

    /**
     * Default constructor.
     *
     * @param enrollment
     *            The school being displayed.
     * @param inPageHistoryToken
     *            The page history token for canceling.
     */
    public EducationPanel(final Enrollment enrollment, final String inPageHistoryToken)
    {
        this(enrollment, false, inPageHistoryToken);
    }

    /**
     * Constructor.
     *
     * @param enrollment
     *            The school being displayed.
     * @param readOnly
     *            If the data can be edited.
     */
    public EducationPanel(final Enrollment enrollment, final boolean readOnly)
    {
        this(enrollment, readOnly, Session.getInstance().generateUrl(new CreateUrlRequest()));
    }

    /**
     * Constructor.
     *
     * @param enrollment
     *            The school being displayed.
     * @param readOnly
     *            If the data can be edited.
     * @param inPageHistoryToken
     *            The page history token for canceling.
     */
    public EducationPanel(final Enrollment enrollment, final boolean readOnly, final String inPageHistoryToken)
    {
        this.setStyleName(StaticResourceBundle.INSTANCE.coreCss().education());

        if (!readOnly)
        {
            EditPanel editControls = new EditPanel(educationPanel, Mode.EDIT_AND_DELETE);

            educationPanel.add(editControls);

            editControls.addDeleteClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    if (new WidgetJSNIFacadeImpl().confirm("Are you sure you want to delete this school?"))
                    {
                        PersonalEducationModel.getInstance().delete(enrollment.getId());
                    }
                }
            });

            editControls.addEditClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    updateEducationPanel.setVisible(true);
                    educationPanel.setVisible(false);
                }
            });

            Session.getInstance().getEventBus().addObserver(BackgroundEducationEditCanceledEvent.class,
                    new Observer<BackgroundEducationEditCanceledEvent>()
                    {
                        public void update(final BackgroundEducationEditCanceledEvent arg1)
                        {
                            educationPanel.setVisible(true);
                            updateEducationPanel.setVisible(false);
                        }
                    });

            updateEducationPanel = new CreateOrUpdateEducationPanel(enrollment, inPageHistoryToken);
            updateEducationPanel.setVisible(false);

            this.add(updateEducationPanel);
        }
        Hyperlink schoolName = new Hyperlink(enrollment.getSchoolName(), Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.SEARCH, "", getSearchParams(enrollment.getSchoolName()))));
        schoolName.setStyleName(StaticResourceBundle.INSTANCE.coreCss().title());
        educationPanel.add(schoolName);

        InlineLabel degree = new InlineLabel(enrollment.getDegree() + ", ");
        InlineHyperlink areaOfStudy = new InlineHyperlink(enrollment.getAreasOfStudy().get(0).getName(),
                Session.getInstance().generateUrl(new CreateUrlRequest(Page.SEARCH, "",
                getSearchParams(enrollment.getAreasOfStudy().get(0).getName()))));
        Label graduationDate = new Label("");
        if (enrollment.getGradDate() != null)
        {
            graduationDate = new Label(Integer.toString(YEAR_CONVERSION + enrollment.getGradDate().getYear()));
            graduationDate.setStyleName(StaticResourceBundle.INSTANCE.coreCss().description());
        }

        Label additionalDetails = new Label(enrollment.getAdditionalDetails());
        additionalDetails.setStyleName(StaticResourceBundle.INSTANCE.coreCss().description());

        this.add(educationPanel);

        educationPanel.add(degree);
        educationPanel.add(areaOfStudy);
        educationPanel.add(graduationDate);
        educationPanel.add(additionalDetails);
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
        params.put("boost", "education");
        params.put("query", query);

        return params;
    }
}
