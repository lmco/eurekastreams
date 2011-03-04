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

import org.eurekastreams.server.domain.Job;
import org.eurekastreams.web.client.events.BackgroundEmploymentAddCanceledEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.data.DeletedPersonalEmploymentResponseEvent;
import org.eurekastreams.web.client.events.data.GotPersonalEmploymentResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedPersonalEmploymentResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedPersonalEmploymentResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.PersonalEmploymentModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Displays a users employment information.
 */
public class EmploymentListPanel extends FlowPanel
{
    /**
     * The panel.
     */
    private FlowPanel panel = new FlowPanel();
    
    /**
     * The create panel.
     */
    private CreateOrUpdateEmploymentPanel createEmploymentPanel;
    
    /**
     * The add new position panel.
     */
    private FlowPanel addPosition = new FlowPanel();
    
    /**
     * The page history token.
     */
    private String pageHistoryToken;
    
    /**
     * default constructor.
     */
    public EmploymentListPanel()
    {
        this(Session.getInstance().generateUrl(new CreateUrlRequest()));
    }
    
    /**
     * default constructor.
     * 
     * @param inPageHistoryToken
     *            The page history token.
     */
    public EmploymentListPanel(final String inPageHistoryToken)
    {   
        pageHistoryToken = inPageHistoryToken;
        
        final Label addNewPosition = new Label("Add position");
        addNewPosition.addStyleName(StaticResourceBundle.INSTANCE.coreCss().linkedLabel());
        
        addPosition.add(addNewPosition);
        addPosition.addStyleName(StaticResourceBundle.INSTANCE.coreCss().addNewBackground());
        
        Session.getInstance().getEventBus().addObserver(GotPersonalEmploymentResponseEvent.class,
                new Observer<GotPersonalEmploymentResponseEvent>()
                {
                    public void update(final GotPersonalEmploymentResponseEvent event)
                    {                       
                        panel.clear();
                        panel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().personalSettingsBackground());
                        
                        Label title = new Label("Work History");
                        title.addStyleName(StaticResourceBundle.INSTANCE.coreCss().title());
                        panel.add(title);
                        
                        for (Job job : event.getResponse())
                        {
                            panel.add(new EmploymentPanel(job, pageHistoryToken));
                        }                      
                        
                        addPosition.setVisible(true);
                        panel.add(addPosition);
                        
                        createEmploymentPanel = new CreateOrUpdateEmploymentPanel(null, pageHistoryToken);
                        
                        createEmploymentPanel.setVisible(false);
                        panel.add(createEmploymentPanel);
                        
                        addNewPosition.addClickHandler(new ClickHandler()
                        {
                            public void onClick(final ClickEvent event)
                            {
                                createEmploymentPanel.setVisible(true);
                                createEmploymentPanel.clearData();                                
                                addPosition.setVisible(false);
                            }
                        });
                    }
                });
        
        Session.getInstance().getEventBus().addObserver(InsertedPersonalEmploymentResponseEvent.class,
                new Observer<InsertedPersonalEmploymentResponseEvent>()
                {
            public void update(final InsertedPersonalEmploymentResponseEvent arg1)
            {
                Session.getInstance().getEventBus().notifyObservers(new ShowNotificationEvent(
                        new Notification("Your Position has been created")));

                createEmploymentPanel.setVisible(false);
                addPosition.setVisible(true);
                PersonalEmploymentModel.getInstance().fetch(Session.getInstance().getCurrentPerson().getId(), true);
            }
                });
        
        
        Session.getInstance().getEventBus().addObserver(UpdatedPersonalEmploymentResponseEvent.class,
                new Observer<UpdatedPersonalEmploymentResponseEvent>()
                {
                    public void update(final UpdatedPersonalEmploymentResponseEvent arg1)
                    {
                        Session.getInstance().getEventBus().notifyObservers(new ShowNotificationEvent(
                                new Notification("Your Position has been saved")));

                        PersonalEmploymentModel.getInstance().fetch(Session.getInstance().getCurrentPerson().getId(),
                                true);
                    }
                });
        
        Session.getInstance().getEventBus().addObserver(DeletedPersonalEmploymentResponseEvent.class,
                new Observer<DeletedPersonalEmploymentResponseEvent>()
                {
                    public void update(final DeletedPersonalEmploymentResponseEvent arg1)
                    {
                        Session.getInstance().getEventBus().notifyObservers(new ShowNotificationEvent(
                                new Notification("Your Position has been deleted")));

                        PersonalEmploymentModel.getInstance().fetch(Session.getInstance().getCurrentPerson().getId(),
                                true);
                    }            
                });
        
        Session.getInstance().getEventBus().addObserver(BackgroundEmploymentAddCanceledEvent.class, 
                new Observer<BackgroundEmploymentAddCanceledEvent>()
                {
                    public void update(final BackgroundEmploymentAddCanceledEvent arg1)
                    {
                        createEmploymentPanel.setVisible(false);
                        addPosition.setVisible(true);
                    }
                });
        
        this.add(panel);
        
        PersonalEmploymentModel.getInstance().fetch(Session.getInstance().getCurrentPerson().getId(), true);
    }
}
