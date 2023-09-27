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

import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import ca.uhn.fhir.util.ElementUtil;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.smartregister.utils.Utils;

import java.util.ArrayList;
import java.util.List;

@DatatypeDef(name = "TreeNode")
public class TreeNode extends Type implements ICompositeType {

    @Child(
            name = "name",
            type = {StringType.class},
            order = 0,
            min = 1,
            max = 1,
            modifier = false,
            summary = false)
    protected StringType name;

    @Child(
            name = "nodeId",
            type = {StringType.class},
            order = 2)
    private StringType nodeId;

    @Child(
            name = "label",
            type = {StringType.class},
            order = 3)
    private StringType label;

    @Child(
            name = "node",
            type = {Location.class},
            order = 4)
    private Location node;

    @Child(
            name = "parent",
            type = {StringType.class},
            order = 5)
    private StringType parent;

    @Child(
            name = "children",
            type = {ChildTreeNode.class},
            order = 6,
            min = 0,
            max = -1,
            modifier = false,
            summary = false)
    private List<ChildTreeNode> children;

    public TreeNode() {
        children = new ArrayList<>();
    }

    public TreeNode(
            StringType name,
            StringType nodeId,
            StringType label,
            Location node,
            StringType parent) {
        this.name = name;
        this.nodeId = nodeId;
        this.label = label;
        this.node = node;
        this.parent = parent;
    }

    public StringType getName() {
        if (name == null) {
            name = new StringType();
        }
        return name;
    }

    public TreeNode setName(StringType name) {
        this.name = name;
        return this;
    }

    public StringType getLabel() {
        return label;
    }

    public TreeNode setLabel(StringType label) {
        this.label = label;
        return this;
    }

    @Override
    public Type copy() {
        TreeNode treeNode = new TreeNode();
        copyValues(treeNode);
        return treeNode;
    }

    @Override
    public boolean isEmpty() {
        return ElementUtil.isEmpty(node);
    }

    @Override
    protected Type typedCopy() {
        return copy();
    }

    public StringType getNodeId() {
        return nodeId;
    }

    public TreeNode setNodeId(StringType nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    public Location getNode() {
        return node;
    }

    public TreeNode setNode(Location node) {
        this.node = node;
        return this;
    }

    public StringType getParent() {
        return parent;
    }

    public TreeNode setParent(StringType parent) {
        this.parent = parent;
        return this;
    }

    public List<ChildTreeNode> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    public TreeNode setChildren(List<ChildTreeNode> children) {
        this.children = children;
        return this;
    }

    public void addChild(TreeNode node) {
        if (children == null) {
            children = new ArrayList<>();
        }
        ChildTreeNode childTreeNode = new ChildTreeNode();
        childTreeNode.setChildId(node.getNodeId());
        List<TreeNode> treeNodeList = new ArrayList<>();
        TreeNode treeNode = new TreeNode();
        treeNode.setNode(node.getNode());
        treeNode.setNodeId(node.getNodeId());
        treeNode.setLabel(node.getLabel());
        treeNode.setParent(node.getParent());
        treeNodeList.add(treeNode);
        childTreeNode.setChildren(treeNode);
        children.add(childTreeNode);
    }

    public TreeNode findChild(String childId) {

        String idString = Utils.cleanIdString(childId);
        if (children != null && !children.isEmpty()) {
            for (ChildTreeNode child : children) {
                if (isChildFound(child, idString)) {
                    return child.getChildren();
                } else if (child != null && child.getChildren() != null) {
                    TreeNode node = child.getChildren().findChild(idString);
                    if (node != null) return node;
                }
            }
        }
        return null;
    }

    private static boolean isChildFound(ChildTreeNode child, String idString) {
        return child != null
                && child.getChildren() != null
                && child.getChildren().getNodeId() != null
                && StringUtils.isNotBlank(
                child.getChildren().getNodeId().getValue())
                && child.getChildren().getNodeId().getValue().equals(idString);
    }
}
