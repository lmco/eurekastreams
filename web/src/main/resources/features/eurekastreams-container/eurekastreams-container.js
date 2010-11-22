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
var eurekastreams = eurekastreams || {};

/**
 * REST based endpoint user prefs store implementation. 
 */

/**
* User preference store implementation.
* TODO: Implement request token signing of the request here.
* @constructor
*/
eurekastreams.RESTUserPrefStore = function()  {
    shindig.UserPrefStore.call(this);
};

eurekastreams.RESTUserPrefStore.inherits(shindig.UserPrefStore);

eurekastreams.RESTUserPrefStore.prototype.getPrefs = function(gadget, callback)  { 
    var userPrefsUri = "/resources/gadgets/" + gadget.id + "/userprefs/";
    eurekastreams.util.sendRequestToServer(
        userPrefsUri, "GET", null, callback, true);
};

eurekastreams.RESTUserPrefStore.prototype.savePrefs = function(gadget) { 
    var userPrefsUri = "/resources/gadgets/" + gadget.id + "/userprefs/";
    gwt_updateGadgetPrefs(gadget.id, gadgets.json.stringify(gadget.getUserPrefs()));
    eurekastreams.util.sendRequestToServer(
            userPrefsUri, "PUT", gadgets.json.stringify(gadget.getUserPrefs()), null, true);
};

/**
 * Eureka Streams StaticLayoutManager extension.
 */
eurekastreams.StaticLayoutManager = function() {
	shindig.StaticLayoutManager.call(this);
};

eurekastreams.StaticLayoutManager.inherits(shindig.StaticLayoutManager);

eurekastreams.StaticLayoutManager.prototype.getGadgetChrome = function(gadget) {
    var chromeId = "gadget-zone-render-zone-" + gadget.id;
    return chromeId ? document.getElementById(chromeId) : null;
};

/**
 * Eureka Streams Gadget extension.
 */
eurekastreams.Gadget = function(opt_params) {
    shindig.Gadget.call(this, opt_params);
    this.serverBase_ = '/gadgets/'; // default gadget server
    this.queryIfrGadgetType_();
};

eurekastreams.Gadget.inherits(shindig.BaseIfrGadget);

eurekastreams.Gadget.prototype.GADGET_IFRAME_PREFIX_ = 'remote_iframe_';
eurekastreams.Gadget.prototype.CONTAINER = "${build.gadget.container.name}";
eurekastreams.Gadget.prototype.BASE_URL = "${build.web.grbaseurl}";
eurekastreams.Gadget.prototype.debug = 0;
eurekastreams.Gadget.prototype.caja = 0;
eurekastreams.Gadget.prototype.cssClassGadget = 'gadgets-gadget';
eurekastreams.Gadget.prototype.cssClassGadgetLoading = 'gadgets-gadget-loading';
eurekastreams.Gadget.prototype.cssClassGadgetLoadingImage = 'gadgets-gadget-loading-image';
eurekastreams.Gadget.prototype.cssClassTitleBar = 'gadget-zone-chrome-title-bar';
eurekastreams.Gadget.prototype.cssClassTitle = 'gadget-zone-chrome-title-bar-title-button';
eurekastreams.Gadget.prototype.cssClassTitleButtonBar = 'gadgets-gadget-title-button-bar';
eurekastreams.Gadget.prototype.cssClassGadgetUserPrefsDialog = 'gadgets-gadget-user-prefs-dialog';
eurekastreams.Gadget.prototype.cssClassGadgetUserPrefsDialogActionBar = 'gadgets-gadget-user-prefs-dialog-action-bar';
eurekastreams.Gadget.prototype.cssClassTitleButton = 'gadgets-gadget-title-button';
eurekastreams.Gadget.prototype.cssClassGadgetContent = 'gadgets-gadget-content';
    
/**
 * This overrides the creation of the title bar method.
 * This has been replaced with our own GWT method to create the title bar 
 * we use the same name convention to link up the correct titlebar to the correct gadget
 * 
 */
eurekastreams.Gadget.prototype.getTitleBarContent = function(continuation) {
    continuation('');
};
  
eurekastreams.Gadget.prototype.setServerBase = function(url) {
    this.serverBase_ = this.BASE_URL + url;
};

