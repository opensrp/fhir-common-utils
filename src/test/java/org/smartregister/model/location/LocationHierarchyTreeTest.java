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
import org.hl7.fhir.r4.model.Reference;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

public class LocationHierarchyTreeTest {

    @Test
    public void testAddLocationWithoutChildLocations() {
        Location location = new Location();
        location.setId("Location/1");
        location.setName("Test Location");
        Reference partOfReference = new Reference();
        partOfReference.setReference("");
        location.setPartOf(partOfReference);
        LocationHierarchyTree locationHierarchyTree = new LocationHierarchyTree();
        locationHierarchyTree.addLocation(location);

        Tree tree = locationHierarchyTree.getLocationsHierarchy();
        assertNotNull(tree);
        assertNotNull(tree.getTree());
        assertEquals("Location/1", tree.getTree().getTreeNodeId().getValue());
        assertEquals("Location/1", tree.getTree().getTreeNode().getNodeId().getValue());
        assertEquals("Test Location", tree.getTree().getTreeNode().getLabel().getValue());
        assertNull(tree.getTree().getTreeNode().getParent().getValue());
        assertEquals(0, tree.getTree().getTreeNode().getChildren().size());
    }

    @Test
    public void testBuildTreeFromList() {
        List<Location> locationList = getLocationList();
        LocationHierarchyTree locationHierarchyTree = new LocationHierarchyTree();
        locationHierarchyTree.buildTreeFromList(locationList);
        Tree tree = locationHierarchyTree.getLocationsHierarchy();
        assertNotNull(tree);
        assertNotNull(tree.getTree());
        assertEquals("Location/1", tree.getTree().getTreeNodeId().getValue());
        assertEquals("Location/1", tree.getTree().getTreeNode().getNodeId().getValue());
        assertEquals("Test Location 1", tree.getTree().getTreeNode().getLabel().getValue());
        assertNull(tree.getTree().getTreeNode().getParent().getValue());
        assertEquals(1, tree.getTree().getTreeNode().getChildren().size());

        assertEquals(
                "Location/2",
                tree.getTree()
                        .getTreeNode()
                        .getChildren()
                        .get(0)
                        .getChildren()
                        .getNodeId()
                        .getValue());
        assertEquals(
                "Test Location 2",
                tree.getTree()
                        .getTreeNode()
                        .getChildren()
                        .get(0)
                        .getChildren()
                        .getLabel()
                        .getValue());
        assertNotNull(
                tree.getTree()
                        .getTreeNode()
                        .getChildren()
                        .get(0)
                        .getChildren()
                        .getParent()
                        .getValue());
        assertEquals(
                "Location/1",
                tree.getTree()
                        .getTreeNode()
                        .getChildren()
                        .get(0)
                        .getChildren()
                        .getParent()
                        .getValue());
        assertEquals(
                1,
                tree.getTree()
                        .getTreeNode()
                        .getChildren()
                        .get(0)
                        .getChildren()
                        .getChildren()
                        .size());

        assertEquals(
                "Location/3",
                tree.getTree()
                        .getTreeNode()
                        .getChildren()
                        .get(0)
                        .getChildren()
                        .getChildren()
                        .get(0)
                        .getChildren()
                        .getNodeId()
                        .getValue());
        assertEquals(
                "Test Location 3",
                tree.getTree()
                        .getTreeNode()
                        .getChildren()
                        .get(0)
                        .getChildren()
                        .getChildren()
                        .get(0)
                        .getChildren()
                        .getLabel()
                        .getValue());
        assertNotNull(
                tree.getTree()
                        .getTreeNode()
                        .getChildren()
                        .get(0)
                        .getChildren()
                        .getChildren()
                        .get(0)
                        .getChildren()
                        .getParent()
                        .getValue());
        assertEquals(
                "Location/2",
                tree.getTree()
                        .getTreeNode()
                        .getChildren()
                        .get(0)
                        .getChildren()
                        .getChildren()
                        .get(0)
                        .getChildren()
                        .getParent()
                        .getValue());
        assertEquals(
                0,
                tree.getTree()
                        .getTreeNode()
                        .getChildren()
                        .get(0)
                        .getChildren()
                        .getChildren()
                        .get(0)
                        .getChildren()
                        .getChildren()
                        .size());

        assertNotNull(locationHierarchyTree.getLocationsHierarchy().getParentChildren());
        assertEquals(2, locationHierarchyTree.getLocationsHierarchy().getParentChildren().size());
        assertEquals(
                "Location/1",
                locationHierarchyTree
                        .getLocationsHierarchy()
                        .getParentChildren()
                        .get(0)
                        .getIdentifier()
                        .getValue());
        assertEquals(
                1,
                locationHierarchyTree
                        .getLocationsHierarchy()
                        .getParentChildren()
                        .get(0)
                        .getChildIdentifiers()
                        .size());
        assertEquals(
                "Location/2",
                locationHierarchyTree
                        .getLocationsHierarchy()
                        .getParentChildren()
                        .get(0)
                        .getChildIdentifiers()
                        .get(0)
                        .getValue());

        assertEquals(
                "Location/2",
                locationHierarchyTree
                        .getLocationsHierarchy()
                        .getParentChildren()
                        .get(1)
                        .getIdentifier()
                        .getValue());
        assertEquals(
                1,
                locationHierarchyTree
                        .getLocationsHierarchy()
                        .getParentChildren()
                        .get(1)
                        .getChildIdentifiers()
                        .size());
        assertEquals(
                "Location/3",
                locationHierarchyTree
                        .getLocationsHierarchy()
                        .getParentChildren()
                        .get(1)
                        .getChildIdentifiers()
                        .get(0)
                        .getValue());
    }

    private static List<Location> getLocationList() {
        Location location1 = new Location();
        location1.setId("Location/1");
        location1.setName("Test Location 1");
        Reference partOfReference = new Reference();
        partOfReference.setReference("");
        location1.setPartOf(partOfReference);

        Location location2 = new Location();
        location2.setId("Location/2");
        location2.setName("Test Location 2");
        partOfReference = new Reference();
        partOfReference.setReference("Location/1");
        location2.setPartOf(partOfReference);

        Location location3 = new Location();
        location3.setId("Location/3");
        location3.setName("Test Location 3");
        partOfReference = new Reference();
        partOfReference.setReference("Location/2");
        location3.setPartOf(partOfReference);

        List<Location> locationList = new ArrayList<>();
        locationList.add(location1);
        locationList.add(location2);
        locationList.add(location3);


        return locationList;
    }
}
