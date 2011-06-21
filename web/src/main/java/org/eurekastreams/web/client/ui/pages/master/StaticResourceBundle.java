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
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.ImageResource;
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
     *
     * @return core.css.
     */
    @Source("style/core.css")
    CoreCss coreCss();

    /**
     * YUI CSS.
     *
     * @return yui-core.css.
     */
    @NotStrict
    @Source("style/yui-core.css")
    CssResource yuiCss();

    @Source("style/images/navLogo.png")
    ImageResource navLogo();

    @Source("style/images/navStartPage.png")
    ImageResource navStartPage();

    @Source("style/images/navActivity.png")
    ImageResource navActivity();

    @Source("style/images/navDiscover.png")
    ImageResource navDiscover();

    @Source("style/images/navStartPageActive.png")
    ImageResource navStartPageActive();

    @Source("style/images/navActivityActive.png")
    ImageResource navActivityActive();

    @Source("style/images/navDiscoverActive.png")
    ImageResource navDiscoverActive();

    @Source("style/images/red-error-icon-sm.png")
    ImageResource redErrorIconSmall();

    @Source("style/images/information-circle-icon.png")
    ImageResource informationCircleIcon();

    @Source("style/images/global-nav-bar-5px.png")
    ImageResource globalNavBar5px();

    @Source("style/images/footerTermsOfService.png")
    ImageResource footerTermsOfService();

    @Source("style/images/poweredByEurekaStreams.png")
    ImageResource poweredByEurekaStreams();

    @Source("style/images/footerBackground.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource footerBackground();

    @Source("style/images/globalNavSearchBtn.png")
    ImageResource globalNavSearchBtn();

    @Source("style/images/navBg.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource navBg();

    @Source("style/images/globalnavBg.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource globalNavBg();

    @Source("style/images/streamHeaderOverlay.png")
    ImageResource streamHeaderOverlay();

    @Source("style/images/createGadget.png")
    ImageResource createGadget();

    @Source("style/images/createGadgetHover.png")
    ImageResource createGadgetHover();

    @Source("style/images/activityStreamSearchBtn.png")
    ImageResource activityStreamSearchBtn();

    @Source("style/images/feed-icon-16x16.png")
    ImageResource feedIcon16();

    @Source("style/images/activitypageListItemOverlayInActive.png")
    ImageResource activitypageListItemOverlayInActive();

    @Source("style/images/activitypageListItemOverlayActive.png")
    ImageResource activitypageListItemOverlayActive();

    @Source("style/images/postBtnInactive.png")
    ImageResource postBtnInactive();

    @Source("style/images/postBtn.png")
    ImageResource postBtn();

    @Source("style/images/share-btn-tall.png")
    ImageResource shareBtnTall();

    @Source("style/images/share-btn-tall-inactive.png")
    ImageResource shareBtnTallInactive();

    @Source("style/images/moveBtnInactive.png")
    ImageResource moveBtnInactive();

    @Source("style/images/moveBtnActive.png")
    ImageResource moveBtnActive();

    @Source("style/images/modalCloseBtn.png")
    ImageResource modalCloseBtn();

    @Source("style/images/modalCloseBtnHover.png")
    ImageResource modalCloseBtnHover();

    @Source("style/images/modalCloseBtnSmall.png")
    ImageResource modalCloseBtnSmall();

    @Source("style/images/modalCloseBtnSmallHover.png")
    ImageResource modalCloseBtnSmallHover();

    @Source("style/images/createOrganizationBtn.png")
    ImageResource createOrganizationBtn();

    @Source("style/images/createGroupBtn.png")
    ImageResource createGroupBtn();

    @Source("style/images/private-icon.png")
    ImageResource privateIcon();

    @Source("style/images/confirm-btn-small.png")
    ImageResource confirmBtnSmall();

    @Source("style/images/approveBtnSmall.png")
    ImageResource approveBtnSmall();

    @Source("style/images/denyBtnSmall.png")
    ImageResource denyBtnSmall();

    @Source("style/images/loginBtn.png")
    ImageResource loginBtn();

    @Source("style/images/cancelBtn.png")
    ImageResource cancelBtn();

    @Source("style/images/feedbackMessageBoxCenter.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource feedbackMessageBoxCenter();

    @Source("style/images/feedbackMessageBoxLeft.png")
    ImageResource feedbackMessageBoxLeft();

    @Source("style/images/feedbackMessageBoxRight.png")
    ImageResource feedbackMessageBoxRight();

    @Source("style/images/createSubOrgBtn.png")
    ImageResource createSubOrgBtn();

    @Source("style/images/websiteIcon.png")
    ImageResource websiteIcon();

    @Source("style/images/connectionsActiveBtn.png")
    ImageResource connectionsActiveBtn();

    @Source("style/images/connectionsInactiveBtn.png")
    ImageResource connectionsInactiveBtn();

    @Source("style/images/wait-spinner.gif")
    ImageResource waitSpinner();

    @Source("style/images/search-icon-sm-gold.png")
    ImageResource searchIconSmGold();

    @Source("style/images/verifyGoodIcon.png")
    ImageResource verifyGoodIcon();

    @Source("style/images/verifyErrorIcon.png")
    ImageResource verifyErrorIcon();

    @Source("style/images/deleteBtnSmall.png")
    ImageResource deleteBtnSmall();

    @Source("style/images/doneBtnSmall.png")
    ImageResource doneBtnSmall();

    @Source("style/images/addBtnSmall.png")
    ImageResource addBtnSmall();

    @Source("style/images/addBtnLarge.png")
    ImageResource addBtnLarge();

    @Source("style/images/editBtnSmall.png")
    ImageResource editBtnSmall();

    @Source("style/images/locationIcon.png")
    ImageResource locationIcon();

    @Source("style/images/emailIcon.png")
    ImageResource emailIcon();

    @Source("style/images/phoneIcon.png")
    ImageResource phoneIcon();

    @Source("style/images/celllcon.png")
    ImageResource cellIcon();

    @Source("style/images/faxIcon.png")
    ImageResource faxIcon();

    @Source("style/images/progressBarShadow.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource progressBarShadow();

    @Source("style/images/progressBarFill.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource progressBarFill();

    @Source("style/images/appliedButton.png")
    ImageResource appliedButton();

    @Source("style/images/addedButton.png")
    ImageResource addedButton();

    @Source("style/images/applyBtnSmall.png")
    ImageResource applyBtnSmall();

    @Source("style/images/addThemeGadgetBtnSmall.png")
    ImageResource addThemeGadgetBtnSmall();

    @Source("style/images/triangle-right.png")
    ImageResource triangleRight();

    @Source("style/images/triangle-down.png")
    ImageResource triangleDown();

    @Source("style/images/plus-icon.png")
    ImageResource plusIcon();

    @Source("style/images/selectBtnInactive.png")
    ImageResource selectBtnInactive();

    @Source("style/images/selectBtnActive.png")
    ImageResource selectBtnActive();

    @Source("style/images/deleteBtn.png")
    ImageResource deleteBtn();

    @Source("style/images/subscribeButton.png")
    ImageResource subscribeButton();

    @Source("style/images/unsubscribeButton.png")
    ImageResource unsubscribeButton();
}
