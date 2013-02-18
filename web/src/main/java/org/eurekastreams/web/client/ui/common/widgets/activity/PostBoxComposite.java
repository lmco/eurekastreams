/*
 * Copyright (c) 2009-2013 Lockheed Martin Corporation
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

import java.util.ArrayList;

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
import org.eurekastreams.web.client.events.data.GotAllPopularHashTagsResponseEvent;
import org.eurekastreams.web.client.events.data.GotSystemSettingsResponseEvent;
import org.eurekastreams.web.client.events.data.PostableStreamScopeChangeEvent;
import org.eurekastreams.web.client.model.ActivityModel;
import org.eurekastreams.web.client.model.AllPopularHashTagsModel;
import org.eurekastreams.web.client.model.SystemSettingsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.TimerFactory;
import org.eurekastreams.web.client.ui.TimerHandler;
import org.eurekastreams.web.client.ui.common.autocomplete.ExtendedTextArea;
import org.eurekastreams.server.domain.stream.LinkInformation;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.common.stream.attach.Attachment;
import org.eurekastreams.web.client.ui.common.stream.attach.bookmark.AddLinkComposite;
import org.eurekastreams.web.client.ui.common.stream.attach.bookmark.Bookmark;
import org.eurekastreams.web.client.ui.common.stream.decorators.ActivityDTOPopulator;
import org.eurekastreams.web.client.ui.common.stream.decorators.ActivityDTOPopulatorStrategy;
import org.eurekastreams.web.client.ui.common.stream.decorators.object.NotePopulator;
import org.eurekastreams.web.client.ui.common.stream.decorators.verb.PostPopulator;
import org.eurekastreams.web.client.ui.common.stream.renderers.AvatarRenderer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * Post box.
 */
public class PostBoxComposite extends Composite
{
    /** Max auto-complete hashtags to show. */
    private static final int MAX_HASH_TAG_AUTOCOMPLETE_ENTRIES = 10;

    /** Hide delay after blur on post box. */
    private static final Integer BLUR_DELAY = 500;

    /** Max chars for post. */
    private static final Integer POST_MAX = 250;

    /** Padding for hashtag dropdown. */
    private static final int HASH_TAG_DROP_DOWN_PADDING = 14;

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

        /**
         * Post char count over limit.
         * 
         * @return Post char count over limit.
         */
        String postCharCountOverLimit();

        /**
         * Post button inactive.
         * 
         * @return post button inactive.
         */
        String postButtonInactive();

        /**
         * Active hashtag style.
         * 
         * @return Active hashtag style.
         */
        String activeHashTag();
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
    PushButton postButton;

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
     * Hash Tags.
     */
    @UiField
    FlowPanel hashTags;

    /**
     * The content warning.
     */
    @UiField
    FlowPanel contentWarning;

    /**
     * The content warning container.
     */
    @UiField
    DivElement contentWarningContainer;

    /**
     * Currently active item.
     */
    private Integer activeItemIndex = null;

    /**
     * Timer factory.
     */
    private final TimerFactory timerFactory = new TimerFactory();

    /**
     * Current stream to post to.
     */
    private StreamScope currentStream = new StreamScope(ScopeType.PERSON, Session.getInstance().getCurrentPerson()
            .getAccountId());

    /**
     * Avatar Renderer.
     */
    private final AvatarRenderer avatarRenderer = new AvatarRenderer();

    /**
     * Activity Populator.
     */
    private final ActivityDTOPopulator activityPopulator = new ActivityDTOPopulator();

    /**
     * Attachment.
     */
    private Attachment attachment = null;

    /**
     * All hash tags.
     */
    private ArrayList<String> allHashTags;

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
        posterAvatar.add(avatarRenderer.render(Session.getInstance().getCurrentPerson().getEntityId(), Session
                .getInstance().getCurrentPerson().getAvatarId(), EntityType.PERSON, Size.Small));
        postCharCount.setInnerText(POST_MAX.toString());
        checkPostBox();
        postBox.setLabel("Post to your stream...");
        postBox.reset();

        addEvents();