eurekastreams.Gadget.prototype.handleSaveUserPrefs = function() {
  this.hideUserPrefsDialog();

  var numFields = document.getElementById('m_' + this.id +
      '_numfields').value;
  for (var i = 0; i < numFields; i++) {
    var input = document.getElementById('m_' + this.id + '_' + i);
    var userPrefNamePrefix = 'm_' + this.id + '_up_';
    var userPrefName = input.name.substring(userPrefNamePrefix.length);
    var userPrefValue = input.value;
    this.userPrefs[userPrefName] = userPrefValue;
  }
    
  this.saveUserPrefs();
  eurekastreams.util.gadgetIFrameUrlRefreshing(this);
  eurekastreams.util.refreshGadgetIFrameUrl(this, null);
};

eurekastreams.Gadget.prototype.getUrlForView = function(view) {
    return 'setview/' + view;
};

/**
 * This is overriden because we need to use the frame index
 * when maximizing because the frames array seems to lose the
 * ability to access elements by name in the array.
 */
eurekastreams.Gadget.prototype.render = function(chrome) {
  if (chrome) {
    var gadget = this;
    this.getContent(function(content) {
      //add the iframe to the DOM. 
      chrome.innerHTML = content;
	  document.getElementById(gadget.getIframeId()).src = gadget.getIframeUrl(); 
      
    });
  }
};

/**
 * Build the user prefs dialog with the custom REST endpoint.
 */
eurekastreams.Gadget.prototype.handleOpenUserPrefsDialog = function() {
    if (this.userPrefsDialogContentLoaded) {
      this.showUserPrefsDialog();
    } else {
      var gadget = this;
      var gadgetCallbackName = 'gadget_callback_' + this.id;
      window[gadgetCallbackName] = function(userPrefsDialogContent) {
        gadget.userPrefsDialogContentLoaded = true;
        gadget.buildUserPrefsDialog(userPrefsDialogContent);
        gadget.showUserPrefsDialog();
      };

      var script = document.createElement('script');
      script.src = '/resources/gadgets/' + this.id + '/' + encodeURIComponent(this.specUrl) + 
          '/userprefsformui/' + this.getUserPrefsParams();
          document.body.appendChild(script);
    }
};
  
eurekastreams.Gadget.prototype.handleCancelUserPrefs = function() {
    this.userPrefsDialogContentLoaded = false;
    this.hideUserPrefsDialog();
};

  /**
   * This method is used when a gadget is currently being displayed and just the contents are to be refreshed.
   * There are a number of things happening in this method that are important to ensure both ff and ie 
   * behave properly.
   * - The gadgetContent and gadgetContentLoading elements are swapped in an attempt to display the spinny, however
   * that doesn't seem to be working quite right.
   * - The setRelayUrl and setAuthToken calls force ie to reconnect the rpc channel through a patch that
   * Eureka added to the rpc javascript.
   * - The timeout and url with the random number at the end help force both browsers to actually refresh the content.
   * I think that the timeout is the key to ensuring the content is refreshed because the browser seems to get confused when the iframe src
   * is just reset quickly in sequence.  So the about:blank, clears the iframe, then a second later the src is reset to the gadget url
   * with the random number attached.  This is a combination of experimentation with the browser.
   */
eurekastreams.Gadget.prototype.refresh = function() {
    /*
    this.userPrefsDialogContentLoaded = false; 
    document.getElementById('gadgetContent_' + this.getIframeId()).style.display = 'none';
    document.getElementById('gadgetContentLoading_' + this.getIframeId()).style.display = 'inline-block';
    document.getElementById(this.getIframeId()).src = 'about:blank';
    gadgets.rpc.setRelayUrl(this.getIframeId(), this.getServerBase() + this.rpcRelay);
    gadgets.rpc.setAuthToken(this.getIframeId(), this.rpcToken, false);

    var iframeUrl = this.getIframeUrl().replace(/'/g, "\\'");
    var statement = "document.getElementById('" + this.getIframeId() + "').src = '" + iframeUrl + "&r=" + Math.floor(Math.random()*11) + "';";
    setTimeout(statement, 1000);
    */
    eurekastreams.util.refreshGadgetIFrameUrl(this, null);
  };

