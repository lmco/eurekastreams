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
package org.eurekastreams.web.client.ui.pages.start.dragging;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * Gadget drag controller.
 *
 */
public class GadgetDragController extends PickupDragController
{

    /**
     * Default constructor.
     *
     * @param boundaryPanel
     *            boundary panel.
     * @param allowDroppingOnBoundaryPanel
     *            allow dropping.
     */
    public GadgetDragController(final AbsolutePanel boundaryPanel, final boolean allowDroppingOnBoundaryPanel)
    {
        super(boundaryPanel, allowDroppingOnBoundaryPanel);
    }

    /**
     * Override for drag start.
     */
    @Override
    public void dragStart()
    {
        super.dragStart();
        setDragProxyLocation();
    }

    /**
     * JSNI used to position the drag drop proxy underneath your mouse.
     */
    private native void setDragProxyLocation()
    /*-{
        $wnd.jQuery(".dragdrop-proxy").css("display","none");
        //var leftGutter = ($wnd.screen.width - 990) / 2;
        var leftGutter = 0;
        var winLeft = $wnd.jQuery(".dragdrop-proxy").parent().css('left');
        var winLeft = winLeft.substring(0,winLeft.length-2);
        var shiftLeft = $wnd.mouseXpos - winLeft - leftGutter - 40;

        if(shiftLeft < 0)
        {
        	shiftLeft = 0;
        }
        $wnd.jQuery(".dragdrop-proxy").css("paddingLeft",shiftLeft+"px");
        $wnd.jQuery(".dragdrop-proxy").css("paddingTop","30px");
    	$wnd.jQuery(".dragdrop-proxy").get(0).style.background =
    	    'transparent url(/style/images/gadget-dragged.png) no-repeat ' +
    	    shiftLeft + 'px 0px';
    	$wnd.jQuery(".dragdrop-proxy").html(
    	"<span class='dragdrop-proxy-content'>" +
    		$wnd.jQuery(".dragdrop-dragging .gadget-zone-chrome-title-bar .title-label").text() + "</span>");

        setTimeout("$wnd.jQuery('.dragdrop-proxy').css('display','inline-block');", 100);


    }-*/;
}
