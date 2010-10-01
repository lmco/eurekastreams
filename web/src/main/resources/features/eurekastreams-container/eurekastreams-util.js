var eurekastreams = eurekastreams || {};
eurekastreams.util = {};

/**
 * Public url for eureka documentation.
 */
eurekastreams.util.getExternalUrl = function () {
    return "http://eurekastreams.org";
};

eurekastreams.util.generateSecureToken = function(ownerId, viewerId, appId, url) {
    var fields = [ownerId, viewerId, appId, "shindig", url, "0", "default"];
    for (var i = 0; i < fields.length; i++) {
        // escape each field individually, for metachars in URL
        fields[i] = escape(fields[i]);
    }
    return fields.join(":");
};

/**
 * This needs to support gadget token use.  Currently, requests only work when security
 * token is overriden.  Also, this always sets the content type to JSON.
 */
eurekastreams.util.sendRequestToServer = function(
        url, method, opt_postParams, opt_callback, opt_excludeSecurityToken, opt_contentType) {
    // TODO: Should re-use the jsoncontainer code somehow
    opt_postParams = opt_postParams || {};

    var makeRequestParams = {
      "CONTENT_TYPE" : "JSON",
      "METHOD" : method,
      "POST_DATA" : opt_postParams};

    if (!opt_excludeSecurityToken) {
      url = socialDataPath + url + "?st=" + gadget.secureToken;
    }

    gadgets.io.makeNonProxiedRequest(url,
      function(data) {
        data = data.data;
        if (opt_callback) {
            opt_callback(data);
        }
      },
      makeRequestParams, 
      opt_contentType ? opt_contentType : "application/json"
    );
  };
  
  eurekastreams.util.gadgetIFrameUrlRefreshing = function(gadget) {
      document.getElementById(gadget.getIframeId()).src = "${build.web.baseurl}/style/images/wait-spinner.gif";
  };

  eurekastreams.util.refreshGadgetIFrameUrl = function(gadget, opt_viewParams) {
      document.getElementById('gadgetContent_' + gadget.getIframeId()).style.display = 'none';
      document.getElementById('gadgetContentLoading_' + gadget.getIframeId()).style.display = 'inline-block';
      gadget.viewParams = opt_viewParams;
      var iframeId = gadget.getIframeId();
      gadgets.rpc.setRelayUrl(iframeId, gadget.serverBase_ + gadget.rpcRelay);
      gadgets.rpc.setAuthToken(iframeId, gadget.rpcToken, false);
      document.getElementById(iframeId).src = gadget.getIframeUrl();
  };