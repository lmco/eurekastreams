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
package org.eurekastreams.web.client.ui.pages.profile.settings;

import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.server.domain.Job;
import org.eurekastreams.web.client.events.BackgroundEmploymentAddCanceledEvent;
import org.eurekastreams.web.client.events.BackgroundEmploymentEditCanceledEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.PersonalEmploymentModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.autocomplete.AutoCompleteItemDropDownFormElement;
import org.eurekastreams.web.client.ui.common.form.FormBuilder;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.form.elements.BasicDropDownFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextAreaFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.DateRangePickerFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.ValueOnlyFormElement;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Displays the form for adding or updating a job.
 */
public class CreateOrUpdateEmploymentPanel extends FlowPanel
{
    /**
     * The page history token to direct to upon saving or canceling.
     */
    private String pageHistoryToken;
    
    /**
     * Max length for autocomplete fields.
     */
    private static final int MAX_LENGTH = 50;
    
    /**
     * Maximum description length.
     */
    private static final int MAX_DESCRIPTION = 200;

    /**
     * Default constructor.
     * 
     * @param employment
     *            The employment.
     */
    public CreateOrUpdateEmploymentPanel(final Job employment)
    {
        pageHistoryToken = Session.getInstance().generateUrl(new CreateUrlRequest());
        createForm(employment);
    }
    
    /**
     * Default constructor.
     * 
     * @param employment
     *            The employment.
     * @param inPageHistoryToken
     *            The page history token.
     */
    public CreateOrUpdateEmploymentPanel(final Job employment, final String inPageHistoryToken)
    {
        pageHistoryToken = inPageHistoryToken;
        createForm(employment);
    }
    
    /**
     * Clears the form.
     */
    public void clearData()
    {
        createForm(null);
    }
    
    /**
     * Recreates the form.
     * 
     * @param employment
     *            The employment.
     */
    private void createForm(final Job employment)
    {
        this.clear();
        
        FormBuilder form;
        
        AutoCompleteItemDropDownFormElement companyName = new AutoCompleteItemDropDownFormElement("Company Name",
                "companyName", "", "", true, "/resources/autocomplete/companies/", "companies", "");
        companyName.setMaxLength(MAX_LENGTH);
        
        companyName.setOnItemSelectedCommand(new AutoCompleteItemDropDownFormElement.OnItemSelectedCommand()
        {
            public void itemSelected(final JavaScriptObject obj)
            {
                
            }
        });
        
        BasicDropDownFormElement industry = new BasicDropDownFormElement(
                "Industry", "industry", getIndustryValues(), "", "", true);
        
        AutoCompleteItemDropDownFormElement title = new AutoCompleteItemDropDownFormElement("Title",
                StaticResourceBundle.INSTANCE.coreCss().title(), "", "", true, "/resources/autocomplete/titles/", "titles", "");
        title.setMaxLength(MAX_LENGTH);
        
        title.setOnItemSelectedCommand(new AutoCompleteItemDropDownFormElement.OnItemSelectedCommand()
        {
            public void itemSelected(final JavaScriptObject obj)
            {
                
            }
        });
                
        DateRangePickerFormElement timePeriod = new DateRangePickerFormElement("Time Period", "dates", "", 
                "I currently work here", "", true);
        
        BasicTextAreaFormElement description = new BasicTextAreaFormElement(MAX_DESCRIPTION, "Description",
                StaticResourceBundle.INSTANCE.coreCss().description(), "", "Add some details of the position, so users viewing your profile can get a quick "
                + "idea what the position involves.", false);
        
        if (employment == null)
        {
            form = new FormBuilder("Add Position", PersonalEmploymentModel.getInstance(), Method.INSERT);
            industry = new BasicDropDownFormElement("Industry", "industry", getIndustryValues(), "", "", true);
        }        
        else
        {
            form = new FormBuilder("Edit Position", PersonalEmploymentModel.getInstance(), Method.UPDATE);
            form.addStyleName(StaticResourceBundle.INSTANCE.coreCss().editPosition());

            form.addFormElement(new ValueOnlyFormElement("id", employment.getId()));
            companyName.setValue(employment.getCompanyName());
            industry = new BasicDropDownFormElement(
                    "Industry", "industry", getIndustryValues(), employment.getIndustry(), "", true);
            title.setValue(employment.getTitle());
            timePeriod.setValue(employment.getDateFrom(), employment.getDateTo());
            description.setValue(employment.getDescription());
        }
               
        form.addFormElement(companyName);        
        form.addFormElement(industry);              
        form.addFormElement(title);        
        form.addFormElement(timePeriod);
        form.addFormElement(description);
        form.addFormDivider();
        
        form.addOnCancelCommand(new Command()
        {
            public void execute()
            {                
                if (employment == null)
                {
                    Session.getInstance().getEventBus().notifyObservers(new BackgroundEmploymentAddCanceledEvent());
                }
                else
                {
                    Session.getInstance().getEventBus().notifyObservers(new BackgroundEmploymentEditCanceledEvent());
                }
            }            
        });
        
        form.setOnCancelHistoryToken(pageHistoryToken);
        
        this.add(form);
    }
    
