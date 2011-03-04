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

import java.util.Iterator;
import java.util.Set;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.SwitchToFilterOnPagedFilterPanelEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.PersonPanel;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Display a list of people.
 */
public class PeopleListPanel extends FlowPanel
{
    /**
     * Constant to pass into the constructor indicating that all people are to be displayed.
     */
    public static final int DISPLAY_ALL = -1;

    /**
     * Constructor.
     * 
     * @param people
     *            the Person records to display
     * @param title
     *            the title to be displayed above the people
     * @param maxDisplayed
     *            the maximum number of people to display
     * @param urlRequest
     *            the url to go to.
     * @param filterReq
     *            the view of the connection tab to go to.
     */
    public PeopleListPanel(final Set<Person> people, final String title, final int maxDisplayed,
            final CreateUrlRequest urlRequest, final SwitchToFilterOnPagedFilterPanelEvent filterReq)
    {
        if (people.size() > 0)
        {
            int cap = (maxDisplayed == DISPLAY_ALL) ? people.size() : maxDisplayed;
            Label leadership = new Label(title);
            leadership.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileSubheader());
            this.add(leadership);

            PersonPanel personPanel;
            int i = 0;
            Iterator<Person> iter = people.iterator();
            while (iter.hasNext() && i < cap)
            {
                personPanel = new PersonPanel(iter.next().toPersonModelView(), false, true);
                this.add(personPanel);
                i++;
            }

            if (i < people.size())
            {
                Anchor moreLabel = new Anchor(Integer.toString(people.size() - i) + " more");
                if (urlRequest != null)
                {
                    moreLabel.addClickHandler(new ClickHandler()
                    {
                        public void onClick(final ClickEvent event)
                        {
                            Session.getInstance().getEventBus().notifyObservers(new UpdateHistoryEvent(urlRequest));
                            if (filterReq != null)
                            {
                                Session.getInstance().getEventBus().notifyObservers(filterReq);
                            }
                        }
                    });
                }
                this.add(moreLabel);
            }
        }
    }

    /**
     * Constructor.
     * 
     * @param people
     *            the Person records to display
     * @param title
     *            the title to be displayed above the people
     * @param maxDisplayed
     *            the maximum number of people to display
     * @param urlRequest
     *            the url to go to.
     * @param filterReq
     *            the view of the connection tab to go to.
     * @param bogus
     *            not used.
     */
    public PeopleListPanel(final Set<PersonModelView> people, final String title, final int maxDisplayed,
            final CreateUrlRequest urlRequest, final SwitchToFilterOnPagedFilterPanelEvent filterReq, final Long bogus)
    {
        if (people.size() > 0)
        {
            int cap = (maxDisplayed == DISPLAY_ALL) ? people.size() : maxDisplayed;
            Label leadership = new Label(title);
            leadership.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileSubheader());
            this.add(leadership);

            PersonPanel personPanel;
            int i = 0;
            Iterator<PersonModelView> iter = people.iterator();
            while (iter.hasNext() && i < cap)
            {
                personPanel = new PersonPanel(iter.next(), false, true);
                this.add(personPanel);
                i++;
            }

            if (i < people.size())
            {
                Anchor moreLabel = new Anchor(Integer.toString(people.size() - i) + " more");
                if (urlRequest != null)
                {
                    moreLabel.addClickHandler(new ClickHandler()
                    {
                        public void onClick(final ClickEvent event)
                        {
                            Session.getInstance().getEventBus().notifyObservers(new UpdateHistoryEvent(urlRequest));
                            if (filterReq != null)
                            {
                                Session.getInstance().getEventBus().notifyObservers(filterReq);
                            }
                        }
                    });
                }
                this.add(moreLabel);
            }
        }
    }

    /**
     * Constructor.
     * 
     * @param people
     *            the Person records to display
     * @param title
     *            the title to be displayed above the people
     * @param maxDisplayed
     *            the maximum number of people to display
     */
    public PeopleListPanel(final Set<Person> people, final String title, final int maxDisplayed)
    {
        this(people, title, maxDisplayed, null, null);
    }

}
