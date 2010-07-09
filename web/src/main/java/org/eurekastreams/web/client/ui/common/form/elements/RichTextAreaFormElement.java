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
package org.eurekastreams.web.client.ui.common.form.elements;

import java.io.Serializable;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;

/**
 * RichText Text Area.
 *
 */
public class RichTextAreaFormElement extends FlowPanel implements FormElement
{
    /**
     * The default width of the form element.
     */
    private static final int DEFAULT_WIDTH = 400;

    /**
     * The delay in ms before the reinitialize commands run.
     */
    private static final int REINITIALIZE_DELAY = 10;

    /**
     * The text box.
     */
    private TextArea textArea = new TextArea();

    /**
     * The label.
     */
    private Label label = new Label();
    /**
     * Puts a (required) on the form.
     */
    private Label requiredLabel = new Label();
    /**
     * instructions for the element.
     */
    private Label instructions = new Label();
    /**
     * The key that this corresponds to in the model.
     */
    private String key = "";

    /**
     * The text value in the text box.
     */
    private String textValue = "";

    /**
     * Creates a RichText TextArea Form element.
     *
     * @param labelVal
     *            The label test.
     * @param inKey
     *            The value name of the element.
     * @param value
     *            The value of the element.
     * @param inInstructions
     *            The instructions for the element.
     * @param required
     *            If the field is required.
     */
    public RichTextAreaFormElement(final String labelVal, final String inKey, final String value,
            final String inInstructions, final boolean required)
    {
        this(labelVal, inKey, value, inInstructions, DEFAULT_WIDTH, required);
    }

    /**
     * Creates a RichText TextArea Form element.
     *
     * @param labelVal
     *            The label test.
     * @param inKey
     *            The value name of the element.
     * @param value
     *            The value of the element.
     * @param inInstructions
     *            The instructions for the element.
     * @param width
     *            The width in pixels of the form element.
     * @param required
     *            If the field is required.
     */
    public RichTextAreaFormElement(final String labelVal, final String inKey, final String value,
            final String inInstructions, final int width, final boolean required)
    {
        key = inKey;
        label.setText(labelVal);
        label.addStyleName("form-label");
        textArea.setText(value);
        // Add random int to ID to prevent bug where text areas are rendered twice
        // when user leaves page and then comes back without refreshing.
        textArea.getElement().setId(inKey + Random.nextInt());

        this.addStyleName("yui-skin-sam");

        if (required)
        {
            requiredLabel.addStyleName("required-form-label");
            requiredLabel.setText("(required)");
        }

        this.add(label);
        this.add(requiredLabel);
        this.add(textArea);

        if (inInstructions != null && !inInstructions.isEmpty())
        {
            instructions.addStyleName("form-instructions");
            instructions.setText(inInstructions);
            this.add(instructions);
        }

        setUpRTE(textArea.getElement().getId(), width + "px");
    }

    /**
     * Sets the value the text editor should have when it comes back from being hidden.
     */
    @Override
    protected void onDetach()
    {
        textValue = getValue().toString();
        super.onDetach();
    }

    /**
     * Reinitializes the text editor with the value it should have, and calls Show to fix bugs from hiding it.
     */
    @Override
    protected void onAttach()
    {
        DeferredCommand.addCommand(new Command()
        {
            public void execute()
            {
                // There is a race condition here between the time the text area breaks and
                // the time that the deferred command is ran. Adding this timer helps to make sure
                // that this occurs last.
                Timer delay = new Timer()
                {
                    public void run()
                    {
                        if (textValue != "")
                        {
                            setEditorHtml(textArea.getElement().getId(), textValue);
                            showEditor(textArea.getElement().getId());
                        }
                    }
                };

                // Delay for 10 ms.
                delay.schedule(REINITIALIZE_DELAY);
            }
        });

        super.onAttach();
    }

    /**
     * Clear the editor.
     *
     * Note: Clearing the editor still leaves a space in the editor. YUI does this because to set the cursor something
     * has to be in the element.
     */
    public void clearEditor()
    {
        clearEditorNative(textArea.getElement().getId());
    }

    /**
     * get key.
     *
     * @return key.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * get value.
     *
     * @return value.
     */
    public Serializable getValue()
    {
        if (textArea.isAttached())
        {
            saveEditorHtml(textArea.getElement().getId());
        }
        return textArea.getValue();
    }

