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

package org.eurekastreams.web.client.ui.common.autocomplete;

import java.io.Serializable;

import org.eurekastreams.web.client.ui.common.form.elements.FormElement;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Label;

/**
 *  A form element for an auto complete text box.
 */
public class AutoCompleteItemDropDownFormElement extends AutoCompleteDropDownPanel implements FormElement
{
    /**
     * The label.
     */
    private Label label = new Label();
    /**
     * Puts a (required) on the form.
     */
    private Label requiredLabel = new Label();
    /**
     * instructions for the element.
     */
    private Label instructions = new Label();
    /**
     * The key that this corresponds to in the model.
     */
    private String key = "";
    /**
     * The name of the result list from the JSON.
     */
    private String resultListName;
    /**
     * The delimiter for autocomplete items.
     */
    private String delimiter;

    /**
     * Creates an Auto Completing text box.
     *
     * @param inUrl
     *            The resource URL for the auto complete.
     */
    public AutoCompleteItemDropDownFormElement(final String inUrl)
    {
        super(inUrl);
    }

    /**
     * Creates an Auto Completing text box form element.
     *
     * @param inLabelVal
     *            The label.
     * @param inKey
     *            The key.
     * @param inInstructions
     *            The instructions.
     * @param inCurrentValue
     *            The current value.
     * @param inRequired
     *            Whether or not this is a required element.
     * @param inUrl
     *            The resource URL for the auto complete.
     * @param inResultListName
     *            The result list name from the JSON
     * @param inDelimiter
     *            The delimiter.
     */
    public AutoCompleteItemDropDownFormElement(
            final String inLabelVal,
            final String inKey,
            final String inCurrentValue,
            final String inInstructions,
            final boolean inRequired,
            final String inUrl,
            final String inResultListName,
            final String inDelimiter)
    {
        this(inLabelVal, inKey, inCurrentValue, inInstructions, inRequired,
                inUrl, inResultListName, ElementType.TEXTBOX, inDelimiter);
    }

    /**
     * Creates an Auto Completing text box or text area form element.
     *
     * @param inLabelVal
     *            The label.
     * @param inKey
     *            The key.
     * @param inInstructions
     *            The instructions.
     * @param inCurrentValue
     *            The current value.
     * @param inRequired
     *            Whether or not this is a required element.
     * @param inUrl
     *            The resource URL for the auto complete.
     * @param inResultListName
     *            The result list name from the JSON
     * @param inElementType
     *            The type of form element to display.
     * @param inDelimiter
     *            The delimiter.
     */
    public AutoCompleteItemDropDownFormElement(
            final String inLabelVal,
            final String inKey,
            final String inCurrentValue,
            final String inInstructions,
            final boolean inRequired,
            final String inUrl,
            final String inResultListName,
            final ElementType inElementType,
            final String inDelimiter)
    {
        super(inUrl, inElementType);

        key = inKey;
        resultListName = inResultListName;
        delimiter = inDelimiter;

        label.setText(inLabelVal);
        label.setStyleName(StaticResourceBundle.INSTANCE.coreCss().formLabel());

        setText(inCurrentValue);

        if (inRequired)
        {
            requiredLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().requiredFormLabel());
            requiredLabel.setText("(required)");
        }

        instructions.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formInstructions());
        instructions.setText(inInstructions);

        this.insert(label, 0);
        this.insert(requiredLabel, 2);
        this.add(instructions);
    }

    /**
     * Gets the key.
     *
     * @return the key.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Gets the value of the text box.
     *
     * @return
     *            the value.
     */
    public Serializable getValue()
    {
        return getText();
    }

    /**
     * Sets the value of the text box.
     *
     * @param value
     *            the value.
     */
    public void setValue(final String value)
    {
        setText(value);
    }

    /**
     * Gets called if this element has an error.
     *
     * @param errMessage
     *            the error Message.
     */
    public void onError(final String errMessage)
    {
        label.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formError());
    }

    /**
     * Gets called if this element was successful.
     */
    public void onSuccess()
    {
        label.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().formError());
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
    @Override
    protected void setUpAutoComplete(final String taId,
            final String acdId, final String url)
    {
        setUpAutoCompleteJSON(taId, acdId, url, resultListName, delimiter);
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
     * @param inResultsListName
     *            the name of the results list returned.
     *  @param inDelimiter
     *            the item delimiter string.
     */
    private static native void setUpAutoCompleteJSON(final String taId,
            final String acdId, final String url, final String inResultsListName, final String inDelimiter)
    /*-{
       var autocompleteConfig={
          config:{
              delimiter:inDelimiter,
              textAreaId:taId,
                 autoCompleteDiv:acdId
          },
          data:{
             autoComplete:url
          }
       };

       var oDS;
       var myAutoComp;

       oDS=new $wnd.YAHOO.util.XHRDataSource(autocompleteConfig.data.autoComplete);
       oDS.responseType = $wnd.YAHOO.util.XHRDataSource.TYPE_JSON;
       oDS.resultTypeList = false;
       oDS.responseSchema={
           resultsList:inResultsListName
       };
       oDS.formatResult = function(oResultData, sQuery, sResultMatch) {
           return oResultData.displayName;
       };

       myAutoComp =new $wnd.YAHOO.widget.AutoComplete(autocompleteConfig.config.textAreaId,
           autocompleteConfig.config.autoCompleteDiv, oDS);
       myAutoComp.queryDelay=.2;

       myAutoComp.generateRequest=function(sQuery){return sQuery+"/";};
       myAutoComp.delimChar=autocompleteConfig.config.delimiter;

       if (myAutoComp.itemSelectEvent != null)
       {
           myAutoComp.itemSelectEvent.subscribe(function(sType, sArgs) {
            @org.eurekastreams.web.client.ui.common.autocomplete.AutoCompleteItemDropDownFormElement::onItemSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(sArgs[2]);
           });
       }
    }-*/;

}
