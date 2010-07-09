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
package org.eurekastreams.server.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.eurekastreams.commons.model.DomainEntity;
import org.hibernate.validator.Length;


/**
 *Tutorial video.
 * 
 */
@Entity
public class TutorialVideo extends DomainEntity implements Serializable
{    
    
    /**
     * Serialization id.
     */
    private static final long serialVersionUID = 7125126277510916401L;

    /**
     * Innercontent max length.
     */
    private static final int MAX_INNERCONTENT_LENGTH = 1000;
    
    /**
     * Video URL max length.
     */
    private static final int MAX_VIDEO_URL_LENGTH = 1000;
    
    /**
     * Innercontent message.
     */
    private static final String MAX_INNERCONTENT_MESSAGE = "Inner content has a maximum length of "
        + MAX_INNERCONTENT_LENGTH;
    
    /**
     * Video URL message.
     */
    private static final String MAX_VIDEO_URL_MESSAGE = "Video URL has a maximum length of " + MAX_VIDEO_URL_LENGTH;
        
    /**
     * The page this video is supposed to appear on.
     */
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    private Page page;
    
    /**
     * Dialog video title.
     */
    @Basic
    private String dialogTitle = "";

    /**
     * Video content title.
     */
    @Basic
    @Length(max = MAX_INNERCONTENT_LENGTH, message = MAX_INNERCONTENT_MESSAGE)
    private String innerContentTitle = "";
    
    /**
     * Video Content.
     */
    @Basic
    private String innerContent = "";
    
    /**
     * Video URL.
     */
    @Basic
    @Length(max = MAX_VIDEO_URL_LENGTH, message = MAX_VIDEO_URL_MESSAGE)
    private String videoUrl;
    
    /**
     * Video width.
     */
    @Basic
    private Integer videoWidth;

    /**
     * Video height.
     */
    @Basic
    private Integer videoHeight;

    /**
     * @return dialog title.
     */
    public String getDialogTitle()
    {
        return dialogTitle;
    }

    
    /**
     * @return the page this video belongs to.
     */
    public Page getPage()
    {
        return page;
    }


    /**
     * @param inPage the page to set for this video.
     */
    public void setPage(final Page inPage)
    {
        page = inPage;
    }


    /**
     * @param inDialogTitle
     *            set video dialog title.
     */
    public void setDialogTitle(final String inDialogTitle)
    {
        dialogTitle = inDialogTitle;
    }

    /**
     * @return video content title.
     */
    public String getInnerContentTitle()
    {
        return innerContentTitle;
    }

    /**
     * @param inInnerContentTitle
     *            set video content title.
     */
    public void setInnerContentTitle(final String inInnerContentTitle)
    {
        innerContentTitle = inInnerContentTitle;
    }

    /**
     * @return get video content.
     */
    public String getInnerContent()
    {
        return innerContent;
    }

    /**
     * @param inInnerContent
     *            set video content.
     */
    public void setInnerContent(final String inInnerContent)
    {
        innerContent = inInnerContent;
    }

    /**
     * @return video URL.
     */
    public String getVideoUrl()
    {
        return videoUrl;
    }

    /**
     * @param inVideoUrl
     *            set video URL.
     */
    public void setVideoUrl(final String inVideoUrl)
    {
        videoUrl = inVideoUrl;
    }

    /**
     * @return video width.
     */
    public Integer getVideoWidth()
    {
        return videoWidth;
    }

    /**
     * @param inVideoWidth
     *            set video width.
     */
    public void setVideoWidth(final Integer inVideoWidth)
    {
        videoWidth = inVideoWidth;
    }

    /**
     * @return get video height.
     */
    public Integer getVideoHeight()
    {
        return videoHeight;
    }

    /**
     * @param inVideoHeight
     *            set video height.
     */
    public void setVideoHeight(final Integer inVideoHeight)
    {
        videoHeight = inVideoHeight;
    }
    
    
}