    /**
     * Creates an array list of all possible Industry values.
     * 
     * @return
     *            The array list of industries.
     */
    private Map<String, String> getIndustryValues()
    {
        Map<String, String> values = new HashMap<String, String>();
        
        values.put("", "Select");
        values.put("Accounting", "Accounting");
        values.put("Airlines/Aviation", "Airlines/Aviation");
        values.put("Alternative Dispute Resolution", "Alternative Dispute Resolution");
        values.put("Alternative Medicine", "Alternative Medicine");
        values.put("Animation", "Animation");
        values.put("Apparel & Fashion", "Apparel & Fashion");
        values.put("Architecture & Planning", "Architecture & Planning");
        values.put("Arts and Crafts", "Arts and Crafts");
        values.put("Automotive", "Automotive");
        values.put("Aviation & Aerospace", "Aviation & Aerospace");
        values.put("Banking", "Banking");
        values.put("Biotechnology", "Biotechnology");
        values.put("Broadcast Media", "Broadcast Media");
        values.put("Building Materials", "Building Materials");
        values.put("Business Supplies and Equipment", "Business Supplies and Equipment");
        values.put("Capital Markets", "Capital Markets");
        values.put("Chemicals", "Chemicals");
        values.put("Civic & Social Organization", "Civic & Social Organization");
        values.put("Civil Engineering", "Civil Engineering");
        values.put("Commercial Real Estate", "Commercial Real Estate");
        values.put("Computer & Network Security", "Computer & Network Security");
        values.put("Computer Games", "Computer Games");
        values.put("Computer Hardware", "Computer Hardware");
        values.put("Computer Networking", "Computer Networking");
        values.put("Computer Software", "Computer Software");
        values.put("Construction", "Construction");
        values.put("Consumer Electronics", "Consumer Electronics");
        values.put("Consumer Goods", "Consumer Goods");
        values.put("Consumer Services", "Consumer Services");
        values.put("Cosmetics", "Cosmetics");
        values.put("Dairy", "Dairy");
        values.put("Defense & Space", "Defense & Space");
        values.put("Design", "Design");
        values.put("Education Management", "Education Management");
        values.put("E-Learning", "E-Learning");
        values.put("Electrical/Electronic Manufacturing", "Electrical/Electronic Manufacturing");
        values.put("Entertainment", "Entertainment");
        values.put("Environmental Services", "Environmental Services");
        values.put("Events Services", "Events Services");
        values.put("Executive Office", "Executive Office");
        values.put("Facilities Services", "Facilities Services");
        values.put("Farming", "Farming");
        values.put("Financial Services", "Financial Services");
        values.put("Fine Art", "Fine Art");
        values.put("Fishery", "Fishery");
        values.put("Food & Beverages", "Food & Beverages");
        values.put("Food Production", "Food Production");
        values.put("Fund-Raising", "Fund-Raising");
        values.put("Furniture", "Furniture");
        values.put("Gambling & Casinos", "Gambling & Casinos");
        values.put("Glass, Ceramics & Concrete", "Glass, Ceramics & Concrete");
        values.put("Government Administration", "Government Administration");
        values.put("Government Relations", "Government Relations");
        values.put("Graphic Design", "Graphic Design");
        values.put("Health, Wellness and Fitness", "Health, Wellness and Fitness");
        values.put("Higher Education", "Higher Education");
        values.put("Hospital & Health Care", "Hospital & Health Care");
        values.put("Hospitality", "Hospitality");
        values.put("Human Resources", "Human Resources");
        values.put("Import and Export", "Import and Export");
        values.put("Individual & Family Services", "Individual & Family Services");
        values.put("Industrial Automation", "Industrial Automation");
        values.put("Information Services", "Information Services");
        values.put("Information Technology and Services", "Information Technology and Services");
        values.put("Insurance", "Insurance");
        values.put("International Affairs", "International Affairs");
        values.put("International Trade and Development", "International Trade and Development");
        values.put("Internet", "Internet");
        values.put("Investment Banking", "Investment Banking");
        values.put("Investment Management", "Investment Management");
        values.put("Judiciary", "Judiciary");
        values.put("Law Enforcement", "Law Enforcement");
        values.put("Law Practice", "Law Practice");
        values.put("Legal Services", "Legal Services");
        values.put("Legislative Office", "Legislative Office");
        values.put("Leisure, Travel & Tourism", "Leisure, Travel & Tourism");
        values.put("Libraries", "Libraries");
        values.put("Logistics and Supply Chain", "Logistics and Supply Chain");
        values.put("Luxury Goods & Jewelry", "Luxury Goods & Jewelry");
        values.put("Machinery", "Machinery");
        values.put("Management Consulting", "Management Consulting");
        values.put("Maritime", "Maritime");
        values.put("Marketing and Advertising", "Marketing and Advertising");
        values.put("Market Research", "Market Research");
        values.put("Mechanical or Industrial Engineering", "Mechanical or Industrial Engineering");
        values.put("Media Production", "Media Production");
        values.put("Medical Devices", "Medical Devices");
        values.put("Medical Practice", "Medical Practice");
        values.put("Mental Health Care", "Mental Health Care");
        values.put("Military", "Military");
        values.put("Mining & Metals", "Mining & Metals");
        values.put("Motion Pictures and Film", "Motion Pictures and Film");
        values.put("Museums and Institutions", "Museums and Institutions");
        values.put("Music", "Music");
        values.put("Nanotechnology", "Nanotechnology");
        values.put("Newspapers", "Newspapers");
        values.put("Non-Profit Organization Management", "Non-Profit Organization Management");
        values.put("Oil & Energy", "Oil & Energy");
        values.put("Online Media", "Online Media");
        values.put("Outsourcing/Offshoring", "Outsourcing/Offshoring");
        values.put("Package/Freight Delivery", "Package/Freight Delivery");
        values.put("Packaging and Containers", "Packaging and Containers");
        values.put("Paper & Forest Products", "Paper & Forest Products");
        values.put("Performing Arts", "Performing Arts");
        values.put("Pharmaceuticals", "Pharmaceuticals");
        values.put("Philanthropy", "Philanthropy");
        values.put("Photography", "Photography");
        values.put("Plastics", "Plastics");
        values.put("Political Organization", "Political Organization");
        values.put("Primary/Secondary Education", "Primary/Secondary Education");
        values.put("Printing", "Printing");
        values.put("Professional Training & Coaching", "Professional Training & Coaching");
        values.put("Program Development", "Program Development");
        values.put("Public Policy", "Public Policy");
        values.put("Public Relations and Communications", "Public Relations and Communications");
        values.put("Public Safety", "Public Safety");
        values.put("Publishing", "Publishing");
        values.put("Railroad Manufacture", "Railroad Manufacture");
        values.put("Ranching", "Ranching");
        values.put("Real Estate", "Real Estate");
        values.put("Recreational Facilities and Services", "Recreational Facilities and Services");
        values.put("Religious Institutions", "Religious Institutions");
        values.put("Renewables & Environment", "Renewables & Environment");
        values.put("Research", "Research");
        values.put("Restaurants", "Restaurants");
        values.put("Retail", "Retail");
        values.put("Security and Investigations", "Security and Investigations");
        values.put("Semiconductors", "Semiconductors");
        values.put("Shipbuilding", "Shipbuilding");
        values.put("Sporting Goods", "Sporting Goods");
        values.put("Sports", "Sports");
        values.put("Staffing and Recruiting", "Staffing and Recruiting");
        values.put("Supermarkets", "Supermarkets");
        values.put("Telecommunications", "Telecommunications");
        values.put("Textiles", "Textiles");
        values.put("Think Tanks", "Think Tanks");
        values.put("Tobacco", "Tobacco");
        values.put("Translation and Localization", "Translation and Localization");
        values.put("Transportation/Trucking/Railroad", "Transportation/Trucking/Railroad");
        values.put("Utilities", "Utilities");
        values.put("Venture Capital & Private Equity", "Venture Capital & Private Equity");
        values.put("Veterinary", "Veterinary");
        values.put("Warehousing", "Warehousing");
        values.put("Wholesale", "Wholesale");
        values.put("Wine and Spirits", "Wine and Spirits");
        values.put("Wireless", "Wireless");
        values.put("Writing and Editing", "Writing and Editing");
        values.put("Other", "Other");
        return values;
    }
}
