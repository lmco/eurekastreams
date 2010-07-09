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
import java.util.Map;

import org.eurekastreams.commons.search.modelview.ModelView;

/** 
 * A view of a Tutorial Video containing everything you need for display.
 */
public class TutorialVideoDTO extends ModelView implements Serializable
{

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 481355180064206422L;
    
    /**
     * The page this video is supposed to appear on.
     */
    private Page page;
    
    /**
     * Dialog video title.
     */
    private String dialogTitle = UNINITIALIZED_STRING_VALUE;

    /**
     * Video content title.
     */
    private String innerContentTitle = UNINITIALIZED_STRING_VALUE;
    
    /**
     * Video Content.
     */
    private String innerContent = UNINITIALIZED_STRING_VALUE;
    
    /**
     * Video URL.
     */
    private String videoUrl = UNINITIALIZED_STRING_VALUE;
    
    /**
     * Video width.
     */
    private Integer videoWidth = UNINITIALIZED_INTEGER_VALUE;

    /**
     * Video height.
     */
    private Integer videoHeight = UNINITIALIZED_INTEGER_VALUE;
    
    /**
     * Constructor.
     */
    public TutorialVideoDTO()
    { 
        //nothing to do here need for serialization.
    }
    
    /**
     * Load this object's properties from the input Map.
     * 
     * @param properties
     *            the Map of the properties to load
     */
    public void loadProperties(final Map<String, Object> properties)
    {
        super.loadProperties(properties);
        
        if (properties.containsKey("page"))
        {
            setPage((Page) properties.get("page"));
        }
        if (properties.containsKey("dialogTitle"))
        {
            setDialogTitle((String) properties.get("dialogTitle"));
        }
        if (properties.containsKey("innerContentTitle"))
        {
            setInnerContentTitle((String) properties.get("innerContentTitle"));
        }
        if (properties.containsKey("innerContent"))
        {
            setInnerContent((String) properties.get("innerContent"));
        }
        if (properties.containsKey("videoUrl"))
        {
            setVideoUrl((String) properties.get("videoUrl"));
        }
        if (properties.containsKey("videoWidth"))
        {
            setVideoWidth((Integer) properties.get("videoWidth"));
        }
        if (properties.containsKey("videoHeight"))
        {
            setVideoHeight((Integer) properties.get("videoHeight"));        
        }
    }

    @Override
    protected String getEntityName()
    {
        return "TutorialVideo";
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
     * @return dialog title.
     */
    public String getDialogTitle()
    {
        return dialogTitle;
    }

    /**
     * @param inDialogTitle
     *            set video dialog title.
     */
    public void setDialogTitle(final String inDialogTitle)
    {
        this.dialogTitle = inDialogTitle;
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
