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
package org.eurekastreams.web.client.ui.common;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

/**
 * A button based on a label which automatically disables itself and shows a spinner when clicked.
 */
public class SpinnerLabelButton extends Composite
{
    /** Extra style when enabled. */
    public static final String ENABLED_STYLE = "label-button-enabled";

    /** Extra style when in-process. */
    public static final String SPINNER_STYLE = "label-button-wait-spinner";

    /** Extra style when disabled. */
    public static final String DISABLED_STYLE = "label-button-disabled";

    /** Style always applied. */
    public static final String STYLE = "label-button";

    /** The "button" itself. */
    private Label button = new Label();

    /** The (caller's) action to take when clicked. */
    private ClickHandler clickHandler;

    /** If the button is currently clickable. */
    private boolean enabled = true;

    /**
     * Constructor.
     *
     * @param inClickHandler
     *            Action to take when clicked.
     */
    public SpinnerLabelButton(final ClickHandler inClickHandler)
    {
        clickHandler = inClickHandler;
        button.addStyleName(STYLE);
        button.addStyleName(ENABLED_STYLE);
        initWidget(button);

        button.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent ev)
            {
                if (enabled)
                {
                    enabled = false;
                    button.removeStyleName(ENABLED_STYLE);
                    button.addStyleName(SPINNER_STYLE);
                    clickHandler.onClick(ev);
                }
            }
        });
    }

    /**
     * Puts the button into an active / clickable state.
     */
    public void enable()
    {
        button.addStyleName(ENABLED_STYLE);
        button.removeStyleName(DISABLED_STYLE);
        button.removeStyleName(SPINNER_STYLE);
        enabled = true;
    }

    /**
     * Puts the button into an inactive / non-clickable state.
     */
    public void disable()
    {
        button.removeStyleName(ENABLED_STYLE);
        button.removeStyleName(SPINNER_STYLE);
        button.addStyleName(DISABLED_STYLE);
        enabled = false;
    }
}
