/*
 * Copyright 2021 Ona Systems, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartregister.model.location;

import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.StringType;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class TreeNodeTest {

    @Test
    public void testAddingChild() {

        StringType rootNodeName = new StringType();
        rootNodeName.setValue("Root Node");

        StringType rootNodeId = new StringType();
        rootNodeId.setValue("Location/1");

        StringType rootNodeLabel = new StringType();
        rootNodeLabel.setValue("Root Location Node");

        Location location = new Location();
        location.setId("Location/1");

        StringType childNodeName = new StringType();
        childNodeName.setValue("Child Node");

        StringType childNodeId = new StringType();
        childNodeId.setValue("Location/2");

        StringType childNodeLabel = new StringType();
        childNodeLabel.setValue("Child Location Node");

        TreeNode rootNode = new TreeNode(rootNodeName, rootNodeId, rootNodeLabel, location, null);
        TreeNode childNode =
                new TreeNode(childNodeName, childNodeId, childNodeLabel, location, rootNodeId);
        rootNode.addChild(childNode);

        assertEquals(
                childNodeId.getValue(),
                rootNode.findChild(childNodeId.getValue()).getNodeId().getValue());
        assertEquals(
                rootNodeId.getValue(),
                rootNode.findChild(childNodeId.getValue()).getParent().getValue());
    }

    @Test
    public void findInvalidChildren() {
        StringType rootNodeName = new StringType();
        rootNodeName.setValue("Root Node");

        StringType rootNodeId = new StringType();
        rootNodeId.setValue("Location/1");

        StringType rootNodeLabel = new StringType();
        rootNodeLabel.setValue("Root Location Node");

        Location location = new Location();
        location.setId("Location/1");

        TreeNode rootNode = new TreeNode(rootNodeName, rootNodeId, rootNodeLabel, location, null);
        assertEquals(0, rootNode.getChildren().size());
        assertNull(rootNode.findChild("Location/2"));
    }
}
