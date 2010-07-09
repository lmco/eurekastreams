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
package org.eurekastreams.web.client.ui.pages.profile.settings.stream;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.server.domain.GeneralGadgetDefinition;
import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.server.domain.stream.plugins.FeedSubscriber;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.StreamPluginsUpdateCanceledEvent;
import org.eurekastreams.web.client.events.data.GotSystemSettingsResponseEvent;
import org.eurekastreams.web.client.jsni.GadgetRenderer;
import org.eurekastreams.web.client.model.BaseModel;
import org.eurekastreams.web.client.model.SystemSettingsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.form.FormBuilder;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.form.elements.BasicCheckBoxFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.BasicDropDownFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextBoxFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.JSNICommandFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.UrlValidatorFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.ValueOnlyFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.UrlValidatorFormElement.GenerateUrlCommand;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * Renders a gadget to the screen and sets up the javascript API for stream plugins.
 *
 */
public class StreamPluginConfigurationPanel extends FlowPanel
{

    /**
     * The form builder.
     */
    private static FormBuilder formBuilder;

    /**
     * Spinny.
     */
    private static FlowPanel spinny;
    /**
     * The conf values.
     */
    private static HashMap<String, Serializable> confValues;

    /**
     * Gadget renderer.
     */
    private GadgetRenderer gadgetRenderer = new GadgetRenderer();

    /**
     * The current mode of the panel.
     */
    private static Method mode;

    /**
     * Default constructor.
     *
     * @param feedSubscriber
     *            The feed subscriber object, which contains the feed and plugin as well.
     * @param model
     *            The base model.
     * @param metaData
     *            the plugin title.
     */
    public StreamPluginConfigurationPanel(final FeedSubscriber feedSubscriber, final BaseModel model,
            final GadgetMetaDataDTO metaData)
    {
        this(feedSubscriber.getFeed().getPlugin(), model, feedSubscriber.getConfSettings(), Method.UPDATE,
                feedSubscriber.getId(), metaData);
    }

    /**
     * Default Constructor.
     *
     * @param pluginDefinition
     *            The plugin being configured
     * @param model
     *            The base model.
     * @param metaData
     *            the plugin title.
     */
    public StreamPluginConfigurationPanel(final GeneralGadgetDefinition pluginDefinition, final BaseModel model,
            final GadgetMetaDataDTO metaData)
    {
        this(pluginDefinition, model, new HashMap<String, Serializable>(), Method.INSERT, null, metaData);
    }

