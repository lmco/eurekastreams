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
package org.eurekastreams.web.client.jsni;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.server.domain.GeneralGadgetDefinition;
import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.server.domain.gadgetspec.UserPrefDTO;
import org.eurekastreams.web.client.events.GotGadgetMetaDataEvent;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Wrapper class for shindigs gadget meta data fetcher.
 *
 */
public class GadgetMetaDataFetcher extends FlowPanel
{
    /**
     * Interface for the command that will be executed once we get the metadata.
     *
     */
    public interface GotGadgetMetaDataCommand
    {
        /**
         * This method will be called after we get the metadata.
         *
         * @param metadata
         *            the metadata that we got from shindig.
         */
        void onGotGadgetMetaData(final List<GadgetMetaDataDTO> metadata);
    }

    /**
     * The command.
     */
    private static GotGadgetMetaDataCommand onGotGadgetMetaData = null;
    /**
     * The gadget defs to look up.
     */
    private static List<GeneralGadgetDefinition> gadgetDefs;

    /**
     * Default constructor.
     *
     * @param inGadgetDefs
     *            the gadget defs to look up.
     */
    @SuppressWarnings("unchecked")
    public GadgetMetaDataFetcher(final List inGadgetDefs)
    {
        gadgetDefs = inGadgetDefs;
    }

    /**
     * Adds a command to be executed when we have the metadata.
     *
     * @param inCommand
     *            the command to execute.
     */
    @Deprecated
    public void addOnMetaDataRetrievedCommand(final GotGadgetMetaDataCommand inCommand)
    {
        onGotGadgetMetaData = inCommand;
    }

    /**
     * Gets called from JSNI with the shindig results and wraps them into Java objects.
     *
     * @param metadata
     *            the shindig metadata.
     */
    public static void gotGadgetMetaData(final JavaScriptObject metadata)
    {
        List<GadgetMetaDataDTO> gadgetMetaDataList = new LinkedList<GadgetMetaDataDTO>();
        for (GeneralGadgetDefinition gadgetDef : gadgetDefs)
        {
            gadgetMetaDataList.add(new GadgetMetaDataDTO(gadgetDef));
        }

        for (int i = 0; i < getGadgetCount(metadata); i++)
        {
            if (isGadgetValid(metadata, i))
            {
                String url = getGadgetUrl(metadata, i);
                int j = 0;
                for (j = 0; j < gadgetDefs.size(); j++)
                {
                    if (gadgetDefs.get(j).getUrl().equals(url))
                    {
                        break;
                    }
                }
                GadgetMetaDataDTO gMetaData = new GadgetMetaDataDTO(gadgetDefs.get(j));
                gMetaData.setTitle(getGadgetTitle(metadata, i));
                gMetaData.setTitleUrl(getGadgetTitleUrl(metadata, i));
                gMetaData.setDescription(getGadgetDescription(metadata, i));
                gMetaData.setAuthor(getGadgetAuthor(metadata, i));
                gMetaData.setAuthorEmail(getGadgetAuthorEmail(metadata, i));
                gMetaData.setThumbnail(getGadgetThumbnail(metadata, i));
                gMetaData.setScreenshot(getGadgetScreenshot(metadata, i));
                gMetaData.setString(getGadgetString(metadata, i));


                List<UserPrefDTO> userPrefs = new ArrayList<UserPrefDTO>();
                String[] keys = getUserPrefsKeys(metadata, i);
                for (int k = 0; k < keys.length; k++)
                {
                    UserPrefDTO userPref = new UserPrefDTO();
                    userPref.setDisplayName(getUserPrefDisplayName(metadata, i, keys[k]));
                    userPref.setDataType(getUserPrefType(metadata, i, keys[k]).toUpperCase());
                    userPrefs.add(userPref);
                }
                gMetaData.setUserPrefs(userPrefs);

                List<String> features = new ArrayList<String>();
                String[] featuresFromGadget = getFeatures(metadata, i);
                for (int f = 0; f < featuresFromGadget.length; f++)
                {
                    features.add(featuresFromGadget[f]);
                }
                gMetaData.setFeatures(features);
                List<String> views = new ArrayList<String>();
                String[] viewsFromGadget = getViewNames(metadata, i);
                for (int f = 0; f < viewsFromGadget.length; f++)
                {
                    views.add(viewsFromGadget[f]);
                }
                gMetaData.setViewNames(views);

                gadgetMetaDataList.remove(j);
                gadgetMetaDataList.add(j, gMetaData);
            }
        }

        // DEPRECATED: Use event bus.
        if (onGotGadgetMetaData != null)
        {
            onGotGadgetMetaData.onGotGadgetMetaData(gadgetMetaDataList);
        }
        Session.getInstance().getEventBus().notifyObservers(new GotGadgetMetaDataEvent(gadgetMetaDataList));
    }

