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
package org.eurekastreams.web.client.ui.common.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.ExceptionResponseEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PreSwitchedHistoryViewEvent;
import org.eurekastreams.web.client.events.PreventHistoryChangeEvent;
import org.eurekastreams.web.client.events.SubmitFormIfChangedEvent;
import org.eurekastreams.web.client.events.data.ValidationExceptionResponseEvent;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.BaseModel;
import org.eurekastreams.web.client.model.Insertable;
import org.eurekastreams.web.client.model.Updateable;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.form.elements.FormElement;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Form Builder.
 * 
 */
public class FormBuilder extends FlowPanel
{
    /**
     * Method to call on the BaseModel. (Forms never fetch or delete).
     */
    public enum Method
    {
        /**
         * Call the Insert method.
         */
        INSERT,
        /**
         * Call the Update method.
         */
        UPDATE
    }

    /**
     * The command to exe on cancel.
     */
    private Command onCancelCommand;

    /**
     * The base model.
     */
    private final BaseModel baseModel;

    /**
     * The method.
     */
    private final Method method;
    /**
     * The title of the form.
     */
    private final Label formTitle = new Label();
    /**
     * Contains the form.
     */
    private final FlowPanel formContainer = new FlowPanel();
    /**
     * Contains the inner elements of the form.
     */
    private final FlowPanel formElementsContainer = new FlowPanel();
    /**
     * The error box for evil.
     */
    private final FlowPanel errorBox = new FlowPanel();
    /**
     * The fade panel to disable controls while submitting.
     */
    private final FlowPanel fadePanel = new FlowPanel();
    /**
     * The submit button of the form.
     */
    private final Anchor submitButton = new Anchor("");
    /**
     * The cancel button of the form.
     */
    private final Hyperlink cancelButton = new Hyperlink("Cancel", History.getToken());

    /**
     * The processing spinner.
     */
    private final Label processingSpinny = new Label("Processing...");

    /**
     * The data of the form.
     */
    private final List<FormElement> data = new LinkedList<FormElement>();

    /**
     * The original values of the form, to see if anything has changed.
     */
    private final HashMap<String, Serializable> originalValues = new HashMap<String, Serializable>();

    /**
     * Did the user add a "last form element".
     */
    private boolean addedLastFormElement = false;

    /**
     * Is the form inactive.
     */
    private boolean inactive = false;

    /**
     * If should scroll to top of window on ValidationExceptionResponseEvent.
     */
    private boolean scrollToTopOnValidationError = true;

    /** If events are currently wired to the event bus. */
    private boolean eventsWired;
    /** Event handler. */
    private Observer<ValidationExceptionResponseEvent> validationExceptionResponseHandler;
    /** Event handler. */
    private Observer<ExceptionResponseEvent> exceptionResponseHandler;
    /** Event handler. */
    private Observer<PreSwitchedHistoryViewEvent> preSwitchedHistoryViewHandler;
    /** Event handler. */
    private Observer<SubmitFormIfChangedEvent> submitFormIfChangedHandler;

    /**
     * Constructor.
     * 
     * @param title
     *            the form title.
     * @param inBaseModel
     *            the base model to use to persist data.
     * @param inMethod
     *            the method to call on the base model.
     */
    public FormBuilder(final String title, final BaseModel inBaseModel, final Method inMethod)
    {
        this(title, inBaseModel, inMethod, true);
    }

