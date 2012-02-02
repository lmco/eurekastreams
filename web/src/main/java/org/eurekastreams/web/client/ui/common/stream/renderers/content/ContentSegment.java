/*
 * Copyright (c) 2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.stream.renderers.content;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A piece of content (activity or comment body) to be displayed as a single HTML DOM element (text or link).
 */
public class ContentSegment extends JavaScriptObject
{
    /**
     * @return If this segment is a link.
     */
    public final native boolean isLink()
    /*-{ return this.type === $wnd.EurekaStreams.Client.TYPE_LINK; }-*/;

    /**
     * @return If this segment is text.
     */
    public final native boolean isText()
    /*-{ return this.type === $wnd.EurekaStreams.Client.TYPE_TEXT; }-*/;

    /**
     * @return If this segment is a tag.
     */
    public final native boolean isTag()
    /*-{ return this.type === $wnd.EurekaStreams.Client.TYPE_TAG; }-*/;

    /**
     * @return The text to display for the segment.
     */
    public final native String getContent()
    /*-{ return this.content; }-*/;

    /**
     * @return The URL for the link segment.
     */
    public final native String getUrl()
    /*-{ return this.url; }-*/;

    /**
     * @return The next segment. (The segments form a linked list.)
     */
    public final native ContentSegment getNext()
    /*-{ return this.next; }-*/;
}