    /**
     * Gets called by clients to fetch metadata.
     */
    public void fetchMetaData()
    {
        String[] urlArray = new String[gadgetDefs.size()];
        for (int i = 0; i < gadgetDefs.size(); i++)
        {
            urlArray[i] = gadgetDefs.get(i).getUrl();
        }
        fetchMetaData(urlArray);
    }

    /**
     * Gets the gadgets count from shindig.
     *
     * @param metadata
     *            the metadata object.
     * @return the count.
     */
    private static native int getGadgetCount(final JavaScriptObject metadata) /*-{
                                                        return metadata.gadgets.length;
                                                    }-*/;

    /**
     * Gets the user prefs count.
     *
     * @param metadata
     *            the metadata object.
     * @param index
     *            the index.
     * @return the user prefs count.
     */
    private static native String[] getUserPrefsKeys(final JavaScriptObject metadata, final int index) /*-{
                      var arr = [];
                      for (var p in metadata.gadgets[index].userPrefs)
                      {
                          arr.push(p);
                      }

                      return arr;
                   }-*/;

    /**
     * Gets the gadget view names.
     *
     * @param metadata
     *            the metadata object.
     * @param index
     *            the index.
     * @return the gadget view names.
     */
    private static native String[] getViewNames(final JavaScriptObject metadata, final int index) /*-{
                      var arr = [];
                      for (var p in metadata.gadgets[index].views)
                      {
                          arr.push(p);
                      }

                      return arr;
                   }-*/;

    /**
     * Gets the features.
     *
     * @param metadata
     *            the metadata object.
     * @param index
     *            the index.
     * @return the features.
     */
    private static native String[] getFeatures(final JavaScriptObject metadata, final int index) /*-{
                      return metadata.gadgets[index].features;
                   }-*/;

    /**
     * Gets the user pref display name.
     *
     * @param metadata
     *            the metadata object.
     * @param gadgetIndex
     *            the gadget index.
     * @param prefIndex
     *            the user pref index.
     * @return the user pref display name.
     */
    private static native String getUserPrefDisplayName(final JavaScriptObject metadata, final int gadgetIndex,
            final String prefIndex) /*-{
                   return metadata.gadgets[gadgetIndex].userPrefs[prefIndex].displayName;
                }-*/;

    /**
     * Gets the user pref type.
     *
     * @param metadata
     *            the metadata object.
     * @param gadgetIndex
     *            the gadget index.
     * @param prefIndex
     *            the user pref index.
     * @return the user pref type.
     */
    private static native String getUserPrefType(final JavaScriptObject metadata, final int gadgetIndex,
            final String prefIndex) /*-{
                   return metadata.gadgets[gadgetIndex].userPrefs[prefIndex].type;
                }-*/;

    /**
     * Checks if the gadget is valid.
     *
     * @param metadata
     *            the metadata object.
     * @param index
     *            the index.
     * @return whether or not the gadget is valid.
     */
    private static native boolean isGadgetValid(final JavaScriptObject metadata, final int index) /*-{
                                                        return metadata.gadgets[index].errors == null;
                                                    }-*/;

    /**
     * Gets the gadget title.
     *
     * @param metadata
     *            the metadata.
     * @param index
     *            the index.
     * @return the title.
     */
    private static native String getGadgetTitle(final JavaScriptObject metadata, final int index) /*-{
                                                        return metadata.gadgets[index].title;
                                                    }-*/;

