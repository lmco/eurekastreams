package org.eurekastreams.web.client.ui.common.widgets.activity;

import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.DomainConversionUtility;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.web.client.events.EventBus;
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
import org.eurekastreams.web.client.ui.common.stream.decorators.ActivityDTOPopulator;
import org.eurekastreams.web.client.ui.common.stream.decorators.ActivityDTOPopulatorStrategy;
import org.eurekastreams.web.client.ui.common.stream.decorators.object.NotePopulator;
import org.eurekastreams.web.client.ui.common.stream.decorators.verb.PostPopulator;
import org.eurekastreams.web.client.ui.common.stream.renderers.AvatarRenderer;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PostBoxComposite extends Composite
{
    /** Binder for building UI. */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    /**
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, PostBoxComposite>
    {
    }

    interface PostBoxStyle extends CssResource
    {
        String visiblePostBox();
    }

    @UiField
    PostBoxStyle style;

    @UiField
    HTMLPanel posterAvatar;

    @UiField
    HTMLPanel postPanel;

    @UiField
    ExtendedTextArea postBox;

    @UiField
    DivElement postOptions;

    @UiField
    Label postButton;

    @UiField
    DivElement postCharCount;

    private static final Integer BLUR_DELAY = 100;

    private static final Integer POST_MAX = 250;

    private ExpandCollapseAnimation postBoxAnimation;

    private TimerFactory timerFactory = new TimerFactory();

    private StreamScope currentStream = new StreamScope(ScopeType.PERSON, Session.getInstance().getCurrentPerson()
            .getAccountId());

    /**
     * Avatar Renderer.
     */
    private AvatarRenderer avatarRenderer = new AvatarRenderer();

    /** Activity Populator. */
    private final ActivityDTOPopulator activityPopulator = new ActivityDTOPopulator();

    /**
     * Default constructor.
     */
    public PostBoxComposite()
    {
        initWidget(binder.createAndBindUi(this));
        buildPage();
    }

    private void buildPage()
    {
        postBoxAnimation = new ExpandCollapseAnimation(postBox.getElement(), 250, 100);
        posterAvatar.add(avatarRenderer.render(Session.getInstance().getCurrentPerson().getEntityId(), Session
                .getInstance().getCurrentPerson().getAvatarId(), EntityType.PERSON, Size.Small));
        postCharCount.setInnerText(POST_MAX.toString());

        EventBus.getInstance().addObserver(MessageStreamAppendEvent.class, new Observer<MessageStreamAppendEvent>()
        {
            public void update(MessageStreamAppendEvent event)
            {
                postBox.setText("");
                postBox.getElement().getStyle().clearHeight();
                postOptions.removeClassName(style.visiblePostBox());
            }
        });

        postBox.addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(KeyUpEvent event)
            {
                checkPostBox();
            }
        });

        postBox.addChangeHandler(new ChangeHandler()
        {

            public void onChange(ChangeEvent event)
            {
                checkPostBox();
            }
        });

        postBox.addFocusHandler(new FocusHandler()
        {
            public void onFocus(FocusEvent event)
            {
                postOptions.addClassName(style.visiblePostBox());
            }
        });

        postBox.addBlurHandler(new BlurHandler()
        {
            public void onBlur(BlurEvent event)
            {

                timerFactory.runTimer(BLUR_DELAY, new TimerHandler()
                {
                    public void run()
                    {
                        if (postBox.getText().length() == 0)
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
                    public void update(PostableStreamScopeChangeEvent stream)
                    {
                        currentStream = stream.getResponse();
                        postPanel.setVisible(stream.getResponse().getScopeType() != null);
                    }
                });

        postButton.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                Attachment attachment = null;
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

    protected void checkPostBox()
    {
        if (postBox.getElement().getClientHeight() != postBox.getElement().getScrollHeight())
        {
            postBoxAnimation.expand(postBox.getElement().getScrollHeight());
        }

        postCharCount.setInnerText(Integer.toString(POST_MAX - postBox.getText().length()));
    }

}