    /**
     * Private Constructor, used to switch values between Add and Edit modes.
     *
     * @param pluginDefinition
     *            The current plugin
     * @param model
     *            The base model.
     * @param inConfValues
     *            The configuration values for the feed
     * @param inMode
     *            The mode of the panel
     * @param feedSubId
     *            id of the feed sub.
     * @param metaData
     *            the plugin title.
     */
    private StreamPluginConfigurationPanel(final GeneralGadgetDefinition pluginDefinition, final BaseModel model,
            final HashMap<String, Serializable> inConfValues, final Method inMode, final Long feedSubId,
            final GadgetMetaDataDTO metaData)
    {
        mode = inMode;
        confValues = inConfValues;


        formBuilder = new FormBuilder("", model, mode);
        this.addStyleName("stream-plugin-conf-panel");
        formBuilder.setVisible(false);

        final Integer random = Random.nextInt(10000);

        FlowPanel gadgetDiv = new FlowPanel();
        gadgetDiv.setVisible(false);
        gadgetDiv.getElement().setAttribute("id", "gadget-zone-render-zone-" + random.toString());
        this.add(gadgetDiv);

        spinny = new FlowPanel();
        spinny.addStyleName("gadgets-gadget-loading");

        this.add(spinny);

        final BasicCheckBoxFormElement lastCheckBox = new BasicCheckBoxFormElement("Terms of Use", "EUREKA:TOS",
                "I understand that the plugin I am about to configure will import activity "
                        + "into this stream and that the content I am sharing is consistent with the Eureka "
                        + "Streams terms of service.", false, mode.equals(Method.UPDATE));

        Session.getInstance().getEventBus().addObserver(GotSystemSettingsResponseEvent.class,
                new Observer<GotSystemSettingsResponseEvent>()
                {

                    public void update(final GotSystemSettingsResponseEvent event)
                    {
                        lastCheckBox.addAdditionalInstructions(new HTML(event.getResponse().getPluginWarning()));
                    }
                });
        SystemSettingsModel.getInstance().fetch(null, true);

        lastCheckBox.addStyleName("stream-plugin-checkbox");

        formBuilder.setOnCancelHistoryToken(History.getToken());
        formBuilder.addWidget(new PluginMetaDataDescriptionPanel(metaData));
        formBuilder.addLastFormElement(lastCheckBox);

        formBuilder.addFormElement(new ValueOnlyFormElement("EUREKA:GROUP", Session.getInstance().getUrlViews().get(
                Session.getInstance().getUrlViews().size() - 1)));
        formBuilder.addFormElement(new JSNICommandFormElement("EUREKA:FEEDURL", "getFeedCommand"));
        formBuilder.addFormElement(new ValueOnlyFormElement("EUREKA:PLUGINID", pluginDefinition.getId()));
        formBuilder.addFormElement(new ValueOnlyFormElement("EUREKA:PLUGINTITLE", metaData.getTitle()));

        if (mode.equals(Method.UPDATE))
        {
            formBuilder.addFormElement(new ValueOnlyFormElement("EUREKA:FEEDSUBID", feedSubId));
        }

        this.add(formBuilder);

        formBuilder.addOnCancelCommand(new Command()
        {
            public void execute()
            {
                Session.getInstance().getEventBus().notifyObservers(new StreamPluginsUpdateCanceledEvent());
            }
        });

        setUpAPI();

        gadgetRenderer.registerSingleGadgetInContainer("gadget-zone-render-zone-" + random.toString(), pluginDefinition
                .getUrl(), Long.valueOf(random), pluginDefinition.getId(), null);
        gadgetRenderer.addGadgetChrome("gadget-zone-render-zone-" + random.toString(), Long.valueOf(random));

        DeferredCommand.addCommand(new Command()
        {
            public void execute()
            {
                // gadgetRenderer.renderGadget(random.toString());
                gadgetRenderer.renderGagdets();
            }
        });
    }

    /**
     * Helper method to check if required text is there and if it is return the required boolean and add a required
     * hidden form element to the form.
     *
     * @param key
     *            the key
     * @param requiredText
     *            the required text.
     * @return the required boolean.
     */
    private static boolean setupRequired(final String key, final String requiredText)
    {
        boolean required;
        if (requiredText == null || requiredText.equals(""))
        {
            required = false;
        }
        else
        {
            required = true;
            formBuilder.addFormElement(new ValueOnlyFormElement("REQUIRED:" + key, requiredText));
        }

        return required;
    }

    /**
     * Adds a url validator.
     *
     * @param labelVal
     *            the label.
     * @param inKey
     *            the key.
     * @param inValue
     *            the value.
     * @param inInstructions
     *            the instructions.
     * @param requiredText
     *            the required text.
     * @param commandName
     *            the command.
     */
    public static void addUrlValidator(final String labelVal, final String inKey, final String inValue,
            final String inInstructions, final String requiredText, final String commandName)
    {

        String value = inValue;
        if ((value == null || value.equals("")) && confValues.containsKey(inKey + "original"))
        {
            value = (String) confValues.get(inKey + "original");
        }

        boolean required = setupRequired(inKey, requiredText);

        UrlValidatorFormElement urlVal = new UrlValidatorFormElement(labelVal, inKey, value, inInstructions, required,
                new GenerateUrlCommand()
                {
                    public String generateUrl(final String value)
                    {
                        return runJSNICommand(value, commandName);
                    }
                });

        formBuilder.addFormElement(urlVal);
        formBuilder.addFormElement(urlVal.getOriginalValueFormElement());
        hideSpinny();

        // If we're updating, hit the import button.
        if (mode.equals(Method.UPDATE))
        {
            urlVal.importUrl();
        }
    }

    /**
     * Adds a text box.
     *
     * @param size
     *            the size.
     * @param labelVal
     *            the label.
     * @param inKey
     *            the key.
     * @param inValue
     *            the value.
     * @param inInstructions
     *            the instructions.
     * @param requiredText
     *            the required text.
     */
    public static void addTextBox(final int size, final String labelVal, final String inKey, final String inValue,
            final String inInstructions, final String requiredText)
    {
        String value = inValue;
        if ((value == null || value.equals("")) && confValues.containsKey(inKey))
        {
            value = (String) confValues.get(inKey);
        }

        boolean required = setupRequired(inKey, requiredText);

        formBuilder.addFormElement(new BasicTextBoxFormElement(size, true, labelVal, inKey, value, inInstructions,
                required));
        hideSpinny();
    }

