/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.dialog.optoutvideo;

import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.server.domain.TutorialVideoDTO;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.PreDialogHideEvent;
import org.eurekastreams.web.client.model.OptOutVideosModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.FlashWidget;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.common.form.elements.BasicCheckBoxFormElement;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Content for the optoutvideo dialog.
 */
public class OptOutableVideoDialogContent implements DialogContent
{
    /**
     * The number to use when setting width to offset for margins.
     */
    public static final Integer MARGIN_OFFSET = 60;

    /**
     * Used to bring the video up higher on the screen.
     */
    public static final Integer DIALOG_HEIGHT_OFFSET = 109;

    /**
     * Default value to use to adjust dialog box width.
     */
    public static final Integer DEFAULT_VIDEO_WIDTH = 450;
    /**
     * The width of the content segment.
     */
    public static final Integer CONTENT_WIDTH = 210;

    /**
     * DialogContent Title.
     */
    private String title = "";

    /**
     * The command to close the dialog.
     */
    private WidgetCommand closeCommand = null;

    /**
     * Content body.
     */
    private FlowPanel body = new FlowPanel();

    /**
     * "Don't show again" checkbox.
     */
    private BasicCheckBoxFormElement dontShowAgain;

    /**
     * Video details being displayed.
     */
    private TutorialVideoDTO tutorialVideo;

    /**
     * The flash content itself.
     */
    FlashWidget flashVideo;

    /**
     * @param inTutorialVideo
     *            the tutorialvideo to be displayed.
     */
    public OptOutableVideoDialogContent(final TutorialVideoDTO inTutorialVideo)
    {
        tutorialVideo = inTutorialVideo;
        title = inTutorialVideo.getDialogTitle();

        final HTML textContent = new HTML();
        FlowPanel breakdiv = new FlowPanel();

        breakdiv.addStyleName("break");
        FlowPanel textDiv = new FlowPanel();
        textDiv.add(textContent);
        dontShowAgain = new BasicCheckBoxFormElement(null, ((Long) tutorialVideo.getEntityId()).toString(),
                "Don't show this again", false, false);

        textContent.setHTML("<b>" + tutorialVideo.getInnerContentTitle() + "</b><br/>"
                + tutorialVideo.getInnerContent());
        textDiv.addStyleName("content_text");

        body.addStyleName("optoutable-video-modal");
        body.add(dontShowAgain);
        body.add(breakdiv);
        body.add(textDiv);

        if (inTutorialVideo.getVideoUrl() != null)
        {
            flashVideo = new FlashWidget();
            flashVideo.setFlashWidget(tutorialVideo.getVideoUrl(), ((Long) tutorialVideo.getEntityId()).toString(),
                    tutorialVideo.getVideoWidth(), tutorialVideo.getVideoHeight());
            flashVideo.addStyleName("content_video");
            body.add(flashVideo);
        }

        Session.getInstance().getEventBus().addObserver(PreDialogHideEvent.class, new Observer<PreDialogHideEvent>()
        {
            public void update(final PreDialogHideEvent event)
            {
                nativeStop(flashVideo.getVideoName());
            }
        });
    }

    /**
     * @return a Clicklistener to be set on dialog close.
     */
    public ClickListener closeDialog()
    {
        return new ClickListener()
        {
            public void onClick(final Widget inSender)
            {
                if ((Boolean) dontShowAgain.getValue())
                {
                    OptOutVideosModel.getInstance().insert(tutorialVideo.getEntityId());
                }
            }
        };
    }

    /**
     * what to do when you close the dialog.
     */
    public void close()
    {
        closeCommand.execute();
    }

    /**
     * Get the body of the DialogContent.
     * 
     * @return the body of the dialog.
     */
    public Widget getBody()
    {
        return body;
    }

    /**
     * Get the css of the DialogContent.
     * 
     * @return the css name to apply to the dialog.
     */
    public String getCssName()
    {
        return "videoDialog";
    }

    /**
     * Get the title of the DialogContent.
     * 
     * @return the title of the dialog box.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * set the close command.
     * 
     * @param command
     *            the command to set the close command to.
     */
    public void setCloseCommand(final WidgetCommand command)
    {
        closeCommand = command;
    }

    /**
     * what to do when you show the box.
     */
    public void show()
    {
        // Nothing special to do here.
    }

    /**
     * Stops the video.
     * 
     * @param movieName
     *            the name of the movie to stop.
     */
    private static native void nativeStop(final String movieName) /*-{
                                                                  var movie = $doc[movieName];
                                                                  if (movie != null)
                                                                  {
                                                                  movie.StopPlay();
                                                                  }
                                                                  }-*/;
}
