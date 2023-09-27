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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

@DatatypeDef(name = "Tree")
public class Tree extends Type implements ICompositeType {

    @Child(
            name = "listOfNodes",
            type = {SingleTreeNode.class})
    private SingleTreeNode listOfNodes;

    @Child(
            name = "parentChildren",
            type = {ParentChildrenMap.class},
            order = 1,
            min = 0,
            max = -1,
            modifier = false,
            summary = false)
    private List<ParentChildrenMap> parentChildren;

    private static final Logger logger = Logger.getLogger(Tree.class.getSimpleName());

    public SingleTreeNode getTree() {
        return listOfNodes;
    }

    public Tree() {
        listOfNodes = new SingleTreeNode();
        parentChildren = new ArrayList<>();
    }

    private void addToParentChildRelation(String parentId, String id) {
        if (parentChildren == null) {
            parentChildren = new CopyOnWriteArrayList<>();
        }

        List<StringType> kids = getChildrenIdsByParentId(parentId);

        if (kids == null) {
            kids = new ArrayList<>();
        }
        StringType idStringType = new StringType();
        String idString = Utils.cleanIdString(id);
        idStringType.setValue(idString);

        StringType parentStringType = new StringType();
        parentStringType.setValue(parentId);
        kids.add(idStringType);
        AtomicReference<Boolean> setParentChildMap = new AtomicReference<>(false);

        List<StringType> finalKids = kids;
        parentChildren.parallelStream().filter(parentChildrenMap -> parentChildrenMap != null
                && parentChildrenMap.getIdentifier() != null
                && StringUtils.isNotBlank(parentChildrenMap.getIdentifier().getValue())
                && parentChildrenMap.getIdentifier().getValue().equals(parentId)).forEach(innerParentChildrenMap -> {

            innerParentChildrenMap.setChildIdentifiers(finalKids);
            setParentChildMap.set(true);

        });

        if (!setParentChildMap.get()) {
            ParentChildrenMap parentChildrenMap = new ParentChildrenMap();
            parentChildrenMap.setIdentifier(parentStringType);
            parentChildrenMap.setChildIdentifiers(kids);
            parentChildren.add(parentChildrenMap);
        }
    }

    private List<StringType> getChildrenIdsByParentId(String parentId) {
        Optional<List<StringType>> kidsOptional = parentChildren.parallelStream().filter(parentChildrenMap -> parentChildrenMap != null
                && parentChildrenMap.getIdentifier() != null
                && StringUtils.isNotBlank(parentChildrenMap.getIdentifier().getValue())
                && parentChildrenMap
                .getIdentifier()
                .getValue()
                .equals(parentId)).map(ParentChildrenMap::getChildIdentifiers).filter(Objects::nonNull).findFirst();
        return kidsOptional.orElse(null);
    }

    public void addNode(String id, String label, Location node, String parentId) {
        if (listOfNodes == null) {
            listOfNodes = new SingleTreeNode();
        }

        // We only add node if it doesn't already exist, else log as an exception
        TreeNode treenode = getNode(id);

        if (treenode == null) {
            TreeNode treeNode = makeNode(id, null, label, node, parentId);

            if (parentId != null) {

                addToParentChildRelation(parentId, id);
                TreeNode parentNode = getNode(parentId);

                // if parent exists add to it otherwise add as root for now
                if (parentNode != null) {
                    parentNode.addChild(treeNode);
                } else {
                    // if no parent exists add it as root node
                    SingleTreeNode singleTreeNode = getSingleTreeNode(id, treeNode);
                    listOfNodes = singleTreeNode;
                }
            } else {
                // if no parent add it as root node
                SingleTreeNode singleTreeNode = getSingleTreeNode(id, treeNode);
                listOfNodes = singleTreeNode;
            }

        } else {
            logger.severe("Node with ID " + id + " already exists in tree");
        }
    }

    private static SingleTreeNode getSingleTreeNode(String id, TreeNode treeNode) {
        String idString = id;
        idString = Utils.cleanIdString(idString);
        SingleTreeNode singleTreeNode = new SingleTreeNode();
        StringType treeNodeId = new StringType();
        treeNodeId.setValue(idString);
        singleTreeNode.setTreeNodeId(treeNodeId);
        singleTreeNode.setTreeNode(treeNode);
        return singleTreeNode;
    }

    private TreeNode makeNode(String currentNodeId, TreeNode treenode, String label, Location node, String parentId) {
        if (treenode == null) {
            treenode = new TreeNode();
            StringType nodeId = new StringType();
            String idString = Utils.cleanIdString(currentNodeId);
            nodeId.setValue(idString);
            treenode.setNodeId(nodeId);
            StringType labelString = new StringType();
            labelString.setValue(label);
            treenode.setLabel(labelString);
            treenode.setNode(node);
            StringType parentIdString = new StringType();
            String parentIdStringVar = Utils.cleanIdString(parentId);
            parentIdString.setValue(parentIdStringVar);
            treenode.setParent(parentIdString);
        }
        return treenode;
    }

    @Nullable
    public TreeNode getNode(String id) {
        // Check if id is any root node
        String idString = Utils.cleanIdString(id);

        if (listOfNodes.getTreeNodeId() != null
                && StringUtils.isNotBlank(listOfNodes.getTreeNodeId().getValue())
                && listOfNodes.getTreeNodeId().getValue().equals(idString)) {
            return listOfNodes.getTreeNode();

        } else {
            if (listOfNodes != null && listOfNodes.getTreeNode() != null) {
                return listOfNodes.getTreeNode().findChild(idString);
            }
        }
        return null;
    }

    public SingleTreeNode getListOfNodes() {
        return listOfNodes;
    }

    public void setListOfNodes(SingleTreeNode listOfNodes) {
        this.listOfNodes = listOfNodes;
    }

    public List<ParentChildrenMap> getParentChildren() {
        return parentChildren;
    }

    public void setParentChildren(List<ParentChildrenMap> parentChildren) {
        this.parentChildren = parentChildren;
    }

    @Override
    public Type copy() {
        Tree tree = new Tree();
        copyValues(tree);
        return tree;
    }

    @Override
    public boolean isEmpty() {
        return ElementUtil.isEmpty(listOfNodes);
    }

    @Override
    protected Type typedCopy() {
        return copy();
    }
}