    /**
     * Adds a checkbox.
     *
     * @param labelVal
     *            the label.
     * @param inKey
     *            the key.
     * @param inInstructions
     *            the instructions.
     * @param requiredText
     *            the required text.
     * @param inChecked
     *            checked.
     */
    public static void addCheckBox(final String labelVal, final String inKey, final String inInstructions,
            final String requiredText, final boolean inChecked)
    {
        boolean checked = inChecked;
        if (confValues.containsKey(inKey))
        {
            checked = Boolean.valueOf((String) confValues.get(inKey));
        }
        boolean required = setupRequired(inKey, requiredText);

        formBuilder.addFormElement(new BasicCheckBoxFormElement(labelVal, inKey, inInstructions, required, checked));
        hideSpinny();
    }

    /**
     * Adds a drop down.
     *
     * @param labelVal
     *            the label.
     * @param inKey
     *            the key.
     * @param values
     *            the values.
     * @param inCurrentValue
     *            the current value.
     * @param inInstructions
     *            the instructions.
     * @param requiredText
     *            required text.
     */
    public static void addDropDown(final String labelVal, final String inKey, final String[] values,
            final String inCurrentValue, final String inInstructions, final String requiredText)
    {
        String currentValue = inCurrentValue;
        if ((currentValue == null || currentValue.equals("")) && confValues.containsKey(inKey))
        {
            currentValue = (String) confValues.get(inKey);
        }
        List<String> valuesList = new ArrayList<String>();
        for (int i = 0; i < values.length; i++)
        {
            valuesList.add(values[i]);
        }

        boolean required = setupRequired(inKey, requiredText);

        formBuilder.addFormElement(new BasicDropDownFormElement(labelVal, inKey, valuesList, currentValue,
                inInstructions, required));
        hideSpinny();
    }

    /**
     * Gets the form value.
     *
     * @param key
     *            key.
     * @return value.
     */
    public static String getFormValue(final String key)
    {
        return formBuilder.getFormValue(key).toString();
    }

    /**
     * Hide the spinny.
     */
    private static void hideSpinny()
    {
        formBuilder.setVisible(true);
        spinny.setVisible(false);
    }
    /**
     * Runs a JSNI command.
     *
     * @param value
     *            the value to pass in it.
     * @param commandName
     *            the command name.
     * @return the return value.
     */
    private static native String runJSNICommand(final String value, final String commandName)
    /*-{
     	 return $wnd[commandName](value);
    }-*/;

    /**
     * Set up the API for the gadget to use.
     */
    private static native void setUpAPI()
    /*-{
     	$wnd.gwt_getFormValue = function(key) {
        	return @org.eurekastreams.web.client.ui.pages.profile.settings.stream.StreamPluginConfigurationPanel::getFormValue(Ljava/lang/String;)(key);
        }

        $wnd.gwt_registerGetFeedCallback = function(func) {
        	eval("$wnd['getFeedCommand'] = " + func);
        }

        $wnd.gwt_addUrlValidator = function(label, key, value, instructions, required, command) {
        		var commandName = "urlValidator"+Math.floor(Math.random()*10000);
        		eval("$wnd['" + commandName + "'] = " + command);
                @org.eurekastreams.web.client.ui.pages.profile.settings.stream.StreamPluginConfigurationPanel::addUrlValidator(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(label, key, value, instructions, required, commandName);
        }

        $wnd.gwt_addTextBox = function(size, label, key, value, instructions, required) {
                @org.eurekastreams.web.client.ui.pages.profile.settings.stream.StreamPluginConfigurationPanel::addTextBox(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(size, label, key, value, instructions, required);
        }

        $wnd.gwt_addCheckBox = function(label, key, value, instructions, required, checked) {
                @org.eurekastreams.web.client.ui.pages.profile.settings.stream.StreamPluginConfigurationPanel::addCheckBox(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)(label, key, value, instructions, required, checked);
        }

        $wnd.gwt_addDropDown = function(label, key, values, currentValue, instructions, required) {
                @org.eurekastreams.web.client.ui.pages.profile.settings.stream.StreamPluginConfigurationPanel::addDropDown(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(label, key, values, currentValue, instructions, required);
        }

    }-*/;
}
