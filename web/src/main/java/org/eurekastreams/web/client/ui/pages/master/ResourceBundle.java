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
public interface ResourceBundle extends ClientBundle
{
    /**
     * The instance of the bundle.
     */
    ResourceBundle INSTANCE = GWT.create(ResourceBundle.class);

    /**
     * Core CSS.
     * 
     * @return core.css.
     */
    @NotStrict
    @Source("style/core.css")
    CssResource coreCss();

    /**
     * YUI CSS.
     * 
     * @return yui-core.css.
     */
    @NotStrict
    @Source("style/yui-core.css")
    CssResource yuiCss();

    /**
     * IE CSS.
     * 
     * @return ie.css.
     */
    @NotStrict
    @Source("style/ie.css")
    CssResource ieCss();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/navLogo.png")
    ImageResource navLogo();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/navStartPage.png")
    ImageResource navStartPage();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/navActivity.png")
    ImageResource navActivity();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/navProfiles.png")
    ImageResource navProfiles();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/navMyProfile.png")
    ImageResource navMyProfile();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/navSettings.png")
    ImageResource navSettings();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/navHelp.png")
    ImageResource navHelp();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/red-error-icon-sm.png")
    ImageResource redErrorIconSmall();

    /**
     * get image.
     * 
     * @return ImageResource
     */

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/information-circle-icon.png")
    ImageResource informationCircleIcon();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/global-nav-bar-5px.png")
    ImageResource globalNavBar5px();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/footerTermsOfService.png")
    ImageResource footerTermsOfService();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/poweredByEurekaStreams.png")
    ImageResource poweredByEurekaStreams();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/footerBackground.png")
    ImageResource footerBackground();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/globalNavSearchBtn.png")
    ImageResource globalNavSearchBtn();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/navBg.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource navBg();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/globalnavBg.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource globalNavBg();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/navNotification.png")
    ImageResource navNotification();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/streamHeaderOverlay.png")
    ImageResource streamHeaderOverlay();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/createGadget.png")
    ImageResource createGadget();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/createGadgetHover.png")
    ImageResource createGadgetHover();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/activityStreamSearchBtn.png")
    ImageResource activityStreamSearchBtn();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/feed-icon-16x16.png")
    ImageResource feedIcon16();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/activitypageListItemOverlayInActive.png")
    ImageResource activitypageListItemOverlayInActive();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/postBtnInactive.png")
    ImageResource postBtnInactive();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/postBtn.png")
    ImageResource postBtn();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/moveBtnInactive.png")
    ImageResource moveBtnInactive();

    /**
     * get image.
     * 
     * @return ImageResource
     */
    @Source("style/images/moveBtnActive.png")
    ImageResource moveBtnActive();
}
