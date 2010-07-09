/**
 * These constants are used to access things like the title
 */

gadgets.IfrGadget.prototype.GADGET_IFRAME_PREFIX_ = 'remote_iframe_';

gadgets.IfrGadget.prototype.CONTAINER = 'default';
gadgets.IfrGadget.prototype.cssClassGadget = 'gadgets-gadget';
gadgets.IfrGadget.prototype.cssClassTitleBar = 'gadget-zone-chrome-title-bar';
gadgets.IfrGadget.prototype.cssClassTitle = 'gadget-zone-chrome-title-bar-title-button';

gadgets.IfrGadget.prototype.cssClassTitleButtonBar =
    'gadgets-gadget-title-button-bar';
gadgets.IfrGadget.prototype.cssClassGadgetUserPrefsDialog =
    'gadgets-gadget-user-prefs-dialog';
gadgets.IfrGadget.prototype.cssClassGadgetUserPrefsDialogActionBar =
    'gadgets-gadget-user-prefs-dialog-action-bar';
gadgets.IfrGadget.prototype.cssClassTitleButton = 'gadgets-gadget-title-button';
gadgets.IfrGadget.prototype.cssClassGadgetContent = 'gadgets-gadget-content';

/*
 * This overrides the creation of the title bar method of the gadgets.js.
 * This has been replaced with our own GWT method to create the title bar 
 * we use the same name convention to link up the correct titlebar to the correct gadget
 * 
 */
gadgets.IfrGadget.prototype.getTitleBarContent = function(continuation) {
	continuation('');
};

gadgets.rpc.register('requestNavigateTo', eurekastreamsrequestNavigateTo);
gadgets.rpc.register('getAppId', eurekastreamsGetAppId);

/**
 * Navigates the page to a new url based on a gadgets requested view and
 * parameters.
 * This is intended to override the gadgets.IfrGadgetService.prototype.requestNavigateTo
 * in shindig's gadgets.js, but needs to be done more correctly.  Namely, creating a 
 * true Eureka Streams GadgetService that inherits from the Shindig one and makes the call
 * more cleanly.
 */
function eurekastreamsrequestNavigateTo(view,
    opt_params) {
  var id = gadgets.container.gadgetService.getGadgetIdFromModuleId(this.f);
  var url = gadgets.container.gadgetService.getUrlForView(view);
  url += '/gadgetid/' + id;
  if (opt_params) {
    var paramStr = gadgets.json.stringify(opt_params);
    if (paramStr.length > 0) {
      url += '/appParams/' + encodeURIComponent(paramStr);
    }
  }

  if (url && document.location.href.indexOf(url) == -1) {
    gwt_newHistoryItem(url);
  }
};

gadgets.IfrGadgetService.prototype.getUrlForView = function(
	    view) {
	return '#setview/' + view;
}

function eurekastreamsGetAppId()
{
	var id = gadgets.container.gadgetService.getGadgetIdFromModuleId(this.f);
	var gadget = gadgets.container.getGadget(id);
	return gadget.appId;
}

gadgets.Container.prototype.addGadgetWithId = function(gadget, id) {
	  gadget.id = id;
	  gadget.setUserPrefs(this.userPrefStore.getPrefs(gadget));
	  this.gadgets_[this.getGadgetKey_(gadget.id)] = gadget;
};



gadgets.StaticLayoutManager.prototype.addGadgetChromeId = 
	function(gadgetChromeId) {
    if (this.gadgetChromeIds_ ) {
            this.gadgetChromeIds_.push(gadgetChromeId);  
    }
    else {
            this.gadgetChromeIds_ = [gadgetChromeId];
    }
 };
 

gadgets.StaticLayoutManager.prototype.getGadgetChrome = function(gadget) {
	//TODO I hate this for loop.  We need to totally rewrite the gadget.js file to get rid of the need to do this. I SO HATE THIS
	var chromeIndex=null;
	var inc=0
	  for (var key in gadgets.container.gadgets_) {
		  	if (key=="gadget_"+gadget.id)
		  	{
		  		chromeIndex=inc;	
		  	}
		  	inc++;
	  }
	var chromeId = this.gadgetChromeIds_[chromeIndex];
return chromeId ? document.getElementById(chromeId) : null;
};

//Override for refreshing gadgets.  For some reason, the frame gets dereferrenced 
//during refresh actions and thus needs this approach.
gadgets.Gadget.prototype.render = function(chrome) {
  if (chrome) {
    var gadget = this;
    this.getContent(function(content) {
      chrome.innerHTML = content;
      //This following line is what errors.
      //window.frames[gadget.getIframeId()].location = gadget.getIframeUrl();
      for(var i=0; i<window.frames.length; i++)
      {
	      if(window.frames[i].name==gadget.getIframeId())
	      {
		      window.frames[i].location = gadget.getIframeUrl();
		      break;
	      }
      } 
    });
  }
};

