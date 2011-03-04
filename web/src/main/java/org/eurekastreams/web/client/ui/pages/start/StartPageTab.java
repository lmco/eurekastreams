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
package org.eurekastreams.web.client.ui.pages.start;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.action.request.start.RenameTabRequest;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.web.client.events.GadgetAddedToStartPageEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.data.UpdatedStartPageLayoutResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedStartPageTabNameResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.GadgetMetaDataFetcher;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.StartTabsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.tabs.SimpleTab;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;
import org.eurekastreams.web.client.ui.pages.start.layouts.TabLayoutSelectorPanel;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * The start page tab extends the simpletab and adds a menu and the ability to rename, delete, and change layout.
 *
 */
public class StartPageTab extends SimpleTab
{
    /**
     * The tab layout selector panel.
     */
    TabLayoutSelectorPanel tabLayoutSelectorPanel;

    /**
     * The textbox the user inserts a new tab name or rename into.
     */
    TextBox textBox = new TextBox();

    /**
     * The domain model object the tabcomposite gets its info from.
     */
    Tab tab = null;

    /**
     * The tab menu bar.
     */
    MenuBar menuItems = new MenuBar(true);

    /**
     * The menu.
     */
    MenuBar menu = new MenuBar();

    /**
     * JSNI Facade.
     */
    WidgetJSNIFacadeImpl jSNIFacade = new WidgetJSNIFacadeImpl();

    /**
     * The remove item.
     */
    MenuItem removeItem;

    /**
     * Am I active?
     */
    boolean isActive = false;

    /**
     * No Blur.
     */
    boolean noBlur = false;

