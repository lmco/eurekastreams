/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.stream;

import org.eurekastreams.server.domain.stream.GroupStreamDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamScopeDeletedEvent;
import org.eurekastreams.web.client.events.SwitchedToCustomStreamEvent;
import org.eurekastreams.web.client.events.SwitchedToGroupStreamEvent;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacade;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.ui.common.autocomplete.AutoCompleteEntityDropDownPanel;
import org.eurekastreams.web.client.ui.common.stream.filters.list.StreamScopePanel;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * A panel for determining where a post gets posted to.
 * 
 */
public class PostToPanel extends FlowPanel
{
    /**
     * The scope that represents "you".
     */
    private StreamScope myScope;
    /**
     * The scope that the user selects other than him/herself.
     */
    private StreamScope typedInScope = null;
    /**
     * Radio button for "my" scope.
     */
    private final RadioButton mine;
    /**
     * Radio button for another users scope.
     */
    private final RadioButton other;
    /**
     * The scope panel.
     */
    private StreamScopePanel scopePanel;

    /**
     * The auto-complete panel.
     */
    private AutoCompleteEntityDropDownPanel autoComplete;

    /**
     * Constant for "other" text.
     */
    private static final String OTHER_TEXT = "other activity stream";

    /**
     * Default constructor.
     * 
     * @param postScope
     *            the scope of the current user.
     */
    public PostToPanel(final StreamScope postScope)
    {
        this(postScope, false);
    }

    /**
     * Default constructor.
     * 
     * @param postScope
     *            the scope of the current user.
     * @param alwaysShow
     *            always show no matter where you are.
     */
    public PostToPanel(final StreamScope postScope, final boolean alwaysShow)
    {
        // Make this random so we can support multiple of these.
        String groupName = "postTo-" + String.valueOf(Random.nextInt());
        mine = new RadioButton(groupName, "My activity stream");
        other = new RadioButton(groupName, "");

        myScope = postScope;
        WidgetJSNIFacade jSNIFacade = new WidgetJSNIFacadeImpl();
        mine.setChecked(true);
        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().postTo());

        if ((!jSNIFacade.getSecondToLastUrlToken().equals("people") && !jSNIFacade.getSecondToLastUrlToken().equals(
                "groups"))
                || alwaysShow)
        {

            final PostToPanel thisBuffered = this;

            Label postTo = new Label("Post to:");
            postTo.addStyleName(StaticResourceBundle.INSTANCE.coreCss().label());
            this.add(postTo);

            this.add(mine);
            this.add(other);

            autoComplete = new AutoCompleteEntityDropDownPanel("/resources/autocomplete/entities/");

            autoComplete.setOnItemSelectedCommand(new AutoCompleteEntityDropDownPanel.OnItemSelectedCommand()
            {
                public void itemSelected(final JavaScriptObject obj)
                {
                    String[] results = obj.toString().split(",", 4);
                    if (!results[0].equals("NOTSET"))
                    {
                        String displayName = results[3];
                        ScopeType entityType = ScopeType.valueOf(results[0]);
                        String uniqueId = results[1];
                        long streamScopeId = Long.parseLong(results[2]);

                        typedInScope = new StreamScope(displayName, entityType, uniqueId, streamScopeId);

                        other.setChecked(true);

                        autoComplete.setVisible(false);
                        autoComplete.setDefaultText(OTHER_TEXT);
                        scopePanel = new StreamScopePanel(typedInScope);
                        thisBuffered.add(scopePanel);
                    }
                    else
                    {
                        typedInScope = null;
                    }
                }
            });
            autoComplete.setDefaultText(OTHER_TEXT);
            this.add(autoComplete);

            EventBus.getInstance().addObserver(StreamScopeDeletedEvent.getEvent(),
                    new Observer<StreamScopeDeletedEvent>()
                    {
                        public void update(final StreamScopeDeletedEvent event)
                        {
                            try
                            {
                                if (typedInScope.equals(event.getScope()))
                                {
                                    autoComplete.setVisible(true);
                                    autoComplete.setDefaultText(OTHER_TEXT);
                                    thisBuffered.remove(scopePanel);
                                    typedInScope = null;
                                }
                            }
                            catch (final Exception ex)
                            {
                                int x = 0;
                                // no logging Im client side. This means a scope
                                // closed that doesnt have an ID
                                // Usually by a user who just entered it into
                                // the create list modal.
                                // This is necessary because we use a little
                                // long vs a big Long.
                            }
                        }
                    });

            EventBus.getInstance().addObserver(SwitchedToCustomStreamEvent.class,
                    new Observer<SwitchedToCustomStreamEvent>()
                    {
                        public void update(final SwitchedToCustomStreamEvent arg1)
                        {
                            if (other.isChecked())
                            {
                                selectMyActivityStream();
                            }
                        }
                    });

            EventBus.getInstance().addObserver(SwitchedToGroupStreamEvent.getEvent(),
                    new Observer<SwitchedToGroupStreamEvent>()
                    {
                        public void update(final SwitchedToGroupStreamEvent arg1)
                        {
                            GroupStreamDTO group = arg1.getView();

                            // If the group has disabled posting, make "my activity stream" the default destination. The
                            // one case where this would be the incorrect thing to do is if the user is a group or org
                            // coordinator. But it is better for coordinators to have to explicitly choose the group
                            // than the alternative: a non-postable group pre-selected for regular users and their posts
                            // just going nowhere.
                            if (!group.isPostable())
                            {
                                selectMyActivityStream();
                            }
                            else
                            {
                                other.setChecked(true);
                                autoComplete.setVisible(false);
                                autoComplete.setDefaultText(OTHER_TEXT);
                                removeScopePanel(thisBuffered);

                                typedInScope = new StreamScope(ScopeType.GROUP, group.getShortName());
                                typedInScope.setDisplayName(group.getName());

                                scopePanel = new StreamScopePanel(typedInScope);
                                thisBuffered.add(scopePanel);
                            }
                        }
                    });
        }
    }

    /**
     * If necessary, removes an existing StreamScopePanel in the indicated PostToPanel.
     * 
     * @param postToPanel
     *            the panel to search for a StreamScopePanel.
     */
    private void removeScopePanel(final PostToPanel postToPanel)
    {
        int oldScopePanelIndex = -1;

        for (int i = 0; i < postToPanel.getChildren().size(); i++)
        {
            if (postToPanel.getWidget(i).getClass() == StreamScopePanel.class)
            {
                oldScopePanelIndex = i;
            }
        }

        if (oldScopePanelIndex >= 0)
        {
            postToPanel.remove(oldScopePanelIndex);
        }

    }

    /**
     * Gets the post scope.
     * 
     * @return the scope.
     */
    public StreamScope getPostScope()
    {
        if (mine.isChecked())
        {
            return myScope;
        }
        else if (other.isChecked() && typedInScope != null)
        {
            return typedInScope;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the post scope.
     * 
     * @param inScope
     *            the scope.
     */
    public void setPostScope(final StreamScope inScope)
    {
        myScope = inScope;
    }

    /**
     * Sets state of panel back to My Activity Stream and removes any other selected scopes.
     */
    public void selectMyActivityStream()
    {
        if (typedInScope != null)
        {
            mine.setChecked(true);
            autoComplete.setVisible(true);
            autoComplete.setDefaultText(OTHER_TEXT);
            this.remove(scopePanel);
            typedInScope = null;
        }
    }
}
