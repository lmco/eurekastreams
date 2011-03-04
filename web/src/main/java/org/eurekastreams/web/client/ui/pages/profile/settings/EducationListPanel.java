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

import org.eurekastreams.server.domain.Enrollment;
import org.eurekastreams.web.client.events.BackgroundEducationAddCanceledEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.data.DeletedPersonalEducationResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonalEducationResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedPersonalEducationResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedPersonalEducationResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.PersonalEducationModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Displays a users education information.
 */
public class EducationListPanel extends FlowPanel 
{
    /**
     * The panel.
     */
    FlowPanel panel = new FlowPanel();
    
    /**
     * The edit panel.
     */
    CreateOrUpdateEducationPanel createEducationPanel;
    
    /**
     * The add new school panel.
     */
    FlowPanel addSchool = new FlowPanel();
    
    /**
     * The page history token.
     */
    private String pageHistoryToken;
    
    /**
     * default constructor.
     */
    public EducationListPanel()
    {
        this(Session.getInstance().generateUrl(new CreateUrlRequest()));
    }
    
    /**
     * default constructor.
     * 
     * @param inPageHistoryToken
     *            the page history token.
     */
    public EducationListPanel(final String inPageHistoryToken)
    {
        pageHistoryToken = inPageHistoryToken;
        
        final Label addNewSchool = new Label("Add school");
        addNewSchool.addStyleName(StaticResourceBundle.INSTANCE.coreCss().linkedLabel());
        
        addSchool.add(addNewSchool);
        addSchool.addStyleName(StaticResourceBundle.INSTANCE.coreCss().addNewBackground());
        
        Session.getInstance().getEventBus().addObserver(GotPersonalEducationResponseEvent.class,
                new Observer<GotPersonalEducationResponseEvent>()
                {
                    public void update(final GotPersonalEducationResponseEvent event)
                    {                       
                        panel.clear();
                        panel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().personalSettingsBackground());
                        
                        Label title = new Label("Education");
                        title.addStyleName(StaticResourceBundle.INSTANCE.coreCss().title());
                        panel.add(title);
                        
                        for (Enrollment enrollment : event.getResponse())
                        {
                            panel.add(new EducationPanel(enrollment, pageHistoryToken));
                        }                        
                        
                        addSchool.setVisible(true);
                        panel.add(addSchool);
                        
                        createEducationPanel = new CreateOrUpdateEducationPanel(null, pageHistoryToken);
                        createEducationPanel.setVisible(false);
                        panel.add(createEducationPanel);
                        
                        addNewSchool.addClickHandler(new ClickHandler()
                        {
                            public void onClick(final ClickEvent event)
                            {
                                createEducationPanel.setVisible(true);
                                createEducationPanel.clearData();                                
                                addSchool.setVisible(false);
                            }
                        });
                    }
                });    
        
        Session.getInstance().getEventBus().addObserver(InsertedPersonalEducationResponseEvent.class,
                new Observer<InsertedPersonalEducationResponseEvent>()
                {
            public void update(final InsertedPersonalEducationResponseEvent arg1)
            {
                Session.getInstance().getEventBus().notifyObservers(new ShowNotificationEvent(
                        new Notification("Your School has been created")));

                createEducationPanel.setVisible(false);
                addSchool.setVisible(true); 
                PersonalEducationModel.getInstance().fetch(Session.getInstance().getCurrentPerson().getId(), true);
            }
                });
        
        Session.getInstance().getEventBus().addObserver(UpdatedPersonalEducationResponseEvent.class,
                new Observer<UpdatedPersonalEducationResponseEvent>()
                {
                    public void update(final UpdatedPersonalEducationResponseEvent arg1)
                    {
                        Session.getInstance().getEventBus().notifyObservers(new ShowNotificationEvent(
                                new Notification("Your School has been saved")));

                        PersonalEducationModel.getInstance().fetch(Session.getInstance().getCurrentPerson().getId(),
                                true);
                    }
                });
        
        Session.getInstance().getEventBus().addObserver(DeletedPersonalEducationResponseEvent.class, 
                new Observer<DeletedPersonalEducationResponseEvent>()
                {
                    public void update(final DeletedPersonalEducationResponseEvent arg1)
                    {               
                        Session.getInstance().getEventBus().notifyObservers(new ShowNotificationEvent(
                                new Notification("Your School has been deleted")));

                        PersonalEducationModel.getInstance().fetch(Session.getInstance().getCurrentPerson().getId(),
                                true);
                    }            
                });
        
        Session.getInstance().getEventBus().addObserver(BackgroundEducationAddCanceledEvent.class, 
                new Observer<BackgroundEducationAddCanceledEvent>()
                {
                    public void update(final BackgroundEducationAddCanceledEvent arg1)
                    {
                        createEducationPanel.setVisible(false);
                        addSchool.setVisible(true);
                    }
                });
        
        this.add(panel);
        
        PersonalEducationModel.getInstance().fetch(Session.getInstance().getCurrentPerson().getId(), true);
    }
}
