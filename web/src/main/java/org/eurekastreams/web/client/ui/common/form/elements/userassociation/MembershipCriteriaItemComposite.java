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
package org.eurekastreams.web.client.ui.common.form.elements.userassociation;

import org.eurekastreams.server.domain.dto.MembershipCriteriaDTO;
import org.eurekastreams.web.client.ui.common.EditPanel;
import org.eurekastreams.web.client.ui.common.EditPanel.Mode;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Membership criteria item.
 */
public class MembershipCriteriaItemComposite extends FlowPanel
{
    /**
     * The membership criteria.
     */
    private String criteria;

    /**
     * The edit controls.
     */
    private EditPanel editControls;

    /**
     * Constructor.
     * 
     * @param inCriteria
     *            the membership criteria.
     */
    public MembershipCriteriaItemComposite(final MembershipCriteriaDTO inCriteria)
    {
        criteria = inCriteria.getCriteria();

        editControls = new EditPanel(this, Mode.DELETE);

        this.add(editControls);
        this.add(new Label(criteria));
        this.add(new Label(inCriteria.getThemeName() == null ? "None" : inCriteria.getThemeName()));
        this.add(new Label(inCriteria.getGalleryTabTemplateName() == null ? "None" : inCriteria
                .getGalleryTabTemplateName()));
    }

    /**
     * Add a delete button click handler.
     * 
     * @param handler
     *            the handler.
     */
    public void addDeleteClickHandler(final ClickHandler handler)
    {
        editControls.addDeleteClickHandler(handler);
    }

    /**
     * @return the criteria
     */
    public String getCriteria()
    {
        return criteria;
    }
}