    /**
     * Gets called if this element has an error.
     *
     * @param errMessage
     *            the error Message.
     */
    public void onError(final String errMessage)
    {
        label.addStyleName("form-error");
    }

    /**
     * Gets called if this element was successful.
     */
    public void onSuccess()
    {
        label.removeStyleName("form-error");
    }

    /**
     * @return if the richtext area is empty.
     */
    public boolean isEmpty()
    {
        return stripHTML((String) this.getValue()).trim().isEmpty();
    }

    /**
     * When run it sets up the RTA.
     *
     * @param targetTextAreaId
     *            The ID of the element you want to setup.
     * @param controlWidth
     *            The width of the element in pixels.
     */
    private native void setUpRTE(final String targetTextAreaId, final String controlWidth)
    /*-{
             var editorConfig={
                height: '130px',
                width: controlWidth,
                animate: true,
                dompath: false,
                extracss: 'body {background-color:white;font-family: "Lucida Sans Unicode", "Lucida Sans",'
                    + '"Verdana", "Tahoma", "Geneva", "Kalimati", sans-serif ! important;font-size:14px}',
                toolbar: {
                    grouplabels: false,
                    buttons: [
                        { group: 'textstyle', label: '',
                            buttons: [
                            { type: 'push', label: 'Bold', value: 'bold' },
                            { type: 'push', label: 'Italic', value: 'italic' },
                            { type: 'push', label: 'Underline', value: 'underline' },
                            { type: 'separator' },
                            { type: 'push', label: 'Create an Unordered List', value:'insertunorderedlist'},
                            { type: 'push', label: 'Create an Ordered List', value:'insertorderedlist'},
                            { type: 'separator' },
                            { type: 'push', label: 'HTML Link CTRL + SHIFT +L', value:'createlink'}
                            ]
                        }
                    ]
                }
            };

            var editorKey = 'editor_' + targetTextAreaId;
            if (!$wnd[editorKey]) {
                var editor = new $wnd.YAHOO.widget.SimpleEditor(targetTextAreaId, editorConfig);
                $wnd[editorKey] = editor;

                $wnd['saveEditor_' + targetTextAreaId] = function() {
                    editor.saveHTML();
                }

                $wnd['clearEditor_' + targetTextAreaId] = function() {
                    editor.clearEditorDoc();
                }

                $wnd['showEditor_' + targetTextAreaId] = function() {
                    editor.show();
                }

                $wnd['setEditor_' + targetTextAreaId] = function(value) {
                    editor.setEditorHTML(value);
                }
            }

            $wnd[editorKey].render();
    }-*/;

    /**
     * Save the editor HTML.
     *
     * @param targetTextAreaId
     *            the text area id.
     */
    private native void saveEditorHtml(final String targetTextAreaId)
    /*-{
         $wnd['saveEditor_' + targetTextAreaId]();
    }-*/;

    /**
     * Clear the editor HTML.
     *
     * @param targetTextAreaId
     *            the text area id.
     */
    private native void clearEditorNative(final String targetTextAreaId)
    /*-{
         $wnd['clearEditor_' + targetTextAreaId]();
    }-*/;

    /**
     * Show the editor (fixes bugs when the editor is hidden).
     *
     * @param targetTextAreaId
     *            the text area id.
     */
    private native void showEditor(final String targetTextAreaId)
    /*-{
        $wnd['showEditor_' + targetTextAreaId]();
    }-*/;

    /**
     * Sets the editor's text value.
     *
     * @param targetTextAreaId
     *            the text area id.
     * @param value
     *            the text area value.
     */
    private native void setEditorHtml(final String targetTextAreaId, final String value)
    /*-{
        $wnd['setEditor_' + targetTextAreaId](value);
    }-*/;

    /**
     * strips html out of String.
     *
     * @param htmlString
     *            the string to strip.
     * @return a stripped string.
     */
    private String stripHTML(final String htmlString)
    {
        String stripHTML =
                "<\\/?\\w+((\\s+(\\w|\\w[\\w-]*\\w)"
                        + "(\\s*=\\s*(?:\\\".*?\\\"|\'.*?\'|[^\'\\\">\\s]+))?)+\\s*|\\s*)\\/?>";

        String nohtml = htmlString.replaceAll(stripHTML, "");

        nohtml = nohtml.replaceAll("&nbsp;", "");

        return nohtml;
    }
}