    /**
     * The empty constructor is used for making a tabcomposite with now tab entity. The result is a tabcomposite with a
     * new text box in it.
     */
    public StartPageTab()
    {
        super("+");
        textBox.addStyleName(StaticResourceBundle.INSTANCE.coreCss().newTabTextbox());
        textBox.setMaxLength(TabTemplate.MAX_TAB_NAME_LENGTH);
        getPanel().add(textBox);
        textBox.setVisible(false);
        textBox.setText("New Tab");

        getFocusPanel().addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                textBox.setText("New Tab");
                textBox.setVisible(true);
                textBox.selectAll();
                textBox.setFocus(true);
                getLabel().setVisible(false);
            }
        });

        textBox.addBlurHandler(new BlurHandler()
        {
            public void onBlur(final BlurEvent event)
            {
                if (!noBlur)
                {
                    StartTabsModel.getInstance().insert(textBox.getText());
                }
                noBlur = false;
            }
        });

        textBox.addKeyPressHandler(new KeyPressHandler()
        {

            public void onKeyPress(final KeyPressEvent event)
            {
                if (event.getCharCode() == KeyCodes.KEY_ENTER)
                {
                    noBlur = true;
                    StartTabsModel.getInstance().insert(textBox.getText());
                }
                if (event.getCharCode() == KeyCodes.KEY_ESCAPE)
                {
                    noBlur = true;
                    textBox.setVisible(false);
                    getLabel().setVisible(true);
                }
            }
        });
    }

    /**
     * This constructor makes a tabcomposite with a tab entity.
     *
     * @param inTab
     *            the domain model object to populate the tab composite with.
     */
    public StartPageTab(final Tab inTab)
    {
        super(inTab.getTabName());

        setContents(new StartPageTabContent(inTab));
        tab = inTab;

        menu.addItem("X", menuItems);
        menu.addStyleName(StaticResourceBundle.INSTANCE.coreCss().tabMenu());
        getPanel().add(menu);

        textBox.addStyleName(StaticResourceBundle.INSTANCE.coreCss().newTabTextbox());
        textBox.setMaxLength(TabTemplate.MAX_TAB_NAME_LENGTH);
        textBox.addBlurHandler(new BlurHandler()
        {
            public void onBlur(final BlurEvent event)
            {
                tab.setTabName(textBox.getText());
                StartTabsModel.getInstance().rename(new RenameTabRequest(tab.getId(), textBox.getText()));

                getPanel().remove(textBox);
                getPanel().add(getLabel());
                getPanel().add(menu);
            }
        });

        textBox.addKeyPressHandler(new KeyPressHandler()
        {
            public void onKeyPress(final KeyPressEvent event)
            {
                if (event.getCharCode() == KeyCodes.KEY_ENTER || event.getCharCode() == KeyCodes.KEY_ESCAPE)
                {
                    if (event.getCharCode() == KeyCodes.KEY_ENTER)
                    {
                        tab.setTabName(textBox.getText());
                        StartTabsModel.getInstance().rename(new RenameTabRequest(tab.getId(), textBox.getText()));
                    }
                }

            }
        });

        menuItems.addItem("Change Layout", new Command()
        {
            public void execute()
            {
                ((StartPageTabContent) getContents()).showTabLayoutSelector();
            }
        });

        menuItems.addItem("Rename Tab", new Command()
        {
            public void execute()
            {
                getPanel().remove(getLabel());
                getPanel().remove(menu);
                getPanel().add(textBox);
                textBox.setText(getLabel().getText());
                textBox.setFocus(true);
                textBox.setSelectionRange(0, textBox.getText().length());
            }
        });

        removeItem = new MenuItem("Remove", new Command()
        {
            public void execute()
            {
                StartTabsModel.getInstance().delete(tab);
            }
        });

        menuItems.addItem(removeItem);

        Session.getInstance().getEventBus().addObserver(UpdatedStartPageTabNameResponseEvent.class,
                new Observer<UpdatedStartPageTabNameResponseEvent>()
                {
                    public void update(final UpdatedStartPageTabNameResponseEvent event)
                    {
                        if (event.getResponse() == tab.getId())
                        {
                            getLabel().setText(textBox.getText());
                            getPanel().remove(textBox);
                            getPanel().add(getLabel());
                            getPanel().add(menu);
                        }
                    }
                });

        Session.getInstance().getEventBus().addObserver(UpdatedStartPageLayoutResponseEvent.class,
                new Observer<UpdatedStartPageLayoutResponseEvent>()
                {

                    public void update(final UpdatedStartPageLayoutResponseEvent event)
                    {
                        if (event.getResponse().getId() == tab.getId())
                        {
                            ((StartPageTabContent) getContents()).renderGadgetContainer(event.getResponse());
                            ((StartPageTabContent) getContents()).renderGadgets();
                            ((StartPageTabContent) getContents()).refreshGadgetMetadata();
                        }
                    }
                });

        Session.getInstance().getEventBus().addObserver(GadgetAddedToStartPageEvent.class,
                new Observer<GadgetAddedToStartPageEvent>()
                {
                    public void update(final GadgetAddedToStartPageEvent event)
                    {
                        if (isActive)
                        {
                            ((StartPageTabContent) getContents()).insertGadget(event.getGadget(), true);
                            Session.getInstance().getEventBus().notifyObservers(
                                    new UpdateHistoryEvent(new CreateUrlRequest("action", null, false)));

                            // fetch metadata again to force reevaluation of metadata
                            // (for making sure "edit settings" is hidden, if necessary)
                            List<GadgetDefinition> gadgetDefList = new ArrayList<GadgetDefinition>();
                            gadgetDefList.add(event.getGadget().getGadgetDefinition());
                            (new GadgetMetaDataFetcher(gadgetDefList)).fetchMetaData();
                        }
                    }
                });

    }

    /**
     * @return tab the tab.
     */
    public Tab getTab()
    {
        return tab;
    }

    /**
     * Disable the remove button.
     */
    public void disableRemove()
    {
        menuItems.removeItem(removeItem);
    }

    /**
     * Enable the remove button.
     */
    public void enableRemove()
    {
        menuItems.addItem(removeItem);
    }

    /**
     * Set's the tab's CSS style to be inactive.
     */
    @Override
    public void unSelect()
    {
        super.unSelect();
        isActive = false;
        if (getContents() != null && getContents() instanceof StartPageTabContent)
        {
            ((StartPageTabContent) getContents()).hideTabLayoutSelector();
        }
    }

    @Override
    public void select()
    {
        super.select();
        isActive = true;
        if (getContents() != null && getContents() instanceof StartPageTabContent)
        {
            ((StartPageTabContent) getContents()).renderGadgets();
            if (((StartPageTabContent) getContents()).isAnyGadgetMaximized())
            {
                RootPanel.get().addStyleName(StaticResourceBundle.INSTANCE.coreCss().maximizedGadget());
            }
            else
            {
                RootPanel.get().removeStyleName(StaticResourceBundle.INSTANCE.coreCss().maximizedGadget());
            }
        }
    }

    @Override
    public String getIdentifier()
    {
        if (tab == null)
        {
            return null;
        }
        else
        {
            return String.valueOf(tab.getId());
        }
    }

    /**
     * Get the textbox.
     *
     * @return the textbox.
     */
    public TextBox getTextBox()
    {
        return textBox;
    }

}