    /**
     * Constructor.
     * 
     * @param title
     *            the form title.
     * @param inBaseModel
     *            the base model to use to persist data.
     * @param inMethod
     *            the method to call on the base model.
     * @param inScrollToTopOnValidationError
     *            scroll to top on ValidationExceptionResponseEvent.
     */
    public FormBuilder(final String title, final BaseModel inBaseModel, final Method inMethod,
            final boolean inScrollToTopOnValidationError)
    {
        scrollToTopOnValidationError = inScrollToTopOnValidationError;
        baseModel = inBaseModel;
        method = inMethod;
        submitButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formSubmitButton());
        cancelButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formCancelButton());
        errorBox.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formErrorBox());
        formContainer.add(errorBox);

        formContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formContainer());

        formContainer.add(formElementsContainer);

        fadePanel.setVisible(false);
        fadePanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formDisable());
        formContainer.add(fadePanel);

        formContainer.add(submitButton);

        processingSpinny.setVisible(false);
        processingSpinny.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formSubmitSpinny());
        processingSpinny.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formProcessingSpinny());
        formContainer.add(processingSpinny);

        formContainer.add(cancelButton);

        if (!title.equals(""))
        {
            formTitle.setText(title);
            formTitle.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formTitle());
            this.add(formTitle);
        }

        this.add(formContainer);
        errorBox.setVisible(false);

        submitButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                submit();
            }
        });

        cancelButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                inactive = true;
                if (onCancelCommand != null)
                {
                    onCancelCommand.execute();
                }
            }
        });

        createEventHandlers();
        // wireEventHandlers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onAttach()
    {
        super.onAttach();
        wireEventHandlers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDetach()
    {
        super.onDetach();
        unwireEventHandlers();
    }

    /**
     * Creates the event handlers used with the event bus.
     */
    private void createEventHandlers()
    {
        validationExceptionResponseHandler = new Observer<ValidationExceptionResponseEvent>()
        {
            public void update(final ValidationExceptionResponseEvent event)
            {
                List<String> errors = new ArrayList<String>();

                for (FormElement element : data)
                {
                    String error = event.getResponse().getErrors().get(element.getKey());
                    if (error != null)
                    {
                        errors.add(error);
                    }
                }

                if (!errors.isEmpty())
                {
                    errorBox.clear();
                    resetSubmitButton();

                    String errorBoxText = new String("Please correct the following errors:<ul>");

                    for (FormElement element : data)
                    {
                        String error = event.getResponse().getErrors().get(element.getKey());
                        if (error != null)
                        {
                            errorBox.setVisible(true);
                            errorBoxText += "<li>" + error + "</li>";
                            element.onError(error);
                        }
                        else
                        {
                            element.onSuccess();
                        }
                    }

                    errorBoxText += "</ul>";
                    errorBox.add(new HTML(errorBoxText));
                    if (scrollToTopOnValidationError)
                    {
                        Window.scrollTo(0, 0);
                    }
                }
            }
        };

        exceptionResponseHandler = new Observer<ExceptionResponseEvent>()
        {
            public void update(final ExceptionResponseEvent event)
            {
                if (event.getModel() == baseModel)
                {
                    resetSubmitButton();
                }
            }
        };

        preSwitchedHistoryViewHandler = new Observer<PreSwitchedHistoryViewEvent>()
        {
            public void update(final PreSwitchedHistoryViewEvent arg1)
            {
                if (hasFormChanged())
                {
                    if (new WidgetJSNIFacadeImpl().confirm("The form has been changed. Do you wish to save changes?"))
                    {
                        EventBus eventBus = Session.getInstance().getEventBus();
                        eventBus.notifyObservers(new PreventHistoryChangeEvent());
                        eventBus.notifyObservers(new SubmitFormIfChangedEvent());
                    }
                }
            }
        };

        submitFormIfChangedHandler = new Observer<SubmitFormIfChangedEvent>()
        {
            public void update(final SubmitFormIfChangedEvent arg1)
            {
                if (hasFormChanged())
                {
                    submit();
                }
            }
        };
    }

    /**
     * Attaches the event handlers to the event bus.
     */
    public void wireEventHandlers()
    {
        if (!eventsWired)
        {
            final EventBus eventBus = Session.getInstance().getEventBus();
            eventBus.addObserver(ValidationExceptionResponseEvent.class, validationExceptionResponseHandler);
            eventBus.addObserver(ExceptionResponseEvent.class, exceptionResponseHandler);
            eventBus.addObserver(PreSwitchedHistoryViewEvent.class, preSwitchedHistoryViewHandler);
            eventBus.addObserver(SubmitFormIfChangedEvent.class, submitFormIfChangedHandler);
            eventsWired = true;
        }
    }

    /**
     * Removes the event handlers from the event bus.
     */
    public void unwireEventHandlers()
    {
        if (eventsWired)
        {
            final EventBus eventBus = Session.getInstance().getEventBus();
            eventBus.removeObserver(ValidationExceptionResponseEvent.class, validationExceptionResponseHandler);
            eventBus.removeObserver(ExceptionResponseEvent.class, exceptionResponseHandler);
            eventBus.removeObserver(PreSwitchedHistoryViewEvent.class, preSwitchedHistoryViewHandler);
            eventBus.removeObserver(SubmitFormIfChangedEvent.class, submitFormIfChangedHandler);
            eventsWired = false;
        }
    }

    /**
     * Cancel the form manually.
     */
    public void turnOffChangeCheck()
    {
        inactive = true;
    }

    /**
     * Has the form changed?
     * 
     * @return has the form changed?
     */
    private boolean hasFormChanged()
    {
        boolean changed = false;

        for (FormElement element : data)
        {
            if (originalValues.containsKey(element.getKey())
                    && (originalValues.get(element.getKey()) != null && !originalValues.get(element.getKey()).equals(
                            element.getValue()))
                    || (element.getValue() != null && !element.getValue().equals(originalValues.get(element.getKey()))))
            {
                changed = true;
            }
        }

        return changed && !inactive;
    }

    /**
     * Submit the form.
     */
    private void submit()
    {
        processingSpinny.setVisible(true);
        fadePanel.setVisible(true);
        submitButton.setVisible(false);

        HashMap<String, Serializable> dataValues = new HashMap<String, Serializable>();
        for (FormElement element : data)
        {
            dataValues.put(element.getKey(), element.getValue());
            originalValues.put(element.getKey(), element.getValue());
        }

        if (method.equals(Method.INSERT))
        {
            ((Insertable) baseModel).insert(dataValues);
        }
        else
        {
            ((Updateable) baseModel).update(dataValues);
        }
    }

    /**
     * Happens on success of the form.
     */
    public void onSuccess()
    {
        resetSubmitButton();
        errorBox.clear();
        errorBox.setVisible(false);

        for (FormElement element : data)
        {
            element.onSuccess();
        }
    }

    /**
     * Sets the CSS class on the Submit button. Used if you're changing it to say, an update button.
     * 
     * @param cssClass
     *            the css class.
     */
    public void setSubmitButtonClass(final String cssClass)
    {
        submitButton.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().formSubmitButton());
        submitButton.addStyleName(cssClass);
    }

    /**
     * Used to inject widgets in the form container itself. Useful if you need to add something after the submit and
     * cancel.
     * 
     * @param widget
     *            the widget.
     */
    public void addWidgetToFormContainer(final Widget widget)
    {
        formContainer.add(widget);
    }

    /**
     * Sets the token for when the user clicks cancel.
     * 
     * @param token
     *            the token.
     */
    public void setOnCancelHistoryToken(final String token)
    {
        cancelButton.setTargetHistoryToken(token);
    }

    /**
     * Gives a command to execute on cancel.
     * 
     * @param inOnCancelCommand
     *            the command.
     */
    public void addOnCancelCommand(final Command inOnCancelCommand)
    {
        onCancelCommand = inOnCancelCommand;
    }

    /**
     * Adds a form element to the form.
     * 
     * @param element
     *            the form element.
     */
    public void addFormElement(final FormElement element)
    {
        if (element instanceof Widget)
        {
            ((Widget) element).addStyleName(StaticResourceBundle.INSTANCE.coreCss().formElement());
            if (addedLastFormElement)
            {
                formElementsContainer.insert((Widget) element, formElementsContainer.getWidgetCount() - 1);
            }
            else
            {
                formElementsContainer.add((Widget) element);
            }
        }

        data.add(element);
        originalValues.put(element.getKey(), element.getValue());
    }

    /**
     * Adds a "last form element". This is a form element that will ALWAYS stay at the bottom of the form Regardless of
     * others added.
     * 
     * @param element
     *            the element.
     */
    public void addLastFormElement(final FormElement element)
    {
        if (element instanceof Widget)
        {
            ((Widget) element).addStyleName(StaticResourceBundle.INSTANCE.coreCss().formElement());
            formElementsContainer.insert((Widget) element, formElementsContainer.getWidgetCount());
            addedLastFormElement = true;
        }

        data.add(element);
        originalValues.put(element.getKey(), element.getValue());
    }

    /**
     * Gets the form value from the key.
     * 
     * @param key
     *            the key.
     * @return the value.
     */
    public Serializable getFormValue(final String key)
    {
        for (FormElement formElement : data)
        {
            if (formElement.getKey().equals(key))
            {
                return formElement.getValue();
            }
        }

        return null;
    }

    /**
     * Adds a form divider.
     */
    public void addFormDivider()
    {
        Label divider = new Label();
        divider.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formDivider());
        formElementsContainer.add(divider);
    }

    /**
     * Reset the submit button.
     */
    private void resetSubmitButton()
    {
        processingSpinny.setVisible(false);
        fadePanel.setVisible(false);
        submitButton.setVisible(true);
    }

    /**
     * Adds a clearing divider to the form.
     */
    public void addClear()
    {
        FlowPanel clear = new FlowPanel();
        clear.addStyleName(StaticResourceBundle.INSTANCE.coreCss().clear());
        formElementsContainer.add(clear);
    }

    /**
     * Adds a form label to the form.
     * 
     * @param header
     *            the label.
     * @return the label.
     */
    public Label addFormLabel(final String header)
    {
        Label subHeader = new Label(header);
        subHeader.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formLabel());
        subHeader.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formStandaloneLabel());
        formElementsContainer.add(subHeader);

        return subHeader;
    }

    /**
     * Adds a widget to the form panel.
     * 
     * @param w
     *            the widget to add.
     */
    public void addWidget(final Widget w)
    {
        formElementsContainer.add(w);
    }

}
