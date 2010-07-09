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

/**
 * Auto complete drop down widget.
 *
 */
public class AutoCompleteEntityDropDownPanel extends AutoCompleteDropDownPanel
{
    /**
     * Default constructor.
     *
     * @param inUrl
     *            the resource url.
     */
    public AutoCompleteEntityDropDownPanel(final String inUrl)
    {
        super(inUrl);
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
        setUpAutoCompleteJSON(taId, acdId, url);
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
    private static native void setUpAutoCompleteJSON(final String taId,
            final String acdId, final String url)
    /*-{
       var autocompleteConfig={
          config:{
              delimiter:"",
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
           resultsList:"entities",
           fields: ["displayName","entityType","uniqueId","streamScopeId"]
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
            @org.eurekastreams.web.client.ui.common.autocomplete.AutoCompleteEntityDropDownPanel::onItemSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(sArgs[2]);
           });
       }

       myAutoComp.doBeforeLoadData = function(sQuery, oResponse, oPayload) {

           if(oResponse.results[0] == null || oResponse.results[0].entityType == "NOTSET")
           {
               myAutoComp.suppressInputUpdate = true;
           }
           else
           {
               myAutoComp.suppressInputUpdate = false;
           }
           return true;
       };

    }-*/;
}
