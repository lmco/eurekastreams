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
package org.eurekastreams.web.client.ui.common.autocomplete;

import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;

/**
 * Auto complete drop down widget.
 *
 */
public abstract class AutoCompleteDropDownPanel extends FlowPanel
{
    /**
     * The possible form element types.
     *
     */
    public enum ElementType
    {
        /**
         * A textbox form element.
         */
        TEXTBOX,
        /**
         * A textarea form element.
         */
        TEXTAREA;
    }

    /**
     * The command interface for what happens when one selects the item.
     *
     */
    public interface OnItemSelectedCommand
    {
        /**
         * Gets called when the item is selected.
         *
         * @param obj
         *            the javascript object of the selected.
         */
        void itemSelected(final JavaScriptObject obj);
    }

    /** The command. */
    private static OnItemSelectedCommand command;

    /** Text box/area. */
    private TextBoxBase textWidget;

    /** The text widget as a textbox (avoid cast). */
    private TextBox textBox;

    /** Clear panel. */
    private final FlowPanel clearPanel = new FlowPanel();

    /** Results panel. */
    private final FlowPanel resultsPanel = new FlowPanel();

    /** Random identifier. */
    private final String rand = String.valueOf(Random.nextInt());

    /**
     * Default constructor.
     *
     * @param url
     *            the resource url.
     */
    public AutoCompleteDropDownPanel(final String url)
    {
        this(url, ElementType.TEXTBOX);
    }

    /**
     * Default constructor.
     *
     * @param url
     *            the resource url.
     * @param inElementType
     *            the element type
     */
    public AutoCompleteDropDownPanel(final String url, final ElementType inElementType)
    {
        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().yuiSkinSam());
        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().autoComplete());

        final SimplePanel textWrapper = new SimplePanel();

        if (inElementType == ElementType.TEXTBOX)
        {
            textBox = new TextBox();
            textWidget = textBox;
            textWrapper.addStyleName(StaticResourceBundle.INSTANCE.coreCss().textboxInputWrapper());
        }
        else
        {
            textWidget = new ExtendedTextArea(false);
            textWrapper.addStyleName(StaticResourceBundle.INSTANCE.coreCss().textareaInputWrapper());
        }
        textWidget.getElement().setAttribute("id", "actb-" + rand);

        // Need to do this to fix an especially nasty IE CSS bug (input margin inheritance)
        textWrapper.addStyleName(StaticResourceBundle.INSTANCE.coreCss().acInputWrapper());
        textWrapper.add(textWidget);
        this.add(textWrapper);

        resultsPanel.getElement().setAttribute("id", "acra-" + rand);
        clearPanel.setStyleName(StaticResourceBundle.INSTANCE.coreCss().clear());

        this.add(clearPanel);
        this.add(resultsPanel);

        DeferredCommand.addCommand(new Command()
        {
            public void execute()
            {
                setUpAutoComplete("actb-" + rand, "acra-" + rand, url);
            }
        });
    }

    /**
     * Sets the default text.
     *
     * @param text
     *            the text.
     */
    public void setDefaultText(final String text)
    {
        textWidget.setText(text);
        textWidget.addStyleName(StaticResourceBundle.INSTANCE.coreCss().defaultClass());
        textWidget.addFocusHandler(new FocusHandler()
        {
            public void onFocus(final FocusEvent inEvent)
            {
                textWidget.setText("");
                textWidget.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().defaultClass());
            }
        });
    }

    /**
     * Sets the text.
     *
     * @param text
     *            The text.
     */
    public void setText(final String text)
    {
        textWidget.setText(text);
    }

    /**
     * Clear the text in the text box.
     */
    public void clearText()
    {
        textWidget.setText("");
    }

    /**
     * Set the maxlength field.
     *
     * @param maxLength
     *            the length to set.
     */
    public void setMaxLength(final int maxLength)
    {
        textBox.setMaxLength(maxLength);
    }

    /**
     * Get the text from the text box.
     *
     * @return The text.
     */
    public String getText()
    {
        return textWidget.getText();
    }

    /**
     * Returns the random identifier.
     *
     * @return the random identifier.
     */
    public String getRandomIdentifier()
    {
        return rand;
    }

    /**
     * Set up the command.
     *
     * @param inCommand
     *            the command.
     */
    public void setOnItemSelectedCommand(final OnItemSelectedCommand inCommand)
    {
        command = inCommand;
    }

    /**
     * Gets called by the JSNI when the item is selected.
     *
     * @param obj
     *            the javascript object.
     */
    public static void onItemSelect(final JavaScriptObject obj)
    {
        if (command != null)
        {
            command.itemSelected(obj);
        }
    }

    /**
     * Returns the text widget.
     *
     * @return the text widget.
     */
    protected TextBoxBase getTextWidget()
    {
        return textWidget;
    }

    /**
     * Sets up the auto complete.
     *
     * @param taId
     *            the text area id.
     * @param acdId
     *            the auto complete area id.
     * @param url
     *            the url.
     */
    protected abstract void setUpAutoComplete(final String taId, final String acdId, final String url);

}
