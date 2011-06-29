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
package org.eurekastreams.web.client.ui.common.widgets.activity;

import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.DomainConversionUtility;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MessageAttachmentChangedEvent;
import org.eurekastreams.web.client.events.MessageStreamAppendEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.PostableStreamScopeChangeEvent;
import org.eurekastreams.web.client.model.ActivityModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.TimerFactory;
import org.eurekastreams.web.client.ui.TimerHandler;
import org.eurekastreams.web.client.ui.common.animation.ExpandCollapseAnimation;
import org.eurekastreams.web.client.ui.common.autocomplete.ExtendedTextArea;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.common.stream.attach.Attachment;
import org.eurekastreams.web.client.ui.common.stream.attach.bookmark.AddLinkComposite;
import org.eurekastreams.web.client.ui.common.stream.decorators.ActivityDTOPopulator;
import org.eurekastreams.web.client.ui.common.stream.decorators.ActivityDTOPopulatorStrategy;
import org.eurekastreams.web.client.ui.common.stream.decorators.object.NotePopulator;
import org.eurekastreams.web.client.ui.common.stream.decorators.verb.PostPopulator;
import org.eurekastreams.web.client.ui.common.stream.renderers.AvatarRenderer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Post box.
 */
public class PostBoxComposite extends Composite
{
    /**
     * Binder for building UI.
     */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    /**
     * 
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, PostBoxComposite>
    {
    }

    /**
     * Post box CssResource style.
     */
    interface PostBoxStyle extends CssResource
    {
        /**
         * Visible post box style.
         * 
         * @return Visible post box style.
         */
        String visiblePostBox();
    }

    /**
     * Post box CssResource style.
     */
    @UiField
    PostBoxStyle style;

    /**
     * UI element for poster avatar.
     */
    @UiField
    HTMLPanel posterAvatar;

    /**
     * UI element for post panel.
     */
    @UiField
    HTMLPanel postPanel;

    /**
     * UI element for post box.
     */
    @UiField
    ExtendedTextArea postBox;

    /**
     * UI element for post options.
     */
    @UiField
    DivElement postOptions;

    /**
     * UI element for post button.
     */
    @UiField
    Label postButton;

    /**
     * UI element for post char count.
     */
    @UiField
    DivElement postCharCount;

    /**
     * UI element for add link composite.
     */
    @UiField
    AddLinkComposite addLinkComposite;

    /**
     * Hide delay after blur on post box.
     */
    private static final Integer BLUR_DELAY = 500;

    /**
     * Max chars for post.
     */
    private static final Integer POST_MAX = 250;

    /**
     * Post box default height.
     */
    private static final int POST_BOX_DEFAULT_HEIGHT = 250;

    /**
     * Post box expand animation duration.
     */
    private static final int POST_BOX_EXPAND_ANIMATION_DURATION = 100;

    /**
     * Post box animation.
     */
    private ExpandCollapseAnimation postBoxAnimation;

    /**
     * Timer factory.
     */
    private TimerFactory timerFactory = new TimerFactory();

    /**
     * Current stream to post to.
     */
    private StreamScope currentStream = new StreamScope(ScopeType.PERSON, Session.getInstance().getCurrentPerson()
            .getAccountId());

    /**
     * Avatar Renderer.
     */
    private AvatarRenderer avatarRenderer = new AvatarRenderer();

    /**
     * Activity Populator.
     */
    private final ActivityDTOPopulator activityPopulator = new ActivityDTOPopulator();

    /**
     * Attachment.
     */
    private Attachment attachment = null;

    /**
     * Default constructor.
     */
    public PostBoxComposite()
    {
        initWidget(binder.createAndBindUi(this));
        buildPage();
    }

    /**
     * Build page.
     */
    private void buildPage()
    {
        postBoxAnimation = new ExpandCollapseAnimation(postBox.getElement(), POST_BOX_EXPAND_ANIMATION_DURATION);
        posterAvatar.add(avatarRenderer.render(Session.getInstance().getCurrentPerson().getEntityId(), Session
                .getInstance().getCurrentPerson().getAvatarId(), EntityType.PERSON, Size.Small));
        postCharCount.setInnerText(POST_MAX.toString());

        EventBus.getInstance().addObserver(MessageStreamAppendEvent.class, new Observer<MessageStreamAppendEvent>()
        {
            public void update(final MessageStreamAppendEvent event)
            {
                postBox.setText("");
                postBox.getElement().getStyle().clearHeight();
                postOptions.removeClassName(style.visiblePostBox());
            }
        });

        postBox.addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent event)
            {
                checkPostBox();
            }
        });

        postBox.addChangeHandler(new ChangeHandler()
        {
            public void onChange(final ChangeEvent event)
            {
                checkPostBox();
            }
        });

        postBox.addFocusHandler(new FocusHandler()
        {
            public void onFocus(final FocusEvent event)
            {
                postOptions.addClassName(style.visiblePostBox());
            }
        });

        postBox.addBlurHandler(new BlurHandler()
        {
            public void onBlur(final BlurEvent event)
            {

                timerFactory.runTimer(BLUR_DELAY, new TimerHandler()
                {
                    public void run()
                    {
                        if (postBox.getText().length() == 0 && !addLinkComposite.inAddMode())
                        {
                            postOptions.removeClassName(style.visiblePostBox());
                            postBox.getElement().getStyle().clearHeight();
                        }
                    }
                });
            }
        });

        EventBus.getInstance().addObserver(PostableStreamScopeChangeEvent.class,
                new Observer<PostableStreamScopeChangeEvent>()
                {
                    public void update(final PostableStreamScopeChangeEvent stream)
                    {
                        currentStream = stream.getResponse();
                        postPanel.setVisible(stream.getResponse().getScopeType() != null);
                    }
                });

        EventBus.getInstance().addObserver(MessageAttachmentChangedEvent.class,
                new Observer<MessageAttachmentChangedEvent>()
                {
                    public void update(final MessageAttachmentChangedEvent evt)
                    {
                        attachment = evt.getAttachment();
                    }
                });

        postButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                ActivityDTOPopulatorStrategy objectStrat = attachment != null ? attachment.getPopulator()
                        : new NotePopulator();

                ActivityDTO activity = activityPopulator.getActivityDTO(postBox.getText(), DomainConversionUtility
                        .convertToEntityType(currentStream.getScopeType()), currentStream.getUniqueKey(),
                        new PostPopulator(), objectStrat);
                PostActivityRequest postRequest = new PostActivityRequest(activity);

                ActivityModel.getInstance().insert(postRequest);
            }
        });
    }

    /**
     * Check the post box.
     */
    protected void checkPostBox()
    {
        if (postBox.getElement().getClientHeight() != postBox.getElement().getScrollHeight())
        {
            postBoxAnimation.expand(postBox.getElement().getScrollHeight());
        }

        postCharCount.setInnerText(Integer.toString(POST_MAX - postBox.getText().length()));
    }

}
