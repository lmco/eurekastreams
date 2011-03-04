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
package org.eurekastreams.web.client.ui.common.form.elements;

import java.io.Serializable;
import java.util.Date;

import org.eurekastreams.web.client.ui.common.LabeledTextBox;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * A Date Picker Form Element consisting of a month drop down and a year text box. 
 */
public class DateRangePickerFormElement extends FlowPanel implements FormElement
{
    /**
     * for converting date.getYear into yyyy format.
     */
    private static final int YEAR_CONVERSION = 1900;
    
    /**
     * The key to be used when the value is sent to the server.
     */
    private String key = "";
    
    /**
     * The current selected date.
     */
    private String value = "";
    
    /**
     * The label.
     */
    private Label label = new Label();
    
    /**
     * Puts a (required) on the form.
     */
    private Label requiredLabel  = new Label();
    
    /**
     * The label for the check box.
     */
    private Label checkBoxLabel  = new Label();
    
    /**
     * The present label.
     */
    private Label presentLabel = new Label("Present");
    
    /**
     * The to label.
     */
    private Label toLabel = new Label("to");
        
    /**
     * The instructions.
     */
    private Label instructions = new Label();
    
    /**
     * The starting month list box.
     */
    private ListBox startMonthDropDown = new ListBox();
    
    /**
     * The ending month list box.
     */
    private ListBox endMonthDropDown = new ListBox();
    
    /**
     * The start year text box.
     */
    private LabeledTextBox startYear = new LabeledTextBox("yyyy");
        
    /**
     * The end year text box.
     */
    private LabeledTextBox endYear = new LabeledTextBox("yyyy");
       
    /**
     * The check box.
     */
    private CheckBox checkBox = new CheckBox();
        
    /**
     * default constructor.
     * 
     * @param inLabel
     *            The label text.
     * @param inKey
     *            The value name of the element.
     * @param inValue
     *            The value of the element.
     * @param inCheckBoxLabel
     *            The label for the check box.
     * @param inInstructions
     *            The instructions of the element.
     * @param inRequired
     *            If the field is required.
     */
    public DateRangePickerFormElement(final String inLabel, final String inKey, final String inValue,
            final String inCheckBoxLabel, final String inInstructions, final boolean inRequired)
    {
        label.setText(inLabel);
        label.setStyleName(StaticResourceBundle.INSTANCE.coreCss().formLabel());
        
        checkBoxLabel.setText(inCheckBoxLabel);
        checkBoxLabel.setStyleName("check-box-label");
        
        presentLabel.setStyleName("form-text");
        toLabel.setStyleName("form-text");
        key = inKey;
        value = inValue;
        
        this.setStyleName("date-picker-form-element");
        
        setMonths(startMonthDropDown);
        setMonths(endMonthDropDown);
        
        startYear.setMaxLength(4);
        startYear.setVisibleLength(4);
        startYear.addStyleName(StaticResourceBundle.INSTANCE.coreCss().variable());
        
        endYear.setMaxLength(4);
        endYear.setVisibleLength(4);
        endYear.addStyleName(StaticResourceBundle.INSTANCE.coreCss().variable());
       
        if (inRequired)
        {
            requiredLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().requiredFormLabel());
            requiredLabel.setText("(required)");
        }
        
        presentLabel.setVisible(false);
        
        instructions.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formInstructions());
        instructions.setText(inInstructions);
        
        checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>()
                {
                    public void onValueChange(final ValueChangeEvent<Boolean> event)
                    {
                        checkDisplay();
                    }
                
                });
        
        this.add(label);
        this.add(requiredLabel);
        this.add(checkBox);
        this.add(checkBoxLabel);
        this.add(startMonthDropDown);
        this.add(startYear);
        this.add(toLabel);
        this.add(endMonthDropDown);
        this.add(endYear);
        this.add(presentLabel);
        this.add(instructions);
    }
    
    /**
     * Adds all of the months to the drop down.
     * 
     * @param dropDown
     *            The drop down to initialize.
     */
    private void setMonths(final ListBox dropDown)
    {
        dropDown.addItem("Select", "00");
        dropDown.addItem("January", "01");
        dropDown.addItem("February", "02");
        dropDown.addItem("March", "03");
        dropDown.addItem("April", "04");
        dropDown.addItem("May", "05");
        dropDown.addItem("June", "06");
        dropDown.addItem("July", "07");
        dropDown.addItem("August", "08");
        dropDown.addItem("September", "09");
        dropDown.addItem("October", "10");
        dropDown.addItem("November", "11");
        dropDown.addItem("December", "12");
    }
    
    /**
     * Sets the value of the date picker.
     * 
     * @param inStartDate
     *            The start date.
     * @param inEndDate
     *            The end date.
     */
    public void setValue(final Date inStartDate, final Date inEndDate)
    {
        startMonthDropDown.setSelectedIndex(inStartDate.getMonth() + 1);
        startYear.setText(Integer.toString(YEAR_CONVERSION + inStartDate.getYear()));
        
        if (inEndDate != null)
        {
            endMonthDropDown.setSelectedIndex(inEndDate.getMonth() + 1);
            endYear.setText(Integer.toString(YEAR_CONVERSION + inEndDate.getYear()));
        }
        else
        {
            checkBox.setValue(true);
            checkDisplay();
        }
        
        startYear.checkBox();
        endYear.checkBox();
    }
    
    /**
     * Get the key.
     * 
     * @return the key.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Get the value.
     * 
     * @return the value.
     */
    public Serializable getValue()
    {
        value = startMonthDropDown.getValue(startMonthDropDown.getSelectedIndex()) + "/" + startYear.getText();
        if (!checkBox.getValue())
        {
            value += ";" + endMonthDropDown.getValue(endMonthDropDown.getSelectedIndex()) + "/" + endYear.getText();
        }
        
        return value;
    }

    /**
     * Called on error.
     * 
     * @param errMessage
     *            the error.
     */
    public void onError(final String errMessage)
    {
        label.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formError());
    }

    /**
     * Called on success.
     */
    public void onSuccess()
    {
        label.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().formError());
    }
    
    /**
     * Checks to make sure the correct elements are visible and hidden.
     */
    private void checkDisplay()
    {
        if (checkBox.getValue())
        {
            endMonthDropDown.setVisible(false);
            endYear.setVisible(false);
            presentLabel.setVisible(true);                            
        }
        else
        {
            endMonthDropDown.setVisible(true);
            endYear.setVisible(true);
            presentLabel.setVisible(false);
        }
    }
}
