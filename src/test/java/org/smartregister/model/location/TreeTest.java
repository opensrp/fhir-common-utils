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
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

public class TreeTest {

    @Test
    public void testAddingNodeWithOutParent() {
        Tree tree = new Tree();
        Location location = new Location();
        location.setId("testId");
        tree.addNode("Location/1", "test", location, null);

        TreeNode treeNode = tree.getNode("Location/1");

        assertEquals("Location/1", treeNode.getNodeId().getValue());
        assertEquals("test", treeNode.getLabel().getValue());
        assertEquals(location, treeNode.getNode());
        assertNull(treeNode.getParent().getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotReAddExistingNode() {
        Tree tree = new Tree();
        Location location = new Location();
        location.setId("testId");
        tree.addNode("Location/1", "test", location, null);
        tree.addNode("Location/1", "test", location, null);
    }

    @Test
    public void testAddingNodeWithValidParent() {
        Tree tree = new Tree();
        Location location = new Location();
        location.setId("testId");
        tree.addNode("Location/1", "test", location, null);
        tree.addNode("Location/2", "test2", location, "Location/1");

        TreeNode childNode = tree.getNode("Location/2");

        assertEquals("Location/2", childNode.getNodeId().getValue());
        assertEquals("test2", childNode.getLabel().getValue());
        assertEquals(location, childNode.getNode());
        assertNotNull(childNode.getParent().getValue());

        String parentNodeId = childNode.getParent().getValue();

        assertEquals("Location/1", parentNodeId);
    }
}
