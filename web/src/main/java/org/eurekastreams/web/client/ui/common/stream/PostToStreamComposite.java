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
package org.eurekastreams.web.client.ui.common.stream;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PostReadyEvent;
import org.eurekastreams.web.client.events.data.GotSystemSettingsResponseEvent;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.SystemSettingsModel;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.PropertyMapper;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.stream.attach.bookmark.AddLinkComposite;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Post a message to the stream.
 */
public class PostToStreamComposite extends FlowPanel implements Bindable
{
    /**
     * The number of characters remaining.
     */
    Label charsRemaining;

    /**
     * The post button.
     */
    Label postButton;

    /**
     * The message.
     */
    TextArea message;

    /**
     * The list of links in the message.
     */
    AddLinkComposite links;

    /**
     * The refresh interval in milliseconds.
     */
    private static final int REFRESH_INTERVAL = 250;

    /**
     * The model.
     */
    private PostToStreamModel model;

    /**
     * The post to panel.
     */
    PostToPanel postToPanel;

    /**
     * Container for content warning.
     */
    FlowPanel contentWarningContainer = new FlowPanel();

    /**
     * The content warning.
     */
    Label contentWarning;

    /**
     * error label.
     */
    Label errorMsg = new Label();

    /**
     * Constructor.
     *
     * @param inProcessor
     *            the action processor.
     * @param inScope
     *            the scope.
     */
    @SuppressWarnings("deprecation")
    public PostToStreamComposite(final ActionProcessor inProcessor, final StreamScope inScope)
    {
        final PostToStreamComposite thisBuffered = this;
        this.getElement().setAttribute("id", "post-to-stream");

        charsRemaining = new Label();
        postButton = new Label("Post");
        message = new TextArea();
        message.setText("Something to share?");
        message.setVisible(false); // Hide until post ready event.

        this.addStyleName("post-to-stream");
        errorMsg.addStyleName("form-error-box");
        errorMsg.setVisible(false);
        this.add(errorMsg);

        FlowPanel postInfoContainer = new FlowPanel();
        postInfoContainer.addStyleName("post-info-container");

        postButton.addStyleName("post-button");
        postInfoContainer.add(postButton);

        charsRemaining.addStyleName("characters-remaining");
        postInfoContainer.add(charsRemaining);

        Panel entryPanel = new FlowPanel();
        entryPanel.addStyleName("post-entry-panel");
        entryPanel.add(message);
        entryPanel.add(postInfoContainer);
        add(entryPanel);

        // below text area: links and post to on one line, then content warning below
        Panel expandedPanel = new FlowPanel();
        expandedPanel.addStyleName("post-expanded-panel");

        postToPanel = new PostToPanel(inScope);
        expandedPanel.add(postToPanel);
        links = new AddLinkComposite(inProcessor);
        expandedPanel.add(links);

        contentWarning = new Label();
        contentWarningContainer.addStyleName("content-warning");
        contentWarning.addStyleName("content-warning-text");
        contentWarningContainer.add(contentWarning);
        expandedPanel.add(contentWarningContainer);

        add(expandedPanel);

        model = new PostToStreamModel(EventBus.getInstance(), inProcessor, postToPanel);
        PostToStreamView view = new PostToStreamView(model);
        final PostToStreamController controller = new PostToStreamController(EventBus.getInstance(), view, model, this);

        PropertyMapper mapper = new PropertyMapper(GWT.create(PostToStreamComposite.class), GWT
                .create(PostToStreamView.class));

        mapper.bind(this, view);

        controller.init();

        setVisible(false);

        WidgetJSNIFacadeImpl jsni = new WidgetJSNIFacadeImpl();

        setVisible(Session.getInstance().getCurrentPerson() != null);

        Timer timer = new Timer()
        {

            /**
             * Poll for links.
             */
            @Override
            public void run()
            {
                controller.checkForLinks();
            }
        };

        /**
         * This needs to be done because onChange in a text area in Firefox is only called onBlur.
         */
        timer.scheduleRepeating(REFRESH_INTERVAL);
        this.addStyleName("small");

        Session.getInstance().getEventBus().addObserver(MessageStreamAppendEvent.class,
                new Observer<MessageStreamAppendEvent>()
                {
                    public void update(final MessageStreamAppendEvent event)
                    {
                        thisBuffered.addStyleName("small");
                        message.setText("Something to share?");
                    }

                });


        Session.getInstance().getEventBus().addObserver(GotSystemSettingsResponseEvent.class,
                new Observer<GotSystemSettingsResponseEvent>()
                {
                    public void update(final GotSystemSettingsResponseEvent event)
                    {
                        String warning = event.getResponse().getContentWarningText();
                        PostReadyEvent postEvent = new PostReadyEvent(warning);
                        Session.getInstance().getEventBus().notifyObservers(postEvent);
                    }

                });



        SystemSettingsModel.getInstance().fetch(null, true);

    }

    /**
     * Sets up the magic show/hide for the publisher.
     */
    public static native void setUpMinimizer() /*-{

                                      $wnd.overPoster = false;
                                      $doc.onmousedown = function() {
                                          if(!$wnd.overPoster && $wnd.jQuery(".post-button").is(".inactive"))
                                          {
                                              setTimeout("$wnd.jQuery('#post-to-stream').addClass('small');",500);
                         setTimeout("$wnd.jQuery('#post-to-stream textarea').val('Something to share?');",500);
                                          }
                                          else if($wnd.overPoster && $wnd.jQuery("#post-to-stream").is(".small"))
                                          {
                                              $wnd.jQuery('#post-to-stream textarea').focus();
                                          }

                                      }

                                      $wnd.jQuery("#post-to-stream textarea").focus(
                                          function() {
                                              if($wnd.jQuery("#post-to-stream").is(".small")) {
                                                  $wnd.jQuery("#post-to-stream").removeClass("small");
                                                  $wnd.jQuery("#post-to-stream textarea").val("");
                                              }
                                          });

                                      $wnd.jQuery("#post-to-stream").hover(
                                          function() { $wnd.overPoster = true; },
                                          function() { $wnd.overPoster = false; });

                                     }-*/;

    /**
     * Set the scope.
     *
     * @param inScope
     *            the scope.
     */
    public void setScope(final StreamScope inScope)
    {
        postToPanel.setPostScope(inScope);
    }

}