/**
 * This method was overriden to support  setting the value of a user preference
 * that is not included in the gadget definition.
 */
eurekastreams.Gadget.prototype.setUserPref = function(name, value) {
    this.userPrefs[name] = value;
    shindig.container.userPrefStore.savePrefs(this);
};

/**
 * Default implementation uses the .value of the pref object which we don't set.
 */
eurekastreams.Gadget.prototype.getUserPrefValue = function(name) {
    return this.userPrefs[name];  
};

/**
 * Short circuiting this method to force to type IfrGadget.
 */
eurekastreams.Gadget.prototype.queryIfrGadgetType_ = function() {
    var gadget = this;
    var subClass = eurekastreams.IfrGadget;
    for (var name in subClass) if (subClass.hasOwnProperty(name)) {
      gadget[name] = subClass[name];
    }
}

//---------
//IfrGadget

eurekastreams.IfrGadget = {
getMainContent: function(continuation) {
  var iframeId = this.getIframeId();
  gadgets.rpc.setRelayUrl(iframeId, this.serverBase_ + this.rpcRelay);
  gadgets.rpc.setAuthToken(iframeId, this.rpcToken, false);
  continuation('<span id="gadgetContentLoading_' + iframeId + '" class="' + this.cssClassGadgetLoading + '"></span>' +
      '<div class="' + this.cssClassGadgetContent + '" id="gadgetContent_' + iframeId + '" ' + 
      'style="display:none;"' +  
      '><div class="gadget-container">' + 
      '<iframe id="' + iframeId + '" name="' + iframeId + '" class="' + this.cssClassGadget + '" src="about:blank' +
      '" frameborder="no" scrolling="no"' + 
      ' onload="javascript:document.getElementById(\'gadgetContent_' + iframeId + '\').style.display=\'inline\';' +
      'document.getElementById(\'gadgetContentLoading_' + iframeId + '\').style.display=\'none\';"' +
      (this.height ? ' height="' + this.height + '"' : '') +
      (this.width ? ' width="' + this.width + '"' : '') +
      '></iframe></div></div>');
},

finishRender: function(chrome) {
window.frames[this.getIframeId()].location = this.getIframeUrl();
},

getIframeUrl: function() {
return this.serverBase_ + 'ifr?' +
   'container=' + this.CONTAINER +
   '&mid=' +  this.id +
   '&nocache=' + shindig.container.nocache_ +
   '&country=' + shindig.container.country_ +
   '&lang=' + shindig.container.language_ +
   '&view=' + shindig.container.view_ +
   (this.specVersion ? '&v=' + this.specVersion : '') +
   (shindig.container.parentUrl_ ? '&parent=' + encodeURIComponent(shindig.container.parentUrl_) : '') +
   (this.debug ? '&debug=1' : '') +
   this.getAdditionalParams() +
   this.getUserPrefsParams() +
   (this.secureToken ? '&st=' + this.secureToken : '') +
   '&url=' + encodeURIComponent(this.specUrl) +
   '#rpctoken=' + this.rpcToken +
   (this.viewParams ?
       '&view-params=' +  encodeURIComponent(gadgets.json.stringify(this.viewParams)) : '') +
   (this.hashData ? '&' + this.hashData : '');
}
};

/**
 * Eureka Streams GadgetService extension.
 */
eurekastreams.GadgetService = function() {
    shindig.IfrGadgetService.call(this);
    //Register additional rpc calls on the container
    gadgets.rpc.register('getAppId', eurekastreams.GadgetService.getAppId);
    gadgets.rpc.register('getModuleId', eurekastreams.GadgetService.getModuleId);
    gadgets.rpc.register('getOrgName', eurekastreams.GadgetService.getOrgName);
    gadgets.rpc.register('getGroupName', eurekastreams.GadgetService.getGroupName);
    gadgets.rpc.register('triggerShowNotificationEvent', eurekastreams.GadgetService.triggerShowNotificationEvent);
    gadgets.rpc.register('eurekaNavigate', eurekastreams.GadgetService.eurekaNavigate);
    gadgets.rpc.register('refreshCurrentGadget', eurekastreams.GadgetService.refreshCurrentGadget);
};

