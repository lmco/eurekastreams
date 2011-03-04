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

import java.util.LinkedList;

import org.eurekastreams.server.domain.Enrollment;
import org.eurekastreams.web.client.events.BackgroundEducationAddCanceledEvent;
import org.eurekastreams.web.client.events.BackgroundEducationEditCanceledEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.PersonalEducationModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.autocomplete.AutoCompleteItemDropDownFormElement;
import org.eurekastreams.web.client.ui.common.form.FormBuilder;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.form.elements.BasicDropDownFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextAreaFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.IntegerTextBoxFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.ValueOnlyFormElement;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Displays the form for adding or editing a new enrollment.
 */
public class CreateOrUpdateEducationPanel extends FlowPanel
{
    /**
     * for converting date.getYear into yyyy format.
     */
    private static final int YEAR_CONVERSION = 1900;

    /**
     * Max length for autocomplete fields.
     */
    private static final int MAX_LENGTH = 50;

    /**
     * Maximum details length.
     */
    private static final int MAX_DETAILS = 200;

    /**
     * The page history token to direct to upon saving or canceling.
     */
    private String pageHistoryToken;

    /**
     * Default constructor.
     * 
     * @param education
     *            The enrollment.
     */
    public CreateOrUpdateEducationPanel(final Enrollment education)
    {
        pageHistoryToken = Session.getInstance().generateUrl(new CreateUrlRequest());
        createForm(education);
    }

    /**
     * Default constructor.
     * 
     * @param education
     *            The enrollment.
     * @param inPageHistoryToken
     *            The page history token for cancel.
     */
    public CreateOrUpdateEducationPanel(final Enrollment education, final String inPageHistoryToken)
    {
        pageHistoryToken = inPageHistoryToken;
        createForm(education);
    }

    /**
     * Clears the form.
     */
    public void clearData()
    {
        createForm(null);
    }

    /**
     * A Recreates the form.
     * 
     * @param education
     *            The enrollment.
     */
    private void createForm(final Enrollment education)
    {
        this.clear();

        FormBuilder form;

        AutoCompleteItemDropDownFormElement nameOfSchool = new AutoCompleteItemDropDownFormElement("Name of School",
                "nameOfSchool", "", "", true, "/resources/autocomplete/school_name/", "itemNames", "");
        nameOfSchool.setMaxLength(MAX_LENGTH);

        nameOfSchool.setOnItemSelectedCommand(new AutoCompleteItemDropDownFormElement.OnItemSelectedCommand()
        {
            public void itemSelected(final JavaScriptObject obj)
            {

            }
        });

        LinkedList<String> degrees = new LinkedList<String>();
        degrees.add("Select");
        degrees.add("Associate");
        degrees.add("Bachelors");
        degrees.add("Masters");
        degrees.add("Doctorate");

        BasicDropDownFormElement degree;

        AutoCompleteItemDropDownFormElement areaOfStudy = new AutoCompleteItemDropDownFormElement("Area of Study",
                "areasOfStudy", "", "", true, "/resources/autocomplete/area_of_study/", "itemNames", "");
        areaOfStudy.setMaxLength(MAX_LENGTH);

        areaOfStudy.setOnItemSelectedCommand(new AutoCompleteItemDropDownFormElement.OnItemSelectedCommand()
        {
            public void itemSelected(final JavaScriptObject obj)
            {

            }
        });

        IntegerTextBoxFormElement yearGraduated = new IntegerTextBoxFormElement("yyyy", 4, "Year Graduated",
                "yearGraduated", "", "Currently a student? Enter your expected graduation year.", false);

        BasicTextAreaFormElement additionalDetails = new BasicTextAreaFormElement(MAX_DETAILS, "Additional Details",
                "additionalDetails", "", "Add any additional comments about your academic studies such as awards, "
                        + "papers authored, honors received, etc.", false);

        if (education == null)
        {
            form = new FormBuilder("Add School", PersonalEducationModel.getInstance(), Method.INSERT);
            degree = new BasicDropDownFormElement("Degree", "degree", degrees, "", "", true);
        }
        else
        {
            form = new FormBuilder("Edit School", PersonalEducationModel.getInstance(), Method.UPDATE);
            form.addStyleName(StaticResourceBundle.INSTANCE.coreCss().editSchool());
            degree = new BasicDropDownFormElement("Degree", "degree", degrees, education.getDegree(), "", true);

            form.addFormElement(new ValueOnlyFormElement("id", education.getId()));

            nameOfSchool.setValue(education.getSchoolName());
            String areaOfStudyString = education.getAreasOfStudy().toString();
            areaOfStudy.setValue(areaOfStudyString.substring(1, areaOfStudyString.length() - 1));
            if (education.getGradDate() != null)
            {
                yearGraduated.setValue(Integer.toString(education.getGradDate().getYear() + YEAR_CONVERSION));
            }
            additionalDetails.setValue(education.getAdditionalDetails());
        }

        form.addFormElement(nameOfSchool);
        form.addFormElement(degree);
        form.addFormElement(areaOfStudy);
        form.addFormElement(yearGraduated);
        form.addFormElement(additionalDetails);
        form.addFormDivider();

        form.addOnCancelCommand(new Command()
        {
            public void execute()
            {
                if (education == null)
                {
                    Session.getInstance().getEventBus().notifyObservers(new BackgroundEducationAddCanceledEvent());
                }
                else
                {
                    Session.getInstance().getEventBus().notifyObservers(new BackgroundEducationEditCanceledEvent());
                }
            }
        });

        form.setOnCancelHistoryToken(pageHistoryToken);

        this.add(form);
    }
}
