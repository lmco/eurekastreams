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

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.data.GotPersonalBiographyResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedPersonalBiographyResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.PersonalBiographyModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.form.FormBuilder;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.form.elements.RichTextAreaFormElement;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Displays a users personal background information.
 */
public class PersonalBackgroundTabContent extends Composite
{
    /**
     * The width of the text editor.
     */
    private static final int TEXT_EDITOR_WIDTH = 430;

    /**
     * The panel.
     */
    FlowPanel panel = new FlowPanel();

    /**
     * default constructor.
     */
    public PersonalBackgroundTabContent()
    {
        Session.getInstance().getEventBus().addObserver(GotPersonalBiographyResponseEvent.class,
                new Observer<GotPersonalBiographyResponseEvent>()
                {
                    public void update(final GotPersonalBiographyResponseEvent event)
                    {
                        String biography = event.getResponse();

                        panel.clear();

                        final FormBuilder form = new FormBuilder("", PersonalBiographyModel.getInstance(),
                                Method.UPDATE);

                        form.setStyleName("biography");

                        Session.getInstance().getEventBus().addObserver(UpdatedPersonalBiographyResponseEvent.class,
                                new Observer<UpdatedPersonalBiographyResponseEvent>()
                                {
                                    public void update(final UpdatedPersonalBiographyResponseEvent arg1)
                                    {
                                        Session.getInstance().getEventBus().notifyObservers(
                                                new ShowNotificationEvent(new Notification(
                                                        "Your biography has been successfully updated")));
                                        form.onSuccess();
                                    }
                                });

                        form.addFormElement(new RichTextAreaFormElement("Biography", "biography", biography, "",
                                TEXT_EDITOR_WIDTH, false));

                        HashMap<String, String> currentPageParams = new HashMap<String, String>();
                        currentPageParams.put(StaticResourceBundle.INSTANCE.coreCss().tab(), "Work History & Education");
                        String currentPageHistoryToken = Session.getInstance().generateUrl(
                                new CreateUrlRequest(Page.PERSONAL_SETTINGS, Session.getInstance().getCurrentPerson()
                                        .getAccountId(), currentPageParams));
                        form.addWidget(new EmploymentListPanel(currentPageHistoryToken));
                        form.addWidget(new EducationListPanel(currentPageHistoryToken));
                        form.addFormDivider();
                        
                        form.setOnCancelHistoryToken(Session.getInstance().generateUrl(
                                new CreateUrlRequest(Page.PEOPLE, Session.getInstance().getCurrentPerson()
                                        .getAccountId())));

                        panel.add(form);
                    }
                });
        initWidget(panel);

        PersonalBiographyModel.getInstance().fetch(Session.getInstance().getCurrentPerson().getAccountId(), true);
    }

}
