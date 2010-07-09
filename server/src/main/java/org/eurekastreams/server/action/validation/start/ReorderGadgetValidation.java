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
package org.eurekastreams.server.action.validation.start;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.start.ReorderGadgetRequest;
import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.Layout;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabTemplate;
import org.eurekastreams.server.persistence.TabMapper;

/**
 * This class handles validation for the Reorder Gadget Execution. This ensures that the inputs are valid and will
 * result in a consistent Tab/Gadget data model.
 *
 */
public class ReorderGadgetValidation implements ValidationStrategy<PrincipalActionContext>
{
    /**
     * Local instance of the {@link TabMapper}.
     */
    private final TabMapper tabMapper;

    /**
     * Constructor.
     *
     * @param inTabMapper
     *            - instance of the {@link TabMapper} to lookup tabs from the inputs.
     */
    public ReorderGadgetValidation(final TabMapper inTabMapper)
    {
        tabMapper = inTabMapper;
    }

    /**
     * {@inheritDoc}.
     *
     * This method ensures that the following conditions are met:
     * - The target zone is in the range of what the layout allows.
     * - The provided tab exists and is valid. - The source template where the gadget can be found exists.
     * - The target zone index is consistent with the gadgets that are already in place.
     */
    @Override
    public void validate(final PrincipalActionContext inActionContext) throws ValidationException
    {
        ValidationException vex = new ValidationException();

        ReorderGadgetRequest request = (ReorderGadgetRequest) inActionContext.getParams();
        Long targetTabId = request.getCurrentTabId();
        Long gadgetId = request.getGadgetId();
        Integer targetZoneNumber = request.getTargetZoneNumber();
        Integer targetZoneIndex = request.getTargetZoneIndex();

        TabTemplate sourceTemplate = tabMapper.findByGadgetId(gadgetId);

        Tab destinationTab = tabMapper.findById(targetTabId);

        // Ensure that the destination tab exists.
        if (destinationTab == null)
        {
            vex.addError("invalidTab", "Destination zone does not exist.");
            throw vex;
        }

        TabTemplate destinationTemplate = destinationTab.getTemplate();
        Layout destinationLayout = destinationTemplate.getTabLayout();

        // Save the Source and Destination TabTemplate to state so they can be reused in execution.
        inActionContext.getState().put("destinationTemplate", destinationTemplate);
        inActionContext.getState().put("sourceTemplate", sourceTemplate);

        // Destination zone is within the valid number of destination zones.
        if (targetZoneNumber + 1 > destinationLayout.getNumberOfZones())
        {
            vex.addError("invalidZone", "ReorderGadgetAction told to move a gadget to a nonexistent zone.");
            throw vex;
        }

        // Ensure that the gadget to be moved exists.
        if (sourceTemplate == null)
        {
            vex.addError("invalidGadget", "Gadget to be moved is invalid.");
            throw vex;
        }

        // Create a map of the zonenumbers and a list of the corresponding zone indexes.
        HashMap<Integer, List<Integer>> gadgetZoneIndexes = new HashMap<Integer, List<Integer>>();
        for (Gadget currentGadget : destinationTemplate.getGadgets())
        {
            if (gadgetZoneIndexes.containsKey(currentGadget.getZoneNumber()))
            {
                gadgetZoneIndexes.get(currentGadget.getZoneNumber()).add(currentGadget.getZoneIndex());
            }
            else
            {
                ArrayList<Integer> currentZoneIndexes = new ArrayList<Integer>();
                currentZoneIndexes.add(currentGadget.getZoneIndex());
                gadgetZoneIndexes.put(currentGadget.getZoneNumber(), currentZoneIndexes);
            }
        }

        //Test the zone boundaries only if the target zone contains gadgets.
        if (gadgetZoneIndexes.containsKey(targetZoneNumber))
        {
            List<Integer> targetZoneIndexes = gadgetZoneIndexes.get(targetZoneNumber);
            Collections.sort(targetZoneIndexes);
            // Test the targetzoneindex to be sure it is within the range of the indexes in the target zone.
            // Test that the target zone index is not greater than the last position in the list of indexes
            // or less than the first position in the list of indexes.
            if (targetZoneIndex > targetZoneIndexes.get(targetZoneIndexes.size() - 1) + 1
                    || targetZoneIndex < targetZoneIndexes.get(0) - 1)
            {
                vex.addError("invalidZoneIndex",
                        "Destination zone index is outside of the acceptable bounds for the target zone index.");
                throw vex;
            }
        }
        //If the target zone number does not have any gadgets in it, then the index should always be 0.
        else if (targetZoneIndex != 0)
        {
            vex.addError("invalidZoneIndex",
                    "Destination zone index should be zero when moving to an empty zone.");
        }
    }
}
