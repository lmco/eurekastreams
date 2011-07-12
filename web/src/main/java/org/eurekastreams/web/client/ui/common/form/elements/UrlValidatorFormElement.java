/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.form.elements;

import java.io.Serializable;

import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

/**
 * User types whatever they want into the textbox. The command is used to transform what they type into an RSS/ATOM feed
 * url endpoint (in case you want them to only type part of the URL, like a username). Control Can then check if the
 * feed is valid and shows the feed title.
 *
 */
public class UrlValidatorFormElement extends BasicTextBoxFormElement
{
    /**
     * The inferface for the generate url command.
     *
     */
    public interface GenerateUrlCommand
    {
        /**
         * Generate the url.
         *
         * @param value
         *            the value they typed.
         * @return the url.
         */
        String generateUrl(final String value);
    }

    /**
     * This.
     */
    private final UrlValidatorFormElement thisBuffered;
    /**
     * The general url command.
     */
    private final GenerateUrlCommand generateUrlCommand;
    /**
     * The processing spinner.
     */
    private final Label processingSpinny = new Label("");

    /**
     * Error label.
     */
    private final Label errorLabel = new Label("Error importing feed");

    /** Label with details about error. */
    private final Label errorDetail = new Label();

    /**
     * Error box.
     */
    private final FlowPanel errorBox;
    /**
     * Import button.
     */
    private final Hyperlink importBtn;

    /**
     * Url panel.
     */
    private final FlowPanel urlPanel = new FlowPanel();
    /**
     * Url label.
     */
    private final Label urlLabel = new Label();

    /**
     * Am I in a failed on uninited state?
     */
    private boolean failed = true;

    /**
     * Value form element.
     */
    private final ValueOnlyFormElement originalValueFormElement;

    /**
     * Gets the value. If the feed has failed or not been verified, return null.
     *
     * @return value.
     */
    @Override
    public Serializable getValue()
    {
        if (super.getValue().equals("") || failed)
        {
            return "";
        }
        return generateUrlCommand.generateUrl((String) super.getValue());
    }

    /**
     * Get the original value the user typed in in a form element.
     *
     * @return the form element.
     */
    public ValueOnlyFormElement getOriginalValueFormElement()
    {
        return originalValueFormElement;
    }

    /**
     * Get original value..
     *
     * @return original value.
     */
    private String getOriginalValue()
    {
        return (String) super.getValue();
    }

    /**
     * Default constructor.
     *
     * @param labelVal
     *            the label.
     * @param inKey
     *            the key.
     * @param value
     *            the value.
     * @param inInstructions
     *            the instructions.
     * @param required
     *            whether its required.
     * @param inGenerateUrlCommand
     *            generate url command.
     */
    public UrlValidatorFormElement(final String labelVal, final String inKey, final String value,
            final String inInstructions, final boolean required, final GenerateUrlCommand inGenerateUrlCommand)
    {
        super(labelVal, inKey, value, inInstructions, required);
        thisBuffered = this;
        originalValueFormElement = new ValueOnlyFormElement(inKey + "original", value);

        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().urlValidator());

        generateUrlCommand = inGenerateUrlCommand;

        Hyperlink closeUrlPanel = new Hyperlink("Delete", History.getToken());
        closeUrlPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().smallX());
        closeUrlPanel.addClickHandler(new ClickHandler()
        {

            public void onClick(final ClickEvent event)
            {
                urlPanel.setVisible(false);
                getTextBox().setVisible(true);
                getTextBox().setText("");
                importBtn.setVisible(true);
                failed = true;
            }
        });

        urlPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().urlPanel());
        urlPanel.setVisible(false);
        urlLabel.setStyleName(StaticResourceBundle.INSTANCE.coreCss().urlLabel());
        urlPanel.add(closeUrlPanel);
        urlPanel.add(urlLabel);
        mainPanel.insert(urlPanel, 3);

        importBtn = new Hyperlink("import", History.getToken());
        importBtn.addStyleName(StaticResourceBundle.INSTANCE.coreCss().importButton());
        importBtn.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formUploadButton());
        importBtn.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formButton());
        mainPanel.insert(importBtn, 4);
        processingSpinny.setVisible(false);
        processingSpinny.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formSubmitSpinny());
        mainPanel.insert(processingSpinny, 5);

        errorBox = new FlowPanel();
        errorBox.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formErrorBox());
        errorBox.setVisible(false);

        errorLabel.getElement().setId("url-validator-form-element-error-label");
        errorLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().error());
        errorBox.add(errorLabel);

        errorDetail.addStyleName(StaticResourceBundle.INSTANCE.coreCss().error());
        errorBox.add(errorDetail);

        mainPanel.insert(errorBox, 0);

        importBtn.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                importUrl();
            }
        });
    }

    /**
     * Import the URL.
     */
    public void importUrl()
    {
        failed = false;
        importBtn.setVisible(false);
        processingSpinny.setVisible(true);

        Session.getInstance().getActionProcessor()
                .makeRequest("getFeedTitleAction", getValue(), new AsyncCallback<String>()
                {
                    /* implement the async call back methods */
                    public void onFailure(final Throwable caught)
                    {
                        importBtn.setVisible(true);
                        processingSpinny.setVisible(false);
                        errorBox.setVisible(true);
                        if (caught instanceof ValidationException)
                        {
                            errorDetail.setVisible(true);
                            errorDetail.setText(caught.getMessage());
                        }
                        else
                        {
                            errorDetail.setVisible(false);
                        }
                        requiredLabel.setVisible(true);
                        instructions.setVisible(true);
                        failed = true;
                        thisBuffered.onError("");
                    }

                    public void onSuccess(final String result)
                    {
                        originalValueFormElement.setValue(getOriginalValue());
                        importBtn.setVisible(false);
                        requiredLabel.setVisible(false);
                        instructions.setVisible(false);
                        processingSpinny.setVisible(false);
                        errorBox.setVisible(false);
                        urlPanel.setVisible(true);
                        getTextBox().setVisible(false);
                        urlLabel.setText(result);
                        thisBuffered.onSuccess();
                    }
                });
    }
}