eurekastreams.GadgetService.inherits(shindig.IfrGadgetService);

/**
 * RPC call to retrieve the app id for a gadget.
 */
eurekastreams.GadgetService.getAppId = function()   {
    var id = shindig.container.gadgetService.getGadgetIdFromModuleId(this.f);
    var gadget = shindig.container.getGadget(id);
    return Number(gadget.appId);
};

/**
 * RPC call to retrieve the current module id for the requesting gadget.
 */
eurekastreams.GadgetService.getModuleId = function() {
    return Number(this.f);
};


/**
 * RPC call to refresh the current gadget.
 */
eurekastreams.GadgetService.refreshCurrentGadget = function() {
  var id = shindig.container.gadgetService.getGadgetIdFromModuleId(this.f);
  var gadget = shindig.container.getGadget(id);
  eurekastreams.util.gadgetIFrameUrlRefreshing(gadget);
  eurekastreams.util.refreshGadgetIFrameUrl(gadget, null);
};


/**
 * RPC call to retrieve the current ORGNAME.
 */
eurekastreams.GadgetService.getOrgName = function() {
    if(!(typeof ORGNAME == "undefined")) {
        return ORGNAME;
    }
    else {
        return null;
    }
};

/**
 * RPC call to retrieve the current GROUPNAME.
 */
eurekastreams.GadgetService.getGroupName = function() {
    if(!(typeof GROUPNAME == "undefined")) {
        return GROUPNAME;
    }
    else {
        return null;
    }
};

/**
 * Navigates the page to a new url based on a gadgets requested view and
 * parameters.
 * This is intended to override the gadgets.IfrGadgetService.prototype.requestNavigateTo
 * in shindig's gadgets.js, but needs to be done more correctly.  Namely, creating a 
 * true Eureka Streams GadgetService that inherits from the Shindig one and makes the call
 * more cleanly.
 */
eurekastreams.GadgetService.prototype.requestNavigateTo = function(view, opt_params) {
    var id = shindig.container.gadgetService.getGadgetIdFromModuleId(this.f);
    var paramStr = "";
    if (opt_params) {
        paramStr = gadgets.json.stringify(opt_params);
    }
  
    gwt_changeGadgetState(id, view, paramStr);
};

eurekastreams.GadgetService.prototype.setTitle = function(title) {
    var element = document.getElementById(this.f + '_title');
    if (element) {
    	element.innerHTML = title.replace(/&/g, '&amp;').replace(/</g, '&lt;');
    }
    var id = shindig.container.gadgetService.getGadgetIdFromModuleId(this.f);
    var gadget = shindig.container.getGadget(id);
gadget.setUserPref('eureka-container-gadget-title',title);
};
	
/**
 * Generates an event that will cause the UI notifier to be displayed.
 */
eurekastreams.GadgetService.triggerShowNotificationEvent = function(inNotification) {
    gwt_triggerShowNotificationEvent(inNotification);
};

/**
 * Navigates to a relative eureka streams url in the browser. Necessary since gadget iFrame will not
 * allow changing main container location.
 */
eurekastreams.GadgetService.eurekaNavigate = function(inRelativeUrl) {
    gwt_newHistoryItem(inRelativeUrl);
};

/**
 * Eureka Streams customized container
 */
eurekastreams.Container = function() {
    shindig.IfrContainer.call(this);
};

eurekastreams.Container.inherits(shindig.IfrContainer);

eurekastreams.Container.prototype.userPrefStore = new eurekastreams.RESTUserPrefStore();

eurekastreams.Container.prototype.gadgetClass = eurekastreams.Gadget;

eurekastreams.Container.prototype.gadgetService = new eurekastreams.GadgetService();

eurekastreams.Container.prototype.layoutManager = new eurekastreams.StaticLayoutManager();

/**
 * Overriding this method because Eureka Streams supplies its own unique gadget id rather
 * than just retrieving the next available.
 */
eurekastreams.Container.prototype.addGadget = function(gadget) {
    this.gadgets_[this.getGadgetKey_(gadget.id)] = gadget;
};

shindig.container = new eurekastreams.Container();