        postBox.addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent event)
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
                        if (postBox.getText().trim().length() == 0 && !addLinkComposite.inAddMode()
                                && attachment == null)
                        {
                            postOptions.removeClassName(style.visiblePostBox());
                            postBox.getElement().getStyle().clearHeight();
                            postBox.reset();
                        }
                    }
                });
            }
        });

        setupPostButtonClickHandler();

        postBox.addKeyDownHandler(new KeyDownHandler()
        {
            public void onKeyDown(final KeyDownEvent event)
            {
                if ((event.getNativeKeyCode() == KeyCodes.KEY_TAB || event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
                        && !event.isAnyModifierKeyDown() && activeItemIndex != null)
                {
                    hashTags.getWidget(activeItemIndex).getElement()
                            .dispatchEvent(Document.get().createClickEvent(1, 0, 0, 0, 0, false, false, false, false));
                    event.preventDefault();
                    event.stopPropagation();
                    // clearSearch();

                }
                else if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN && activeItemIndex != null)
                {
                    if (activeItemIndex + 1 < hashTags.getWidgetCount())
                    {
                        selectItem((Label) hashTags.getWidget(activeItemIndex + 1));
                    }
                    event.preventDefault();
                    event.stopPropagation();
                }
                else if (event.getNativeKeyCode() == KeyCodes.KEY_UP && activeItemIndex != null)
                {
                    if (activeItemIndex - 1 >= 0)
                    {
                        selectItem((Label) hashTags.getWidget(activeItemIndex - 1));
                    }
                    event.preventDefault();
                    event.stopPropagation();
                }
                else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER && event.isControlKeyDown())
                {
                    postButton.getElement().dispatchEvent(
                            Document.get().createClickEvent(1, 0, 0, 0, 0, false, false, false, false));
                    event.preventDefault();
                    event.stopPropagation();
                }

            }
        });

        postBox.addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent event)
            {
                int tagsAdded = 0;

                String postText = postBox.getText();
                if (!postText.endsWith(" "))
                {
                    String[] words = postText.split("\\s");
                    if (words.length >= 1)
                    {
                        final String lastWord = words[words.length - 1];
                        if (lastWord.startsWith("#"))
                        {
                            boolean activeItemSet = false;
                            Label firstItem = null;

                            for (String hashTag : allHashTags)
                            {
                                if (hashTag.startsWith(lastWord))
                                {
                                    // get list ready on first tag added
                                    if (tagsAdded == 0)
                                    {
                                        hashTags.clear();

                                        String boxHeight = postBox.getElement().getStyle().getHeight()
                                                .replace("px", "");
                                        if (boxHeight.isEmpty())
                                        {
                                            boxHeight = "44";
                                        }
                                        hashTags.getElement()
                                                .getStyle()
                                                .setTop(Integer.parseInt(boxHeight) + HASH_TAG_DROP_DOWN_PADDING,
                                                        Unit.PX);
                                        hashTags.setVisible(true);
                                    }

                                    final String hashTagFinal = hashTag;
                                    final Label tagLbl = new Label(hashTagFinal);
                                    tagLbl.addClickHandler(new ClickHandler()
                                    {
                                        public void onClick(final ClickEvent event)
                                        {
                                            String postText = postBox.getText();
                                            postText = postText.substring(0, postText.length() - lastWord.length())
                                                    + hashTagFinal + " ";
                                            postBox.setText(postText);
                                            hashTags.setVisible(false);
                                            hashTags.clear();
                                            activeItemIndex = null;
                                        }
                                    });
                                    tagLbl.addMouseOverHandler(new MouseOverHandler()
                                    {
                                        public void onMouseOver(final MouseOverEvent arg0)
                                        {
                                            selectItem(tagLbl);
                                        }
                                    });
                                    hashTags.add(tagLbl);

                                    if (firstItem == null)
                                    {
                                        firstItem = tagLbl;
                                    }

                                    if (activeItemIndex != null
                                            && activeItemIndex.equals(hashTags.getWidgetCount() - 1))
                                    {
                                        activeItemIndex = null;
                                        activeItemSet = true;
                                        selectItem(tagLbl);
                                    }

                                    tagsAdded++;
                                    if (tagsAdded >= MAX_HASH_TAG_AUTOCOMPLETE_ENTRIES)
                                    {
                                        break;
                                    }
                                }
                            }

                            if (!activeItemSet && firstItem != null)
                            {
                                activeItemIndex = null;
                                selectItem(firstItem);
                            }
                        }
                    }
                }

                if (tagsAdded == 0)
                {
                    hashTags.setVisible(false);
                    activeItemIndex = null;
                }
            }
        });

        AllPopularHashTagsModel.getInstance().fetch(null, true);
        SystemSettingsModel.getInstance().fetch(null, true);

        hashTags.setVisible(false);
    }

    /**
     * Add events.
     */
    private void addEvents()
    {
        EventBus.getInstance().addObserver(MessageStreamAppendEvent.class, new Observer<MessageStreamAppendEvent>()
        {
            public void update(final MessageStreamAppendEvent event)
            {
                attachment = null;
                addLinkComposite.close();
                postBox.setText("");
                postBox.reset();
                postBox.getElement().getStyle().clearHeight();
                postOptions.removeClassName(style.visiblePostBox());
                checkPostBox();
            }
        });

        EventBus.getInstance().addObserver(PostableStreamScopeChangeEvent.class,
                new Observer<PostableStreamScopeChangeEvent>()
                {
                    public void update(final PostableStreamScopeChangeEvent stream)
                    {
                        currentStream = stream.getResponse();
                        if (currentStream != null && !"".equals(currentStream.getDisplayName()))
                        {
                            if (currentStream.getScopeType().equals(ScopeType.PERSON))
                            {
                                if (currentStream.getDisplayName().endsWith("s"))
                                {
                                    postBox.setLabel("Post to " + currentStream.getDisplayName() + "' stream...");
                                }
                                else
                                {
                                    postBox.setLabel("Post to " + currentStream.getDisplayName() + "'s stream...");
                                }
                            }
                            else
                            {
                                postBox.setLabel("Post to the " + currentStream.getDisplayName() + " stream...");
                            }
                        }
                        else
                        {
                            postBox.setLabel("Post to your stream...");
                        }

                        postPanel.setVisible(stream.getResponse().getScopeType() != null);
                    }
                });

        EventBus.getInstance().addObserver(MessageAttachmentChangedEvent.class,
                new Observer<MessageAttachmentChangedEvent>()
                {
                    public void update(final MessageAttachmentChangedEvent evt)
                    {
                        attachment = evt.getAttachment();
                        // When adding an attachment,
                        // make the "Post" Button visible.
                        checkPostBox();
                    }
                });

        EventBus.getInstance().addObserver(GotSystemSettingsResponseEvent.class,
                new Observer<GotSystemSettingsResponseEvent>()
                {
                    public void update(final GotSystemSettingsResponseEvent event)
                    {
                        String warning = event.getResponse().getContentWarningText();
                        if (warning != null && !warning.isEmpty())
                        {
                            contentWarning.getElement().setInnerHTML(warning);
                        }
                        else
                        {
                            contentWarning.setVisible(false);
                            contentWarningContainer.getStyle().setDisplay(Display.NONE);
                        }
                    }
                });

        Session.getInstance()
                .getEventBus()
                .addObserver(GotAllPopularHashTagsResponseEvent.class,
                        new Observer<GotAllPopularHashTagsResponseEvent>()
                        {
                            public void update(final GotAllPopularHashTagsResponseEvent event)
                            {
                                allHashTags = new ArrayList<String>(event.getResponse());
                            }
                        });

    }

    /**
     * Check the post box.
     */
    private void checkPostBox()
    {
        postCharCount.setInnerText(Integer.toString(POST_MAX - postBox.getText().length()));

        if (POST_MAX - postBox.getText().length() < 0)
        {
            postCharCount.addClassName(style.postCharCountOverLimit());
        }
        else
        {
            postCharCount.removeClassName(style.postCharCountOverLimit());
        }

        if ((postBox.getText().length() > 0 && POST_MAX - postBox.getText().length() >= 0) || attachment != null)
        {
            postButton.removeStyleName(style.postButtonInactive());
        }
        else
        {
            postButton.addStyleName(style.postButtonInactive());
        }
    }

    /**
     * Select an item.
     * 
     * @param item
     *            the item.
     */
    private void selectItem(final Label item)
    {
        if (activeItemIndex != null)
        {
            hashTags.getWidget(activeItemIndex).removeStyleName(style.activeHashTag());
        }
        item.addStyleName(style.activeHashTag());
        activeItemIndex = hashTags.getWidgetIndex(item);
    }

    /**
     * Setup the post button click handler - to prevent the checkstyle error of too-long method name.
     */
    private void setupPostButtonClickHandler()
    {
        postButton.addClickHandler(new ClickHandler()
        {
            /**
             * Handle the button click.
             * 
             * @param inEvent
             *            the click event
             */
            public void onClick(final ClickEvent inEvent)
            {
                if (!postButton.getStyleName().contains(style.postButtonInactive()))
                {
                    if (!isLinkAttached() && addLinkComposite.inAddMode() && doesUnattachedLinkTextHaveText())
                    {
                        // Assume that the user meant to attach the link,
                        // so attach the link to the message.

                        // Grab the link text from the unattached link textbox
                        LinkInformation linkInformation = new LinkInformation();
                        String linkText = addLinkComposite.getLinkText();
                        linkInformation.setUrl(linkText);
                        linkInformation.setTitle(linkText);

                        Bookmark bookmark = new Bookmark(linkInformation);
                        attachment = bookmark;
                    }

                    ActivityDTOPopulatorStrategy objectStrat = attachment != null ? attachment.getPopulator()
                            : new NotePopulator();

                    ActivityDTO activity = activityPopulator.getActivityDTO(postBox.getText(),
                            DomainConversionUtility.convertToEntityType(currentStream.getScopeType()),
                            currentStream.getUniqueKey(), new PostPopulator(), objectStrat);

                    PostActivityRequest postRequest = new PostActivityRequest(activity);

                    ActivityModel.getInstance().insert(postRequest);

                    postButton.addStyleName(style.postButtonInactive());
                }
            }
        });
    }

    /**
     * Checks whether there's text in an unattached link.
     * 
     * @return boolean true or false
     */
    private boolean doesUnattachedLinkTextHaveText()
    {
        String linkText = addLinkComposite.getLinkText();
        String basicLegitimateHttpPattern = "://";

        // Does the unattached link have "http://" and a period
        if (linkText.toLowerCase().contains(basicLegitimateHttpPattern) && linkText.contains("."))
        {
            return true;
        }
        return false;
    }

    /**
     * Returns whether a link is attached to a user's unsubmitted post.
     * 
     * @return boolean true or false
     */
    private boolean isLinkAttached()
    {
        return addLinkComposite.hasAttachment();
    }

}
