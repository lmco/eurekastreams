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
package org.eurekastreams.web.client.ui.pages.master;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

/**
 * Resource Bundle for Eureka Streams.
 */
public interface StaticResourceBundle extends ClientBundle
{
    /**
     * The instance of the bundle.
     */
    StaticResourceBundle INSTANCE = GWT.create(StaticResourceBundle.class);

    /**
     * Core CSS.
     * @return core.css.
     */
    @NotStrict
    @Source("style/core.css")
    CoreCss coreCss();
     
    /**
     * YUI CSS.
     * @return yui-core.css.
     */
    @NotStrict
    @Source("style/yui-core.css")
    CssResource yuiCss();
    
    /**
     * @return image.
     */
    @Source("style/images/navLogo.png")
    ImageResource navLogo();
    
    /**
     * @return image.
     */
    @Source("style/images/navStartPage.png")
    ImageResource navStartPage();
    
    /**
     * @return image.
     */
    @Source("style/images/navActivity.png")
    ImageResource navActivity();

    /**
     * @return image.
     */
    @Source("style/images/navProfiles.png")
    ImageResource navProfiles();

    /**
     * @return image.
     */
    @Source("style/images/navMyProfile.png")
    ImageResource navMyProfile();

    /**
     * @return image.
     */
    @Source("style/images/navSettings.png")
    ImageResource navSettings();

    /**
     * @return image.
     */
    @Source("style/images/navHelp.png")
    ImageResource navHelp();

    /**
     * @return image.
     */
    @Source("style/images/red-error-icon-sm.png")
    ImageResource redErrorIconSmall();

    /**
     * @return image.
     */
    @Source("style/images/information-circle-icon.png")
    ImageResource informationCircleIcon();

    /**
     * @return image.
     */
    @Source("style/images/global-nav-bar-5px.png")
    ImageResource globalNavBar5px();
    
    /**
     * @return image.
     */
    @Source("style/images/footerTermsOfService.png")
    ImageResource footerTermsOfService();
    
    /**
     * @return image.
     */
    @Source("style/images/poweredByEurekaStreams.png")
    ImageResource poweredByEurekaStreams();
    
    /**
     * @return image.
     */
    @Source("style/images/footerBackground.png")
    @ImageOptions(repeatStyle=RepeatStyle.Horizontal)
    ImageResource footerBackground();
    
    /**
     * @return image.
     */
    @Source("style/images/globalNavSearchBtn.png")
    ImageResource globalNavSearchBtn();
    
    /**
     * @return image.
     */
    @Source("style/images/navBg.png")
    @ImageOptions(repeatStyle=RepeatStyle.Horizontal)
    ImageResource navBg();

    /**
     * @return image.
     */
    @Source("style/images/globalnavBg.png")
    @ImageOptions(repeatStyle=RepeatStyle.Horizontal)
    ImageResource globalNavBg();

    /**
     * @return image.
     */
    @Source("style/images/navNotification.png")
    ImageResource navNotification();

    /**
     * @return image.
     */
    @Source("style/images/streamHeaderOverlay.png")
    ImageResource streamHeaderOverlay();

    /**
     * @return image.
     */
    @Source("style/images/createGadget.png")
    ImageResource createGadget();
    
    /**
     * @return image.
     */
    @Source("style/images/createGadgetHover.png")
    ImageResource createGadgetHover();
    
    /**
     * @return image.
     */
    @Source("style/images/activityStreamSearchBtn.png")
    ImageResource activityStreamSearchBtn(); 
    
    /**
     * @return image.
     */
    @Source("style/images/feed-icon-16x16.png")
    ImageResource feedIcon16();
    
    /**
     * @return image.
     */
    @Source("style/images/activitypageListItemOverlayInActive.png")
    ImageResource activitypageListItemOverlayInActive();
    
    /**
     * @return image.
     */
    @Source("style/images/postBtnInactive.png")
    ImageResource postBtnInactive();

    /**
     * @return image.
     */
    @Source("style/images/postBtn.png")
    ImageResource postBtn();
    
    /**
     * @return image.
     */
    @Source("style/images/moveBtnInactive.png")
    ImageResource moveBtnInactive();

    /**
     * @return image.
     */
    @Source("style/images/moveBtnActive.png")
    ImageResource moveBtnActive();
}