    /**
     * Gets the gadget url.
     *
     * @param metadata
     *            the metadata.
     * @param index
     *            the index.
     * @return the url.
     */
    private static native String getGadgetTitleUrl(final JavaScriptObject metadata, final int index) /*-{
                                                    return metadata.gadgets[index].titleUrl;
                                                 }-*/;

    /**
     * Gets the gadget url.
     *
     * @param metadata
     *            the metadata.
     * @param index
     *            hte index.
     * @return the url.
     */
    private static native String getGadgetUrl(final JavaScriptObject metadata, final int index) /*-{
                                                    return metadata.gadgets[index].url;
                                                 }-*/;

    /**
     * Gets the gadget desc.
     *
     * @param metadata
     *            the metadata.
     * @param index
     *            the index.
     * @return the desc.
     */
    private static native String getGadgetDescription(final JavaScriptObject metadata, final int index) /*-{
                                                    return metadata.gadgets[index].description;
                                                 }-*/;

    /**
     * Gets the gadget author.
     *
     * @param metadata
     *            the metadata.
     * @param index
     *            the index.
     * @return the author.
     */
    private static native String getGadgetAuthor(final JavaScriptObject metadata, final int index) /*-{
                                                    return metadata.gadgets[index].author;
                                                 }-*/;

    /**
     * Gets the gadget author email.
     *
     * @param metadata
     *            the metadata.
     * @param index
     *            the index.
     * @return the authors email.
     */
    private static native String getGadgetAuthorEmail(final JavaScriptObject metadata, final int index) /*-{
                                                    return metadata.gadgets[index].authorEmail;
                                                 }-*/;

    /**
     * Gets the gadget thumbnail.
     *
     * @param metadata
     *            the metadata.
     * @param index
     *            the index.
     * @return the thumbnail.
     */
    private static native String getGadgetThumbnail(final JavaScriptObject metadata, final int index) /*-{
                                                    return metadata.gadgets[index].thumbnail;
                                                 }-*/;

    /**
     * Gets the gadget screenshot.
     *
     * @param metadata
     *            the metadata.
     * @param index
     *            the index.
     * @return the screenshot.
     */
    private static native String getGadgetScreenshot(
            final JavaScriptObject metadata, final int index) /*-{
                                  return metadata.gadgets[index].screenshot;
                               }-*/;

    /**
     * Gets the gadget string.
     *
     * @param metadata
     *            the metadata.
     *
     * @param index
     *            the index.     *
     * @return the string.
     */
    private static native String getGadgetString(
            final JavaScriptObject metadata, final int index) /*-{
                                  return metadata.gadgets[index].string;
                               }-*/;


    /**
     * Fetches the metadata from shindig.
     *
     * @param url
     *            the urls to fetch for.
     */
    private native void fetchMetaData(final String[] url) /*-{
                                                        var request = '{';
                                                        request += '"context": {';
                                                        request += '"country": StaticResourceBundle.INSTANCE.coreCss().default(),';
                                                        request += '"language": StaticResourceBundle.INSTANCE.coreCss().default(),';
                                                        request += '"view": "preview",';
                                                        request += '"container": "eureka"';
                                                        request += '},"gadgets": [';

                                                        for(var i=0;i<url.length;i++)
                                                        {
                                                            request += '{StaticResourceBundle.INSTANCE.coreCss().url(): "' + url[i] +  '"}';
                                                            if(i!=url.length-1)
                                                            {
                                                                request += ",";
                                                            }
                                                        }

                                                        request += ']}';

                                                        var callback = function(m){
                                                              if (m != null )
                                                              {
                   @org.eurekastreams.web.client.jsni.GadgetMetaDataFetcher::gotGadgetMetaData(Lcom/google/gwt/core/client/JavaScriptObject;)(m);
                                                              }
                                                        };


            $wnd.eurekastreams.util.sendRequestToServer("/gadgets/metadata",
                                                    "POST", request,
                                                            callback, true);

                                                    }-*/;
}
