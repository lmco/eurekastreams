/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * API to assist with management of the OAuth popup window.
 *
 * MAKE A COPY OF THIS FILE.  Do not hot link to it.
 *
 * Expected usage:
 *
 * 1) Gadget attempts to fetch OAuth data for the user and discovers that
 * approval is needed.  The gadget creates two new UI elements:
 *
 *   - a "personalize this gadget" button or link
 *   - a "personalization done" button or link, which is initially hidden.
 *
 * With any luck, the user will never need to click the "personalization done"
 * button, but it should be created and displayed in case we can't
 * automatically detect when the user has approved access to their gadget.
 *
 * 2) Gadget creates a popup object and associates event handlers with the UI
 * elements:
 *  
 *    var popup = shindig.oauth.popup({
 *        destination: response.oauthApprovalUrl,
 *        windowOptions: "height=300,width=200",
 *        onOpen: function() { 
 *          $("personalizeDone").style.display = "block"
 *        },
 *        onClose: function() {
 *          $("personalizeDone").style.display = "none"
 *          $("personalizeDone").style.display = "none"
 *          fetchData();
 *        }
 *    });
 *
 *    personalizeButton.onclick = popup.createOpenerOnClick();
 *    personalizeDoneButton.onclick = popup.createApprovedOnClick();
 *
 * 3) When the user clicks the personalization button/link, a window is opened
 *    to the approval URL.
 *
 * 4) When the window is closed, the oauth popup calls the onClose function
 *    and the gadget attempts to fetch the user's data.
 */

var shindig = shindig || {};
shindig.oauth = shindig.oauth || {};

/**
 * Initialize a new OAuth popup manager.  Parameters must be specified as
 * an object, e.g. shindig.oauth.popup({destination: somewhere,...});
 *
 * @param {String} destination Target URL for the popup window.
 * @param {String} windowOptions Options for window.open, used to specify
 *     look and feel of the window.
 * @param {function} onOpen Function to call when the window is opened.
 * @param {function} onClose Function to call when the window is closed.
 */
shindig.oauth.popup = function(options) {
  if (!("destination" in options)) {
    throw "Must specify options.destination";
  }
  if (!("windowOptions" in options)) {
    throw "Must specify options.windowOptions";
  }
  if (!("onOpen" in options)) {
    throw "Must specify options.onOpen";
  }
  if (!("onClose" in options)) {
    throw "Must specify options.onClose";
  }
  var destination = options.destination;
  var windowOptions = options.windowOptions;
  var onOpen = options.onOpen;
  var onClose = options.onClose;

  // created window
  var win = null;
  // setInterval timer
  var timer = null;

  // Called when we recieve an indication the user has approved access, either
  // because they closed the popup window or clicked an "I've approved" button.
  function handleApproval() {
    if (timer) {
      window.clearInterval(timer);
      timer = null;
    }
    if (win) {
      win.close();
      win = null;
    }
    onClose();
    return false;
  }

  // Called at intervals to check whether the window has closed.  If it has,
  // we act as if the user had clicked the "I've approved" link.
  function checkClosed() {
    if ((!win) || win.closed) {
      win = null;
      handleApproval();
    }
  }

  /**
   * @return an onclick handler for the "open the approval window" link
   */
  function createOpenerOnClick() {
    return function() {
      // If a popup blocker blocks the window, we do nothing.  The user will
      // need to approve the popup, then click again to open the window.
      // Note that because we don't call window.open until the user has clicked
      // something the popup blockers *should* let us through.
      win = window.open(destination, "_blank", windowOptions);
      if (win) {
        // Poll every 100ms to check if the window has been closed
        timer = window.setInterval(checkClosed, 100);
        onOpen();
      }
      return false;
    };
  }

  /**
   * @return an onclick handler for the "I've approved" link.  This may not
   * ever be called.  If we successfully detect that the window was closed,
   * this link is unnecessary.
   */
  function createApprovedOnClick() {
    return handleApproval;
  }

  return {
    createOpenerOnClick: createOpenerOnClick,
    createApprovedOnClick: createApprovedOnClick
  };
};

