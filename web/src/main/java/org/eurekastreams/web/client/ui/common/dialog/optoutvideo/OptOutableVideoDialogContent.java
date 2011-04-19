/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import org.eurekastreams.server.domain.TutorialVideoDTO;
import org.eurekastreams.web.client.model.OptOutVideosModel;
import org.eurekastreams.web.client.ui.common.FlashWidget;
import org.eurekastreams.web.client.ui.common.dialog.BaseDialogContent;
import org.eurekastreams.web.client.ui.common.form.elements.BasicCheckBoxFormElement;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Content for the optoutvideo dialog.
 */
public class OptOutableVideoDialogContent extends BaseDialogContent
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
     * Content body.
     */
    private final FlowPanel body = new FlowPanel();

    /**
     * "Don't show again" checkbox.
     */
    private final BasicCheckBoxFormElement dontShowAgain;

    /**
     * Video details being displayed.
     */
    private final TutorialVideoDTO tutorialVideo;

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

        breakdiv.addStyleName(StaticResourceBundle.INSTANCE.coreCss().breakClass());
        FlowPanel textDiv = new FlowPanel();
        textDiv.add(textContent);
        dontShowAgain = new BasicCheckBoxFormElement(null, ((Long) tutorialVideo.getEntityId()).toString(),
                "Don't show this again", false, false);

        textContent.setHTML("<b>" + tutorialVideo.getInnerContentTitle() + "</b><br/>"
                + tutorialVideo.getInnerContent());
        textDiv.addStyleName(StaticResourceBundle.INSTANCE.coreCss().content_text());

        body.addStyleName(StaticResourceBundle.INSTANCE.coreCss().optoutableVideoModal());
        body.add(dontShowAgain);
        body.add(breakdiv);
        body.add(textDiv);

        if (inTutorialVideo.getVideoUrl() != null)
        {
            flashVideo = new FlashWidget();
            flashVideo.setFlashWidget(tutorialVideo.getVideoUrl(), ((Long) tutorialVideo.getEntityId()).toString(),
                    tutorialVideo.getVideoWidth(), tutorialVideo.getVideoHeight());
            flashVideo.addStyleName(StaticResourceBundle.INSTANCE.coreCss().content_video());
            body.add(flashVideo);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeHide()
    {
        nativeStop(flashVideo.getVideoName());
        if ((Boolean) dontShowAgain.getValue())
        {
            OptOutVideosModel.getInstance().insert(tutorialVideo.getEntityId());
        }
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
        return StaticResourceBundle.INSTANCE.coreCss().videoDialog();
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
     * Stops the video.
     *
     * @param movieName
     *            the name of the movie to stop.
     */
    private static native void nativeStop(final String movieName) /*-{
        var movie = $doc[movieName];
        if (movie != null)
        {
            try
            {
                movie.StopPlay();
            }
            catch (e) {}
        }
    }-*/;
}
